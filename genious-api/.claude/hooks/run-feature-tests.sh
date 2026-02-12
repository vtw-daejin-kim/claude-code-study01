#!/usr/bin/env bash
###############################################################################
# run-feature-tests.sh
#
# Claude Code Stop hook: 기능 단위 작업 완료 후 해당 기능의 테스트 자동 실행
###############################################################################
set -u -o pipefail
export LANG=en_US.UTF-8

# stdin으로 전달되는 hook JSON input 읽기
INPUT=$(cat 2>/dev/null || echo '{}')

# 프로젝트 디렉토리 결정
PROJECT_DIR=$(echo "$INPUT" | jq -r '.cwd // empty' 2>/dev/null)
if [ -z "$PROJECT_DIR" ] || [ "$PROJECT_DIR" = "null" ]; then
  PROJECT_DIR="${CLAUDE_PROJECT_DIR:-$(pwd)}"
fi

cd "$PROJECT_DIR" || exit 0

# 변경된 Java 소스 파일 감지 (src/main/java 하위만)
CHANGED_FILES=$(git diff --name-only HEAD 2>/dev/null || git diff --name-only 2>/dev/null)
MAIN_JAVA_FILES=$(echo "$CHANGED_FILES" | grep -E '^src/main/java/.*\.java$' || true)

if [ -z "$MAIN_JAVA_FILES" ]; then
  exit 0
fi

# 대응하는 테스트 클래스 탐색
TEST_CLASSES=""
FOUND_TESTS=""

for src_file in $MAIN_JAVA_FILES; do
  [ -z "$src_file" ] && continue

  test_file=$(echo "$src_file" | sed 's|src/main/java|src/test/java|')
  class_name=$(basename "$src_file" .java)
  test_dir=$(dirname "$test_file")
  test_candidate="${test_dir}/${class_name}Test.java"

  if [ -f "$test_candidate" ]; then
    fqn=$(echo "$test_candidate" | sed 's|src/test/java/||; s|/|.|g; s|\.java$||')
    TEST_CLASSES="${TEST_CLASSES} --tests ${fqn}"
    FOUND_TESTS="${FOUND_TESTS} ${test_candidate}"
  fi
done

if [ -z "$TEST_CLASSES" ]; then
  exit 0
fi

echo ""
echo "==========================================="
echo " [Hook] 변경된 기능의 테스트 자동 실행"
echo "==========================================="
echo ""
echo "변경된 소스 파일:"
for f in $MAIN_JAVA_FILES; do
  echo "  - $f"
done
echo ""
echo "실행할 테스트:"
for t in $FOUND_TESTS; do
  echo "  - $t"
done
echo ""
echo "$ ./gradlew test${TEST_CLASSES}"
echo "-------------------------------------------"

TEST_OUTPUT=$(timeout 300 ./gradlew test ${TEST_CLASSES} --no-daemon --console=plain 2>&1) || true
EXIT_CODE=$?

echo "$TEST_OUTPUT"
echo "-------------------------------------------"

if [ "$EXIT_CODE" -eq 0 ]; then
  echo ""
  echo "[PASS] 모든 테스트가 통과했습니다."
else
  echo ""
  echo "[FAIL] 테스트 실패가 발견되었습니다. (exit code: $EXIT_CODE)"
  REPORT_DIR="$PROJECT_DIR/build/reports/tests/test"
  if [ -d "$REPORT_DIR" ]; then
    echo "상세 리포트: $REPORT_DIR/index.html"
  fi
fi

echo "==========================================="
echo ""
exit 0
