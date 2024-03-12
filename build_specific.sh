#!/bin/bash

# Define the resources directory
resources_directory="src/main/resources"

# Define class list
class_list="ClassList"

# Define the name for the JAR file
jar_file="ESP32PartitionTool.jar"

# Check if the "tool" folder exists, then remove it
if [ -d "tool" ]; then
    rm -rf "tool" || { echo "Error removing 'tool' folder"; exit 1; }
fi

# Create necessary directory
mkdir -p "bin" || { echo "Error creating 'bin' directory"; exit 1; }
mkdir -p "build"  || { echo "Error creating 'build' directory"; exit 1; }

# Find all Java files and export the list in a ClassList file
find src/main/java/com -name "*.java" > "$class_list" || { echo "Error finding Java files"; exit 1; }

# Compile java classes
javac -Xlint -cp "dependencies/*" -d "bin" @"$class_list" || { echo "Error compiling Java classes"; exit 1; }

# Copy resources to bin directory 
cp -r "$resources_directory"/* "bin/" || { echo "Error copying resources"; exit 1; }

# Create the JAR file with the manifest entries defaulting to Arduino IDE implementation
jar cfe "build/$jar_file" com.serifpersia.esp32partitiontool.ESP32PartitionTool -C "bin" . || { echo "Error creating JAR file"; exit 1; }

# User choice loop
while true; do
    read -p "Enter your choice (0 for Arduino IDE, 1 for PlatformIO): " choice

    if [ "$choice" -eq 0 ]; then
        # Arduino IDE choice
        mkdir -p "tool/ESP32PartitionTool/tool" || { echo "Error creating directory for Arduino IDE"; exit 1; }
        # Move the JAR file to the tool folder
        cp "build/$jar_file" "tool/ESP32PartitionTool/tool/$jar_file" || { echo "Error copying JAR file for Arduino IDE"; exit 1; }
        break
    elif [ "$choice" -eq 1 ]; then
        # PlatformIO choice
        mkdir -p "tool/tool-esp32partitiontool" || { echo "Error creating directory for PlatformIO"; exit 1; }
        cp "build/$jar_file" "tool/tool-esp32partitiontool/$jar_file" || { echo "Error copying JAR file for PlatformIO"; exit 1; }
        cp "esp32partitiontool.py" "tool/tool-esp32partitiontool" || { echo "Error copying Python file for PlatformIO"; exit 1; }
        cp "package.json" "tool/tool-esp32partitiontool/" || { echo "Error copying package.json file for PlatformIO"; exit 1; }
        break
    else
        echo "Invalid choice. Please enter 0 or 1."
    fi
done

# Clean up - Delete bin folder and ClassList file
rm -rf "bin" || { echo "Error removing 'bin' folder"; exit 1; }
rm -rf "build" || { echo "Error removing 'build' folder"; exit 1; }
rm "$class_list" || { echo "Error removing ClassList file"; exit 1; }
