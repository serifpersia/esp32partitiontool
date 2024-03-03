package com.serifpersia.esp32partitiontool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Desktop;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.event.*;
import java.net.*;
import java.io.IOException;



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
		for( int i=0; i<ui.csvRows.size(); i++ ) {
			if( ui.getCSVRow(i) == component.getParent().getParent() ) {
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
			fileManager.createPartitionsBin();
		} else if (e.getSource() == ui.getFlashSPIFFSButton()) {
			fileManager.handleSPIFFS();
		} else if (e.getSource() == ui.getFlashMergedBin()) {
			fileManager.handleMergedBin();
		} else if (e.getSource() == ui.getHelpButton()) {
			handleHelpButton();
		} else if (e.getSource() == ui.getAboutButton()) {
			handleAboutButton();
		}

	}

	private void handleCheckBoxAction(JCheckBox checkBox) {
		boolean isSelected = checkBox.isSelected();

		if( checkBox == ui.getDebug() ) {
			fileManager.setDebug(isSelected);
			return;
		}

		int csvRowId = getRowIndexForComponent(checkBox);
		if( csvRowId < 0 ) return;

		CSVRow csvRow = ui.getCSVRow(csvRowId);

		csvRow.name.   setEditable( isSelected );
		csvRow.type.   setEnabled(  isSelected );
		csvRow.subtype.setEditable( isSelected );
		csvRow.size.   setEditable( isSelected );
		csvRow.sizeHex.setEditable( isSelected );
		csvRow.offset. setEditable( isSelected );

		if( isSelected) {
			if( csvRowId<6 ) csvRow.setDefaults( csvRowId );
			else csvRow.enableRow();
			if( csvRowId == ui.csvRows.size()-1 ) { // we're enabling the last checkbox, add one!
			  if( csvRowId > 0 ) {
					//CSVRow prevLine = ui.getCSVRow(csvRowId-1);
					//if( prevLine.enabled.isSelected() ) { // previous checkbox is enabled too
					ui.renderCSVRows(); // the additional empty line will be inserted by renderCSVRows()
					//}
			  }
			}
		} else {
			//csvRow.setDefaults( -1 );
			csvRow.disableRow();
			if( csvRowId>=ui.MIN_ITEMS-1 && csvRowId == ui.csvRows.size()-2 ) { // we're disabling the last enabled checkbox
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
		//int id = getIndexForComponent(textField);

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
			String toolPath = fileManager.prefs.getProperty("mk"+fsName.toLowerCase()+".path");
			if( toolPath == null ) {
				System.err.println("Invalid filesystem :" + fsName);
			} else {
				System.out.println("Changed filesystem to :" + fsName);
			}
		}
	}


	private void handleAboutButton() {

			String boxpadding   = "padding-top: 0px;padding-right: 10px;padding-bottom: 10px;padding-left: 10px;";
			String titleSpanned = "<span style=\"background-color: #d7a631\">&nbsp;ESP32&nbsp;</span>"
													+ "<span style=\"background-color: #bf457a\">&nbsp;Partition&nbsp;</span>"
													+ "<span style=\"background-color: #42b0f5\">&nbsp;Tool&nbsp;</span>"
													+ "<span style=\"background-color: #9a41c2\">&nbsp;v1.3&nbsp;</span>";
			String title        = "<h2 align=center style=\"color: #ffffff;\">"+titleSpanned+"</h2>";
			String description  = "<p>The ESP32 Partition Tool is a utility designed to ease the manipulation<br>"
													+ "of custom partition schemes in the Arduino IDE 1.8.x environment.<br>"
													+ "This tool aims to simplify the process of creating custom partition<br>"
													+ "schemes for ESP32 projects.</p>";
			String projectlink  = "<p><b>Source:</b><br>https://github.com/serifpersia/esp32partitiontool</p>";
			String copyright    = "<p><b>Copyright (c) 2024 @serifpersia</b><br>https://github.com/serifpersia</p>";
			String credits      = "<p><b>Contributors:</b><br>serifpersia, tobozo</p>";
			String message      = "<html>"+title+"<div style=\""+boxpadding+"\">"
													+ description +  projectlink + copyright + credits + "</div></html>";

			JEditorPane ep = new JEditorPane("text/html", message);
			ep.setEditable(false);

			ImageIcon icon = new ImageIcon(UIController.class.getResource("/resources/shrug.png"));

			int option = JOptionPane.showConfirmDialog(null, ep, "About ESP32PartitionTool",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, icon);
	}


	private void handleHelpButton() {
		int currentStep = 0;
		String[] messages = {
				"<html>Usage:Export CSV to sketch dir, under Tools > Partition schemes, select: Huge App/No OTA/1MB SPIFFS to use custom partition scheme.</html>",
				"<html>Compile sketch to use SPIFFS & Merge tools.",
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
