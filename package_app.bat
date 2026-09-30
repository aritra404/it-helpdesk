@echo off
echo ========================================================
echo  Compiling IT Helpdesk and Incident Management System
echo ========================================================

if not exist "webapp\WEB-INF\classes" mkdir "webapp\WEB-INF\classes"
if not exist "webapp\WEB-INF\lib" mkdir "webapp\WEB-INF\lib"

echo [1/3] Compiling Java source packages...
javac -cp ".;lib/*" -d webapp/WEB-INF/classes src/com/helpdesk/util/*.java src/com/helpdesk/model/*.java src/com/helpdesk/dao/*.java src/com/helpdesk/servlet/*.java src/com/helpdesk/*.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %errorlevel%
)

echo [2/3] Syncing runtime libraries to webapp/WEB-INF/lib...
copy /Y "lib\*" "webapp\WEB-INF\lib\" >nul

echo [3/3] Packaging helpdesk.war archive...
jar -cvf helpdesk.war -C webapp . >nul

echo.
echo ========================================================
echo  BUILD SUCCESSFUL!
echo  - Output: helpdesk.war
echo  - Deploy this WAR into Apache Tomcat's 'webapps/' directory
echo ========================================================
