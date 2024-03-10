import os.path
import sys
from os.path import join
from SCons.Script import DefaultEnvironment, SConscript
import subprocess

def getJavaVersion():
    try:
        output = subprocess.check_output(['java', '-version'], stderr=subprocess.STDOUT).decode()
        for line in output.splitlines():
            print("Output: " + line )
            # E.g. openjdk version "1.8.0_212"
            line = line.strip()
            if 'version' in line:
                ver = line.split('version', 1)[1].strip()
                if ver.startswith('"') and ver.endswith('"'):
                    ver = ver[1:-1]
                return ver
    except:
        return None
    return None




def load_pm(env):

  java_version  = getJavaVersion()

  if java_version != None:

      board         = env.BoardConfig()
      platform      = env.PioPlatform()

      jar_file      = "ESP32PartitionTool.jar"
      jar_pkg_dir   = platform.get_package_dir("tool-esp32partitiontool")

      build_core    = board.get("build.core", "").lower()
      partition     = os.path.abspath(board.get("build.partitions"))
      build_variant = board.get("build.variant")
      csv_file      = partition if os.path.isfile(partition) else ""

      print( "****************************************************" )
      print( "Java version      = " + java_version )
      print( "Jar Relative Path = " + jar_file )
      print( "Jar Dir           = " + jar_pkg_dir )
      print( "Build Core        = " + build_core )
      print( "Partition         = " + csv_file )
      print( "Build Variant     = " + build_variant )
      print( "****************************************************" )

      command_format = 'java -cp "%s/%s" com.serifpersia.esp32partitiontool.ESP32PartitionToolStandalone "%s"'
      command = command_format % (jar_pkg_dir, jar_file, csv_file)

      env.AddCustomTarget("ESP32PartitionTool", None, command)

  else:

      print ("No java executable found, cannot start ESP32PartitionTool :(")

