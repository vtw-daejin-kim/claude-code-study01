#!/bin/bash
###############################################################################
# update-task-progress.sh
#
# Claude Code Stop hook: 작업 완료 시 전체_TASK.md 자동 업데이트
#
# 동작:
#   1. 소스 파일 존재 여부로 Sprint 체크리스트 자동 체크
#   2. agent-memory MEMORY.md 파일에서 진행 메모 수집
#   3. 전체_TASK.md 하단에 진행 로그 추가
###############################################################################
set -uo pipefail
export LANG=en_US.UTF-8

# stdin으로 전달되는 hook JSON input 읽기
INPUT=$(cat 2>/dev/null || echo '{}')

# 프로젝트 디렉토리 결정
PROJECT_DIR=$(echo "$INPUT" | jq -r '.cwd // empty' 2>/dev/null)
if [ -z "$PROJECT_DIR" ] || [ "$PROJECT_DIR" = "null" ]; then
    PROJECT_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
fi

TASK_FILE="$PROJECT_DIR/전체_TASK.md"
SRC_DIR="$PROJECT_DIR/src/main/java/com/genious/api"
MEMORY_DIR="$PROJECT_DIR/.claude/agent-memory"
TIMESTAMP=$(date '+%Y-%m-%d %H:%M')

# 전체_TASK.md 없으면 종료
if [ ! -f "$TASK_FILE" ]; then
    exit 0
fi

# 변경 사항 추적용
CHANGES_MADE=0

###############################################################################
# Part 1: 소스 파일 존재 여부 기반 체크리스트 자동 업데이트
###############################################################################

# check_and_mark <파일경로(SRC_DIR 상대)> <TASK.md 내 체크박스 텍스트 패턴>
# 파일이 존재하면 해당 체크박스를 [x]로 변경
check_and_mark() {
    local file_path="$1"
    local task_pattern="$2"

    if [ -f "$SRC_DIR/$file_path" ]; then
        # 이미 체크된 항목은 건너뜀
        if grep -q "\- \[ \].*${task_pattern}" "$TASK_FILE" 2>/dev/null; then
            sed -i "s/- \[ \] \(.*${task_pattern}\)/- [x] \1/" "$TASK_FILE"
            CHANGES_MADE=$((CHANGES_MADE + 1))
        fi
    fi
}

# 디렉토리 존재 여부로 체크
check_dir_and_mark() {
    local dir_path="$1"
    local task_pattern="$2"

    if [ -d "$SRC_DIR/$dir_path" ] && [ "$(ls -A "$SRC_DIR/$dir_path" 2>/dev/null)" ]; then
        if grep -q "\- \[ \].*${task_pattern}" "$TASK_FILE" 2>/dev/null; then
            sed -i "s/- \[ \] \(.*${task_pattern}\)/- [x] \1/" "$TASK_FILE"
            CHANGES_MADE=$((CHANGES_MADE + 1))
        fi
    fi
}

# ── Sprint 1: 인프라 및 기본 설정 ──
check_and_mark "GeniousApiApplication.java" "Spring Boot 프로젝트 생성"
check_and_mark "global/config/RedisConfig.java" "Redis 설정"
check_and_mark "global/security/JwtTokenProvider.java" "JWT 설정 (JwtTokenProvider)"
check_and_mark "global/config/SecurityConfig.java" "Spring Security 설정"
check_and_mark "domain/user/entity/User.java" "User 엔티티 및 Repository"
check_and_mark "domain/user/repository/UserRepository.java" "User 엔티티 및 Repository"
check_and_mark "domain/user/controller/AuthController.java" "회원가입\/로그인 API"

# Docker Compose 체크 (프로젝트 루트)
if [ -f "$PROJECT_DIR/docker-compose.yml" ] || [ -f "$PROJECT_DIR/docker-compose.yaml" ]; then
    if grep -q "\- \[ \].*PostgreSQL Docker Compose" "$TASK_FILE" 2>/dev/null; then
        sed -i "s/- \[ \] \(.*PostgreSQL Docker Compose.*\)/- [x] \1/" "$TASK_FILE"
        CHANGES_MADE=$((CHANGES_MADE + 1))
    fi
fi

