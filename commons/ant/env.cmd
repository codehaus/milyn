@echo off

@rem ===================================================================================
@rem Check for top level env.cmd script.
@rem ===================================================================================
if exist %~dp0..\..\env.cmd (call %~dp0..\..\env.cmd && goto end)

@rem ===================================================================================
@rem The JAVA_HOME variable must be set for the environment.  Set it
@rem on the line below this comment.
@rem ===================================================================================
set JAVA_HOME=
set ANT_HOME=

@rem Check for JAVA_HOME and ANT_HOME.
if "%JAVA_HOME%"=="" (@echo. && @echo. && @echo Sorry, JAVA_HOME environment variable must be set in "%~dp0env.cmd"!! && goto fail)
if "%ANT_HOME%"=="" (@echo. && @echo. && @echo Sorry, ANT_HOME environment variable must be set in "%~dp0env.cmd"!! && goto fail)
if not exist "%ANT_HOME%\lib\junit.jar" (@echo. && @echo. && @echo Sorry, junit.jar not installed in "%ANT_HOME%\lib\"!! && goto fail)

@rem ===================================================================================
@rem Environment Setup....
@rem ===================================================================================
set PATH=%PATH%;%ANT_HOME%\bin

goto end

:fail
set TERMINATE=YES

:end