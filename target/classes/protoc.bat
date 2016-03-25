@echo off

echo ------------------------------------------------
rem ----------- settings ----------------
set base=%~dp0
rem echo 1. Now going into directory [%base%]
cd /d %base%
rem echo 2. Go into directory [%base%] successfully!
cd ..
rem .proto source
set src=proto\proto
rem .proto backup
set backup=proto\protobackup
rem .java destination
set dst=proto\java
rem ----------- settings ----------------

rem echo 3. Source directory is %src%
rem echo 4. Destination directory is %dst%

protoc.exe -I=%src% --java_out=%dst%  %src%\*.proto

move %src%\* %backup%\

rem echo 5. Compiled successfully!

echo ------------------------------------------------

pause
