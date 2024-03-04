package com.serifpersia.esp32partitiontool;

import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.event.WindowEvent;
import javax.swing.*;
import java.io.*;

import javax.swing.event.*;


public class UI extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int NUM_ITEMS = 100;
	public 	static final int MIN_ITEMS = 15;
	public int lastIndex;

	public UIController controller;


	private JPanel csv_GenPanel;
	private JLabel csv_GenLabel;

	private JPanel csvPanel;
	private JScrollPane csvScrollPanel;

	private JPanel csv_PartitionsVisual;
	private JPanel partitions_UtilButtonsPanel;
	private JPanel csv_partitionsCenterVisualPanel;

	private JLabel SPIFFS_GenLabel;
	private JPanel SPIFFS_AND_MERGE_AND_FLASH_RootPanel;
	private JPanel SPIFFS_AND_MERGE_AND_FLASH_InnerPanel;

	private JComboBox<?> partitionsFlashTypes;
	private JLabel lb_filesystems;
	private JComboBox<?> partitions_FlashSizes;
	long FlashSizeBytes = 4 * 1024 * 1024 - 36864;
	int flashSizeMB = 4;
	// long FlashSizeBytes = 0;

	private JButton partitions_CSVButton;
	private JButton partitions_BinButton;
	private JPanel csv_PartitionSizeHexPanel;
	private JLabel csv_PartitionSizeHexLabel;
	private JPanel csv_PartitionSizeHexInnerPanel;
	private JLabel csv_partitionFlashFreeSpace;

	private JPanel SPIFFS_InnerPanel;
	private JLabel lb_blockSize;
	private JTextField spiffs_blockSize;
	private JPanel SPIFFS_AND_MERGE_AND_FLASH_Panel;
	private JPanel panel_2;
	private JButton btn_flashSPIFFS;
	private JPanel panel_3;
	private JLabel lblNewLabel_3;
	private JPanel panel_4;
	private JPanel panel_5;
	// private JButton btn_flashSketch;
	private JButton btn_flashMergedBin;
	private JButton btn_help;
	private JButton btn_about;
	private JButton partitions_ImportCSVButton;

	private JCheckBox enableDebugCheckBox;      // used in pref panel
	private JCheckBox confirmOverwriteCheckBox; // used in pref panel
	private JCheckBox confirmDataEmptyCheckBox; // used in pref panel
	private JCheckBox rememberChoiceCheckBox = new JCheckBox("Remember my decision"); // used in confirmDialogs

	public ArrayList<CSVRow> csvRows = new ArrayList<CSVRow>();


	public UI() {
		setLayout(new BorderLayout(0, 0));
		init();
	}


	private void init() {
		createPanels();
		createFreeSpaceBox();
		createHelpButton();
		createAboutButton();
		createOverwriteCheckBox();
		createConfirmDataEmptyCheckBox();
		createDebugCheckBox();
		calculateOffsets();
		createPartitionFlashVisualPanel();
		updatePartitionFlashVisual();
	}


	public void addCSVRow( CSVRow line ) {
		if( line == null )  {
			line = new CSVRow(null);
		}
		line.attachListeners( this.controller );
		line.size.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) { recalculate(); }
			public void removeUpdate(DocumentEvent e) { recalculate(); }
			public void insertUpdate(DocumentEvent e) { recalculate(); }
			public void recalculate() {
				calculateSizeHex();
				calculateOffsets();
				updatePartitionFlashVisual();
			}
		});

		line.subtype.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) { validate(); }
			public void removeUpdate(DocumentEvent e) { validate(); }
			public void insertUpdate(DocumentEvent e) { validate(); }
			public void validate() {
				validateSubtypes();
			}
		});

		csvRows.add( line );
	}


	public void popCSVRow() {
		int lastIndex = csvRows.size() -1;
		if( lastIndex >= 0 ) {
		  CSVRow csvRow = getCSVRow(lastIndex);
		  csvPanel.remove( csvRow );
			csvRows.remove(lastIndex);
		}
	}


	public void removeCVSLine(int index) {
		int lastIndex = csvRows.size() -1;
		if( lastIndex >= 0 && lastIndex>=index) {
			CSVRow csvRow = getCSVRow(lastIndex);
			csvPanel.remove( csvRow );
			csvRows.remove(index);
		}
	}


	public CSVRow getCSVRow(int index) {
		if( index>=0 && csvRows.size() > index ) {
			return csvRows.get(index);
		}
		return null;
	}


	public void clearCSVRows() {
		csvRows.clear();
	}


	public void renderCSVRows() {
		csvPanel.removeAll();
		int layoutSize = csvRows.size()+2 < MIN_ITEMS+1 ? MIN_ITEMS+1 : csvRows.size()+2;
		csvPanel.setLayout( new GridLayout(layoutSize, 0, 0, 0) );
		addTitleCSVRow(); // add column titles
		while( csvRows.size() < layoutSize-1 ) {
			addCSVRow( null );
		}
		for( int i=0; i<csvRows.size(); i++ ) {
			csvPanel.add( getCSVRow(i), BorderLayout.CENTER );
		}
	}


	public void addTitleCSVRow() {
		JPanel titleLinePanel = new JPanel();
		titleLinePanel.setLayout(new GridLayout(0, 7, 0, 0));

		String labels[] = { "Enable", "Name", "Type", "SubType", "Size(kB)", "Size(hex)", "Offset(hex)" };
		for( int i=0; i<labels.length; i++ ) {
			JLabel label = new JLabel( labels[i] );
			label.setHorizontalAlignment(SwingConstants.CENTER);
			titleLinePanel.add(label, BorderLayout.NORTH);
		}
		csvPanel.add( titleLinePanel );
	}


	// confirm dialog with optional "Remember my decision" checkbox
	public boolean confirmDialogOverwrite( String msg, String title ) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(msg), BorderLayout.NORTH);

		panel.add(rememberChoiceCheckBox, BorderLayout.SOUTH);

		int userConfirm = JOptionPane.showConfirmDialog(new JFrame(), panel, title, JOptionPane.YES_NO_OPTION);

		if( rememberChoiceCheckBox.isSelected()  ) {
			getOverwriteCheckBox().setSelected( userConfirm != JOptionPane.YES_OPTION );
		}

		return userConfirm == JOptionPane.YES_OPTION;
	}



	private void createPanels() {
		csv_GenPanel = new JPanel();
		add(csv_GenPanel);
		csv_GenPanel.setLayout(new BorderLayout(0, 0));
		csv_GenPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		csv_GenLabel = new JLabel("Partitions");
		csv_GenLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		csv_GenLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_GenPanel.add(csv_GenLabel, BorderLayout.NORTH);

		csvPanel = new JPanel();
		csvScrollPanel = new JScrollPane( csvPanel );
		csvScrollPanel.getVerticalScrollBar().setUnitIncrement(100); // prevent the scroll wheel from going sloth

		csv_GenPanel.add(csvScrollPanel, BorderLayout.CENTER );

		csv_PartitionsVisual = new JPanel();
		csv_GenPanel.add(csv_PartitionsVisual, BorderLayout.SOUTH);
		csv_PartitionsVisual.setLayout(new BorderLayout(0, 0));

		partitions_UtilButtonsPanel = new JPanel();
		csv_PartitionsVisual.add(partitions_UtilButtonsPanel, BorderLayout.NORTH);

		JLabel csv_FlashSizeLabel = new JLabel("Flash Size: MB");
		csv_FlashSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		partitions_UtilButtonsPanel.add(csv_FlashSizeLabel);

		partitions_FlashSizes = new JComboBox<>(new String[] { "4", "8", "16", "32" });
		partitions_UtilButtonsPanel.add(partitions_FlashSizes);

		partitions_ImportCSVButton = new JButton("Import CSV");
		partitions_UtilButtonsPanel.add(partitions_ImportCSVButton);

		partitions_CSVButton = new JButton("Export CSV");
		partitions_UtilButtonsPanel.add(partitions_CSVButton);

		partitions_BinButton = new JButton("Create Bin");
		partitions_UtilButtonsPanel.add(partitions_BinButton);

		SPIFFS_AND_MERGE_AND_FLASH_RootPanel = new JPanel();
		add(SPIFFS_AND_MERGE_AND_FLASH_RootPanel, BorderLayout.EAST);
		SPIFFS_AND_MERGE_AND_FLASH_RootPanel.setLayout(new BorderLayout(0, 0));
		SPIFFS_AND_MERGE_AND_FLASH_RootPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		SPIFFS_GenLabel = new JLabel("SPIFFS");
		SPIFFS_GenLabel.setHorizontalAlignment(SwingConstants.CENTER);
		SPIFFS_GenLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		SPIFFS_AND_MERGE_AND_FLASH_RootPanel.add(SPIFFS_GenLabel, BorderLayout.NORTH);

		SPIFFS_AND_MERGE_AND_FLASH_InnerPanel = new JPanel();

		SPIFFS_AND_MERGE_AND_FLASH_RootPanel.add(SPIFFS_AND_MERGE_AND_FLASH_InnerPanel, BorderLayout.CENTER);
		SPIFFS_AND_MERGE_AND_FLASH_InnerPanel.setLayout(new BorderLayout(0, 0));

		GridLayout gl_SPIFFS_InnerPanel = new GridLayout(0, 2);
		gl_SPIFFS_InnerPanel.setHgap(5);
		SPIFFS_InnerPanel = new JPanel(gl_SPIFFS_InnerPanel); // 0 rows means any number of rows, 2 columns
		SPIFFS_AND_MERGE_AND_FLASH_InnerPanel.add(SPIFFS_InnerPanel, BorderLayout.NORTH);

		lb_filesystems = new JLabel("Filesystem:");
		lb_filesystems.setHorizontalAlignment(SwingConstants.CENTER);
		SPIFFS_InnerPanel.add(lb_filesystems);

		partitionsFlashTypes = new JComboBox<>(new String[] { "SPIFFS", "LittleFS", "FatFS" });
		SPIFFS_InnerPanel.add(partitionsFlashTypes);

		lb_blockSize = new JLabel("Block Size:");
		lb_blockSize.setHorizontalAlignment(SwingConstants.CENTER);
		SPIFFS_InnerPanel.add(lb_blockSize);

		spiffs_blockSize = new JTextField("4096");
		spiffs_blockSize.setEditable(false);
		SPIFFS_InnerPanel.add(spiffs_blockSize);

		SPIFFS_AND_MERGE_AND_FLASH_Panel = new JPanel();
		SPIFFS_AND_MERGE_AND_FLASH_InnerPanel.add(SPIFFS_AND_MERGE_AND_FLASH_Panel, BorderLayout.CENTER);
		SPIFFS_AND_MERGE_AND_FLASH_Panel.setLayout(new BorderLayout(0, 0));

		panel_2 = new JPanel();
		SPIFFS_AND_MERGE_AND_FLASH_Panel.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new BorderLayout(0, 0));

		btn_flashSPIFFS = new JButton("SPIFFS");
		panel_2.add(btn_flashSPIFFS);

		panel_3 = new JPanel();
		SPIFFS_AND_MERGE_AND_FLASH_Panel.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new BorderLayout(0, 0));
		panel_3.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

		lblNewLabel_3 = new JLabel("Merge");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);

		panel_3.add(lblNewLabel_3, BorderLayout.NORTH);

		panel_4 = new JPanel();
		panel_3.add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new BorderLayout(0, 0));

		panel_5 = new JPanel();
		panel_4.add(panel_5, BorderLayout.NORTH);
		panel_5.setLayout(new GridLayout(2, 0, 0, 0));

		btn_flashMergedBin = new JButton("Merge Binary");
		panel_5.add(btn_flashMergedBin);

		// TODO: insert image 170 * 384 ?

	}


	private void createFreeSpaceBox() {
		csv_partitionFlashFreeSpace = new JLabel("Free Space: not set");
		partitions_UtilButtonsPanel.add(csv_partitionFlashFreeSpace);
	}

	private void createAboutButton() {
		btn_about = new JButton("About");
		partitions_UtilButtonsPanel.add(btn_about);
	}


	private void createHelpButton() {
		btn_help = new JButton("Help");
		partitions_UtilButtonsPanel.add(btn_help);
	}


	private void createConfirmDataEmptyCheckBox() {
		confirmDataEmptyCheckBox = new JCheckBox("[?]");
		confirmDataEmptyCheckBox.setToolTipText("Will ask for confirmation if a 'data' folder is missing from the sketch folder before creating spiffs.bin.");
		partitions_UtilButtonsPanel.add(confirmDataEmptyCheckBox);
	}


	private void createOverwriteCheckBox() {
		confirmOverwriteCheckBox = new JCheckBox("Overwrite");
		confirmOverwriteCheckBox.setToolTipText("Automatically overwrite partitions.csv without asking for confirmation.");
		partitions_UtilButtonsPanel.add(confirmOverwriteCheckBox);
	}


	private void createDebugCheckBox() {
		enableDebugCheckBox = new JCheckBox("debug");
		partitions_UtilButtonsPanel.add(enableDebugCheckBox);
	}


	public String[] convertKbToHex(long[] sizes) {
		String[] hexValues = new String[sizes.length];
		for (int i = 0; i < sizes.length; i++) {
			if (sizes[i] != 0) {
				long bytes = sizes[i] * 1024; // Convert kilobytes to bytes
				String hexValue = Long.toHexString(bytes); // Convert bytes to hexadecimal
				hexValues[i] = hexValue.toUpperCase(); // Prefix "0x" and convert to uppercase
			} else {
				hexValues[i] = ""; // Set empty string if size is zero
			}
		}
		return hexValues;
	}


	public void calculateSizeHex() {
		FlashSizeBytes = flashSizeMB * 1024 * 1024 - 0x9000;
		long[] partitionSizes = new long[getNumOfItems()];

		// Iterate through all text fields to calculate the total size
		for (int i = 0; i < getNumOfItems(); i++) {
			CSVRow csvRow = getCSVRow(i);
			if ( csvRow == null ) {
				System.err.println("csv line # " + i + " not found, skipping");
				continue;
			}
			if( !csvRow.enabled.isSelected() ) continue;
			String sizeText = csvRow.size.getText();
			if (!sizeText.isEmpty()) {
				try {
					long partitionTotalSize = Long.parseLong(sizeText) * 1024;
					FlashSizeBytes -= partitionTotalSize;
					// Store partition size in kilobytes
					partitionSizes[i] = partitionTotalSize / 1024;
				} catch (NumberFormatException e) {
					// Handle parsing errors if necessary
					System.out.println("Invalid input in text field " + i + ": " + sizeText );
				}
			}
		}

		// Update the free space label
		getFlashFreeLabel().setText("Free Space: " + FlashSizeBytes / 1024 + " kB");
		getFlashFreeLabel().setForeground( FlashSizeBytes >= 0 ? Color.BLACK : Color.RED );

		// Convert partition sizes to hexadecimal strings
		String[] hexStrings = convertKbToHex(partitionSizes);

		// Set hexadecimal strings to the respective text fields
		for (int i = 0; i < hexStrings.length; i++) {
			CSVRow csvRow = getCSVRow(i);
			if ( csvRow == null ) {
				System.err.println("csv line # " + i + " not found, skipping");
				continue;
			}
			csvRow.sizeHex.setText(hexStrings[i]);
		}
	}


	public void calculateOffsets() {
		lastIndex = -1; // Initialize to -1 indicating no selected index found yet

		// Find the last selected index
		for (int i = 0; i < getNumOfItems(); i++) {
			if (getCheckBox(i).isSelected()) {
				lastIndex = i;
			} else {
				break; // Break the loop if a non-selected checkbox is found
			}
		}

		if (lastIndex != -1) { // If a selected index is found
			long previousOffset=0, size=0;
			for (int i = 1; i <= lastIndex + 1; i++) { // Calculate offsets including the hardcoded first offset
				CSVRow prevLine = getCSVRow( i - 1 );
				CSVRow currLine = getCSVRow( i );
				if (currLine != null && prevLine != null && !prevLine.sizeHex.getText().isEmpty()) {
					String previousOffsetHex = prevLine.offset.getText();
					String sizeHex = prevLine.sizeHex.getText();

					try {
						// Convert the hexadecimal strings to long values
						previousOffset = Long.parseLong(previousOffsetHex, 16);
						size = Long.parseLong(sizeHex, 16);
					} catch( NumberFormatException e ) {
						size = 0; // cell was either disabled or contains invalid value, safe to ignore
					}

					// Calculate the new offset
					long newOffset = previousOffset + size;


					if(newOffset%0x1000!=0 ) { // uh-oh, not a multiple of 4096!!
						// System.err.println( newOffset + " is not a multiple of 4096 (0x1000 )");
						currLine.size.setForeground(Color.RED);
						prevLine.size.setForeground(Color.RED);
					} else {
						currLine.size.setForeground(Color.BLACK);
						prevLine.size.setForeground(Color.BLACK);
					}
					// Convert the new offset back to hexadecimal and set it to the current offset field
					currLine.offset.setText( Long.toHexString(newOffset).toUpperCase() );
				}
			}
		}
	}


	private void createPartitionFlashVisualPanel() {
		csv_partitionsCenterVisualPanel = new JPanel(new GridBagLayout());
		csv_PartitionsVisual.add(csv_partitionsCenterVisualPanel, BorderLayout.SOUTH);
	}


	public void updatePartitionFlashTypeLabel() {
		SPIFFS_GenLabel.setText( (String)getPartitionFlashType().getSelectedItem() );
		btn_flashSPIFFS.setText( (String)getPartitionFlashType().getSelectedItem() );
	}


	public void validateSubtypes() {
		//
		for( int i=0;i<csvRows.size();i++ ) {
			CSVRow csvRow = getCSVRow( i );
			if( !csvRow.enabled.isSelected() ) continue;
			String type    = (String)csvRow.type.getSelectedItem();
			String subtype = csvRow.subtype.getText();
			csvRow.subtype.setForeground( csvRow.isValidSubtype(subtype) ? Color.BLACK : Color.RED );

			if     ( type.equals("data") && subtype.equals("fat") )      getPartitionFlashType().setSelectedItem("FatFS");
			else if( type.equals("data") && subtype.equals("spiffs") )   getPartitionFlashType().setSelectedItem("SPIFFS");
			else if( type.equals("data") && subtype.equals("littlefs") ) getPartitionFlashType().setSelectedItem("LittleFS");

		}
	}


	public void updatePartitionLabel( String label ) {
		csv_GenLabel.setText( label );
	}


	public void updatePartitionFlashVisual() {
		// Clear the center panel before updating
		csv_partitionsCenterVisualPanel.removeAll();

		int FLASH_SIZE = flashSizeMB * 1024;
		int RESERVED_SPACE = 36;
		int totalPartitionSize = 0;

		for (int i = 0; i < getNumOfItems(); i++) {
			CSVRow csvRow = getCSVRow( i );
			if ( csvRow == null ) {
				System.err.println("csv line # " + i + " not found, skipping");
				continue;
			}
			if (!csvRow.enabled.isSelected()) continue;
			try {
				totalPartitionSize += Integer.parseInt(csvRow.size.getText());
			} catch (NumberFormatException e) { }
		}

		int remainingSpace = FLASH_SIZE - RESERVED_SPACE - totalPartitionSize;

		JPanel initialPanel = new JPanel(new BorderLayout());
		JLabel initialLabel = new JLabel("0x9000");
		GridBagConstraints gbc = new GridBagConstraints();

		initialLabel.setHorizontalAlignment(SwingConstants.CENTER);
		initialPanel.add(initialLabel, BorderLayout.CENTER);
		initialPanel.setPreferredSize(new Dimension(50, 24));

		gbc.fill = GridBagConstraints.HORIZONTAL;
		//gbc.setForeground( remainingSpace > 0 ? Color.BLACK : Color.RED );

		csv_partitionsCenterVisualPanel.add(initialPanel, gbc);

		// TODO: get rid of duplicate code
		if (remainingSpace > 0) {
			for (int i = 0; i < getNumOfItems(); i++) {
				CSVRow csvRow = getCSVRow( i );
				if ( csvRow == null ) {
					System.err.println("csv line # " + i + " not found, skipping");
					continue;
				}
				if (!csvRow.enabled.isSelected()) continue;
				try {
					int partitionSize = Integer.parseInt(csvRow.size.getText());
					double weight = (double) partitionSize / (FLASH_SIZE - RESERVED_SPACE);
					JPanel partitionPanel = new JPanel();

					String partName = (String) getPartitionName(i).getText();
					String partType = (String) getPartitionType(i).getSelectedItem();
					String partSubType = (String) getPartitionSubType(i).getText();

					Color partColor = partType.equals("app") ? new Color(66, 176, 245) : new Color(47, 98, 207);
					if (partSubType.equals("factory")) {
						partColor = new Color(40, 87, 163); // Darker Blue for Factory
					} else if (partSubType.equals("spiffs")) {
						partColor = new Color(154, 65, 194); // Purple for SPIFFS
					} else if (partSubType.equals("coredump")) {
						partColor = new Color(200, 50, 50); // Dark Red for coredump
					} else if (partName.equals("nvs")) {
						partColor = new Color(215, 166, 49); // Gold for NVS
					} else if (partName.equals("otadata")) {
						partColor = new Color(191, 69, 122); // Magenta for OTA Data
					}

					partitionPanel.setBackground(partColor);
					// Set the text color to white
					JLabel label = new JLabel(getPartitionSubType(i).getText());
					label.setForeground(Color.WHITE);
					partitionPanel.add(label);

					partitionPanel.setBorder(BorderFactory.createEtchedBorder());

					gbc.weightx = weight;
					csv_partitionsCenterVisualPanel.add(partitionPanel, gbc);
				} catch (NumberFormatException e) {
				}
			}

			JPanel unusedSpacePanel = new JPanel();
			unusedSpacePanel.setBorder(BorderFactory.createEtchedBorder());
			unusedSpacePanel.setBackground(Color.GRAY);
			gbc.weightx = (double) remainingSpace / (FLASH_SIZE - RESERVED_SPACE);
			// Set the text color to white
			JLabel label = new JLabel("Free Space");
			label.setForeground(Color.WHITE);
			unusedSpacePanel.add(label);

			csv_partitionsCenterVisualPanel.add(unusedSpacePanel, gbc);
		} else {
			for (int i = 0; i < getNumOfItems(); i++) {
				CSVRow csvRow = getCSVRow( i );
				if ( csvRow == null ) {
					System.err.println("csv line # " + i + " not found, skipping");
					continue;
				}
				if (!csvRow.enabled.isSelected()) continue;
				try {
					int partitionSize = Integer.parseInt(csvRow.size.getText());
					double weight = (double) partitionSize / (FLASH_SIZE - RESERVED_SPACE);
					JPanel partitionPanel = new JPanel();

					String partName    = (String) getPartitionName(i).getText();
					String partType    = (String) getPartitionType(i).getSelectedItem();
					String partSubType = (String) getPartitionSubType(i).getText();

					Color partColor = partType.equals("app") ? new Color(66, 176, 245) : new Color(47, 98, 207);
					if (partSubType.equals("factory")) {
						partColor = new Color(40, 87, 163); // Darker Blue for Factory
					} else if (partSubType.equals("spiffs")) {
						partColor = new Color(154, 65, 194); // Purple for SPIFFS
					} else if (partSubType.equals("coredump")) {
						partColor = new Color(200, 50, 50); // Dark Red for coredump
					} else if (partName.equals("nvs")) {
						partColor = new Color(215, 166, 49); // Gold for NVS
					} else if (partName.equals("otadata")) {
						partColor = new Color(191, 69, 122); // Magenta for OTA Data
					}

					partitionPanel.setBackground(partColor);
					// Set the text color to white
					JLabel label = new JLabel(partSubType);
					label.setForeground(Color.WHITE);
					partitionPanel.add(label);

					partitionPanel.setBorder(BorderFactory.createEtchedBorder());

					gbc.weightx = weight;
					csv_partitionsCenterVisualPanel.add(partitionPanel, gbc);
				} catch (NumberFormatException e) {
				}

			}
		}

		// Revalidate and repaint the center panel to reflect changes
		csv_partitionsCenterVisualPanel.revalidate();
		csv_partitionsCenterVisualPanel.repaint();
	}


	public long         getFlashBytes() { return FlashSizeBytes; }
	public int          getNumOfItems() { return csvRows.size(); }
	public JButton      getImportCSVButton() { return partitions_ImportCSVButton; }
	public JButton      getCreatePartitionsCSV() { return partitions_CSVButton; }
	public JButton      getCreatePartitionsBin() { return partitions_BinButton; }
	public JButton      getFlashSPIFFSButton() { return btn_flashSPIFFS; }
	public JButton      getFlashMergedBin() { return btn_flashMergedBin; }
	public JButton      getHelpButton() { return btn_help; }
	public JButton      getAboutButton() { return btn_about; }
	public JTextField   getSpiffsBlockSize() { return spiffs_blockSize; }
	public JCheckBox    getDebug() { return enableDebugCheckBox; }
	public JCheckBox    getConfirmDataEmptyCheckBox() { return confirmDataEmptyCheckBox; }
	public JCheckBox    getOverwriteCheckBox() { return confirmOverwriteCheckBox; }
	public JComboBox<?> getPartitionFlashType() { return partitionsFlashTypes; }
	public JComboBox<?> getFlashSize() { return partitions_FlashSizes; }
	public JLabel       getFlashFreeLabel() { return csv_partitionFlashFreeSpace; }



	// Getter method to access a specific checkbox by index
	public JCheckBox getCheckBox(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow( index );
			if ( csvRow == null ) {
				return null;
			}
			return csvRow.enabled;
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionName(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow( index );
			if ( csvRow == null ) {
				return null;
			}
			return csvRow.name;
		}
		return null;
	}

	// Getter method to access a specific partitionType by index
	public JComboBox<?> getPartitionType(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow( index );
			if ( csvRow == null ) {
				return null;
			}
			return csvRow.type;
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionSubType(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow( index );
			if ( csvRow == null ) {
				return null;
			}
			return csvRow.subtype;
		}
		return null;
	}

	// Getter method to access a specific partitionSize by index
	public JTextField getPartitionSize(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow( index );
			if ( csvRow == null ) {
				return null;
			}
			return csvRow.size;
		}
		return null;
	}

	// Getter method to access a specific partitionSizeHex by index
	public JTextField getPartitionSizeHex(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow( index );
			if ( csvRow == null ) {
				return null;
			}
			return csvRow.sizeHex;
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionOffsets(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow( index );
			if ( csvRow == null ) {
				return null;
			}
			return csvRow.offset;
		}
		return null;
	}


	public String getSpiffsOffset() {
		String result = "";
		int spiffsIndex = -1; // Initialize spiffsIndex to -1 to indicate not found
		// Iterate through the partition subtypes to find the SPIFFS partition
		for (int i = 0; i < getNumOfItems(); i++) {
		  if (!getCSVRow(i).enabled.isSelected()) continue;
			JTextField partitionSubType = getPartitionSubType(i);
			if (partitionSubType != null && partitionSubType.getText().equals("spiffs")) {
				spiffsIndex = i;
				break; // Exit the loop once SPIFFS partition subtype is found
			}
		}
		if (spiffsIndex != -1) {
			// Retrieve the partition offset using the SPIFFS partition subtype index
			String partitionOffset = getPartitionOffsets(spiffsIndex).getText();
			result = "0x" + partitionOffset;
		} else {
			result = "SPIFFS partition not found.";
		}
		return result;
	}

}
