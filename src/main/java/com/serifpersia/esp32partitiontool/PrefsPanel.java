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
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Component;
import java.awt.event.WindowEvent;
import javax.swing.*;
import java.io.*;

import javax.swing.event.*;


class PrefsPanel extends JPanel {

	private JButton cancelButton;
	private JButton saveButton;
	private JCheckBox enableDebugCheckBox;      // used in pref panel
	private JCheckBox confirmOverwriteCheckBox; // used in pref panel
	private JCheckBox confirmDataEmptyCheckBox; // used in pref panel
	private JButton helpButton;


	public PrefsPanel() {
    createPanel();
	}


	private void createPanel() {

		setLayout(new BorderLayout(0, 0));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		cancelButton             = new JButton("Cancel");
		saveButton               = new JButton("Save");
		helpButton               = new JButton("Help");

		confirmDataEmptyCheckBox = new JCheckBox();
		confirmOverwriteCheckBox = new JCheckBox();
		enableDebugCheckBox      = new JCheckBox();

		saveButton.setPreferredSize(cancelButton.getPreferredSize());
		saveButton.setMinimumSize(cancelButton.getMinimumSize());

		helpButton.setPreferredSize(cancelButton.getPreferredSize());
		helpButton.setMinimumSize(cancelButton.getMinimumSize());

		add( getGeneralSettingsPanel(), BorderLayout.CENTER);
	}


	private JPanel wrap( Component src ) {
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		wrapper.add( src );
		return wrapper;
	}


	private JPanel getGeneralSettingsPanel() {

		JPanel mainFrame = new JPanel();
		JLabel mainLabel = new JLabel("Settings");

		mainLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		mainLabel.setHorizontalAlignment(SwingConstants.CENTER);

		mainFrame.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridheight = 1; // common to all inserted components

		gbc.gridx = gbc.gridy = 0; // top left
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(10, 15, 0, 0);
		mainFrame.add(mainLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = gbc.weighty = 1.; // stretch w/h
		gbc.insets = new Insets(10, 15, 0, 10);
		mainFrame.add(getSettingsPanel(), gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = GridBagConstraints.RELATIVE;
		gbc.anchor = GridBagConstraints.BASELINE_LEADING;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.; // no stretch
		gbc.weighty = 0.; // no stretch
		gbc.insets = new Insets(10, 15, 0, 15);
		mainFrame.add(helpButton, gbc);

		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.BASELINE_TRAILING;
		gbc.weightx = 1.;
		gbc.insets = new Insets(10, 3, 0, 0);
		mainFrame.add(saveButton, gbc);

		gbc.gridx = 3;
		gbc.weightx = 0.;
		gbc.insets = new Insets(10, 3, 0, 10);
		mainFrame.add(cancelButton, gbc);

		return mainFrame;

	}



	private JPanel getCheckBoxPropertyPanel( JCheckBox checkBox, String title, String description ) {
		JPanel mainFrame = new JPanel();

		checkBox.setToolTipText( description );

		mainFrame.setLayout(new FlowLayout(SwingConstants.NORTH));

		Dimension titleSize    = new Dimension(200, 40);
		Dimension descSize     = new Dimension(400, 40);
		Dimension checkBoxSize = new Dimension(30,  40);

		JLabel checkBoxLabel = new JLabel("<html>" + title + "</html>");
		checkBoxLabel.setMinimumSize( titleSize );
		checkBoxLabel.setPreferredSize( titleSize );
		checkBoxLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		JLabel checkBoxDesc = new JLabel("<html>" + description + "</html>");
		checkBoxDesc.setMinimumSize( descSize );
		checkBoxDesc.setPreferredSize( descSize );
		checkBoxDesc.setHorizontalAlignment(SwingConstants.LEFT);

		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout( new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS) );
		checkBoxPanel.setMinimumSize( checkBoxSize );
		checkBoxPanel.setPreferredSize( checkBoxSize );
		checkBoxPanel.add(checkBox);

		mainFrame.add(wrap(checkBoxLabel), BorderLayout.LINE_START);
		mainFrame.add(wrap(checkBoxPanel));
		mainFrame.add(wrap(checkBoxDesc));
	  return mainFrame;
	}



	private JPanel getSettingsPanel() {
		JPanel settingsPanel = new JPanel();

		JPanel myBorderCheckBox0 = getCheckBoxPropertyPanel( enableDebugCheckBox,      "Enable debug", "Dramatically elevates the application verbosity. Be ready to have your console flooded with a lot of information :-)" );
		JPanel myBorderCheckBox1 = getCheckBoxPropertyPanel( confirmOverwriteCheckBox, "Auto overwrite", "Automatically overwrite partitions.csv without asking for confirmation." );
		JPanel myBorderCheckBox2 = getCheckBoxPropertyPanel( confirmDataEmptyCheckBox, "Warn if empty SPIFFS", "Will ask for confirmation if a 'data' folder is missing from the sketch folder before creating spiffs.bin.");

		settingsPanel.setBorder( BorderFactory.createEmptyBorder() );
		settingsPanel.setLayout( new BoxLayout(settingsPanel, BoxLayout.Y_AXIS) );
		settingsPanel.add( new JSeparator(SwingConstants.HORIZONTAL) );
		settingsPanel.add( myBorderCheckBox0 );
		settingsPanel.add( new JSeparator(SwingConstants.HORIZONTAL) );
		settingsPanel.add( myBorderCheckBox1 );
		settingsPanel.add( new JSeparator(SwingConstants.HORIZONTAL) );
		settingsPanel.add( myBorderCheckBox2 );
		settingsPanel.add( new JSeparator(SwingConstants.HORIZONTAL) );

		return settingsPanel;
	}


	public JButton   getCancelButton() { return cancelButton; }
	public JButton   getSaveButton() { return saveButton; }
	public JButton   getHelpButton() { return helpButton; }
	public JCheckBox getDebug() { return enableDebugCheckBox; }
	public JCheckBox getConfirmDataEmptyCheckBox() { return confirmDataEmptyCheckBox; }
	public JCheckBox getOverwriteCheckBox() { return confirmOverwriteCheckBox; }


}
