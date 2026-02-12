# 사용자 관리 기능 - 상세 TASK

**작성일**: 2026-02-09  
**관련 계획서**: [User_Plan.md](./User_Plan.md)  
**상태**: 🔄 진행 예정

---

## 📋 TASK 개요

사용자 관리 기능의 백엔드 API를 개발합니다. 각 Step은 독립적으로 완료 가능하며, 순서대로 진행합니다.

---

## ✅ Step 1: 엔티티 및 Repository

### 📌 목표
데이터베이스 테이블과 매핑되는 JPA 엔티티 및 Repository 인터페이스 생성

### 📝 세부 작업

#### 1.1 Role Enum 생성
**파일**: `domain/user/entity/Role.java`
```java
public enum Role {
    CUSTOMER,  // 일반 사용자
    ADMIN      // 관리자
}
```
- [ ] Role enum 생성
- [ ] description 필드 추가

#### 1.2 User 엔티티 생성
**파일**: `domain/user/entity/User.java`

| 필드 | 타입 | 제약조건 | 설명 |
|------|------|----------|------|
| id | Long | PK, AUTO_INCREMENT | 사용자 ID |
| email | String | UNIQUE, NOT NULL | 이메일 (로그인 ID) |
| password | String | NOT NULL | 암호화된 비밀번호 |
| name | String | NOT NULL | 이름 |
| phone | String | | 전화번호 |
| role | Role | NOT NULL, DEFAULT CUSTOMER | 권한 |
| isActive | boolean | DEFAULT true | 활성화 상태 |
| createdAt | LocalDateTime | NOT NULL | 생성일시 |
| updatedAt | LocalDateTime | | 수정일시 |

- [ ] User 엔티티 생성
- [ ] BaseEntity 상속 (createdAt, updatedAt)
- [ ] @Builder 패턴 적용
- [ ] 비즈니스 메서드 구현
  - [ ] updateProfile(name, phone)
  - [ ] changePassword(newPassword)
  - [ ] deactivate()

#### 1.3 Address 엔티티 생성
**파일**: `domain/user/entity/Address.java`

| 필드 | 타입 | 제약조건 | 설명 |
|------|------|----------|------|
| id | Long | PK, AUTO_INCREMENT | 주소 ID |
| user | User | FK, NOT NULL | 사용자 |
| recipient | String | NOT NULL | 수령인 |
| phone | String | NOT NULL | 연락처 |
| zipCode | String | NOT NULL | 우편번호 |
| address | String | NOT NULL | 기본주소 |
| detailAddress | String | | 상세주소 |
| isDefault | boolean | DEFAULT false | 기본배송지 여부 |
| createdAt | LocalDateTime | NOT NULL | 생성일시 |
| updatedAt | LocalDateTime | | 수정일시 |

- [ ] Address 엔티티 생성
- [ ] User와 ManyToOne 관계 설정
- [ ] 비즈니스 메서드 구현
  - [ ] update(recipient, phone, zipCode, address, detailAddress)
  - [ ] setAsDefault()
  - [ ] unsetDefault()

#### 1.4 Repository 생성
**파일**: `domain/user/repository/UserRepository.java`
- [ ] UserRepository 인터페이스 생성
- [ ] 메서드 정의
  - [ ] `Optional<User> findByEmail(String email)`
  - [ ] `boolean existsByEmail(String email)`

**파일**: `domain/user/repository/AddressRepository.java`
- [ ] AddressRepository 인터페이스 생성
- [ ] 메서드 정의
  - [ ] `List<Address> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId)`
  - [ ] `Optional<Address> findByIdAndUserId(Long id, Long userId)`
  - [ ] `Optional<Address> findByUserIdAndIsDefaultTrue(Long userId)`

### ✅ 완료 기준
- [ ] 모든 엔티티 컴파일 성공
- [ ] 애플리케이션 실행 시 테이블 자동 생성 확인

---

## ✅ Step 2: JWT + Redis 인증 설정

### 📌 목표
JWT 토큰 기반 인증 시스템 및 Redis를 이용한 Refresh Token 관리 구현

### 📝 세부 작업

#### 2.1 Redis 설정
**파일**: `global/config/RedisConfig.java`
- [ ] RedisTemplate 빈 설정
- [ ] StringRedisTemplate 빈 설정
- [ ] Redis 연결 확인

#### 2.2 JWT Token Provider 구현
**파일**: `global/security/JwtTokenProvider.java`

