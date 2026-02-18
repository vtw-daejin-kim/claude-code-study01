#!/bin/bash
# Claude Code Desktop Notification Hook (WSL2 → Windows Toast)
# Reads hook JSON from stdin and sends a Windows desktop notification

INPUT=$(cat)

# Parse JSON using python3 (jq not always available in WSL)
json_get() {
  echo "$INPUT" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('$1',''))" 2>/dev/null
}

EVENT=$(json_get hook_event_name)

TITLE="Claude Code"
MESSAGE=""

case "$EVENT" in
  Stop)
    MESSAGE="작업이 완료되었습니다."
    ;;
  Notification)
    NTYPE=$(json_get type)
    case "$NTYPE" in
      permission_prompt)
        MESSAGE="권한 승인이 필요합니다."
        ;;
      *)
        MESSAGE="응답을 기다리고 있습니다."
        ;;
    esac
    ;;
  TaskCompleted)
    SUBJECT=$(json_get task_subject)
    if [ -n "$SUBJECT" ]; then
      MESSAGE="작업 완료: $SUBJECT"
    else
      MESSAGE="작업이 완료되었습니다."
    fi
    ;;
  PostToolUse)
    TOOL=$(json_get tool_name)
    if [ "$TOOL" = "AskUserQuestion" ]; then
      MESSAGE="선택지가 제시되었습니다. 확인해주세요."
    else
      exit 0
    fi
    ;;
  *)
    exit 0
    ;;
esac

if [ -z "$MESSAGE" ]; then
  exit 0
fi

# Escape single quotes for PowerShell
TITLE_ESC=$(echo "$TITLE" | sed "s/'/\\\\'/g")
MESSAGE_ESC=$(echo "$MESSAGE" | sed "s/'/\\\\'/g")

powershell.exe -NoProfile -Command "
Add-Type -AssemblyName System.Windows.Forms
\$balloon = New-Object System.Windows.Forms.NotifyIcon
\$balloon.Icon = [System.Drawing.SystemIcons]::Information
\$balloon.BalloonTipIcon = 'Info'
\$balloon.BalloonTipTitle = '$TITLE_ESC'
\$balloon.BalloonTipText = '$MESSAGE_ESC'
\$balloon.Visible = \$true
\$balloon.ShowBalloonTip(5000)
Start-Sleep -Milliseconds 500
\$balloon.Dispose()
" >/dev/null 2>&1 &

exit 0
