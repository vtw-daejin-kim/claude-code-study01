# 이커머스 MVP 구현 체크리스트

> 각 Phase는 순서대로 진행하며, 하위 작업은 독립적으로 병렬 진행 가능한 경우 표시합니다.
> 완료 시 `[ ]` → `[x]`로 체크합니다.

---

## Phase 0: 프로젝트 스캐폴딩

### 프로젝트 초기화
- [ ] Spring Boot 3.x + Gradle 프로젝트 생성
- [ ] `build.gradle` 의존성 설정
  - [ ] spring-boot-starter-web
  - [ ] spring-boot-starter-data-jpa
  - [ ] spring-boot-starter-security
  - [ ] spring-boot-starter-validation
  - [ ] mybatis-spring-boot-starter 3.x
  - [ ] postgresql driver
  - [ ] flyway-core + flyway-database-postgresql
  - [ ] jjwt-api / jjwt-impl / jjwt-jackson (0.12.x)
  - [ ] lombok
  - [ ] spring-boot-starter-test + testcontainers
- [ ] `settings.gradle` 설정
- [ ] `.gitignore` 생성
- [ ] `docker-compose.yml` 생성 (PostgreSQL)

### 설정 파일
- [ ] `application.yml` 기본 설정
- [ ] `application-local.yml` 로컬 프로파일
- [ ] `application-dev.yml` 개발 프로파일
- [ ] `application-prod.yml` 운영 프로파일

### DB 마이그레이션
- [ ] `V1__init_schema.sql` — 전체 DDL 작성
  - [ ] `users` 테이블
  - [ ] `brands` 테이블
  - [ ] `products` 테이블
  - [ ] `product_stock` 테이블
  - [ ] `product_revisions` 테이블
  - [ ] `likes` 테이블
  - [ ] `cart_items` 테이블
  - [ ] `orders` 테이블
  - [ ] `order_items` 테이블
  - [ ] `order_cart_restore` 테이블
  - [ ] 인덱스 (partial index 포함)
  - [ ] CHECK 제약조건
- [ ] `V2__seed_admin_user.sql` — 개발용 관리자 계정

### 글로벌 인프라 (global/)
- [ ] 예외 처리
  - [ ] `ErrorCode` enum 정의
  - [ ] `BusinessException` 기본 예외 클래스
  - [ ] `EntityNotFoundException`
  - [ ] `DuplicateException`
  - [ ] `StockInsufficientException`
  - [ ] `InvalidStateException`
  - [ ] `GlobalExceptionHandler` (@RestControllerAdvice)
- [ ] 공통 응답 DTO
  - [ ] `ApiResponse<T>` (성공 래퍼)
  - [ ] `ErrorResponse` (에러 래퍼)
  - [ ] `PageResponse<T>` (페이지네이션 래퍼)
  - [ ] `FieldError`
- [ ] 보안 설정
  - [ ] `SecurityConfig` (필터 체인, 경로별 권한)
  - [ ] `JwtConfig` (속성 바인딩)
  - [ ] `JwtTokenProvider` (토큰 생성/검증/파싱)
  - [ ] `JwtAuthenticationFilter` (OncePerRequestFilter)
  - [ ] `CustomUserDetails`
  - [ ] `CustomUserDetailsService`
- [ ] 설정 클래스
  - [ ] `MyBatisConfig`
  - [ ] `JpaConfig`
  - [ ] `WebMvcConfig`
  - [ ] `SchedulerConfig` (@EnableScheduling)
- [ ] 유틸리티
  - [ ] `SecurityUtil` (현재 사용자 ID 추출)
  - [ ] `JsonSnapshotUtil` (스냅샷 JSON 직렬화)

### 검증
- [ ] 애플리케이션 부팅 확인
- [ ] Flyway 마이그레이션 실행 확인
- [ ] JWT 토큰 발급/검증 단위 테스트

---

## Phase 1: User 도메인 (계정 + 인증)

