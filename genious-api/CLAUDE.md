# CLAUDE.md - 지니어스 E-commerce API 프로젝트 컨텍스트

> 이 파일은 AI 어시스턴트(Claude/Cline)가 프로젝트를 이해하고 효과적으로 작업할 수 있도록 프로젝트 컨텍스트를 제공합니다.

---

## 📋 프로젝트 개요

- **프로젝트명**: Genious E-commerce API
- **설명**: E-commerce 플랫폼의 백엔드 REST API 서버
- **GitHub**: https://github.com/dd-jiny/claude-code-test01.git

### 핵심 비즈니스 도메인
온라인 쇼핑몰 시스템으로, 사용자가 상품을 조회하고 장바구니에 담아 주문/결제할 수 있는 플랫폼

---

## 🛠 기술 스택

### 백엔드
| 기술 | 버전 | 용도 |
|------|------|------|
| Java | 17 (LTS) | 메인 언어 |
| Spring Boot | 3.2.2 | 프레임워크 |
| Spring Security | 6.x | 인증/인가 |
| Spring Data JPA | 3.x | ORM |
| QueryDSL | 5.0.0 | 동적 쿼리 |
| PostgreSQL | 16.x | 메인 DB |
| Redis | 7.x | 캐시/세션 |
| JWT (jjwt) | 0.12.3 | 토큰 인증 |
| MapStruct | 1.5.5 | DTO 매핑 |
| SpringDoc OpenAPI | 2.3.0 | API 문서화 |
| Gradle | 8.x | 빌드 도구 |

### 인프라
- Docker & Docker Compose (개발 환경)
- GitHub Actions (CI/CD 예정)

---

## 📁 프로젝트 구조

```
genious-api/
├── src/main/java/com/genious/api/
│   ├── GeniousApiApplication.java    # 메인 클래스
│   ├── domain/                        # 도메인별 패키지 (예정)
│   │   ├── user/                      # 사용자 도메인
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── controller/
│   │   │   └── dto/
│   │   ├── product/                   # 상품 도메인
│   │   ├── cart/                      # 장바구니 도메인
│   │   ├── order/                     # 주문 도메인
│   │   ├── payment/                   # 결제 도메인
│   │   ├── inventory/                 # 재고 도메인
│   │   └── review/                    # 리뷰 도메인
│   ├── global/                        # 전역 설정
│   │   ├── common/                    # 공통 컴포넌트
│   │   │   └── BaseEntity.java        # ✅ 완료
│   │   ├── config/                    # 설정 클래스
│   │   ├── exception/                 # 예외 처리
│   │   ├── security/                  # 보안 설정
│   │   └── util/                      # 유틸리티
│   └── infra/                         # 외부 연동
│       └── storage/                   # 파일 저장소
├── src/main/resources/
│   ├── application.yml                # ✅ 완료 (프로파일별 설정)
│   └── data.sql                       # 초기 데이터 (예정)
└── src/test/java/                     # 테스트
```

---

## 🎯 핵심 설계 결정사항

### 1. 재고 관리 전략
- **결정**: 결제 완료 시 재고 차감 + 주문 생성 시 소프트 예약 (10분 TTL)
- **구현**: `InventoryReservation` 엔티티로 임시 예약 관리
- **이유**: 동시 주문 경합 완화 + 결제 실패 시 자동 복구

### 2. 주문-결제 트랜잭션
- **결정**: Service 계층 순차 호출 (@Transactional)
- **Phase 2**: Saga 패턴으로 전환 가능한 구조 유지
- **이유**: MVP에서는 구현 단순화 우선

### 3. 장바구니 저장
- **결정**: 하이브리드 (비로그인: localStorage, 로그인: DB)
- **이유**: 전환율 향상 + 디바이스 간 동기화

### 4. 인증 방식
- **결정**: JWT 기반 무상태(Stateless) 인증
- **Access Token**: 1시간
- **Refresh Token**: 7일

### 5. 동시성 제어
- **Phase 1**: 비관적 락 (Pessimistic Lock)
- **Phase 2**: Redis 분산 락 도입 예정

---

## 📐 코딩 컨벤션

### 네이밍 규칙
```java
// 클래스: PascalCase
public class ProductService { }

// 메서드/변수: camelCase
public ProductResponse getProductById(Long productId) { }

// 상수: SCREAMING_SNAKE_CASE
public static final String ORDER_PREFIX = "ORD";

// 패키지: lowercase
package com.genious.api.domain.product;
```

### 계층별 규칙

#### Controller
```java
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }
}
```

#### Service
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        // 비즈니스 로직
    }
}
```

#### Entity
```java
@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    // 생성 메서드 (정적 팩토리)
    public static Product create(String name, BigDecimal price) {
        Product product = new Product();
        product.name = name;
        product.price = price;
        return product;
    }
}
```

#### DTO
```java
// Request DTO
public record ProductCreateRequest(
    @NotBlank String name,
    @NotNull @Positive BigDecimal price,
    @NotNull Long categoryId
) { }

