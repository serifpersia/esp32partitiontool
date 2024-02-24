@echo off
setlocal

set "script_dir=%~dp0"
set "source_folder=%script_dir%src/main/java/com/serifpersia/esp32partitiontool"
set "dependencies_folder=%script_dir%dependencies"
set "bin_folder=%script_dir%bin"
set "tool_folder=%script_dir%tool"

mkdir "%bin_folder%" 2>nul
cd /d "%source_folder%" || exit /b
javac -cp "%dependencies_folder%\pde.jar;%dependencies_folder%\arduino-core.jar;%dependencies_folder%\commons-codec-1.7.jar" -d "%bin_folder%" *.java
mkdir "%tool_folder%" 2>nul
mkdir "%tool_folder%\ESP32PartitionTool\tool" 2>nul
jar cvf "%tool_folder%\ESP32PartitionTool\tool\ESP32PartitionTool.jar" -C "%bin_folder%" .
rmdir /s /q "%bin_folder%"

pause
endlocal
