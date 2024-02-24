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
		ui.getSpiffsBlockSize().addActionListener(this);
		ui.getFlashSPIFFSButton().addActionListener(this);
		ui.getGenerateSPIFFSButton().addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ui.getGenCSVButton()) {
			// Handle CSV export
			fileManager.exportCSV();
		} else if (e.getSource() instanceof JCheckBox) {
			handleCheckBoxAction((JCheckBox) e.getSource());
		} else if (e.getSource() instanceof JTextField) {
			handleTextFieldAction((JTextField) e.getSource());
		} else if (e.getSource() instanceof JComboBox<?>) {
			handleComboBoxAction((JComboBox<?>) e.getSource());
		} else if (e.getSource() == ui.getGenerateSPIFFSButton()) {
			fileManager.createSPIFFS();
		} else if (e.getSource() == ui.getFlashSPIFFSButton()) {
			fileManager.uploadSPIFFS();
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
		} else if (comboBox == ui.getSpiffsBlockSize()) {
			fileManager.test();
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

}