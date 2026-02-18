# 이커머스 MVP 상세 구현 계획

> `01-requirements.md`의 요구사항과 `analysis.md`의 분석 결과를 기반으로 작성한 구현 계획입니다.

---

## 1. 기술 스택

| 영역 | 기술 | 버전 | 비고 |
|---|---|---|---|
| Language | Java | 17+ | LTS |
| Framework | Spring Boot | 3.x | Spring 6 기반 |
| ORM | JPA (Hibernate) | - | 엔티티 매핑, CRUD |
| SQL Mapper | MyBatis | 3.x | 복잡 조회 쿼리, 통계 |
| Database | PostgreSQL | 15+ | JSONB, partial index 활용 |
| Migration | Flyway | - | DDL 버전 관리 |
| Security | Spring Security + JWT | jjwt 0.12.x | Stateless 인증 |
| Build | Gradle | - | Groovy DSL |
| Test | JUnit 5 + Testcontainers | - | 통합 테스트용 PostgreSQL |

### JPA + MyBatis 하이브리드 전략

- **JPA**: 엔티티 매핑, 단순 CRUD, 연관관계 관리
- **MyBatis**: 복잡 조회 쿼리 (상품 목록 검색/정렬/필터, 장바구니 JOIN 조회, 관리자 주문 목록, 통계 집계, 만료 배치)
- **원칙**: 쓰기는 JPA, 복잡한 읽기는 MyBatis

---

## 2. 프로젝트 구조

