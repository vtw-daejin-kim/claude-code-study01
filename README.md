# Claude Code Study — 이커머스 MVP로 배우는 Claude Code 활용법

이 프로젝트는 **Claude Code를 실무에서 어떻게 활용하는지** 보여주기 위한 스터디 프로젝트입니다.

이커머스 MVP 백엔드를 구축하는 과정을 통해, 요구사항 분석부터 설계, 구현, 테스트까지 **Claude Code의 주요 기능들을 단계적으로 학습**할 수 있습니다.

## 이 프로젝트에서 다루는 Claude Code 기능

### `.claude/` 디렉터리 구조

프로젝트 루트의 `.claude/` 디렉터리에 Claude Code의 설정 파일들이 위치합니다.

```
.claude/
├── settings.json                  # 프로젝트 범위 설정 (Hooks 이벤트 바인딩)
├── hooks/
│   └── notify.sh                  # WSL2→Windows 데스크톱 알림 스크립트
├── agents/                        # Sub-Agent 정의 (도메인별 전문 에이전트)
│   ├── sub-agent-architect.md     #   프로젝트 분석 → 에이전트 구성 설계
│   ├── project-scaffolding.md     #   Phase 0: 프로젝트 초기화, 글로벌 인프라
│   ├── user-domain.md             #   Phase 1: 회원가입/로그인/프로필
│   ├── brand-domain.md            #   Phase 2: 브랜드 CRUD
│   ├── product-domain.md          #   Phase 3: 상품/재고 관리
│   ├── like-cart-domain.md        #   Phase 4~5: 좋아요/장바구니
│   ├── order-domain.md            #   Phase 6: 주문/만료 배치
│   ├── stats-domain.md            #   Phase 7: 관리자 통계 대시보드
│   └── test-quality.md            #   Phase 8: E2E 테스트, 코드 품질 점검
├── agent-memory/                  # Sub-Agent 전용 메모리 (세션 간 지식 유지)
│   └── sub-agent-architect/
│       ├── MEMORY.md              #     핵심 요약 (시스템 프롬프트에 포함)
│       ├── project-analysis.md    #     프로젝트 분석 결과
│       └── agent-configs.md       #     에이전트 구성 설계 내역
├── SKILLS/                        # Custom Slash Commands (재사용 가능한 스킬)
│   └── requirements-analysis/
│       └── SKILL.md               #     /requirements-analysis 스킬 정의
└── commands/                      # Custom Slash Commands (레거시 방식, 현재 비어 있음)
```

각 디렉터리의 역할은 다음과 같습니다.

| 경로 | 역할 |
|---|---|
| `settings.json` | 프로젝트 범위의 Claude Code 설정. Hook 이벤트(Stop, Notification, PostToolUse, TaskCompleted)별 실행할 커맨드를 정의 |
| `hooks/` | Hook에서 실행되는 스크립트. `notify.sh`는 WSL2 환경에서 `powershell.exe`를 호출하여 Windows 토스트 알림 전송 |
| `agents/` | Sub-Agent 정의 파일. 각 `.md` 파일이 하나의 전문 에이전트로, Task 도구 호출 시 `subagent_type`으로 지정하여 사용 |
| `agent-memory/` | Sub-Agent가 세션 간에 학습한 지식을 저장하는 영속 메모리. `MEMORY.md`는 시스템 프롬프트에 자동 포함 |
| `SKILLS/` | `/명령어` 형태로 호출 가능한 커스텀 스킬. `SKILL.md`의 frontmatter에 이름과 설명을 정의 |
| `commands/` | 커스텀 슬래시 커맨드의 레거시 방식 디렉터리 (현재 미사용, SKILLS로 대체) |

### 1. CLAUDE.md — 프로젝트 컨텍스트 공유

프로젝트 루트의 [`CLAUDE.md`](./CLAUDE.md)에 기술 스택, 아키텍처, 설계 원칙, 빌드 명령어 등을 기술하여 Claude Code가 프로젝트 맥락을 이해한 상태에서 작업하도록 합니다.

### 2. Sub-Agents — 도메인별 전문 에이전트

[`.claude/agents/`](./.claude/agents/) 디렉토리에 도메인별 전문 에이전트를 정의했습니다.