### 엔티티 & 리포지토리
- [ ] `User` 엔티티 (`users` 테이블 매핑)
- [ ] `UserRole` enum (USER, ADMIN)
- [ ] `UserRepository` (JPA)
  - [ ] `findByLoginId`
  - [ ] `findByEmail`
  - [ ] `existsByLoginId`
  - [ ] `existsByEmail`

### 서비스
- [ ] `AuthService`
  - [ ] `signup()` — loginId/email 중복 검증, 비밀번호 정책 검증, BCrypt 해싱, JWT 발급
  - [ ] `login()` — loginId + password 인증, JWT 발급
- [ ] `UserService`
  - [ ] `getProfile()` — 현재 사용자 정보 조회
  - [ ] `changePassword()` — 현재 비밀번호 검증 후 변경

### DTO
- [ ] `SignupRequest` (loginId, email, password, name) + 유효성 검증 어노테이션
- [ ] `LoginRequest` (loginId, password)
- [ ] `LoginResponse` (token, userId)
- [ ] `UserProfileResponse` (id, loginId, email, name, createdAt)
- [ ] `PasswordChangeRequest` (currentPassword, newPassword)

### 컨트롤러
- [ ] `AuthController` (/api/v1/auth)
  - [ ] `POST /api/v1/auth/signup` → FR-U-001
  - [ ] `POST /api/v1/auth/login`
- [ ] `UserController` (/api/v1/users)
  - [ ] `GET /api/v1/users/me` → FR-U-002
  - [ ] `PUT /api/v1/users/me/password` → FR-U-003

### 테스트
- [ ] `AuthService` 단위 테스트 (가입 검증, 중복 체크, 비밀번호 정책)
- [ ] `UserService` 단위 테스트 (프로필 조회, 비밀번호 변경)
- [ ] `AuthController` 통합 테스트 (가입→로그인 플로우)
- [ ] `UserController` 통합 테스트 (인증 필수 검증)

---

## Phase 2: Brand 도메인

### 엔티티 & 리포지토리
- [ ] `Brand` 엔티티
- [ ] `BrandStatus` enum (ACTIVE, HIDDEN, DELETED)
- [ ] `BrandRepository` (JPA)

### 서비스
- [ ] `BrandService` (고객용)
  - [ ] `listBrands(q, pageable)` — ACTIVE만, 키워드 검색
  - [ ] `getBrand(brandId)` — ACTIVE만
- [ ] `BrandAdminService` (관리자용)
  - [ ] `listBrands(q, pageable)` — 전체 상태
  - [ ] `getBrand(brandId)` — 전체 상태
  - [ ] `createBrand()` — status=ACTIVE
  - [ ] `updateBrand()`
  - [ ] `deleteBrand()` — 소프트 삭제, cascade 상품 삭제, PENDING_PAYMENT 보호

### DTO
- [ ] `BrandListResponse`
- [ ] `BrandDetailResponse`
- [ ] `BrandCreateRequest` + 유효성 검증
- [ ] `BrandUpdateRequest` + 유효성 검증

### 컨트롤러
- [ ] `BrandController` (/api/v1/brands)
  - [ ] `GET /api/v1/brands` → FR-C-000
  - [ ] `GET /api/v1/brands/{brandId}` → FR-C-001
- [ ] `BrandAdminController` (/api-admin/v1/brands)
  - [ ] `GET /api-admin/v1/brands` → FR-A-001
  - [ ] `GET /api-admin/v1/brands/{brandId}` → FR-A-001
  - [ ] `POST /api-admin/v1/brands` → FR-A-001
  - [ ] `PUT /api-admin/v1/brands/{brandId}` → FR-A-001
  - [ ] `DELETE /api-admin/v1/brands/{brandId}` → FR-A-001

### 테스트
- [ ] `BrandService` 단위 테스트
- [ ] `BrandAdminService` 단위 테스트 (삭제 시 cascade, PENDING_PAYMENT 보호)
- [ ] 컨트롤러 통합 테스트 (권한 분리: Guest 조회 가능, Admin만 CUD)

