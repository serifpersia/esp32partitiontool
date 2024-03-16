package com.arduino;


import processing.app.Editor;
import processing.app.Sketch;
import processing.app.PreferencesData;
import org.apache.commons.codec.digest.DigestUtils;
import processing.app.BaseNoGui;
import processing.app.debug.TargetPlatform;
import processing.app.helpers.FileUtils;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.io.*;

import com.serifpersia.esp32partitiontool.AppSettings;

public class AppSettingsArduino extends AppSettings {

	private Editor editor;
	private boolean debug_ui = true;
	private boolean isWindows;
	private File defaultSketchbookFolder;
	private String toolsPathBase;
	private TargetPlatform platform;
	private String espotaCmd;
	private String esptoolCmd;
	private String genEsp32PartCmd;

	public AppSettingsArduino(Editor editor) {
		this.editor = editor;
		this.hasFSPanel = true;
		init();
	}

	public void init() {

		// these don't need to be evaluated more than once
		isWindows = PreferencesData.get("runtime.os").contentEquals("windows");
		espotaCmd = "espota" + (isWindows ? ".exe" : ".py");
		esptoolCmd = "esptool" + (isWindows ? ".exe" : ".py");
		genEsp32PartCmd = "gen_esp32part" + (isWindows ? ".exe" : ".py");

		load();

		set("csvDir.path", get("sketchDir.path") );

		// check if the board uses a custom partition, could be stored in variants or
		// tools folder

		String partitionsPath = get("platform.path") + "/tools/partitions";
		String csvPath = partitionsPath + "/" + get("build.partitions") + ".csv";
		String variantCsvPath = null;

		if (BaseNoGui.getBoardPreferences().containsKey("build.custom_partitions")) {
			variantCsvPath = get("variant.path") + "/" + get("build.custom_partitions") + ".csv";
		} else {
			variantCsvPath = get("variant.path") + "/" + get("build.partitions") + ".csv";
		}

		String searchPaths[] = { get("sketchDir.path") + "/partitions.csv", variantCsvPath, csvPath };

		for (int i = 0; i < searchPaths.length; i++) {
			if (Files.exists(Paths.get(searchPaths[i]))) {
				set("csvFile.path", searchPaths[i] );
				break;
			}
		}

		if( debug_ui ) {
			prefs.forEach((key, value) -> {
				System.out.printf("%-24s : %s\n", key, value );
			});
		}
	}


	@Override
	public void load() {

		defaultSketchbookFolder = BaseNoGui.getDefaultSketchbookFolder();
		toolsPathBase = BaseNoGui.getToolsPath();
		platform = BaseNoGui.getTargetPlatform();

		set("sketch.name", editor.getSketch().getName() );
		set("sketch.path", editor.getSketch().getMainFilePath() );
		set("sketchDir.path", editor.getSketch().getFolder().getAbsolutePath() );
		set("platform.path", platform.getFolder().toString() );
		set("build.partitions", BaseNoGui.getBoardPreferences().get("build.partitions") );
		set("build.custom_partitions", BaseNoGui.getBoardPreferences().get("build.custom_partitions") );
		set("build.variant", BaseNoGui.getBoardPreferences().get("build.variant") );
		set("variant.path", get("platform.path") + "/variants/" + get("build.variant") );
		set("build.path", getBuildFolderPath() );
		set("bootloader.path", getBootloaderImagePath() );
		set("upload.speed", BaseNoGui.getBoardPreferences().get("upload.speed") );
		set("serial.port", BaseNoGui.getBoardPreferences().get("serial.port") );
		set("build.bootloader_addr", BaseNoGui.getBoardPreferences().get("build.bootloader_addr") );
		set("build.mcu", BaseNoGui.getBoardPreferences().get("build.mcu") );
		set("flashMode", BaseNoGui.getBoardPreferences().get("build.flash_mode") );
		set("flashFreq", BaseNoGui.getBoardPreferences().get("build.flash_freq") );
		set("pythonCmd", isWindows ? "python3.exe" : "python3");
		set("espotaCmd", espotaCmd );
		set("esptoolCmd", esptoolCmd );
		set("genEsp32PartCmd", genEsp32PartCmd );

		String espToolSearchPaths[] = {
				PreferencesData.get("runtime.tools.esptool_py.path"), // preferences.txt
				get("platform.path") + "/tools", get("platform.path") + "/tools/esptool_py", defaultSketchbookFolder + "/tools",
				defaultSketchbookFolder + "/tools/esptool_py", toolsPathBase, toolsPathBase + "/tools/esptool_py" };

		if (!findFile(esptoolCmd, espToolSearchPaths, "esptool")) {
			editor.statusError(" Error: esptool not found!");
			this.hasFSPanel = false;
		}

		// search for esptool.[py|exe] in all the following folders
		String otaToolSearchPaths[] = { //getProperty("espota.path"), // user.properties (always first)
				get("platform.path") + "/tools", defaultSketchbookFolder + "/tools", toolsPathBase, };

		if (!findFile(espotaCmd, espToolSearchPaths, "espota")) {
			editor.statusError(" Error: espota not found!");
			this.hasFSPanel = false;
		}

		if (!findFile(genEsp32PartCmd, espToolSearchPaths, "genEsp32Part")) {
			editor.statusError(" Error: genEsp32Part not found!");
			this.hasFSPanel = false;
		}

		String[] fsNames = {"SPIFFS", "LittleFS", "FatFS"};
		int size = fsNames.length;

		for (int i = 0; i < size; i++) {

			String toolBinName = "mk" + fsNames[i].toLowerCase();
			String toolExeName = toolBinName + (isWindows ? ".exe" : "");

			String toolPrefName = "runtime.tools." + toolBinName + ".path";
			String toolPathPref = PreferencesData.get(toolPrefName);

			// search for mk{filesystem}fs binary in the following folders:
			String searchPaths[] = { // getProperty(toolBinName + ".path"), // user.properties (always first)
					toolPathPref, // preferences.txt (always second)
					get("platform.path") + "/tools", get("platform.path") + "/tools/" + toolBinName,
					defaultSketchbookFolder + "/tools", defaultSketchbookFolder + "/tools/" + toolBinName,
					toolsPathBase, toolsPathBase + "/" + toolBinName };

			findFile(toolExeName, searchPaths, toolBinName);
		}

	}

