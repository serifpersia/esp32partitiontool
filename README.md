# ESP32 Partition Tool

Arduino IDE 1.8.X Partition Utility

**Currently in WIP state**

## Overview

The ESP32 Partition Tool is a utility designed to facilitate creating custom partition scheme in the Arduino IDE 1.8.X environment. This tool aims to simplify the process of creating custom partition schemes for ESP32 projects.

## Build Requirements

To build the plugin, you need JDK 8 installed. Note that anything newer will cause the Arduino IDE to crash since its only supporting java class files compiled with JDK 8 version of Java.

## Screenshots

![Screenshot 1](https://github.com/serifpersia/esp32partitiontool/assets/62844718/8724d57c-ebb5-404f-97f1-fe09134f53b5)
![Screenshot 2](https://github.com/serifpersia/esp32partitiontool/assets/62844718/73ab96b1-6b65-40d0-9274-9762a92da0d5)


## Functionality

- [x] CSV Export
- [ ] Create SPIFFS
- [ ] Auto Merge & Flash at 0x0 Offset
- [ ] Public Tool Release

## Usage

1. Clone the repository.
2. Build the tool using JDK 8.
3. Install the plugin in your Arduino IDE.
4. Access partitioning options through the IDE interface.

## Issues and Contributions

Feel free to report any issues or contribute to the development of this tool. Pull requests are welcome!

## License

This project is licensed under the [MIT License](LICENSE).
