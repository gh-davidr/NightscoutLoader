@ECHO OFF
SETLOCAL enabledelayedexpansion

cd /D "%~dp0"

java -Xmx1024m -Xms128m -Djdk.tls.client.protocols=TLSv1.2 -jar NightScoutLoader.jar