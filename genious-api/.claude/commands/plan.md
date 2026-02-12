---
description: 요구사항을 다시 정리하고, 리스크를 평가하며, 단계별 구현 계획을 만듭니다. 어떤 코드에도 손대기 전에 사용자 확인을 기다립니다.
---

# 계획 명령

이 명령은 **planner** 에이전트를 호출하여, 어떤 코드도 작성하기 전에 포괄적인 구현 계획을 수립합니다.

## 이 명령이 하는 일

1. **요구사항 재진술** - 무엇을 만들어야 하는지 명확히 함
2. **리스크 식별** - 잠재 문제와 장애 요인을 드러냄
3. **단계별 계획 수립** - 구현을 여러 단계로 쪼갬
4. **확인 대기** - 반드시 사용자 승인을 받은 뒤에만 진행

## 언제 사용하나요

다음 상황에서 `/plan`을 사용하세요:

* 새 기능을 시작할 때
* 중요한 아키텍처 변경을 진행할 때
* 복잡한 리팩터링을 다룰 때
* 여러 파일/컴포넌트에 영향이 갈 때
* 요구사항이 불명확하거나 애매함이 있을 때

## 동작 원리

planner 에이전트는 다음을 수행합니다:

1. 요청을 **분석**하고, 이해하기 쉬운 표현으로 요구사항을 다시 정리
2. 각 단계에 구체적이고 실행 가능한 항목을 포함해 **여러 단계로 분해**
3. **컴포넌트 간 의존성** 식별
4. **리스크 및 잠재 장애 요인** 평가
5. **복잡도**(높음/중간/낮음) 추정
6. 계획을 **제시**하고 사용자의 명확한 확인을 기다림

## 사용 예시

```text
User: /plan I need to add real-time notifications when markets resolve

Agent (planner):
# Implementation Plan: Real-Time Market Resolution Notifications

## Requirements Restatement
- Send notifications to users when markets they're watching resolve
- Support multiple notification channels (in-app, email, webhook)
- Ensure notifications are delivered reliably
- Include market outcome and user's position result

## Implementation Phases

### Phase 1: Database Schema
- Add notifications table with columns: id, user_id, market_id, type, status, created_at
- Add user_notification_preferences table for channel preferences
- Create indexes on user_id and market_id for performance

### Phase 2: Notification Service
- Create notification service in lib/notifications.ts
- Implement notification queue using BullMQ/Redis
- Add retry logic for failed deliveries
- Create notification templates

### Phase 3: Integration Points
- Hook into market resolution logic (when status changes to "resolved")
- Query all users with positions in market
- Enqueue notifications for each user

### Phase 4: Frontend Components
- Create NotificationBell component in header
- Add NotificationList modal
- Implement real-time updates via Supabase subscriptions
- Add notification preferences page

## Dependencies
- Redis (for queue)
- Email service (SendGrid/Resend)
- Supabase real-time subscriptions

## Risks
- HIGH: Email deliverability (SPF/DKIM required)
- MEDIUM: Performance with 1000+ users per market
- MEDIUM: Notification spam if markets resolve frequently
- LOW: Real-time subscription overhead

## Estimated Complexity: MEDIUM
- Backend: 4-6 hours
- Frontend: 3-4 hours
- Testing: 2-3 hours
- Total: 9-13 hours

**WAITING FOR CONFIRMATION**: Proceed with this plan? (yes/no/modify)
```

## 중요 안내

**핵심**: planner 에이전트는 사용자가 “예”, “진행”, “계속” 등과 같은 **명확한 긍정 응답**으로 계획을 확인하기 전까지는 **어떤 코드도 작성하지 않습니다.**

수정이 필요하다면 아래처럼 답해주세요:

* "수정: [수정 내용]"
* "다른 방법: [대안]"
* "2단계는 건너뛰고 3단계부터 진행"

## 다른 명령과의 연동

계획 이후에는:

* `/tdd`로 테스트 주도 개발 방식으로 구현
* 빌드 오류가 발생하면 `/build-and-fix` 사용
* 구현이 끝나면 `/code-review`로 리뷰

## 관련 에이전트

이 명령은 `~/.claude/agents/planner.md`에 위치한 `planner` 에이전트를 호출합니다.