# ── Sprint 2: 상품 관리 ──
check_and_mark "domain/product/entity/Product.java" "Product 엔티티 설계"
check_and_mark "domain/product/entity/Category.java" "Category 엔티티 설계"
check_and_mark "domain/product/entity/ProductImage.java" "ProductImage 엔티티"
check_and_mark "domain/product/entity/ProductOption.java" "ProductOption 엔티티"
check_and_mark "domain/product/repository/ProductRepositoryImpl.java" "ProductRepository (QueryDSL"
check_and_mark "domain/product/controller/ProductController.java" "상품 목록 조회"
check_and_mark "domain/product/controller/ProductController.java" "상품 상세 조회"
check_and_mark "domain/product/service/ProductService.java" "상품 검색"
check_and_mark "domain/product/controller/ProductController.java" "관리자: 상품 등록\/수정\/삭제"

# ── Sprint 3: 재고 관리 ──
check_and_mark "domain/inventory/entity/Inventory.java" "Inventory 엔티티"
check_and_mark "domain/inventory/entity/InventoryReservation.java" "InventoryReservation 엔티티"
check_and_mark "domain/inventory/service/InventoryService.java" "InventoryService"
check_and_mark "domain/inventory/service/InventoryService.java" "재고 조회"
check_and_mark "domain/inventory/service/InventoryService.java" "재고 예약"
check_and_mark "domain/inventory/service/InventoryService.java" "재고 차감"
check_and_mark "domain/inventory/service/InventoryService.java" "재고 복구"
check_and_mark "domain/inventory/scheduler/InventoryReservationCleanupScheduler.java" "만료된 예약 정리"

# ── Sprint 4: 장바구니 & 위시리스트 ──
check_and_mark "domain/cart/entity/Cart.java" "Cart 엔티티"
check_and_mark "domain/cart/entity/CartItem.java" "CartItem 엔티티"
check_and_mark "domain/cart/service/CartService.java" "CartService"
check_and_mark "domain/wishlist/entity/Wishlist.java" "Wishlist 엔티티"
check_and_mark "domain/wishlist/service/WishlistService.java" "WishlistService"

# ── Sprint 5: 주문 시스템 ──
check_and_mark "domain/order/entity/Order.java" "Order 엔티티"
check_and_mark "domain/order/entity/OrderItem.java" "OrderItem 엔티티"
check_and_mark "domain/order/entity/OrderStatus.java" "OrderStatus enum"
check_and_mark "domain/order/service/OrderService.java" "OrderService"
check_and_mark "domain/order/service/OrderService.java" "주문 생성"
check_and_mark "domain/order/service/OrderService.java" "주문 조회"
check_and_mark "domain/order/service/OrderService.java" "주문 취소"
check_and_mark "domain/order/service/OrderService.java" "주문 상태 전이"

# ── Sprint 6: 결제 시스템 ──
check_and_mark "domain/payment/entity/Payment.java" "Payment 엔티티"
check_and_mark "domain/payment/entity/PaymentMethod.java" "PaymentMethod enum"
check_and_mark "domain/payment/entity/PaymentStatus.java" "PaymentStatus enum"
check_and_mark "domain/payment/service/PaymentService.java" "PaymentService"
check_and_mark "domain/payment/service/MockPaymentGateway.java" "결제 처리 (Mock)"
check_and_mark "domain/order/service/OrderPaymentFacade.java" "OrderPaymentFacade"

# ── Sprint 7: 리뷰 시스템 ──
check_and_mark "domain/review/entity/Review.java" "Review 엔티티"
check_and_mark "domain/review/entity/ReviewImage.java" "ReviewImage 엔티티"
check_and_mark "domain/review/service/ReviewService.java" "ReviewService"

# ── Sprint 8: 관리자 기능 ──
check_and_mark "domain/admin/service/AdminDashboardService.java" "대시보드 통계 API"
check_and_mark "domain/admin/service/AdminOrderService.java" "주문 관리 API"
check_and_mark "domain/admin/service/AdminUserService.java" "회원 관리 API"

# ── Sprint 9: 테스트 ──
TEST_DIR="$PROJECT_DIR/src/test/java/com/genious/api"
if [ -d "$TEST_DIR/domain/user" ] && [ "$(find "$TEST_DIR/domain/user" -name '*Test.java' 2>/dev/null | head -1)" ]; then
    if grep -q "\- \[ \].*단위 테스트 (Service 계층)" "$TASK_FILE" 2>/dev/null; then
        sed -i "s/- \[ \] \(.*단위 테스트 (Service 계층)\)/- [x] \1/" "$TASK_FILE"
        CHANGES_MADE=$((CHANGES_MADE + 1))
    fi
