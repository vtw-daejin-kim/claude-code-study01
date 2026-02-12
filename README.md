# claude-code-study01

클로드 코드 연습을 위한 레포지토리입니다.

## 📋 프로젝트 개요

**Genious E-commerce API** - Spring Boot 기반의 E-commerce 플랫폼 백엔드 REST API 서버

### 핵심 기능
- 사용자 인증/인가 (JWT 기반)
- 상품 관리 및 조회
- 장바구니 관리
- 주문 및 결제 처리
- 재고 관리
- 상품 리뷰 시스템

## 🛠 기술 스택

### Backend
- **Java** 17 (LTS)
- **Spring Boot** 3.2.2
- **Spring Security** 6.x
- **Spring Data JPA** 3.x
- **QueryDSL** 5.0.0
- **PostgreSQL** 16.x
- **Redis** 7.x
- **JWT** (jjwt 0.12.3)
- **Gradle** 8.x

### Tools & Libraries
- MapStruct (DTO 매핑)
- SpringDoc OpenAPI (API 문서화)
- Lombok
- Docker & Docker Compose

## 📁 프로젝트 구조

```
claude-code-study01/
├── README.md
└── genious-api/                          # Spring Boot 백엔드 프로젝트
    ├── CLAUDE.md                         # AI 어시스턴트용 프로젝트 컨텍스트
    ├── build.gradle                      # Gradle 빌드 설정
    ├── settings.gradle
    ├── .claude/                          # Claude Code 설정
    │   ├── agents/                       # Sprint별 에이전트 가이드
    │   │   ├── sprint-orchestrator.md
    │   │   ├── sprint1-user-auth.md
    │   │   ├── sprint2-product.md
    │   │   ├── sprint3-inventory.md
    │   │   ├── sprint4-cart-wishlist.md
    │   │   ├── sprint5-order.md
    │   │   ├── sprint6-payment.md
    │   │   ├── sprint7-review.md
    │   │   ├── sprint8-admin.md
    │   │   └── sprint9-testing.md
    │   ├── commands/                     # 커스텀 명령어
    │   │   ├── plan.md
    │   │   └── tdd.md
    │   └── SKILLS/                       # 커스텀 스킬
    │       └── requirements-analysis/
    ├── src/
    │   ├── main/
    │   │   ├── java/com/genious/api/
    │   │   │   ├── GeniousApiApplication.java
    │   │   │   ├── domain/              # 도메인별 패키지 (DDD)
    │   │   │   │   ├── user/           # 사용자 도메인
    │   │   │   │   │   ├── entity/
    │   │   │   │   │   │   └── Role.java
    │   │   │   │   │   ├── repository/
    │   │   │   │   │   ├── service/
    │   │   │   │   │   ├── controller/
    │   │   │   │   │   └── dto/
    │   │   │   │   ├── product/        # 상품 도메인 (예정)
    │   │   │   │   ├── cart/           # 장바구니 도메인 (예정)
    │   │   │   │   ├── order/          # 주문 도메인 (예정)
    │   │   │   │   ├── payment/        # 결제 도메인 (예정)
    │   │   │   │   ├── inventory/      # 재고 도메인 (예정)
    │   │   │   │   └── review/         # 리뷰 도메인 (예정)
    │   │   │   └── global/             # 전역 설정 및 공통 컴포넌트
    │   │   │       ├── common/
    │   │   │       │   ├── ApiResponse.java      # 공통 API 응답
    │   │   │       │   ├── BaseEntity.java       # 공통 엔티티
    │   │   │       │   └── PageResponse.java     # 페이징 응답
    │   │   │       ├── config/         # 설정 클래스 (예정)
    │   │   │       ├── exception/
    │   │   │       │   ├── BusinessException.java
    │   │   │       │   ├── ErrorCode.java
    │   │   │       │   └── GlobalExceptionHandler.java
    │   │   │       ├── security/       # 보안 설정 (예정)
    │   │   │       └── util/           # 유틸리티 (예정)
    │   │   └── resources/
    │   │       └── application.yml     # 환경별 설정 (local/dev/prod)
    │   └── test/                        # 테스트 코드 (예정)
    ├── 사용자관리_요구사항.md
    ├── 사용자관리_PLAN.md
    ├── 사용자관리_TASK.md
    ├── 전체_요구사항.md
    ├── 전체_요구사항_분석.md
    ├── 전체_TASK.md
    └── 전체_프로젝트 구조.md
```

## 🏗 아키텍처 구조

### 시스템 아키텍처

