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
	public AppSettings settings;

	private ArrayList<String> createdPartitionsData;

	// Constructor to initialize FileManager with UI instance and Editor instance
	public FileManager(UI ui, AppSettings settings) {
		this.ui = ui;
		this.settings = settings;
	}

	public void setUIController(UIController controller) {
		ui.setController(controller);
	}

	public void emitError(String msg) {
		System.err.println();
	}

	public void loadDefaultCSV() {
		if (settings.csvFilePath != null) {
			importCSV(settings.csvFilePath);
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

			settings.csvFilePath = basename(file);
			ui.updatePartitionLabel(settings.csvFilePath);

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

	public boolean generateCSV() {
		calculateCSV();

		// Get the default directory path

		// Export to CSV
		FileDialog dialog = new FileDialog(new Frame(), "Create Partitions CSV", FileDialog.SAVE);

		if (settings.csvFilePath != null) {
			dialog.setFile(settings.csvFilePath);
			// dialog.setDirectory(homeDir);
		} else {
			dialog.setFile("partitions.csv");
		}
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

			settings.csvFilePath = filePath;

			return true;
		} catch (IOException ex) {
			emitError("Error creating CSV: " + ex.getMessage());
		}
		return false;
	}
}
