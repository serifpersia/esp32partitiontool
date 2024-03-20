package com.serifpersia.esp32partitiontool;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.lang.Process;
import java.lang.Runtime;




public class FileManager {

	private UI ui; // Reference to the UI instance
	public AppSettings settings;

	private ArrayList<String> createdPartitionsData;

	// Constructor to initialize FileManager with UI instance and Editor instance
	public FileManager(UI ui, AppSettings settings) {
		this.ui = ui;
		this.settings = settings;
		ui.setAppSettings( this, settings );
	}

	public void setUIController(UIController controller) {
		ui.setController(controller);
	}

	public void emitMessage(String msg) {
		if( settings.hasFSPanel ) {
			ui.fsPanel.emitMessage( msg, false );
		} else {
			System.out.println(msg);
		}
	}

	public void emitError(String msg) {
		if( settings.hasFSPanel ) {
			ui.fsPanel.emitMessage( msg, true );
		} else {
			System.err.println(msg);
		}
	}

	public void loadDefaultCSV() {
		if (settings.get("csvFile.path") != null) {
			importCSV(settings.get("csvFile.path"));
			ui.setFrameTitleNeedsSaving(false);
			return;
		}

		String defaultCSVData = "# Name,   Type, SubType,  Offset,   Size,  Flags\n"
				+ "nvs,       data, nvs,          0x9000,    0x5000,\n"
				+ "otadata,   data, ota,          0xE000,    0x2000,\n"
				+ "app0,      app,  ota_0,       0x10000,  0x140000,\n"
				+ "app1,      app,  ota_1,      0x150000,  0x140000,\n"
				+ "spiffs,    data, spiffs,     0x290000,  0x160000,\n"
				+ "coredump,  data, coredump,   0x3F0000,   0x10000,\n";

		try (BufferedReader reader = new BufferedReader(new StringReader(defaultCSVData))) {
			processCSV(reader);
		} catch (IOException e) {
			emitError("Error processing default CSV data: " + e.getMessage());
		}

		ui.calculateSizeHex();
		ui.calculateOffsets();
		ui.updatePartitionFlashVisual();
		ui.validateSubtypes();

		JTextField lastPartitionOffsetField = ui.getPartitionOffsets(ui.lastIndex + 1);

		if (lastPartitionOffsetField != null && !lastPartitionOffsetField.getText().isEmpty()) {
			// Get the text from the JTextField and parse it as hexadecimal
			String hexOffset = lastPartitionOffsetField.getText();
			long lastOffset = Long.parseLong(hexOffset, 16);

			// Calculate offset in MB, rounding up to the nearest integer
			long offsetInMB = (lastOffset + 1024 * 1024 - 1) / (1024 * 1024);

			ui.flashSizeMB = (int) offsetInMB;
		}

		String flashSizeString = String.valueOf(ui.flashSizeMB);
		ui.getFlashSize().setSelectedItem(flashSizeString);
		ui.setFrameTitleNeedsSaving(true);
	}

