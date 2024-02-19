#!/bin/bash

# Get the directory of the script
script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# Define the location of folders
source_folder="$script_dir/source"
dependencies_folder="$script_dir/dependencies"
bin_folder="$script_dir/bin"
tool_folder="$script_dir/tool"

# Create bin folder if it doesn't exist
mkdir -p "$bin_folder"

# Change directory to the source folder
cd "$source_folder" || exit

# Compile Java files
javac -cp "$dependencies_folder/pde.jar:$dependencies_folder/arduino-core.jar:$dependencies_folder/commons-codec-1.7.jar" -d "$bin_folder" *.java

# Create the tool directory if it doesn't exist
mkdir -p "$tool_folder"

# Find the main class file
class_file=$(find "$bin_folder" -name "*.class" -exec basename {} \; | head -n 1)

# Extract package name from the class file
package_name=$(echo "$class_file" | cut -d'.' -f1)

# Create output directory structure
output_dir="$tool_folder/$package_name/tool"
mkdir -p "$output_dir"

# Package class files into a JAR file in the tool directory
jar cvf "$output_dir/$class_file.jar" -C "$bin_folder" .

# Delete the bin folder
rm -rf "$bin_folder"

echo "JAR file created successfully."

