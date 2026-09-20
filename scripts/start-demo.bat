@echo off
setlocal EnableExtensions
echo ========================================
echo   Yilu Yinling Demo startup check
echo ========================================
where java >nul 2>nul
if errorlevel 1 (
  echo [ERROR] Java was not found. Install Java 21 or newer and add it to PATH.
  exit /b 1
)
java -version 2>&1 | findstr /R /C:"version \"2[1-9]" /C:"version \"[3-9][0-9]" >nul
if errorlevel 1 (
  echo [ERROR] Java 21 or newer is required.
  java -version
  exit /b 1
)
set "APP_DIR=%~dp0"
if not exist "%APP_DIR%yinling-backend.jar" if exist "%APP_DIR%..\backend\target\yinling-backend.jar" set "APP_DIR=%APP_DIR%..\backend\target\"
if not exist "%APP_DIR%yinling-backend.jar" (
  echo [ERROR] yinling-backend.jar was not found.
  echo Put this script beside the JAR, or build the project first.
  exit /b 1
)
netstat -ano | findstr /R /C:":8080 .*LISTENING" >nul
if not errorlevel 1 (
  echo [ERROR] Port 8080 is already in use. Close the process and retry.
  netstat -ano | findstr /R /C:":8080 .*LISTENING"
  exit /b 1
)
echo [OK] Demo mode uses no Docker, database, object storage, or external AI API.
echo.
echo ========================================
echo   Yilu Yinling Demo started
echo ========================================
echo Swagger: http://localhost:8080/swagger-ui/index.html
echo Health:  http://localhost:8080/api/v1/system/health
echo.
java -jar "%APP_DIR%yinling-backend.jar" --spring.profiles.active=demo
if errorlevel 1 (
  echo [ERROR] Demo stopped unexpectedly. Check the Java version, port, and JAR file.
  exit /b 1
)
endlocal