---

## Phase 3: Product + Stock 도메인

### 엔티티 & 리포지토리
- [ ] `Product` 엔티티
- [ ] `ProductStatus` enum (ACTIVE, HIDDEN, DELETED)
- [ ] `ProductStock` 엔티티 (Product와 1:1)
- [ ] `ProductRevision` 엔티티
- [ ] `ProductRepository` (JPA)
- [ ] `ProductStockRepository` (JPA)
  - [ ] `reserveStock(productId, qty)` — 조건부 UPDATE (네이티브 쿼리)
  - [ ] `releaseStock(productId, qty)` — 조건부 UPDATE
  - [ ] `commitStock(productId, qty)` — Phase2용 조건부 UPDATE
- [ ] `ProductRevisionRepository` (JPA)
- [ ] `ProductQueryMapper` (MyBatis)
  - [ ] `findProductsForCustomer()` — 키워드 검색, 정렬, 페이징, JOIN stock/likes

### MyBatis 매퍼 XML
- [ ] `mapper/ProductQueryMapper.xml`
  - [ ] 고객용 상품 목록 쿼리 (ACTIVE + ACTIVE brand, 키워드, 정렬)
  - [ ] 관리자용 상품 목록 쿼리 (includeDeleted 지원)

### 서비스
- [ ] `ProductService` (고객용)
  - [ ] `listProducts(brandId, q, sort, pageable)` — MyBatis 매퍼 사용
  - [ ] `getProduct(productId)` — ACTIVE만, availableStock + likesCount 포함
- [ ] `ProductAdminService` (관리자용)
  - [ ] `listProducts(brandId, q, includeDeleted, pageable)`
  - [ ] `getProduct(productId)` — 전체 상태
  - [ ] `createProduct()` — 브랜드 존재 + ACTIVE 검증, stock 초기화
  - [ ] `updateProduct()` — brandId 변경 불가, onHand >= reserved 검증, Revision 생성
  - [ ] `deleteProduct()` — PENDING_PAYMENT 존재 시 HIDDEN, 아니면 DELETED, Revision 생성
  - [ ] `listRevisions(productId, pageable)`
  - [ ] `getRevision(productId, revisionId)`
- [ ] `ProductStockService`
  - [ ] `reserve(commands)` — productId 오름차순 정렬, 조건부 UPDATE, 전체 롤백
  - [ ] `release(commands)` — 예약 해제
  - [ ] `commit(commands)` — Phase2, onHand + reserved 동시 감소
  - [ ] `getAvailableStock(productId)`

### DTO
- [ ] `ProductListResponse` (id, name, price, imageUrl, brandId, brandName, availableStock, likesCount)
- [ ] `ProductDetailResponse`
- [ ] `ProductCreateRequest` (brandId, name, description, price, imageUrl, onHand) + 유효성 검증
- [ ] `ProductUpdateRequest` (name, description, price, imageUrl, status, onHand, changeReason)
- [ ] `ProductAdminListResponse`
- [ ] `ProductRevisionListResponse`
- [ ] `ProductRevisionDetailResponse`

### 컨트롤러
- [ ] `ProductController` (/api/v1/products)
  - [ ] `GET /api/v1/products` → FR-C-002
  - [ ] `GET /api/v1/products/{productId}` → FR-C-003
- [ ] `ProductAdminController` (/api-admin/v1/products)
  - [ ] `GET /api-admin/v1/products` → FR-A-002-1
  - [ ] `GET /api-admin/v1/products/{productId}` → FR-A-002-2
  - [ ] `POST /api-admin/v1/products` → FR-A-002
  - [ ] `PUT /api-admin/v1/products/{productId}` → FR-A-002-5
  - [ ] `DELETE /api-admin/v1/products/{productId}` → FR-A-002-6
  - [ ] `GET /api-admin/v1/products/{productId}/revisions` → FR-A-002-3
  - [ ] `GET /api-admin/v1/products/{productId}/revisions/{revisionId}` → FR-A-002-4

