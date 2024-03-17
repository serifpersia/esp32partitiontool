package com.serifpersia.esp32partitiontool;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.lang.Process;
import java.lang.Runtime;
import java.io.*;

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

	public void emitError(String msg) {
		System.err.println(msg);
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

	public void importCSV(String file) {
		File readerFile = null;

		String directory;

		if (file == null) { // show a dialog
			FileDialog filedialog = new FileDialog(ui.getFrame(), "Select CSV File", FileDialog.LOAD);
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
			file = filedialog.getFile();
			filedialog.dispose();
			if (file == null || file.isEmpty())
				return;
			readerFile = new File(directory, file);
		} else {
			readerFile = new File(file);
			directory = readerFile.getParent();
			// update last csvDir
			// settings.set("csvDir.path", readerFile.getParent() );
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(readerFile))) {
			processCSV(reader);
			settings.set("csvFile.path", directory + "/" + basename(file) );
			ui.updatePartitionLabel( basename(file) );

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
		String filename = path.substring(path.lastIndexOf('/') + 1);
		if (filename == null || filename.equalsIgnoreCase("")) {
			filename = "";
		}
		return filename;
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



	public boolean saveCSV() {
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
    FileDialog filedialog = new FileDialog(ui.getFrame(), "Save partitions.csv", FileDialog.SAVE);
    filedialog.setDirectory(sketchDir);
    filedialog.setFile("partitions.csv");
    filedialog.setFilenameFilter(csvFilter);
    filedialog.setVisible(true);

    if (filedialog.getFile() == null) {
			// aborted by user
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

	public boolean createSPIFFS( Runnable onSuccess ) {
		settings.load();

		if( ! settings.hasFSPanel ) return false;

		String buildPath = settings.get("build.path");
		String sketchDir = settings.get("sketchDir.path");
		String sketchName = settings.get("sketch.name");

		// TODO: check if folder exists, prompt user to compile from the Arduino IDE
		if( buildPath == null || sketchDir == null || sketchName == null ) return false;

		long spiStart = 0;
		long spiSize = 0;
		int spiPage = 256;
		int spiBlock = ui.flashSizeMB * 1024;

		String fsName = ui.fsPanel.getPartitionFlashTypes().getSelectedItem().toString();
		String mkFsBinName = "mk" + fsName.toLowerCase();
		String mkFsPath = settings.get(mkFsBinName + ".path");

		if (mkFsPath == null || Files.notExists(Paths.get(mkFsPath))) {
			emitError(mkFsBinName + " path not found");
			return false;
		}

		String csvFilePath = buildPath + "/partitions.csv";

		if (Files.notExists(Paths.get(csvFilePath))) {
		  // TODO: prompt user to hit the "Export CSV" in the applet, and "compile" button in Arduino IDE
			settings.build( ui.fsPanel.progressBar, new Runnable(){
				@Override
				public void run() {
					// prevent recursion
					if( !createSPIFFS( onSuccess ) ) {
						emitError(csvFilePath + " path not found, compile the sketch first?");
						return;
					}
				}
			});
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
						spiSize = Long.parseLong(pSize.substring(2), 16); // Convert hex to int
					}
				}
			}
			if (spiSize == 0) {
				emitError(fsName + " Error: partition size could not be found!");
				return false;
			}
		} catch (Exception e) {
			emitError(e.toString());
			return false;
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

		try {

			String baseMkFsArgs[] = { mkFsPath, "-c", dataPath, "-p", spiPage + "", "-b", spiBlock + "", "-s",
					spiSize + "", imagePath };
			String mkFatFsArgs[] = { mkFsPath, "-c", dataPath, "-s", spiSize + "", imagePath };

			String mkFsArgs[] = fsName.equals("FatFS") ? mkFatFsArgs : baseMkFsArgs;

			if (listenOnProcess(mkFsArgs) != 0) {
				emitError("Failed to create " + fsName + "!");
				return false;
			}
			System.out.println("Completed creating " + fsName);
			System.out.println("[" + fsName + "] Data partition successfully written at " + imagePath);
		} catch (Exception e) {
			emitError("Failed to create " + fsName + "!");
			return false;
		} finally {

		}

		boolean success = Files.exists(Paths.get(imagePath));

		if( success && onSuccess != null ) onSuccess.run();

		return success;
	}

	public boolean uploadSPIFFS() {
		settings.load();

		if( !settings.hasFSPanel ) return false;

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
			return createSPIFFS( new Runnable() {
				@Override
				public void run() {
					uploadSPIFFS();
				}
			});
		}
		// if( ! createSPIFFS() ) return false;

		System.out.println("Uploading " + fsName + "...");
		System.out.println("[" + fsName + "] upload : " + imagePath);

		if (isNetwork == false && serialPort == null ) {
			emitError("serialPort not found, try to close and reopen the applet");
			return false;
		}

		if (isNetwork) {

			if (espotaPath == null || Files.notExists(Paths.get(espotaPath))) {
				emitError("espota tool not found");
				return false;
			}

			System.out.println("[" + fsName + "] IP     : " + serialPort);
			System.out.println();

			if (espotaPath.endsWith(".py"))
				sysExec(new String[] { pythonCmd, espotaPath, "-i", serialPort, "-p", "3232", "-s", "-f", imagePath });
			else
				sysExec(new String[] { espotaPath, "-i", serialPort, "-p", "3232", "-s", "-f", imagePath });
		} else {

			if (esptoolPath == null || Files.notExists(Paths.get(esptoolPath))) {
				emitError("esptool path not found");
				return false;
			}

			System.out.println("[" + fsName + "] address: " + spiStart);
			System.out.println("[" + fsName + "] port   : " + serialPort);
			System.out.println("[" + fsName + "] speed  : " + uploadSpeed);
			System.out.println("[" + fsName + "] mode   : " + flashMode);
			System.out.println("[" + fsName + "] freq   : " + flashFreq);
			System.out.println();
			if (esptoolPath.endsWith(".py"))
				sysExec(new String[] { pythonCmd, esptoolPath, "--chip", mcu, "--baud", uploadSpeed, "--port",
						serialPort, "--before", "default_reset", "--after", "hard_reset", "write_flash", "-z",
						"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", "" + spiStart,
						imagePath });
			else
				sysExec(new String[] { esptoolPath, "--chip", mcu, "--baud", uploadSpeed, "--port", serialPort,
						"--before", "default_reset", "--after", "hard_reset", "write_flash", "-z", "--flash_mode",
						flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", "" + spiStart, imagePath });
		}

		return true;
	}

	public boolean createMergedBin( Runnable onSuccess ) {
		settings.load();

		if( ! settings.hasFSPanel ) return false;

		String buildPath = settings.get("build.path");

		if (Files.notExists(Paths.get(buildPath))) {
			emitError("Please compile the sketch in Arduino IDE first!");
			return false;
		}

		String fsName = ui.fsPanel.getPartitionFlashTypes().getSelectedItem().toString();

		String platformPath = settings.get("platform.path");
		String sketchName = settings.get("sketch.name");
		String esptoolPath = settings.get("esptool.path");
		String bootloaderImage = settings.get("bootloader.path");
		String mcu = settings.get("build.mcu");
		String flashMode = settings.get("flashMode");
		String flashFreq = settings.get("flashFreq");
		String pythonCmd = settings.get("pythonCmd");

		String bootImage = platformPath + "/tools/partitions/boot_app0.bin";
		String partitionsImage = buildPath + "/" + sketchName + ".ino.partitions.bin";
		String spiffsImage = buildPath + "/" + sketchName + ".spiffs.bin";
		String mergedImage = buildPath + "/" + sketchName + ".merged.bin";
		String appImage = buildPath + "/" + sketchName + ".ino.bin";

		if (esptoolPath == null || Files.notExists(Paths.get(esptoolPath))) {
			emitError("esptool path not found");
			return false;
		}

		if (Files.notExists(Paths.get(spiffsImage))) {
			return createSPIFFS( new Runnable() {
				@Override
				public void run() {
					createMergedBin( onSuccess );
				}
			});
		}

		// check that all necessary files are in place
		String checkFiles[] = { bootloaderImage, partitionsImage, bootImage, appImage, spiffsImage };
		for (int i = 0; i < checkFiles.length; i++) {
			if ( checkFiles[i] == null || Files.notExists(Paths.get(checkFiles[i]))) {
				System.out.printf("Missing file #%d: %s. Forgot to compile the sketch?", i, checkFiles[i] );
				//emitError("Missing file: " + checkFiles[i] + ". Forgot to compile the sketch?");
				return false;
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

		int ret = listenOnProcess(esptoolPath.endsWith(".py") ? mergeCommand : mergeWindowsCommand);

		boolean success = ret != -1;

		if( success && onSuccess != null ) onSuccess.run();

		return success;
	}

	public boolean uploadMergedBin() {

		settings.load();

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
			return createMergedBin(new Runnable() {
				@Override
				public void run() {
					uploadMergedBin();
				}
			});
		}

		if (buildPath == null || Files.notExists(Paths.get(buildPath))) {
			emitError("Please compile the sketch in Arduino IDE first!");
			return false;
		}

		if (espotaPath == null || Files.notExists(Paths.get(espotaPath))) {
			emitError("espota tool not found");
			return false;
		}
		if (esptoolPath == null || Files.notExists(Paths.get(esptoolPath))) {
			emitError("esptool path not found");
			return false;
		}

		// make sure the serial port or IP is defined
		if (serialPort == null || serialPort.isEmpty()) {
			emitError(fsName + " Error: serial port not defined!");
			return false;
		}

		// find espota if IP else find esptool
		boolean isNetwork = (serialPort.split("\\.").length == 4);

		String mergedOffset = "0x0";

		System.out.println("Creating merged binary...");
		System.out.println("[Merged binary] creation:");

		if (isNetwork) {

			System.out.println("[Merged bin] IP: " + serialPort);
			System.out.println();
			String[] writeFlashCmdLinux = { pythonCmd, espotaPath, "-i", serialPort, "-p", "3232", "-s", "-f",
					mergedImage };
			String[] writeFlashCmdWindows = { espotaPath, "-i", serialPort, "-p", "3232", "-s", "-f", mergedImage };

			listenOnProcess(espotaPath.endsWith(".py") ? writeFlashCmdLinux : writeFlashCmdWindows);

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

			listenOnProcess(esptoolPath.endsWith(".py") ? writeFlashCmdLinux : writeFlashCmdWindows);

		}

		return true;
	}

	private int listenOnProcess(String[] arguments) {
		System.out.println("Running command:\n" + String.join(" ", arguments));
		try {
			Runtime runtime = Runtime.getRuntime();
			final Process p = runtime.exec(arguments);
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
			//System.out.println("Thread finished");
			return res;
		} catch (Exception e) {
			System.out.println("Thread errored: " + e.getMessage() );
			return -1;
		}
	}

	private void sysExec(final String[] arguments) {
		Thread thread = new Thread() {
			public void run() {
				try {
					if (listenOnProcess(arguments) != 0) {
					} else {
						System.out.println("Uploaded!");
					}
				} catch (Exception e) {
				}
			}
		};
		thread.start();
	}


}
