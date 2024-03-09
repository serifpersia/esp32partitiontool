#!/bin/bash

# Get the directory of the script
script_dir="$(dirname "$(readlink -f "$0")")"

# Define folder names
source_folder="$script_dir/src/main/java/com/serifpersia/esp32partitiontool"
bin_folder="$script_dir/bin"
output_dir="$script_dir/output"
resources_folder="$script_dir/src/main/resources"

# Create necessary folders
mkdir -p "$bin_folder" "$output_dir"

# Compile Java files
javac -d "$bin_folder" "$source_folder"/*.java

# Copy resources directory contents into the bin folder
cp -r "$resources_folder"/* "$bin_folder/"

# Create the JAR file with the manifest entries
jar cfe "$output_dir/ESP32PartitionTool.jar" com.serifpersia.esp32partitiontool.ESP32PartitionTool -C "$bin_folder" .

# Remove the temporary bin folder
rm -rf "$bin_folder"
