package com.serifpersia.esp32partitiontool;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

@SuppressWarnings("serial")

public class FSPanel extends JPanel {

	private String lastFsName;
	public JPanel FSGenPanel;
	public JLabel FSGenLabel;
	public JComboBox<?> FSTypesComboBox;
	public JTextField FSBlockSize;
	private JButton FSUploadButton;
	private JButton mergeBinButton;
	private JButton uploadMergedBinButton;
	private JButton cleanLogsButton;
	private JProgressBar progressBar;
	private JScrollPane consoleScrollPanel;

	private JPanel FSGenInnerPanel;
	private JPanel FSInnerPanel;
	private JLabel FSComboLabel;
	private JLabel blockSizeLabel;
	private JPanel FSMergeFlashPanel;
	private JPanel FSUploadPanel;
	private JPanel mergeBoxPanel;
	private JLabel mergeBoxLabel;
	private JPanel mergeButtonsPanel;
	private JPanel mergeButtonsWrapper;
	private JPanel buildWidgetsPanel;
	private JPanel consoleLogPanel;

	private GridBagConstraints consoleGBC;
	private GridLayout FSInnerLayout;

	public FSPanel() {
		setLayout(new BorderLayout(0, 0));
		setOpaque(false); // transparent background!
		createPanel();
	}

	public void updatePartitionFlashTypeLabel() {
		FSGenLabel.setText(getPartitionFlashTypes().getSelectedItem().toString());
		FSUploadButton.setText(l10n.getString("fsPanel.uploadButtonLabel") + " "
				+ getPartitionFlashTypes().getSelectedItem().toString());
	}

	public JComboBox<?> getPartitionFlashTypes() {
		return FSTypesComboBox;
	}

	public JTextField getFSBlockSize() {
		return FSBlockSize;
	}

	public JButton getUploadFSBtn() {
		return FSUploadButton;
	}

	public JButton getMergeBinBtn() {
		return mergeBinButton;
	}

	public JButton getUploadMergedBinBtn() {
		return uploadMergedBinButton;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JPanel getConsoleLogPanel() {
		return consoleLogPanel;
	}

	public JPanel wrapButton(JButton button) {
		JPanel wrapper = new UI.JTransparentPanel();
		wrapper.setLayout(new BorderLayout(0, 0));
		wrapper.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		wrapper.add(button);
		return wrapper;
	}

	public void initComponents() {
		consoleGBC = new GridBagConstraints();
		FSInnerLayout = new GridLayout(0, 2);
		FSGenLabel = new JLabel();
		mergeBoxLabel = new JLabel(l10n.getString("fsPanel.mergeBoxLabel"));
		FSComboLabel = new JLabel(l10n.getString("fsPanel.comboLabel") + ":");
		blockSizeLabel = new JLabel(l10n.getString("fsPanel.blockSizeLabel") + ":");
		FSGenInnerPanel = new UI.JTransparentPanel( /* Color.MAGENTA */ );
		FSInnerPanel = new UI.JTransparentPanel();
		FSGenPanel = new UI.JTransparentPanel();
		FSMergeFlashPanel = new UI.JTransparentPanel();
		FSUploadPanel = new UI.JTransparentPanel( /* Color.CYAN */ );
		mergeBoxPanel = new UI.JTransparentPanel( /* Color.ORANGE */ );
		mergeButtonsPanel = new UI.JTransparentPanel();
		mergeButtonsWrapper = new UI.JTransparentPanel();
		FSTypesComboBox = new JComboBox<>(new String[] { "SPIFFS", "LittleFS", "FatFS" });
		FSBlockSize = new JTextField("4096");
		FSUploadButton = new JButton(l10n.getString("fsPanel.uploadButtonLabel"));
		mergeBinButton = new JButton(l10n.getString("fsPanel.mergeBinButtonLabel"));
		uploadMergedBinButton = new JButton(l10n.getString("fsPanel.uploadMergedBinButtonLabel"));
		cleanLogsButton = new UI.JButtonIcon(l10n.getString("fsPanel.cleanLogsButtonLabel"), "/clear.png");
		consoleLogPanel = new JPanel();
		consoleScrollPanel = new JScrollPane(consoleLogPanel);
		progressBar = new JProgressBar(0, 100);
		buildWidgetsPanel = new JPanel() {
			@Override
			public boolean isOptimizedDrawingEnabled() {
				return false;
			}
		};
	}

	public void addComponents() {
		add(FSGenPanel);
		buildWidgetsPanel.add(cleanLogsButton);
		buildWidgetsPanel.add(progressBar);
		mergeButtonsWrapper.add(wrapButton(mergeBinButton));
		mergeButtonsWrapper.add(wrapButton(uploadMergedBinButton));
		mergeButtonsWrapper.add(buildWidgetsPanel);
		mergeButtonsPanel.add(consoleScrollPanel, BorderLayout.CENTER);
		mergeButtonsPanel.add(mergeButtonsWrapper, BorderLayout.NORTH);
		mergeBoxPanel.add(mergeBoxLabel, BorderLayout.NORTH);
		mergeBoxPanel.add(mergeButtonsPanel, BorderLayout.CENTER);
		FSUploadPanel.add(wrapButton(FSUploadButton));
		FSMergeFlashPanel.add(mergeBoxPanel, BorderLayout.CENTER);
		FSMergeFlashPanel.add(FSUploadPanel, BorderLayout.NORTH);
		FSInnerPanel.add(FSComboLabel);
		FSInnerPanel.add(FSTypesComboBox);
		FSInnerPanel.add(blockSizeLabel);
		FSInnerPanel.add(FSBlockSize);
		FSGenInnerPanel.add(FSInnerPanel, BorderLayout.NORTH);
		FSGenInnerPanel.add(FSMergeFlashPanel, BorderLayout.CENTER);
		FSGenPanel.add(FSGenLabel, BorderLayout.NORTH);
		FSGenPanel.add(FSGenInnerPanel, BorderLayout.CENTER);
	}

	public void createPanel() {
		initComponents();

		consoleGBC.gridx = 0;
		consoleGBC.gridy = GridBagConstraints.RELATIVE;
		consoleGBC.gridheight = 1024;
		consoleGBC.gridwidth = 1;
		consoleGBC.weightx = 1.0;
		consoleGBC.weighty = 1.0;
		consoleGBC.anchor = GridBagConstraints.NORTH;// GridBagConstraints.FIRST_LINE_START;
		consoleGBC.fill = GridBagConstraints.HORIZONTAL;

		FSGenPanel.setLayout(new BorderLayout(0, 0));
		FSGenPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));

