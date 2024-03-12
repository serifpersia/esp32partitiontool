#!/bin/bash

jar_file=$1

[[ -f "$jar_file" ]] || exit 1

archive_file="ESP32PartitionTool-Arduino.zip"
tmp_dir="tmp/ESP32PartitionTool/tool"
# cleanup from previous build
rm -Rf "$tmp_dir"
mkdir -p "$tmp_dir"
cp "$jar_file" "$tmp_dir/ESP32PartitionTool.jar" || exit 1
cd tmp
rm -f ../$archive_file
zip -rq ../$archive_file . || exit 1
cd ..
rm -Rf tmp

[[ -f "$archive_file" ]] || exit 1

echo "Arduino Package: $archive_file"