fi
if [ -d "$TEST_DIR" ] && [ "$(find "$TEST_DIR" -name '*ControllerTest.java' 2>/dev/null | head -1)" ]; then
    if grep -q "\- \[ \].*통합 테스트 (API)" "$TASK_FILE" 2>/dev/null; then
        sed -i "s/- \[ \] \(.*통합 테스트 (API)\)/- [x] \1/" "$TASK_FILE"
        CHANGES_MADE=$((CHANGES_MADE + 1))
    fi
fi

###############################################################################
# Part 2: agent-memory 메모 수집 → 진행 로그 추가
###############################################################################

NOTES=""

if [ -d "$MEMORY_DIR" ]; then
    for MEMORY_FILE in "$MEMORY_DIR"/*/MEMORY.md; do
        if [ -f "$MEMORY_FILE" ]; then
            AGENT_NAME=$(basename "$(dirname "$MEMORY_FILE")")
            CONTENT=$(head -50 "$MEMORY_FILE" 2>/dev/null | grep -v '^$' | head -10)
            if [ -n "$CONTENT" ]; then
                NOTES="${NOTES}\n#### ${AGENT_NAME}\n${CONTENT}\n"
            fi
        fi
    done
fi

# 글로벌 메모리 파일도 수집
GLOBAL_MEMORY="$HOME/.claude/projects/-mnt-c-Users-----git-claude-code-test01/memory/MEMORY.md"
if [ -f "$GLOBAL_MEMORY" ]; then
    GLOBAL_CONTENT=$(head -20 "$GLOBAL_MEMORY" 2>/dev/null | grep -v '^$' | head -5)
    if [ -n "$GLOBAL_CONTENT" ]; then
        NOTES="${NOTES}\n#### global-memory\n${GLOBAL_CONTENT}\n"
    fi
fi

###############################################################################
# Part 3: 진행 로그 섹션 업데이트
###############################################################################

PROGRESS_HEADER="## 📝 자동 진행 로그"

# 진행 로그 섹션이 없으면 파일 끝에 추가
if ! grep -q "$PROGRESS_HEADER" "$TASK_FILE" 2>/dev/null; then
    cat >> "$TASK_FILE" << 'SECTION_EOF'

---

## 📝 자동 진행 로그

> 이 섹션은 Claude Code Stop hook에 의해 자동 업데이트됩니다.

SECTION_EOF
fi

# 변경 사항이 있을 때만 로그 기록
if [ "$CHANGES_MADE" -gt 0 ] || [ -n "$NOTES" ]; then
    # git에서 최근 변경 파일 목록 가져오기
    CHANGED_FILES=""
    if command -v git &> /dev/null && [ -d "$PROJECT_DIR/.git" ]; then
        CHANGED_FILES=$(cd "$PROJECT_DIR" && git diff --name-only HEAD 2>/dev/null | grep '\.java$' | head -20)
        if [ -z "$CHANGED_FILES" ]; then
            CHANGED_FILES=$(cd "$PROJECT_DIR" && git diff --name-only --cached 2>/dev/null | grep '\.java$' | head -20)
        fi
    fi

    # 로그 엔트리 작성
    LOG_ENTRY="\n### ${TIMESTAMP}\n"
    LOG_ENTRY="${LOG_ENTRY}\n**체크리스트 업데이트**: ${CHANGES_MADE}개 항목 완료 체크\n"

    if [ -n "$CHANGED_FILES" ]; then
        LOG_ENTRY="${LOG_ENTRY}\n**변경된 파일**:\n"
        while IFS= read -r file; do
            LOG_ENTRY="${LOG_ENTRY}- \`${file}\`\n"
        done <<< "$CHANGED_FILES"
    fi

    if [ -n "$NOTES" ]; then
        LOG_ENTRY="${LOG_ENTRY}\n**에이전트 메모**:\n${NOTES}"
    fi

    # 파일 끝에 로그 추가
    echo -e "$LOG_ENTRY" >> "$TASK_FILE"
fi

exit 0