```
src/main/java/kr/go/ecommerce/
├── EcommerceApplication.java
│
├── global/                          # 글로벌 인프라
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── JwtConfig.java
│   │   ├── MyBatisConfig.java
│   │   ├── JpaConfig.java
│   │   ├── WebMvcConfig.java
│   │   └── SchedulerConfig.java
│   ├── security/
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── CustomUserDetails.java
│   │   └── CustomUserDetailsService.java
│   ├── exception/
│   │   ├── ErrorCode.java
│   │   ├── BusinessException.java
│   │   ├── EntityNotFoundException.java
│   │   ├── DuplicateException.java
│   │   ├── StockInsufficientException.java
│   │   ├── InvalidStateException.java
│   │   └── GlobalExceptionHandler.java
│   ├── dto/
│   │   ├── ApiResponse.java
│   │   ├── ErrorResponse.java
│   │   ├── PageResponse.java
│   │   └── FieldError.java
│   └── util/
│       ├── SecurityUtil.java
│       └── JsonSnapshotUtil.java
│
├── domain/
│   ├── user/                        # User 도메인
│   │   ├── entity/
│   │   │   ├── User.java
│   │   │   └── UserRole.java
│   │   ├── repository/
│   │   │   └── UserRepository.java
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   └── UserService.java
│   │   ├── dto/
│   │   │   ├── SignupRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── UserProfileResponse.java
│   │   │   └── PasswordChangeRequest.java
│   │   └── controller/
│   │       ├── AuthController.java
│   │       └── UserController.java
│   │
│   ├── brand/                       # Brand 도메인
│   │   ├── entity/
│   │   │   ├── Brand.java
│   │   │   └── BrandStatus.java
│   │   ├── repository/
│   │   │   └── BrandRepository.java
│   │   ├── service/
│   │   │   ├── BrandService.java
│   │   │   └── BrandAdminService.java
│   │   ├── dto/
│   │   │   ├── BrandListResponse.java
│   │   │   ├── BrandDetailResponse.java
│   │   │   ├── BrandCreateRequest.java
│   │   │   └── BrandUpdateRequest.java
│   │   └── controller/
│   │       ├── BrandController.java
│   │       └── BrandAdminController.java
│   │
│   ├── product/                     # Product 도메인
│   │   ├── entity/
│   │   │   ├── Product.java
│   │   │   ├── ProductStatus.java
│   │   │   ├── ProductStock.java
│   │   │   └── ProductRevision.java
│   │   ├── repository/
│   │   │   ├── ProductRepository.java
│   │   │   ├── ProductStockRepository.java
│   │   │   └── ProductRevisionRepository.java
│   │   ├── mapper/
│   │   │   └── ProductQueryMapper.java
│   │   ├── service/
│   │   │   ├── ProductService.java
│   │   │   ├── ProductAdminService.java
│   │   │   └── ProductStockService.java
│   │   ├── dto/
│   │   │   ├── ProductListResponse.java
│   │   │   ├── ProductDetailResponse.java
│   │   │   ├── ProductCreateRequest.java
│   │   │   ├── ProductUpdateRequest.java
│   │   │   ├── ProductAdminListResponse.java
│   │   │   ├── ProductRevisionListResponse.java
│   │   │   └── ProductRevisionDetailResponse.java
│   │   └── controller/
│   │       ├── ProductController.java
│   │       └── ProductAdminController.java
│   │
│   ├── like/                        # Like 도메인
│   │   ├── entity/
│   │   │   └── Like.java
│   │   ├── repository/
│   │   │   └── LikeRepository.java
│   │   ├── service/
│   │   │   └── LikeService.java
│   │   ├── dto/
│   │   │   └── LikedProductResponse.java
│   │   └── controller/
│   │       └── LikeController.java
│   │
│   ├── cart/                        # Cart 도메인
│   │   ├── entity/
│   │   │   └── CartItem.java
│   │   ├── repository/
│   │   │   └── CartItemRepository.java
│   │   ├── mapper/
│   │   │   └── CartItemQueryMapper.java
│   │   ├── service/
│   │   │   └── CartService.java
│   │   ├── dto/
│   │   │   ├── CartAddRequest.java
│   │   │   ├── CartUpdateQuantityRequest.java
│   │   │   └── CartItemResponse.java
│   │   └── controller/
│   │       ├── CartController.java
│   │       └── CartAdminController.java
│   │
│   ├── order/                       # Order 도메인
│   │   ├── entity/
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   ├── OrderStatus.java
│   │   │   └── OrderCartRestore.java
│   │   ├── repository/
│   │   │   ├── OrderRepository.java
│   │   │   ├── OrderItemRepository.java
│   │   │   └── OrderCartRestoreRepository.java
│   │   ├── mapper/
│   │   │   └── OrderQueryMapper.java
│   │   ├── service/
│   │   │   ├── OrderService.java
│   │   │   ├── OrderExpireScheduler.java
│   │   │   └── CartRestorationService.java
│   │   ├── dto/
│   │   │   ├── OrderCreateRequest.java
│   │   │   ├── OrderItemRequest.java
│   │   │   ├── OrderResponse.java
│   │   │   ├── OrderDetailResponse.java
│   │   │   ├── OrderItemResponse.java
│   │   │   └── OrderListResponse.java
│   │   └── controller/
│   │       ├── OrderController.java
│   │       └── OrderAdminController.java
│   │
│   └── stats/                       # Stats 도메인 (Admin)
│       ├── mapper/
│       │   └── StatsQueryMapper.java
│       ├── service/
│       │   └── StatsService.java
│       ├── dto/
│       │   ├── StatsOverviewResponse.java
│       │   ├── DailyOrderStatsResponse.java
│       │   ├── TopProductResponse.java
│       │   └── LowStockResponse.java
│       └── controller/
│           └── StatsAdminController.java
│
src/main/resources/
├── application.yml
├── application-local.yml
├── application-dev.yml
├── application-prod.yml
├── db/migration/
│   ├── V1__init_schema.sql
│   └── V2__seed_admin_user.sql
└── mapper/
    ├── ProductQueryMapper.xml
    ├── CartItemQueryMapper.xml
    ├── OrderQueryMapper.xml
    └── StatsQueryMapper.xml
```

---

## 3. API 경로 설계

### 3.1 고객용 API (`/api/v1`)

