package com.serifpersia.esp32partitiontool;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")


public class FSPanel extends JPanel {

	final class JTransparentPanel extends JPanel {
		public JTransparentPanel() {
			setOpaque(false);
		}
	}

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
		FSGenLabel.setText( getPartitionFlashTypes().getSelectedItem().toString() );
		FSUploadButton.setText( "Upload " + getPartitionFlashTypes().getSelectedItem().toString() );
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

	public void initComponents() {
		consoleGBC = new GridBagConstraints();
		FSInnerLayout = new GridLayout(0, 2);
		FSGenLabel = new JLabel("SPIFFS");
		mergeBoxLabel = new JLabel("Merge");
		FSComboLabel = new JLabel("Filesystem:");
		blockSizeLabel = new JLabel("Block Size:");
		FSGenInnerPanel = new JTransparentPanel();
		FSInnerPanel = new JTransparentPanel();
		FSGenPanel = new JTransparentPanel();
		FSMergeFlashPanel = new JTransparentPanel();
		FSUploadPanel = new JTransparentPanel();
		mergeBoxPanel = new JTransparentPanel();
		mergeButtonsPanel = new JTransparentPanel();
		mergeButtonsWrapper = new JTransparentPanel();
		FSTypesComboBox = new JComboBox<>(new String[] { "SPIFFS", "LittleFS", "FatFS" });
		FSBlockSize = new JTextField("4096");
		FSUploadButton = new JButton("SPIFFS");
		mergeBinButton = new JButton("Merge Binary");
		uploadMergedBinButton = new JButton("Merge Binary & Upload");
		cleanLogsButton = new JButton();
		consoleScrollPanel = new JScrollPane(consoleLogPanel);
		progressBar = new JProgressBar(0, 100);
		consoleLogPanel = new JPanel();
		buildWidgetsPanel = new JPanel(){
			@Override
			public boolean isOptimizedDrawingEnabled() {
				return false;
			}
		};
	}

	public void addComponents() {
		add(FSGenPanel);
		FSUploadPanel.add(FSUploadButton);
		buildWidgetsPanel.add( cleanLogsButton );
		buildWidgetsPanel.add( progressBar );
		mergeButtonsWrapper.add(mergeBinButton);
		mergeButtonsWrapper.add(uploadMergedBinButton);
		mergeButtonsWrapper.add(buildWidgetsPanel);
		mergeButtonsPanel.add(consoleScrollPanel, BorderLayout.CENTER);
		mergeButtonsPanel.add(mergeButtonsWrapper, BorderLayout.NORTH);
		mergeBoxPanel.add(mergeBoxLabel, BorderLayout.NORTH);
		mergeBoxPanel.add(mergeButtonsPanel, BorderLayout.CENTER);
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
		consoleGBC.anchor = GridBagConstraints.NORTH;//GridBagConstraints.FIRST_LINE_START;
		consoleGBC.fill = GridBagConstraints.HORIZONTAL;

		FSGenPanel.setLayout(new BorderLayout(0, 0));
		FSGenPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		FSGenLabel.setHorizontalAlignment(SwingConstants.CENTER);
		FSGenLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));

		FSGenInnerPanel.setLayout(new BorderLayout(0, 0));

		FSInnerLayout.setHgap(5);
		FSInnerPanel.setLayout(FSInnerLayout);

		FSComboLabel.setHorizontalAlignment(SwingConstants.CENTER);

		blockSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);

		FSBlockSize.setEditable(false);

		FSMergeFlashPanel.setLayout(new BorderLayout(0, 0));

		FSUploadPanel.setLayout(new BorderLayout(0, 0));

		mergeBoxPanel.setLayout(new BorderLayout(0, 0));
		mergeBoxPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

		mergeBoxLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		mergeBoxLabel.setHorizontalAlignment(SwingConstants.CENTER);

		mergeButtonsPanel.setLayout(new BorderLayout(0, 0));

		mergeButtonsWrapper.setLayout(new GridLayout(3, 0, 0, 0));

		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setVisible(false);
		progressBar.setAlignmentX(CENTER_ALIGNMENT);
		progressBar.setAlignmentY(0.5f);

		try {
			ImageIcon broomIcon = new ImageIcon(getClass().getResource("/mini-broom.png"));
			cleanLogsButton.setIcon(broomIcon);
			cleanLogsButton.setRolloverIcon(broomIcon);
			cleanLogsButton.setMaximumSize( new Dimension( 16, 16 ) );
		} catch (Exception ex) {
			cleanLogsButton.setText("Clear logs");
		}
		cleanLogsButton.setVisible(false);
		cleanLogsButton.setBorderPainted(false);
		cleanLogsButton.setAlignmentX(RIGHT_ALIGNMENT);
		cleanLogsButton.setAlignmentY(0.5f);

		buildWidgetsPanel.setLayout(new OverlayLayout(buildWidgetsPanel));
		buildWidgetsPanel.setOpaque(false);
		buildWidgetsPanel.setMinimumSize( new Dimension(0, 16) );

		consoleLogPanel.setLayout(new GridBagLayout());
		consoleLogPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		consoleLogPanel.setForeground(Color.WHITE);
		consoleLogPanel.setBackground(Color.BLACK);
		consoleLogPanel.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Set a monospaced font
		consoleLogPanel.setAlignmentY(JPanel.TOP_ALIGNMENT);

		consoleScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		addComponents();
	}

	public void clearMessages() {
		cleanLogsButton.setVisible(false);
		consoleLogPanel.removeAll();
		consoleLogPanel.revalidate();
		consoleLogPanel.repaint();
	}

	public void emitMessage(String msg, boolean is_error ) {
			msg = msg.trim();
			if( msg.isEmpty() ) return;
			JTextArea msgArea = new JTextArea(0,0);
			msgArea.setForeground(is_error ? Color.RED : Color.WHITE);
			msgArea.setBackground(Color.BLACK);
			msgArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Set a monospaced font
			msgArea.setEditable(false); // Make the text area read-only
			msgArea.setLineWrap(true); // Enable line wrapping
			msgArea.setWrapStyleWord(true); // Wrap at word boundaries
			msgArea.setText( msg +"\n" );
			msgArea.setAlignmentY(JPanel.TOP_ALIGNMENT);
			consoleLogPanel.add( msgArea, consoleGBC );
			consoleLogPanel.revalidate();
			consoleLogPanel.repaint();
			msgArea.setCaretPosition(msgArea.getDocument().getLength());
			cleanLogsButton.setVisible( true );
	}

	public void attachListeners( UI ui, FileManager fileManager ) {
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

		getUploadFSBtn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						fileManager.uploadSPIFFS();
					}
				});
			}
		});

		getMergeBinBtn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						fileManager.createMergedBin(null);
					}
				});
			}
		});

		getUploadMergedBinBtn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						fileManager.uploadMergedBin();
					}
				});
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
