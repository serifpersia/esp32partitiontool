#!/bin/bash

# Get the directory of the script
script_dir="$(dirname "$(readlink -f "$0")")"

# Define folder names
source_folder="$script_dir/src/main/java/com/serifpersia/esp32partitiontool"
dependencies_folder="$script_dir/dependencies"
bin_folder="$script_dir/bin"
tool_folder="$script_dir/tool"
resources_folder="$script_dir/resources"

# Create necessary folders
mkdir -p "$bin_folder" "$tool_folder" "$tool_folder/ESP32PartitionTool/tool"

# Compile Java files
javac -cp "$dependencies_folder/pde.jar:$dependencies_folder/arduino-core.jar:$dependencies_folder/commons-codec-1.7.jar" -d "$bin_folder" "$source_folder"/*.java

# Copy resources directory into the bin folder
cp -r "$resources_folder" "$bin_folder/resources"

# Package class files and resources into a JAR file
jar cvf "$tool_folder/ESP32PartitionTool/tool/ESP32PartitionTool.jar" -C "$bin_folder" .

# Remove bin folder
rm -rf "$bin_folder"
