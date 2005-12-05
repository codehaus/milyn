@echo off

setlocal

set TERMINATE=NO

call env.cmd
if "%TERMINATE%"=="YES" goto end

if "%1"=="" (call %ANT_HOME%\bin\ant -projecthelp && goto end)
if "%1"=="?" (call %ANT_HOME%\bin\ant -projecthelp && goto end)
if "%1"=="-?" (call %ANT_HOME%\bin\ant -projecthelp && goto end)
if "%1"=="/?" (call %ANT_HOME%\bin\ant -projecthelp && goto end)

call %ANT_HOME%\bin\ant %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
endlocal