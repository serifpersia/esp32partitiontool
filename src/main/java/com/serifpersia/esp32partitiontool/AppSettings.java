package com.serifpersia.esp32partitiontool;

import java.util.Map;
import java.util.HashMap;

public class AppSettings {

	public boolean hasFSPanel = false;
	public Map<String, String> prefs = new HashMap<>();

	public String get( String key ) {
		return prefs.get( key );
	}

	public String set( String key, String value ) {
		return prefs.put( key, value );
	}

	// this method is meant to be overloaded by platformio or arduino AppSettings classes
	public void load() {

	}


}
