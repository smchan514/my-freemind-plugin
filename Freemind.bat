@echo off
REM Use the default JRE
SET JRE=javaw

REM Nominal class path
SET CLASSPATH=lib\freemind.jar;lib\commons-lang-2.0.jar;lib\forms-1.0.5.jar;lib\jibx\jibx-run.jar;lib\jibx\xpp3.jar;lib\bindings.jar;lib\xalan.jar;lib\serializer.jar;lib\xml-apis.jar;lib\xercesImpl.jar;lib\jortho.jar

REM Nominal main class
SET MAIN_CLASS=freemind.main.FreeMindStarter

REM Enable remote debugging
REM SET DEBUG_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000

REM Optional support for FlatLaf
SET CLASSPATH=lib\freemind-overrides.jar;lib\flatlaf-3.5.2.jar;%CLASSPATH%
REM SET MAIN_CLASS=freemind.main.FreeMindPreStarter

REM Start the process!
start %JRE% %DEBUG_OPTS% -Xmx256M -Xss8M -cp %CLASSPATH% %MAIN_CLASS%
