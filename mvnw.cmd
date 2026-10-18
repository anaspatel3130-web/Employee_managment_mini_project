@echo off
set "SCRIPT_DIR=%~dp0"
if exist "%SCRIPT_DIR%apache-maven-3.9.6\bin\mvn.cmd" (
    call "%SCRIPT_DIR%apache-maven-3.9.6\bin\mvn.cmd" %*
) else (
    mvn %*
)
