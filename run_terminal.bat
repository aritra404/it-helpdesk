@echo off
echo ========================================================
echo  Launching Terminal IT Helpdesk and Incident System
echo ========================================================

if not exist "webapp\WEB-INF\classes" mkdir "webapp\WEB-INF\classes"

javac -cp ".;lib/*" -d webapp/WEB-INF/classes src/com/helpdesk/util/*.java src/com/helpdesk/model/*.java src/com/helpdesk/dao/*.java src/com/helpdesk/servlet/*.java src/com/helpdesk/*.java
if %errorlevel% equ 0 (
    java -cp ".;webapp/WEB-INF/classes;lib/*" com.helpdesk.Main
) else (
    echo [ERROR] Compilation failed!
    pause
)
