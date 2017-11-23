@echo off

:: define path of your JDK/JRE (must be 1.8 or older)
set JAVA_HOME_PATH="C:\Program Files\Java\jdk1.8.0_131"

:: select level
set LEVEL=world\test1_windengine.xml
::set LEVEL=world\test2_beach.xml

%JAVA_HOME_PATH%\bin\java -jar CompactCarRace.jar %LEVEL%
