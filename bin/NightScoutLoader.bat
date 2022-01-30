@ECHO OFF
REM ---------------------------------------------------------------------------
REM 
REM Run script for launching the NightScoutLoader application
REM 
REM   1 Checks that jar file is correctly installed in folder
REM   2 Checks that the C:\Temp folder exists for log file
REM   3 Ensures that correct parameters are passed to runtime JVM
REM
REM ---------------------------------------------------------------------------
SETLOCAL enabledelayedexpansion

REM 
REM Set some variables up
REM
SET jarfile=NightScoutLoader.jar
SET tmpdir=C:\Temp
SET msgdir=%temp%

REM
REM Ensure that the script runs from the same directory the script resides in
REM The assumption is that both the BAT script and the JAR file are in the same
REM directory.
REM
cd /D "%~dp0"

REM ---------------------------------------------------------------------------
REM
REM Do some basic checks and alert if needed
REM
REM ---------------------------------------------------------------------------

REM
REM Java file needs to be present
REM
if NOT EXIST %jarfile% (
    CALL :displayMessage CRITICAL "NightScoutLoader Jar file needs to be present as '%jarfile%'" "" "Did the download append '(1)' to the filename perhaps?" "" "If so, please rename the Jar file to '%jarfile%' in directory %~dp0"
    SET ERRORLEVEL=1
    EXIT /B %ERRORLEVEL%
)

REM
REM NightScoutLoader logs to %tmpdir% so ensure it exists too
REM
if NOT EXIST %tmpdir% (
    md %tmpdir%
    if NOT EXIST %tmpdir% (
        CALL :displayMessage EXCLAMATION "NightScoutLoader relies on %tmpdir% by default for its log file" "" "However, you don't have permission to create this folder" "" "Use elevated permissions to create manually"
        SET ERRORLEVEL=1
        EXIT /B %ERRORLEVEL% 
    )
    CALL :displayMessage INFORMATION "Successfully created %tmpdir% for NightScoutLoader log file" "" "Select OK to continue"
)

REM
REM Invoke NightScoutLoader with options to increase memory and also with the TLS option set
REM TLS is needed to successfully connect to Mongo Atlas on Windows
REM
java -Xmx1024m -Xms128m -Djdk.tls.client.protocols=TLSv1.2 -jar %jarfile%
EXIT /B %ERRORLEVEL%

REM ---------------------------------------------------------------------------
REM
REM Display Message function for notifications
REM 
REM   %~1 CRITICAL, QUESTION, EXCLAMATION or INFORMATION - sets msgbox symbol
REM   %~2 First line message
REM   %~2 Second line message
REM    ...
REM   %~2 Fifth line message
REM ---------------------------------------------------------------------------
:displayMessage
    SET symbol=""
	SET header=""
	
	if "%~1"=="CRITICAL" (
	    SET symbol=, vbCritical    + vbOKOnly
		SET header="SETUP ERROR:"
	)
	if "%~1"=="QUESTION" (
	    SET symbol=, vbQuestion    + vbOKOnly
		SET header=""
	)
	if "%~1"=="EXCLAMATION" (
	    SET symbol=, vbExclamation + vbOKOnly
		SET header="SETUP ERROR:"
	)
	if "%~1"=="INFORMATION" (
	    SET symbol=, vbInformation + vbOKOnly
		SET header="Please Note:"
	)
	shift

    IF NOT "%~5"=="" goto fiveLines
    IF NOT "%~4"=="" goto fourLines
    IF NOT "%~3"=="" goto threeLines
    IF NOT "%~2"=="" goto twoLines
    IF NOT "%~1"=="" goto oneLine

:oneLine
    echo MSGBOX %header% ^& vbCrLf ^& vbCrLf ^& "%~1" %symbol% > %msgdir%\TEMPmessage.vbs
    goto runVB
:twoLines
    echo MSGBOX %header% ^& vbCrLf ^& vbCrLf ^& "%~1" ^& vbCrLf ^& "%~2" %symbol% > %msgdir%\TEMPmessage.vbs
    goto runVB
:threeLines
    echo MSGBOX %header% ^& vbCrLf ^& vbCrLf ^& "%~1" ^& vbCrLf ^& "%~2" ^& vbCrLf ^& "%~3" %symbol% > %msgdir%\TEMPmessage.vbs
    goto runVB
:fourLines
    echo MSGBOX %header% ^& vbCrLf ^& vbCrLf ^& "%~1" ^& vbCrLf ^& "%~2" ^& vbCrLf ^& "%~3" ^& vbCrLf ^& "%~4" %symbol% > %msgdir%\TEMPmessage.vbs
    goto runVB
:fiveLines
    echo MSGBOX %header% ^& vbCrLf ^& vbCrLf ^& "%~1" ^& vbCrLf ^& "%~2" ^& vbCrLf ^& "%~3" ^& vbCrLf ^& "%~4" ^& vbCrLf ^& "%~5" %symbol% > %msgdir%\TEMPmessage.vbs
    goto runVB

:runVB
    call %msgdir%\TEMPmessage.vbs
    del  %msgdir%\TEMPmessage.vbs /f /q
    EXIT /B 0