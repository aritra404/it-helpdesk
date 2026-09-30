@echo off
echo ========================================================
echo  Compiling IT Helpdesk and Incident Management System
echo ========================================================
javac -cp ".;lib/*" *.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %errorlevel%
)

echo.
echo [1/3] Copying compiled classes to webapp/WEB-INF/classes...
if not exist "webapp\WEB-INF\classes" mkdir "webapp\WEB-INF\classes"
copy /Y *.class "webapp\WEB-INF\classes\" >nul

echo [2/3] Copying libraries to webapp/WEB-INF/lib...
if not exist "webapp\WEB-INF\lib" mkdir "webapp\WEB-INF\lib"
copy /Y "lib\*" "webapp\WEB-INF\lib\" >nul

echo [3/3] Packaging helpdesk.war archive...
jar -cvf helpdesk.war -C webapp . >nul

echo.
echo ========================================================
echo  BUILD SUCCESSFUL! 
echo  - Output: helpdesk.war
echo  - Deploy this WAR into Apache Tomcat's 'webapps/' directory
echo ========================================================
