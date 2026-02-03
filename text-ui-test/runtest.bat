@ECHO OFF

REM create bin directory if it doesn't exist
if not exist ..\bin mkdir ..\bin

REM delete output from previous run
if exist ACTUAL.TXT del ACTUAL.TXT

REM delete persisted data from previous run (so tests are deterministic)
if exist data\kraken.txt del data\kraken.txt

REM compile the code into the bin folder
dir /s /b ..\src\main\java\*.java > sources.txt
javac  -cp ..\src\main\java -Xlint:none -d ..\bin @sources.txt
IF ERRORLEVEL 1 (
    del sources.txt
    echo ********** BUILD FAILURE **********
    exit /b 1
)
del sources.txt
REM no error here, errorlevel == 0

REM run the program, feed commands from input.txt file and redirect the output to the ACTUAL.TXT
java -classpath ..\bin kraken.Kraken < input.txt > ACTUAL.TXT

REM compare the output to the expected output
FC ACTUAL.TXT EXPECTED.TXT
