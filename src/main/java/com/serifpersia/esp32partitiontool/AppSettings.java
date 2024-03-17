package com.serifpersia.esp32partitiontool;

import java.util.Map;
import java.util.HashMap;
import javax.swing.*;

public class AppSettings {

	public boolean debug_settings = true;
	public boolean hasFSPanel = false;
	private boolean changed = false;
	public Map<String, String> prefs = new HashMap<>();

	public String get( String key ) {
	  String value = prefs.get( key );
	  if( debug_settings && value == null ) {
			System.out.printf("[debug] settings.%s is null\n", key );
	  }
		return value;
	}

	public String set( String key, String value ) {
	  String oldvalue = prefs.put( key, value );
		if( debug_settings && oldvalue != null && value != null ) {
			if( !oldvalue.equals(value) ) {
				System.out.printf("[debug] Value change for settings.%s:\n  [old] %s\n  [new] %s\n", key, oldvalue, value );
				changed = true;
			}
		}
		return oldvalue;
	}

	public void setChanged() {
		changed = true;
	}

	public boolean changed() {
		boolean have_changed = changed;
		changed = false; // reset
		return have_changed;
	}

	// this method is overriden from AppSettingsStandalone or AppSettingsArduino classes
	public void load() {

	}

	// this method is overriden from AppSettingsArduino only
	public void build(JProgressBar progressBar, Runnable runAfter) {

	}

	// this method is overriden from AppSettingsArduino only
	public void clean() {

	}


}
