Import('env')
import os.path
import sys
from platformio import fs
from platformio import util
from platformio.compat import WINDOWS

# add esp32partitiontool to path

default_env = DefaultEnvironment()
platform    = default_env.PioPlatform()
package_dir = platform.get_package_dir("tool-esp32partitiontool")

assert os.path.isdir(package_dir)

sys.path.insert(0, package_dir)
# sys.path.append( os.path.abspath( package_dir ) )
# import module
from esp32partitiontool import *
# run module
print("Running module")
load_pm(env)



