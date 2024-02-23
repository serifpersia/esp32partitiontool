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
import java.util.ArrayList;
import java.util.List;

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
			exportCSV();
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
		ui.updatePartitionFlashVisual();
		System.out.println("Toggle " + toggleID + ": State " + (isSelected ? "Enabled" : "Disabled"));
	}

	private void handleTextFieldAction(JTextField textField) {
		int textFieldID = getIndexForComponent(textField);
		String newText = textField.getText();
		System.out.println("Text: " + newText + " PartitionNameID " + textFieldID);

		ui.calculateSizeHex();
		ui.calculateOffsets();
		ui.updatePartitionFlashVisual();
	}

	private void handleComboBoxAction(JComboBox<?> comboBox) {
		if (comboBox == ui.getFlashSize()) {
			// Get the selected item from the combo box
			String selectedItem = comboBox.getSelectedItem().toString();

			// Convert the selected item to an integer
			ui.flashSizeMB = Integer.parseInt(selectedItem);

			ui.calculateSizeHex();
			ui.updatePartitionFlashVisual();
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

	private void exportCSV() {
		int numOfItems = ui.getNumOfItems();
		List<String> exportedData = new ArrayList<>();
		exportedData.add("# Name,   Type, SubType,  Offset,   Size,  Flags");

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
				String offset = "0x" + partitionOffset.getText(); // Assuming offset is same as size

				String exported_csvPartition = name + ", " + type + ", " + subType + ", " + offset + ", " + "0x" + size;
				exportedData.add(exported_csvPartition);
			}
		}

		// Export to CSV
		FileDialog dialog = new FileDialog(new Frame(), "Export Partitions CSV", FileDialog.SAVE);
		dialog.setFile("partitions.csv");
		dialog.setVisible(true);
		String fileName = dialog.getFile();
		if (fileName != null) {
			String filePath = dialog.getDirectory() + fileName;
			try (FileWriter writer = new FileWriter(filePath)) {
				// Write the exported data to the CSV file
				for (String partitionData : exportedData) {
					writer.write(partitionData + "\n");
				}
				System.out.println("CSV exported successfully to: " + filePath);
			} catch (IOException ex) {
				System.err.println("Error exporting CSV: " + ex.getMessage());
			}
		}
	}
}