| Method | Path | Actor | 설명 | FR |
|---|---|---|---|---|
| POST | /api/v1/auth/signup | Guest | 회원가입 | FR-U-001 |
| POST | /api/v1/auth/login | Guest | 로그인 | - |
| GET | /api/v1/users/me | User | 내 정보 조회 | FR-U-002 |
| PUT | /api/v1/users/me/password | User | 비밀번호 변경 | FR-U-003 |
| GET | /api/v1/brands | Guest/User | 브랜드 목록 | FR-C-000 |
| GET | /api/v1/brands/{brandId} | Guest/User | 브랜드 상세 | FR-C-001 |
| GET | /api/v1/products | Guest/User | 상품 목록 | FR-C-002 |
| GET | /api/v1/products/{productId} | Guest/User | 상품 상세 | FR-C-003 |
| POST | /api/v1/products/{productId}/likes | User | 좋아요 등록 | FR-L-001 |
| DELETE | /api/v1/products/{productId}/likes | User | 좋아요 취소 | FR-L-002 |
| GET | /api/v1/users/me/likes | User | 내 좋아요 목록 | FR-L-003 |
| GET | /api/v1/cart | User | 장바구니 조회 | FR-CART-005 |
| POST | /api/v1/cart/items | User | 장바구니 추가 | FR-CART-001 |
| PATCH | /api/v1/cart/items/{productId} | User | 수량 변경 | FR-CART-003 |
| DELETE | /api/v1/cart/items/{productId} | User | 장바구니 삭제 | FR-CART-002 |
| POST | /api/v1/orders | User | 주문 생성 | FR-O-001 |
| GET | /api/v1/orders | User | 주문 목록 | FR-O-002 |
| GET | /api/v1/orders/{orderId} | User | 주문 상세 | FR-O-003 |
| POST | /api/v1/orders/{orderId}/cancel | User | 주문 취소 | FR-O-004 |

### 3.2 관리자용 API (`/api-admin/v1`)

| Method | Path | Actor | 설명 | FR |
|---|---|---|---|---|
| GET | /api-admin/v1/brands | Admin | 브랜드 목록 | FR-A-001 |
| GET | /api-admin/v1/brands/{brandId} | Admin | 브랜드 상세 | FR-A-001 |
| POST | /api-admin/v1/brands | Admin | 브랜드 등록 | FR-A-001 |
| PUT | /api-admin/v1/brands/{brandId} | Admin | 브랜드 수정 | FR-A-001 |
| DELETE | /api-admin/v1/brands/{brandId} | Admin | 브랜드 삭제 | FR-A-001 |
| GET | /api-admin/v1/products | Admin | 상품 목록 | FR-A-002-1 |
| GET | /api-admin/v1/products/{productId} | Admin | 상품 상세 | FR-A-002-2 |
| POST | /api-admin/v1/products | Admin | 상품 등록 | FR-A-002 |
| PUT | /api-admin/v1/products/{productId} | Admin | 상품 수정 | FR-A-002-5 |
| DELETE | /api-admin/v1/products/{productId} | Admin | 상품 삭제 | FR-A-002-6 |
| GET | /api-admin/v1/products/{id}/revisions | Admin | 수정 이력 목록 | FR-A-002-3 |
| GET | /api-admin/v1/products/{id}/revisions/{rid} | Admin | 수정 이력 상세 | FR-A-002-4 |
| GET | /api-admin/v1/orders | Admin | 주문 목록 | FR-A-003 |
| GET | /api-admin/v1/orders/{orderId} | Admin | 주문 상세 | FR-A-003 |
| GET | /api-admin/v1/users/{userId}/cart | Admin | 회원 장바구니 | FR-A-004 |
| GET | /api-admin/v1/stats/overview | Admin | 통계 개요 | FR-A-005 |
| GET | /api-admin/v1/stats/orders/daily | Admin | 일별 주문 통계 | FR-A-005 |
| GET | /api-admin/v1/stats/products/top-liked | Admin | 좋아요 TOP | FR-A-005 |
| GET | /api-admin/v1/stats/products/top-ordered | Admin | 주문 TOP | FR-A-005 |
| GET | /api-admin/v1/stats/stocks/low | Admin | 저재고 목록 | FR-A-005 |

---

## 4. 인증/인가 설계

### JWT 구조

```
Authorization: Bearer <JWT>
```

- **토큰 페이로드**: `{ sub: userId, role: "USER"|"ADMIN", iat, exp }`
- **만료**: 24시간 (MVP, 추후 조정 가능)
- **리프레시 토큰**: Phase2에서 도입

### SecurityConfig 경로별 권한

