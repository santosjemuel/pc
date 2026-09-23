@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-27"
if not exist "%JAVA_HOME%\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_161"
)
set "PATH=%JAVA_HOME%\bin;j:\Coding\pc\tools\apache-maven-3.9.9\bin;%PATH%"

call mvn %*
