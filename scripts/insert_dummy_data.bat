@echo off
setlocal enabledelayedexpansion

rem K-Culture dummy data loader
rem Double-click this file (or run it) to insert MV analysis dummy data
rem into the local kculture MySQL database. Safe to run more than once.

set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=kculture
set DB_USER=root
if not defined DB_PASSWORD set DB_PASSWORD=  rem db비번의 경우는 본인의 비번에 맞게 수정하여 사용

set SCRIPT_DIR=%~dp0
set SQL_FILE=%SCRIPT_DIR%dummy_data.sql

echo ============================================
echo  K-Culture dummy data loader
echo ============================================
echo.

where mysql >nul 2>nul
if %ERRORLEVEL%==0 (
    set MYSQL_EXE=mysql
) else (
    set "MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"  rem 해당 부분도 본인의 mysql.exe 경로를 파악하여 변경해야됨
    if not exist "!MYSQL_EXE!" (
        echo [ERROR] mysql.exe not found.
        echo   1. Check that MySQL is installed and the service is running.
        echo   2. If it is installed somewhere else, edit MYSQL_EXE in this file.
        pause
        exit /b 1
    )
)

echo mysql client : %MYSQL_EXE%
echo target DB    : %DB_HOST%:%DB_PORT%/%DB_NAME% ^(user=%DB_USER%^)
echo.

set MYSQL_PWD=%DB_PASSWORD%
"%MYSQL_EXE%" -h %DB_HOST% -P %DB_PORT% -u %DB_USER% %DB_NAME% -e "source %SQL_FILE%"
set MYSQL_RESULT=%ERRORLEVEL%
set MYSQL_PWD=

echo.
if %MYSQL_RESULT%==0 (
    echo [OK] dummy data inserted successfully.
) else (
    echo [FAIL] insert failed, exit code %MYSQL_RESULT%. See the error above.
    echo   - Is the MySQL80 service running?
    echo   - Has the kculture DB been created and migrated at least once ^(run the app once with ./gradlew bootRun^)?
    echo   - If your local MySQL password is not 1234, set DB_PASSWORD before running this script.
)
echo.
pause
endlocal
