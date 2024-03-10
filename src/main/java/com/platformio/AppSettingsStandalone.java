package com.serifpersia.esp32partitiontool;
import java.nio.file.*;


public class AppSettingsStandalone extends AppSettings {

	private String[] args;

	public AppSettingsStandalone(String [] args) {
		this.args = args;
	}


	public void load() {
		if( args == null ) {
			System.out.println("No args given");
		} else {
			for( int i=0; i<args.length; i++ ) {
				if( args[i].endsWith(".csv") && Files.exists(Paths.get(args[i])) ) {
					csvFilePath = args[i];
					defaultCsvSaveDir = Paths.get(csvFilePath).getFileName().toString();
					System.out.println("CSV File: "+csvFilePath);
					System.out.println("CSV Path: "+defaultCsvSaveDir);
				} else if( ! args[i].trim().isEmpty() ) {
					System.out.println("Ignored Arg#"+i+": "+args[i]);
				}
			}
		}
	}


	public void save() {

	}


}

