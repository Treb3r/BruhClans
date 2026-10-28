@echo off
cd /d "%~dp0"
call mvn package -o -DskipTests
if %errorlevel% neq 0 (
    echo [ERROR] Build failed!
    pause
    exit /b %errorlevel%
)
copy /y "target\bruhstudio-1.0.0.jar" "test_server\plugins\BruhClans.jar"
echo [OK] BruhClans rebuilt and copied to test_server\plugins\BruhClans.jar
pause
