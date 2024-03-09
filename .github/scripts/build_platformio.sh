#!/bin/bash


die () {
  echo ""
  printf " ❌ ${1}"
  echo ""
  exit 1
}

# Note: Deliberately using relative paths since this will be run by the CI
source_folder="src/main/java/com/serifpersia/esp32partitiontool"
bin_folder="bin"
output_dir="tools" # comply with platformio
resources_folder="src/main/resources"
class_list="ClassList"

# Create necessary folders
mkdir -p "$bin_folder" "$output_dir" || die "Unable to create the required folders"

# Find Java files and exclude InputArduino.java
find src/main/java/com/serifpersia/esp32partitiontool -type f \( ! -iname "InputArduino.java" \) > $class_list
javac -Xlint -d "$bin_folder" @$class_list

# Copy resources directory contents into the bin folder
cp -r "$resources_folder"/* "$bin_folder/" || die "Unable to copy resources directory into bin folder"

# Create the JAR file with the manifest entries
jar cfe "$output_dir/ESP32PartitionTool.jar" com.serifpersia.esp32partitiontool.ESP32PartitionTool -C "$bin_folder" . || die "Unable to create jar file"

# Remove the temporary bin folder
rm -rf "$bin_folder"

# append date to folder name
foldername="esp32partitiontool-$(date +"%F")"

mkdir -p tmp/$foldername
cp "$output_dir/ESP32PartitionTool.jar" tmp/$foldername/
cp "package.json" tmp/$foldername/
cd tmp
zip -rq ../esp32partitiontool-platformio.zip .
cd ..
rm -Rf tmp

# java -jar "$output_dir/ESP32PartitionTool.jar" | exit 1


