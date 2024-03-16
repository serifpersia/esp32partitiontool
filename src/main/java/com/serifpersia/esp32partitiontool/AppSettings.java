package com.serifpersia.esp32partitiontool;

import java.util.Map;
import java.util.HashMap;

public class AppSettings {

	public boolean debug_settings = true;
	public boolean hasFSPanel = false;
	public Map<String, String> prefs = new HashMap<>();


	public String get( String key ) {
	  String value = prefs.get( key );
	  if( debug_settings && value == null ) {
			System.out.printf("settings.%s is null\n", key );
	  }
		return value;
	}

	public String set( String key, String value ) {
	  String oldvalue = prefs.put( key, value );
		if( debug_settings && oldvalue != null && value != null ) {
			if( !oldvalue.equals(value) ) {
				System.out.printf("Value change for settings.%s:\n  [old] %s\n  [new] %s\n", key, oldvalue, value );
			}
		}
		return oldvalue;
	}

	// this method is meant to be overriden from platformio or arduino AppSettings classes
	public void load() {

	}


}
