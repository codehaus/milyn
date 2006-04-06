@echo off

set JAVA_HOME=

set BASEURL=
set OUTFOLDER=

set ENCODING=ISO-8859-1
set RECURSIVE=-r
set BROWSERS=msie6w,firefox,opera7

@rem Check env...
if not exist "%JAVA_HOME%" (@echo. && @echo. && @echo Sorry, JAVA_HOME environment variable must be set in "%~dp0report.cmd"!! && goto end)
if not exist "%OUTFOLDER%" (@echo. && @echo. && @echo Sorry, OUTFOLDER environment variable must be set in "%~dp0report.cmd"!! && goto end)
if "%BASEURL%"=="" (@echo. && @echo. && @echo Sorry, BASEURL environment variable must be set in "%~dp0report.cmd"!! && goto end)
if "%BROWSERS%"=="" (@echo. && @echo. && @echo Sorry, BROWSERS environment variable must be set in "%~dp0report.cmd"!! && goto end)

set SMOOKS_HOME=%~dp0..
set LIB=%SMOOKS_HOME%/lib

set CLASSPATH=%LIB%/milyn-smooks-0.5.jar;%LIB%/xercesImpl.jar;%LIB%/spring-1.1.2-core.jar;%LIB%/commons-logging1.0.4.jar;%LIB%/bsh-core-2.0b4.jar
if exist "%SMOOKS_HOME%/classes" (set CLASSPATH=%SMOOKS_HOME%/classes;%CLASSPATH%)

%JAVA_HOME%\bin\java -DSMOOKS_HOME=%SMOOKS_HOME% org.milyn.report.SmooksReportGenerator -e %ENCODING% %RECURSIVE% -d %OUTFOLDER% -b %BASEURL% -l "%BROWSERS%" %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