| 메서드 | 설명 |
|--------|------|
| createAccessToken(userId, role) | Access Token 생성 (1시간) |
| createRefreshToken(userId) | Refresh Token 생성 (14일) |
| validateToken(token) | 토큰 유효성 검증 |
| getUserIdFromToken(token) | 토큰에서 사용자 ID 추출 |
| getRoleFromToken(token) | 토큰에서 권한 추출 |

- [ ] JwtTokenProvider 클래스 생성
- [ ] 시크릿 키 설정 (application.yml)
- [ ] Access Token 생성 메서드
- [ ] Refresh Token 생성 메서드
- [ ] 토큰 검증 메서드
- [ ] 토큰 파싱 메서드

#### 2.3 Refresh Token 관리 서비스
**파일**: `global/security/RefreshTokenService.java`
- [ ] saveRefreshToken(userId, token) - Redis 저장 (14일 TTL)
- [ ] getRefreshToken(userId) - Redis 조회
- [ ] deleteRefreshToken(userId) - Redis 삭제
- [ ] validateRefreshToken(userId, token) - 검증

#### 2.4 JWT 인증 필터 구현
**파일**: `global/security/JwtAuthenticationFilter.java`
- [ ] OncePerRequestFilter 상속
- [ ] Authorization 헤더에서 토큰 추출
- [ ] 토큰 검증 및 SecurityContext 설정
- [ ] 예외 처리 (만료, 유효하지 않은 토큰)

#### 2.5 UserDetailsService 구현
**파일**: `global/security/CustomUserDetailsService.java`
- [ ] UserDetailsService 구현
- [ ] loadUserByUsername(email) 구현
- [ ] UserDetails 객체 생성

#### 2.6 Security 설정
**파일**: `global/config/SecurityConfig.java`
- [ ] SecurityFilterChain 빈 설정
- [ ] 인증 제외 경로 설정 (/api/auth/signup, /api/auth/login, /api/auth/refresh)
- [ ] JWT 필터 등록
- [ ] CORS 설정
- [ ] CSRF 비활성화 (REST API)
- [ ] PasswordEncoder 빈 (BCrypt)

### ✅ 완료 기준
- [ ] Redis 연결 성공
- [ ] JWT 토큰 생성/검증 테스트 통과
- [ ] 인증이 필요한 API 접근 시 401 응답 확인

---

## ✅ Step 3: 회원가입 API

### 📌 목표
이메일/비밀번호 기반 회원가입 API 구현

### 📝 세부 작업

#### 3.1 Request DTO 생성
**파일**: `domain/user/dto/request/SignUpRequest.java`

| 필드 | 검증 규칙 |
|------|----------|
| email | @Email, @NotBlank |
| password | @NotBlank, @Size(min=8), @Pattern(영문+숫자) |
| name | @NotBlank, @Size(max=50) |
| phone | @Pattern(한국 전화번호 형식) |

- [ ] SignUpRequest DTO 생성
- [ ] Jakarta Validation 어노테이션 적용
- [ ] 비밀번호 검증 커스텀 어노테이션 (선택)

#### 3.2 AuthService 구현
**파일**: `domain/user/service/AuthService.java`

```java
@Transactional
public UserResponse signUp(SignUpRequest request) {
    // 1. 이메일 중복 체크
    // 2. 비밀번호 암호화
    // 3. User 엔티티 생성 및 저장
    // 4. UserResponse 반환
}
```

- [ ] AuthService 클래스 생성
- [ ] signUp() 메서드 구현
  - [ ] 이메일 중복 체크 로직
  - [ ] PasswordEncoder를 이용한 비밀번호 암호화
  - [ ] User 엔티티 생성 (Role.CUSTOMER)
  - [ ] UserResponse DTO 변환

#### 3.3 Response DTO 생성
**파일**: `domain/user/dto/response/UserResponse.java`

| 필드 | 설명 |
|------|------|
| id | 사용자 ID |
| email | 이메일 |
| name | 이름 |
| phone | 전화번호 |
| role | 권한 |
| createdAt | 가입일 |

- [ ] UserResponse DTO 생성
- [ ] User 엔티티 → DTO 변환 메서드

#### 3.4 AuthController 구현
**파일**: `domain/user/controller/AuthController.java`

```
POST /api/auth/signup
Request Body: SignUpRequest
Response: ApiResponse<UserResponse>
```

