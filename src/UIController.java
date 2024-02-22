package com.serifpersia.esp32partitiontool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.FileWriter;

import java.io.IOException;

public class UIController implements ActionListener {
	private UI ui;

	public UIController(UI ui) {
		this.ui = ui;
		attachListeners();
	}

	private void attachListeners() {
		// Attach listener to the "Generate CSV" button
		ui.getGenCSVButton().addActionListener(this);
		int numOfItems = ui.getNumOfItems();
		for (int i = 0; i < numOfItems; i++) {
			JCheckBox checkBox = ui.getCheckBox(i);
			checkBox.addActionListener(this);
			ui.getPartitionName(i).addActionListener(this);
			ui.getPartitionType(i).addActionListener(this);
			ui.getPartitionSubType(i).addActionListener(this);
			ui.getPartitionSize(i).addActionListener(this);
		}
		ui.getFlashSize().addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ui.getGenCSVButton()) {
			// Handle CSV export
			exportToCSV();
		} else if (e.getSource() instanceof JCheckBox) {
			handleCheckBoxAction((JCheckBox) e.getSource());
		} else if (e.getSource() instanceof JTextField) {
			handleTextFieldAction((JTextField) e.getSource());
		} else if (e.getSource() instanceof JComboBox<?>) {
			handleComboBoxAction((JComboBox<?>) e.getSource());
		} else if (e.getSource() instanceof JComboBox<?>) {
			handleComboBoxAction((JComboBox<?>) e.getSource());
		}
	}

	private void handleCheckBoxAction(JCheckBox checkBox) {
		int toggleID = getIndexForComponent(checkBox);
		boolean isSelected = checkBox.isSelected();
		ui.getPartitionName(toggleID).setEnabled(isSelected);
		ui.getPartitionType(toggleID).setEnabled(isSelected);
		ui.getPartitionSubType(toggleID).setEnabled(isSelected);
		ui.getPartitionSize(toggleID).setEnabled(isSelected);
		ui.getPartitionOffsets(toggleID).setEnabled(isSelected);
		System.out.println("Toggle " + toggleID + ": State " + (isSelected ? "Enabled" : "Disabled"));
	}

	private void handleTextFieldAction(JTextField textField) {
		int textFieldID = getIndexForComponent(textField);
		String newText = textField.getText();
		System.out.println("Text: " + newText + " PartitionNameID " + textFieldID);

		ui.calculateSizeHex();
		ui.calculateOffsets();
	}

	private void handleComboBoxAction(JComboBox<?> comboBox) {
		if (comboBox == ui.getFlashSize()) {
			// Get the selected item from the combo box
			String selectedItem = comboBox.getSelectedItem().toString();

			// Convert the selected item to an integer
			ui.flashSizeMB = Integer.parseInt(selectedItem);

			ui.FlashSizeBytes = ui.flashSizeMB * 1024 * 1024 - 36864;

			// Iterate through all text fields to calculate the total size
			for (int i = 0; i < ui.getNumOfItems(); i++) {
				if (!ui.getPartitionSize(i).getText().isEmpty()) {
					try {
						int partitionTotalSize = Integer.parseInt(ui.getPartitionSize(i).getText()) * 1024;

						// Round up to the nearest multiple of 4096
						int partitionRoundedSize = (partitionTotalSize + 4095) / 4096 * 4096;

						ui.FlashSizeBytes -= partitionRoundedSize;

						// Set the rounded value back to the text field
						ui.getPartitionSize(i).setText(Integer.toString(partitionRoundedSize / 1024)); // Convert
																										// back to
																										// kilobytes
					} catch (NumberFormatException e) {
						// Handle parsing errors if necessary
						System.out.println("Invalid input in text field " + i);
					}
				}
			}
			// Set the text of the label
			String labelText = "Free Space: " + ui.FlashSizeBytes + " bytes";
			ui.getFlashFreeLabel().setText(labelText);

			System.out.println(labelText);
		}
		System.out.println("Item Data: " + comboBox.getSelectedItem());
	}

	private int getIndexForComponent(Component component) {
		int numOfItems = ui.getNumOfItems();
		for (int i = 0; i < numOfItems; i++) {
			if (component == ui.getCheckBox(i) || component == ui.getPartitionName(i)
					|| component == ui.getPartitionType(i) || component == ui.getPartitionSubType(i)
					|| component == ui.getPartitionSize(i)) {
				return i;
			}
		}
		return -1;
	}

	private void exportToCSV() {
		FileDialog dialog = new FileDialog(new Frame(), "Export Partitions CSV", FileDialog.SAVE);
		// Set default filename to partitions.csv
		dialog.setFile("partitions.csv");
		dialog.setVisible(true);
		String fileName = dialog.getFile();
		if (fileName != null) {
			String filePath = dialog.getDirectory() + fileName;
			try (FileWriter writer = new FileWriter(filePath)) {
				// Write the exact text structure as requested, with spaces after commas
				String[] lines = new String[] { "# Name,   Type, SubType,  Offset,   Size,  Flags",
						"nvs, data, nvs, 0x9000, 0x5000", "otadata, data, ota, 0xE000, 0x2000",
						"app0, app, ota_0, 0x10000, 0x190000", "app1, app, ota_1, 0x1A0000, 0x190000",
						"spiffs, data, spiffs, 0x330000, 0x80000", "coredump, data, coredump, 0x3B0000, 0x10000" };
				for (String line : lines) {
					writer.write(line + "\n");
				}
				System.out.println("CSV exported successfully to: " + filePath);
			} catch (IOException ex) {
				System.err.println("Error exporting CSV: " + ex.getMessage());
			}
		}
	}
}