```mermaid
graph TB
    subgraph "Client Layer"
        Client[사용자/프론트엔드]
    end

    subgraph "API Layer"
        API[Spring Boot API<br/>Port 8080]
    end

    subgraph "Data Layer"
        DB[(PostgreSQL<br/>Port 5432)]
        Cache[(Redis<br/>Port 6379)]
    end

    Client -->|HTTP/REST| API
    API -->|JPA/Hibernate| DB
    API -->|Session/Cache| Cache

    style Client fill:#e1f5ff
    style API fill:#fff4e1
    style DB fill:#e8f5e9
    style Cache fill:#fce4ec
```

### 애플리케이션 계층 구조

```mermaid
graph LR
    subgraph "Presentation Layer"
        Controller[Controller<br/>@RestController]
    end

    subgraph "Business Layer"
        Service[Service<br/>@Service]
    end

    subgraph "Persistence Layer"
        Repository[Repository<br/>@Repository]
        Entity[Entity<br/>@Entity]
    end

    subgraph "Infrastructure"
        Config[Config<br/>Security/JWT]
        Exception[Exception Handler]
    end

    Controller --> Service
    Service --> Repository
    Repository --> Entity
    Config -.-> Controller
    Exception -.-> Controller

    style Controller fill:#e3f2fd
    style Service fill:#f3e5f5
    style Repository fill:#e8f5e9
    style Entity fill:#fff3e0
```

### 도메인 모듈 구조

```mermaid
graph TD
    subgraph "Domain Modules"
        User[User 도메인<br/>사용자 인증/관리]
        Product[Product 도메인<br/>상품 관리]
        Cart[Cart 도메인<br/>장바구니]
        Order[Order 도메인<br/>주문 처리]
        Payment[Payment 도메인<br/>결제 처리]
        Inventory[Inventory 도메인<br/>재고 관리]
        Review[Review 도메인<br/>리뷰 시스템]
    end

    subgraph "Global Layer"
        Common[Common<br/>공통 컴포넌트]
        Security[Security<br/>인증/인가]
        Exception[Exception<br/>예외 처리]
    end

    User -.-> Security
    Cart --> User
    Cart --> Product
    Order --> User
    Order --> Product
    Order --> Inventory
    Payment --> Order
    Review --> User
    Review --> Product

    style User fill:#e1f5ff
    style Product fill:#f3e5f5
    style Cart fill:#e8f5e9
    style Order fill:#fff4e1
    style Payment fill:#fce4ec
    style Inventory fill:#f1f8e9
    style Review fill:#ede7f6
```

## 🚀 시작하기

### 필수 요구사항
- Java 17+
- Docker & Docker Compose
- Gradle 8.x

### 로컬 실행

```bash
# 1. 저장소 클론
git clone https://github.com/dd-jiny/claude-code-test01.git
cd claude-code-study01/genious-api

# 2. PostgreSQL + Redis 실행 (Docker)
docker-compose up -d

# 3. 애플리케이션 실행
./gradlew bootRun

# 4. API 문서 확인
open http://localhost:8080/swagger-ui.html
```

### 테스트 실행

```bash
./gradlew test
```

### 빌드

```bash
./gradlew clean build
```

## 📊 개발 현황

### ✅ 완료
- [x] Spring Boot 프로젝트 초기 설정
- [x] 기본 패키지 구조 구성
- [x] BaseEntity, 공통 예외 처리
- [x] application.yml 환경별 프로파일 설정
- [x] 요구사항 분석 및 설계 문서화

### 🔄 진행 중 (Sprint 1 - 사용자 인증)
- [ ] JWT 인증 시스템
- [ ] Spring Security 설정
- [ ] User 엔티티 및 Repository
- [ ] 회원가입/로그인 API

### ⏳ 예정
- Sprint 2: 상품 도메인
- Sprint 3: 재고 도메인
- Sprint 4: 장바구니 & 위시리스트
- Sprint 5: 주문 도메인
- Sprint 6: 결제 도메인
- Sprint 7: 리뷰 도메인
- Sprint 8: 관리자 기능
- Sprint 9: 통합 테스트

## 📚 문서

- [CLAUDE.md](./genious-api/CLAUDE.md) - AI 어시스턴트용 프로젝트 상세 컨텍스트
- [전체_요구사항.md](./genious-api/전체_요구사항.md) - 프로젝트 요구사항 명세
- [전체_요구사항_분석.md](./genious-api/전체_요구사항_분석.md) - 요구사항 분석 및 다이어그램
- [전체_프로젝트 구조.md](./genious-api/전체_프로젝트%20구조.md) - 아키텍처 설계 문서

## 🔗 참고 링크

- [GitHub Repository](https://github.com/dd-jiny/claude-code-test01.git)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Security 문서](https://spring.io/projects/spring-security)

## 📝 라이선스

이 프로젝트는 학습 목적으로 만들어졌습니다.
