@echo off
echo ========================================================
echo  Launching Terminal IT Helpdesk and Incident System
echo ========================================================
javac -cp ".;lib/*" *.java
if %errorlevel% equ 0 (
    java -cp ".;lib/*" Main
) else (
    echo [ERROR] Compilation failed!
    pause
)
