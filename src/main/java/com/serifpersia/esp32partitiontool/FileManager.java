package com.serifpersia.esp32partitiontool;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JOptionPane;

import processing.app.PreferencesData;
import processing.app.Editor;
import processing.app.BaseNoGui;
import processing.app.Sketch;
import processing.app.helpers.ProcessUtils;
import processing.app.debug.TargetPlatform;

import org.apache.commons.codec.digest.DigestUtils;
import processing.app.helpers.FileUtils;

public class FileManager {

	private UI ui; // Reference to the UI instance
	private Editor editor; // Reference to the Editor instance

	private String imagePath;
	Boolean isNetwork = false;
	private String serialPort;
	File espota;
	File esptool;
	File gen_esp32part;
	String pythonCmd = "python3";
	String uploadSpeed;
	long spiStart;

	String mcu = BaseNoGui.getBoardPreferences().get("build.mcu");
	String flashMode = BaseNoGui.getBoardPreferences().get("build.flash_mode");
	String flashFreq = BaseNoGui.getBoardPreferences().get("build.flash_freq");

	// Declare spiSize, spiPage, spiBlock here
	long spiSize;
	int spiPage;
	int spiBlock;
	private ArrayList<String> createdPartitionsData;

	// Constructor to initialize FileManager with UI instance and Editor instance
	public FileManager(UI ui, Editor editor) {
		this.ui = ui;
		this.editor = editor;
	}

	private void calculateCSV() {
		int numOfItems = ui.getNumOfItems();
		createdPartitionsData = new ArrayList<>();
		createdPartitionsData.add("# Name,   Type, SubType,  Offset,   Size,  Flags");

		for (int i = 0; i < numOfItems; i++) {
			JCheckBox checkBox = ui.getCheckBox(i);
			JTextField partitionNameField = ui.getPartitionName(i);
			JComboBox<?> partitionTypeComboBox = ui.getPartitionType(i);
			JTextField partitionSubTypeField = ui.getPartitionSubType(i);
			JTextField partitionSizeField = ui.getPartitionSizeHex(i);
			JTextField partitionOffset = ui.getPartitionOffsets(i);

			if (checkBox.isSelected()) {
				String name = partitionNameField.getText();
				String type = (String) partitionTypeComboBox.getSelectedItem();
				String subType = partitionSubTypeField.getText();
				String size = partitionSizeField.getText();
				String offset = partitionOffset.getText(); // Assuming offset is same as size

				String exported_csvPartition = name + ", " + type + ", " + subType + ", " + "0x" + offset + ", " + "0x"
						+ size + ", ";
				createdPartitionsData.add(exported_csvPartition);
			}
		}
	}

	public void importCSV() {
		File defaultDirectory = editor.getSketch().getFolder();

		FileDialog dialog = new FileDialog((Frame) null, "Select CSV File", FileDialog.LOAD);
		dialog.setDirectory(defaultDirectory.getAbsolutePath());
		dialog.setFile("*.csv");
		dialog.setVisible(true);

		String directory = dialog.getDirectory();
		String file = dialog.getFile();

		if (file != null) {
			try (BufferedReader reader = new BufferedReader(new FileReader(new File(directory, file)))) {
				for (int i = 0; i < ui.getNumOfItems(); i++) {
					setUIComponents(i, false);
				}
				processCSV(reader);
			} catch (IOException e) {
				System.err.println("Error reading CSV file: " + e.getMessage());
			}
		} else {
			System.out.println("No file selected.");
		}
		ui.calculateSizeHex();
		ui.calculateOffsets();
		ui.updatePartitionFlashVisual();

		JTextField lastPartitionOffsetField = ui.getPartitionOffsets(ui.lastIndex + 1);

		if (lastPartitionOffsetField != null) {
			// Get the text from the JTextField and parse it as hexadecimal
			String hexOffset = lastPartitionOffsetField.getText();
			long lastOffset = Long.parseLong(hexOffset, 16);

			// Calculate offset in MB, rounding up to the nearest integer
			long offsetInMB = (lastOffset + 1024 * 1024 - 1) / (1024 * 1024);

			ui.flashSizeMB = (int) offsetInMB;
		}

		String flashSizeString = String.valueOf(ui.flashSizeMB);
		ui.getFlashSize().setSelectedItem(flashSizeString);
	}

