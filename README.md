# Supermarket Chain Management API 
### Production-Ready RESTful Backend Engine for Retail Logistics

An architectural showcase of a high-performance, enterprise-grade RESTful API built with **Java** and **Spring Boot**. This project was developed for a real-world supermarket management ecosystem, handling multi-branch coordination, highly dynamic product catalogs, automated stock control, and transaction-safe sales processing.

Designed strictly as a professional portfolio asset, this system demonstrates the implementation of corporate software engineering standards, advanced persistence patterns, clean architecture, and rigorous validation metrics required in top-tier engineering teams.

---

## Architectural Core & Design Patterns

The system is built upon **Clean Architecture** and **Layered Architecture** paradigms, enforcing separation of concerns and the **SOLID** design principles:

* **Domain-Driven Design (DDD) Strategy:** `Sale` is established as an **Aggregate Root**. Sub-entities like `SaleDetails` have their lifecycles strictly managed by the parent root through JPA cascades, eliminating fragmented state synchronization.
* **Decoupled Data Contracts (DTO Pattern):** Absolute isolation between the persistence layer (Database Entities) and the presentation layer (Network Payloads). Specialized `RequestDTO` and `ResponseDTO` structures prevent data leakage and decouple API evolution from schema alterations.
* **Pure Single-Responsibility Mappers:** Static mapping components designed with zero business logic. They serve exclusively as data transporters, ensuring computational mutations happen strictly inside the Service layer.
* **Inversion of Control (IoC):** Controller components depend entirely on service abstractions (`interfaces`) rather than concrete implementations, maximizing testability, decoupling, and flexibility for future architectural adjustments.

---

## Key Technical Features

### 1. Transaction-Safe Sales Engine (`@Transactional`)
Processing a supermarket invoice requires multi-point data synchronization. The sales processing pipeline guarantees **Atomicity** and **Consistency**:
* **Payload Neutralization:** The API completely ignores pricing data sent by the client. It dynamically resolves product prices directly from the database, preventing price-tampering fraud.
* **Atomic Rollbacks:** If a transaction includes 50 items and item 49 fails validation or suffers from stock depletion, the entire operation undergoes an instantaneous rollback, ensuring zero corrupted or "half-saved" data states.
* **Bi-directional Graph Management:** Orchestrates complex relationship linking in-memory prior to execution, ensuring foreign keys are correctly bound without overhead database hits.

### 2. Advanced Persistence Mechanics & Optimization
* **Hibernate Dirty Checking:** Stateful entities retrieved within a transactional context are tracked via Hibernate's session snapshot. State mutations (such as updating stock on sale creation or reversing inventory on logical cancellation) rely on automatic dirty checking, completely avoiding redundant and expensive `.save()` repository operations.
* **Server-Side Pagination & Chunking (`Pageable`):** The product catalog and historical transaction routes completely discard unbounded collections (`List<E>`). Instead, they enforce strict, index-optimized server-side pagination. This protects system memory, allowing the backend to scale effortlessly to millions of rows while maintaining a microscopic memory footprint.
* **Advanced Analytical Queries (JPQL):** Deep database-level aggregation utilizing structural joining and grouping (`COUNT`, `SUM`, `GROUP BY`) to extract high-value performance metrics (e.g., retrieving the historical top-selling product) with minimal execution overhead.

### 3. Global Exception Mapping
The backend replaces Spring's default Whitelabel HTML views and raw stack traces with a standardized JSON Error Matrix through a unified `@RestControllerAdvice`:
* **Predictable Error Contracts:** Clients consistently receive an explicit `ErrorResponseDTO` mapping timestamp, exact HTTP statuses, localized messages, and request URIs.
* **Data Integrity Shield:** Critical exception payloads (such as `NotFoundException` or `NotEnoughStockException`) are translated directly into precise HTTP responses (`404 Not Found`, `409 Conflict`).
* **Structured Validation Array:** Automatically captures failures triggered by Jakarta Validation (`@Valid`). If an incoming payload contains multiple structural errors (e.g., empty strings, negative numbers), the API intercepts them globally and maps them out as a clean array within a `400 Bad Request` state, empowering front-end apps to bind form errors dynamically.

### 4. Interactive OpenAPI 3 / Swagger Documentation
The system integrates an interactive documentation layout using **Springdoc OpenAPI**, customized specifically to present clear integration capabilities to tech recruiters, software architects, and front-end engineers.
* Accessible at: `http://localhost:8080/docs` (Configured route).
* Exposes all functional contracts, payload requirements, validation rules, and structural error responses.

---

## Technology Stack

* **Language:** Java 21+ (LTS)
* **Framework:** Spring Boot (Core, Web, Data JPA)
* **Persistence Provider:** Hibernate ORM
* **Database:** PostgreSQL (Production Profile) / H2 Database (Development Profile)
* **Documentation:** Springdoc OpenAPI UI (v3)
* **Boilerplate Reduction:** Project Lombok
* **Build Automation:** Apache Maven

---

## API Endpoint Architecture

### Branch Management (`/api/branches`)
* `POST /api/branches` - Registers a new corporate branch (`201 Created`).
* `GET /api/branches` - Lists all registered branches (`200 OK`).
* `GET /api/branches/{id}` - Retrieves detailed information of a specific branch (`200 OK`).
* `PUT /api/branches/{id}` - Modifies branch parameters (`200 OK`).
* `DELETE /api/branches/{id}` - Physical soft-deletion of a branch entity (`204 No Content`).

### Product Inventory Management (`/api/products`)
* `POST /api/products` - Registers a new inventory product (`201 Created`).
* `GET /api/products` - Page-chunked extraction of the entire product catalog (`200 OK`). Supports sorting parameters (Default: Name DESC, 50 items/page).
* `GET /api/products/{id}` - Extracts single product details (`200 OK`).
* `GET /api/products/top-seller` - High-performance analytical endpoint resolving the single highest-grossing product historically (`200 OK`).
* `PUT /api/products/{id}` - Modifies product attributes, pricing, or baseline stock (`200 OK`).
* `DELETE /api/products/{id}` - Drops a product from the database catalog (`204 No Content`).

### Transactional Sales Operations (`/api/sales`)
* `POST /api/sales` - Initiates, validates, processes, and commits an atomic supermarket sale (`201 Created`). Automatically deducts inventory stock.
* `GET /api/sales` - Paged retrieval of corporate sales history (`200 OK`). Sorted by date (DESC).
* `GET /api/sales/{id}` - Retrieves structural receipt metadata and nested line-item details of a single sale (`200 OK`).
* `GET /api/sales/branch/{id}` - Target extraction of all sales processed by a specific branch (`200 OK`). Enforces full pagination.
* `DELETE /api/sales/{id}` - **Logical Sale Cancellation / Annullation** (`204 No Content`). Financial records are locked; the sale status transitions to `CANCELLED`, and Hibernate automatically restores the corresponding line-item quantities back to the product stock.
* *Note: `PUT /api/sales/{id}` is strictly omitted by architectural design to ensure immutable auditing parameters and prevent transaction fraud.*

---

## Upcoming Quality Assurance Roadmap
1.  **Testing Suite Matrix:** Implementation of automated unit and integration tests using **JUnit 5** and **Mockito** to lock service invariants and transaction handling.
2.  **Continuous Inspection Quality Gates:** Setting up **JaCoCo** code coverage reports and static code analysis tracking via **SonarLint / SonarQube**.
3.  **Containerized Deployment:** Multi-stage **Docker** build configurations to sandbox the Spring Boot application and link it to a containerized PostgreSQL instance via Docker Compose.