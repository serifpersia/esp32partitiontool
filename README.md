<div align="center">

![logo-icon](https://github.com/user-attachments/assets/1463c312-1e81-4107-93be-0570c1b52532)
    
</div>


<div align="center">
    <img src="https://img.shields.io/badge/ESP32-f8a631" height="50">
    <img src="https://img.shields.io/badge/Partition-bf457a" height="50">
    <img src="https://img.shields.io/badge/Tool-42b0f5" height="50">
    <a href="https://github.com/serifpersia/esp32partitiontool/releases/latest"><img src="https://img.shields.io/badge/v1.4-9a41c2" height="50"></a>
</div>

## Overview
The ESP32 Partition Tool is a utility designed to facilitate creating custom partition schemes in Arduino IDE 1.8.x & PlatformIO.

This tool aims to simplify the process of creating custom partition schemes for ESP32 projects.
## Screenshots
![Screenshot 1](https://github.com/serifpersia/esp32partitiontool/assets/62844718/8724d57c-ebb5-404f-97f1-fe09134f53b5)
![image](https://github.com/serifpersia/esp32partitiontool/assets/62844718/bdb4562d-91ac-4348-9de7-be7a20935240)
![Screenshot 2024-03-23 121910](https://github.com/serifpersia/esp32partitiontool/assets/62844718/a79bfaea-902d-471a-a1b1-d936c660284c)

## Functionality

- [x] Create partitions csv
- [x] Import custom partitions csv
- [x] Export custom partitions csv
- [x] Create & upload custom SPIFFS image(SPIFFS, LittleFS or FATFS)
- [x] Create & upload merged binary(Serial ports only)

## Download
 [![Release](https://img.shields.io/github/release/serifpersia/esp32partitiontool.svg?style=flat-square)](https://github.com/serifpersia/esp32partitiontool/releases)

# Build Instructions

## Build Requirements
Ensure you have the following installed:
- **Java JDK 8**: Required for compiling Java source files.
- **Python 3.6+**: Required for running the build script.

## Building the Tool

1. **Clone the Repository**:
   ```sh
   git clone https://github.com/serifpersia/esp32partitiontool.git
   cd esp32partitiontool
## Run the Build Script

- On **Unix-based systems** (Linux, macOS):
  ```
    python3 build_tool.py
- On **Windows**:
  ```
    python build_tool.py


## Installation (Arduino IDE)
- Download the tool from releases or build it yourself.
- In your Arduino sketchbook directory, create tools directory if it doesn't exist yet.
- Unpack the tool into tools directory (the path will look like `<home_dir>/Arduino/tools/ESP32PartitionTool/tool/ESP32PartitionTool.jar`).
- Restart Arduino IDE

## Usage (Arduino IDE)
- Select Tools > ESP32 Partition Tool menu item.
- Customize partition scheme.
- Export the custom partitions CSV file to sketch directory.
- Select Tools > Partition Scheme & select `Huge App (3MB APP/NO OTA/1MB SPIFFS)`*this will tell Arduino IDE to use our custom partitions.csv file that's located in sketch directory(export csv via the the tool first).
- Close and open the tool to automatically load csv located at the sketch directory or load csv manually with import csv button.
- Configure Flash size and use the uploading buttons. Upload SPIFFS for filesystem spiffs binary, Upload Merge for uploading all binaries or Merge binary for just creating the merge binary file.
* Merged binary can only be uploaded to ESP32 boards via serial ports, OTA uploading is not supported, spiffs upload is supported for uploading over OTA.

## Installation (Platformio)

- Install Java and make sure the executable is in the path
- Create a `partition_manager.py` in your project folder

#### partition_manager.py

```python
Import('env')
import os.path
import sys

# add esp32partitiontool to path
sys.path.append(os.path.abspath( env.PioPlatform().get_package_dir("tool-esp32partitiontool") ))
# import module
from esp32partitiontool import *
# run module
load_pm(env)
```

- To launch the GUI, both `extra_scripts` and `platform_packages` entries must be set

#### platformio.ini

```ini
[platformio]
src_dir = src
default_envs = hello-world

[env]
framework = arduino
platform = espressif32
board = esp32dev
; register the "tool-esp32partitiontool"
; remove url if you have own built tool in pacakges directory of platformio core directory
platform_packages = tool-esp32partitiontool @ https://github.com/serifpersia/esp32partitiontool/releases/download/v1.4.4/esp32partitiontool-platformio.zip
; register the "edit_partition" target
extra_scripts = partition_manager.py

[env:hello-world]
board_build.partitions = partitions/default.csv
board_upload.flash_size = 4MB
upload_speed = 1500000


```

- If building the plugin from source, remove the URL from the `platform_packages` entry ...

```ini
platform_packages      = tool-esp32partitiontool
```

- ... and use `pio pkg` to install the plugin from the root of your platformio project:

```shell
pio pkg install -e build-partition --no-save --tool /path/to/esp32partitiontool/esp32partitiontool-platformio.zip --force
```


## Usage (Platformio)

- If building from vsCode: run  ESP32 Partition Tool Task
- If building from a shell : `pio run -t edit_partition -e your_environment`



## Issues and Contributions
Feel free to report any [issues](https://github.com/serifpersia/esp32partitiontool/issues). Translation [contributions are welcome](https://github.com/serifpersia/esp32partitiontool/tree/main/src/main/resources/l10n) if you find the current translations to be wrong, not adequate or you want to add support for another language.

## License
This project is licensed under the [MIT License](LICENSE).


