@echo off
REM 1) .env 파일 있는지 확인
IF NOT EXIST .env (
  echo ERROR: .env 파일이 없습니다. .env.example를 복사해 만드세요.
  exit /b 1
)

REM 2) .env 내용 읽어서 환경변수 설정
for /f "usebackq tokens=1,* delims==" %%A in (`type .env ^| findstr /v "^#"`) do (
  set "%%A=%%B"
)

REM 3) 애플리케이션 실행
gradlew.bat clean bootRun