		FSGenLabel.setHorizontalAlignment(SwingConstants.CENTER);
		FSGenLabel.setFont(UI.defaultFont.deriveFont(Font.PLAIN, 20));

		FSGenInnerPanel.setLayout(new BorderLayout(0, 0));

		FSInnerLayout.setHgap(5);
		FSInnerPanel.setLayout(FSInnerLayout);
		FSInnerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 2, 5));

		FSComboLabel.setHorizontalAlignment(SwingConstants.CENTER);
		FSComboLabel.setFont(UI.defaultFont.deriveFont(Font.PLAIN, 13));

		FSTypesComboBox.setFont(UI.defaultFont.deriveFont(Font.PLAIN, 13));

		blockSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		blockSizeLabel.setFont(UI.defaultFont.deriveFont(Font.PLAIN, 13));

		FSBlockSize.setEditable(false);

		FSMergeFlashPanel.setLayout(new BorderLayout(0, 0));

		FSUploadPanel.setLayout(new BorderLayout(0, 0));

		mergeBoxPanel.setLayout(new BorderLayout(0, 0));
		mergeBoxLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mergeBoxLabel.setFont(UI.defaultFont.deriveFont(Font.PLAIN, 20));

		mergeButtonsPanel.setLayout(new BorderLayout(0, 0));

		mergeButtonsWrapper.setLayout(new GridLayout(3, 0, 0, 0));

		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setVisible(false);
		progressBar.setAlignmentX(CENTER_ALIGNMENT);
		progressBar.setAlignmentY(0.5f);

		cleanLogsButton.setVisible(false);
		cleanLogsButton.setAlignmentX(RIGHT_ALIGNMENT);
		cleanLogsButton.setAlignmentY(0.5f);

		buildWidgetsPanel.setLayout(new OverlayLayout(buildWidgetsPanel));
		buildWidgetsPanel.setOpaque(false);
		buildWidgetsPanel.setMinimumSize(new Dimension(0, 16));
		buildWidgetsPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 2, 5));

		consoleLogPanel.setLayout(new GridBagLayout());
		consoleLogPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		consoleLogPanel.setForeground(Color.WHITE);
		consoleLogPanel.setBackground(Color.BLACK);
		consoleLogPanel.setFont(UI.monotypeFont.deriveFont(Font.PLAIN, 13));
		consoleLogPanel.setAlignmentY(JPanel.TOP_ALIGNMENT);

		// consoleScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		consoleScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		consoleScrollPanel.getVerticalScrollBar().setUnitIncrement(100);

		addComponents();
	}

	public void clearMessages() {
		cleanLogsButton.setVisible(false);
		consoleLogPanel.removeAll();
		consoleLogPanel.revalidate();
		consoleLogPanel.repaint();
	}

	public void emitMessage(String msg, boolean is_error) {
		msg = msg.trim();
		if (msg.isEmpty())
			return;
		JTextArea msgArea = new JTextArea(0, 0);
		msgArea.setForeground(is_error ? Color.RED : Color.WHITE);
		msgArea.setBackground(Color.BLACK);
		msgArea.setFont(UI.monotypeFont.deriveFont(Font.PLAIN, 13));
		msgArea.setEditable(false); // Make the text area read-only
		msgArea.setLineWrap(true); // Enable line wrapping
		msgArea.setWrapStyleWord(true); // Wrap at word boundaries
		msgArea.setText(msg);
		msgArea.setAlignmentY(JPanel.TOP_ALIGNMENT);
		consoleLogPanel.add(msgArea, consoleGBC);
		consoleLogPanel.revalidate();
		consoleLogPanel.repaint();
		msgArea.setCaretPosition(msgArea.getDocument().getLength());
		cleanLogsButton.setVisible(true);
	}

	public void attachListeners(UI ui, FileManager fileManager) {
		ui.getFlashSize().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int spiffs_setBlockSize = 0;

				if (ui.flashSizeMB == 4 || ui.flashSizeMB == 8 || ui.flashSizeMB == 16 || ui.flashSizeMB == 32) {
					spiffs_setBlockSize = ui.flashSizeMB * 1024;
				} else {
					// Handle other cases or provide a default value if necessary
				}
				String blockSizeText = String.valueOf(spiffs_setBlockSize);
				getFSBlockSize().setText(blockSizeText);
			}
		});

		cleanLogsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cleanLogsButton.setVisible(false);
				consoleLogPanel.removeAll();
				consoleLogPanel.revalidate();
				consoleLogPanel.repaint();
			}
		});

		final Runnable onBefore = () -> {
			getProgressBar().setVisible(true);
			getProgressBar().setIndeterminate(true);
			cleanLogsButton.setVisible(false);
		};

		final Runnable onAfter = () -> {
			getProgressBar().setIndeterminate(false);
			getProgressBar().setVisible(false);
			cleanLogsButton.setVisible(true);
		};

		final AppSettings.EventCallback DefaultEventCallbacks = new AppSettings.EventCallback(onBefore, onAfter, null,
				null);

		final Runnable onUploadSPIFFS = () -> fileManager.uploadSPIFFS(DefaultEventCallbacks);
		final Runnable onCreateMergedBin = () -> fileManager.createMergedBin(DefaultEventCallbacks);
		final Runnable onUploadMergedBin = () -> fileManager.uploadMergedBin(DefaultEventCallbacks);

		getUploadFSBtn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ui.settings.reload();
				fileManager.saveCSV(null);
				new Thread(onUploadSPIFFS).start();
			}
		});

		getMergeBinBtn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ui.settings.reload();
				fileManager.saveCSV(null);
				new Thread(onCreateMergedBin).start();
			}
		});

		getUploadMergedBinBtn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ui.settings.reload();
				fileManager.saveCSV(null);
				new Thread(onUploadMergedBin).start();
			}
		});

		getPartitionFlashTypes().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fsName = getPartitionFlashTypes().getSelectedItem().toString();
				String toolPath = ui.settings.get("mk" + fsName.toLowerCase() + ".path");
				if (toolPath == null) {
					fileManager.emitError("Tool for creating " + fsName + " spiffs.bin" + " not found!");
				} else {
					// selection changed
					if (!fsName.equals(lastFsName)) {
						lastFsName = fsName;
						updatePartitionFlashTypeLabel();
					}
				}
			}
		});
	}

}
