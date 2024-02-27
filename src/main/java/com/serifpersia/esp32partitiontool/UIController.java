package com.serifpersia.esp32partitiontool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import java.awt.Component;

public class UIController implements ActionListener {
	private static UIController instance;
	private UI ui;
	private FileManager fileManager;

	public UIController(UI ui, FileManager fileManager) {
		this.ui = ui;
		this.fileManager = fileManager;
		attachListeners();
	}

	// Public method to get the singleton instance
	public static UIController getInstance(UI ui, FileManager fileManager) {
		if (instance == null) {
			instance = new UIController(ui, fileManager);
		}
		return instance;
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

	private void attachListeners() {
		// Attach listener to the "Generate CSV" button
		ui.getImportCSVButton().addActionListener(this);
		ui.getCreatePartitionsCSV().addActionListener(this);
		int numOfItems = ui.getNumOfItems();
		for (int i = 0; i < numOfItems; i++) {
			JCheckBox checkBox = ui.getCheckBox(i);
			checkBox.addActionListener(this);
			ui.getPartitionName(i).addActionListener(this);
			ui.getPartitionType(i).addActionListener(this);
			ui.getPartitionSubType(i).addActionListener(this);
			ui.getPartitionSize(i).addActionListener(this);
		}
		ui.getCreatePartitionsBin().addActionListener(this);
		ui.getFlashSize().addActionListener(this);
		ui.getFlashSPIFFSButton().addActionListener(this);
		ui.getFlashSketchButton().addActionListener(this);
		ui.getFlashMergedBin().addActionListener(this);
		ui.getHelpButton().addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ui.getImportCSVButton()) {
			// Handle CSV export
			fileManager.importCSV();
		} else if (e.getSource() == ui.getCreatePartitionsCSV()) {
			// Handle CSV export
			fileManager.generateCSV();
		} else if (e.getSource() instanceof JCheckBox) {
			handleCheckBoxAction((JCheckBox) e.getSource());
		} else if (e.getSource() instanceof JTextField) {
			handleTextFieldAction((JTextField) e.getSource());
		} else if (e.getSource() instanceof JComboBox<?>) {
			handleComboBoxAction((JComboBox<?>) e.getSource());
		} else if (e.getSource() == ui.getCreatePartitionsBin()) {
			fileManager.createPartitionsBin();
		} else if (e.getSource() == ui.getFlashSPIFFSButton()) {
			fileManager.handleSPIFFS();
		} else if (e.getSource() == ui.getFlashSketchButton()) {
			fileManager.flashCompiledSketch();
		} else if (e.getSource() == ui.getFlashMergedBin()) {
			fileManager.handleMergedBin();
		} else if (e.getSource() == ui.getHelpButton()) {
			handleHelpButton();
		}

	}

	private void handleCheckBoxAction(JCheckBox checkBox) {
		int toggleID = getIndexForComponent(checkBox);
		boolean isSelected = checkBox.isSelected();
		ui.getPartitionName(toggleID).setEditable(isSelected);
		ui.getPartitionType(toggleID).setEnabled(isSelected);
		ui.getPartitionSubType(toggleID).setEditable(isSelected);
		ui.getPartitionSize(toggleID).setEditable(isSelected);

		if (isSelected) {
			// Set text if isSelected is true
			String[] defaultPartitionNameText = { "nvs", "otadata", "app0", "app1", "spiffs", "coredump" };
			int[] defaultPartitionTypeText = { 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 };
			String[] defaultPartitionSubTypeText = { "nvs", "ota", "ota_0", "ota_1", "spiffs", "coredump" };
			String[] defaultPartitionSizeText = { "20", "8", "1280", "1280", "1408", "64" };

			if (toggleID < defaultPartitionNameText.length) {
				ui.getPartitionName(toggleID).setText(defaultPartitionNameText[toggleID]);
				if (defaultPartitionTypeText[toggleID] < ui.getPartitionType(toggleID).getItemCount()) {
					ui.getPartitionType(toggleID).setSelectedIndex(defaultPartitionTypeText[toggleID]);
				}
				ui.getPartitionSubType(toggleID).setText(defaultPartitionSubTypeText[toggleID]);
				ui.getPartitionSize(toggleID).setText(defaultPartitionSizeText[toggleID]);
				ui.calculateSizeHex();
				ui.calculateOffsets();
			}
		} else {
			// Make that index partitionSize an empty string if isSelected is false
			ui.getPartitionSize(toggleID).setText("");
			ui.getPartitionName(toggleID).setText("");
			ui.getPartitionSubType(toggleID).setText("");
			ui.calculateSizeHex();
			ui.calculateOffsets();
		}
		ui.updatePartitionFlashVisual();
	}

	private void handleTextFieldAction(JTextField textField) {
		int id = getIndexForComponent(textField);

		JTextField partitionSizeField = ui.getPartitionSize(id);
		if (partitionSizeField == textField) {
			ui.calculateSizeHex();
			ui.calculateOffsets();
			ui.updatePartitionFlashVisual();
		} else {
			ui.updatePartitionFlashVisual();
		}
	}

	private void handleComboBoxAction(JComboBox<?> comboBox) {
		if (comboBox == ui.getFlashSize()) {
			// Get the selected item from the combo box
			String selectedItem = comboBox.getSelectedItem().toString();

			// Convert the selected item to an integer
			ui.flashSizeMB = Integer.parseInt(selectedItem);

			ui.calculateSizeHex();
			ui.updatePartitionFlashVisual();

			int spiffs_setBlockSize = 0;

			if (ui.flashSizeMB == 4 || ui.flashSizeMB == 8 || ui.flashSizeMB == 16) {
				spiffs_setBlockSize = ui.flashSizeMB * 1024;
			} else {
				// Handle other cases or provide a default value if necessary
			}

			String blockSizeText = String.valueOf(spiffs_setBlockSize);
			ui.getSpiffsBlockSize().setText(blockSizeText);

		}
	}

	private void handleHelpButton() {
		int currentStep = 0;
		String[] messages = {
				"<html>Partitions like nvs or any other small partitions before the app partition<br>needs to be a multiple of 4.</html>",
				"<html>Partitions before the first app partition should have a total of 28 kB so the offset<br>for the first app partition will always be correct at 0x10000 offset.<br>Any other configuration will cause the ESP32 board to not function properly.</html>",
				"<html>The app partition needs to be at 0x10000, and following partitions have to be<br>a multiple of 64.</html>",
				"<html>The app partition needs to be a minimum of 1024 kB in size.</html>" };
		while (currentStep < messages.length) {
			String message = messages[currentStep];
			int option = JOptionPane.showConfirmDialog(null, message, "Tip " + (currentStep + 1),
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (option == JOptionPane.OK_OPTION) {
				currentStep++;
			} else {
				break; // Exit the loop if the user cancels
			}
		}
	}

}