### 테스트
- [ ] `ProductStockService` 단위 테스트 (reserve 성공/실패, release, 정렬 순서)
- [ ] `ProductAdminService` 단위 테스트 (Revision 생성, HIDDEN/DELETED 분기, onHand 검증)
- [ ] **재고 동시성 통합 테스트** — 20개 스레드 동시 reserve, oversell 방지 검증
- [ ] **데드락 방지 통합 테스트** — 교차 상품 주문 시 데드락 없음 검증
- [ ] MyBatis 매퍼 테스트 (키워드 검색, 정렬, 필터)

---

## Phase 4: Like 도메인

### 엔티티 & 리포지토리
- [ ] `Like` 엔티티 (user_id + product_id UNIQUE)
- [ ] `LikeRepository` (JPA)
  - [ ] `findByUserIdAndProductId`
  - [ ] `existsByUserIdAndProductId`
  - [ ] `deleteByUserIdAndProductId`
  - [ ] `findByUserIdOrderByCreatedAtDesc`
  - [ ] `countByProductId`

### 서비스
- [ ] `LikeService`
  - [ ] `addLike(userId, productId)` — 멱등, 상품 ACTIVE 검증
  - [ ] `removeLike(userId, productId)` — 멱등 (없으면 no-op)
  - [ ] `getMyLikes(userId, pageable)` — 상품 정보 포함

### DTO
- [ ] `LikedProductResponse` (productId, productName, price, imageUrl, brandName, status, likedAt)

### 컨트롤러
- [ ] `LikeController`
  - [ ] `POST /api/v1/products/{productId}/likes` → FR-L-001
  - [ ] `DELETE /api/v1/products/{productId}/likes` → FR-L-002
  - [ ] `GET /api/v1/users/me/likes` → FR-L-003

### 테스트
- [ ] `LikeService` 단위 테스트 (멱등성 add/remove, 비활성 상품 차단)
- [ ] 컨트롤러 통합 테스트

---

## Phase 5: Cart 도메인

### 엔티티 & 리포지토리
- [ ] `CartItem` 엔티티 (user_id + product_id UNIQUE)
- [ ] `CartItemRepository` (JPA)
  - [ ] `findByUserId`
  - [ ] `findByUserIdAndProductId`
  - [ ] `countByUserId`
  - [ ] `deleteByUserIdAndProductId`
- [ ] `CartItemQueryMapper` (MyBatis)
  - [ ] `findCartItemsWithProductInfo()` — product + brand + stock JOIN

### MyBatis 매퍼 XML
- [ ] `mapper/CartItemQueryMapper.xml`
  - [ ] 장바구니 목록 조회 쿼리 (product + brand + stock LEFT JOIN)

### 서비스
- [ ] `CartService`
  - [ ] `addItem(userId, productId, qty)` — 상품 ACTIVE 검증, 재고 초과 검증, 중복 시 수량 병합, MAX_CART_ITEMS 검증
  - [ ] `removeItem(userId, productId)` — 멱등
  - [ ] `updateQuantity(userId, productId, qty)` — qty >= 1, 재고 초과 검증, MAX_QTY_PER_ITEM 검증
  - [ ] `getCartItems(userId)` — MyBatis JOIN 쿼리, availability 계산
  - [ ] `removeByProductIds(userId, productIds)` — 주문 확정 시 장바구니 정리용 (Phase2)
  - [ ] `restoreFromOrder(userId, orderItems)` — 바로주문 실패 시 복원, 기존 항목 수량 병합

