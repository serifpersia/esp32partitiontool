package com.serifpersia.esp32partitiontool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class UI extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int NUM_ITEMS = 10;

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
	private JPanel partitions_FlashPartitionsVisualsPanel;

	private JPanel SPIFFS_GenPanel;
	private JPanel SPIFFS_GenInnerPanel;
	private JPanel SPIFFS_BlockSizePanel;
	private JPanel SPIFFS_PageSizePanel;
	private JPanel SPIFFS_OffsetsPanel;
	private JPanel SPIFFS_OutputPanel;

	private JCheckBox[] partitions_EnableChckb = new JCheckBox[NUM_ITEMS];
	private JTextField[] partitionsNames = new JTextField[NUM_ITEMS];
	private JComboBox<?>[] partitionsType = new JComboBox[NUM_ITEMS];
	private JTextField[] partitionsSubType = new JTextField[NUM_ITEMS];
	private JTextField[] partitionsSize = new JTextField[NUM_ITEMS];
	private JTextField[] partitionsSizeHex = new JTextField[NUM_ITEMS];
	private JTextField[] partitionsOffsets = new JTextField[NUM_ITEMS];

	private JComboBox<?> partitions_FlashSizes;
	int FlashSizeBytes = 4 * 1024 * 1024 - 36864;
	int flashSizeMB = 4;
	// long FlashSizeBytes = 0;

	private JButton partitions_GenerateCSVButton;
	private JButton partitions_GenerateBinButton;
	private JButton SPIFFS_GenerateSPIFFSButton;

	private JTextField SPIFFS_BlockSizes;
	private JTextField SPIFFS_PageSizes;
	private JTextField SPIFFS_Offsets;
	private JPanel csv_PartitionSizeHexPanel;
	private JLabel csv_PartitionSizeHexLabel;
	private JPanel csv_PartitionSizeHexInnerPanel;
	private JLabel csv_partitionFlashFreeSpace;

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
		createPartitionsSizeHex();
		createPartitionsOffsets();
	}

	private void createPanels() {
		csv_GenPanel = new JPanel();
		add(csv_GenPanel);
		csv_GenPanel.setLayout(new BorderLayout(0, 0));

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
		csv_EnableInnerPanel.setLayout(new GridLayout(10, 0, 0, 0));

		csv_PartitionNamePanel = new JPanel();
		csv_RootPanel.add(csv_PartitionNamePanel);
		csv_PartitionNamePanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_PartitionNameLabel = new JLabel("Name");
		csv_PartitionNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionNamePanel.add(csv_PartitionNameLabel, BorderLayout.NORTH);

		csv_PartitionNameInnerPanel = new JPanel();
		csv_PartitionNamePanel.add(csv_PartitionNameInnerPanel, BorderLayout.CENTER);
		csv_PartitionNameInnerPanel.setLayout(new GridLayout(10, 0, 0, 0));

		csv_PartitionTypePanel = new JPanel();
		csv_RootPanel.add(csv_PartitionTypePanel);
		csv_PartitionTypePanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_PartitionTypeLabel = new JLabel("Type");
		csv_PartitionTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionTypePanel.add(csv_PartitionTypeLabel, BorderLayout.NORTH);

		csv_PartitionTypeInnerPanel = new JPanel();
		csv_PartitionTypePanel.add(csv_PartitionTypeInnerPanel, BorderLayout.CENTER);
		csv_PartitionTypeInnerPanel.setLayout(new GridLayout(10, 0, 0, 0));

		csv_PartitionSubTypePanel = new JPanel();
		csv_RootPanel.add(csv_PartitionSubTypePanel);
		csv_PartitionSubTypePanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_PartitionSubTypeLabel = new JLabel("SubType");
		csv_PartitionSubTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionSubTypePanel.add(csv_PartitionSubTypeLabel, BorderLayout.NORTH);

		csv_PartitionSubTypeInnerPanel = new JPanel();
		csv_PartitionSubTypePanel.add(csv_PartitionSubTypeInnerPanel, BorderLayout.CENTER);
		csv_PartitionSubTypeInnerPanel.setLayout(new GridLayout(10, 0, 0, 0));

		csv_PartitionSizePanel = new JPanel();
		csv_RootPanel.add(csv_PartitionSizePanel);
		csv_PartitionSizePanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_PartitionSizeLabel = new JLabel("Size(kB)");
		csv_PartitionSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionSizePanel.add(csv_PartitionSizeLabel, BorderLayout.NORTH);

		csv_PartitionSizeInnerPanel = new JPanel();
		csv_PartitionSizePanel.add(csv_PartitionSizeInnerPanel, BorderLayout.CENTER);
		csv_PartitionSizeInnerPanel.setLayout(new GridLayout(10, 0, 0, 0));

		csv_PartitionSizeHexPanel = new JPanel();
		csv_RootPanel.add(csv_PartitionSizeHexPanel);
		csv_PartitionSizeHexPanel.setLayout(new BorderLayout(0, 0));

		csv_PartitionSizeHexLabel = new JLabel("Size(hex)");
		csv_PartitionSizeHexLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionSizeHexPanel.add(csv_PartitionSizeHexLabel, BorderLayout.NORTH);

		csv_PartitionSizeHexInnerPanel = new JPanel();
		csv_PartitionSizeHexPanel.add(csv_PartitionSizeHexInnerPanel, BorderLayout.CENTER);
		csv_PartitionSizeHexInnerPanel.setLayout(new GridLayout(10, 0, 0, 0));

		csv_PartitionOffsetsPanel = new JPanel();
		csv_RootPanel.add(csv_PartitionOffsetsPanel);
		csv_PartitionOffsetsPanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_PartitionOffsetsLabel = new JLabel("Offset(hex)");
		csv_PartitionOffsetsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionOffsetsPanel.add(csv_PartitionOffsetsLabel, BorderLayout.NORTH);

		csv_PartitionOffsetsInnerPanel = new JPanel();
		csv_PartitionOffsetsPanel.add(csv_PartitionOffsetsInnerPanel, BorderLayout.CENTER);
		csv_PartitionOffsetsInnerPanel.setLayout(new GridLayout(10, 0, 0, 0));

		csv_PartitionsVisual = new JPanel();
		csv_GenPanel.add(csv_PartitionsVisual, BorderLayout.SOUTH);
		csv_PartitionsVisual.setLayout(new BorderLayout(0, 0));

		partitions_FlashPartitionsVisualsPanel = new JPanel();
		csv_PartitionsVisual.add(partitions_FlashPartitionsVisualsPanel, BorderLayout.SOUTH);

		JLabel tempVisualsLabel = new JLabel("Visuals TO DO:");
		tempVisualsLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		partitions_FlashPartitionsVisualsPanel.add(tempVisualsLabel);

		partitions_UtilButtonsPanel = new JPanel();
		csv_PartitionsVisual.add(partitions_UtilButtonsPanel, BorderLayout.NORTH);

		JLabel csv_FlashSizeLabel = new JLabel("Flash Size:");
		csv_FlashSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		partitions_UtilButtonsPanel.add(csv_FlashSizeLabel);

		partitions_FlashSizes = new JComboBox<>(new String[] { "4", "8", "16" });
		partitions_UtilButtonsPanel.add(partitions_FlashSizes);

		partitions_GenerateCSVButton = new JButton("Generate CSV");
		partitions_UtilButtonsPanel.add(partitions_GenerateCSVButton);

		partitions_GenerateBinButton = new JButton("Generate Bin");
		partitions_UtilButtonsPanel.add(partitions_GenerateBinButton);

		SPIFFS_GenPanel = new JPanel();
		add(SPIFFS_GenPanel, BorderLayout.EAST);
		SPIFFS_GenPanel.setLayout(new BorderLayout(0, 0));

		JLabel SPIFFS_GenLabel = new JLabel("SPIFFS");
		SPIFFS_GenLabel.setHorizontalAlignment(SwingConstants.CENTER);
		SPIFFS_GenLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		SPIFFS_GenPanel.add(SPIFFS_GenLabel, BorderLayout.NORTH);

		SPIFFS_GenInnerPanel = new JPanel();

		SPIFFS_GenPanel.add(SPIFFS_GenInnerPanel, BorderLayout.CENTER);
		SPIFFS_GenInnerPanel.setLayout(new GridLayout(4, 0, 0, 0));

		SPIFFS_BlockSizePanel = new JPanel();
		FlowLayout fl_SPIFFS_BlockSizePanel = (FlowLayout) SPIFFS_BlockSizePanel.getLayout();
		fl_SPIFFS_BlockSizePanel.setVgap(30);
		SPIFFS_GenInnerPanel.add(SPIFFS_BlockSizePanel);

		JLabel SPIFFS_BlockSizeInnerLabel = new JLabel("Block Size");
		SPIFFS_BlockSizeInnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		SPIFFS_BlockSizePanel.add(SPIFFS_BlockSizeInnerLabel);

		SPIFFS_BlockSizes = new JTextField();
		SPIFFS_BlockSizes.setHorizontalAlignment(SwingConstants.CENTER);
		SPIFFS_BlockSizePanel.add(SPIFFS_BlockSizes);
		SPIFFS_BlockSizes.setColumns(5);
		SPIFFS_BlockSizes.setPreferredSize(new Dimension(SPIFFS_BlockSizes.getPreferredSize().width, 30));

		SPIFFS_PageSizePanel = new JPanel();
		FlowLayout fl_SPIFFS_PageSizePanel = (FlowLayout) SPIFFS_PageSizePanel.getLayout();
		fl_SPIFFS_PageSizePanel.setVgap(30);
		SPIFFS_GenInnerPanel.add(SPIFFS_PageSizePanel);

		JLabel SPIFFS_PageSizeInnerLabel = new JLabel("Page Size");
		SPIFFS_PageSizeInnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		SPIFFS_PageSizePanel.add(SPIFFS_PageSizeInnerLabel);

		SPIFFS_PageSizes = new JTextField();
		SPIFFS_PageSizes.setHorizontalAlignment(SwingConstants.CENTER);
		SPIFFS_PageSizes.setColumns(5);
		SPIFFS_PageSizes.setPreferredSize(new Dimension(SPIFFS_PageSizes.getPreferredSize().width, 30));
		SPIFFS_PageSizePanel.add(SPIFFS_PageSizes);

		SPIFFS_OffsetsPanel = new JPanel();
		FlowLayout fl_SPIFFS_OffsetsPanel = (FlowLayout) SPIFFS_OffsetsPanel.getLayout();
		fl_SPIFFS_OffsetsPanel.setVgap(30);
		SPIFFS_GenInnerPanel.add(SPIFFS_OffsetsPanel);

		JLabel SPIFFS_OffsetsInnerLabel = new JLabel("Offset(x)");
		SPIFFS_OffsetsInnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		SPIFFS_OffsetsPanel.add(SPIFFS_OffsetsInnerLabel);

		SPIFFS_Offsets = new JTextField();
		SPIFFS_Offsets.setHorizontalAlignment(SwingConstants.CENTER);
		SPIFFS_Offsets.setColumns(5);
		SPIFFS_Offsets.setPreferredSize(new Dimension(SPIFFS_Offsets.getPreferredSize().width, 30));
		SPIFFS_OffsetsPanel.add(SPIFFS_Offsets);

		SPIFFS_OutputPanel = new JPanel();
		FlowLayout fl_SPIFFS_OutputPanel = (FlowLayout) SPIFFS_OutputPanel.getLayout();
		fl_SPIFFS_OutputPanel.setVgap(30);
		SPIFFS_GenInnerPanel.add(SPIFFS_OutputPanel);

		SPIFFS_GenerateSPIFFSButton = new JButton("Generate SPIFFS");
		SPIFFS_OutputPanel.add(SPIFFS_GenerateSPIFFSButton);
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
			// Set enabled property to false for items 6-10
			if (i >= 6 && i <= 10) {
				partitionsNames[i].setEnabled(false);
			}
		}
	}

	private void createPartitionsTypes() {
		String[] defaultItems = { "data", "data", "app", "app", "data", "data", "data", "data", "data", "data" };

		for (int i = 0; i < NUM_ITEMS; i++) {
			partitionsType[i] = new JComboBox<>(new String[] { "data", "app" });
			partitionsType[i].setSelectedItem(defaultItems[i]);
			csv_PartitionTypeInnerPanel.add(partitionsType[i]);

			// Set enabled property to false for items 6-10
			if (i >= 6 && i <= 10) {
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
			// Set enabled property to false for items 6-10
			if (i >= 6 && i <= 10) {
				partitionsSubType[i].setEnabled(false);
			}
		}
	}

	private void createPartitionsSize() {
		String[] initialPartitionSizeArray = { "20", "8", "1280", "1280", "1408", "64" };
		for (int i = 0; i < NUM_ITEMS; i++) {
			String value = (i < initialPartitionSizeArray.length) ? initialPartitionSizeArray[i] : "";
			partitionsSize[i] = new JTextField(value);
			partitionsSize[i].setHorizontalAlignment(SwingConstants.CENTER);
			partitionsSize[i].setColumns(10);
			csv_PartitionSizeInnerPanel.add(partitionsSize[i]);
			// Set enabled property to false for items 6-10
			if (i >= 6 && i <= 10) {
				partitionsSize[i].setEnabled(false);
			}
		}
		// Iterate through all text fields to calculate the total size
		for (int i = 0; i < getNumOfItems(); i++) {
			if (!getPartitionSize(i).getText().isEmpty()) {
				try {
					int partitionRoundedSize = Integer.parseInt(getPartitionSize(i).getText()) * 1024;
					FlashSizeBytes -= partitionRoundedSize;
					partitionRoundedSize = (partitionRoundedSize + 4095) / 4096 * 4096;

				} catch (NumberFormatException e) {
					// Handle parsing errors if necessary
					System.out.println("Invalid input in text field " + i);
				}
			}
		}
		csv_partitionFlashFreeSpace = new JLabel("Free Space: " + FlashSizeBytes + " bytes");
		partitions_UtilButtonsPanel.add(csv_partitionFlashFreeSpace);
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
			partitionsSizeHex[i].setHorizontalAlignment(SwingConstants.CENTER);
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
					// Round up to the nearest multiple of 4096
					int partitionRoundedSize = (partitionTotalSize + 4095) / 4096 * 4096;

					FlashSizeBytes -= partitionRoundedSize;

					// Set the rounded value back to the text field
					getPartitionSize(i).setText(Integer.toString(partitionRoundedSize / 1024)); // Convert back to
																								// kilobytes

					// Store partition size in kilobytes
					partitionSizes[i] = partitionRoundedSize / 1024;
				} catch (NumberFormatException e) {
					// Handle parsing errors if necessary
					System.out.println("Invalid input in text field " + i);
				}
			}
		}

		// Update the free space label
		getFlashFreeLabel().setText("Free Space: " + FlashSizeBytes + " bytes");

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
			partitionsOffsets[i].setHorizontalAlignment(SwingConstants.CENTER);
			partitionsOffsets[i].setColumns(10);
			partitionsOffsets[i].setEditable(false);
			csv_PartitionOffsetsInnerPanel.add(partitionsOffsets[i]);

			// Set enabled property to false for items 6-10
			if (i >= 6 && i <= 10) {
				partitionsOffsets[i].setEnabled(false);
			}
		}
		// Offset for the first index is hard coded as 9000 hex
		partitionsOffsets[0].setText("9000");

		calculateOffsets(); // Calculate offsets after creating the fields
	}

	public void calculateOffsets() {
		partitionsOffsets[0].setText("9000"); // Offset for the first index is hard coded as 9000 hex

		for (int i = 1; i < NUM_ITEMS; i++) {
			if (partitionsSizeHex[i - 1] != null && partitionsSizeHex[i - 1].getText() != null
					&& !partitionsSizeHex[i - 1].getText().isEmpty()) {
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

	public JLabel getFlashFreeLabel() {
		return csv_partitionFlashFreeSpace;
	}

	public long getFlashBytes() {
		return FlashSizeBytes;
	}

	public JComboBox<?> getFlashSize() {
		return partitions_FlashSizes;
	}

	public JButton getGenCSVButton() {
		return partitions_GenerateCSVButton;
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
}
