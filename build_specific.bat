@echo off
setlocal

REM Define the resources directory
set "resources_directory=src\main\resources"

REM Define class list
set "class_list=ClassList"

REM Define the name for the JAR file
set "jar_file=ESP32PartitionTool.jar"

REM Check if the "tool" folder exists, then remove it
if exist "tool" (
    rmdir /s /q "tool" || (
        echo Error removing 'tool' folder
        exit /b 1
    )
)

REM Create necessary directory
mkdir "bin" 2>nul || (
    echo Error creating 'bin' directory
    exit /b 1
)
mkdir "build" 2>nul || (
    echo Error creating 'build' directory
    exit /b 1
)

REM Find all Java files and export the list in a ClassList file
dir /b /s "src\main\java\com\*.java" > "%class_list%" || (
    echo Error finding Java files
    exit /b 1
)

REM Compile java classes
javac -Xlint -cp "dependencies\*" -d "bin" @"%class_list%" || (
    echo Error compiling Java classes
    exit /b 1
)

REM Copy resources to bin directory
xcopy /s /y "%resources_directory%\*" "bin\" || (
    echo Error copying resources
    exit /b 1
)

REM Create the JAR file with the manifest entries defaulting to Arduino IDE implementation
jar cfe "build\%jar_file%" com.serifpersia.esp32partitiontool.ESP32PartitionTool -C "bin" . || (
    echo Error creating JAR file
    exit /b 1
)

:UserChoice
set /p "choice=Enter your choice (0 for Arduino IDE, 1 for PlatformIO): "

if "%choice%"=="0" (
    REM Arduino IDE choice
    mkdir "tool\ESP32PartitionTool\tool" 2>nul || (
        echo Error creating directory for Arduino IDE
        exit /b 1
    )
    REM Move the JAR file to the tool folder
    copy "build\%jar_file%" "tool\ESP32PartitionTool\tool\%jar_file%" || (
        echo Error copying JAR file for Arduino IDE
        exit /b 1
    )
) else if "%choice%"=="1" (
    REM PlatformIO choice
    mkdir "tool\tool-esp32partitiontool" 2>nul || (
        echo Error creating directory for PlatformIO
        exit /b 1
    )
    copy "build\%jar_file%" "tool\tool-esp32partitiontool\%jar_file%" || (
        echo Error copying JAR file for PlatformIO
        exit /b 1
    )
    copy "esp32partitiontool.py" "tool\tool-esp32partitiontool" || (
        echo Error copying Python file for PlatformIO
        exit /b 1
    )
    copy "package.json" "tool\tool-esp32partitiontool\" || (
        echo Error copying package.json file for PlatformIO
        exit /b 1
    )
) else (
    echo Invalid choice. Please enter 0 or 1.
    goto :UserChoice
)

REM Clean up - Delete bin folder and ClassList file
rmdir /s /q "bin" || (
    echo Error removing 'bin' folder
    exit /b 1
)
rmdir /s /q "build" || (
    echo Error removing 'build' folder
    exit /b 1
)
del "%class_list%" || (
    echo Error removing ClassList file
    exit /b 1
)

endlocal