- [ ] AuthController 클래스 생성
- [ ] @RestController, @RequestMapping("/api/auth") 설정
- [ ] signUp() 엔드포인트 구현
- [ ] @Valid 적용

### ✅ 완료 기준
- [ ] 회원가입 API 호출 성공 (201 Created)
- [ ] 이메일 중복 시 409 Conflict 응답
- [ ] 유효성 검증 실패 시 400 Bad Request 응답
- [ ] DB에 사용자 저장 확인

---

## ✅ Step 4: 로그인/로그아웃 API

### 📌 목표
JWT 기반 로그인 및 토큰 관리 API 구현

### 📝 세부 작업

#### 4.1 Request/Response DTO 생성
**파일**: `domain/user/dto/request/LoginRequest.java`
- [ ] email: @NotBlank, @Email
- [ ] password: @NotBlank

**파일**: `domain/user/dto/response/TokenResponse.java`
- [ ] accessToken: String
- [ ] refreshToken: String
- [ ] tokenType: "Bearer"
- [ ] expiresIn: Long (초 단위)

#### 4.2 AuthService 로그인 구현
```java
@Transactional(readOnly = true)
public TokenResponse login(LoginRequest request) {
    // 1. 이메일로 사용자 조회
    // 2. 비밀번호 검증
    // 3. Access Token 생성
    // 4. Refresh Token 생성 및 Redis 저장
    // 5. TokenResponse 반환
}
```

- [ ] login() 메서드 구현
  - [ ] 사용자 조회 (없으면 예외)
  - [ ] 비밀번호 검증 (불일치 시 예외)
  - [ ] 비활성화 계정 체크
  - [ ] 토큰 생성
  - [ ] Redis에 Refresh Token 저장

#### 4.3 AuthService 로그아웃 구현
```java
public void logout(Long userId) {
    // 1. Redis에서 Refresh Token 삭제
}
```

- [ ] logout() 메서드 구현
- [ ] Redis에서 토큰 삭제

#### 4.4 AuthService 토큰 갱신 구현
```java
public TokenResponse refresh(String refreshToken) {
    // 1. Refresh Token 유효성 검증
    // 2. Redis 저장된 토큰과 비교
    // 3. 새로운 Access Token 발급
    // 4. (선택) 새로운 Refresh Token 발급 (Rotation)
}
```

- [ ] refresh() 메서드 구현
  - [ ] 토큰 검증
  - [ ] Redis 조회 및 비교
  - [ ] 새 Access Token 발급

#### 4.5 AuthController 구현

| 엔드포인트 | 설명 |
|-----------|------|
| POST /api/auth/login | 로그인 |
| POST /api/auth/logout | 로그아웃 |
| POST /api/auth/refresh | 토큰 갱신 |

- [ ] login() 엔드포인트
- [ ] logout() 엔드포인트 (@AuthenticationPrincipal 사용)
- [ ] refresh() 엔드포인트

### ✅ 완료 기준
- [ ] 로그인 성공 시 Access/Refresh Token 반환
- [ ] 잘못된 자격 증명 시 401 Unauthorized
- [ ] 로그아웃 후 동일 Refresh Token으로 갱신 시 실패
- [ ] Refresh Token으로 새 Access Token 발급 성공

---

## ✅ Step 5: 프로필/비밀번호 관리 API

### 📌 목표
로그인한 사용자의 정보 조회/수정 및 비밀번호 변경 API 구현

### 📝 세부 작업

#### 5.1 Request DTO 생성
**파일**: `domain/user/dto/request/UpdateProfileRequest.java`
- [ ] name: @NotBlank, @Size(max=50)
- [ ] phone: @Pattern(한국 전화번호)

**파일**: `domain/user/dto/request/ChangePasswordRequest.java`
- [ ] currentPassword: @NotBlank
- [ ] newPassword: @NotBlank, @Size(min=8), @Pattern(영문+숫자)
- [ ] confirmPassword: @NotBlank (newPassword와 일치 검증)

#### 5.2 UserService 구현
**파일**: `domain/user/service/UserService.java`

```java
@Transactional(readOnly = true)
public UserResponse getMyProfile(Long userId) {
    // 사용자 조회 및 응답 반환
}

@Transactional
public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
    // 사용자 조회
    // 정보 업데이트
    // 응답 반환
}

@Transactional
public void changePassword(Long userId, ChangePasswordRequest request) {
    // 사용자 조회
    // 현재 비밀번호 검증
    // 새 비밀번호 암호화 및 저장
}
```