### Availability 계산 로직
- [ ] `DELETED` → unavailableReason = "DELETED"
- [ ] `HIDDEN` → unavailableReason = "HIDDEN"
- [ ] Brand `DELETED/HIDDEN` → unavailableReason = "BRAND_DELETED"
- [ ] availableStock <= 0 → unavailableReason = "SOLD_OUT"
- [ ] quantity > availableStock → unavailableReason = "OUT_OF_STOCK"
- [ ] quantity > MAX_QTY_PER_ITEM → unavailableReason = "INVALID_QUANTITY"

### DTO
- [ ] `CartAddRequest` (productId, quantity) + 유효성 검증
- [ ] `CartUpdateQuantityRequest` (quantity) + 유효성 검증
- [ ] `CartItemResponse` (id, productId, productName, price, imageUrl, brandId, brandName, quantity, available, unavailableReason, availableStock)

### 컨트롤러
- [ ] `CartController` (/api/v1/cart)
  - [ ] `GET /api/v1/cart` → FR-CART-005
  - [ ] `POST /api/v1/cart/items` → FR-CART-001
  - [ ] `PATCH /api/v1/cart/items/{productId}` → FR-CART-003
  - [ ] `DELETE /api/v1/cart/items/{productId}` → FR-CART-002
- [ ] `CartAdminController` (/api-admin/v1/users/{userId}/cart)
  - [ ] `GET /api-admin/v1/users/{userId}/cart` → FR-A-004

### 테스트
- [ ] `CartService` 단위 테스트 (추가/병합, 제한, availability 계산, 복원)
- [ ] MyBatis 매퍼 테스트 (JOIN 쿼리 정확성)
- [ ] 컨트롤러 통합 테스트

---

## Phase 6: Order 도메인

### 엔티티 & 리포지토리
- [ ] `Order` 엔티티
- [ ] `OrderItem` 엔티티 (스냅샷 컬럼 포함)
- [ ] `OrderStatus` enum (PENDING_PAYMENT, CANCELLED, EXPIRED, PAID, PAYMENT_FAILED)
- [ ] `OrderCartRestore` 엔티티
- [ ] `OrderRepository` (JPA)
  - [ ] `findByUserIdAndCreatedAtBetween`
  - [ ] `findByIdAndUserId`
  - [ ] `findByIdempotencyKeyAndUserId`
  - [ ] `cancelOrder(orderId)` — CAS UPDATE (status=PENDING_PAYMENT → CANCELLED)
- [ ] `OrderItemRepository` (JPA)
  - [ ] `findByOrderId`
- [ ] `OrderCartRestoreRepository` (JPA)
  - [ ] `existsByOrderId`
- [ ] `OrderQueryMapper` (MyBatis)
  - [ ] 관리자 주문 목록 쿼리 (필터: userId, status, 기간)
  - [ ] 만료 배치 쿼리 (CAS + FOR UPDATE SKIP LOCKED)

### MyBatis 매퍼 XML
- [ ] `mapper/OrderQueryMapper.xml`
  - [ ] 관리자 주문 목록 쿼리
  - [ ] 만료 배치 CAS 쿼리 (RETURNING id)

### 서비스
- [ ] `OrderService`
  - [ ] `createOrder(userId, items, idempotencyKey)`
    - [ ] 요청 검증 (items 비어있지 않음, quantity >= 1)
    - [ ] 동일 productId 합산 병합
    - [ ] 상품/브랜드 유효성 검증 (ACTIVE만)
    - [ ] productId 오름차순 정렬
    - [ ] **단일 트랜잭션**: 재고 예약 → 주문서 저장 → 스냅샷 저장
    - [ ] expiresAt 설정 (현재 + 15분)
    - [ ] totalAmount 계산
    - [ ] 멱등성 키 중복 체크
  - [ ] `cancelOrder(userId, orderId)`
    - [ ] CAS UPDATE (PENDING_PAYMENT → CANCELLED)
    - [ ] 이미 취소된 경우 멱등 처리
    - [ ] 재고 예약 해제
    - [ ] 장바구니 복원 안 함 (CANCELLED는 복원 대상 아님)
  - [ ] `getOrders(userId, startAt, endAt, pageable)`
  - [ ] `getOrderDetail(userId, orderId)` — 본인 주문만
