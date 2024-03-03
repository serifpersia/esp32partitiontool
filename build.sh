#!/bin/bash

# Get the directory of the script
script_dir="$(dirname "$(readlink -f "$0")")"

# Define folder names
source_folder="$script_dir/src/main/java/com/serifpersia/esp32partitiontool"
deps_dir="$script_dir/dependencies"
bin_folder="$script_dir/bin"
tool_folder="$script_dir/tool"

# Create necessary folders
mkdir -p "$bin_folder" "$tool_folder" "$tool_folder/ESP32PartitionTool/tool"

# Compile Java files
if javac -cp "$deps_dir/pde.jar:$deps_dir/arduino-core.jar:$deps_dir/commons-codec-1.7.jar:$script_dir/resources/*" -d "$bin_folder" "$source_folder"/*.java -Xlint:deprecation; then
  echo "Files compiled successfully"
else
  exit 1
fi


# Package class files into a JAR file
if jar cvf "$tool_folder/ESP32PartitionTool/tool/ESP32PartitionTool.jar" -C "$bin_folder" . ; then
  echo "Package created successfully"
else
  exit 1
fi

# Remove bin folder
rm -rf "$bin_folder"