- [ ] UserService 클래스 생성
- [ ] getMyProfile() 구현
- [ ] updateProfile() 구현
- [ ] changePassword() 구현
  - [ ] 현재 비밀번호 검증
  - [ ] 새 비밀번호 / 확인 비밀번호 일치 검증
  - [ ] BCrypt 암호화

#### 5.3 UserController 구현
**파일**: `domain/user/controller/UserController.java`

| 엔드포인트 | 설명 |
|-----------|------|
| GET /api/users/me | 내 정보 조회 |
| PUT /api/users/me | 내 정보 수정 |
| PUT /api/users/me/password | 비밀번호 변경 |

- [ ] UserController 클래스 생성
- [ ] @AuthenticationPrincipal로 현재 사용자 ID 추출
- [ ] 각 엔드포인트 구현

### ✅ 완료 기준
- [ ] 인증된 사용자만 접근 가능
- [ ] 프로필 조회/수정 성공
- [ ] 현재 비밀번호 불일치 시 400 Bad Request
- [ ] 비밀번호 변경 성공

---

## ✅ Step 6: 배송지 관리 API

### 📌 목표
사용자의 배송지 CRUD 및 기본 배송지 설정 API 구현

### 📝 세부 작업

#### 6.1 Request/Response DTO 생성
**파일**: `domain/user/dto/request/AddressRequest.java`
- [ ] recipient: @NotBlank
- [ ] phone: @NotBlank, @Pattern
- [ ] zipCode: @NotBlank
- [ ] address: @NotBlank
- [ ] detailAddress: (선택)
- [ ] isDefault: boolean

**파일**: `domain/user/dto/response/AddressResponse.java`
- [ ] id, recipient, phone, zipCode, address, detailAddress, isDefault, createdAt

#### 6.2 AddressService 구현
**파일**: `domain/user/service/AddressService.java`

| 메서드 | 설명 |
|--------|------|
| getAddresses(userId) | 배송지 목록 조회 |
| addAddress(userId, request) | 배송지 추가 |
| updateAddress(userId, addressId, request) | 배송지 수정 |
| deleteAddress(userId, addressId) | 배송지 삭제 |
| setDefaultAddress(userId, addressId) | 기본 배송지 설정 |

- [ ] AddressService 클래스 생성
- [ ] getAddresses() 구현 (기본 배송지 먼저, 최신순 정렬)
- [ ] addAddress() 구현
  - [ ] 첫 번째 주소면 자동으로 기본 배송지 설정
  - [ ] isDefault=true면 기존 기본 배송지 해제
- [ ] updateAddress() 구현
  - [ ] 본인 주소만 수정 가능
- [ ] deleteAddress() 구현
  - [ ] 본인 주소만 삭제 가능
  - [ ] 기본 배송지 삭제 시 처리 (다음 주소를 기본으로?)
- [ ] setDefaultAddress() 구현
  - [ ] 기존 기본 배송지 해제
  - [ ] 새 기본 배송지 설정

#### 6.3 AddressController 구현
**파일**: `domain/user/controller/AddressController.java`

| 엔드포인트 | 설명 |
|-----------|------|
| GET /api/users/me/addresses | 목록 조회 |
| POST /api/users/me/addresses | 추가 |
| PUT /api/users/me/addresses/{id} | 수정 |
| DELETE /api/users/me/addresses/{id} | 삭제 |
| PUT /api/users/me/addresses/{id}/default | 기본 설정 |

- [ ] AddressController 클래스 생성
- [ ] 각 엔드포인트 구현

### ✅ 완료 기준
- [ ] 배송지 CRUD 동작 확인
- [ ] 기본 배송지 설정 시 기존 기본 배송지 해제 확인
- [ ] 다른 사용자의 배송지 접근 시 403 Forbidden

---

## ✅ Step 7: 예외 처리 및 공통 모듈

### 📌 목표
일관된 API 응답 형식 및 예외 처리 구현

### 📝 세부 작업

#### 7.1 공통 응답 객체
**파일**: `global/common/ApiResponse.java`

```java
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> success(T data) { ... }
    public static <T> ApiResponse<T> success(String message, T data) { ... }
    public static ApiResponse<?> error(String message) { ... }
}
```

- [ ] ApiResponse 클래스 생성
- [ ] 정적 팩토리 메서드 구현

#### 7.2 에러 코드 정의
**파일**: `global/exception/ErrorCode.java`

