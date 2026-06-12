# Architecture Decision Records (ADR.md)
**Project:** Supermarkets API 

**Database Engine:** PostgreSQL

This document logs the design and architectural decisions made during the development of the project, including their context and technical justifications.

---

## ADR 001 - [06-10-2026]:

### 1. Strict Separation with DTO Pattern (Request / Response)
* **Decision:** Implement separated DTOs for incoming requests (`RequestDTO`) and outgoing responses (`ResponseDTO`), completely isolating the database entities (`@Entity`) from the presentation layer.
* **Justification:** Prevents security vulnerabilities such as Mass Assignment or price manipulation from the frontend. The client only sends the "intent" (e.g., which products and quantities), while the backend calculates prices and subtotals by querying the database directly.

### 2. Builder Pattern Restricted to DTOs
* **Decision:** Use Lombok's `@Builder` annotation exclusively in the DTO layer.
* **Justification:** Prevents unsafe instantiations in the Entities layer that could compromise the generation of primary keys (`@Id`) or corrupt the lifecycle and managed collections handled by the Hibernate/JPA persistence context.

### 3. Soft Delete Implementation
* **Decision:** Apply logical deletion (`is_deleted`) using `@SQLDelete` and `@SQLRestriction` annotations on entities like `Product` and `Branch`.
* **Exceptions:**
  * `Sale` does not use a boolean flag for deletion. Its lifecycle is managed through a semantic domain using the `Status` Enum (e.g., `CANCELED`).
  * `SaleDetails` does not have its own deletion state; its existence is strictly tied to the parent `Sale` entity.
* **Justification:** Maintains referential integrity for historical and financial data within the supermarket system.

### 4. Sale Details Lifecycle (Cascade)
* **Decision:** Configure the relationship between `Sale` and `SaleDetails` using `cascade = CascadeType.ALL` and `orphanRemoval = true`.
* **Justification:** `SaleDetails` acts as a weak entity. Its persistence and deletion depend entirely on the parent `Sale` object, centralizing transactional management within a single repository (`SaleRepository`).

### 5. Delete Query Optimization (JPA)
* **Decision:** Override the use of `deleteById(id)` with `delete(entity)` in the Service layer when executing deletions.
* **Justification:** Since the Service layer already performs a `findById()` query to validate existence and throw custom exceptions (`NotFoundException`), passing the managed entity directly to the `delete()` method prevents Hibernate from executing a redundant `SELECT` query, optimizing performance.

### 6. Credential Security
* **Decision:** Load PostgreSQL database credentials via environment variables at runtime (injected through the IDE configuration).
* **Justification:** Prevents leakage of sensitive data and credentials in public version control.

### 7. Technical Debt Log (Day 1)
* **Money Management:** `Double` is currently being used for prices and totals. In future refactoring iterations, it is highly recommended to migrate to `BigDecimal` to prevent floating-point precision errors in critical financial operations.
* **Database Indexing:** Pending creation of Partial Indexes (B-Tree) on the `is_deleted = false` field to maintain high read-query performance as the table grows.

---

## ADR 002 [06-11-2026]

The system requires a robust solution to process transactional sales invoices across various supermarket branches. Each sale consists of fundamental header information (date, total, status, branch association) linked to multiple itemized lines (`SaleDetails`), which tightly bind specific quantities to their respective product catalog entities.

Handling this complex domain entity introduces several architectural challenges:
1.  **Lifecycle Splitting:** Allowing child elements (`SaleDetails`) to be managed independently creates high risks of relational desynchronization, detached Hibernate states, and corrupted data graphs.
2.  **Inventory Integrity & Price Fraud:** Malicious clients can attempt to alter prices via payload manipulation during a checkout request. Furthermore, concurrent operations could lead to race conditions or negative inventory stock without strict isolation.
3.  **Auditing and Transactional Immutability:** In corporate retail software, a finalized invoice cannot be updated (`HTTP PUT`) due to standard financial auditing and anti-fraud policies. Erroneous sales must be annulled logically, reversing their real-world impact.
4.  **Memory Footprint & Leakage:** Unbounded database list extractions (`List<E>`) on rapidly expanding operational tables (such as sales history and product catalogs) will inevitably choke system memory and downscale performance on limited-resource hosting environments.
5.  **API Error Leakage:** Unhandled exceptions expose low-level JVM stack traces, internal database schemas, or return generic, uninformative HTML pages, heavily undermining both application security and front-end integration velocity.

