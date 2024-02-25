package com.serifpersia.esp32partitiontool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import java.awt.Component;

public class UIController implements ActionListener {
	private UI ui;
	private FileManager fileManager;

	public UIController(UI ui, FileManager fileManager) {
		this.ui = ui;
		this.fileManager = fileManager;
		attachListeners();
	}

	private void attachListeners() {
		// Attach listener to the "Generate CSV" button
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
		ui.getSpiffsPageSize().addActionListener(this);
		ui.getFlashSize().addActionListener(this);
		ui.getFlashSPIFFSButton().addActionListener(this);
		ui.getCreateSPIFFSButton().addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ui.getCreatePartitionsCSV()) {
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
		} else if (e.getSource() == ui.getCreateSPIFFSButton()) {
			fileManager.createSPIFFS();
		} else if (e.getSource() == ui.getFlashSPIFFSButton()) {
			fileManager.uploadSPIFFS();
		}
	}

	private void handleCheckBoxAction(JCheckBox checkBox) {
		int toggleID = getIndexForComponent(checkBox);
		boolean isSelected = checkBox.isSelected();
		ui.getPartitionName(toggleID).setEditable(isSelected);
		ui.getPartitionType(toggleID).setEnabled(isSelected);
		ui.getPartitionSubType(toggleID).setEditable(isSelected);
		ui.getPartitionSize(toggleID).setEditable(isSelected);
		ui.getPartitionOffsets(toggleID).setEditable(isSelected);
		ui.updatePartitionFlashVisual();
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

}