package com.serifpersia.esp32partitiontool;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JOptionPane;

import java.util.Properties;
import java.nio.file.*;

import processing.app.PreferencesData;
import processing.app.Editor;
import processing.app.EditorToolbar;
import processing.app.BaseNoGui;
import processing.app.Sketch;
import processing.app.helpers.ProcessUtils;
import processing.app.debug.TargetPlatform;

import org.apache.commons.codec.digest.DigestUtils;
import processing.app.helpers.FileUtils;

public class FileManager {

	private UI ui; // Reference to the UI instance
	private Editor editor; // Reference to the Editor instance

	private ArrayList<String> createdPartitionsData;


	boolean isWindows      = PreferencesData.get("runtime.os").contentEquals("windows");
	String mcu             = BaseNoGui.getBoardPreferences().get("build.mcu");
	String flashMode       = BaseNoGui.getBoardPreferences().get("build.flash_mode");
	String flashFreq       = BaseNoGui.getBoardPreferences().get("build.flash_freq");
	String pythonCmd       = isWindows ? "python3.exe" : "python3";
	String toolExtension   = isWindows ? ".exe" : ".py";
	String espotaCmd       = "espota"  + toolExtension;
	String esptoolCmd      = "esptool" + toolExtension;
	String genEsp32PartCmd = "gen_esp32part" + toolExtension;

	String imagePath;
	Boolean isNetwork;
	String serialPort;
	String uploadSpeed;

	long spiStart;
	// Declare spiSize, spiPage, spiBlock here
	long spiSize;
	int spiPage;
	int spiBlock;

	boolean debug_ui     			= false;
	boolean confirm_overwrite = true;
	// Not all projects have a /data folder, and the confirmation window can be annoying
	// TODO: rename this veeeeery_looooooong_variable to something shorter
	boolean creating_empty_spiffs_needs_confirmation = false;

	TargetPlatform platform      = BaseNoGui.getTargetPlatform();
	File platformPath            = platform.getFolder();
	String toolsPathBase         = BaseNoGui.getToolsPath();
	File defaultSketchbookFolder = BaseNoGui.getDefaultSketchbookFolder();
	String jarPath               = FileManager.class.getProtectionDomain().getCodeSource()
																						.getLocation().getPath(); // => /path/to/ESP32PartitionTool/tool/ESP32PartitionTool.jar
	File jarFile                 = new File(jarPath);
	String classPath             = jarFile.getParent();                 // => /path/to/ESP32PartitionTool/tool
	String propertiesFile        = classPath + "/prefs.properties";     // => /path/to/ESP32PartitionTool/tool/prefs.properties

	String espotaPath;   // populated by loadProperties()
	String esptoolPath;  // populated by loadProperties()
	String sketchName;   // populated by loadProperties()
	String sketchPath;   // populated by loadProperties()
	String sketchDir;    // populated by loadProperties()

	Properties prefs             = new Properties();


	// Constructor to initialize FileManager with UI instance and Editor instance
	public FileManager(UI ui, Editor editor) {
		this.ui = ui;
		this.editor = editor;
	}


	public void emitError( String msg ) {
		System.err.println();
		editor.statusError( msg );
	}


	public static String basename(String path) {
		String filename = path.substring(path.lastIndexOf('/') + 1);
		if (filename == null || filename.equalsIgnoreCase("")) {
			filename = "";
		}
		return filename;
	}


	public void setUIController( UIController controller ) {
		ui.setController( controller );
	}


	public void setConfirmDataEmpty( boolean enable ) {
		creating_empty_spiffs_needs_confirmation = enable;
		prefs.setProperty("spiffs.confirm_empty_data_creation", creating_empty_spiffs_needs_confirmation?"true":"false");
		saveProperties();
	}


	public void setConfirmOverwrite( boolean enable ) {
		confirm_overwrite = enable;
		prefs.setProperty("files.confirm_overwrite", confirm_overwrite?"true":"false");
		saveProperties();
	}


	public void setDebug( boolean enable ) {
		debug_ui = enable;
		prefs.setProperty("debug.enabled", debug_ui?"true":"false");
		saveProperties();
	}


	public boolean canRun() {
		if (!PreferencesData.get("target_platform").contentEquals("esp32")) {
			emitError("Unsupported platform: " + PreferencesData.get("target_platform"));
			emitError("This tools only runs on esp32 platform");
			return false;
		}

		if (!BaseNoGui.getBoardPreferences().containsKey("build.partitions")) {
			emitError("No partitions defined for " + BaseNoGui.getBoardPreferences().get("name") + " in boards.txt");
			return false;
		}
		return true;
	}


