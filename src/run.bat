@echo off
call cleanup.bat
javac *.java
start "" java UDPLoggerServer 12344
echo " Waiting for logger server to start ... "
timeout /t 2
start /B java Coordinator 12345 12344 4 500 A B
start cmd /k java Participant 12345 12344 12346 500
start cmd /k java Participant 12345 12344 12347 500
start cmd /k java Participant 12345 12344 12348 500
start cmd /k java Participant 12345 12344 12349 500