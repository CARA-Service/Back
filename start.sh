#!/usr/bin/env bash
# start.sh
set -e

# 1) .env 파일이 있는지 확인
if [ ! -f .env ]; then
  echo "ERROR: .env 파일이 없습니다. .env.example를 복사해 만드세요."
  exit 1
fi

# 2) 환경변수 로딩
export $(grep -v '^#' .env | xargs)

# 3) 애플리케이션 실행
./gradlew clean bootRun
