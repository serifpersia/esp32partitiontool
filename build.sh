#!/bin/bash

# 1) build the project
. .github/scripts/build_jar.sh "build/ESP32PartitionTool.jar"
[ $? -eq 0 ] || die "Unable to build jar"

# 2) build the Arduino package
.github/scripts/create_arduino_package.sh $jar_file
[ $? -eq 0 ] || die "Arduino Package creation failed"

# 3) build the Platformio package
.github/scripts/create_platformio_package.sh $jar_file
[ $? -eq 0 ] || die "Platformio Package creation failed"