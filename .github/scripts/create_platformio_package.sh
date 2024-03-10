#!/bin/bash

jar_file=$1

[[ -f "$jar_file" ]] || exit 1

# platformio requirement: append date to folder name
tmp_dir="tmp/esp32partitiontool-$(date +"%F")"
archive_file="esp32partitiontool-platformio.zip"
# cleanup from previous build
rm -Rf "$tmp_dir"
mkdir -p "$tmp_dir"
cp "$jar_file" "$tmp_dir/ESP32PartitionTool.jar" || exit 1
# TODO: raise package.version in json
cp "package.json" $tmp_dir/
cp "esp32partitiontool.py" $tmp_dir/
cd tmp
rm -f ../$archive_file
zip -rq ../$archive_file . || exit 1
cd ..
rm -Rf tmp

[[ -f "$archive_file" ]] || exit 1

echo "Platformio Package: $archive_file"
