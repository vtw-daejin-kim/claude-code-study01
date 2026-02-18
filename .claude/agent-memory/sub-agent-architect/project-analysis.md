# Project Analysis Summary

## Current State
- Only EcommerceApplication.java exists (empty Spring Boot app)
- build.gradle fully configured with all dependencies
- application.yml / -local / -dev / -prod profiles configured
- docker-compose.yml for PostgreSQL 15 ready
- No domain code, no migrations, no tests beyond contextLoads()

## Implementation Phases (all unchecked)
- Phase 0: Scaffolding (DDL, global infra, security, config)
- Phase 1: User domain (auth, profile, password)
- Phase 2: Brand domain (CRUD, soft delete, cascade)
- Phase 3: Product + Stock domain (CRUD, stock concurrency, revisions, MyBatis)
- Phase 4: Like domain (idempotent toggle, listing)
- Phase 5: Cart domain (CRUD, availability, MyBatis JOIN)
- Phase 6: Order domain (create, cancel, expire batch, cart restoration)
- Phase 7: Stats domain (admin dashboard, MyBatis aggregation)
- Phase 8: Hardening (E2E tests, quality, docs)

## Key Patterns to Enforce
1. JPA for writes, MyBatis for complex reads
2. ErrorCode enum + BusinessException hierarchy
3. ApiResponse<T> wrapping all responses
4. PageResponse<T> for paginated results
5. Flyway V{N}__*.sql for schema
6. SecurityConfig with path-based auth (/api/v1 vs /api-admin/v1)
7. Conditional UPDATE for stock (no SELECT FOR UPDATE)
8. CAS for order state transitions
9. Soft delete with status=DELETED + deleted_at
10. Snapshot columns in order_items (not JSON)

## Domain Dependencies
- Brand <- Product (FK, cascade delete)
- Product <- Like, CartItem, OrderItem
- Product <-> ProductStock (1:1)
- Product <- ProductRevision
- Order <- OrderItem, OrderCartRestore
- User <- Like, CartItem, Order

## Tech Stack
- Java 17, Gradle 8.12, Spring Boot 3.4.1
- JPA (Hibernate) + MyBatis 3.x
- PostgreSQL 15+ (JSONB for revisions, partial indexes)
- Flyway for migrations
- Spring Security + JWT (jjwt 0.12.6)
- Testcontainers (PostgreSQL) for integration tests
- Lombok