	private void processCSV(BufferedReader reader) throws IOException {
		int rowIndex = 0;
		String line;
		while ((line = reader.readLine()) != null && rowIndex < ui.getNumOfItems()) {
			// Skip comment lines that start with #
			if (line.trim().startsWith("#")) {
				continue;
			}
			String[] columns = line.split(",\\s*");
			// Skip empty or incomplete lines (e.g. trailing CR/LF)
			if (columns.length < 5) {
				continue;
			}
			setUIComponents(rowIndex, true);
			updateUIComponents(columns, rowIndex);
			rowIndex++;
		}
	}

	private void setUIComponents(int index, boolean enabled) {
		ui.getCheckBox(index).setSelected(enabled);
		ui.getPartitionName(index).setEditable(enabled);
		ui.getPartitionType(index).setEnabled(enabled);
		ui.getPartitionSubType(index).setEditable(enabled);
		ui.getPartitionSize(index).setEditable(enabled);
		ui.getPartitionSize(index).setText("");
		ui.getPartitionName(index).setText("");
		ui.getPartitionSubType(index).setText("");
		ui.getPartitionOffsets(index).setText("");
	}

	private void updateUIComponents(String[] columns, int rowIndex) {
		JTextField partitionName = ui.getPartitionName(rowIndex);
		JComboBox<?> partitionType = ui.getPartitionType(rowIndex);
		JTextField partitionSubType = ui.getPartitionSubType(rowIndex);
		JTextField partitionSize = ui.getPartitionSize(rowIndex);
		JTextField partitionSizeHex = ui.getPartitionSizeHex(rowIndex);
		JTextField partitionOffset = ui.getPartitionOffsets(rowIndex);

		partitionName.setText(columns[0].trim());

		String partitionTypeStr = columns[1].trim().toLowerCase();

		if (!partitionTypeStr.equals("app") && !partitionTypeStr.equals("data")) {
			partitionTypeStr = stringToDec(partitionTypeStr) == 0 ? "app" : "data";
		}

		partitionType.setSelectedItem(partitionTypeStr);

		partitionSubType.setText(columns[2].trim());

		int bytesOffset = stringToDec(columns[3].trim());
		partitionOffset.setText(String.format("%x", bytesOffset));

		int byteSize = stringToDec(columns[4].trim());
		String kbSize = stringToKb(columns[4].trim());
		partitionSize.setText(kbSize);
		partitionSizeHex.setText(String.format("%x", byteSize));
	}

	private String formatKilobytes(double kilobytes) {
		// Check if the kilobytes value is a whole number
		if (kilobytes % 1 == 0) {
			return String.format("%.0f", kilobytes);
		} else {
			return String.format("%.2f", kilobytes);
		}
	}

	private int stringToDec(String value) {
		if (value.toLowerCase().startsWith("0x")) {
			return Integer.decode(value);
		} else {
			return Integer.parseInt(value);
		}
	}

	private String stringToKb(String value) {
		int decimalValue = stringToDec(value);
		return formatKilobytes(decimalValue / 1024.0);
	}

