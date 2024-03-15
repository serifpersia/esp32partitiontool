package com.platformio;

import java.nio.file.*;

import com.serifpersia.esp32partitiontool.AppSettings;

public class AppSettingsStandalone extends AppSettings {

	private String[] args;

	public AppSettingsStandalone(String[] args) {
		this.args = args;
		init();
	}

	public void init() {
		if (args == null) {
			System.out.println("No args given");
		} else {
			for (int i = 0; i < args.length; i++) {
				if (args[i].endsWith(".csv") && Files.exists(Paths.get(args[i]))) {
					prefs.put("csvFile.path", args[i] );
					//defaultCsvSaveDir = Paths.get(args[i]).getFileName().toString();
					System.out.println("CSV File: " + args[i]);
					//System.out.println("CSV Path: " + defaultCsvSaveDir);
				} else if (!args[i].trim().isEmpty()) {
					System.out.println("Ignored Arg#" + i + ": " + args[i]);
				}
			}
		}
	}

	@Override
	public void load() {

	}


}