- [ ] `OrderExpireScheduler`
  - [ ] `@Scheduled(fixedDelay=30s)` 만료 배치
  - [ ] CAS 기반 상태 전이 (PENDING_PAYMENT → EXPIRED, expiresAt < now())
  - [ ] 배치 사이즈 처리 (100건씩)
  - [ ] 개별 주문 실패 시 나머지 계속 처리
  - [ ] 재고 예약 해제
  - [ ] 장바구니 복원 트리거 (DIRECT 주문만, EXPIRED 사유)
- [ ] `CartRestorationService`
  - [ ] `restoreIfNeeded(orderId, reason)`
    - [ ] EXPIRED / PAYMENT_FAILED만 처리 (CANCELLED 제외)
    - [ ] 멱등성 체크 (`order_cart_restore` 테이블)
    - [ ] 원래 수량 그대로 장바구니에 추가 (기존 항목 있으면 수량 병합)
    - [ ] 복원 기록 저장

### DTO
- [ ] `OrderCreateRequest` (items: [{productId, quantity}], idempotencyKey)
- [ ] `OrderItemRequest` (productId, quantity)
- [ ] `OrderResponse` (orderId, status, totalAmount, expiresAt, items)
- [ ] `OrderDetailResponse` (orderId, status, totalAmount, expiresAt, items with snapshots, createdAt)
- [ ] `OrderItemResponse` (productId, quantity, snapshotProductName, snapshotUnitPrice, snapshotBrandName, snapshotImageUrl, subtotal)
- [ ] `OrderListResponse` (orderId, status, totalAmount, itemCount, createdAt)

### 컨트롤러
- [ ] `OrderController` (/api/v1/orders)
  - [ ] `POST /api/v1/orders` → FR-O-001
  - [ ] `GET /api/v1/orders` → FR-O-002
  - [ ] `GET /api/v1/orders/{orderId}` → FR-O-003, FR-O-005
  - [ ] `POST /api/v1/orders/{orderId}/cancel` → FR-O-004
- [ ] `OrderAdminController` (/api-admin/v1/orders)
  - [ ] `GET /api-admin/v1/orders` → FR-A-003
  - [ ] `GET /api-admin/v1/orders/{orderId}` → FR-A-003

### 테스트
- [ ] `OrderService.createOrder` 단위 테스트
  - [ ] 정상 생성 (단일/다건 상품)
  - [ ] 동일 productId 병합
  - [ ] 재고 부족 시 전체 롤백
  - [ ] 비활성 상품 거부 (HIDDEN/DELETED)
  - [ ] 비활성 브랜드 거부
  - [ ] 멱등성 키 중복 처리
- [ ] `OrderService.cancelOrder` 단위 테스트
  - [ ] 정상 취소 + 재고 해제
  - [ ] 이미 취소된 주문 멱등 처리
  - [ ] EXPIRED/PAID 주문 취소 거부
  - [ ] 본인 주문 아닌 경우 거부
- [ ] `OrderExpireScheduler` 단위 테스트
  - [ ] 만료 대상 조회 + 상태 전이
  - [ ] 재고 해제 확인
  - [ ] 장바구니 복원 트리거 확인
- [ ] `CartRestorationService` 단위 테스트
  - [ ] EXPIRED → 복원 O
  - [ ] PAYMENT_FAILED → 복원 O
  - [ ] CANCELLED → 복원 X
  - [ ] 중복 복원 방지 (멱등)
  - [ ] 기존 장바구니 항목 수량 병합
- [ ] **주문 생성 동시성 통합 테스트** — 동시 주문 시 oversell 방지
- [ ] **만료 배치 통합 테스트** — 타이머 만료 → 상태 전이 → 재고 해제 → 복원
- [ ] **주문→취소→재고 해제 E2E 테스트**

