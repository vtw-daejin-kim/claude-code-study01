# 사용자 관리 기능 - 개발 계획서

**작성일**: 2026-02-09  
**상태**: 계획 확정  
**예상 소요 기간**: 약 6일

---

## 📋 개요

지니어스 E-commerce 시스템의 사용자 관리 기능을 개발합니다.
요구사항_분석.md와 사용자관리.md를 기반으로 MVP 범위를 결정하고 작업 계획을 수립했습니다.

---

## 🎯 MVP 범위 결정

### ✅ Phase 1 (MVP)에 포함

| 기능 | 설명 | 우선순위 |
|------|------|----------|
| 회원가입 | 이메일/비밀번호 기반 가입 | 🔴 필수 |
| 로그인 | JWT 기반 인증 (Access + Refresh Token) | 🔴 필수 |
| 로그아웃 | Token 무효화 | 🔴 필수 |
| 프로필 관리 | 기본 정보 조회/수정 | 🟡 중요 |
| 비밀번호 변경 | 현재 비밀번호 검증 후 변경 | 🟡 중요 |
| 배송지 관리 | 주소 CRUD + 기본 배송지 설정 | 🟡 중요 |

### ❌ Phase 2로 연기

| 기능 | 연기 이유 |
|------|----------|
| 이메일 인증 | 이메일 서비스 연동 필요, MVP 복잡도 증가 |
| 소셜 로그인 | OAuth 연동 별도 작업 필요 |
| 비밀번호 찾기 | 이메일 서비스 의존 |
| 로그인 실패 제한 | MVP에서 제외 결정 |
| 세션 관리 (다중 기기) | 복잡도 높음 |
| 회원 탈퇴 | 데이터 정책 결정 필요 |

---

## ⚙️ 기술 결정사항

### 1. JWT 인증 구조

```yaml
Access Token:
  - 유효 시간: 1시간
  - 저장 위치: 클라이언트 메모리/localStorage
  - 용도: API 요청 인증

Refresh Token:
  - 유효 시간: 14일
  - 저장 위치: Redis (서버측)
  - 용도: Access Token 갱신
```

### 2. 비밀번호 정책 (MVP)

- 최소 8자 이상
- 영문, 숫자 필수 포함
- 특수문자 선택 (권장)
- BCrypt 암호화

### 3. 이메일 중복 체크

- 회원가입 API 호출 시에만 체크
- 실시간 중복 체크 API는 제공하지 않음 (MVP)

### 4. Refresh Token 관리

- Redis에 저장 (key: `refresh:userId`, value: token)
- TTL 14일 자동 만료
- 로그아웃 시 Redis에서 삭제

---

## 📁 패키지 구조

```
com.genious.api
├── domain
│   └── user
│       ├── controller
│       │   ├── AuthController.java      # 인증 관련 API
│       │   ├── UserController.java      # 사용자 정보 API
│       │   └── AddressController.java   # 배송지 관리 API
│       ├── dto
│       │   ├── request
│       │   │   ├── SignUpRequest.java
│       │   │   ├── LoginRequest.java
│       │   │   ├── UpdateProfileRequest.java
│       │   │   ├── ChangePasswordRequest.java
│       │   │   └── AddressRequest.java
│       │   └── response
│       │       ├── UserResponse.java
│       │       ├── TokenResponse.java
│       │       └── AddressResponse.java
│       ├── entity
│       │   ├── User.java
│       │   ├── Address.java
│       │   └── Role.java
│       ├── repository
│       │   ├── UserRepository.java
│       │   └── AddressRepository.java
│       ├── service
│       │   ├── AuthService.java
│       │   ├── UserService.java
│       │   └── AddressService.java
│       └── exception
│           └── UserException.java
└── global
    ├── config
    │   ├── SecurityConfig.java
    │   └── RedisConfig.java
    ├── security
    │   ├── JwtTokenProvider.java
    │   ├── JwtAuthenticationFilter.java
    │   └── CustomUserDetailsService.java
    ├── common
    │   ├── BaseEntity.java
    │   └── ApiResponse.java
    └── exception
        ├── GlobalExceptionHandler.java
        └── ErrorCode.java
```

