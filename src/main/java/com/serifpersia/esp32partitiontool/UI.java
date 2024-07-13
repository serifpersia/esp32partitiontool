package com.serifpersia.esp32partitiontool;

import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")

public class UI extends JPanel {

	public static final class JTransparentPanel extends JPanel {
		public JTransparentPanel(Color color) {
			if (color.getAlpha() >= 0xff) {
				// force alpha 0.5
				color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0x80);
			}
			setBackground(color);
		}

		public JTransparentPanel() {
			setOpaque(false);
		}
	}

	public static final class JButtonIcon extends JButton {
		public JButtonIcon(String title, String iconPath) {
			try {
				ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
				setIcon(icon);
				setBorder(null);
				// setRolloverIcon(icon);
				// setBorderPainted(false);
				// setContentAreaFilled(false);
				// setFocusPainted(false);
				setOpaque(false);
				setPreferredSize(new Dimension(36, 26));
				setToolTipText(" " + title + " ");
			} catch (Exception ex) {
				setText(" " + title + " ");
			}
		}
	}

	private static final long serialVersionUID = 1L;
	public static final int MIN_ITEMS = 15;
	public int lastIndex;

	long FlashSizeBytes = 4 * 1024 * 1024 - 36864;
	int flashSizeMB = 4;

	private JFrame frame;
	private String frameTitle;

	JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame, String title) {
		this.frame = frame;
		this.frameTitle = title;
		setFrameTitleNeedsSaving(false);
	}

	public void setFrameTitleNeedsSaving(boolean needs_saving) {
		if (needs_saving) {
			// System.out.println("Document needs saving!");
			frame.setTitle("(*) " + frameTitle);
			settings.clean();
		} else {
			frame.setTitle(frameTitle);
		}
	}

	private UIController controller;
	public AppSettings settings;

	public HelpPanel helpPanel;
	public AboutPanel aboutPanel;
	public FSPanel fsPanel;

	private JScrollPane csvScrollPanel;

	private JPanel csvGenPanel;
	private JPanel csvPanel;
	private JPanel csvBottomPanel;
	private JPanel partitionsUtilButtonsPanel;
	private JPanel csvpartitionsCenterVisualPanel;
	private JPanel flashSizeFieldSetPanel;
	private JPanel actionButtonsPanel;
	private JLabel partitionFlashFreeSpace;
	private JLabel flashSizeLabel;
	private JLabel csvGenLabel;
	private JPanel tableWrapperPanel;

	private JComboBox<?> flashSizesComboBox;
	private JButton aboutBtn;
	private JButton importCsvBtn;
	private JButton saveCsvBtn;
	private JButton exporCsvBtn;
	private JButton helpButton;

	static Font defaultFont;
	static Font condensedFont;
	static Font monotypeFont;
	static Font monotypeBoldFont;

	private ArrayList<CSVRow> csvRows = new ArrayList<CSVRow>();

	public UI(JFrame frame, String title) {

		// Show tool tips immediately
		ToolTipManager.sharedInstance().setInitialDelay(0);

		defaultFont = new Font("Monospaced", Font.PLAIN, 12);
		monotypeFont = new Font("Monospaced", Font.PLAIN, 12);
		monotypeBoldFont = new Font("Monospaced", Font.BOLD, 12);
		condensedFont = new Font("Monospaced", Font.PLAIN, 12);

		new l10n();

		setFrame(frame, title);
		setLayout(new BorderLayout(0, 0));
		setOpaque(false); // transparent background!
		init();
	}

	private void init() {
		createPanels();
		calculateOffsets();
		createPartitionFlashVisualPanel();
		updatePartitionFlashVisual();
	}

	public void reload() {
		settings.reload();
		updateFrame();
	}

	public void updateFrame() {
		if (!settings.platformSupported) {
			System.out.println("Hiding app");
			frame.setVisible(false);
			return;
		}

		if (settings.hasFSPanel) {
			frame.setSize(1024, 640);
		} else {
			frame.setSize(800, 567);
		}
		frame.setLocationRelativeTo(null);
		fsPanel.setVisible(settings.hasFSPanel);
	}

	public void setController(UIController controller) {
		this.controller = controller;
	}

	public void setAppSettings(FileManager fileManager, AppSettings settings) {
		this.settings = settings;
		add(fsPanel, BorderLayout.EAST);
		fsPanel.attachListeners(this, fileManager);
		updateFrame();
	}

	public void addCSVRow(CSVRow line) {
		if (line == null) {
			line = new CSVRow(null);
		}

		line.size.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				recalculate();
			}

			public void removeUpdate(DocumentEvent e) {
				recalculate();
			}

			public void insertUpdate(DocumentEvent e) {
				recalculate();
			}

			public void recalculate() {
				calculateSizeHex();
				calculateOffsets();
				updatePartitionFlashVisual();
			}
		});

		line.subtype.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				validate();
			}

			public void removeUpdate(DocumentEvent e) {
				validate();
			}

			public void insertUpdate(DocumentEvent e) {
				validate();
			}

			public void validate() {
				validateSubtypes();
			}
		});

		line.attachListeners(getController());
		csvRows.add(line);
	}

	public void popCSVRow() {
		int lastIndex = csvRows.size() - 1;
		if (lastIndex >= 0) {
			CSVRow csvRow = getCSVRow(lastIndex);
			csvPanel.remove(csvRow);
			csvRows.remove(lastIndex);
		}
	}

	public void removeCVSLine(int index) {
		int lastIndex = csvRows.size() - 1;
		if (lastIndex >= 0 && lastIndex >= index) {
			CSVRow csvRow = getCSVRow(lastIndex);
			csvPanel.remove(csvRow);
			csvRows.remove(index);
		}
	}

	public CSVRow getCSVRow(int index) {
		if (index >= 0 && csvRows.size() > index) {
			return csvRows.get(index);
		}
		return null;
	}

	public void renderCSVRows() {
		csvPanel.removeAll();
		int layoutSize = csvRows.size() + 2 < MIN_ITEMS + 1 ? MIN_ITEMS + 1 : csvRows.size() + 2;
		csvPanel.setLayout(new GridLayout(layoutSize - 1, 0, 0, 0));
		while (csvRows.size() < layoutSize - 1) {
			addCSVRow(null);
		}
		for (int i = 0; i < csvRows.size(); i++) {
			if (getCSVRow(i) != null)
				csvPanel.add(getCSVRow(i), BorderLayout.CENTER);
		}

		Component it = csvPanel;
		while (it != frame) {
			it.revalidate();
			it.repaint();
			it = it.getParent();
		}
	}

	public JPanel getTitleCSVRow() {
		JPanel partitionTitlePanel = new JTransparentPanel();
		partitionTitlePanel.setLayout(new BorderLayout());

		String labels[] = { l10n.getString("columnTitle.enable"), l10n.getString("columnTitle.name"),
				l10n.getString("columnTitle.type"), l10n.getString("columnTitle.subtype"),
				l10n.getString("columnTitle.sizekb"), l10n.getString("columnTitle.sizehex"),
				l10n.getString("columnTitle.offset") };

		JLabel enableLabel = new JLabel(labels[0]);
		enableLabel.setFont(condensedFont.deriveFont(Font.BOLD, 13));
		enableLabel.setPreferredSize(new Dimension(50, 12));
		enableLabel.setHorizontalAlignment(SwingConstants.CENTER);

		partitionTitlePanel.add(enableLabel, BorderLayout.WEST);

		JPanel labelsPanel = new JTransparentPanel();
		labelsPanel.setLayout(new GridLayout(1, labels.length - 1, 0, 0));
		for (int i = 1; i < labels.length; i++) {
			JLabel titles = new JLabel(labels[i]);
			titles.setFont(condensedFont.deriveFont(Font.BOLD, 13));
			titles.setHorizontalAlignment(SwingConstants.CENTER);
			labelsPanel.add(titles);
		}

		partitionTitlePanel.add(labelsPanel, BorderLayout.CENTER);

		return partitionTitlePanel;
	}

	private void createPanels() {

		helpPanel = new HelpPanel();
		aboutPanel = new AboutPanel() {
			// disallow parent scroll bar pane to use horizontal scrollbar
			public boolean getScrollableTracksViewportWidth() {
				return true;
			}
		};
		fsPanel = new FSPanel();

		fsPanel.setVisible(false);

		csvGenPanel = new JTransparentPanel();

		add(csvGenPanel);
		csvGenPanel.setLayout(new BorderLayout(0, 0));
		csvGenPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));

		csvGenLabel = new JLabel(l10n.getString("csvGenLabel.defaultLabel"));
		csvGenLabel.setOpaque(false);
		csvGenLabel.setFont(defaultFont.deriveFont(Font.PLAIN, 20));
		csvGenLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csvGenPanel.add(csvGenLabel, BorderLayout.NORTH);

		csvPanel = new JTransparentPanel(/* Color.BLUE */ );
		csvPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		csvScrollPanel = new JScrollPane(csvPanel);
		csvScrollPanel.setViewportBorder(null);
		csvScrollPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		csvScrollPanel.getViewport().setOpaque(false);
		csvScrollPanel.getViewport().setBorder(null);
		csvScrollPanel.getViewport().getInsets().set(0, 0, 0, 0);
		csvScrollPanel.getVerticalScrollBar().setUnitIncrement(100); // prevent the scroll wheel from going sloth

		tableWrapperPanel = new JTransparentPanel( /* Color.BLUE */ );
		tableWrapperPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		tableWrapperPanel.setLayout(new BorderLayout(0, 0));

		// column titles have fixed position
		tableWrapperPanel.add(getTitleCSVRow(), BorderLayout.NORTH);
		// only rows are scrollable
		tableWrapperPanel.add(csvScrollPanel);

		csvBottomPanel = new JTransparentPanel(/* Color.GREEN */);
		csvBottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		csvBottomPanel.setLayout(new BorderLayout(0, 0));

		csvGenPanel.add(tableWrapperPanel, BorderLayout.CENTER);
		csvGenPanel.add(csvBottomPanel, BorderLayout.SOUTH);

		partitionsUtilButtonsPanel = new JTransparentPanel();
		partitionsUtilButtonsPanel.setLayout(new BorderLayout(0, 0));
		csvBottomPanel.add(partitionsUtilButtonsPanel);

		actionButtonsPanel = new JTransparentPanel();
		actionButtonsPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
		actionButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		actionButtonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		importCsvBtn = new JButtonIcon(l10n.getString("importCsvButton.label"), "/import.png");
		actionButtonsPanel.add(importCsvBtn);
		saveCsvBtn = new JButtonIcon(l10n.getString("saveCsvButton.label"), "/save.png");
		actionButtonsPanel.add(saveCsvBtn);
		exporCsvBtn = new JButtonIcon(l10n.getString("exportCsvButton.label"), "/export2.png");
		actionButtonsPanel.add(exporCsvBtn);
		helpButton = new JButtonIcon(l10n.getString("helpButton.label"), "/help.png");
		actionButtonsPanel.add(helpButton);
		aboutBtn = new JButtonIcon(l10n.getString("aboutButton.label"), "/about.png");
		actionButtonsPanel.add(aboutBtn);
		csvBottomPanel.add(actionButtonsPanel, BorderLayout.WEST);

		flashSizeFieldSetPanel = new JTransparentPanel();
		flashSizeLabel = new JLabel(l10n.getString("flashSize.label") + " (MB):");
		flashSizeLabel.setFont(defaultFont.deriveFont(Font.PLAIN, 13));
		flashSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		flashSizeFieldSetPanel.add(flashSizeLabel);
		flashSizesComboBox = new JComboBox<>(new String[] { "4", "8", "16", "32" });
		flashSizesComboBox.setFont(UI.defaultFont.deriveFont(Font.PLAIN, 13));
		flashSizeFieldSetPanel.add(flashSizesComboBox);
		partitionsUtilButtonsPanel.add(flashSizeFieldSetPanel, BorderLayout.CENTER);

		// free space box
		partitionFlashFreeSpace = new JLabel(l10n.getString("flash.freeSpace") + ": ...");
		partitionFlashFreeSpace.setFont(defaultFont.deriveFont(Font.PLAIN, 13));
		partitionsUtilButtonsPanel.add(partitionFlashFreeSpace, BorderLayout.EAST);

	}

	private void createPartitionFlashVisualPanel() {
		csvpartitionsCenterVisualPanel = new JTransparentPanel();
		csvpartitionsCenterVisualPanel.setLayout(new GridBagLayout());
		csvBottomPanel.add(csvpartitionsCenterVisualPanel, BorderLayout.SOUTH);
	}

	public void updatePartitionLabel(String file) {
		csvGenLabel.setText(FileManager.basename(file));
		csvGenLabel.setToolTipText(file);
	}

	public void calculateSizeHex() {
		FlashSizeBytes = flashSizeMB * 1024 * 1024 - 0x9000;
		long[] partitionSizes = new long[getNumOfItems()];

		// Iterate through all text fields to calculate the total size
		for (int i = 0; i < getNumOfItems(); i++) {
			CSVRow csvRow = getCSVRow(i);
			if (csvRow == null) {
				System.err.println("csv line # " + i + " not found, skipping");
				continue;
			}
			if (!csvRow.enabled.isSelected())
				continue;
			String sizeText = csvRow.size.getText();
			if (!sizeText.isEmpty()) {
				try {
					long partitionTotalSize = Long.parseLong(sizeText) * 1024;
					FlashSizeBytes -= partitionTotalSize;
					// Store partition size in kilobytes
					partitionSizes[i] = partitionTotalSize / 1024;
				} catch (NumberFormatException e) {
					// Handle parsing errors if necessary
					System.out.println("Invalid input in text field " + i + ": " + sizeText);
				}
			}
		}

		// Update the free space label
		getFlashFreeLabel().setText(l10n.getString("flash.freeSpace") + ": " + FlashSizeBytes / 1024 + " KB");
		getFlashFreeLabel().setForeground(FlashSizeBytes >= 0 ? Color.BLACK : Color.RED);

		// Convert partition sizes to hexadecimal strings
		String[] hexStrings = convertKbToHex(partitionSizes);

		// Set hexadecimal strings to the respective text fields
		for (int i = 0; i < hexStrings.length; i++) {
			CSVRow csvRow = getCSVRow(i);
			if (csvRow == null) {
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
			long previousOffset = 0, size = 0;
			for (int i = 1; i <= lastIndex + 1; i++) { // Calculate offsets including the hardcoded first offset
				CSVRow prevLine = getCSVRow(i - 1);
				CSVRow currLine = getCSVRow(i);
				if (currLine != null && prevLine != null && !prevLine.sizeHex.getText().isEmpty()) {
					String previousOffsetHex = prevLine.offset.getText();
					String sizeHex = prevLine.sizeHex.getText();

					try {
						// Convert the hexadecimal strings to long values
						previousOffset = Long.parseLong(previousOffsetHex, 16);
						size = Long.parseLong(sizeHex, 16);
					} catch (NumberFormatException e) {
						size = 0; // cell was either disabled or contains invalid value, safe to ignore
					}

					// Calculate the new offset
					long newOffset = previousOffset + size;

					if (newOffset % 0x1000 != 0) { // uh-oh, not a multiple of 4096!!
						// System.err.println( newOffset + " is not a multiple of 4096 (0x1000 )");
						currLine.size.setForeground(Color.RED);
						prevLine.size.setForeground(Color.RED);
					} else {
						currLine.size.setForeground(Color.BLACK);
						prevLine.size.setForeground(Color.BLACK);
					}
					// Convert the new offset back to hexadecimal and set it to the current offset
					// field
					currLine.offset.setText(Long.toHexString(newOffset).toUpperCase());
				}
			}
		}
	}

	public void validateSubtypes() {
		//
		for (int i = 0; i < csvRows.size(); i++) {
			CSVRow csvRow = getCSVRow(i);
			if (!csvRow.enabled.isSelected())
				continue;
			String subtype = csvRow.subtype.getText();
			csvRow.subtype.setForeground(csvRow.isValidSubtype(subtype) ? Color.BLACK : Color.RED);
			if (settings.hasFSPanel) {
				String type = csvRow.type.getSelectedItem().toString().toLowerCase();
				if (type.equals("data") && subtype.equals("fat"))
					fsPanel.getPartitionFlashTypes().setSelectedItem("FatFS");
				else if (type.equals("data") && subtype.equals("spiffs"))
					fsPanel.getPartitionFlashTypes().setSelectedItem("SPIFFS");
				else if (type.equals("data") && subtype.equals("littlefs"))
					fsPanel.getPartitionFlashTypes().setSelectedItem("LittleFS");
			}
		}
	}

	Color partColor;
	JTextArea console_logField;

	private Color getPartitionColor(String partName, String partType, String partSubType) {
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
		return partColor;
	}

	public void updatePartitionFlashVisual() {
		// Clear the center panel before updating
		csvpartitionsCenterVisualPanel.removeAll();

		int FLASH_SIZE = flashSizeMB * 1024;
		int RESERVED_SPACE = 36;
		int totalPartitionSize = 0;

		for (int i = 0; i < getNumOfItems(); i++) {
			CSVRow csvRow = getCSVRow(i);
			if (csvRow == null) {
				System.err.println("csv line # " + i + " not found, skipping");
				continue;
			}
			if (!csvRow.enabled.isSelected())
				continue;
			try {
				totalPartitionSize += Integer.parseInt(csvRow.size.getText());
			} catch (NumberFormatException e) {
			}
		}

		int remainingSpace = FLASH_SIZE - RESERVED_SPACE - totalPartitionSize;

		JPanel initialPanel = new JTransparentPanel();
		initialPanel.setLayout(new BorderLayout());
		JLabel initialLabel = new JLabel("0x9000 ");
		initialLabel.setFont(monotypeBoldFont.deriveFont(Font.PLAIN, 12));
		GridBagConstraints gbc = new GridBagConstraints();

		initialLabel.setHorizontalAlignment(SwingConstants.CENTER);
		initialPanel.add(initialLabel, BorderLayout.CENTER);
		initialPanel.setPreferredSize(new Dimension(50, 24));

		gbc.fill = GridBagConstraints.HORIZONTAL;

		csvpartitionsCenterVisualPanel.add(initialPanel, gbc);

		if (remainingSpace > 0) {
			for (int i = 0; i < getNumOfItems(); i++) {
				CSVRow csvRow = getCSVRow(i);
				if (csvRow == null) {
					System.err.println("csv line # " + i + " not found, skipping");
					continue;
				}
				if (!csvRow.enabled.isSelected())
					continue;
				try {
					int partitionSize = Integer.parseInt(csvRow.size.getText());
					double weight = (double) partitionSize / (FLASH_SIZE - RESERVED_SPACE);
					JPanel partitionPanel = new JPanel();

					String partName = getPartitionName(i).getText();
					String partType = (String) getPartitionType(i).getSelectedItem();
					String partSubType = getPartitionSubType(i).getText();
					String partSizeKb = getPartitionSize(i).getText();

					partColor = getPartitionColor(partName, partType, partSubType);

					partitionPanel.setBackground(partColor);

					// Set the text color to white
					JLabel label = new JLabel(getPartitionSubType(i).getText());
					label.setFont(monotypeBoldFont.deriveFont(Font.PLAIN, 12));
					label.setForeground(Color.WHITE);
					partitionPanel.add(label);
					partitionPanel.setToolTipText(partSizeKb + "KB");
					partitionPanel.setBorder(BorderFactory.createEtchedBorder());
					gbc.weightx = weight;
					csvpartitionsCenterVisualPanel.add(partitionPanel, gbc);
				} catch (NumberFormatException e) {
				}
			}

			JPanel unusedSpacePanel = new JTransparentPanel();
			unusedSpacePanel.setBorder(BorderFactory.createEtchedBorder());
			unusedSpacePanel.setBackground(new Color(130, 135, 145));
			gbc.weightx = (double) remainingSpace / (FLASH_SIZE - RESERVED_SPACE);
			// Set the text color to white
			JLabel label = new JLabel(l10n.getString("flash.freeSpace"));
			label.setForeground(Color.BLACK);
			label.setFont(monotypeBoldFont.deriveFont(Font.PLAIN, 12));
			unusedSpacePanel.add(label);

			csvpartitionsCenterVisualPanel.add(unusedSpacePanel, gbc);
		} else {
			for (int i = 0; i < getNumOfItems(); i++) {
				CSVRow csvRow = getCSVRow(i);
				if (csvRow == null) {
					System.err.println("csv line # " + i + " not found, skipping");
					continue;
				}
				if (!csvRow.enabled.isSelected())
					continue;
				try {
					int partitionSize = Integer.parseInt(csvRow.size.getText());
					double weight = (double) partitionSize / (FLASH_SIZE - RESERVED_SPACE);
					JPanel partitionPanel = new JPanel();

					String partName = getPartitionName(i).getText();
					String partType = (String) getPartitionType(i).getSelectedItem();
					String partSubType = getPartitionSubType(i).getText();
					String partSizeKb = getPartitionSize(i).getText();

					partColor = getPartitionColor(partName, partType, partSubType);

					partitionPanel.setBackground(partColor);
					// Set the text color to white
					JLabel label = new JLabel(partSubType);
					label.setFont(monotypeBoldFont.deriveFont(Font.PLAIN, 12));
					label.setForeground(Color.WHITE);
					partitionPanel.add(label);
					partitionPanel.setToolTipText(partSizeKb + "KB");

					partitionPanel.setBorder(BorderFactory.createEtchedBorder());

					gbc.weightx = weight;
					csvpartitionsCenterVisualPanel.add(partitionPanel, gbc);
				} catch (NumberFormatException e) {
				}

			}
		}
		// Revalidate and repaint the center panel to reflect changes
		csvpartitionsCenterVisualPanel.revalidate();
		csvpartitionsCenterVisualPanel.repaint();
	}

	public ArrayList<CSVRow> getCSVRows() {
		return csvRows;
	}

	public long getFlashBytes() {
		return FlashSizeBytes;
	}

	public int getNumOfItems() {
		return csvRows.size();
	}

	public String getPartitionLabel() {
		return csvGenLabel.getText();
	}

	public JButton getImportCSVButton() {
		return importCsvBtn;
	}

	public JButton getExporCsvBtn() {
		return exporCsvBtn;
	}

	public JButton getHelpButton() {
		return helpButton;
	}

	public JButton getAboutButton() {
		return aboutBtn;
	}

	public JComboBox<?> getFlashSize() {
		return flashSizesComboBox;
	}

	public JLabel getFlashFreeLabel() {
		return partitionFlashFreeSpace;
	}

	public UIController getController() {
		return controller;
	}

	// Getter method to access a specific checkbox by index
	public JCheckBox getCheckBox(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow(index);
			if (csvRow == null) {
				return null;
			}
			return csvRow.enabled;
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionName(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow(index);
			if (csvRow == null) {
				return null;
			}
			return csvRow.name;
		}
		return null;
	}

	// Getter method to access a specific partitionType by index
	public JComboBox<?> getPartitionType(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow(index);
			if (csvRow == null) {
				return null;
			}
			return csvRow.type;
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionSubType(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow(index);
			if (csvRow == null) {
				return null;
			}
			return csvRow.subtype;
		}
		return null;
	}

	// Getter method to access a specific partitionSize by index
	public JTextField getPartitionSize(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow(index);
			if (csvRow == null) {
				return null;
			}
			return csvRow.size;
		}
		return null;
	}

	// Getter method to access a specific partitionSizeHex by index
	public JTextField getPartitionSizeHex(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow(index);
			if (csvRow == null) {
				return null;
			}
			return csvRow.sizeHex;
		}
		return null;
	}

	// Getter method to access a specific partitionName by index
	public JTextField getPartitionOffsets(int index) {
		if (index >= 0 && index < getNumOfItems()) {
			CSVRow csvRow = getCSVRow(index);
			if (csvRow == null) {
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
			if (!getCSVRow(i).enabled.isSelected())
				continue;
			JTextField partitionSubType = getPartitionSubType(i);
			if (partitionSubType != null && (partitionSubType.getText().equals("spiffs")
					|| partitionSubType.getText().equals("littlefs") || partitionSubType.getText().equals("fatfs")
					|| partitionSubType.getText().equals("ffat"))) {
				spiffsIndex = i;
				break; // Exit the loop once SPIFFS partition subtype is found
			}
		}
		if (spiffsIndex != -1) {
			// Retrieve the partition offset using the SPIFFS partition subtype index
			String partitionOffset = getPartitionOffsets(spiffsIndex).getText();
			result = "0x" + partitionOffset;
		} else {
			result = l10n.getString("spiffs.partitionNotFound");
		}
		return result;
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

}