// Response DTO
public record ProductResponse(
    Long id,
    String name,
    BigDecimal price,
    String categoryName
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getCategory().getName()
        );
    }
}
```

### 예외 처리
```java
// 도메인별 예외 정의
public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(Long id) {
        super(ErrorCode.PRODUCT_NOT_FOUND, "Product not found: " + id);
    }
}

// GlobalExceptionHandler에서 처리
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        // 에러 응답 반환
    }
}
```

---

## 🗄 데이터베이스

### 연결 정보 (개발 환경)
```yaml
host: localhost
port: 5432
database: genious
username: genious
password: genious123
```

### 주요 테이블 (예정)
| 테이블 | 설명 |
|--------|------|
| users | 사용자 정보 |
| addresses | 배송지 주소 |
| categories | 상품 카테고리 |
| products | 상품 정보 |
| product_images | 상품 이미지 |
| product_options | 상품 옵션 (색상, 사이즈) |
| inventories | 재고 정보 |
| inventory_reservations | 재고 예약 (소프트 예약) |
| carts | 장바구니 |
| cart_items | 장바구니 상품 |
| orders | 주문 |
| order_items | 주문 상품 |
| payments | 결제 |
| reviews | 리뷰 |
| wishlists | 위시리스트 |

---

## 📊 API 설계 원칙

### URL 구조
```
GET    /api/v1/products           # 목록 조회
GET    /api/v1/products/{id}      # 단건 조회
POST   /api/v1/products           # 생성
PUT    /api/v1/products/{id}      # 수정
DELETE /api/v1/products/{id}      # 삭제
```

### 응답 형식
```json
// 성공 응답
{
  "success": true,
  "data": { ... },
  "message": null
}

// 에러 응답
{
  "success": false,
  "data": null,
  "error": {
    "code": "PRODUCT_NOT_FOUND",
    "message": "상품을 찾을 수 없습니다."
  }
}

// 페이징 응답
{
  "success": true,
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

---

## 🚀 개발 현황

### ✅ 완료된 항목
- [x] Spring Boot 프로젝트 초기 설정
- [x] build.gradle 의존성 구성
- [x] application.yml 프로파일 설정 (local/dev/prod)
- [x] BaseEntity (createdAt, updatedAt 자동 관리)
- [x] JPA Auditing 활성화
- [x] QueryDSL 빌드 설정
- [x] 요구사항 분석 및 설계 문서화

### 🔄 진행 중 (Sprint 1)
- [ ] JWT 인증 시스템 (JwtTokenProvider)
- [ ] Spring Security 설정 (SecurityConfig)
- [ ] User 엔티티 및 Repository
- [ ] 회원가입/로그인 API

### ⏳ 대기 중
- [ ] 상품 도메인 (Sprint 2)
- [ ] 재고 도메인 (Sprint 3)
- [ ] 장바구니 도메인 (Sprint 4)
- [ ] 주문 도메인 (Sprint 5)
- [ ] 결제 도메인 (Sprint 6)
- [ ] 리뷰 도메인 (Sprint 7)
- [ ] 관리자 기능 (Sprint 8)

---

## 📚 참조 문서

| 문서 | 설명 |
|------|------|
| [requirements.md](./requirements.md) | 요구사항 명세서 |
| [TASK.md](./TASK.md) | 개발 작업 계획 (Sprint별) |
| [요구사항_분석.md](전체_요구사항_분석.md) | 요구사항 분석 + 다이어그램 |
| [프로젝트 구조.md](./프로젝트%20구조.md) | 아키텍처 설계 |

---

## 🔧 개발 환경 설정

### 필수 요구사항
- Java 17+
- Docker & Docker Compose
- IDE: IntelliJ IDEA 또는 VS Code

### 로컬 실행 방법
```bash
# 1. PostgreSQL + Redis 실행 (Docker)
docker-compose up -d

# 2. 애플리케이션 실행
./gradlew bootRun

# 3. API 문서 확인
open http://localhost:8080/swagger-ui.html
```

### 테스트 실행
```bash
./gradlew test
```

### 빌드
```bash
./gradlew build
```

---

## ⚠️ 주의사항

### 코드 작성 시
1. **엔티티 직접 노출 금지**: Controller에서 Entity 대신 DTO 사용
2. **N+1 문제 방지**: Fetch Join 또는 @EntityGraph 활용
3. **트랜잭션 범위 최소화**: Service 메서드 단위로 관리
4. **예외 메시지에 민감정보 포함 금지**: 비밀번호, 토큰 등

### 보안
1. JWT Secret은 환경변수로 관리 (프로덕션)
2. 비밀번호는 BCrypt로 암호화
3. SQL Injection 방지: QueryDSL 또는 Parameter Binding 사용
4. XSS 방지: 입력값 검증 및 이스케이프

---

## 💡 자주 사용하는 명령어

```bash
# QueryDSL Q클래스 생성
./gradlew compileJava

# 빌드 캐시 정리
./gradlew clean build

# 특정 테스트만 실행
./gradlew test --tests "ProductServiceTest"

# 프로파일 지정 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

---

**마지막 업데이트**: 2026-02-09
