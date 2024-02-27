# ESP32 Partition Tool
Arduino IDE 1.8.X Partition Utility
## Overview
The ESP32 Partition Tool is a utility designed to facilitate creating custom partition schemes in the Arduino IDE 1.8.X environment. This tool aims to simplify the process of creating custom partition schemes for ESP32 projects.
## Screenshots
![Screenshot 1](https://github.com/serifpersia/esp32partitiontool/assets/62844718/8724d57c-ebb5-404f-97f1-fe09134f53b5)
![Screenshot 2](https://github.com/serifpersia/esp32partitiontool/assets/62844718/43414d8d-c9a3-4e61-8009-e1630c44f8b5)
## Functionality
- [x] Create partitions csv
- [x] Import custom partitions csv
- [x] Create partitions bin
- [x] Create & flash custom SPIFFS
- [x] Flash sketch
- [x] Create merged bin & flash merged bin at 0x0 offset
- [ ] Public tool release
## Build Requirements
To build the plugin, you need JDK 8 installed. Note that anything newer will cause the Arduino IDE to crash since its only supporting java class files compiled with JDK 8 version of Java.
## Installation
- Download the tool from releases or build it yourself.
- In your Arduino sketchbook directory, create tools directory if it doesn't exist yet.
- Unpack the tool into tools directory (the path will look like `<home_dir>/Arduino/tools/ESP32PartitionTool/tool/ESP32PartitionTool.jar`).
- Restart Arduino IDE
## Usage
- Select Tools > ESP32 Partition Tool menu item.
- Customize partition scheme.
- Export the custom partitions CSV file.
## Issues and Contributions
Feel free to report any issues.
## License
This project is licensed under the [MIT License](LICENSE).