---

## 📊 API 명세

### 인증 API (AuthController)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| POST | `/api/auth/signup` | 회원가입 | ❌ |
| POST | `/api/auth/login` | 로그인 | ❌ |
| POST | `/api/auth/logout` | 로그아웃 | ✅ |
| POST | `/api/auth/refresh` | 토큰 갱신 | ❌ (Refresh Token) |

### 사용자 API (UserController)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| GET | `/api/users/me` | 내 정보 조회 | ✅ |
| PUT | `/api/users/me` | 내 정보 수정 | ✅ |
| PUT | `/api/users/me/password` | 비밀번호 변경 | ✅ |

### 배송지 API (AddressController)

| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| GET | `/api/users/me/addresses` | 배송지 목록 조회 | ✅ |
| POST | `/api/users/me/addresses` | 배송지 추가 | ✅ |
| PUT | `/api/users/me/addresses/{id}` | 배송지 수정 | ✅ |
| DELETE | `/api/users/me/addresses/{id}` | 배송지 삭제 | ✅ |
| PUT | `/api/users/me/addresses/{id}/default` | 기본 배송지 설정 | ✅ |

---

## 📝 작업 단계

### Step 1: 엔티티 및 Repository (예상: 1일)
- User 엔티티 생성
- Address 엔티티 생성
- Role enum 생성
- UserRepository 생성
- AddressRepository 생성

### Step 2: JWT + Redis 인증 설정 (예상: 1일)
- RedisConfig 설정
- JwtTokenProvider 구현
- JwtAuthenticationFilter 구현
- CustomUserDetailsService 구현
- SecurityConfig 설정

### Step 3: 회원가입 API (예상: 0.5일)
- SignUpRequest DTO 생성
- 유효성 검증 어노테이션 적용
- AuthService.signUp() 구현
- AuthController.signUp() 구현
- 이메일 중복 체크 로직
- BCrypt 비밀번호 암호화

### Step 4: 로그인/로그아웃 API (예상: 0.5일)
- LoginRequest DTO 생성
- TokenResponse DTO 생성
- AuthService.login() 구현
- AuthService.logout() 구현
- AuthService.refresh() 구현
- Redis Refresh Token 관리

### Step 5: 프로필/비밀번호 관리 API (예상: 0.5일)
- UpdateProfileRequest DTO 생성
- ChangePasswordRequest DTO 생성
- UserResponse DTO 생성
- UserService 구현
- UserController 구현

### Step 6: 배송지 관리 API (예상: 1일)
- AddressRequest DTO 생성
- AddressResponse DTO 생성
- AddressService CRUD 구현
- AddressController 구현
- 기본 배송지 설정 로직

### Step 7: 예외 처리 및 공통 모듈 (예상: 0.5일)
- ErrorCode enum 생성
- GlobalExceptionHandler 구현
- ApiResponse 공통 응답 객체
- UserException 커스텀 예외

### Step 8: 테스트 (예상: 1일)
- AuthService 단위 테스트
- UserService 단위 테스트
- AddressService 단위 테스트
- Controller 통합 테스트 (MockMvc)

---

## ⏱️ 예상 일정

| 단계 | 예상 시간 |
|------|----------|
| Step 1: 엔티티/Repository | 1일 |
| Step 2: JWT + Redis 인증 | 1일 |
| Step 3: 회원가입 API | 0.5일 |
| Step 4: 로그인/로그아웃 API | 0.5일 |
| Step 5: 프로필/비밀번호 API | 0.5일 |
| Step 6: 배송지 관리 API | 1일 |
| Step 7: 예외 처리 | 0.5일 |
| Step 8: 테스트 | 1일 |
| **총계** | **약 6일** |

---

## 🔗 관련 문서

- [요구사항_분석.md](전체_요구사항_분석.md)
- [사용자관리.md](사용자관리_요구사항.md)
- [TASK.md](./TASK.md)

---

**작성자**: Cline AI Assistant  
**최종 수정일**: 2026-02-09
