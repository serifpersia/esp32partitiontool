package com.serifpersia.esp32partitiontool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

	private JCheckBox[] enableCheckboxes = new JCheckBox[NUM_ITEMS];
	private JTextField[] partitionNames = new JTextField[NUM_ITEMS];
	private JComboBox<?>[] partitionType = new JComboBox[NUM_ITEMS];
	private JTextField[] partitionSubType = new JTextField[NUM_ITEMS];
	private JTextField[] partitionSize = new JTextField[NUM_ITEMS];
	private JTextField[] partitionOffsets = new JTextField[NUM_ITEMS];

	private JComboBox<?> partitions_FlashSizes;

	private JButton partitions_GenerateCSVButton;
	private JButton partitions_GenerateBinButton;
	private JButton SPIFFS_GenerateSPIFFSButton;

	private JTextField SPIFFS_BlockSizes;
	private JTextField SPIFFS_PageSizes;
	private JTextField SPIFFS_Offsets;

	public UI() {
		setLayout(new BorderLayout(0, 0));
		init();
	}

	private void init() {
		createPanels();
		createPartitionEnableToggles();
		createPartitionNames();
		createPartitionTypes();
		createPartitionSubTypes();
		createPartitionSize();
		createPartitionOffsets();
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
		csv_RootPanel.setLayout(new GridLayout(0, 6, 0, 0));

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

		JLabel csv_FlashSizeLabel = new JLabel("Flash Size");
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

	private void createPartitionEnableToggles() {
		for (int i = 0; i < NUM_ITEMS; i++) {
			enableCheckboxes[i] = new JCheckBox("Enable");
			// Set the first 6 checkboxes as selected (enabled) by default
			enableCheckboxes[i].setSelected(i < 6);
			enableCheckboxes[i].setHorizontalAlignment(SwingConstants.CENTER);
			csv_EnableInnerPanel.add(enableCheckboxes[i]);
		}
	}

	private void createPartitionNames() {
		String[] initialTexts = { "nvs", "otadata", "app0", "app1", "spiffs", "coredump" };

		for (int i = 0; i < NUM_ITEMS; i++) {
			final int id = i; // Capture the value of i
			partitionNames[i] = new JTextField(i < initialTexts.length ? initialTexts[i] : "");
			partitionNames[i].setHorizontalAlignment(SwingConstants.CENTER);
			partitionNames[i].setColumns(10);
			csv_PartitionNameInnerPanel.add(partitionNames[i]);

			partitionNames[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JTextField source = (JTextField) e.getSource();
					System.out.println("Text entered/edited: " + source.getText() + " ID: " + id);
				}
			});
			// Set enabled property to false for items 6-10
			if (i >= 6 && i <= 10) {
				partitionNames[i].setEnabled(false);
			}
		}
	}

	private void createPartitionTypes() {
		String[] defaultItems = { "data", "data", "app", "app", "data", "data", "data", "data", "data", "data" };

		for (int i = 0; i < NUM_ITEMS; i++) {
			partitionType[i] = new JComboBox<>(new String[] { "data", "app" });
			partitionType[i].setSelectedItem(defaultItems[i]);
			csv_PartitionTypeInnerPanel.add(partitionType[i]);

			// Set enabled property to false for items 6-10
			if (i >= 6 && i <= 10) {
				partitionType[i].setEnabled(false);
			}
		}
	}

	private void createPartitionSubTypes() {
		String[] initialTexts = { "nvs", "ota", "ota_0", "ota_1", "spiffs", "coredump" };

		for (int i = 0; i < NUM_ITEMS; i++) {
			partitionSubType[i] = new JTextField(i < initialTexts.length ? initialTexts[i] : "");
			partitionSubType[i].setHorizontalAlignment(SwingConstants.CENTER);
			partitionSubType[i].setColumns(10);
			csv_PartitionSubTypeInnerPanel.add(partitionSubType[i]);
			// Set enabled property to false for items 6-10
			if (i >= 6 && i <= 10) {
				partitionSubType[i].setEnabled(false);
			}
		}
	}

	private void createPartitionSize() {
		String[] initialTexts = { "20", "8", "1536", "1536", "896", "64" };

		for (int i = 0; i < NUM_ITEMS; i++) {
			partitionSize[i] = new JTextField(i < initialTexts.length ? initialTexts[i] : "");
			partitionSize[i].setHorizontalAlignment(SwingConstants.CENTER);
			partitionSize[i].setColumns(10);
			csv_PartitionSizeInnerPanel.add(partitionSize[i]);
			// Set enabled property to false for items 6-10
			if (i >= 6 && i <= 10) {
				partitionSize[i].setEnabled(false);
			}
		}
	}

	private void createPartitionOffsets() {
		String[] arrayF = calculateFAndGArrays(getPartitionSizeValues());

		for (int i = 0; i < NUM_ITEMS && i < arrayF.length; i++) {
			partitionOffsets[i] = new JTextField();
			partitionOffsets[i].setHorizontalAlignment(SwingConstants.CENTER);
			partitionOffsets[i].setColumns(10);
			partitionOffsets[i].setEditable(false);
			partitionOffsets[i].setText(arrayF[i]); // Set text to the corresponding value in arrayF
			csv_PartitionOffsetsInnerPanel.add(partitionOffsets[i]);

			// Set enabled property to false for items 6-10
			if (i >= 6 && i <= 10) {
				partitionOffsets[i].setEnabled(false);
			}
		}
	}

	private String[] getPartitionSizeValues() {
		String[] partitionSizeValues = new String[NUM_ITEMS];
		for (int i = 0; i < NUM_ITEMS; i++) {
			partitionSizeValues[i] = partitionSize[i].getText();
		}
		return partitionSizeValues;
	}

	private String[] calculateFAndGArrays(String[] arrayE) {
		String[] arrayF = new String[arrayE.length];
		String[] arrayG = new String[arrayE.length];

		arrayF[0] = "9000";
		arrayG[0] = "5000";

		for (int i = 1; i < arrayE.length; i++) {
			arrayF[i] = calculateF(arrayF[i - 1], arrayG[i - 1]);
			arrayG[i] = calculateG(arrayE[i], arrayE[i]);
		}

		return arrayF;
	}

	private String calculateF(String previousF, String previousG) {
		long f = hexToDec(previousF);
		long g = hexToDec(previousG);
		return decToHex(f + g);
	}

	private String calculateG(String currentA, String currentE) {
		long result = 0;
		if (!currentA.isEmpty() && !currentE.isEmpty()) {
			long a = Long.parseLong(currentA);
			long e = Long.parseLong(currentE);
			if (a > 0) {
				result = e * 1024;
			}
		}
		return decToHex(result);
	}

	private long hexToDec(String hex) {
		return Long.parseLong(hex, 16);
	}

	private String decToHex(long dec) {
		return Long.toHexString(dec);
	}

	public JButton getGenCSVButton() {
		return partitions_GenerateCSVButton;
	}

	public int getNumEnableCheckboxes() {
		return NUM_ITEMS;
	}

	// Getter method to access a specific checkbox by index
	public JCheckBox getCheckBox(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return enableCheckboxes[index];
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionName(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return partitionNames[index];
		}
		return null;
	}

	// Getter method to access a specific partitionType by index
	public JComboBox<?> getPartitionType(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return partitionType[index];
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionSubType(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return partitionSubType[index];
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionSize(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return partitionSize[index];
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionOffsets(int index) {
		if (index >= 0 && index < NUM_ITEMS) {
			return partitionOffsets[index];
		}
		return null;
	}
}
