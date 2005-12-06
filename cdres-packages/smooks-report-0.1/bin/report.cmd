@echo off

set JAVA_HOME=\j2sdk1.4.2_03

 -e [encoding] -r -d [output-folder] -b [base-URL] -s [start-page]

@rem Check for JAVA_HOME.
if not exist "%JAVA_HOME%" (@echo. && @echo. && @echo Sorry, JAVA_HOME environment variable must be set in "%~dp0env.cmd"!! && goto end)

set SMOOKS_HOME=%~dp0..
rem set LIB=%SMOOKS_HOME%/lib
set LIB=H:\projects\codehaus\smooks\build

set CLASSPATH=%LIB%/milyn-smooks-0.5.jar;%LIB%/xercesImpl.jar;%SMOOKS_HOME%/lib/batik-css.jar;%LIB%/spring-1.1.2-core.jar;%LIB%/commons-logging1.0.4.jar
if exist "%SMOOKS_HOME%/classes" (set CLASSPATH=%SMOOKS_HOME%/classes;%CLASSPATH%)

%JAVA_HOME%\bin\java -DSMOOKS_HOME=%SMOOKS_HOME% org.milyn.report.SmooksReportGenerator -r %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
