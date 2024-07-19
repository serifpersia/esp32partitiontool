import os
import shutil
import subprocess
import sys

def check_java_version():
    try:
        result = subprocess.run(["java", "-version"], stderr=subprocess.PIPE, text=True)
        if "1.8." not in result.stderr:
            print("Failed to find Java JDK 8 installed on the user system")
            sys.exit(1)
    except FileNotFoundError:
        print("Java is not installed")
        sys.exit(1)

def create_directory(path):
    try:
        os.makedirs(path, exist_ok=True)
    except OSError as e:
        print(f"Error creating directory {path}: {e}")
        sys.exit(1)

def remove_directory(path):
    if os.path.exists(path):
        try:
            shutil.rmtree(path)
        except OSError as e:
            print(f"Error removing directory {path}: {e}")
            sys.exit(1)

def find_java_files():
    java_files = []
    for root, _, files in os.walk("src/main/java/com"):
        for file in files:
            if file.endswith(".java"):
                java_files.append(os.path.join(root, file))
    if not java_files:
        print("Error finding Java files")
        sys.exit(1)
    return java_files

def compile_java_files(java_files):
    try:
        subprocess.run(["javac", "-Xlint", "-cp", "dependencies/*", "-d", "bin"] + java_files, check=True)
    except subprocess.CalledProcessError:
        print("Error compiling Java classes")
        sys.exit(1)

def copy_resources():
    try:
        shutil.copytree("src/main/resources", "bin", dirs_exist_ok=True)
    except OSError as e:
        print(f"Error copying resources: {e}")
        sys.exit(1)

def create_jar_file():
    try:
        subprocess.run(["jar", "cfe", "build/ESP32PartitionTool.jar", "com.serifpersia.esp32partitiontool.ESP32PartitionTool", "-C", "bin", "."], check=True)
    except subprocess.CalledProcessError:
        print("Error creating JAR file")
        sys.exit(1)

def user_choice():
    while True:
        choice = input("Enter your choice (0 for Arduino IDE, 1 for PlatformIO): ").strip()
        if choice == '0':
            create_directory("tool/ESP32PartitionTool/tool")
            shutil.copy("build/ESP32PartitionTool.jar", "tool/ESP32PartitionTool/tool/ESP32PartitionTool.jar")
            break
        elif choice == '1':
            create_directory("tool/tool-esp32partitiontool")
            shutil.copy("build/ESP32PartitionTool.jar", "tool/tool-esp32partitiontool/ESP32PartitionTool.jar")
            shutil.copy("esp32partitiontool.py", "tool/tool-esp32partitiontool")
            shutil.copy("package.json", "tool/tool-esp32partitiontool")
            break
        else:
            print("Invalid choice. Please enter 0 or 1.")

def clean_up():
    remove_directory("bin")
    remove_directory("build")
    if os.path.exists("ClassList"):
        os.remove("ClassList")

def main():
    check_java_version()

    # Remove the "tool" directory if it exists
    remove_directory("tool")

    create_directory("bin")
    create_directory("build")

    java_files = find_java_files()
    with open("ClassList", "w") as f:
        for file in java_files:
            f.write(f"{file}\n")

    compile_java_files(java_files)
    copy_resources()
    create_jar_file()
    user_choice()
    clean_up()

    print("Build completed successfully.")

if __name__ == "__main__":
    main()
