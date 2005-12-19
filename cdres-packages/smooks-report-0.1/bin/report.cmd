@echo off

set JAVA_HOME=\j2sdk1.4.2_03

set ENCODING=ISO-8859-1
set RECURSIVE=-r
set OUTFOLDER=\zap
set BASEURL=http://localhost:8080/smooksreport

@rem Check for JAVA_HOME.
if not exist "%JAVA_HOME%" (@echo. && @echo. && @echo Sorry, JAVA_HOME environment variable must be set in "%~dp0env.cmd"!! && goto end)

set SMOOKS_HOME=%~dp0..
rem set LIB=%SMOOKS_HOME%/lib
set LIB=H:\projects\codehaus\smooks\build

set CLASSPATH=%LIB%/milyn-smooks-0.5.jar;%LIB%/xercesImpl.jar;%LIB%/spring-1.1.2-core.jar;%LIB%/commons-logging1.0.4.jar;%LIB%/bsh-core-2.0b4.jar
if exist "%SMOOKS_HOME%/classes" (set CLASSPATH=%SMOOKS_HOME%/classes;%CLASSPATH%)

%JAVA_HOME%\bin\java -DSMOOKS_HOME=%SMOOKS_HOME% org.milyn.report.SmooksReportGenerator -e %ENCODING% %RECURSIVE% -d %OUTFOLDER% -b %BASEURL% %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
