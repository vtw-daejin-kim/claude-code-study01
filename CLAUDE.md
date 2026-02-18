# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

이커머스 MVP 백엔드 — Spring Boot 3.4.1 기반, 결제를 제외한 유저/브랜드/상품/좋아요/장바구니/주문 도메인을 포함합니다.

## Build & Run Commands

```bash
./gradlew build                    # 빌드
./gradlew test                     # 전체 테스트 (Docker 필요 — Testcontainers)
./gradlew bootRun                  # 애플리케이션 실행 (default profile: local)
./gradlew clean build              # 클린 빌드

# 단일 테스트 실행
./gradlew test --tests "kr.go.ecommerce.global.exception.ErrorCodeTest"
./gradlew test --tests "kr.go.ecommerce.global.exception.ErrorCodeTest.allCodesShouldBeUnique"

# 단위 테스트만 (Docker 불필요)
./gradlew test --tests "kr.go.ecommerce.global.exception.*" --tests "kr.go.ecommerce.global.dto.*" --tests "kr.go.ecommerce.global.security.JwtTokenProviderTest" --tests "kr.go.ecommerce.global.security.CustomUserDetailsTest" --tests "kr.go.ecommerce.global.security.JwtAuthenticationFilterTest" --tests "kr.go.ecommerce.global.util.*"
```

로컬 DB 실행: `docker compose up -d` (PostgreSQL 15, port 5432)
통합 테스트는 Docker Desktop이 실행 중이어야 합니다 (Testcontainers).

## Tech Stack

- **Java 17** / **Gradle 8.12** / **Spring Boot 3.4.1**
- **JPA + MyBatis 하이브리드**: 쓰기는 JPA, 복잡한 읽기(검색/정렬/집계/배치)는 MyBatis
- **PostgreSQL 15+** / **Flyway** (DDL 버전 관리)
- **Spring Security + JWT** (jjwt 0.12.6, Stateless)
- **Testcontainers** (통합 테스트용 PostgreSQL)

## Architecture

```
kr.go.ecommerce
├── global/           # 횡단 관심사
│   ├── config/       # SecurityConfig, JpaConfig, MyBatisConfig, WebMvcConfig, SchedulerConfig
│   ├── security/     # JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetails(Service)
│   ├── exception/    # ErrorCode enum, BusinessException 계층, GlobalExceptionHandler
│   ├── dto/          # ApiResponse<T>, ErrorResponse, FieldError, PageResponse<T>
│   └── util/         # SecurityUtil, JsonSnapshotUtil
└── domain/           # 비즈니스 도메인 (DDD 스타일)
    └── {도메인명}/    # user, brand, product, like, cart, order, stats
        ├── entity/
        ├── repository/   # JPA Repository
        ├── mapper/       # MyBatis Mapper 인터페이스
        ├── service/
        ├── dto/
        └── controller/
```

MyBatis XML 매퍼: `src/main/resources/mapper/{도메인명}/*.xml`
Flyway 마이그레이션: `src/main/resources/db/migration/V{N}__*.sql`

## Key Patterns (코드 작성 시 따를 것)

### 예외 처리
- 모든 비즈니스 예외는 `ErrorCode` enum에 정의 후 `BusinessException` 하위 클래스를 사용
- 코드 접두사: `C`=Common, `A`=Auth, `U`=User, `B`=Brand, `P`=Product, `L`=Like, `CT`=Cart, `O`=Order
- 구체적 예외 클래스: `EntityNotFoundException`, `DuplicateException`, `StockInsufficientException`, `InvalidStateException`

### 응답 형식
- 성공: `ApiResponse.success(data)` 또는 `ApiResponse.created(data)`
- 에러: `GlobalExceptionHandler`가 자동 처리 → `ErrorResponse(code, message, fieldErrors)`
- 페이징: JPA는 `PageResponse.from(page)`, MyBatis는 `PageResponse.of(content, page, size, total)`

### 보안
- JWT 토큰 payload: `{ sub: userId, role: "USER"|"ADMIN" }`
- 인증 정보 추출: `SecurityUtil.getCurrentUserId()`, `SecurityUtil.getCurrentUserRole()`
- Public: `POST /api/v1/auth/**`, `GET /api/v1/brands/**`, `GET /api/v1/products/**`
- Admin: `/api-admin/**` → `ROLE_ADMIN` 필수

### 테스트
- 통합 테스트: `IntegrationTestBase` 상속 (Testcontainers PostgreSQL, `@ActiveProfiles("test")`)
- 단위 테스트: Spring 컨텍스트 없이 직접 인스턴스 생성

### DB 스키마 (10개 테이블)
`users`, `brands`, `products`, `product_stock`, `product_revisions`, `likes`, `cart_items`, `orders`, `order_items`, `order_cart_restore`

## Key Design Decisions

- **재고 동시성 제어**: 조건부 UPDATE(`WHERE (on_hand - reserved) >= :qty`)로 oversell 방지, productId 오름차순 정렬로 데드락 방지
- **주문 상태 전이**: CAS(Compare-And-Set) 패턴 — `UPDATE orders SET status=:new WHERE id=:id AND status=:expected`
- **소프트 삭제**: Brand/Product는 `status=DELETED` + `deleted_at` 기록
- **스냅샷**: 장바구니는 Product JOIN(최신 반영), 주문은 `snapshot_*` 컬럼(시점 고정)
- **장바구니 복원**: EXPIRED/PAYMENT_FAILED만 복원, CANCELLED는 복원 안 함 (동기 처리)
- **product_stock 분리**: 재고 UPDATE 락 범위를 상품 메타데이터 변경과 분리

## Configuration Profiles

- `local` (default): Docker PostgreSQL 직접 연결, SQL 로깅 활성화
- `test`: Testcontainers가 자동 구성 (IntegrationTestBase의 @DynamicPropertySource)
- `dev`: 환경변수 기반 DB 연결 (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`)
- `prod`: 환경변수 필수, HikariCP 풀 설정, `JWT_SECRET` 필수

## Documentation

- `01-requirements.md` — 요구사항 명세서 (FR/NFR 전체)
- `analysis.md` — 요구사항 분석, 확정 정책 9건, ERD/시퀀스/상태전이 다이어그램
- `plan.md` — 구현 계획 (기술 스택, API 설계, 패키지 구조)
- `task.md` — Phase 0~8 구현 체크리스트 + FR↔API 추적표
