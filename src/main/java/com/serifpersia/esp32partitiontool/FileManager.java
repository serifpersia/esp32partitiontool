package com.serifpersia.esp32partitiontool;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JTextField;

import java.io.*;

public class FileManager {

	private UI ui; // Reference to the UI instance

	private ArrayList<String> createdPartitionsData;

	private String os = System.getProperty("os.name").toLowerCase();
	private String pythonLocation;
	private String gen_esp32partLocation;

	// Constructor to initialize FileManager with UI instance and Editor instance
	public FileManager(UI ui) {
		this.ui = ui;
	}

	public void setUIController(UIController controller) {
		ui.setController(controller);
	}

	public void emitError(String msg) {
		System.err.println();
	}

	public void loadDefaultCSV() {
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
		ui.updatePartitionFlashTypeLabel();
	}

	public void importCSV(String file) {
		File readerFile = null;

		if (file == null) {
			FileDialog dialog = new FileDialog((Frame) null, "Select CSV File", FileDialog.LOAD);
			dialog.setFile("*.csv");
			dialog.setVisible(true);
			String directory = dialog.getDirectory();
			if (directory == null || directory.isEmpty())
				return;
			file = dialog.getFile();
			if (file == null || file.isEmpty())
				return;
			readerFile = new File(directory, file);
		} else {
			readerFile = new File(file);
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(readerFile))) {
			processCSV(reader);

			String csvBasename = basename(file);
			ui.updatePartitionLabel(csvBasename);

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
				// if (debug_ui)
				// System.out.println(exported_csvPartition);
				createdPartitionsData.add(exported_csvPartition);
			}
		}
	}

	public boolean generateCSV() {
		calculateCSV();

		// Get the default directory path

		// Export to CSV
		FileDialog dialog = new FileDialog(new Frame(), "Create Partitions CSV", FileDialog.SAVE);
		dialog.setFile("partitions.csv");
		dialog.setVisible(true);
		String fileName = dialog.getFile();

		if (fileName == null)
			return false;

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

	public void createPartitionsBin() {
		// Create a file dialog for selecting the CSV file
		FileDialog fileDialog = new FileDialog((Frame) null, "Select CSV File", FileDialog.LOAD);
		fileDialog.setFile("*.csv");
		fileDialog.setVisible(true);

		// Get the selected file path
		String selectedFilePath = fileDialog.getFile();
		if (selectedFilePath != null) {
			// Get the directory of the selected file
			String selectedDirectory = fileDialog.getDirectory();

			// Now, let the user select the output directory for the partitions.bin file
			FileDialog outputFileDialog = new FileDialog((Frame) null, "Select Output Directory", FileDialog.SAVE);
			outputFileDialog.setFile("partitions.bin");
			outputFileDialog.setVisible(true);

			// Get the selected output directory
			String selectedOutputDirectory = outputFileDialog.getDirectory();
			String outputFile = null;

			// Check if the user selected an output directory
			if (selectedOutputDirectory != null) {
				outputFile = selectedOutputDirectory + File.separator + "partitions.bin";

				// Execute the Python script with the selected input file and output directory
				setOsCommands();

				// Construct the command arguments
				String[] arguments = { pythonLocation, gen_esp32partLocation, selectedDirectory + selectedFilePath,
						outputFile };

				// Execute the command using executeCommand method
				executeCommand(arguments);
			}
		}
	}

	private void setOsCommands() {
		if (os.contains("win")) {
			pythonLocation = System.getenv("HOMEPATH") + "\\.platformio\\python3\\python.exe";
			gen_esp32partLocation = System.getenv("HOMEPATH")
					+ "\\.platformio\\packages\\framework-arduinoespressif32\\tools\\gen_esp32part.py";
		} else {
			pythonLocation = "~/.platformio/python3/python";
			gen_esp32partLocation = "~/.platformio/packages/arduinoespressif32/tools/gen_esp32part.py";
		}
	}

	private void executeCommand(String[] commandArguments) {

		try {
			// Execute the command
			int exitCode = listenOnProcess(commandArguments);

			// Check the exit status of the process
			if (exitCode != 0) {
				System.err.println("Error: Command exited with error code " + exitCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int listenOnProcess(String[] arguments) {
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
						}
						reader.close();

						// Read the process error stream
						reader = new InputStreamReader(p.getErrorStream());
						while ((c = reader.read()) != -1) {
							outputBuilder.append((char) c);
						}
						reader.close();

						// Set the text of ui.console_logField with the output
						ui.console_logField.setText(outputBuilder.toString());
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