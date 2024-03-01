package com.serifpersia.esp32partitiontool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.io.*;


public class UI extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int NUM_ITEMS = 15;

	private JPanel csv_GenPanel;
	private JPanel csv_RootPanel;
	private JPanel csv_EnablePanel;
	private JPanel csv_EnableInnerPanel;
	private JPanel csv_PartitionNamePanel;
	private JPanel csv_PartitionNameInnerPanel;
	private JPanel csv_PartitionTypePanel;
	private JPanel csv_PartitionTypeInnerPanel;
	private JPanel csv_PartitionSubTypePanel;
	private JPanel csv_PartitionSubTypeInnerPanel;
	private JPanel csv_PartitionSizePanel;
	private JPanel csv_PartitionSizeInnerPanel;
	private JPanel csv_PartitionOffsetsPanel;
	private JPanel csv_PartitionOffsetsInnerPanel;

	private JPanel csv_PartitionsVisual;
	private JPanel partitions_UtilButtonsPanel;
	private JPanel csv_partitionsCenterVisualPanel;

	private JPanel SPIFFS_AND_MERGE_AND_FLASH_RootPanel;
	private JPanel SPIFFS_AND_MERGE_AND_FLASH_InnerPanel;

	private JCheckBox[] partitions_EnableChckb = new JCheckBox[NUM_ITEMS];
	private JTextField[] partitionsNames = new JTextField[NUM_ITEMS];
	private JComboBox<?>[] partitionsType = new JComboBox[NUM_ITEMS];
	private JComboBox<?> partitionsFlashTypes;
	private JLabel lb_filesystems;

	private JTextField[] partitionsSubType = new JTextField[NUM_ITEMS];
	private JTextField[] partitionsSize = new JTextField[NUM_ITEMS];
	private JTextField[] partitionsSizeHex = new JTextField[NUM_ITEMS];
	private JTextField[] partitionsOffsets = new JTextField[NUM_ITEMS];

	private JComboBox<?> partitions_FlashSizes;
	int FlashSizeBytes = 4 * 1024 * 1024 - 36864;
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
	private JButton partitions_ImportCSVButton;

	private JCheckBox ui_DebugChckb;
	//private JLabel ui_DebugLabel;

	public int lastIndex;

	public UI() {
		setLayout(new BorderLayout(0, 0));
		init();
	}

	private void init() {
		createPanels();
		createPartitionsEnableToggles();
		createPartitionsNames();
		createPartitionsTypes();
		createPartitionsSubTypes();
		createPartitionsSize();
		createDebugCheckBox();
		createPartitionsSizeHex();
		createPartitionsOffsets();
		createPartitionFlashVisualPanel();
	}



	private void createPanels() {
		csv_GenPanel = new JPanel();
		add(csv_GenPanel);
		csv_GenPanel.setLayout(new BorderLayout(0, 0));
		csv_GenPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JLabel csv_GenLabel = new JLabel("Partitions");
		csv_GenLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		csv_GenLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_GenPanel.add(csv_GenLabel, BorderLayout.NORTH);

		csv_RootPanel = new JPanel();
		csv_GenPanel.add(csv_RootPanel, BorderLayout.CENTER);
		csv_RootPanel.setLayout(new GridLayout(0, 7, 0, 0));

		csv_EnablePanel = new JPanel();
		csv_RootPanel.add(csv_EnablePanel);
		csv_EnablePanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_EnableLabel = new JLabel("Enable");
		csv_EnableLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_EnablePanel.add(csv_EnableLabel, BorderLayout.NORTH);

		csv_EnableInnerPanel = new JPanel();
		csv_EnablePanel.add(csv_EnableInnerPanel, BorderLayout.CENTER);
		csv_EnableInnerPanel.setLayout(new GridLayout(15, 0, 0, 0));

		csv_PartitionNamePanel = new JPanel();
		csv_RootPanel.add(csv_PartitionNamePanel);
		csv_PartitionNamePanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_PartitionNameLabel = new JLabel("Name");
		csv_PartitionNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionNamePanel.add(csv_PartitionNameLabel, BorderLayout.NORTH);

		csv_PartitionNameInnerPanel = new JPanel();
		csv_PartitionNamePanel.add(csv_PartitionNameInnerPanel, BorderLayout.CENTER);
		csv_PartitionNameInnerPanel.setLayout(new GridLayout(15, 0, 0, 0));

		csv_PartitionTypePanel = new JPanel();
		csv_RootPanel.add(csv_PartitionTypePanel);
		csv_PartitionTypePanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_PartitionTypeLabel = new JLabel("Type");
		csv_PartitionTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionTypePanel.add(csv_PartitionTypeLabel, BorderLayout.NORTH);

		csv_PartitionTypeInnerPanel = new JPanel();
		csv_PartitionTypePanel.add(csv_PartitionTypeInnerPanel, BorderLayout.CENTER);
		csv_PartitionTypeInnerPanel.setLayout(new GridLayout(15, 0, 0, 0));

		csv_PartitionSubTypePanel = new JPanel();
		csv_RootPanel.add(csv_PartitionSubTypePanel);
		csv_PartitionSubTypePanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_PartitionSubTypeLabel = new JLabel("SubType");
		csv_PartitionSubTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionSubTypePanel.add(csv_PartitionSubTypeLabel, BorderLayout.NORTH);

		csv_PartitionSubTypeInnerPanel = new JPanel();
		csv_PartitionSubTypePanel.add(csv_PartitionSubTypeInnerPanel, BorderLayout.CENTER);
		csv_PartitionSubTypeInnerPanel.setLayout(new GridLayout(15, 0, 0, 0));

		csv_PartitionSizePanel = new JPanel();
		csv_RootPanel.add(csv_PartitionSizePanel);
		csv_PartitionSizePanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_PartitionSizeLabel = new JLabel("Size(kB)");
		csv_PartitionSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionSizePanel.add(csv_PartitionSizeLabel, BorderLayout.NORTH);

		csv_PartitionSizeInnerPanel = new JPanel();
		csv_PartitionSizePanel.add(csv_PartitionSizeInnerPanel, BorderLayout.CENTER);
		csv_PartitionSizeInnerPanel.setLayout(new GridLayout(15, 0, 0, 0));

		csv_PartitionSizeHexPanel = new JPanel();
		csv_RootPanel.add(csv_PartitionSizeHexPanel);
		csv_PartitionSizeHexPanel.setLayout(new BorderLayout(0, 0));

		csv_PartitionSizeHexLabel = new JLabel("Size(hex)");
		csv_PartitionSizeHexLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionSizeHexPanel.add(csv_PartitionSizeHexLabel, BorderLayout.NORTH);

		csv_PartitionSizeHexInnerPanel = new JPanel();
		csv_PartitionSizeHexPanel.add(csv_PartitionSizeHexInnerPanel, BorderLayout.CENTER);
		csv_PartitionSizeHexInnerPanel.setLayout(new GridLayout(15, 0, 0, 0));

		csv_PartitionOffsetsPanel = new JPanel();
		csv_RootPanel.add(csv_PartitionOffsetsPanel);
		csv_PartitionOffsetsPanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_PartitionOffsetsLabel = new JLabel("Offset(hex)");
		csv_PartitionOffsetsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionOffsetsPanel.add(csv_PartitionOffsetsLabel, BorderLayout.NORTH);

		csv_PartitionOffsetsInnerPanel = new JPanel();
		csv_PartitionOffsetsPanel.add(csv_PartitionOffsetsInnerPanel, BorderLayout.CENTER);
		csv_PartitionOffsetsInnerPanel.setLayout(new GridLayout(15, 0, 0, 0));

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

		JLabel SPIFFS_GenLabel = new JLabel("SPIFFS");
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

		btn_flashMergedBin = new JButton("Merged Binary");
		panel_5.add(btn_flashMergedBin);

	}

	private void createPartitionsEnableToggles() {
		for (int i = 0; i < NUM_ITEMS; i++) {
			partitions_EnableChckb[i] = new JCheckBox("Enable");
			// Set the first 6 checkboxes as selected (enabled) by default
			partitions_EnableChckb[i].setSelected(i < 6);
			partitions_EnableChckb[i].setHorizontalAlignment(SwingConstants.CENTER);
			csv_EnableInnerPanel.add(partitions_EnableChckb[i]);
		}
	}

	private void createPartitionsNames() {
		String[] initialTexts = { "nvs", "otadata", "app0", "app1", "spiffs", "coredump" };

		for (int i = 0; i < NUM_ITEMS; i++) {
			partitionsNames[i] = new JTextField(i < initialTexts.length ? initialTexts[i] : "");
			partitionsNames[i].setHorizontalAlignment(SwingConstants.CENTER);
			partitionsNames[i].setColumns(10);
			csv_PartitionNameInnerPanel.add(partitionsNames[i]);
			// Set enabled property to false for items 6-NUM_ITEMS
			if (i >= 6 && i <= NUM_ITEMS) {
				partitionsNames[i].setEditable(false);
			}
		}
	}

	private void createPartitionsTypes() {
		String[] defaultItems = { "data", "data", "app", "app", "data", "data", "data", "data", "data", "data", "data",
				"data", "data", "data", "data" };

		for (int i = 0; i < NUM_ITEMS; i++) {
			partitionsType[i] = new JComboBox<>(new String[] { "data", "app" });
			partitionsType[i].setSelectedItem(defaultItems[i]);
			csv_PartitionTypeInnerPanel.add(partitionsType[i]);

			// Set enabled property to false for items 6-NUM_ITEMS
			if (i >= 6 && i <= NUM_ITEMS) {
				partitionsType[i].setEnabled(false);
			}
		}
	}

	private void createPartitionsSubTypes() {
		String[] initialTexts = { "nvs", "ota", "ota_0", "ota_1", "spiffs", "coredump" };

		for (int i = 0; i < NUM_ITEMS; i++) {
			partitionsSubType[i] = new JTextField(i < initialTexts.length ? initialTexts[i] : "");
			partitionsSubType[i].setHorizontalAlignment(SwingConstants.CENTER);
			partitionsSubType[i].setColumns(10);
			csv_PartitionSubTypeInnerPanel.add(partitionsSubType[i]);
			// Set enabled property to false for items 6-NUM_ITEMS
			if (i >= 6 && i <= NUM_ITEMS) {
				partitionsSubType[i].setEditable(false);
			}
		}
	}

	private void createPartitionsSize() {
		String[] initialPartitionSizeArray = { "20", "8", "1280", "1280", "1408", "64" };
		for (int i = 0; i < NUM_ITEMS; i++) {
			String value = (i < initialPartitionSizeArray.length) ? initialPartitionSizeArray[i] : "";
			partitionsSize[i] = new JTextField(value);
			partitionsSize[i].setHorizontalAlignment(SwingConstants.RIGHT);
			final Font currFont = partitionsSize[i].getFont(); // use monospace font for hex/dec values
			partitionsSize[i].setFont(new Font(Font.MONOSPACED, currFont.getStyle(), currFont.getSize()));
			partitionsSize[i].setColumns(10);
			csv_PartitionSizeInnerPanel.add(partitionsSize[i]);
			// Set enabled property to false for items 6-NUM_ITEMS
			if (i >= 6 && i <= NUM_ITEMS) {
				partitionsSize[i].setEditable(false);
			}
		}
		// Iterate through all text fields to calculate the total size
		for (int i = 0; i < getNumOfItems(); i++) {
			if (!getPartitionSize(i).getText().isEmpty()) {
				try {
					int partitionsTotalSize = Integer.parseInt(getPartitionSize(i).getText()) * 1024;
					FlashSizeBytes -= partitionsTotalSize;
				} catch (NumberFormatException e) {
					// Handle parsing errors if necessary
					System.out.println("Invalid input in text field " + i);
				}
			}
		}
		csv_partitionFlashFreeSpace = new JLabel("Free Space: " + FlashSizeBytes / 1024 + " bytes");
		partitions_UtilButtonsPanel.add(csv_partitionFlashFreeSpace);

		btn_help = new JButton("Help");
		partitions_UtilButtonsPanel.add(btn_help);
	}

	private void createDebugCheckBox() {
		ui_DebugChckb = new JCheckBox("debug");
		partitions_UtilButtonsPanel.add(ui_DebugChckb);
	}


	public String[] convertKbToHex(int[] sizes) {
		String[] hexValues = new String[sizes.length];
		for (int i = 0; i < sizes.length; i++) {
			if (sizes[i] != 0) {
				int bytes = sizes[i] * 1024; // Convert kilobytes to bytes
				String hexValue = Integer.toHexString(bytes); // Convert bytes to hexadecimal
				hexValues[i] = hexValue.toUpperCase(); // Prefix "0x" and convert to uppercase
			} else {
				hexValues[i] = ""; // Set empty string if size is zero
			}
		}
		return hexValues;
	}

	private int[] getPartitionsSizeValues() {
		int[] sizes = new int[NUM_ITEMS];
		for (int i = 0; i < NUM_ITEMS; i++) {
			String value = partitionsSize[i].getText();
			try {
				sizes[i] = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				sizes[i] = -1; // Set default value to -1 if parsing fails
			}
		}
		return sizes;
	}

	private void createPartitionsSizeHex() {
		// Get the partitionSize values
		int[] sizes = getPartitionsSizeValues();

		// Convert sizes array to hexadecimal
		String[] hexValues = convertKbToHex(sizes);

		// Populate partitionSizeHex fields with hexadecimal values
		for (int i = 0; i < NUM_ITEMS; i++) {
			partitionsSizeHex[i] = new JTextField(sizes[i] != -1 ? hexValues[i] : "");
			partitionsSizeHex[i].setHorizontalAlignment(SwingConstants.RIGHT);
			final Font currFont = partitionsSizeHex[i].getFont(); // use monospace font for hex/dec values
			partitionsSizeHex[i].setFont(new Font(Font.MONOSPACED, currFont.getStyle(), currFont.getSize()));
			partitionsSizeHex[i].setColumns(10);
			partitionsSizeHex[i].setEditable(false);
			csv_PartitionSizeHexInnerPanel.add(partitionsSizeHex[i]);
		}
	}

	public void calculateSizeHex() {

		FlashSizeBytes = flashSizeMB * 1024 * 1024 - 36864;
		int[] partitionSizes = new int[getNumOfItems()];

		// Iterate through all text fields to calculate the total size
		for (int i = 0; i < getNumOfItems(); i++) {
			if (!getPartitionSize(i).getText().isEmpty()) {
				try {
					int partitionTotalSize = Integer.parseInt(getPartitionSize(i).getText()) * 1024;

					FlashSizeBytes -= partitionTotalSize;

					// Store partition size in kilobytes
					partitionSizes[i] = partitionTotalSize / 1024;
				} catch (NumberFormatException e) {
					// Handle parsing errors if necessary
					System.out.println("Invalid input in text field " + i);
				}
			}
		}

		// Update the free space label
		getFlashFreeLabel().setText("Free Space: " + FlashSizeBytes / 1024 + " bytes");

		// Convert partition sizes to hexadecimal strings
		String[] hexStrings = convertKbToHex(partitionSizes);

		// Set hexadecimal strings to the respective text fields
		for (int i = 0; i < hexStrings.length; i++) {
			JTextField partitionSizeHexField = getPartitionSizeHex(i);
			if (partitionSizeHexField != null) {
				partitionSizeHexField.setText(hexStrings[i]);
			} else {
				System.out.println("Partition size hex field at index " + i + " is not available.");
			}
		}
	}

	private void createPartitionsOffsets() {
		for (int i = 0; i < NUM_ITEMS; i++) {
			partitionsOffsets[i] = new JTextField("0");
			partitionsOffsets[i].setColumns(10);
			partitionsOffsets[i].setEditable(false);
			partitionsOffsets[i].setHorizontalAlignment(SwingConstants.RIGHT);
			final Font currFont = partitionsOffsets[i].getFont(); // use monospace font for hex/dec values
			partitionsOffsets[i].setFont(new Font(Font.MONOSPACED, currFont.getStyle(), currFont.getSize()));
			csv_PartitionOffsetsInnerPanel.add(partitionsOffsets[i]);

			// Set enabled property to false for items 6-NUM_ITEMS
			if (i >= 6 && i <= NUM_ITEMS) {
				partitionsOffsets[i].setEditable(false);
			}
		}
		// Offset for the first index is hard coded as 9000 hex
		partitionsOffsets[0].setText("9000");

		calculateOffsets(); // Calculate offsets after creating the fields
	}

	public void calculateOffsets() {
		partitionsOffsets[0].setText("9000"); // Offset for the first index is hard coded as 9000 hex

		lastIndex = -1; // Initialize to -1 indicating no selected index found yet

		// Find the last selected index
		for (int i = 0; i < NUM_ITEMS; i++) {
			if (getCheckBox(i).isSelected()) {
				lastIndex = i;
			} else {
				break; // Break the loop if a non-selected checkbox is found
			}
		}

		if (lastIndex != -1) { // If a selected index is found
			for (int i = 1; i <= lastIndex + 1; i++) { // Calculate offsets including the hardcoded first offset
				if (partitionsSizeHex[i - 1] != null && !partitionsSizeHex[i - 1].getText().isEmpty()) {
					String previousOffsetHex = partitionsOffsets[i - 1].getText();
					String sizeHex = partitionsSizeHex[i - 1].getText();

					// Convert the hexadecimal strings to long values
					long previousOffset = Long.parseLong(previousOffsetHex, 16);
					long size = Long.parseLong(sizeHex, 16);

					// Calculate the new offset
					long newOffset = previousOffset + size;

					// Convert the new offset back to hexadecimal and set it to the current offset
					// field
					partitionsOffsets[i].setText(Long.toHexString(newOffset));
				}
			}
		}
	}

	private void createPartitionFlashVisualPanel() {

		csv_partitionsCenterVisualPanel = new JPanel(new GridBagLayout());

		csv_PartitionsVisual.add(csv_partitionsCenterVisualPanel, BorderLayout.SOUTH);

		// Call the method to initially populate the center panel
		updatePartitionFlashVisual();
	}

	public void updatePartitionFlashVisual() {
		// Clear the center panel before updating
		csv_partitionsCenterVisualPanel.removeAll();

		int FLASH_SIZE = flashSizeMB * 1024;
		int RESERVED_SPACE = 36;

		int totalPartitionSize = 0;

		for (int i = 0; i < NUM_ITEMS; i++) {
			if (partitions_EnableChckb[i].isSelected()) {
				try {
					totalPartitionSize += Integer.parseInt(partitionsSize[i].getText());
				} catch (NumberFormatException e) {
				}
			}
		}

		int remainingSpace = FLASH_SIZE - RESERVED_SPACE - totalPartitionSize;

		JPanel initialPanel = new JPanel(new BorderLayout());
		JLabel initialLabel = new JLabel("0x9000");
		initialLabel.setHorizontalAlignment(SwingConstants.CENTER);
		initialPanel.add(initialLabel, BorderLayout.CENTER);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;

		initialPanel.setPreferredSize(new Dimension(50, 24));
		csv_partitionsCenterVisualPanel.add(initialPanel, gbc);

		if (remainingSpace > 0) {
			for (int i = 0; i < NUM_ITEMS; i++) {
				if (partitions_EnableChckb[i].isSelected()) {
					try {
						int partitionSize = Integer.parseInt(partitionsSize[i].getText());
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
			for (int i = 0; i < NUM_ITEMS; i++) {
				if (partitions_EnableChckb[i].isSelected()) {
					try {
						int partitionSize = Integer.parseInt(partitionsSize[i].getText());
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
		}

		// Revalidate and repaint the center panel to reflect changes
		csv_partitionsCenterVisualPanel.revalidate();
		csv_partitionsCenterVisualPanel.repaint();
	}

	public JCheckBox getDebug() {
		return ui_DebugChckb;
	}

	public JComboBox<?> getPartitionFlashType() {
		return partitionsFlashTypes;
	}

	public JLabel getFlashFreeLabel() {
		return csv_partitionFlashFreeSpace;
	}

	public long getFlashBytes() {
		return FlashSizeBytes;
	}

	public JComboBox<?> getFlashSize() {
		return partitions_FlashSizes;
	}

	public JButton getImportCSVButton() {
		return partitions_ImportCSVButton;
	}

	public JButton getCreatePartitionsCSV() {
		return partitions_CSVButton;
	}

	public JButton getCreatePartitionsBin() {
		return partitions_BinButton;
	}

	public JButton getFlashSPIFFSButton() {
		return btn_flashSPIFFS;
	}

	public JButton getFlashMergedBin() {
		return btn_flashMergedBin;
	}

	public JButton getHelpButton() {
		return btn_help;
	}

	public int getNumOfItems() {
		return NUM_ITEMS;
	}

	// Getter method to access a specific checkbox by index
	public JCheckBox getCheckBox(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return partitions_EnableChckb[index];
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionName(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return partitionsNames[index];
		}
		return null;
	}

	// Getter method to access a specific partitionType by index
	public JComboBox<?> getPartitionType(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return partitionsType[index];
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionSubType(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return partitionsSubType[index];
		}
		return null;
	}

	public JTextField getPartitionSize(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return partitionsSize[index];
		}
		return null;
	}

	public JTextField getPartitionSizeHex(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return partitionsSizeHex[index];
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionOffsets(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return partitionsOffsets[index];
		}
		return null;
	}

	public JTextField getSpiffsBlockSize() {
		return spiffs_blockSize;
	}

	public String getSpiffsOffset() {
		String result = "";
		int spiffsIndex = -1; // Initialize spiffsIndex to -1 to indicate not found

		// Iterate through the partition subtypes to find the SPIFFS partition
		for (int i = 0; i < NUM_ITEMS; i++) {
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