### Decisions

We have specified and approved the following multi-layered structural implementations:

### 1. Enforce the Domain-Driven Design (DDD) Aggregate Root Pattern
We abolished any distinct Service or Repository layers for `SaleDetails`. The `Sale` entity is explicitly declared as the **Aggregate Root**.
* The relationship is mapped with `CascadeType.ALL` and `orphanRemoval = true`.
* Any database state transition (Insertion, Selection, Deletion) affecting line items **MUST** channel exclusively through the parent `SaleRepository`.
* This establishes a highly controlled transactional boundary and protects relational integrity.

### 2. Implement a Transaction-Isolated, Anti-Fraud Checkout Pipeline
The sales creation logic inside `SaleService.save()` is bound by Spring's `@Transactional` annotation and executes under these conditions:
* **Zero Payloader Pricing Trust:** The incoming `SaleRequestDTO` only defines product IDs and required quantities. The system explicitly drops payload-submitted prices and forces an isolated query to the database per item to read the immutable `Product.getPrice()`.
* **Fail-Fast Inventory Interception:** Prior to linking, stock availability is evaluated (`Product.getStock() < requestedQuantity`). If inventory is insufficient, execution breaks immediately via a specialized `NotEnoughStockException`, causing a clean database rollback.

### 3. Implement Strict Immutability Controls and Logical Cancellation
* The `PUT /api/sales/{id}` route is completely prohibited and omitted from the endpoint controller layer to preserve audit trails.
* The `DELETE /api/sales/{id}` route is bound to a custom **Logical Annullation Workflow**. Instead of executing a physical SQL delete, the transaction changes the parent state to `Status.CANCELLED`.
* Through an automated loop processing the attached `SaleDetails` graph, the system utilizes **Hibernate Dirty Checking** to re-increment the baseline stock parameters without calling redundant repository save queries, finalizing mutations safely upon transaction commitment.

### 4. Enforce Paged Slicing on Massive Operational Data Sets
Every multi-row tracking route (`findAll` on Products, `findAll` on Sales, and `findByBranchId` on Sales) rejects standard collection types and mandates an incoming `Pageable` argument.
* Results are returned wrapped in a structured Spring Data `Page<T>` container.
* Default benchmarks are established at 50 records per page, keeping data transmissions constant and optimized for hardware configurations with low resource capabilities.

### 5. Centralize Error Structuring and Extinguish Whitelabel Leakage
* Created a unified `ErrorResponseDTO` mapping `timestamp`, `status`, `error` (HTTP string representation), `message`, `path`, and an optional `validationErrors` list.
* Annotated a global class with `@RestControllerAdvice` containing targeted `@ExceptionHandler` blocks for `NotFoundException` (Maps to `409 Conflict`), `NotEnoughStockException` (Maps to `404 Not Found`), and `MethodArgumentNotValidException` (Maps to `400 Bad Request`, converting validation constraints into a readable error array).
* Disabled native web HTML error pages via `spring.web.error.whitelabel.enabled=false` and optimized resource indexing with `spring.web.resources.add-mappings=false`.

### Consequences

* **Positive:** Guaranteed transactional atomicity. Financial operations are completely isolated and fully protected against payload manipulation.
* **Positive:** Drastic reductions in API memory footprint via server-side data slicing, ensuring fast rendering capabilities.
* **Positive:** Clean, predictable, and highly informative REST API error responses, removing security exposures.
* **Positive:** Microscopic code footprint by fully delegating state updates to Hibernate's context monitoring lifecycle (Dirty Checking).
* **Negative:** Slightly higher computational density within the `SaleService` orchestration loop due to the requirement of handling multiple database cross-references per request line item.