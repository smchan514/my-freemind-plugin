@echo off
SET JAVA_BIN=..\Java\jdk8u352-b08\bin\javaw
start %JAVA_BIN% -Djava.security.manager -Djava.security.policy=freemind.java.policy -Xmx256M -Xss8M -cp lib\freemind-overrides-20231227.jar;lib\freemind.jar;lib\commons-lang-2.0.jar;lib\forms-1.0.5.jar;lib\jibx\jibx-run.jar;lib\jibx\xpp3.jar;lib\bindings.jar;lib\xalan.jar;lib\serializer.jar;lib\xml-apis.jar;lib\xercesImpl.jar;lib\jortho.jar freemind.main.FreeMindStarter