	public void generateCSV() {
		calculateCSV();

		// Get the default directory path
		File defaultDirectory = editor.getSketch().getFolder();

		// Export to CSV
		FileDialog dialog = new FileDialog(new Frame(), "Create Partitions CSV", FileDialog.SAVE);
		dialog.setDirectory(defaultDirectory.getAbsolutePath());
		dialog.setFile("partitions.csv");
		dialog.setVisible(true);
		String fileName = dialog.getFile();

		if (fileName != null) {
			String filePath = dialog.getDirectory() + fileName; // Construct the full file path
			try (FileWriter writer = new FileWriter(filePath)) {
				// Write the exported data to the CSV file
				for (String partitionData : createdPartitionsData) {
					writer.write(partitionData + "\n");
				}
				System.out.println("partitions.csv created at: " + filePath);
			} catch (IOException ex) {
				System.err.println("Error creating CSV: " + ex.getMessage());
			}
		}
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

	private String getBuildFolderPath(Sketch s) {
		// first of all try the getBuildPath() function introduced with IDE 1.6.12
		// see commit arduino/Arduino#fd1541eb47d589f9b9ea7e558018a8cf49bb6d03
		try {
			String buildpath = s.getBuildPath().getAbsolutePath();
			return buildpath;
		} catch (IOException er) {
			editor.statusError(er);
		} catch (Exception er) {
			try {
				File buildFolder = FileUtils.createTempFolder("build",
						DigestUtils.md5Hex(s.getMainFilePath()) + ".tmp");
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

	public void createPartitionsBin() {
		TargetPlatform platform = BaseNoGui.getTargetPlatform();

		String gen_esp32partCmd = "gen_esp32part.py";
		gen_esp32part = new File(platform.getFolder() + "/tools", gen_esp32partCmd);
		if (!gen_esp32part.exists() || !gen_esp32part.isFile()) {
			System.err.println();
			editor.statusError("Partitions Bin Generate Error: gen_esp32part not found!");
			return;
		}

		editor.statusNotice("Creating partitions.bin...");

		String buildPath = getBuildFolderPath(editor.getSketch());
		String sketchName = editor.getSketch().getName();
		String csvFilePath = buildPath + "/partitions.csv";

		// Assuming you have access to the UI components
		calculateCSV();

		try (FileWriter writer = new FileWriter(csvFilePath)) {
			// Write the exported data to the CSV file
			for (String partitionData : createdPartitionsData) {
				writer.write(partitionData + "\n");
			}
			System.out.println("partitions.csv successfully created at: " + csvFilePath);
		} catch (IOException ex) {
			System.err.println("Error exporting CSV: " + ex.getMessage());
		}

		String partitionsBinPath = buildPath + "/" + sketchName + ".ino" + ".partitions.bin";
		// Command to generate partitions.bin
		String[] command = { "python3", gen_esp32part.getAbsolutePath(), buildPath + "/partitions.csv",
				partitionsBinPath };

		try {
			// Execute the command
			int exitCode = listenOnProcess(command);
			if (exitCode == 0) {
				editor.statusNotice("partitions.bin created successfully.");
				System.out.println("partitions.bin created successfully.");
			} else {
				editor.statusError("Failed to create partitions.bin.");
			}
		} catch (Exception e) {
			editor.statusError("An error occurred while creating partitions.bin.");
			e.printStackTrace(); // Print the stack trace for debugging
			return;
		}

	}

	public void handleSPIFFS() {

		// Create a JOptionPane to prompt the user
		String fsName = ui.getPartitionFlashType().getSelectedItem().toString();

		int option = JOptionPane.showConfirmDialog(null,
				"Do you want to upload " + fsName + " after it has been created?", "Upload SPIFFS" + fsName,
				JOptionPane.YES_NO_OPTION);

		// Check user's choice
		if (option == JOptionPane.YES_OPTION) {
			createSPIFFS();
			uploadSPIFFS();

		} else {
			createSPIFFS();
		}
	}

	private void createSPIFFS() {

		spiStart = 0;
		spiSize = 0;
		spiPage = 256;
		spiBlock = ui.flashSizeMB * 1024;

		String fsName = ui.getPartitionFlashType().getSelectedItem().toString();
		boolean is_windows = PreferencesData.get("runtime.os").contentEquals("windows");

		if (!PreferencesData.get("target_platform").contentEquals("esp32")) {
			System.err.println();
			editor.statusError(fsName + " Not Supported on " + PreferencesData.get("target_platform"));
			return;
		}

		TargetPlatform platform = BaseNoGui.getTargetPlatform();

		String toolBinName = "mk" + fsName.toLowerCase();
		System.out.println("Selected filesystem: " + fsName);
		System.out.println("Selected tool: " + toolBinName);

		pythonCmd = is_windows ? "python3.exe" : "python3";
		String toolExtension = is_windows ? ".exe" : ".py";
		String mkFsCmd = toolBinName + (is_windows ? ".exe" : "");
		String espotaCmd = is_windows ? "espota.exe" : "espota.py";

		isNetwork = false;
		espota = new File(platform.getFolder() + "/tools");
		esptool = new File(platform.getFolder() + "/tools");
		serialPort = PreferencesData.get("serial.port");

		if (!BaseNoGui.getBoardPreferences().containsKey("build.partitions")) {
			System.err.println();
			editor.statusError("Partitions Not Defined for " + BaseNoGui.getBoardPreferences().get("name"));
			return;
		}

		String buildPath = getBuildFolderPath(editor.getSketch());
		String csvFilePath = buildPath + "/partitions.csv";

		calculateCSV();

		try (FileWriter writer = new FileWriter(csvFilePath)) {
			// Write the exported data to the CSV file
			for (String partitionData : createdPartitionsData) {
				writer.write(partitionData + "\n");
			}
			System.out.println("CSV exported successfully to: " + csvFilePath);
		} catch (IOException ex) {
			System.err.println("Error exporting CSV: " + ex.getMessage());
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
						spiStart = Integer.parseInt(pStart.substring(2), 16); // Convert hex to int
						spiSize = Integer.parseInt(pSize.substring(2), 16); // Convert hex to int
					}
				}
			}
			if (spiSize == 0) {
				System.err.println();
				editor.statusError(fsName + " Error: partition size could not be found!");
				return;
			}
		} catch (Exception e) {
			editor.statusError(e);
			return;
		}

		File tool = new File(platform.getFolder() + "/tools", mkFsCmd);
		if (!tool.exists() || !tool.isFile()) {
			tool = new File(platform.getFolder() + "/tools/" + toolBinName, mkFsCmd);
			if (!tool.exists()) {
				tool = new File(PreferencesData.get("runtime.tools." + toolBinName + ".path"), mkFsCmd);
				if (!tool.exists()) {
					System.err.println();
					editor.statusError(fsName + " Error: " + toolBinName + " not found!");
					return;
				}
			}
		}

		// make sure the serial port or IP is defined
		if (serialPort == null || serialPort.isEmpty()) {
			System.err.println();
			editor.statusError(fsName + " Error: serial port not defined!");
			return;
		}

		// find espota if IP else find esptool
		if (serialPort.split("\\.").length == 4) {
			isNetwork = true;
			espota = new File(platform.getFolder() + "/tools", espotaCmd);
			if (!espota.exists() || !espota.isFile()) {
				System.err.println();
				editor.statusError(fsName + " Error: espota not found!");
				return;
			}
		} else {
			String esptoolCmd = "esptool" + toolExtension;
			esptool = new File(platform.getFolder() + "/tools", esptoolCmd);
			if (!esptool.exists() || !esptool.isFile()) {
				esptool = new File(platform.getFolder() + "/tools/esptool_py", esptoolCmd);
				if (!esptool.exists()) {
					esptool = new File(PreferencesData.get("runtime.tools.esptool_py.path"), esptoolCmd);
					if (!esptool.exists()) {
						System.err.println();
						editor.statusError(fsName + " Error: esptool not found!");
						return;
					}
				}
			}
		}

		// load a list of all files
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
		String toolPath = tool.getAbsolutePath();
		String sketchName = editor.getSketch().getName();
		imagePath = getBuildFolderPath(editor.getSketch()) + "/" + sketchName + ".spiffs.bin";
		uploadSpeed = BaseNoGui.getBoardPreferences().get("upload.speed");

		Object[] options = { "Yes", "No" };
		String title = "Create " + fsName;
		String message = "No files have been found in your data folder!\nAre you sure you want to create an empty "
				+ fsName + " image?";

		if (fileCount == 0 && JOptionPane.showOptionDialog(editor, message, title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[1]) != JOptionPane.YES_OPTION) {
			System.err.println();
			editor.statusError(fsName + " Warning: " + toolBinName + " canceled!");
			return;
		}

		editor.statusNotice("Creating " + fsName + "...");
		System.out.println("[" + fsName + "] data   : " + dataPath);
		System.out.println("[" + fsName + "] start  : " + spiStart);
		System.out.println("[" + fsName + "] size   : " + (spiSize));
		System.out.println("[" + fsName + "] page   : " + spiPage);
		System.out.println("[" + fsName + "] block  : " + spiBlock);

		try {
			if (listenOnProcess(new String[] { toolPath, "-c", dataPath, "-p", spiPage + "", "-b", spiBlock + "", "-s",
					spiSize + "", imagePath }) != 0) {
				System.err.println();
				editor.statusError("Failed to create " + fsName + "!");
				return;
			}
		} catch (Exception e) {
			editor.statusError(e);
			editor.statusError("Failed to create " + fsName + "!");
			return;
		} finally {
			editor.statusNotice("Completed creating " + fsName);
			System.out.println(fsName + " successfully created");
			// Delete the partitions.csv file after reading its contents
			File csvFile = new File(csvFilePath);
			if (csvFile.exists()) {
				if (csvFile.delete()) {
				} else {
					System.err.println("Failed to delete partitions.csv file");
				}
			}
		}
	}

	private void uploadSPIFFS() {
		String fsName = ui.getPartitionFlashType().getSelectedItem().toString();
		editor.statusNotice("Uploading " + fsName + "...");
		System.out.println("[" + fsName + "] upload : " + imagePath);

		if (isNetwork) {
			System.out.println("[" + fsName + "] IP     : " + serialPort);
			System.out.println();
			if (espota.getAbsolutePath().endsWith(".py"))
				sysExec(new String[] { pythonCmd, espota.getAbsolutePath(), "-i", serialPort, "-p", "3232", "-s", "-f",
						imagePath });
			else
				sysExec(new String[] { espota.getAbsolutePath(), "-i", serialPort, "-p", "3232", "-s", "-f",
						imagePath });
		} else {
			System.out.println("[" + fsName + "] address: " + spiStart);
			System.out.println("[" + fsName + "] port   : " + serialPort);
			System.out.println("[" + fsName + "] speed  : " + uploadSpeed);
			System.out.println("[" + fsName + "] mode   : " + flashMode);
			System.out.println("[" + fsName + "] freq   : " + flashFreq);
			System.out.println();
			if (esptool.getAbsolutePath().endsWith(".py"))
				sysExec(new String[] { pythonCmd, esptool.getAbsolutePath(), "--chip", mcu, "--baud", uploadSpeed,
						"--port", serialPort, "--before", "default_reset", "--after", "hard_reset", "write_flash", "-z",
						"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", "" + spiStart,
						imagePath });
			else
				sysExec(new String[] { esptool.getAbsolutePath(), "--chip", mcu, "--baud", uploadSpeed, "--port",
						serialPort, "--before", "default_reset", "--after", "hard_reset", "write_flash", "-z",
						"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", "" + spiStart,
						imagePath });
		}
	}

	public void handleMergedBin() {

		// Create a JOptionPane to prompt the user
		int option = JOptionPane.showConfirmDialog(null,
				"Do you want to upload merged binary after it has been created?", "Upload merged binary",
				JOptionPane.YES_NO_OPTION);

		// Check user's choice
		if (option == JOptionPane.YES_OPTION) {
			createPartitionsBin();
			createSPIFFS();
			createMergedBin();
			try {
				Thread.sleep(2000);
				uploadMergedBin();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			createPartitionsBin();
			createSPIFFS();
			createMergedBin();
		}
	}

	private void createMergedBin() {

		String fsName = ui.getPartitionFlashType().getSelectedItem().toString();

		if (!PreferencesData.get("target_platform").contentEquals("esp32")) {
			System.err.println();
			editor.statusError("Tool Not Supported on " + PreferencesData.get("target_platform"));
			return;
		}

		TargetPlatform platform = BaseNoGui.getTargetPlatform();

		String toolExtension = ".py";
		if (PreferencesData.get("runtime.os").contentEquals("windows")) {
			toolExtension = ".exe";
		}

		if (PreferencesData.get("runtime.os").contentEquals("windows"))
			pythonCmd = "python3.exe";

		String espotaCmd = "espota.py";
		if (PreferencesData.get("runtime.os").contentEquals("windows"))
			espotaCmd = "espota.exe";

		isNetwork = false;
		espota = new File(platform.getFolder() + "/tools");
		esptool = new File(platform.getFolder() + "/tools");
		serialPort = PreferencesData.get("serial.port");

		if (!BaseNoGui.getBoardPreferences().containsKey("build.partitions")) {
			System.err.println();
			editor.statusError("Partitions Not Defined for " + BaseNoGui.getBoardPreferences().get("name"));
			return;
		}

		// make sure the serial port or IP is defined
		if (serialPort == null || serialPort.isEmpty()) {
			System.err.println();
			editor.statusError(fsName + " Error: serial port not defined!");
			return;
		}

		// find espota if IP else find esptool
		if (serialPort.split("\\.").length == 4) {
			isNetwork = true;
			espota = new File(platform.getFolder() + "/tools", espotaCmd);
			if (!espota.exists() || !espota.isFile()) {
				System.err.println();
				editor.statusError(fsName + " Error: espota not found!");
				return;
			}
		} else {
			String esptoolCmd = "esptool" + toolExtension;
			esptool = new File(platform.getFolder() + "/tools", esptoolCmd);
			if (!esptool.exists() || !esptool.isFile()) {
				esptool = new File(platform.getFolder() + "/tools/esptool_py", esptoolCmd);
				if (!esptool.exists()) {
					esptool = new File(PreferencesData.get("runtime.tools.esptool_py.path"), esptoolCmd);
					if (!esptool.exists()) {
						System.err.println();
						editor.statusError("Creating Merge Bin Error: esptool not found!");
						return;
					}
				}
			}
		}

		String sketchName = editor.getSketch().getName();

		String bootloaderImage = getBuildFolderPath(editor.getSketch()) + "/" + sketchName + ".ino" + ".bootloader.bin";
		// TODO: if bootloaderImage file does not exists, print error("You must compile
		// the sketch first");
		String partitionsImage = getBuildFolderPath(editor.getSketch()) + "/" + sketchName + ".ino" + ".partitions.bin";
		String bootImage = platform.getFolder() + "/tools/partitions/boot_app0.bin";
		String appImage = getBuildFolderPath(editor.getSketch()) + "/" + sketchName + ".ino" + ".bin";
		String spiffsImage = getBuildFolderPath(editor.getSketch()) + "/" + sketchName + ".spiffs.bin";

		String mergedImage = getBuildFolderPath(editor.getSketch()) + "/" + sketchName + "merged" + ".bin";

		// String bootloaderOffset = "0x1000";
		String bootloaderOffset = BaseNoGui.getBoardPreferences().get("build.bootloader_addr");
		String partitionsOffset = "0x8000";
		String bootOffset = "0xe000";
		String appOffset = "0x10000";
		String spiffsOffset = ui.getSpiffsOffset();

		String mcu = BaseNoGui.getBoardPreferences().get("build.mcu");
		String serialPort = PreferencesData.get("serial.port");
		String uploadSpeed = BaseNoGui.getBoardPreferences().get("upload.speed");
		String flashMode = BaseNoGui.getBoardPreferences().get("build.flash_mode");
		String flashFreq = BaseNoGui.getBoardPreferences().get("build.flash_freq");
		String flashSize = ui.flashSizeMB + "MB";

		editor.statusNotice("Creating merged binary...");
		System.out.println("[Merged bin] creation:");

		System.out.println("[Merged binary] mcu: " + mcu);
		System.out.println("[Merged binary] port   : " + serialPort);
		System.out.println("[Merged binary] speed  : " + uploadSpeed);
		System.out.println("[Merged binary] mode   : " + flashMode);
		System.out.println("[Merged bbinary freq   : " + flashFreq);

		String[] mergeCommand = { pythonCmd, esptool.getAbsolutePath(), "--chip", mcu, "merge_bin", "-o", mergedImage,
				"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", flashSize, bootloaderOffset,
				bootloaderImage, partitionsOffset, partitionsImage, bootOffset, bootImage, appOffset, appImage,
				spiffsOffset, spiffsImage };

		String[] mergeWindowsCommand = { esptool.getAbsolutePath(), "--chip", mcu, "merge_bin", "-o", mergedImage,
				"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", flashSize, bootloaderOffset,
				bootloaderImage, partitionsOffset, partitionsImage, bootOffset, bootImage, appOffset, appImage,
				spiffsOffset, spiffsImage };

		if (esptool.getAbsolutePath().endsWith(".py")) {
			sysExec(mergeCommand);
		} else {
			sysExec(mergeWindowsCommand);
		}
	}

	private void uploadMergedBin() {

		if (!PreferencesData.get("target_platform").contentEquals("esp32")) {
			System.err.println();
			editor.statusError("Tool Not Supported on " + PreferencesData.get("target_platform"));
			return;
		}

		String fsName = ui.getPartitionFlashType().getSelectedItem().toString();

		TargetPlatform platform = BaseNoGui.getTargetPlatform();

		String toolExtension = ".py";
		if (PreferencesData.get("runtime.os").contentEquals("windows")) {
			toolExtension = ".exe";
		}

		if (PreferencesData.get("runtime.os").contentEquals("windows"))
			pythonCmd = "python3.exe";

		String espotaCmd = "espota.py";
		if (PreferencesData.get("runtime.os").contentEquals("windows"))
			espotaCmd = "espota.exe";

		isNetwork = false;
		espota = new File(platform.getFolder() + "/tools");
		esptool = new File(platform.getFolder() + "/tools");
		serialPort = PreferencesData.get("serial.port");

		if (!BaseNoGui.getBoardPreferences().containsKey("build.partitions")) {
			System.err.println();
			editor.statusError("Partitions Not Defined for " + BaseNoGui.getBoardPreferences().get("name"));
			return;
		}

		// make sure the serial port or IP is defined
		if (serialPort == null || serialPort.isEmpty()) {
			System.err.println();
			editor.statusError(fsName + " Error: serial port not defined!");
			return;
		}

		// find espota if IP else find esptool
		if (serialPort.split("\\.").length == 4) {
			isNetwork = true;
			espota = new File(platform.getFolder() + "/tools", espotaCmd);
			if (!espota.exists() || !espota.isFile()) {
				System.err.println();
				editor.statusError("Merged Bin Upload Error: espota not found!");
				return;
			}
		} else {
			String esptoolCmd = "esptool" + toolExtension;
			esptool = new File(platform.getFolder() + "/tools", esptoolCmd);
			if (!esptool.exists() || !esptool.isFile()) {
				esptool = new File(platform.getFolder() + "/tools/esptool_py", esptoolCmd);
				if (!esptool.exists()) {
					esptool = new File(PreferencesData.get("runtime.tools.esptool_py.path"), esptoolCmd);
					if (!esptool.exists()) {
						System.err.println();
						editor.statusError("Flashing Merged Bin Error: esptool not found!");
						return;
					}
				}
			}
		}

		String sketchName = editor.getSketch().getName();
		String mergedImage = getBuildFolderPath(editor.getSketch()) + "/" + sketchName + "merged" + ".bin";

		String mergedOffset = "0x0";

		editor.statusNotice("Creating merged binary...");
		System.out.println("[Merged binary] creation:");

		if (isNetwork) {
			System.out.println("[Merged bin] IP: " + serialPort);
			System.out.println();

			String[] writeFlashCommand = { pythonCmd, espota.getAbsolutePath(), "-i", serialPort, "-p", "3232", "-s",
					"-f", mergedImage };
			String[] writeWindowsFlashCommand = { espota.getAbsolutePath(), "-i", serialPort, "-p", "3232", "-s", "-f",
					mergedImage };

			if (espota.getAbsolutePath().endsWith(".py")) {
				sysExec(writeFlashCommand);
			} else {
				sysExec(writeWindowsFlashCommand);
			}
		} else {
			System.out.println("[Merged binary] mcu: " + mcu);
			System.out.println("[Merged binary] port   : " + serialPort);
			System.out.println("[Merged binary] speed  : " + uploadSpeed);
			System.out.println("[Merged binary] mode   : " + flashMode);
			System.out.println("[Merged binary] freq(exec)   : " + flashFreq);

			String[] writeFlashCommand = { pythonCmd, esptool.getAbsolutePath(), "--chip", mcu, "--baud", uploadSpeed,
					"--port", serialPort, "--before", "default_reset", "--after", "hard_reset", "write_flash", "-z",
					"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", mergedOffset,
					mergedImage };
			String[] writeWindowsFlashCommand = { esptool.getAbsolutePath(), "--chip", mcu, "--baud", uploadSpeed,
					"--port", serialPort, "--before", "default_reset", "--after", "hard_reset", "write_flash", "-z",
					"--flash_mode", flashMode, "--flash_freq", flashFreq, "--flash_size", "detect", mergedOffset,
					mergedImage };

			if (esptool.getAbsolutePath().endsWith(".py")) {
				sysExec(writeFlashCommand);
			} else {
				sysExec(writeWindowsFlashCommand);
			}
		}
	}
}
