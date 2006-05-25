@echo off

setlocal

if "%JAVA_HOME%" NEQ "" goto setmaven
set JAVA_HOME=

:setmaven
if "%MAVEN_HOME%" NEQ "" goto checkenv
set MAVEN_HOME=

:checkenv
@rem Check for JAVA_HOME and MAVEN_HOME.
if "%JAVA_HOME%"=="" (@echo. && @echo. && @echo Sorry, JAVA_HOME environment variable must be set in "%~dp0env.cmd"!! && goto fail)
if "%MAVEN_HOME%"=="" (@echo. && @echo. && @echo Sorry, MAVEN_HOME environment variable must be set in "%~dp0env.cmd"!! && goto fail)

set MAVEN_OPTS=-Xmx256m -XX:MaxPermSize=256m

call %MAVEN_HOME%/bin/maven %1 %2 %3 %4 %5 %6 %7 %8 %9

endlocal