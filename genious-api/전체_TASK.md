# 지니어스 E-commerce 시스템 - 개발 작업 계획

**작성일**: 2026-02-09  
**분석 방법론**: requirements-analysis SKILL 적용  
**상태**: 설계 단계

---

## 📋 목차
1. [확정된 설계 방향](#확정된-설계-방향)
2. [Phase 1: MVP 개발 계획](#phase-1-mvp-개발-계획)
3. [Phase 2: 확장 기능](#phase-2-확장-기능)
4. [핵심 기술 결정사항](#핵심-기술-결정사항)
5. [개발 우선순위](#개발-우선순위)

---

## 확정된 설계 방향

### ✅ 재고 차감 시점
**결정**: **결제 완료 시 차감** + 주문 생성 시 "소프트 예약" (5-10분 타임아웃)

**근거**:
- 실제 판매 시점에 차감하여 재고 정확도 향상
- 결제 실패 시 별도 복구 로직 불필요
- 주문 생성 시 소프트 예약으로 동시 주문 경합 완화

**구현 방안**:
1. 주문 생성 시: `InventoryReservation` 엔티티에 임시 예약 기록 (10분 TTL)
2. 결제 완료 시: 실제 재고 차감 + 예약 레코드 삭제
3. 결제 실패/타임아웃 시: 예약 레코드 자동 만료

---

### ✅ 주문-결제 책임 분리
**결정**: **Service 계층 순차 호출** (MVP), 향후 이벤트 기반 전환

**근거**:
- MVP에서는 구현 복잡도를 낮추고 빠른 출시 우선
- @Transactional로 트랜잭션 관리 단순화
- Phase 2에서 Saga 패턴으로 전환 가능한 구조 유지

**구현 방안**:
```java
@Transactional
public OrderResponse createOrder(OrderRequest request) {
    // 1. 주문 생성
    Order order = orderService.createOrder(request);
    
    // 2. 재고 예약
    inventoryService.reserveInventory(order);
    
    // 3. 결제 처리
    Payment payment = paymentService.processPayment(order);
    
    // 4. 주문 확정
    if (payment.isSuccess()) {
        inventoryService.deductInventory(order);
        order.confirm();
    } else {
        order.cancel();
    }
    
    return toResponse(order);
}
```

---

### ✅ 장바구니 저장
**결정**: **하이브리드** 방식 (비로그인: localStorage, 로그인: DB 동기화)

**근거**:
- 비로그인 사용자도 장바구니 사용 가능 (전환율 향상)
- 로그인 시 디바이스 간 동기화 제공
- 서버 부하 최소화

**구현 방안**:
1. **프론트엔드**: 
   - 비로그인: localStorage에 장바구니 저장
   - 로그인 시: localStorage → API 전송 → DB 동기화
   
2. **백엔드**:
   - `Cart` 엔티티는 User와 1:1 관계
   - 로그인 시 기존 DB 장바구니 + localStorage 병합 API 제공

---

## Phase 1: MVP 개발 계획

### Sprint 1: 인프라 및 기본 설정 (1주)
- [ ] 프로젝트 초기 설정
  - [x] Spring Boot 프로젝트 생성 (Gradle)
  - [ ] React 프로젝트 생성 (Vite)
  - [ ] PostgreSQL Docker Compose 설정
  - [ ] Redis 설정 (세션 관리용)
  
- [ ] 기본 인증 시스템
  - [ ] JWT 설정 (JwtTokenProvider)
  - [ ] Spring Security 설정
  - [ ] User 엔티티 및 Repository
  - [ ] 회원가입/로그인 API

- [ ] 프론트엔드 기본 구조
  - [ ] React Router 설정
  - [ ] Redux Toolkit 설정
  - [ ] Axios 설정 (인터셉터)
  - [ ] 로그인/회원가입 페이지

**완료 기준**: 회원가입 후 로그인하여 JWT 토큰 발급 확인

---

### Sprint 2: 상품 관리 (1.5주)
- [ ] 백엔드 - 상품 도메인
  - [ ] Product 엔티티 설계
  - [ ] Category 엔티티 설계
  - [ ] ProductImage 엔티티 (1:N)
  - [ ] ProductOption 엔티티 (색상, 사이즈)
  - [ ] ProductRepository (QueryDSL 적용)
  
- [ ] 백엔드 - 상품 API
  - [ ] 상품 목록 조회 (페이징, 정렬, 필터)
  - [ ] 상품 상세 조회
  - [ ] 상품 검색 (상품명, 설명)
  - [ ] 관리자: 상품 등록/수정/삭제
  - [ ] 이미지 업로드 (파일 시스템 or S3)

- [ ] 프론트엔드 - 상품 페이지
  - [ ] 상품 목록 페이지
  - [ ] 상품 상세 페이지
  - [ ] 검색/필터/정렬 기능
  - [ ] 관리자: 상품 관리 페이지

**완료 기준**: 상품 목록 조회, 검색, 상세보기 동작 확인

---

### Sprint 3: 재고 관리 (1주)
- [ ] 백엔드 - 재고 도메인
  - [ ] Inventory 엔티티
  - [ ] InventoryReservation 엔티티 (소프트 예약)
  - [ ] InventoryService
    - [ ] 재고 조회
    - [ ] 재고 예약 (10분 TTL)
    - [ ] 재고 차감
    - [ ] 재고 복구
  
- [ ] 백엔드 - 재고 스케줄러
  - [ ] 만료된 예약 정리 스케줄러
  - [ ] 재고 부족 알림 (관리자)

- [ ] 프론트엔드 - 재고 표시
  - [ ] 상품 상세 페이지에 재고 수량 표시
  - [ ] 품절 상태 UI
  - [ ] 관리자: 재고 관리 대시보드

**완료 기준**: 재고 예약 → 10분 후 자동 해제 확인

---

### Sprint 4: 장바구니 & 위시리스트 (1주)
- [ ] 백엔드 - 장바구니
  - [ ] Cart 엔티티
  - [ ] CartItem 엔티티
  - [ ] CartService
    - [ ] 상품 추가
    - [ ] 수량 변경
    - [ ] 상품 삭제
    - [ ] localStorage 동기화 API
  
- [ ] 백엔드 - 위시리스트
  - [ ] Wishlist 엔티티
  - [ ] WishlistService

- [ ] 프론트엔드
  - [ ] 장바구니 페이지
  - [ ] 위시리스트 페이지
  - [ ] localStorage 장바구니 구현
  - [ ] 로그인 시 동기화 로직

**완료 기준**: 비로그인 장바구니 → 로그인 → DB 동기화 확인

---

### Sprint 5: 주문 시스템 (2주)
- [ ] 백엔드 - 주문 도메인
  - [ ] Order 엔티티
  - [ ] OrderItem 엔티티
  - [ ] OrderStatus enum
  - [ ] OrderService
    - [ ] 주문 생성
    - [ ] 주문 조회
    - [ ] 주문 취소
  
- [ ] 백엔드 - 주문 상태 관리
  - [ ] 주문 상태 전이 로직
  - [ ] 주문 취소 시 재고 복구
  - [ ] 주문 히스토리 기록

- [ ] 프론트엔드
  - [ ] 주문 생성 페이지
  - [ ] 주문 내역 페이지
  - [ ] 주문 상세 페이지
  - [ ] 주문 취소 기능

**완료 기준**: 장바구니 → 주문 생성 → 주문 내역 확인

---

### Sprint 6: 결제 시스템 (2주)
- [ ] 백엔드 - 결제 도메인
  - [ ] Payment 엔티티
  - [ ] PaymentMethod enum
  - [ ] PaymentStatus enum
  - [ ] PaymentService
    - [ ] 결제 처리 (Mock)
    - [ ] 결제 취소/환불
  
- [ ] 백엔드 - 주문-결제 연동
  - [ ] OrderPaymentFacade (트랜잭션 관리)
  - [ ] 결제 성공 시 재고 차감
  - [ ] 결제 실패 시 주문 취소

- [ ] 프론트엔드
  - [ ] 결제 페이지
  - [ ] 결제 방법 선택
  - [ ] 결제 완료 페이지
  - [ ] 결제 실패 처리

**완료 기준**: Mock 결제 성공 → 재고 차감 → 주문 확정 확인

---

### Sprint 7: 리뷰 시스템 (1주)
- [ ] 백엔드
  - [ ] Review 엔티티
  - [ ] ReviewImage 엔티티
  - [ ] ReviewService
    - [ ] 리뷰 작성 (구매 확정 후)
    - [ ] 리뷰 수정/삭제
    - [ ] 리뷰 조회

- [ ] 프론트엔드
  - [ ] 상품 상세 페이지에 리뷰 표시
  - [ ] 리뷰 작성 페이지
  - [ ] 마이페이지에서 내 리뷰 관리

**완료 기준**: 구매 확정 후 리뷰 작성 → 상품 페이지에 표시

---

### Sprint 8: 관리자 기능 (1.5주)
- [ ] 백엔드
  - [ ] 관리자 권한 체크
  - [ ] 대시보드 통계 API
    - [ ] 매출 통계
    - [ ] 주문 수
    - [ ] 회원 수
  - [ ] 주문 관리 API
  - [ ] 회원 관리 API

- [ ] 프론트엔드
  - [ ] 관리자 대시보드
  - [ ] 주문 관리 페이지
  - [ ] 회원 관리 페이지
  - [ ] 상품 관리 페이지 (Sprint 2 연계)

**완료 기준**: 관리자로 로그인 → 대시보드 통계 확인

---

### Sprint 9: 테스트 & 버그 수정 (1주)
- [ ] 백엔드 테스트
  - [ ] 단위 테스트 (Service 계층)
  - [ ] 통합 테스트 (API)
  - [ ] 동시성 테스트 (재고 차감)

- [ ] 프론트엔드 테스트
  - [ ] 컴포넌트 테스트
  - [ ] E2E 테스트 (Playwright)

- [ ] 버그 수정 및 리팩토링
  - [ ] 코드 리뷰
  - [ ] 성능 최적화
  - [ ] 보안 점검

**완료 기준**: 모든 핵심 기능 정상 동작 확인

---

## Phase 2: 확장 기능

### 고려 사항 (우선순위별)

#### 우선순위 1: 실제 결제 연동
- [ ] 토스페이먼츠 or 카카오페이 연동
- [ ] PG 웹훅 처리
- [ ] 결제 실패 재시도 로직
- [ ] 부분 취소/환불 지원

#### 우선순위 2: 배송 시스템
- [ ] 배송지 관리
- [ ] 배송 추적 (외부 API 연동)
- [ ] 배송 상태 업데이트

#### 우선순위 3: 쿠폰/할인 시스템
- [ ] Coupon 엔티티
- [ ] 할인 정책 엔진
- [ ] 쿠폰 발급/사용/만료

#### 우선순위 4: 포인트 시스템
- [ ] Point 엔티티
- [ ] 포인트 적립/사용
- [ ] 포인트 히스토리

#### 우선순위 5: 소셜 로그인
- [ ] OAuth 2.0 (Google, Kakao, Naver)
- [ ] 소셜 계정 연동

#### 우선순위 6: 실시간 알림
- [ ] WebSocket 설정
- [ ] 주문 상태 변경 알림
- [ ] 재입고 알림

#### 우선순위 7: 검색 고도화
- [ ] Elasticsearch 연동
- [ ] 검색어 자동완성
- [ ] 연관 검색어

#### 우선순위 8: 추천 시스템
- [ ] 협업 필터링
- [ ] 상품 추천 알고리즘
- [ ] 개인화 추천

---

## 핵심 기술 결정사항

### 백엔드
```yaml
프레임워크: Spring Boot 3.2.2
언어: Java 17
빌드 도구: Gradle 8.5
데이터베이스: PostgreSQL 16.1
캐시: Redis 7.2
인증: JWT (jjwt 0.12.3)
ORM: JPA/Hibernate
쿼리: QueryDSL 5.0.0
문서화: SpringDoc OpenAPI 2.3.0
테스트: JUnit 5, MockMVC
```

### 프론트엔드
```yaml
프레임워크: React 18.2
빌드 도구: Vite
상태 관리: Redux Toolkit
라우팅: React Router 6
HTTP 클라이언트: Axios
UI 라이브러리: Material-UI 5
폼 관리: Formik + Yup
테스트: Vitest, React Testing Library
```

### 인프라
```yaml
컨테이너: Docker
오케스트레이션: Docker Compose (개발), Kubernetes (프로덕션 고려)
CI/CD: GitHub Actions
모니터링: Prometheus + Grafana (Phase 2)
로깅: ELK Stack (Phase 2)
```

---

## 개발 우선순위

### 🔥 Must Have (MVP 필수)
1. ✅ 회원가입/로그인 (JWT 인증)
2. ✅ 상품 목록/상세 조회
3. ✅ 장바구니
4. ✅ 주문 생성
5. ✅ 결제 (Mock)
6. ✅ 재고 관리
7. ✅ 기본 관리자 기능

### 🎯 Should Have (MVP 후 우선)
1. ⏳ 실제 결제 연동
2. ⏳ 배송 시스템
3. ⏳ 리뷰 시스템
4. ⏳ 위시리스트
5. ⏳ 고급 검색/필터

### 💡 Could Have (추후 고려)
1. ⏳ 쿠폰/할인
2. ⏳ 포인트 시스템
3. ⏳ 소셜 로그인
4. ⏳ 실시간 알림
5. ⏳ 추천 시스템

### 🌟 Won't Have (당분간 제외)
1. ❌ AI 챗봇
2. ❌ AR 상품 미리보기
3. ❌ 음성 주문
4. ❌ 블록체인 결제

---

## 예상 일정

### Phase 1: MVP 개발
- **기간**: 약 3개월 (12주)
- **인력**: 백엔드 2명, 프론트엔드 2명 가정
- **마일스톤**:
  - Week 4: 인증 + 상품 조회 완료
  - Week 8: 장바구니 + 주문 + 결제 완료
  - Week 12: 리뷰 + 관리자 + 테스트 완료

### Phase 2: 확장 기능
- **기간**: 약 2-3개월
- **우선순위에 따라 순차 개발**

---

## 리스크 관리

### 🚨 고위험 (즉시 대응 필요)

#### 리스크 1: 동시성 문제 (재고 차감)
**문제**: 마지막 재고 1개를 여러 사용자가 동시 주문
**해결책**:
- Phase 1: 비관적 락 (Pessimistic Lock) 사용
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Inventory> findByProductIdForUpdate(Long productId);
```
- Phase 2: Redis 분산 락 도입

#### 리스크 2: 결제-재고 불일치
**문제**: 결제 성공 후 재고 차감 실패
**해결책**:
- @Transactional로 원자성 보장
- Phase 2에서 Saga 패턴 + 보상 트랜잭션

#### 리스크 3: 결제 타임아웃
**문제**: PG 응답 지연 시 주문 상태 불명확
**해결책**:
- PG Webhook으로 비동기 처리
- 주기적인 결제 상태 조회 스케줄러

### ⚠️ 중위험 (모니터링 필요)

#### 리스크 4: 데이터베이스 성능
**문제**: 트래픽 증가 시 DB 병목
**해결책**:
- 인덱스 최적화 (복합 인덱스)
- Redis 캐싱 (상품 조회, 장바구니)
- Read Replica 구성

#### 리스크 5: 이미지 저장 공간
**문제**: 상품/리뷰 이미지 용량 증가
**해결책**:
- Phase 1: 로컬 파일 시스템
- Phase 2: AWS S3 or CloudFlare R2 마이그레이션

### 📊 저위험 (주기적 검토)

#### 리스크 6: API 응답 속도
**문제**: 복잡한 쿼리로 응답 지연
**해결책**:
- N+1 문제 방지 (Fetch Join)
- 페이징 최적화
- 캐싱 전략

---

## 다음 단계

### 즉시 착수
1. [ ] 프로젝트 저장소 생성 (GitHub)
2. [ ] 개발 환경 구성 (Docker Compose)
3. [ ] CI/CD 파이프라인 구축
4. [ ] ERD 상세 설계
5. [ ] API 명세서 작성

### 1주 내
1. [ ] Sprint 1 시작 (인프라 설정)
2. [ ] 팀 역할 분담
3. [ ] 코딩 컨벤션 합의
4. [ ] Git 브랜치 전략 수립

---

**작성자**: Cline AI Assistant (requirements-analysis SKILL 적용)  
**최종 수정일**: 2026-02-09

---

## 📝 자동 진행 로그

> 이 섹션은 Claude Code Stop hook에 의해 자동 업데이트됩니다.


### 2026-02-10 09:44

**체크리스트 업데이트**: 1개 항목 완료 체크