```java
// 인증 불필요 (permitAll)
POST /api/v1/auth/signup
POST /api/v1/auth/login
GET  /api/v1/brands/**
GET  /api/v1/products/**

// USER 이상
GET    /api/v1/users/me
PUT    /api/v1/users/me/password
POST   /api/v1/products/{id}/likes
DELETE /api/v1/products/{id}/likes
GET    /api/v1/users/me/likes
GET    /api/v1/cart
POST   /api/v1/cart/items
PATCH  /api/v1/cart/items/{id}
DELETE /api/v1/cart/items/{id}
POST   /api/v1/orders
GET    /api/v1/orders
GET    /api/v1/orders/{id}
POST   /api/v1/orders/{id}/cancel

// ADMIN만
/api-admin/v1/**
```

---

## 5. 재고 동시성 제어 전략

### 조건부 UPDATE (핵심)

```sql
-- 예약 (hold)
UPDATE product_stock
   SET reserved = reserved + :qty
 WHERE product_id = :pid
   AND (on_hand - reserved) >= :qty;

-- 해제 (release)
UPDATE product_stock
   SET reserved = reserved - :qty
 WHERE product_id = :pid
   AND reserved >= :qty;

-- 차감 (commit, Phase2)
UPDATE product_stock
   SET reserved = reserved - :qty,
       on_hand = on_hand - :qty
 WHERE product_id = :pid
   AND reserved >= :qty;
```

### 데드락 방지

- 다건 상품 주문 시 **productId 오름차순 정렬** 후 순차 예약
- 모든 예약이 성공해야 COMMIT, 하나라도 실패하면 전체 ROLLBACK

---

## 6. 주문 만료 배치 설계

```
@Scheduled(fixedDelay = 30_000)  // 30초마다

1. 만료 대상 조회 (100건씩)
   SELECT id FROM orders
    WHERE status = 'PENDING_PAYMENT'
      AND expires_at < now()
    LIMIT 100
    FOR UPDATE SKIP LOCKED;

2. 개별 주문 처리 (실패해도 나머지 계속)
   for each orderId:
     BEGIN TX
       CAS UPDATE: status = EXPIRED (WHERE status = PENDING_PAYMENT)
       재고 예약 해제 (release)
       바로주문(DIRECT)이면 장바구니 복원 트리거
     COMMIT
```

---

## 7. 구현 Phase 개요

| Phase | 도메인 | 핵심 산출물 | 의존 |
|---|---|---|---|
| 0 | 인프라 | 프로젝트 셋업, DDL, 글로벌 인프라 | - |
| 1 | User | 회원가입/로그인, JWT 발급 | Phase 0 |
| 2 | Brand | 브랜드 CRUD, 소프트 삭제 | Phase 1 |
| 3 | Product + Stock | 상품 CRUD, 재고 모델, Revision | Phase 2 |
| 4 | Like | 좋아요 등록/취소/목록 | Phase 3 |
| 5 | Cart | 장바구니 CRUD, availability 계산 | Phase 3 |
| 6 | Order | 주문 생성/취소, 만료 배치, 장바구니 복원 | Phase 3, 5 |
| 7 | Stats | 관리자 통계 대시보드 | Phase 6 |
| 8 | 하드닝 | E2E 테스트, 코드 품질, 문서화 | All |

---

## 8. 핵심 비즈니스 규칙 요약

### 재고
- `AvailableStock = onHand - reserved` (음수 불가)
- 장바구니는 재고를 예약하지 않음
- 주문 생성 시 예약, 취소/만료 시 해제, 결제 시 차감

### 상태 정책
- 고객용 API: ACTIVE만 조회 가능
- 장바구니: 모든 상태 표시 + unavailableReason 제공
- 주문 스냅샷: 상태 무관하게 시점 정보 보존

### 삭제 정책
- 브랜드/상품: 소프트 삭제 (status=DELETED, deletedAt 기록)
- 브랜드 삭제 → 소속 상품 cascade soft delete
- PENDING_PAYMENT 예약 존재 시 상품은 HIDDEN만 허용

### 장바구니 복원 (바로주문)
- EXPIRED / PAYMENT_FAILED → 원래 수량으로 장바구니 복원 (동기, 같은 트랜잭션)
- CANCELLED → 복원 안 함 (사용자 의도 존중)
- 복원 멱등성: order_cart_restore 테이블로 중복 방지
- 재고 초과 시 수량 그대로 복원 + 조회 시 unavailableReason 표시