	public void importCSV(String fileName) {
		File readerFile = null;

		String directory;

		if (fileName == null) { // show a dialog
			FileDialog filedialog = new FileDialog(ui.getFrame(), "Select CSV file to import", FileDialog.LOAD);
			FilenameFilter csvFilter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".csv");
				}
			};
			filedialog.setFilenameFilter(csvFilter);

			if (settings.get("csvDir.path") != null) {
				System.out.println("dialog open at last CSV Dir: " + settings.get("csvDir.path") );
				filedialog.setDirectory( settings.get("csvDir.path") );
			}
			filedialog.setFile("*.csv");
			filedialog.setAlwaysOnTop(true);
			filedialog.setVisible(true);

			directory = filedialog.getDirectory();
			if (directory == null || directory.isEmpty())
				return;
			//settings.set("csvDir.path", directory );
			//System.out.println("dialog returned CSV Dir: " + settings.get("csvDir.path") );
			fileName = filedialog.getFile();
			filedialog.dispose();
			if (fileName == null || fileName.isEmpty())
				return;
			readerFile = new File(directory, fileName);
		} else {
			readerFile = new File(fileName);
			directory = readerFile.getParent();
			// update last csvDir
			// settings.set("csvDir.path", readerFile.getParent() );
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(readerFile))) {
			processCSV(reader);
			settings.set("csvFile.path", directory + "/" + basename(fileName) );
			ui.updatePartitionLabel( fileName );

		} catch (IOException e) {
			emitError("Error reading CSV file: " + e.getMessage());
		}

		ui.calculateSizeHex();
		ui.calculateOffsets();
		ui.updatePartitionFlashVisual();
		ui.validateSubtypes();

		JTextField lastPartitionOffsetField = ui.getPartitionOffsets(ui.lastIndex + 1);

		if (lastPartitionOffsetField != null && !lastPartitionOffsetField.getText().isEmpty()) {
			// Get the text from the JTextField and parse it as hexadecimal
			String hexOffset = lastPartitionOffsetField.getText();
			long lastOffset = Long.parseLong(hexOffset, 16);

			// Calculate offset in MB, rounding up to the nearest integer
			long offsetInMB = (lastOffset + 1024 * 1024 - 1) / (1024 * 1024);

			ui.flashSizeMB = (int) offsetInMB;
		}

		String flashSizeString = String.valueOf(ui.flashSizeMB);
		ui.getFlashSize().setSelectedItem(flashSizeString);
		if( settings.hasFSPanel ) {
			ui.fsPanel.updatePartitionFlashTypeLabel();
		}
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

		if (columns.length < 5)
			return;

		String partitionTypeStr = columns[1].trim().toLowerCase();

		// partition type can be numeric
		if (!partitionTypeStr.equals("app") && !partitionTypeStr.equals("data")) {
			partitionTypeStr = stringToDec(partitionTypeStr) == 0 ? "app" : "data";
		}

		long bytesOffset = stringToDec(columns[3].trim());
		long byteSize = stringToDec(columns[4].trim());
		String kbSize = stringToKb(columns[4].trim());

		String cells[] = { columns[0].trim(), // name
				partitionTypeStr, // type
				columns[2].trim(), // subtype
				kbSize, // size
				String.format("%X", byteSize), // sizeHex
				String.format("%X", bytesOffset) // offsetHex
		};

		CSVRow csvRow = new CSVRow(cells);
		ui.addCSVRow(csvRow);
	}

	public static String basename(String path) {
		return new File(path).getName();
	}

	public static String dirname(String path) {
		return new File(path).getParent();
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

	private String formatKilobytes(double kilobytes) {
		// Check if the kilobytes value is a whole number
		if (kilobytes % 1 == 0) {
			return String.format("%.0f", kilobytes);
		} else {
			return String.format("%.2f", kilobytes);
		}
	}

	private void calculateCSV() {
		int numOfItems = ui.getNumOfItems();
		createdPartitionsData = new ArrayList<>();
		createdPartitionsData.add("# Name,   Type, SubType,  Offset,   Size,  Flags");

		for (int i = 0; i < numOfItems; i++) {
			CSVRow csvRow = ui.getCSVRow(i);
			if (csvRow.enabled.isSelected()) {
				String exported_csvPartition = csvRow.toString();
				createdPartitionsData.add(exported_csvPartition);
			}
		}
	}

	public boolean saveCSV( File file ) {
		calculateCSV();

		if( file == null ) {
			file = new File( settings.get("sketchDir.path"), "/partitions.csv" );
		}

		try {
			FileWriter writer = new FileWriter(file);
			for (String partitionData : createdPartitionsData) {
				writer.write(partitionData + "\n");
			}
			writer.close();
			//System.out.println("partitions.csv written at: " + csvFile );
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public boolean exportCSV() {
		String sketchDir = settings.get("sketchDir.path");

		if( sketchDir == null ) {
			sketchDir = new File(FileManager.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
			System.out.println("Forced sketchDir to " + sketchDir );
		}

		FilenameFilter csvFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".csv");
			}
		};

		String csvName = ui.getPartitionLabel();
		if( !csvName.endsWith(".csv")) {
			csvName = "partitions.csv";
		}

		FileDialog filedialog = new FileDialog(ui.getFrame(), "Export as CSV", FileDialog.SAVE);
		filedialog.setDirectory(sketchDir);
		filedialog.setFile(csvName);
		filedialog.setFilenameFilter(csvFilter);
		filedialog.setVisible(true);

		if (filedialog.getFile() == null) {
			// aborted by user
			System.out.println("File selection aborted by user");
			return false;
		}

		String csvPath = filedialog.getDirectory() + filedialog.getFile();

		filedialog.dispose();

		File csvFile = new File( csvPath );

		boolean success = saveCSV( csvFile );
		if( success ) {
			//settings.set("csvFile.path", csvFile.toString() );
			//settings.set("csvDir.path", csvFile.getParent() );
			System.out.println("Saved file as " + csvPath );
			return true;
		}

		emitError("Failed to save file as " + csvPath );

		return false;
	}

	// called internally either by createMergedBin() or uploadSPIFFS()
	public void createSPIFFS( AppSettings.EventCallback callbacks ) {
		if( ! settings.hasFSPanel ) return;

		String buildPath = settings.get("build.path");
		String sketchDir = settings.get("sketchDir.path");
		String sketchName = settings.get("sketch.name");

		// TODO: check if folder exists, prompt user to compile from the Arduino IDE
		if( buildPath == null || sketchDir == null || sketchName == null ) {
			callbacks.onFail();
			return;
		}

		long spiStart = 0;
		long spiSize = 0;
		int spiPage = 256;
		int spiBlock = ui.flashSizeMB * 1024;

		String fsName = ui.fsPanel.getPartitionFlashTypes().getSelectedItem().toString();
		String mkFsBinName = "mk" + fsName.toLowerCase();
		String mkFsPath = settings.get(mkFsBinName + ".path");

		if (mkFsPath == null || Files.notExists(Paths.get(mkFsPath))) {
			emitError(mkFsBinName + " path not found");
			callbacks.onFail();
			return;
		}

		String csvFilePath = buildPath + "/partitions.csv";

		if (Files.notExists(Paths.get(csvFilePath))) {
			// TODO: prompt user to hit the "Export CSV" in the applet, and "compile" button in Arduino IDE
			Runnable onSuccess = () -> createSPIFFS( callbacks );
			Runnable onFail = () -> {
				emitError("Build failed, check Arduino console for logs");
				callbacks.onFail();
			};
			final AppSettings.EventCallback buildCallbacks = new AppSettings.EventCallback(
				callbacks.onBefore,
				callbacks.onAfter,
				onSuccess,
				onFail
			);
			settings.build( ui.fsPanel.getProgressBar(), buildCallbacks);
			return;
		}

		settings.set("spiffs.addr", null );

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
						spiSize = Long.parseLong(pSize.substring(2), 16); // Convert hex to int
						settings.set("spiffs.addr", pStart );
						System.out.println("settings spiffs.addr at offset " + pStart);
					}
				}
			}
			if (spiSize == 0) {
				emitError(fsName + " Error: partition size could not be found!");
				callbacks.onFail();
				return;
			}
		} catch (Exception e) {
			emitError(e.toString());
			callbacks.onFail();
			return;
		}

		// load a list of all files in the data folder, if any

		int fileCount = 0;

		File dataFolder = new File( sketchDir + "/data");

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
		String imagePath = buildPath + "/" + sketchName + ".spiffs.bin";

		if (fileCount == 0) {
			System.out.println("No files have been found in your data folder, an empty image has been created");
		}

		System.out.println("Creating " + fsName + "...");
		System.out.println("[" + fsName + "] data   : " + dataPath);
		System.out.println("[" + fsName + "] start  : " + spiStart);
		System.out.println("[" + fsName + "] size   : " + (spiSize));
		System.out.println("[" + fsName + "] page   : " + spiPage);
		System.out.println("[" + fsName + "] block  : " + spiBlock);

		callbacks.onBefore();

		try {

			String baseMkFsArgs[] = { mkFsPath, "-c", dataPath, "-p", spiPage + "", "-b", spiBlock + "", "-s",
					spiSize + "", imagePath };
			String mkFatFsArgs[] = { mkFsPath, "-c", dataPath, "-s", spiSize + "", imagePath };

			String mkFsArgs[] = fsName.equals("FatFS") ? mkFatFsArgs : baseMkFsArgs;

			if (listenOnProcess(mkFsArgs) != 0) {
				emitError("Failed to create " + fsName + "!");
				callbacks.onFail();
				return;
			}
			System.out.println("Completed creating " + fsName);
			System.out.println("[" + fsName + "] Data partition successfully written at " + imagePath);
		} catch (Exception e) {
			emitError("Failed to create " + fsName + "!");
			callbacks.onFail();
			return;
		} finally {

		}

		boolean success = Files.exists(Paths.get(imagePath));

		if( success ) {
			callbacks.onSuccess();
		} else {
			callbacks.onFail();
		}

		callbacks.onAfter();
	}

	public void uploadSPIFFS(final AppSettings.EventCallback callbacks) {
		if( !settings.hasFSPanel ) return;

		String fsName = ui.fsPanel.getPartitionFlashTypes().getSelectedItem().toString();
		String espotaPath = settings.get("espota.path");
		String esptoolPath = settings.get("esptool.path");
		String serialPort = settings.get("serial.port");
		String pythonCmd = settings.get("pythonCmd");
		String uploadSpeed = settings.get("upload.speed");
		String flashMode = settings.get("flashMode");
		String flashFreq = settings.get("flashFreq");
		String mcu = settings.get("build.mcu");
		String buildPath = settings.get("build.path");
		String sketchDir = settings.get("sketchDir.path");
		String sketchName = settings.get("sketch.name");
		String spiStart = settings.get("spiffs.addr");

		String imagePath = buildPath + "/" + sketchName + ".spiffs.bin";
		boolean isNetwork = serialPort!=null && (serialPort.split("\\.").length == 4);

		if( Files.notExists(Paths.get(imagePath)) ) {

			Runnable onSuccess = () -> uploadSPIFFS(callbacks);
			Runnable onFail = () -> {
				emitError("spiffs.bin creation failed, check Arduino console for logs");
				callbacks.onFail();
			};
			final AppSettings.EventCallback createSPIFFSCallbacks = new AppSettings.EventCallback(
				callbacks.onBefore,
				callbacks.onAfter,
				onSuccess,
				onFail
			);
			createSPIFFS(createSPIFFSCallbacks);
			return;
		}

		if( spiStart == null ) {
			emitError("This no spiffs partition offset detected in csv data :(");
			callbacks.onFail();
			return;
		}

		// if( ! createSPIFFS() ) return false;

		System.out.println("Uploading " + fsName + "...");
		System.out.println("[" + fsName + "] upload : " + imagePath);

		if (isNetwork == false && serialPort == null ) {
			emitError("serialPort not found, try to close and reopen the applet");
			callbacks.onFail();
			return;
		}

		callbacks.onBefore();

		if (isNetwork) {

			if (espotaPath == null || Files.notExists(Paths.get(espotaPath))) {
				emitError("espota tool not found");
				callbacks.onFail();
				return;
			}

			System.out.println("[" + fsName + "] IP     : " + serialPort);
			System.out.println();

			if (espotaPath.endsWith(".py"))
				listenOnProcess(new String[] { pythonCmd, espotaPath, "-i", serialPort, "-p", "3232", "-s", "-f", imagePath });
			else
				listenOnProcess(new String[] { espotaPath, "-i", serialPort, "-p", "3232", "-s", "-f", imagePath });
		} else {

			if (esptoolPath == null || Files.notExists(Paths.get(esptoolPath))) {
				emitError("esptool path not found");
				callbacks.onFail();
				return;
			}

			System.out.println("[" + fsName + "] address: " + spiStart);
			System.out.println("[" + fsName + "] port   : " + serialPort);
			System.out.println("[" + fsName + "] speed  : " + uploadSpeed);
			System.out.println("[" + fsName + "] mode   : " + flashMode);
			System.out.println("[" + fsName + "] freq   : " + flashFreq);
			System.out.println();
			if (esptoolPath.endsWith(".py"))
				listenOnProcess(new String[] { pythonCmd, esptoolPath, "--chip", mcu, "--baud", uploadSpeed, "--port",
						serialPort, "--before", "default_reset", "--after", "hard_reset", "write_flash", "-z",
						"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", "" + spiStart,
						imagePath });
			else
				listenOnProcess(new String[] { esptoolPath, "--chip", mcu, "--baud", uploadSpeed, "--port", serialPort,
						"--before", "default_reset", "--after", "hard_reset", "write_flash", "-z", "--flash_mode",
						flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", "" + spiStart, imagePath });
		}

		callbacks.onAfter();
		return;
	}

	public void createMergedBin( final AppSettings.EventCallback callbacks ) {
		if( ! settings.hasFSPanel ) return;

		String buildPath = settings.get("build.path");

		if (Files.notExists(Paths.get(buildPath))) {
			emitError("Please compile the sketch in Arduino IDE first!");
			return;
		}

		String fsName = ui.fsPanel.getPartitionFlashTypes().getSelectedItem().toString();

		String platformPath = settings.get("platform.path");
		String sketchName = settings.get("sketch.name");
		String esptoolPath = settings.get("esptool.path");
		String mcu = settings.get("build.mcu");
		String flashMode = settings.get("flashMode");
		String flashFreq = settings.get("flashFreq");
		String pythonCmd = settings.get("pythonCmd");

		String bootImage = platformPath + "/tools/partitions/boot_app0.bin";
		String bootloaderImage = buildPath + "/" + sketchName + ".ino.bootloader.bin"; // settings.get("bootloader.path");
		String partitionsImage = buildPath + "/" + sketchName + ".ino.partitions.bin";
		String spiffsImage = buildPath + "/" + sketchName + ".spiffs.bin";
		String mergedImage = buildPath + "/" + sketchName + ".merged.bin";
		String appImage = buildPath + "/" + sketchName + ".ino.bin";

		if (esptoolPath == null || Files.notExists(Paths.get(esptoolPath))) {
			emitError("esptool path not found");
			callbacks.onFail();
			return;
		}

		if (Files.notExists(Paths.get(spiffsImage))) {
			Runnable onSuccess = () -> createMergedBin(callbacks);
			Runnable onFail = () ->  {
				emitError("spiffs.bin creation failed, check Arduino console for logs");
				callbacks.onFail();
			};
			final AppSettings.EventCallback createSPIFFSCallbacks = new AppSettings.EventCallback(
				callbacks.onBefore,
				callbacks.onAfter,
				onSuccess,
				onFail
			);
			createSPIFFS(createSPIFFSCallbacks);
			return;
		}

		// check that all necessary files are in place
		String checkFiles[] = { bootloaderImage, partitionsImage, bootImage, appImage, spiffsImage };
		for (int i = 0; i < checkFiles.length; i++) {
			if ( checkFiles[i] == null || Files.notExists(Paths.get(checkFiles[i]))) {
				System.out.printf("Missing file #%d: %s. Forgot to compile the sketch?", i, checkFiles[i] );
				//emitError("Missing file: " + checkFiles[i] + ". Forgot to compile the sketch?");
				callbacks.onFail();
				return;
			}
		}

		String bootloaderOffset = settings.get("build.bootloader_addr");
		String partitionsOffset = "0x8000";
		String bootOffset = "0xe000";
		String appOffset = "0x10000";
		String spiffsOffset = ui.getSpiffsOffset();

		String flashSize = ui.flashSizeMB + "MB";

		System.out.println("Creating merged binary...");
		System.out.println("[Merged bin] creation:");
		System.out.println("[Merged binary] mcu: " + mcu);
		System.out.println("[Merged binary] mode   : " + flashMode);
		System.out.println("[Merged bbinary freq   : " + flashFreq);

		String[] mergeCommand = { pythonCmd, esptoolPath, "--chip", mcu, "merge_bin", "-o", mergedImage, "--flash_mode",
				flashMode, "--flash_freq", flashFreq, "--flash_size", flashSize, bootloaderOffset, bootloaderImage,
				partitionsOffset, partitionsImage, bootOffset, bootImage, appOffset, appImage, spiffsOffset,
				spiffsImage };

		String[] mergeWindowsCommand = { esptoolPath, "--chip", mcu, "merge_bin", "-o", mergedImage, "--flash_mode",
				flashMode, "--flash_freq", flashFreq, "--flash_size", flashSize, bootloaderOffset, bootloaderImage,
				partitionsOffset, partitionsImage, bootOffset, bootImage, appOffset, appImage, spiffsOffset,
				spiffsImage };

		callbacks.onBefore();

		int ret = listenOnProcess(esptoolPath.endsWith(".py") ? mergeCommand : mergeWindowsCommand);

		boolean success = ret != -1;

		if( success ) {
			callbacks.onSuccess();
		} else {
			callbacks.onFail();
		}
		callbacks.onAfter();
	}

	public void uploadMergedBin(final AppSettings.EventCallback callbacks) {
		String buildPath = settings.get("build.path");

		String fsName = ui.fsPanel.getPartitionFlashTypes().getSelectedItem().toString();

		String esptoolPath = settings.get("esptool.path");
		String espotaPath = settings.get("espota.path");
		String serialPort = settings.get("serial.port");
		String sketchName = settings.get("sketch.name");
		String pythonCmd = settings.get("pythonCmd");
		String uploadSpeed = settings.get("upload.speed");
		String flashMode = settings.get("flashMode");
		String flashFreq = settings.get("flashFreq");
		String mcu = settings.get("build.mcu");
		String mergedImage = buildPath + "/" + sketchName + ".merged.bin";

		if( Files.notExists( Paths.get(mergedImage) ) ) {
			Runnable onSuccess = () -> uploadMergedBin(callbacks);
			Runnable onFail = () -> {
				emitError("Merged bin creation failed, check Arduino console for logs");
				callbacks.onFail();
			};
			final AppSettings.EventCallback createMergedBinCallbacks = new AppSettings.EventCallback(
				callbacks.onBefore,
				callbacks.onAfter,
				onSuccess,
				onFail
			);
			createMergedBin(createMergedBinCallbacks);
			return;
		}

		if (buildPath == null || Files.notExists(Paths.get(buildPath))) {
			emitError("Please compile the sketch in Arduino IDE first!");
			callbacks.onFail();
			return;
		}

		if (espotaPath == null || Files.notExists(Paths.get(espotaPath))) {
			emitError("espota tool not found");
			callbacks.onFail();
			return;
		}
		if (esptoolPath == null || Files.notExists(Paths.get(esptoolPath))) {
			emitError("esptool path not found");
			callbacks.onFail();
			return;
		}

		// make sure the serial port or IP is defined
		if (serialPort == null || serialPort.isEmpty()) {
			emitError("Error: serial/ota port not defined!");
			callbacks.onFail();
			return;
		}

		// find espota if IP else find esptool
		boolean isNetwork = (serialPort.split("\\.").length == 4);

		String mergedOffset = "0x0";

		System.out.println("Creating merged binary...");
		System.out.println("[Merged binary] creation:");

		int cmdres;

		callbacks.onBefore();

		if (isNetwork) {

			callbacks.onFail();
			emitError("Can't upload merged binary on OTA ports, use Serial COM ports!");
			return;
			
		} else {

			System.out.println("[Merged binary] mcu: " + mcu);
			System.out.println("[Merged binary] port   : " + serialPort);
			System.out.println("[Merged binary] speed  : " + uploadSpeed);
			System.out.println("[Merged binary] mode   : " + flashMode);
			System.out.println("[Merged binary] freq(exec)   : " + flashFreq);

			String[] writeFlashCmdLinux = { pythonCmd, esptoolPath, "--chip", mcu, "--baud", uploadSpeed, "--port",
					serialPort, "--before", "default_reset", "--after", "hard_reset", "write_flash", "-z",
					"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", mergedOffset,
					mergedImage };
			String[] writeFlashCmdWindows = { esptoolPath, "--chip", mcu, "--baud", uploadSpeed, "--port", serialPort,
					"--before", "default_reset", "--after", "hard_reset", "write_flash", "-z", "--flash_mode",
					flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", mergedOffset, mergedImage };

			cmdres = listenOnProcess(esptoolPath.endsWith(".py") ? writeFlashCmdLinux : writeFlashCmdWindows);

		}

		if( cmdres != -1 ) {
			callbacks.onSuccess();
		} else {
			callbacks.onFail();
		}

		callbacks.onAfter();

		return;
	}


	private int listenOnProcess(String[] arguments) {
		System.out.println("Running command:\n" + String.join(" ", arguments));
		try {
			// Start the process
			ProcessBuilder processBuilder = new ProcessBuilder(arguments);
			Process p = processBuilder.start();

			// Create a thread to capture the process output
			Thread thread = new Thread() {
				public void run() {
					try {
						// Read the process output stream
						InputStreamReader reader = new InputStreamReader(p.getInputStream());
						int c;
						StringBuilder outputBuilder = new StringBuilder();
						while ((c = reader.read()) != -1) {
							outputBuilder.append((char) c);
							if( c == '\n' ) {
								emitMessage( outputBuilder.toString().trim() );
								outputBuilder.setLength(0);
							}
						}
						reader.close();
						if( outputBuilder.length() > 0 ) {
							emitMessage( outputBuilder.toString().trim() );
							outputBuilder.setLength(0);
						}

						// Read the process error stream
						reader = new InputStreamReader(p.getErrorStream());
						while ((c = reader.read()) != -1) {
							outputBuilder.append((char) c);
							if( c == '\n' ) {
								emitError( outputBuilder.toString().trim() );
								outputBuilder.setLength(0);
							}
						}
						reader.close();
						if( outputBuilder.length() > 0 ) {
							emitError( outputBuilder.toString().trim() );
							outputBuilder.setLength(0);
						}

						// // Set the text of ui.console_logField with the output
						//emitMessage( outputBuilder.toString() );
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			thread.start();

			// Wait for the process to finish
			int res = p.waitFor();
			thread.join();

			return res;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
	}

}
