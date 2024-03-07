<div align="center">
    <img src="https://img.shields.io/badge/ESP32-f8a631" height="50" style="pointer-events: none;">
    <img src="https://img.shields.io/badge/Partition-bf457a" height="50" style="pointer-events: none;">
    <img src="https://img.shields.io/badge/Tool-42b0f5" height="50" style="pointer-events: none;">
    <a href="https://github.com/serifpersia/esp32partitiontool/releases/latest"><img src="https://img.shields.io/badge/v1.3-9a41c2" height="50"></a>
</div>

## Overview
The ESP32 Partition Tool is a utility designed to facilitate creating custom partition schemes in the Arduino IDE 1.8.X environment. This tool aims to simplify the process of creating custom partition schemes for ESP32 projects.
## Screenshots
![Screenshot 1](https://github.com/serifpersia/esp32partitiontool/assets/62844718/8724d57c-ebb5-404f-97f1-fe09134f53b5)
![Screenshot 2](https://github.com/serifpersia/esp32partitiontool/assets/62844718/2cdfec8b-710c-41e5-9e0c-50dbb52e4181)

## Functionality
- [x] Create partitions csv
- [x] Import custom partitions csv
- [x] Create partitions bin
- [x] Create & upload custom SPIFFS
- [x] Create & upload merged binary
      
## Download
 [![Release](https://img.shields.io/github/release/serifpersia/esp32partitiontool.svg?style=flat-square)](https://github.com/serifpersia/esp32partitiontool/releases)

## Build Requirements
To build the plugin, you need JDK 8 installed. Note that anything newer will cause the Arduino IDE to crash since its only supporting java class files compiled with JDK 8 version of Java.
## Installation
- Download the tool from releases or build it yourself.
- In your Arduino sketchbook directory, create tools directory if it doesn't exist yet.
- Unpack the tool into tools directory (the path will look like `<home_dir>/Arduino/tools/ESP32PartitionTool/tool/ESP32PartitionTool.jar`).
- Restart Arduino IDE
- 
## Usage
- Select Tools > ESP32 Partition Tool menu item.
- Customize partition scheme.
- Export the custom partitions CSV file to sketch directory.
- Select Tools > Partition Scheme & select `Huge App (3MB APP/NO OTA/1MB SPIFFS)`*this will tell Arduino IDE to use our custom partitions.csv file that's located in sketch directory(export csv via the the tool first).
- Compile the sketch.
- Flash Sketch from Arduino IDE.
- Other options such as SPIFFS create & upload as well as merged binary creation and upload are left to users discretion(Merge Binary button will genereate SPIFFS) 

## Issues and Contributions
Feel free to report any issues.
## License
This project is licensed under the [MIT License](LICENSE).
