package com.serifpersia.esp32partitiontool;

import java.util.Map;
import java.util.HashMap;
import javax.swing.*;

public class AppSettings {

	static final public class EventCallback {

		public Runnable onBefore;
		public Runnable onAfter;
		public Runnable onSuccess;
		public Runnable onFail;

		public EventCallback() {
		}

		public EventCallback(Runnable onBefore, Runnable onAfter, Runnable onSuccess, Runnable onFail) {
			this.onBefore  = onBefore;
			this.onAfter   = onAfter;
			this.onSuccess = onSuccess;
			this.onFail    = onFail;
		}

		public void onBefore () { if( this.onBefore  != null ) this.onBefore.run();  }
		public void onAfter  () { if( this.onAfter   != null ) this.onAfter.run();   }
		public void onSuccess() { if( this.onSuccess != null ) this.onSuccess.run(); }
		public void onFail   () { if( this.onFail    != null ) this.onFail.run();    }

		public void onBefore( Runnable onBefore ) {
			this.onBefore = onBefore;
		}
		public void onAfter( Runnable onAfter ) {
			this.onAfter = onAfter;
		}
		public void onSuccess( Runnable onSuccess ) {
			this.onSuccess = onSuccess;
		}
		public void onFail( Runnable onFail ) {
			this.onFail = onFail;
		}

	}

	public boolean platformSupported = false;
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

	public void reload() {
		clean();
		load();
	}


	// this method is overriden from AppSettingsStandalone or AppSettingsArduino classes
	public void load() {

	}

	// this method is overriden from AppSettingsArduino only
	public void build(JProgressBar progressBar, AppSettings.EventCallback callbacks) {

	}

	// this method is overriden from AppSettingsArduino only
	public void clean() {

	}


}
