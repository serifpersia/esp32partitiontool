@echo off

rem Get the current directory of the script
set "script_dir=%~dp0"

rem Define the location of folders
set "source_folder=%script_dir%source"
set "dependencies_folder=%script_dir%dependencies"
set "bin_folder=%script_dir%bin"
set "tool_folder=%script_dir%tool"

rem Create bin folder if it doesn't exist
if not exist "%bin_folder%" mkdir "%bin_folder%"

rem Change directory to the source folder
cd /d "%source_folder%"

rem Compile Java files
javac -cp "%dependencies_folder%\pde.jar;%dependencies_folder%\arduino-core.jar" -d "%bin_folder%" *.java

rem Create the tool directory if it doesn't exist
if not exist "%tool_folder%" mkdir "%tool_folder%"

rem Find the main class file
for /r "%bin_folder%" %%i in (*.class) do (
    set "class_file=%%~ni"
    goto :done
)
:done

rem Extract package name from the class file
for /f "tokens=1,* delims= " %%a in ("%class_file%") do (
    set "package_name=%%a"
    goto :packagefound
)
:packagefound

rem Create output directory structure
set "output_dir=%tool_folder%\%package_name%\tool"
if not exist "%output_dir%" mkdir "%output_dir%"

rem Package class files into a JAR file in the tool directory
jar cvf "%output_dir%\%class_file%.jar" -C "%bin_folder%" .

rem Delete the bin folder
rmdir /s /q "%bin_folder%"

pause