	private String getBootloaderImagePath() {
		// precedence: source > variant > build files
		String searchPaths[] = { get("sketchDir.path") + "/bootloader.bin", // in source directory
				get("variant.path") + "/bootloader.bin", // in board variants directory
				get("build.path") + "/" + get("sketch.name") + ".ino" + ".bootloader.bin" // in build directory
		};
		// walk the array
		for (int i = 0; i < searchPaths.length; i++) {
			if (Files.exists(Paths.get(searchPaths[i]))) {
				// got a match!
				return searchPaths[i];
			}
		}
		return null;
	}

	private String findFile(String fileName, String searchPaths[]) {
		for (int j = 0; j < searchPaths.length; j++) {
			if (searchPaths[j] == null)
				continue;
			if (Files.exists(Paths.get(searchPaths[j] + "/" + fileName))) {
				return searchPaths[j] + "/" + fileName;
			}
		}
		return null;
	}

	private boolean findFile(String fileName, String searchPaths[], String propertyName) {
		String full_path = findFile(fileName, searchPaths);
		boolean found = full_path != null;
		if (found) { // save found path in properties file
			//if (debug_ui)
			//	System.out.println("[" + propertyName + "] => " + full_path);
			set(propertyName + ".path", full_path);
		} else {
			if (debug_ui)
				System.out.println("[" + propertyName + "] " + fileName + " not found in any tools folder");
		}
		return found;
	}

	private String getBuildFolderPath() {
		Sketch sketch = editor.getSketch();
		// first of all try the getBuildPath() function introduced with IDE 1.6.12
		// see commit arduino/Arduino#fd1541eb47d589f9b9ea7e558018a8cf49bb6d03
		try {
			String buildpath = sketch.getBuildPath().getAbsolutePath();
			return buildpath;
		} catch (IOException er) {
			editor.statusError(er);
		} catch (Exception er) {
			try {
				File buildFolder = FileUtils.createTempFolder("build",
						DigestUtils.md5Hex(sketch.getMainFilePath()) + ".tmp");
				return buildFolder.getAbsolutePath();
			} catch (IOException e) {
				editor.statusError(e);
			} catch (Exception e) {
				// Arduino 1.6.5 doesn't have FileUtils.createTempFolder
				// String buildPath = BaseNoGui.getBuildFolder().getAbsolutePath();
				java.lang.reflect.Method method;
				try {
					method = BaseNoGui.class.getMethod("getBuildFolder");
					File f = (File) method.invoke(null);
					return f.getAbsolutePath();
				} catch (SecurityException ex) {
					editor.statusError(ex);
				} catch (IllegalAccessException ex) {
					editor.statusError(ex);
				} catch (InvocationTargetException ex) {
					editor.statusError(ex);
				} catch (NoSuchMethodException ex) {
					editor.statusError(ex);
				}
			}
		}
		return "";
	}


}
