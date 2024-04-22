@echo off
REM SET DEBUG_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000
start javaw %DEBUG_OPTS% -Xmx256M -Xss8M -cp lib\freemind-overrides-20240422.jar;lib\freemind.jar;lib\commons-lang-2.0.jar;lib\forms-1.0.5.jar;lib\jibx\jibx-run.jar;lib\jibx\xpp3.jar;lib\bindings.jar;lib\xalan.jar;lib\serializer.jar;lib\xml-apis.jar;lib\xercesImpl.jar;lib\jortho.jar freemind.main.FreeMindStarter
