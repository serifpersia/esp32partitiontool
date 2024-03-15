package com.serifpersia.esp32partitiontool;

import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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

	public JPanel FSGenPanel;
	public JLabel FSGenLabel;
	public JComboBox<?> partitionsFlashTypes;
	public JTextField FSBlockSize;
	public JButton uploadFSBtn;
	public JButton mergeBinBtn;


	public FSPanel() {
		setLayout(new BorderLayout(0, 0));
		setOpaque(false); // transparent background!
		createPanel();
	}


	public void updatePartitionFlashTypeLabel() {
		FSGenLabel.setText( getPartitionFlashTypes().getSelectedItem().toString() );
		uploadFSBtn.setText( "Upload " + getPartitionFlashTypes().getSelectedItem().toString() );
	}

	public JComboBox<?> getPartitionFlashTypes() {
		return partitionsFlashTypes;
	}

	public JTextField getFSBlockSize() {
		return FSBlockSize;
	}

	public JButton getUploadFSBtn() {
		return uploadFSBtn;
	}

	public JButton getMergeBinBtn() {
		return mergeBinBtn;
	}


	public void createPanel() {

		FSGenPanel = new JTransparentPanel();
		add(FSGenPanel/*, BorderLayout.EAST*/);
		FSGenPanel.setLayout(new BorderLayout(0, 0));
		FSGenPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		FSGenLabel = new JLabel("SPIFFS");
		FSGenLabel.setHorizontalAlignment(SwingConstants.CENTER);
		FSGenLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		FSGenPanel.add(FSGenLabel, BorderLayout.NORTH);

		JPanel FSGenInnerPanel = new JTransparentPanel();

		FSGenPanel.add(FSGenInnerPanel, BorderLayout.CENTER);
		FSGenInnerPanel.setLayout(new BorderLayout(0, 0));

		GridLayout gl = new GridLayout(0, 2);
		gl.setHgap(5);
		JPanel FSInnerPanel = new JTransparentPanel(); // 0 rows means any number of rows, 2 columns
		FSInnerPanel.setLayout(gl);
		FSGenInnerPanel.add(FSInnerPanel, BorderLayout.NORTH);

		JLabel LabelFs = new JLabel("Filesystem:");
		LabelFs.setHorizontalAlignment(SwingConstants.CENTER);
		FSInnerPanel.add(LabelFs);

		partitionsFlashTypes = new JComboBox<>(new String[] { "SPIFFS", "LittleFS", "FatFS" });
		FSInnerPanel.add(partitionsFlashTypes);

		JLabel LabelBlockSize = new JLabel("Block Size:");
		LabelBlockSize.setHorizontalAlignment(SwingConstants.CENTER);
		FSInnerPanel.add(LabelBlockSize);

		FSBlockSize = new JTextField("4096");
		FSBlockSize.setEditable(false);
		FSInnerPanel.add(FSBlockSize);

		JPanel FS_MERGE_FLASH_Panel = new JTransparentPanel();
		FSGenInnerPanel.add(FS_MERGE_FLASH_Panel, BorderLayout.CENTER);
		FS_MERGE_FLASH_Panel.setLayout(new BorderLayout(0, 0));

		JPanel panel2 = new JTransparentPanel();
		FS_MERGE_FLASH_Panel.add(panel2, BorderLayout.NORTH);
		panel2.setLayout(new BorderLayout(0, 0));

		uploadFSBtn = new JButton("SPIFFS");
		panel2.add(uploadFSBtn);

		JPanel panel3 = new JTransparentPanel();
		FS_MERGE_FLASH_Panel.add(panel3, BorderLayout.CENTER);
		panel3.setLayout(new BorderLayout(0, 0));
		panel3.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

		JLabel lblNewLabel3 = new JLabel("Merge");
		lblNewLabel3.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel3.setHorizontalAlignment(SwingConstants.CENTER);

		panel3.add(lblNewLabel3, BorderLayout.NORTH);

		JPanel panel4 = new JTransparentPanel();
		panel3.add(panel4, BorderLayout.CENTER);
		panel4.setLayout(new BorderLayout(0, 0));

		JPanel panel5 = new JTransparentPanel();
		panel4.add(panel5, BorderLayout.NORTH);
		panel5.setLayout(new GridLayout(2, 0, 0, 0));

		mergeBinBtn = new JButton("Merge Binary");
		panel5.add(mergeBinBtn);

	}


	private String lastFsName;

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
		getUploadFSBtn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileManager.uploadSPIFFS();
			}
		});
		getMergeBinBtn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileManager.createMergedBin();
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
