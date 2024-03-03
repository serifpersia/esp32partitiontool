package com.serifpersia.esp32partitiontool;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;


import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;


public class CSVRow extends JPanel {

	public JCheckBox enabled;
	public JTextField name;
	public JComboBox<?> type;
	public JTextField subtype;
	public JFormattedTextField size;
	public JTextField sizeHex;
	public JTextField offset;

	public CSVRow( String values[] ) {

		setLayout(new GridLayout(0, 7, 0, 0));

		enabled   = new JCheckBox("Enable");
		name      = new JTextField();
		type      = new JComboBox<>(new String[] { "data", "app" });
		subtype   = new JTextField();
		//size      = new JTextField();
		sizeHex   = new JTextField();
		offset    = new JTextField();


		// restrict the 'size' field to numbers, with the help of NumberFormat
    NumberFormat format = NumberFormat.getInstance();
    format.setGroupingUsed(false); // no comma separator
    NumberFormatter formatter = new NumberFormatter(format);
    formatter.setValueClass(Integer.class);
    formatter.setMinimum(0);
    formatter.setMaximum(Integer.MAX_VALUE);
    formatter.setAllowsInvalid(false);
    formatter.setCommitsOnValidEdit(true); // true = value committed on each keystroke instead of focus loss
    size = new JFormattedTextField(formatter);


		final Font currFont = name.getFont();

		type.setRenderer(new DefaultListCellRenderer(){{ setHorizontalAlignment(DefaultListCellRenderer.CENTER); }});

		// size.addKeyListener(new KeyAdapter() {
		// 		public void keyPressed(KeyEvent ke) {
		// 			String value = size.getText();
		// 			int l = value.length();
		// 			if (ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') {
		// 					size.setEditable(true);
		// 					//label.setText("");
		// 			} else {
		// 					size.setEditable(false);
		// 					//label.setText("* Enter only numeric digits(0-9)");
		// 			}
		// 		}
		// });

		enabled.setHorizontalAlignment(SwingConstants.CENTER);
		name.   setHorizontalAlignment(SwingConstants.CENTER);
		subtype.setHorizontalAlignment(SwingConstants.CENTER);
		size.   setHorizontalAlignment(SwingConstants.RIGHT);
		sizeHex.setHorizontalAlignment(SwingConstants.RIGHT);
		offset. setHorizontalAlignment(SwingConstants.RIGHT);

		size.   setFont(new Font(Font.MONOSPACED, currFont.getStyle(), currFont.getSize()));
		sizeHex.setFont(new Font(Font.MONOSPACED, currFont.getStyle(), currFont.getSize()));
		offset. setFont(new Font(Font.MONOSPACED, currFont.getStyle(), currFont.getSize()));

		sizeHex.setEditable(false);
		offset. setEditable(false);

		sizeHex.setBackground(Color.GRAY);
		offset. setBackground(Color.GRAY);

		if( values != null && values.length>5 ) {
			this.setRowName(    values[0] );
			this.setRowType(    values[1] );
			this.setRowSubtype( values[2] );
			this.setRowSize(    values[3] );
			this.setRowSizeHex( values[4] );
			this.setRowOffset(  values[5] );
			this.setRowEnabled( true );
		} else {
			enabled.setSelected( false );
			name.   setEditable( false );
			type.   setEnabled(  false );
			subtype.setEditable( false );
			size.   setEditable( false );
		}

		Component[] components = { enabled, name, type, subtype, size, sizeHex, offset };
		for( int i=0; i<components.length; i++ ) {
		  add( wrap( components[i] ), BorderLayout.CENTER );
		}

	}


	public String toString() {
		return name.getText() + ", " + (String)type.getSelectedItem() + ", "
			+ subtype.getText() + ", " + "0x" + offset.getText() + ", " + "0x"
			+ size.getText() + ", "
		;
	}


	public void setDefaults( int rowid ) {
		// Set text if isSelected is true
		String[] defaultPartitionNameText    = { "nvs", "otadata", "app0", "app1", "spiffs", "coredump" };
		int[]    defaultPartitionTypeText    = { 0, 0, 1, 1, 0, 0 };
		String[] defaultPartitionSubTypeText = { "nvs", "ota", "ota_0", "ota_1", "spiffs", "coredump", };
		String[] defaultPartitionSizeText    = { "20", "8", "1280", "1280", "1408", "64" };

		if (rowid >=0 && rowid < defaultPartitionNameText.length) {
			name.   setText(defaultPartitionNameText[rowid]);
			type.   setSelectedIndex(defaultPartitionTypeText[rowid%type.getItemCount()]);
			subtype.setText(defaultPartitionSubTypeText[rowid]);
			size.   setText(defaultPartitionSizeText[rowid]);
			sizeHex.setText("");
			offset. setText("");
		} else {
			name.   setText("");
			subtype.setText("");
			size.   setText("");
			sizeHex.setText("");
			offset. setText("");
		}
	}


	public void attachListeners( UIController controller ) {
		enabled.addActionListener(controller);
		name.   addActionListener(controller);
		type.   addActionListener(controller);
		subtype.addActionListener(controller);
		size.   addActionListener(controller);
		sizeHex.addActionListener(controller);
		offset. addActionListener(controller);
	}


	private JPanel wrap( Component src ) {
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BorderLayout(0, 0));
		wrapper.add( src );
		return wrapper;
	}


	private CSVRow setRowEnabled(boolean enable) { enabled.setSelected( enable ); return this; }
	private CSVRow setRowName(String value)      { name.setText( value );         return this; }
	private CSVRow setRowType(String value)      { type.setSelectedItem(value);   return this; }
	private CSVRow setRowSubtype(String value)   { subtype.setText( value );      return this; }
	private CSVRow setRowSize(String value)      { size.setText( value );         return this; }
	private CSVRow setRowSizeHex(String value)   { sizeHex.setText( value );      return this; }
	private CSVRow setRowOffset(String value)    { offset.setText( value );       return this; }


}
