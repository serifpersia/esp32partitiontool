#!/bin/bash

die () {
  echo ""
  printf " ❌ ${1}"
  echo ""
  exit 1
}

jar_file=$1
jar_dir=`dirname $jar_file`

# Note: Deliberately using relative paths since this will be run by the CI
resources_folder="src/main/resources"
# Temporary storage for java file list
class_list="ClassList"
# Create necessary folders
mkdir -p "bin" "$jar_dir" || die "Unable to create the required folders"
# Find all Java files and export the list in a ClassList file
find src/main/java/com -name "*.java" > $class_list
# Maximum linting
javac -Xlint -cp "dependencies/*" -d "bin" @$class_list || die "Unable compile project"
# Copy resources directory contents into the bin folder
cp -r "$resources_folder"/* "bin/" || die "Unable to copy resources directory into bin folder"
# Create the JAR file with the manifest entries defaulting to Arduino IDE implementation
jar cfe "$jar_file" com.serifpersia.esp32partitiontool.ESP32PartitionTool -C "bin" . || die "Unable to create jar file"
# An additional check doesn't hurt :)
[[ -f "$jar_file" ]] || die "Jar file not found: $jar_file"
# yay!
echo "Successfully created $jar_file"
# Remove the temporary bin folder
rm -rf "bin"
# Remove the ClassList file
rm $class_list