	public void loadProperties() {

		sketchName   = editor.getSketch().getName();
		sketchPath   = editor.getSketch().getMainFilePath();
		sketchDir    = editor.getSketch().getFolder().toString();

		if (Files.notExists(Paths.get(propertiesFile))) {
			try {
				File file = new File(propertiesFile);
				file.createNewFile();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		try (InputStream input = new FileInputStream(propertiesFile)) {

			// load the properties file
			prefs.load(input);

			debug_ui 					= Boolean.parseBoolean( prefs.getProperty("debug.enabled") );
			confirm_overwrite = Boolean.parseBoolean( prefs.getProperty("files.confirm_overwrite") );
			creating_empty_spiffs_needs_confirmation = Boolean.parseBoolean( prefs.getProperty("spiffs.confirm_empty_data_creation") );

			ui.getDebug().setSelected( debug_ui );
			ui.getOverwriteCheckBox().setSelected( !confirm_overwrite );
			ui.getConfirmDataEmptyCheckBox().setSelected( creating_empty_spiffs_needs_confirmation );

			// print key and values
			if( debug_ui ) {
				prefs.forEach((key, value) -> {
					System.out.println("[Loaded pref] " + key + " = " + value);
				});
			}

			// search for esptool.[py|exe] in all the following folders
			String espToolSearchPaths[] = {
				prefs.getProperty("esptool.path"),                    // user.properties (always first)
				PreferencesData.get("runtime.tools.esptool_py.path"), // preferences.txt (always second)
				platformPath + "/tools",
				platformPath + "/tools/esptool_py",
				defaultSketchbookFolder+"/tools",
				defaultSketchbookFolder+"/tools/esptool_py",
				toolsPathBase,
				toolsPathBase+"/tools/esptool_py"
			};

			if( !findFile( esptoolCmd, espToolSearchPaths, "esptool" ) ) {
				editor.statusError(" Error: esptool not found!");
			}

			// search for esptool.[py|exe] in all the following folders
			String otaToolSearchPaths[] = {
				prefs.getProperty("espota.path"), // user.properties (always first)
				platformPath + "/tools",
				defaultSketchbookFolder+"/tools",
				toolsPathBase,
			};

			if( !findFile( esptoolCmd, espToolSearchPaths, "espota" ) ) {
				editor.statusError(" Error: espota not found!");
			}

			int size = ui.getPartitionFlashType().getItemCount();

			for (int i = 0; i < size; i++) {
				String fsName = (String) ui.getPartitionFlashType().getItemAt(i);

				String toolBinName = "mk" + fsName.toLowerCase();
				String toolExeName = toolBinName + (isWindows ? ".exe" : "");

				String toolPrefName = "runtime.tools." + toolBinName + ".path";
				String toolPathPref = PreferencesData.get(toolPrefName);

				// search for mk{filesystem}fs binary in the following folders:
				String searchPaths[] = {
					prefs.getProperty(toolBinName+".path"), // user.properties (always first)
					toolPathPref,                           // preferences.txt (always second)
					platformPath + "/tools",
					platformPath + "/tools/" + toolBinName,
					defaultSketchbookFolder+"/tools",
					defaultSketchbookFolder+"/tools/"+toolBinName,
					toolsPathBase,
					toolsPathBase + "/"+toolBinName
				};

				findFile( toolExeName, searchPaths, toolBinName );
			}

			saveProperties();

		} catch (IOException ex) {
				ex.printStackTrace();
		}

		espotaPath  = prefs.getProperty("espota.path");
		esptoolPath = prefs.getProperty("esptool.path");

		if( debug_ui ) {
			System.out.println("sketchDir               = " + sketchDir );
			System.out.println("jarPath                 = " + jarPath);
			System.out.println("classPath               = " + classPath );
			System.out.println("platformPath            = " + platformPath);
			System.out.println("toolsPathBase           = " + toolsPathBase);
			System.out.println("defaultSketchbookFolder = " + defaultSketchbookFolder);
		}
	}


	public void loadCSV() {
		// figure out what csv file is selected in the boards menu, and where it is
		String csvName        = BaseNoGui.getBoardPreferences().get("build.partitions");
		String customCsvName  = BaseNoGui.getBoardPreferences().get("build.custom_partitions");
		String variantName    = BaseNoGui.getBoardPreferences().get("build.variant");
		String variantPath    = platformPath+"/variants/" + variantName;
		String partitionsPath = platformPath+"/tools/partitions";
		String csvPath        = partitionsPath + "/" + csvName + ".csv";
		String variantCsvPath = null;

		// check if the board uses a custom partition, could be stored in variants or tools folder
		if( BaseNoGui.getBoardPreferences().containsKey("build.custom_partitions") ) {
			variantCsvPath = variantPath + "/" + customCsvName + ".csv";
		} else {
			variantCsvPath = variantPath    + "/" + csvName + ".csv";
		}

		if( debug_ui ) {
			System.out.println("build.custom_partitions = " + customCsvName );
			System.out.println("build.partitions        = " + csvName );
			System.out.println("build.variant           = " + variantName );
			System.out.println("variantCsvPath          = " + variantCsvPath );
			System.out.println("csvPath                 = " + csvPath );
		}

		String searchPaths[] = { sketchDir+"/partitions.csv", variantCsvPath, csvPath };

		for(int i=0;i<searchPaths.length;i++) {
			if ( Files.exists( Paths.get( searchPaths[i] )) ) {
				// load csv file
				importCSV( searchPaths[i] );
				break;
			}
		}
	}


	public String findFile( String fileName, String searchPaths[] ) {
		for( int j=0; j<searchPaths.length; j++ ) {
			if( searchPaths[j] == null ) continue;
			if ( Files.exists( Paths.get( searchPaths[j]+"/"+fileName ))) {
				return searchPaths[j]+"/"+fileName;
			}
		}
		return null;
	}


	public boolean findFile( String fileName, String searchPaths[], String propertyName ) {
		String full_path = findFile( fileName, searchPaths);
		boolean found = full_path != null;
		if( found ) { // save found path in properties file
			if( debug_ui )
				System.out.println("[" + propertyName + "] "+fileName+" found at " + full_path);
			prefs.setProperty(propertyName+".path", full_path);
		} else {
			if( debug_ui )
				System.out.println("[" + propertyName + "] "+fileName+" not found in any tools folder");
		}
		return found;
	}



	public void saveProperties() {
		try {
			File f = new File( propertiesFile );
			OutputStream out = new FileOutputStream( f );
			prefs.store(out, "User properties");
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
	}


	// don't use it, it freezes the applet during compilation, not worth the lazyness
	public boolean CompileSketch() {
		try {
			if( editor.getSketchController().build(true, true) != null )
				return true;
		} catch( Exception e ) {
			emitError( e.getMessage() );
		}
		return false;
	}


	public boolean checkBuildFile( String path ) {
		if (Files.notExists(Paths.get(path))) {
			int option = JOptionPane.showConfirmDialog(null, "Build path " + path + " not found.\nCompile sketch?\nNote: the applet will appear frozen during build.", "Build Project?", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				if( ! CompileSketch() ) {
					return false;
				};
			}
			if (Files.notExists(Paths.get(path))) {
				emitError("Build path "+path+" still not found after compiling, giving up");
				return false;
			}
		}
		return true;
	}


	private void calculateCSV() {
		int numOfItems = ui.getNumOfItems();
		createdPartitionsData = new ArrayList<>();
		createdPartitionsData.add("# Name,   Type, SubType,  Offset,   Size,  Flags");

		for (int i = 0; i < numOfItems; i++) {
			CSVRow csvRow = ui.getCSVRow(i);
			if (csvRow.enabled.isSelected()) {
				String exported_csvPartition = csvRow.toString();
				if( debug_ui )
					System.out.println( exported_csvPartition );
				createdPartitionsData.add(exported_csvPartition);
			}
		}
	}


	public void importCSV( String file ) {
		File defaultDirectory = editor.getSketch().getFolder();
		File readerFile = null;

		if( file == null ) {
			FileDialog dialog = new FileDialog((Frame) null, "Select CSV File", FileDialog.LOAD);
			dialog.setDirectory(defaultDirectory.getAbsolutePath());
			dialog.setFile("*.csv");
			dialog.setVisible(true);
			String directory = dialog.getDirectory();
			if( directory == null || directory.isEmpty() ) return;
			file = dialog.getFile();
			if( file == null || file.isEmpty() ) return;
			readerFile = new File(directory, file);
		} else {
			readerFile = new File(file);
		}

		if (file != null) {
			try (BufferedReader reader = new BufferedReader(new FileReader(readerFile))) {
				processCSV(reader);

				String csvBasename = basename(file);
				ui.updatePartitionLabel(csvBasename);

			} catch (IOException e) {
				emitError("Error reading CSV file: " + e.getMessage());
			}
		} else {
			System.out.println("No file selected.");
		}

		ui.calculateSizeHex();
		ui.calculateOffsets();
		ui.updatePartitionFlashVisual();
		ui.validateSubtypes();

		JTextField lastPartitionOffsetField = ui.getPartitionOffsets(ui.lastIndex + 1);

		if (lastPartitionOffsetField != null && !lastPartitionOffsetField.getText().isEmpty() ) {
			// Get the text from the JTextField and parse it as hexadecimal
			String hexOffset = lastPartitionOffsetField.getText();
			long lastOffset = Long.parseLong(hexOffset, 16);

			// Calculate offset in MB, rounding up to the nearest integer
			long offsetInMB = (lastOffset + 1024 * 1024 - 1) / (1024 * 1024);

			ui.flashSizeMB = (int) offsetInMB;
		}

		String flashSizeString = String.valueOf(ui.flashSizeMB);
		ui.getFlashSize().setSelectedItem(flashSizeString);
		ui.updatePartitionFlashTypeLabel();
	}


	private void processCSV(BufferedReader reader) throws IOException {
		int rowIndex = 0;
		String line;

		ui.getCSVRows().clear();

		while ((line = reader.readLine()) != null && rowIndex < 100) {
			// Skip comment lines that start with #
			if (line.trim().startsWith("#")) {
				continue;
			}
			String[] columns = line.split(",\\s*");
			// Skip empty or incomplete lines (e.g. trailing CR/LF)
			if (columns.length < 5) {
				continue;
			}
			updateUIComponents(columns);
			rowIndex++;
		}
		ui.renderCSVRows();
	}


	private void updateUIComponents(String[] columns) {

		if( columns.length<5 ) return;

		String partitionTypeStr = columns[1].trim().toLowerCase();

		// partition type can be numeric
		if (!partitionTypeStr.equals("app") && !partitionTypeStr.equals("data")) {
			partitionTypeStr = stringToDec(partitionTypeStr) == 0 ? "app" : "data";
		}

		long bytesOffset = stringToDec(columns[3].trim());
		long byteSize    = stringToDec(columns[4].trim());
		String kbSize   = stringToKb(columns[4].trim());

		String cells[] = {
			columns[0].trim(), // name
			partitionTypeStr,  // type
			columns[2].trim(), // subtype
			kbSize,            // size
			String.format("%X", byteSize),   // sizeHex
			String.format("%X", bytesOffset) // offsetHex
		};

		CSVRow csvRow = new CSVRow( cells );
		ui.addCSVRow( csvRow );
	}


	private String formatKilobytes(double kilobytes) {
		// Check if the kilobytes value is a whole number
		if (kilobytes % 1 == 0) {
			return String.format("%.0f", kilobytes);
		} else {
			return String.format("%.2f", kilobytes);
		}
	}


	private long stringToDec(String value) {
		if (value.toLowerCase().startsWith("0x")) {
			return Long.decode(value);
		} else {
			return Long.parseLong(value);
		}
	}


	private String stringToKb(String value) {
		long decimalValue = stringToDec(value);
		return formatKilobytes(decimalValue / 1024.0);
	}


	public boolean generateCSV() {
		calculateCSV();

		// Get the default directory path
		File defaultDirectory = editor.getSketch().getFolder();

		// Export to CSV
		FileDialog dialog = new FileDialog(new Frame(), "Create Partitions CSV", FileDialog.SAVE);
		dialog.setDirectory(defaultDirectory.getAbsolutePath());
		dialog.setFile("partitions.csv");
		dialog.setVisible(true);
		String fileName = dialog.getFile();

		if( fileName == null ) return false;

		String filePath = dialog.getDirectory() + fileName; // Construct the full file path
		try (FileWriter writer = new FileWriter(filePath)) {
			// Write the exported data to the CSV file
			for (String partitionData : createdPartitionsData) {
				writer.write(partitionData + "\n");
			}

			if (Files.notExists(Paths.get(filePath))) {
				emitError("Failed to write " + filePath);
				return false;
			}
			System.out.println("partitions.csv written at: " + filePath);
			return true;
		} catch (IOException ex) {
			emitError("Error creating CSV: " + ex.getMessage());
		}
		return false;
	}


	private int listenOnProcess(String[] arguments) {
		try {
			final Process p = ProcessUtils.exec(arguments);
			Thread thread = new Thread() {
				public void run() {
					try {
						InputStreamReader reader = new InputStreamReader(p.getInputStream());
						int c;
						while ((c = reader.read()) != -1)
							System.out.print((char) c);
						reader.close();

						reader = new InputStreamReader(p.getErrorStream());
						while ((c = reader.read()) != -1)
							System.err.print((char) c);
						reader.close();
					} catch (Exception e) {
					}
				}
			};
			thread.start();
			int res = p.waitFor();
			thread.join();
			return res;
		} catch (Exception e) {
			return -1;
		}
	}


	private void sysExec(final String[] arguments) {
		Thread thread = new Thread() {
			public void run() {
				try {
					if (listenOnProcess(arguments) != 0) {
					} else {
						editor.statusNotice("Uploaded!");
					}
				} catch (Exception e) {
				}
			}
		};
		thread.start();
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


	public void buidBootloaderBin( String output_path ) {

		String flash_mode  = BaseNoGui.getBoardPreferences().get("build.flash_mode");
		String flash_freq  = BaseNoGui.getBoardPreferences().get("build.flash_freq");
		String flash_size  = BaseNoGui.getBoardPreferences().get("build.flash_size");
		String target_chip = BaseNoGui.getBoardPreferences().get("build.mcu");
		String sdk_path    = platformPath + "/tools/sdk/" + target_chip; // {runtime.platform.path}/tools/sdk/{build.mcu}
		String elf_path    = sdk_path + "/bin/bootloader_" + flash_mode + "_" + flash_freq + ".elf";

		if( output_path == null ) {
			String buildPath   = getBuildFolderPath();
			String sketch_name = editor.getSketch().getName();
			output_path = buildPath + "/" + sketch_name + ".ino.bootloader.bin";
		}

		if( Files.notExists(Paths.get(elf_path)) ) {
			emitError(" Error: bootloader_" + flash_mode + "_" + flash_freq + ".elf not found at " + elf_path);
			return;
		}

		String[] elf2imageLinux = {
			pythonCmd, esptoolPath,
			"--chip", target_chip, "elf2image",
			"--flash_mode", flash_mode,
			"--flash_freq", flash_freq,
			"--flash_size", flash_size,
			"-o", output_path,
			elf_path
		};

		String[] elf2imageWindows = {
			esptoolPath,
			"--chip", target_chip, "elf2image",
			"--flash_mode", flash_mode,
			"--flash_freq", flash_freq,
			"--flash_size", flash_size,
			"-o", output_path,
			elf_path
		};

		try {
			if (listenOnProcess(esptoolPath.endsWith(".py")? elf2imageLinux : elf2imageWindows) != 0) {
			} else {
				editor.statusNotice("Bootloader created!");
			}
		} catch (Exception e) {
			emitError("Process error" + e.getMessage() );
		}
	}


	public boolean createPartitionCsv( String csvFilePath ) {
		if( confirm_overwrite && Files.exists(Paths.get(csvFilePath)) ) {
			if ( !ui.confirmDialogOverwrite(csvFilePath + " exists\nOverwrite?", "File exists" ) )
				return false;
			System.out.println("Will overwrite " + csvFilePath);
		}
		calculateCSV();
		try (FileWriter writer = new FileWriter(csvFilePath)) {
			// Write the exported data to the CSV file
			for (String partitionData : createdPartitionsData) {
				writer.write(partitionData + "\n");
			}
		} catch (IOException ex) {
			emitError("Error exporting CSV: " + ex.getMessage());
			return false;
		}
		if (Files.notExists(Paths.get(csvFilePath))) {
			emitError("Failed to create " + csvFilePath);
			return false;
		}
		editor.statusNotice("Created partitions.csv" );
		return true;
	}



	public boolean createPartitionsBin( String buildPath ) {

		String csvFilePath = sketchDir + "/partitions.csv";

		if (! createPartitionCsv(csvFilePath) )
			return false;

		String sketchName       = editor.getSketch().getName();
		String getEsp32PartPath = platform.getFolder() + "/tools/" + genEsp32PartCmd;
		// Full path to the file that will be created
		String partitionsBinPath = buildPath + "/" + sketchName + ".ino" + ".partitions.bin";

		if (Files.notExists(Paths.get(getEsp32PartPath))) {
			emitError("Partitions Bin Generate Error: gen_esp32part not found!");
			return false;
		}

		if( buildPath == null ) {
			buildPath = getBuildFolderPath();
		}

		// Command to generate partitions.bin
		String[] command = { "python3", getEsp32PartPath, csvFilePath, partitionsBinPath };

		editor.statusNotice("Creating partitions.bin...");

		try {
			// Execute the command
			int exitCode = listenOnProcess(command);
			if (exitCode == 0) {
				editor.statusNotice("partitions.bin created successfully.");
				System.out.println("partitions.bin successfully written at: " + partitionsBinPath);
			} else {
				editor.statusError("Failed to write partitions.bin.");
				return false;
			}
		} catch (Exception e) {
			editor.statusError("An error occurred while creating partitions.bin.");
			e.printStackTrace(); // Print the stack trace for debugging
			return false;
		}

		return true;
	}


	public void handleSPIFFSButton(String buildPath ) {

		if( buildPath == null ) {
			buildPath = getBuildFolderPath();
		}

		String fsName = ui.getPartitionFlashType().getSelectedItem().toString();

		if( !createSPIFFS( buildPath ) ) return;

		// Create a JOptionPane to prompt the user for upload
		int option = JOptionPane.showConfirmDialog(null,
				"Do you want to upload " + fsName + "?", "Upload SPIFFS" + fsName,
				JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {
			uploadSPIFFS();
		}
	}


	private boolean createSPIFFS( String buildPath ) {

		if( buildPath == null ) {
			buildPath = getBuildFolderPath();
		}

		spiStart = 0;
		spiSize = 0;
		spiPage = 256;
		spiBlock = ui.flashSizeMB * 1024;

		String fsName      = ui.getPartitionFlashType().getSelectedItem().toString();
		String mkFsBinName = "mk" + fsName.toLowerCase();
		String mkFsPath    = prefs.getProperty(mkFsBinName+".path");//tool.getAbsolutePath();

		if( mkFsPath == null  || Files.notExists(Paths.get(mkFsPath)) ) {
			emitError(mkFsBinName + " path not found");
			return false;
		}

		if( debug_ui ) {
			System.out.println("Selected filesystem: " + fsName);
			System.out.println("Selected tool: " + mkFsBinName);
			System.out.println("Selected exe: " + mkFsPath);
		}

		serialPort = PreferencesData.get("serial.port");

		calculateCSV();

		String csvFilePath = sketchDir + "/partitions.csv";

		if ( Files.notExists(Paths.get(csvFilePath))) {
			if( ! createPartitionCsv(csvFilePath) )
				return false;
		}

		// Read the partitions.csv file
		try (BufferedReader partitionsReader = new BufferedReader(new FileReader(csvFilePath))) {
			String partitionsLine = "";
			while ((partitionsLine = partitionsReader.readLine()) != null) {
				if (partitionsLine.contains("spiffs") || partitionsLine.contains("littlefs")
						|| partitionsLine.contains("ffat")) {
					String[] partitionsData = partitionsLine.split(",\\s*"); // Split by comma with optional spaces
					if (partitionsData.length >= 5) { // Ensure there are enough elements
						String pStart = partitionsData[3].trim(); // Offset value
						String pSize = partitionsData[4].trim(); // Size value
						spiStart = Long.parseLong(pStart.substring(2), 16); // Convert hex to int
						spiSize  = Long.parseLong(pSize.substring(2), 16); // Convert hex to int
					}
				}
			}
			if (spiSize == 0) {
				emitError(fsName + " Error: partition size could not be found!");
				return false;
			}
		} catch (Exception e) {
			editor.statusError(e);
			return false;
		}

		// make sure the serial port or IP is defined
		if (serialPort == null || serialPort.isEmpty()) {
			emitError(fsName + " Error: serial port not defined!");
			return false;
		}

		// find espota if IP else find esptool
		isNetwork = (serialPort.split("\\.").length == 4);

		// load a list of all files in the data folder, if any

		int fileCount = 0;

		File dataFolder = new File(editor.getSketch().getFolder(), "data");

		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}
		if (dataFolder.exists() && dataFolder.isDirectory()) {
			File[] files = dataFolder.listFiles();
			if (files.length > 0) {
				for (File file : files) {
					if ((file.isDirectory() || file.isFile()) && !file.getName().startsWith("."))
						fileCount++;
				}
			}
		}

		String dataPath = dataFolder.getAbsolutePath();
		String sketchName = editor.getSketch().getName();
		imagePath = buildPath + "/" + sketchName + ".spiffs.bin";
		uploadSpeed = BaseNoGui.getBoardPreferences().get("upload.speed");

		if( fileCount == 0 && creating_empty_spiffs_needs_confirmation ) {
			Object[] options = { "Yes", "No" };
			String title = "Create " + fsName;
			String message = "No files have been found in your data folder!\nAre you sure you want to create an empty "
					+ fsName + " image?";

			if (JOptionPane.showOptionDialog(editor, message, title, JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[1]) != JOptionPane.YES_OPTION) {
				emitError(fsName + " Warning: " + mkFsBinName + " canceled!");
				return false;
			}
		} else {
			System.out.println("Creating empty spiffs.bin (no data folder found in sketch folder)");
		}

		editor.statusNotice("Creating " + fsName + "...");
		System.out.println("[" + fsName + "] data   : " + dataPath);
		System.out.println("[" + fsName + "] start  : " + spiStart);
		System.out.println("[" + fsName + "] size   : " + (spiSize));
		System.out.println("[" + fsName + "] page   : " + spiPage);
		System.out.println("[" + fsName + "] block  : " + spiBlock);

		try {

			String baseMkFsArgs[] = { mkFsPath, "-c", dataPath, "-p", spiPage + "", "-b", spiBlock + "", "-s", spiSize + "", imagePath };
			String mkFatFsArgs[]  = { mkFsPath, "-c", dataPath,                                          "-s", spiSize + "", imagePath };

			String mkFsArgs[] =  fsName.equals("FatFS") ? mkFatFsArgs : baseMkFsArgs;

			if (listenOnProcess( mkFsArgs ) != 0) {
				emitError("Failed to create " + fsName + "!");
				return false;
			}
			editor.statusNotice("Completed creating " + fsName);
			System.out.println("[" + fsName + "] Data partition successfully written at " + imagePath);
		} catch (Exception e) {
			emitError("Failed to create " + fsName + "!");
			return false;
		} finally {
			// NOTE: since partitions.csv is now taken from the sketch folder it
			//       is no longer pertinent to delete it after use
		}
		return true;
	}

	private void uploadSPIFFS() {
		String fsName = ui.getPartitionFlashType().getSelectedItem().toString();
		editor.statusNotice("Uploading " + fsName + "...");
		System.out.println("[" + fsName + "] upload : " + imagePath);

		if( espotaPath == null || Files.notExists(Paths.get(espotaPath)) ) {
			emitError("espota tool not found");
			return;
		}
		if( esptoolPath == null || Files.notExists(Paths.get(esptoolPath)) ) {
			emitError("esptool path not found");
			return;
		}

		if (isNetwork) {
			System.out.println("[" + fsName + "] IP     : " + serialPort);
			System.out.println();

			if (espotaPath.endsWith(".py"))
				sysExec(new String[] { pythonCmd, espotaPath, "-i", serialPort, "-p", "3232", "-s", "-f",
						imagePath });
			else
				sysExec(new String[] { espotaPath, "-i", serialPort, "-p", "3232", "-s", "-f",
						imagePath });
		} else {
			System.out.println("[" + fsName + "] address: " + spiStart);
			System.out.println("[" + fsName + "] port   : " + serialPort);
			System.out.println("[" + fsName + "] speed  : " + uploadSpeed);
			System.out.println("[" + fsName + "] mode   : " + flashMode);
			System.out.println("[" + fsName + "] freq   : " + flashFreq);
			System.out.println();
			if (esptoolPath.endsWith(".py"))
				sysExec(new String[] { pythonCmd, esptoolPath, "--chip", mcu, "--baud", uploadSpeed,
						"--port", serialPort, "--before", "default_reset", "--after", "hard_reset", "write_flash", "-z",
						"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", "" + spiStart,
						imagePath });
			else
				sysExec(new String[] { esptoolPath, "--chip", mcu, "--baud", uploadSpeed, "--port",
						serialPort, "--before", "default_reset", "--after", "hard_reset", "write_flash", "-z",
						"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", "" + spiStart,
						imagePath });
		}
	}


	public void handleMergedBinButton(String buildPath ) {

		if( buildPath == null ) {
			buildPath = getBuildFolderPath();
		}

		String sketchName  = editor.getSketch().getName();
		String appImage    = buildPath + "/" + sketchName + ".ino" + ".bin";

		// NOTE: It's not a good idea to trigger Arduino build from the applet, everything appears
		// frozen until the build is finished. Just nudge the user with an alert box.
		if (Files.notExists(Paths.get(appImage))) {
			JOptionPane.showMessageDialog(null, "Please compile the sketch in Arduino IDE first!");
			return;
		}

		if( ! createPartitionsBin(buildPath) ) return;
		if( ! createSPIFFS(buildPath) ) return;
		if( ! createMergedBin(buildPath) ) return;

		// Create a JOptionPane to prompt the user
		int option = JOptionPane.showConfirmDialog(null,
				"Do you want to upload merged binary?", "Upload merged binary",
				JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {
			try {
				Thread.sleep(2000);
				uploadMergedBin(buildPath);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}



	public String getBootloaderImagePath() {
		String sketchName  = editor.getSketch().getName();
		String buildPath   = getBuildFolderPath();
		String variantName = BaseNoGui.getBoardPreferences().get("build.variant");
		String variantDir  = platformPath+"/variants/" + variantName;
		// precedence: source > variant > build files
		String searchPaths[] = {
			sketchDir  + "/bootloader.bin", // in source directory
			variantDir + "/bootloader.bin", // in board variants directory
			buildPath  + "/" + sketchName + ".ino" + ".bootloader.bin" // in build directory
		};
		// walk the array
		for( int i=0; i<searchPaths.length; i++ ) {
			if( Files.exists(Paths.get(searchPaths[i])) ) {
				// got a match!
				return searchPaths[i];
			}
		}
		// ask confirmation to build bootloader
		int option = JOptionPane.showConfirmDialog(null, "bootloader.bin not found, buil it?", "Build bootloader?", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			String saveBootloaderPath = buildPath  + "/" + sketchName + ".ino" + ".bootloader.bin";
			buidBootloaderBin( saveBootloaderPath );
			// if successful, the build path now has a bootloader.bin newly created
			if( Files.exists(Paths.get(saveBootloaderPath)) ) {
				// bootloader build succeeded
				return saveBootloaderPath;
			}
			emitError("esptool Failed to create " + saveBootloaderPath );
		} else {
			emitError("Action cancelled by user");
		}
		return null;
	}



	private boolean createMergedBin( String buildPath ) {

		if( buildPath == null ) {
			buildPath = getBuildFolderPath();
		}

		if (Files.notExists(Paths.get(buildPath))) {
			JOptionPane.showMessageDialog(null, "Please compile the sketch in Arduino IDE first!");
			return false;
		}

		String fsName          = ui.getPartitionFlashType().getSelectedItem().toString();
		String bootImage       = platform.getFolder() + "/tools/partitions/boot_app0.bin";
		String partitionsImage = buildPath + "/" + sketchName + ".ino.partitions.bin";
		String spiffsImage     = buildPath + "/" + sketchName + ".spiffs.bin";
		String mergedImage     = buildPath + "/" + sketchName + ".merged.bin";
		String appImage        = buildPath + "/" + sketchName + ".ino.bin";

		if( espotaPath == null || Files.notExists(Paths.get(espotaPath)) ) {
			emitError("espota tool not found");
			return false;
		}

		if( esptoolPath == null || Files.notExists(Paths.get(esptoolPath)) ) {
			emitError("esptool path not found");
			return false;
		}

		// make sure the serial port or IP is defined
		serialPort = PreferencesData.get("serial.port");
		if (serialPort == null || serialPort.isEmpty()) {
			emitError(fsName + " Error: serial port not defined!");
			return false;
		}
		// find espota if IP else find esptool
		isNetwork = (serialPort.split("\\.").length == 4);
		// figure out the path to the relevant bootloader.bin, create it if necessary
		String bootloaderImage = getBootloaderImagePath();
		if( bootloaderImage == null ) {
			return false;
		}
		// check that all necessary files are in place
		String checkFiles[] = { bootloaderImage, partitionsImage, bootImage, appImage, spiffsImage };
		for( int i=0; i<checkFiles.length; i++ ) {
			if (Files.notExists(Paths.get(checkFiles[i]))) {
				emitError("Missing file: " + checkFiles[i] + ". Forgot to compile the sketch?");
				return false;
			}
		}

		String bootloaderOffset = BaseNoGui.getBoardPreferences().get("build.bootloader_addr");
		String partitionsOffset = "0x8000";
		String bootOffset       = "0xe000";
		String appOffset        = "0x10000";
		String spiffsOffset     = ui.getSpiffsOffset();

		String mcu              = BaseNoGui.getBoardPreferences().get("build.mcu");
		String serialPort       = PreferencesData.get("serial.port");
		String uploadSpeed      = BaseNoGui.getBoardPreferences().get("upload.speed");
		String flashMode        = BaseNoGui.getBoardPreferences().get("build.flash_mode");
		String flashFreq        = BaseNoGui.getBoardPreferences().get("build.flash_freq");
		String flashSize        = ui.flashSizeMB + "MB";

		editor.statusNotice("Creating merged binary...");
		System.out.println("[Merged bin] creation:");

		System.out.println("[Merged binary] mcu: " + mcu);
		System.out.println("[Merged binary] port   : " + serialPort);
		System.out.println("[Merged binary] speed  : " + uploadSpeed);
		System.out.println("[Merged binary] mode   : " + flashMode);
		System.out.println("[Merged bbinary freq   : " + flashFreq);

		String[] mergeCommand = { pythonCmd, esptoolPath, "--chip", mcu, "merge_bin", "-o", mergedImage,
				"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", flashSize, bootloaderOffset,
				bootloaderImage, partitionsOffset, partitionsImage, bootOffset, bootImage, appOffset, appImage,
				spiffsOffset, spiffsImage };

		String[] mergeWindowsCommand = { esptoolPath, "--chip", mcu, "merge_bin", "-o", mergedImage,
				"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", flashSize, bootloaderOffset,
				bootloaderImage, partitionsOffset, partitionsImage, bootOffset, bootImage, appOffset, appImage,
				spiffsOffset, spiffsImage };

		sysExec( esptoolPath.endsWith(".py") ? mergeCommand : mergeWindowsCommand );

		return true;
	}

	private void uploadMergedBin( String buildPath ) {

		if( buildPath == null ) {
			buildPath = getBuildFolderPath();
		}

		String fsName = ui.getPartitionFlashType().getSelectedItem().toString();

		if( espotaPath == null || Files.notExists(Paths.get(espotaPath)) ) {
			emitError("espota tool not found");
			return;
		}
		if( esptoolPath == null || Files.notExists(Paths.get(esptoolPath)) ) {
			emitError("esptool path not found");
			return;
		}

		serialPort = PreferencesData.get("serial.port");

		// make sure the serial port or IP is defined
		if (serialPort == null || serialPort.isEmpty()) {
			emitError(fsName + " Error: serial port not defined!");
			return;
		}

		// find espota if IP else find esptool
		isNetwork = (serialPort.split("\\.").length == 4);

		String sketchName = editor.getSketch().getName();
		String mergedImage = buildPath + "/" + sketchName + "merged" + ".bin";

		String mergedOffset = "0x0";

		editor.statusNotice("Creating merged binary...");
		System.out.println("[Merged binary] creation:");

		if (isNetwork) {

			System.out.println("[Merged bin] IP: " + serialPort);
			System.out.println();
			String[] writeFlashCmdLinux   = { pythonCmd, espotaPath, "-i", serialPort, "-p", "3232", "-s", "-f", mergedImage };
			String[] writeFlashCmdWindows = { espotaPath, "-i", serialPort, "-p", "3232", "-s", "-f", mergedImage };

			sysExec(espotaPath.endsWith(".py")?writeFlashCmdLinux:writeFlashCmdWindows);

		} else {

			System.out.println("[Merged binary] mcu: " + mcu);
			System.out.println("[Merged binary] port   : " + serialPort);
			System.out.println("[Merged binary] speed  : " + uploadSpeed);
			System.out.println("[Merged binary] mode   : " + flashMode);
			System.out.println("[Merged binary] freq(exec)   : " + flashFreq);

			String[] writeFlashCmdLinux   = { pythonCmd, esptoolPath, "--chip", mcu, "--baud", uploadSpeed,
					"--port", serialPort, "--before", "default_reset", "--after", "hard_reset", "write_flash", "-z",
					"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", mergedOffset,
					mergedImage };
			String[] writeFlashCmdWindows = { esptoolPath, "--chip", mcu, "--baud", uploadSpeed,
					"--port", serialPort, "--before", "default_reset", "--after", "hard_reset", "write_flash", "-z",
					"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", mergedOffset,
					mergedImage };

			sysExec(esptoolPath.endsWith(".py")?writeFlashCmdLinux:writeFlashCmdWindows);

		}
	}
}
