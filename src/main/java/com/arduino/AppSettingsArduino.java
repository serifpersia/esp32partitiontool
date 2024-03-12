package com.arduino;

import java.nio.file.*;

import processing.app.Editor;
import processing.app.BaseNoGui;
import processing.app.debug.TargetPlatform;

import com.serifpersia.esp32partitiontool.AppSettings;

public class AppSettingsArduino extends AppSettings {

	private Editor editor;

	public AppSettingsArduino(Editor editor) {
		this.editor = editor;
	}

	public void load() {
		// figure out what csv file is selected in the boards menu, and where it is
		TargetPlatform platform = BaseNoGui.getTargetPlatform();
		String defaultCsvSaveDir = editor.getSketch().getFolder().toString();
		String platformPath = platform.getFolder().toString();
		String csvName = BaseNoGui.getBoardPreferences().get("build.partitions");
		String customCsvName = BaseNoGui.getBoardPreferences().get("build.custom_partitions");
		String variantName = BaseNoGui.getBoardPreferences().get("build.variant");
		String variantPath = platformPath + "/variants/" + variantName;
		String partitionsPath = platformPath + "/tools/partitions";
		String csvPath = partitionsPath + "/" + csvName + ".csv";
		String variantCsvPath = null;

		// check if the board uses a custom partition, could be stored in variants or
		// tools folder
		if (BaseNoGui.getBoardPreferences().containsKey("build.custom_partitions")) {
			variantCsvPath = variantPath + "/" + customCsvName + ".csv";
		} else {
			variantCsvPath = variantPath + "/" + csvName + ".csv";
		}

		System.out.println("build.custom_partitions = " + customCsvName);
		System.out.println("build.partitions        = " + csvName);
		System.out.println("build.variant           = " + variantName);
		System.out.println("variantCsvPath          = " + variantCsvPath);
		System.out.println("csvPath                 = " + csvPath);

		String searchPaths[] = { defaultCsvSaveDir + "/partitions.csv", variantCsvPath, csvPath };

		for (int i = 0; i < searchPaths.length; i++) {
			if (Files.exists(Paths.get(searchPaths[i]))) {
				// load csv file
				csvFilePath = searchPaths[i];

				System.out.println("CSV File: " + csvFilePath);
				System.out.println("CSV Path: " + defaultCsvSaveDir);

				break;
			}
		}
	}

	public void save() {

	}

}
