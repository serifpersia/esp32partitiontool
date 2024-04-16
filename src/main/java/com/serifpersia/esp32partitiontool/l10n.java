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
	private static final String filterExtension = "properties";
	private static final String filterFileName = "label";
	private static final String defaultLang = "en"; // iso-639-2
	private static String currentLang; // iso-639-2

	public static ArrayList<String> languages = new ArrayList<String>();

	public l10n() {
		resourceBundles.clear();
		l10n.loadBundles();
		l10n.loadBundle( Locale.getDefault().getLanguage() );
		System.out.println("Selected l10n: " +  Locale.getDefault().getLanguage() );
	}


	public static String getString( String key ) {
		if( currentBundle == null ) {
			return key;
		}
		String label = currentBundle.getString( key );

		if( currentLang != defaultLang && label == null ) {
			label = defaultBundle.getString( key );
		}

		if( label == null ) {
			return key;
		}

		try {
			return new String(label.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return label; // unencoded
		}
	}



	public static boolean loadBundle( String lang ) {
		String l10nFileName = lang.equals(defaultLang) ? "labels" : "labels_"+lang;
		String l10nRelFilePath = l10nPath + l10nFileName + "." + filterExtension;

		//System.out.println("Will attempt to load " + lang + " bundle at: " + l10nRelFilePath );

		try {
			InputStream is = l10n.class.getClassLoader().getResourceAsStream(l10nRelFilePath);
			if( is == null ) {
				System.out.println("l10n file not found: " + l10nRelFilePath);
				return false;
			}
			currentBundle = new PropertyResourceBundle(is);
			is.close();
			return true;
		} catch( IOException e ) {
			System.out.println("Unable to access l10n resource " + l10nRelFilePath );
			return false;
		}
	}


	public static boolean loadBundles() {
		resourceBundles.clear();

		String[] languageFiles;

		try {
			languageFiles = getL10nResources();
		} catch(Exception e) {
			System.out.println( "failed to load l10n resources" );
			return false;
		}

		if( !loadBundle(defaultLang) ) {
			System.out.println( "Missing default bundle" );
			return false;
		}

		defaultBundle = currentBundle;
		resourceBundles.put(defaultLang, defaultBundle);

		for(String fileName : languageFiles) {
			String[] fileNameParts = fileName.split("\\.");
			if( fileNameParts.length <= 1 ) continue;
			String[] l10nParts = fileNameParts[0].split("_");
			String lang = defaultLang;
			if( l10nParts.length == 2 ) lang = l10nParts[1];

			//System.out.printf("added lang: %s\n", lang);
			languages.add(lang);
			resourceBundles.put(lang, currentBundle);
		}

		if( ! setLang(defaultLang) ) {
			System.out.println("failed to set default lang");
			return false;
		}

		return languages.size()>0;
	}



	public static boolean setLang( String lang )  {
		ResourceBundle langBundle = resourceBundles.get(lang);
		if( langBundle !=null ) {
			currentBundle = langBundle;
			currentLang = lang;
			return true;
		}

		if( !loadBundle(lang) ) {
			if (lang.equals(defaultLang) || !loadBundle(defaultLang)) {
				System.out.println("Default lang not found");
				return false;
			}
			currentLang = defaultLang;
			//System.out.println("Default lang selected");
		} else {
			//System.out.println("Lang selected: " + lang);
			currentLang = lang;
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

		if (dirURL.getProtocol().equals("jar")) {
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
			while(entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(l10nPath) && name.endsWith(filterExtension)) { //filter according to the path and extension
					System.out.println(name);
					String entry = name.substring(l10nPath.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory name
						entry = entry.substring(0, checkSubdir);
					}
					if( entry.startsWith(filterFileName) ) {
						result.add(entry);
					}else {
						//System.out.println("ignoring: " +  entry );
					}
				} else {
					//System.out.println("ignoring: " +  name );
				}
			}
			return result.toArray(new String[result.size()]);
		} else {
			//System.out.println("dirURL protocol is not jar: " +  dirURL );
		}

		throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
	}




}


