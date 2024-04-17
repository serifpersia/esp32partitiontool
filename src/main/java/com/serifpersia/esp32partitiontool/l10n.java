package com.serifpersia.esp32partitiontool;

//import java.util.ResourceBundle;
import java.util.*;
import java.util.Map.Entry;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URISyntaxException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


@SuppressWarnings("serial")

public class l10n {

	public static Map<String, ResourceBundle> resourceBundles = new HashMap<String, ResourceBundle>();
	public static ResourceBundle defaultBundle;
	public static ResourceBundle currentBundle;

	private static final String l10nPath = "l10n/"; // subfolder name in resources dir, needs trailing slash
	private static final String filterExtension = "properties"; // l10n file extension
	private static final String fileNamePrefix = "labels"; // l10n file name prefix
	private static final String defaultLang = "en"; // iso-639-2

	public l10n() {
		resourceBundles.clear();
		l10n.loadBundles();
		if( l10n.setLang( Locale.getDefault().getLanguage() ) ) {
			System.out.println("Selected l10n: " +  Locale.getDefault().getLanguage() );
		}

	}


	public static String getString( String key ) {
		if( currentBundle == null ) {
			return key;
		}
		String label = null;

		try {
			label = currentBundle.getString( key );
		} catch( MissingResourceException e1 ) {
			try {
				label = defaultBundle.getString( key );
			} catch( MissingResourceException e2 ) {
				return key;
			}
		}

		if( label == null || label.trim().isEmpty() ) {
			return key;
		}

		try {
			return new String(label.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return label; // unencoded
		}
	}


	public static PropertyResourceBundle getBundle( String lang ) {
		String fileNameSuffix = lang.equals(defaultLang) ? "" : "_"+lang;
		String filePath = l10nPath + fileNamePrefix + fileNameSuffix + "." + filterExtension;

		try {
			InputStream is = l10n.class.getClassLoader().getResourceAsStream(filePath);
			if( is == null ) {
				System.out.println("l10n resource not found: " + filePath);
				return null;
			}
			PropertyResourceBundle resBundle = new PropertyResourceBundle(is);
			is.close();
			return resBundle;
		} catch( IOException e ) {
			System.out.println("Unable to access l10n resource: " + filePath );
			return null;
		}
	}


	public static boolean loadBundles() {
		resourceBundles.clear();

		String[] languageFiles;

		try {
			languageFiles = getL10nResources();
		} catch(URISyntaxException|IOException e) {
			System.out.println( "Failed to load l10n resources" );
			return false;
		}

		defaultBundle = getBundle(defaultLang);

		if( defaultBundle == null ) {
			System.out.println( "Missing default language bundle" );
			return false;
		}

		resourceBundles.put(defaultLang, defaultBundle);

		for(String fileName : languageFiles) {
			String[] fileNameParts = fileName.split("\\.");
			if( fileNameParts.length <= 1 ) continue; // not an l10n resource
			String[] l10nParts = fileNameParts[0].split("_");
			String lang = ( l10nParts.length == 2 ) ? l10nParts[1] : defaultLang;
			PropertyResourceBundle langBundle = getBundle(lang);
			if( langBundle == null ) continue; // failed to load bundle
			resourceBundles.put(lang, langBundle);
		}

		return resourceBundles.size()>0;
	}


	public static boolean setLang( String lang )  {
		ResourceBundle langBundle = resourceBundles.get(lang);

		if( langBundle != null ) {
			currentBundle = langBundle;
			return true;
		}

		langBundle = getBundle(lang);

		if( langBundle != null ) {
			currentBundle = langBundle;
		} else {
			if (lang.equals(defaultLang) || getBundle(defaultLang) == null ) {
				System.out.println("Default lang not found");
				return false;
			}
			currentBundle = defaultBundle;
		}
		return true;
	}


	private static String[] getL10nResources() throws URISyntaxException, IOException {

		FilenameFilter textFilefilter = new FilenameFilter(){
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(filterExtension)) {
					return true;
				} else {
					return false;
				}
			}
		};

		URL dirURL = l10n.class.getClassLoader().getResource(l10nPath);

		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			return new File(dirURL.toURI()).list(textFilefilter);
		}

		String me = l10n.class.getName().replace(".", "/")+".class";
		dirURL = l10n.class.getClassLoader().getResource(me);

		if (dirURL != null && dirURL.getProtocol().equals("jar")) {
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); // strip out only the JAR file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
			Set<String> result = new HashSet<String>(); // avoid duplicates in case it is a subdirectory
			while(entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(l10nPath) && name.endsWith(filterExtension)) { // filter according to the path and extension
					// System.out.println(name);
					String entry = name.substring(l10nPath.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory name
						entry = entry.substring(0, checkSubdir);
					}
					if( entry.startsWith(fileNamePrefix) ) {
						result.add(entry);
					}
				}
			}
			return result.toArray(new String[result.size()]);
		}

		throw new UnsupportedOperationException("Cannot list l10n files in resource directory: "+dirURL);
	}

}


