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