---

## Phase 7: Admin 통계 도메인

### 리포지토리
- [ ] `StatsQueryMapper` (MyBatis)
  - [ ] `getOrderCountsByStatus(startAt, endAt)` — 상태별 주문 건수
  - [ ] `getDailyOrderStats(startAt, endAt)` — 일별 주문 통계
  - [ ] `getTopLikedProducts(startAt, endAt, limit)` — 좋아요 TOP N
  - [ ] `getTopOrderedProducts(startAt, endAt, limit)` — 주문 TOP N
  - [ ] `getLowStockProducts(threshold, limit)` — 저재고 목록

### MyBatis 매퍼 XML
- [ ] `mapper/StatsQueryMapper.xml`
  - [ ] 주문 상태별 집계 쿼리
  - [ ] 일별 주문 통계 쿼리
  - [ ] 좋아요 TOP N 쿼리
  - [ ] 주문 TOP N 쿼리
  - [ ] 저재고 목록 쿼리

### 서비스
- [ ] `StatsService`
  - [ ] `getOverview(startAt, endAt)`
  - [ ] `getDailyOrderStats(startAt, endAt)`
  - [ ] `getTopLikedProducts(startAt, endAt, limit)`
  - [ ] `getTopOrderedProducts(startAt, endAt, limit)`
  - [ ] `getLowStockProducts(threshold, limit)`

### DTO
- [ ] `StatsOverviewResponse` (pending, expired, cancelled, totalAmount)
- [ ] `DailyOrderStatsResponse` (date, total, pending, expired, cancelled, amount)
- [ ] `TopProductResponse` (id, name, brandName, count)
- [ ] `LowStockResponse` (id, name, brandName, onHand, reserved, availableStock)

### 컨트롤러
- [ ] `StatsAdminController` (/api-admin/v1/stats)
  - [ ] `GET /api-admin/v1/stats/overview` → FR-A-005
  - [ ] `GET /api-admin/v1/stats/orders/daily` → FR-A-005
  - [ ] `GET /api-admin/v1/stats/products/top-liked` → FR-A-005
  - [ ] `GET /api-admin/v1/stats/products/top-ordered` → FR-A-005
  - [ ] `GET /api-admin/v1/stats/stocks/low` → FR-A-005

### 테스트
- [ ] MyBatis 매퍼 테스트 (집계 쿼리 정확성)
- [ ] `StatsService` 단위 테스트
- [ ] 컨트롤러 통합 테스트 (Admin 권한 필수)

---

## Phase 8: 마무리 & 하드닝

### 통합 테스트
- [ ] **E2E 시나리오 테스트: 회원가입 → 상품 탐색 → 좋아요 → 장바구니 → 주문 → 취소**
- [ ] **E2E 시나리오 테스트: 바로주문 → 만료 → 장바구니 복원 확인**
- [ ] **E2E 시나리오 테스트: 관리자 브랜드 삭제 → 상품 cascade → 장바구니 unavailable 확인**
- [ ] **재고 동시성 스트레스 테스트** (멀티스레드 대량 주문)

### 코드 품질
- [ ] N+1 쿼리 점검 (특히 장바구니 조회, 주문 목록 조회)
- [ ] 트랜잭션 경계 점검 (불필요한 트랜잭션 확장 없는지)
- [ ] 예외 처리 누락 점검
- [ ] 입력 유효성 검증 누락 점검

### 문서화 (선택)
- [ ] Swagger/OpenAPI 어노테이션 추가
- [ ] API 문서 자동 생성 확인

### 운영 준비 (선택)
- [ ] 로깅 전략 점검 (민감정보 제외)
- [ ] 헬스체크 엔드포인트 (/actuator/health)
- [ ] 환경별 설정 분리 확인

---

## FR ↔ API ↔ Phase 추적표