| 코드 | HTTP 상태 | 메시지 |
|------|----------|--------|
| USER_NOT_FOUND | 404 | 사용자를 찾을 수 없습니다 |
| EMAIL_DUPLICATED | 409 | 이미 사용 중인 이메일입니다 |
| INVALID_PASSWORD | 400 | 비밀번호가 일치하지 않습니다 |
| INVALID_TOKEN | 401 | 유효하지 않은 토큰입니다 |
| EXPIRED_TOKEN | 401 | 만료된 토큰입니다 |
| ADDRESS_NOT_FOUND | 404 | 배송지를 찾을 수 없습니다 |
| ACCESS_DENIED | 403 | 접근 권한이 없습니다 |

- [ ] ErrorCode enum 생성
- [ ] HTTP 상태 코드 및 메시지 정의

#### 7.3 커스텀 예외
**파일**: `domain/user/exception/UserException.java`
**파일**: `global/exception/BusinessException.java`

- [ ] BusinessException 추상 클래스 생성
- [ ] UserException 생성 (ErrorCode 포함)

#### 7.4 전역 예외 핸들러
**파일**: `global/exception/GlobalExceptionHandler.java`

- [ ] @RestControllerAdvice 적용
- [ ] BusinessException 핸들러
- [ ] MethodArgumentNotValidException 핸들러 (유효성 검증)
- [ ] 기타 예외 핸들러 (500 Internal Server Error)

### ✅ 완료 기준
- [ ] 모든 API 응답이 ApiResponse 형식
- [ ] 예외 발생 시 적절한 HTTP 상태 코드 및 메시지 반환

---

## ✅ Step 8: 테스트

### 📌 목표
단위 테스트 및 통합 테스트를 통한 기능 검증

### 📝 세부 작업

#### 8.1 단위 테스트
**파일**: `domain/user/service/AuthServiceTest.java`
- [ ] 회원가입 성공 테스트
- [ ] 이메일 중복 시 예외 테스트
- [ ] 로그인 성공 테스트
- [ ] 잘못된 자격 증명 시 예외 테스트

**파일**: `domain/user/service/UserServiceTest.java`
- [ ] 프로필 조회 테스트
- [ ] 프로필 수정 테스트
- [ ] 비밀번호 변경 테스트
- [ ] 현재 비밀번호 불일치 시 예외 테스트

**파일**: `domain/user/service/AddressServiceTest.java`
- [ ] 배송지 CRUD 테스트
- [ ] 기본 배송지 설정 테스트

#### 8.2 통합 테스트
**파일**: `domain/user/controller/AuthControllerTest.java`
- [ ] 회원가입 API 테스트 (MockMvc)
- [ ] 로그인 API 테스트
- [ ] 토큰 갱신 API 테스트

**파일**: `domain/user/controller/UserControllerTest.java`
- [ ] 프로필 조회/수정 API 테스트
- [ ] 비밀번호 변경 API 테스트
- [ ] 인증 없이 접근 시 401 테스트

**파일**: `domain/user/controller/AddressControllerTest.java`
- [ ] 배송지 CRUD API 테스트

### ✅ 완료 기준
- [ ] 모든 단위 테스트 통과
- [ ] 모든 통합 테스트 통과
- [ ] 테스트 커버리지 80% 이상

---

## 📊 진행 상황 체크리스트

| Step | 작업 | 상태 | 완료일 |
|------|------|------|--------|
| 1 | 엔티티 및 Repository | ⬜ 미시작 | |
| 2 | JWT + Redis 인증 설정 | ⬜ 미시작 | |
| 3 | 회원가입 API | ⬜ 미시작 | |
| 4 | 로그인/로그아웃 API | ⬜ 미시작 | |
| 5 | 프로필/비밀번호 관리 API | ⬜ 미시작 | |
| 6 | 배송지 관리 API | ⬜ 미시작 | |
| 7 | 예외 처리 및 공통 모듈 | ⬜ 미시작 | |
| 8 | 테스트 | ⬜ 미시작 | |

**상태 범례**: ⬜ 미시작 | 🔄 진행중 | ✅ 완료 | ❌ 차단됨

---

## 🔗 관련 문서

- [User_Plan.md](./User_Plan.md) - 개발 계획서
- [요구사항_분석.md](전체_요구사항_분석.md) - 전체 요구사항 분석
- [사용자관리.md](사용자관리_요구사항.md) - 상세 요구사항

---

**작성자**: Cline AI Assistant  
**최종 수정일**: 2026-02-09
