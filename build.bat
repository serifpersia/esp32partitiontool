@echo off
setlocal

set "script_dir=%~dp0"
set "source_folder=%script_dir%src/main/java/com/serifpersia/esp32partitiontool"
set "bin_folder=%script_dir%bin"
set "output_dir=%script_dir%output"
set "resources_folder=%script_dir%src/main/resources"

mkdir "%bin_folder%" 2>nul
cd /d "%source_folder%" || exit /b
javac -d "%bin_folder%" *.java
mkdir "%output_dir%" 2>nul

REM Copy resources directory contents into the bin folder
xcopy /s /y "%resources_folder%" "%bin_folder%"

REM Create the JAR file with the manifest entries
jar cfe "%output_dir%\ESP32PartitionTool.jar" com.serifpersia.esp32partitiontool.ESP32PartitionTool -C "%bin_folder%" .

REM Remove the temporary bin folder
rmdir /s /q "%bin_folder%"

pause
endlocal