| FR | API | Phase | 상태 |
|---|---|---|---|
| FR-U-001 | POST /api/v1/auth/signup | Phase 1 | [ ] |
| FR-U-002 | GET /api/v1/users/me | Phase 1 | [ ] |
| FR-U-003 | PUT /api/v1/users/me/password | Phase 1 | [ ] |
| FR-C-000 | GET /api/v1/brands | Phase 2 | [ ] |
| FR-C-001 | GET /api/v1/brands/{brandId} | Phase 2 | [ ] |
| FR-C-001-1 | (브랜드 상태 정책) | Phase 2 | [ ] |
| FR-C-002 | GET /api/v1/products | Phase 3 | [ ] |
| FR-C-003 | GET /api/v1/products/{productId} | Phase 3 | [ ] |
| FR-C-004 | (삭제/비노출 제외 규칙) | Phase 3 | [ ] |
| FR-C-005 | (상품 상태 정책) | Phase 3 | [ ] |
| FR-L-001 | POST /api/v1/products/{productId}/likes | Phase 4 | [ ] |
| FR-L-002 | DELETE /api/v1/products/{productId}/likes | Phase 4 | [ ] |
| FR-L-003 | GET /api/v1/users/me/likes | Phase 4 | [ ] |
| FR-L-004 | (좋아요 수 집계) | Phase 4 | [ ] |
| FR-CART-001 | POST /api/v1/cart/items | Phase 5 | [ ] |
| FR-CART-002 | DELETE /api/v1/cart/items/{productId} | Phase 5 | [ ] |
| FR-CART-003 | PATCH /api/v1/cart/items/{productId} | Phase 5 | [ ] |
| FR-CART-004 | (장바구니 선택 주문) | Phase 6 | [ ] |
| FR-CART-005 | GET /api/v1/cart | Phase 5 | [ ] |
| FR-CART-006 | (결제 성공 시 장바구니 정리) | Phase2 확장 | [ ] |
| FR-CART-007 | (바로주문 실패 시 복원) | Phase 6 | [ ] |
| FR-CART-008 | (장바구니 제한) | Phase 5 | [ ] |
| FR-O-000 | (주문 상태) | Phase 6 | [ ] |
| FR-O-001 | POST /api/v1/orders | Phase 6 | [ ] |
| FR-O-002 | GET /api/v1/orders | Phase 6 | [ ] |
| FR-O-003 | GET /api/v1/orders/{orderId} | Phase 6 | [ ] |
| FR-O-004 | POST /api/v1/orders/{orderId}/cancel | Phase 6 | [ ] |
| FR-O-005 | (스냅샷 상세 조회) | Phase 6 | [ ] |
| FR-O-006 | (멱등성) | Phase 6 | [ ] |
| FR-O-007 | (재고 예약 + 만료) | Phase 6 | [ ] |
| FR-O-008 | (결제 완료 처리) | Phase2 확장 | [ ] |
| FR-A-001 | /api-admin/v1/brands CRUD | Phase 2 | [ ] |
| FR-A-002 | POST /api-admin/v1/products | Phase 3 | [ ] |
| FR-A-002-1 | GET /api-admin/v1/products | Phase 3 | [ ] |
| FR-A-002-2 | GET /api-admin/v1/products/{id} | Phase 3 | [ ] |
| FR-A-002-3 | GET /api-admin/v1/products/{id}/revisions | Phase 3 | [ ] |
| FR-A-002-4 | GET /api-admin/v1/products/{id}/revisions/{rid} | Phase 3 | [ ] |
| FR-A-002-5 | PUT /api-admin/v1/products/{id} | Phase 3 | [ ] |
| FR-A-002-6 | DELETE /api-admin/v1/products/{id} | Phase 3 | [ ] |
| FR-A-003 | /api-admin/v1/orders | Phase 6 | [ ] |
| FR-A-004 | GET /api-admin/v1/users/{userId}/cart | Phase 5 | [ ] |
| FR-A-005 | /api-admin/v1/stats/* | Phase 7 | [ ] |
