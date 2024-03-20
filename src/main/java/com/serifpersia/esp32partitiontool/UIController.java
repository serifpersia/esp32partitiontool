package com.serifpersia.esp32partitiontool;

import java.awt.event.*;
import java.awt.Component;
import java.awt.Frame;
import javax.swing.*;

public class UIController implements ActionListener {
	private static UIController instance;
	private UI ui;
	public FileManager fileManager;

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
		  CSVRow csvRow = ui.getCSVRow(i);
			if (csvRow == component || csvRow == component.getParent()) {
				return i;
			}
		}
		return -1;
	}

	private void attachListeners() {
		// Attach listener to the "Generate CSV" button
		ui.getImportCSVButton().addActionListener(this);
		ui.getExporCsvBtn().addActionListener(this);
		ui.getFlashSize().addActionListener(this);
		ui.getHelpButton().addActionListener(this);
		ui.getAboutButton().addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == ui.getImportCSVButton()) {
			// Handle CSV export
			fileManager.importCSV(null);
		} else if (e.getSource() == ui.getExporCsvBtn()) {
			// Handle CSV export
			fileManager.exportCSV();
		} else if (e.getSource() instanceof JCheckBox) {
			handleCheckBoxAction((JCheckBox) e.getSource());
		} else if (e.getSource() instanceof JTextField) {
			handleTextFieldAction((JTextField) e.getSource());
		} else if (e.getSource() instanceof JComboBox<?>) {
			handleComboBoxAction((JComboBox<?>) e.getSource());
		} else if (e.getSource() == ui.getHelpButton()) {
			handleHelpButton();
		} else if (e.getSource() == ui.getAboutButton()) {
			handleAboutButton();
		}

		if( ui.settings.changed()	) {
			ui.setFrameTitleNeedsSaving(true);
		}

	}

	private void handleCheckBoxAction(JCheckBox checkBox) {
		boolean isSelected = checkBox.isSelected();

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
			if (csvRowId == ui.getCSVRows().size() - 1) {
				if (csvRowId > 0) {
					ui.renderCSVRows();
				}
			}
		} else {
			csvRow.disableRow();
			if (csvRowId >= UI.MIN_ITEMS - 1 && csvRowId == ui.getCSVRows().size() - 2) {
				ui.popCSVRow();
				ui.popCSVRow();
				ui.renderCSVRows();
			}
		}

		ui.calculateSizeHex();
		ui.calculateOffsets();
		ui.updatePartitionFlashVisual();
		ui.settings.setChanged();
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
		ui.settings.setChanged();
	}

	private void handleComboBoxAction(JComboBox<?> comboBox) {
		if (comboBox == ui.getFlashSize()) {
			// Get the selected item from the combo box
			String selectedItem = comboBox.getSelectedItem().toString();

			// Convert the selected item to an integer
			ui.flashSizeMB = Integer.parseInt(selectedItem);

			ui.calculateSizeHex();
			ui.calculateOffsets();
			ui.updatePartitionFlashVisual();

			if (ui.flashSizeMB == 4 || ui.flashSizeMB == 8 || ui.flashSizeMB == 16 || ui.flashSizeMB == 32) {
			}

			ui.settings.setChanged();
		}
	}

	private void handleAboutButton() {
		// modal dialog with icon
		ImageIcon icon = new ImageIcon(getClass().getResource("/logo.png"));
		JOptionPane.showConfirmDialog(null, ui.aboutPanel, "About ESP32PartitionTool", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, icon);
	}

	public void handleHelpButton() {
		// modal dialog with minimal decoration
		final JDialog dialog = new JDialog((Frame) null, "Help Tips", true);
		dialog.add(ui.helpPanel);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

}