| 에이전트 | 역할 |
|---|---|
| `sub-agent-architect` | 프로젝트 분석 후 에이전트 구성 설계 |
| `project-scaffolding` | Phase 0 — 프로젝트 초기화, 글로벌 인프라 |
| `user-domain` | Phase 1 — 회원가입/로그인/프로필 |
| `brand-domain` | Phase 2 — 브랜드 CRUD |
| `product-domain` | Phase 3 — 상품/재고 관리 |
| `like-cart-domain` | Phase 4~5 — 좋아요/장바구니 |
| `order-domain` | Phase 6 — 주문/결제/만료 배치 |
| `stats-domain` | Phase 7 — 관리자 통계 대시보드 |
| `test-quality` | Phase 8 — E2E 테스트, 코드 품질 점검 |

### 3. Custom Skills — 재사용 가능한 슬래시 커맨드

[`.claude/SKILLS/`](./.claude/SKILLS/) 디렉토리에 커스텀 스킬을 정의했습니다.

- **`/requirements-analysis`** — 요구사항을 분석하고, 개발자와의 Q&A를 통해 모호한 부분을 명확히 한 뒤, Mermaid 다이어그램(시퀀스, 클래스, ERD)으로 정리

### 4. Hooks — 작업 알림 자동화

[`.claude/hooks/notify.sh`](./.claude/hooks/notify.sh)와 [`.claude/settings.json`](./.claude/settings.json)으로 프로젝트 범위의 Hook을 설정했습니다.

- **Stop** — 작업 완료 시 Windows 토스트 알림
- **Notification** — 권한 승인 대기 시 알림
- **PostToolUse (AskUserQuestion)** — 선택지 제시 시 알림
- **TaskCompleted** — 개별 태스크 완료 시 알림

> WSL2 환경에서 `powershell.exe`를 호출하여 Windows 데스크톱 알림을 보냅니다.

### 5. 문서 기반 개발 프로세스

Claude Code와 함께 아래 순서로 프로젝트를 진행합니다.

```
요구사항 정의 → 요구사항 분석 → 설계 → 구현 계획 → 단계별 구현
```

| 문서 | 설명 |
|---|---|
| [`01-requirements.md`](./01-requirements.md) | 유저 시나리오 기반 기능/비기능 요구사항 |
| [`analysis.md`](./analysis.md) | 요구사항 분석, 확정 정책 9건, ERD/시퀀스/상태전이 다이어그램 |
| [`plan.md`](./plan.md) | 기술 스택, API 설계, 패키지 구조, 데이터 모델 |
| [`task.md`](./task.md) | Phase 0~8 구현 체크리스트 + FR↔API 추적표 |

## 이커머스 MVP 개요

결제를 제외한 유저/브랜드/상품/좋아요/장바구니/주문 도메인을 포함하는 백엔드 API입니다.

### 기술 스택

- **Java 17** / **Gradle 8.12** / **Spring Boot 3.4.1**
- **JPA + MyBatis 하이브리드** — 쓰기는 JPA, 복잡한 읽기는 MyBatis
- **PostgreSQL 15+** / **Flyway** (DDL 버전 관리)
- **Spring Security + JWT** (Stateless)
- **Testcontainers** (통합 테스트)

### 프로젝트 구조

```
kr.go.ecommerce
├── global/               # 횡단 관심사
│   ├── config/           # Security, JPA, MyBatis, WebMvc, Scheduler
│   ├── security/         # JWT 토큰, 필터, UserDetails
│   ├── exception/        # ErrorCode, BusinessException, GlobalExceptionHandler
│   ├── dto/              # ApiResponse, ErrorResponse, PageResponse
│   └── util/             # SecurityUtil, JsonSnapshotUtil
└── domain/               # 비즈니스 도메인
    └── {도메인명}/
        ├── entity/       # JPA 엔티티
        ├── repository/   # JPA Repository
        ├── mapper/       # MyBatis Mapper
        ├── service/
        ├── dto/
        └── controller/
```

### 구현 단계

| Phase | 내용 | 상태 |
|---|---|---|
| 0 | 프로젝트 스캐폴딩 (빌드, DB 마이그레이션, 글로벌 인프라) | 완료 |
| 1 | User 도메인 (회원가입, 로그인, 프로필) | 예정 |
| 2 | Brand 도메인 (브랜드 CRUD) | 예정 |
| 3 | Product 도메인 (상품/재고 관리) | 예정 |
| 4~5 | Like + Cart 도메인 | 예정 |
| 6 | Order 도메인 (주문, 재고 예약, 만료 배치) | 예정 |
| 7 | Stats 도메인 (관리자 통계) | 예정 |
| 8 | 테스트 하드닝 및 코드 품질 점검 | 예정 |

## 로컬 실행

```bash
# PostgreSQL 실행
docker compose up -d

# 빌드 및 실행
./gradlew bootRun

# 전체 테스트 (Docker 필요 — Testcontainers)
./gradlew test
```
