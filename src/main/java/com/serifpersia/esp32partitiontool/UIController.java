package com.serifpersia.esp32partitiontool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.Frame;
import javax.swing.*;

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

	private int getRowIndexForComponent(Component component) {
		for (int i = 0; i < ui.getCSVRows().size(); i++) {
			if (ui.getCSVRow(i) == component.getParent().getParent()) {
				return i;
			}
		}
		return -1;
	}

	private void attachListeners() {
		// Attach listener to the "Generate CSV" button
		ui.getDebug().addActionListener(this);
		ui.getImportCSVButton().addActionListener(this);
		ui.getCreatePartitionsCSV().addActionListener(this);
		ui.getCreatePartitionsBin().addActionListener(this);
		ui.getFlashSize().addActionListener(this);
		ui.getFlashSPIFFSButton().addActionListener(this);
		ui.getPartitionFlashType().addActionListener(this);
		ui.getFlashMergedBin().addActionListener(this);
		ui.getHelpButton().addActionListener(this);
		ui.getAboutButton().addActionListener(this);
		ui.getOverwriteCheckBox().addActionListener(this);
		ui.getConfirmDataEmptyCheckBox().addActionListener(this);
		ui.getSettingsButton().addActionListener(this);
		// ui.getCancelButton().addActionListener(this);
		// ui.getSaveButton().addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ui.getImportCSVButton()) {
			// Handle CSV export
			fileManager.importCSV(null);
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
			fileManager.createPartitionsBin(null);
		} else if (e.getSource() == ui.getFlashSPIFFSButton()) {
			fileManager.handleSPIFFSButton(null);
		} else if (e.getSource() == ui.getFlashMergedBin()) {
			fileManager.handleMergedBinButton(null);
		} else if (e.getSource() == ui.getHelpButton()) {
			handleHelpButton();
		} else if (e.getSource() == ui.getAboutButton()) {
			handleAboutButton();
		} else if (e.getSource() == ui.getSettingsButton()) {
			handleSettingsButton();
		}

	}

	private void handleCheckBoxAction(JCheckBox checkBox) {
		boolean isSelected = checkBox.isSelected();

		if (checkBox == ui.getDebug()) {
			fileManager.setDebug(isSelected);
			return;
		}

		if (checkBox == ui.getOverwriteCheckBox()) {
			fileManager.setConfirmOverwrite(!isSelected);
			return;
		}

		if (checkBox == ui.getConfirmDataEmptyCheckBox()) {
			fileManager.setConfirmDataEmpty(isSelected);
			return;
		}

		int csvRowId = getRowIndexForComponent(checkBox);
		if (csvRowId < 0)
			return;

		CSVRow csvRow = ui.getCSVRow(csvRowId);

		csvRow.name.setEditable(isSelected);
		csvRow.type.setEnabled(isSelected);
		csvRow.subtype.setEditable(isSelected);
		csvRow.size.setEditable(isSelected);
		csvRow.sizeHex.setEditable(isSelected);
		csvRow.offset.setEditable(isSelected);

		if (isSelected) {
			if (csvRowId < 6)
				csvRow.setDefaults(csvRowId);
			else
				csvRow.enableRow();
			if (csvRowId == ui.getCSVRows().size() - 1) { // we're enabling the last checkbox, add one!
				if (csvRowId > 0) {
					ui.renderCSVRows(); // an extra empty line will be inserted by renderCSVRows()
				}
			}
		} else {
			csvRow.disableRow();
			if (csvRowId >= UI.MIN_ITEMS - 1 && csvRowId == ui.getCSVRows().size() - 2) { // we're disabling the last
																							// enabled checkbox
				ui.popCSVRow();
				ui.popCSVRow();
				ui.renderCSVRows();
			}
		}

		ui.calculateSizeHex();
		ui.calculateOffsets();
		ui.updatePartitionFlashVisual();
	}

	private void handleTextFieldAction(JTextField textField) {

		int id = getRowIndexForComponent(textField);

		JTextField partitionSizeField = ui.getPartitionSize(id);
		if (partitionSizeField == textField) {
			ui.calculateSizeHex();
			ui.calculateOffsets();
			ui.updatePartitionFlashVisual();
		} else {
			ui.updatePartitionFlashVisual();
		}
	}

	private String lastFsName = "";

	private void handleComboBoxAction(JComboBox<?> comboBox) {
		if (comboBox == ui.getFlashSize()) {
			// Get the selected item from the combo box
			String selectedItem = comboBox.getSelectedItem().toString();

			// Convert the selected item to an integer
			ui.flashSizeMB = Integer.parseInt(selectedItem);

			ui.calculateSizeHex();
			ui.calculateOffsets();
			ui.updatePartitionFlashVisual();

			int spiffs_setBlockSize = 0;

			if (ui.flashSizeMB == 4 || ui.flashSizeMB == 8 || ui.flashSizeMB == 16 || ui.flashSizeMB == 32) {
				spiffs_setBlockSize = ui.flashSizeMB * 1024;
			} else {
				// Handle other cases or provide a default value if necessary
			}

			String blockSizeText = String.valueOf(spiffs_setBlockSize);
			ui.getSpiffsBlockSize().setText(blockSizeText);

		} else if (comboBox == ui.getPartitionFlashType()) {
			String fsName = ui.getPartitionFlashType().getSelectedItem().toString();
			String toolPath = fileManager.prefs.getProperty("mk" + fsName.toLowerCase() + ".path");
			if (toolPath == null) {
				fileManager.emitError("Tool for creating " + fsName + " spiifs.bin" + " not found!");
			} else {
				if (!fsName.equals(lastFsName)) {
					// no need to spam the console with repeated messages
					lastFsName = fsName;
					ui.updatePartitionFlashTypeLabel();
				}
			}
		}
	}

	private void handleAboutButton() {
		// modal dialog with icon
		ImageIcon icon = new ImageIcon(UIController.class.getResource("/resources/logo.png"));
		JOptionPane.showConfirmDialog(null, ui.aboutPanel, "About ESP32PartitionTool", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, icon);
	}

	public void handleSettingsButton() {
		// modal dialog without icon
		JOptionPane.showOptionDialog(ui, ui.prefsPanel, "Settings", -1, JOptionPane.PLAIN_MESSAGE, null, null, null);
	}

	public void handleHelpButton() {
		// modal dialog with minimal decoration
		final JDialog dialog = new JDialog((Frame) null, "Help Tips", true);
		dialog.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		dialog.add(ui.helpPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

}
