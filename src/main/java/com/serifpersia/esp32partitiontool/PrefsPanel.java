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

	private JCheckBox enableDebugCheckBox;      // used in pref panel
	private JCheckBox confirmOverwriteCheckBox; // used in pref panel
	private JCheckBox confirmDataEmptyCheckBox; // used in pref panel

	Dimension titleSize    = new Dimension(150, 40);
	Dimension checkBoxSize = new Dimension(30,  40);
	Dimension descSize     = new Dimension(400, 40);


	public PrefsPanel() {
		createPanel();
	}


	private void createPanel() {

		setLayout(new BorderLayout(0, 0));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		confirmDataEmptyCheckBox = new JCheckBox();
		confirmOverwriteCheckBox = new JCheckBox();
		enableDebugCheckBox      = new JCheckBox();

		JPanel settingsPanel = getSettingsPanel();

		Box box = new Box(BoxLayout.Y_AXIS);
		box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		//box.setBorder(BorderFactory.createEtchedBorder());
		box.add( Box.createVerticalGlue() );
		box.add( settingsPanel ); // insert panel
		box.add( Box.createVerticalGlue() );

		add(box);

		Dimension prefsPanelSize = new Dimension(650, 400);
		setPreferredSize( prefsPanelSize );
		setMaximumSize( prefsPanelSize );
	}


	private JPanel getSettingsPanel() {
		JPanel settingsPanel = new JPanel();
		JPanel myBorderCheckBox0 = getCheckBoxPropertyPanel( enableDebugCheckBox,
			"Enable debug", "Dramatically increases the application verbosity. Be ready to have your console flooded with a lot of information :-)" );
		JPanel myBorderCheckBox1 = getCheckBoxPropertyPanel( confirmOverwriteCheckBox,
			"Auto overwrite", "Automatically overwrite partitions.csv without asking for confirmation." );
		JPanel myBorderCheckBox2 = getCheckBoxPropertyPanel( confirmDataEmptyCheckBox,
			"Warn if empty SPIFFS", "Will ask for confirmation if a 'data' folder is missing from the sketch folder before creating spiffs.bin.");

		settingsPanel.setBorder( BorderFactory.createEmptyBorder(20, 0,0,0) );
		settingsPanel.setLayout( new BoxLayout(settingsPanel, BoxLayout.Y_AXIS) );
		settingsPanel.add( myBorderCheckBox0 );
		settingsPanel.add( getSeparator() );
		settingsPanel.add( myBorderCheckBox1 );
		settingsPanel.add( getSeparator() );
		settingsPanel.add( myBorderCheckBox2 );

		return settingsPanel;
	}



	private JPanel getCheckBoxPropertyPanel( JCheckBox checkBox, String title, String description ) {
		JPanel mainFrame       = new JPanel();
		JLabel checkBoxTitle   = new JLabel("<html>" + title + "</html>");
		JPanel checkBoxPanel   = new JPanel();
		JLabel checkBoxDesc    = new JLabel("<html>" + description + "</html>");

		checkBox.setToolTipText( description );

		checkBoxTitle.setLayout( new BoxLayout(checkBoxTitle, BoxLayout.Y_AXIS) );
		checkBoxTitle.setMaximumSize(   titleSize );
		checkBoxTitle.setPreferredSize( titleSize );
		checkBoxTitle.setHorizontalAlignment(SwingConstants.RIGHT);
		checkBoxTitle.setVerticalAlignment(JLabel.TOP);
		checkBoxTitle.setVerticalTextPosition(JLabel.TOP);

		checkBoxPanel.setLayout( new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS) );
		checkBoxPanel.setMaximumSize(   checkBoxSize );
		checkBoxPanel.setPreferredSize( checkBoxSize );
		checkBoxPanel.setAlignmentX(JLabel.CENTER);
		checkBoxPanel.setAlignmentY(JLabel.TOP);
		checkBoxPanel.add(checkBox);

		checkBoxDesc.setLayout( new BoxLayout(checkBoxDesc, BoxLayout.Y_AXIS) );
		checkBoxDesc.setMaximumSize(   descSize );
		checkBoxDesc.setPreferredSize( descSize );
		checkBoxDesc.setHorizontalAlignment(SwingConstants.LEFT);
		checkBoxDesc.setVerticalAlignment(JLabel.TOP);
		checkBoxDesc.setVerticalTextPosition(JLabel.TOP);

		mainFrame.setLayout(new FlowLayout(SwingConstants.NORTH));
		mainFrame.add(wrap(checkBoxTitle));
		mainFrame.add(wrap(checkBoxPanel));
		mainFrame.add(wrap(checkBoxDesc));
		return mainFrame;
	}



	private JPanel wrap( Component src ) {
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		// wrapper.setBorder(BorderFactory.createEtchedBorder());
		wrapper.add( src );
		return wrapper;
	}


	private JPanel getSeparator() {
		JPanel wrapped = wrap( new JSeparator( SwingConstants.HORIZONTAL ) );
		wrapped.setBorder( BorderFactory.createEmptyBorder(0, 50, 0, 50));
		return wrapped;
	}

	public JCheckBox getDebug() { return enableDebugCheckBox; }
	public JCheckBox getConfirmDataEmptyCheckBox() { return confirmDataEmptyCheckBox; }
	public JCheckBox getOverwriteCheckBox() { return confirmOverwriteCheckBox; }

}
