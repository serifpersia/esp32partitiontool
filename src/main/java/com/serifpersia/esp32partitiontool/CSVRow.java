package com.serifpersia.esp32partitiontool;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;

import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;

@SuppressWarnings("serial")
public class CSVRow extends JPanel {

	public JCheckBox enabled;
	public JTextField name;
	public JComboBox<?> type;
	public JTextField subtype;
	public JFormattedTextField size;
	public JTextField sizeHex;
	public JTextField offset;

	public CSVRow(String values[]) {

		setLayout(new GridLayout(0, 7, 0, 0));
		setOpaque(false);

		enabled = new JCheckBox("Enable");
		name = new JTextField();
		type = new JComboBox<>(new String[] { "data", "app" });
		subtype = new JTextField();
		// size = new JTextField();
		sizeHex = new JTextField();
		offset = new JTextField();

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

		// center options text in JComboBox
		type.setRenderer(new DefaultListCellRenderer() {
			{
				setHorizontalAlignment(DefaultListCellRenderer.CENTER);
			}
		});

		enabled.setHorizontalAlignment(SwingConstants.CENTER);
		name.setHorizontalAlignment(SwingConstants.CENTER);
		subtype.setHorizontalAlignment(SwingConstants.CENTER);
		size.setHorizontalAlignment(SwingConstants.RIGHT);
		sizeHex.setHorizontalAlignment(SwingConstants.RIGHT);
		offset.setHorizontalAlignment(SwingConstants.RIGHT);

		size.setFont(new Font(Font.MONOSPACED, currFont.getStyle(), currFont.getSize()));
		sizeHex.setFont(new Font(Font.MONOSPACED, currFont.getStyle(), currFont.getSize()));
		offset.setFont(new Font(Font.MONOSPACED, currFont.getStyle(), currFont.getSize()));

		sizeHex.setEditable(false);
		offset.setEditable(false);

		sizeHex.setBackground(new Color(240, 240, 240));
		offset.setBackground(new Color(240, 240, 240));

		if (values != null && values.length > 5) {
			this.setRowName(values[0]);
			this.setRowType(values[1]);
			this.setRowSubtype(values[2]);
			this.setRowSize(values[3]);
			this.setRowSizeHex(values[4]);
			this.setRowOffset(values[5]);
			this.setRowEnabled(true);
		} else {
			disableRow();
		}

		Component[] components = { enabled, name, type, subtype, size, sizeHex, offset };
		for (int i = 0; i < components.length; i++) {
			add(wrap(components[i]), BorderLayout.CENTER);
		}

	}

	public void enableRow() {
		enabled.setSelected(true);
		name.setEditable(true);
		type.setEnabled(true);
		subtype.setEditable(true);
		size.setEditable(true);
	}

	public void disableRow() {
		enabled.setSelected(false);
		name.setEditable(false);
		type.setEnabled(false);
		subtype.setEditable(false);
		size.setEditable(false);
		sizeHex.setText("");
		offset.setText("");
	}

	public String toString() {
		String comma = ",";
		return String.format("%-10s %-5s %-10s %10s %10s", name.getText() + comma,
				type.getSelectedItem().toString() + comma, subtype.getText() + comma, "0x" + offset.getText() + comma,
				"0x" + sizeHex.getText() + comma);
	}

	public void setDefaults(int rowid) {
		// Set text if isSelected is true
		String[] defaultPartitionNameText = { "nvs", "otadata", "app0", "app1", "spiffs", "coredump" };
		// int[] defaultPartitionTypeText = { 0, 0, 1, 1, 0, 0 };
		String[] defaultPartitionSubTypeText = { "nvs", "ota", "ota_0", "ota_1", "spiffs", "coredump", };
		String[] defaultPartitionSizeText = { "20", "8", "1280", "1280", "1408", "64" };

		if (rowid >= 0 && rowid < defaultPartitionNameText.length) {
			if (name.getText().isEmpty())
				setRowName(defaultPartitionNameText[rowid]);
			// type. setSelectedIndex(defaultPartitionTypeText[rowid%type.getItemCount()]);
			if (subtype.getText().isEmpty())
				setRowSubtype(defaultPartitionSubTypeText[rowid]);
			if (size.getText().isEmpty())
				setRowSize(defaultPartitionSizeText[rowid]);
			sizeHex.setText("");
			offset.setText("");
		} else {
			name.setText("");
			subtype.setText("");
			size.setText("");
			sizeHex.setText("");
			offset.setText("");
		}
	}

	public void attachListeners(UIController controller) {
		enabled.addActionListener(controller);
		name.addActionListener(controller);
		type.addActionListener(controller);
		subtype.addActionListener(controller);
		size.addActionListener(controller);
		sizeHex.addActionListener(controller);
		offset.addActionListener(controller);
	}

	private JPanel wrap(Component src) {
		JPanel wrapper = new JPanel();
		wrapper.setOpaque(false);
		wrapper.setLayout(new BorderLayout(0, 0));
		wrapper.add(src);
		return wrapper;
	}

	public boolean isValidSubtype(String typeStr) {
		final String[] validSubtypes = { "factory", "test", "nvs", "phy", "nvs_keys", "undefined", "efuse", "ota",
				"spiffs", "littlefs", "coredump", "ota_0", "ota_1", "ota_2", "ota_3", "ota_4", "ota_5", "ota_6",
				"ota_7", "ota_8", "ota_9", "ota_10", "ota_11", "ota_12", "ota_13", "ota_14", "ota_15" };
		for (int i = 0; i < validSubtypes.length; i++) {
			if (typeStr.equals(validSubtypes[i]))
				return true;
		}

		if (typeStr.startsWith("0x")) {
			try {
				int value = Integer.decode(typeStr);
				if (value < 0xff)
					return true;
			} catch (NumberFormatException e) {
			}
			;
		}
		return false;
	}

	public String subtypeToString(String value) {
		if (value.startsWith("0x")) {
			String typeStr = (String) type.getSelectedItem();
			int subTypeVal = -1;
			try {
				subTypeVal = Integer.decode(value);
			} catch (NumberFormatException e) {
				return value;
			}
			if (typeStr.equals("app")) {
				// When type is app, the SubType field can be specified as factory (0x00), ota_0
				// (0x10) ... ota_15 (0x1F) or test (0x20).
				if (subTypeVal == 0x00)
					value = "factory";
				else if (subTypeVal >= 0x10 && subTypeVal <= 0x1f)
					value = String.format("ota_%d", subTypeVal);
				else if (subTypeVal == 0x20)
					value = "test";
			} else {
				// When type is data, the subtype field can be specified as ota (0x00), phy
				// (0x01), nvs (0x02), nvs_keys (0x04), or a range of other component-specific
				// subtypes
				if (subTypeVal == 0x00)
					value = "ota";
				else if (subTypeVal == 0x01)
					value = "phy";
				else if (subTypeVal == 0x02)
					value = "nvs";
				else if (subTypeVal == 0x03)
					value = "coredump";
				else if (subTypeVal == 0x04)
					value = "nvs_keys";
				else if (subTypeVal == 0x05)
					value = "efuse";
				else if (subTypeVal == 0x06)
					value = "undefined";
				else if (subTypeVal == 0x81)
					value = "fat";
				else if (subTypeVal == 0x82)
					value = "spiffs";
				else if (subTypeVal == 0x83)
					value = "littlefs";
			}
		} else {

		}
		return value;
	}

	private CSVRow setRowEnabled(boolean enable) {
		enabled.setSelected(enable);
		return this;
	}

	private CSVRow setRowName(String value) {
		name.setText(value);
		return this;
	}

	private CSVRow setRowType(String value) {
		type.setSelectedItem(value);
		return this;
	}

	private CSVRow setRowSubtype(String value) {
		String vSubType = subtypeToString(value);
		if (!vSubType.equals(value)) {
			value = vSubType;
		}
		subtype.setForeground(isValidSubtype(value) ? Color.BLACK : Color.RED);
		subtype.setText(value);
		return this;
	}

	private CSVRow setRowSize(String value) {
		size.setText(value);
		return this;
	}

	private CSVRow setRowSizeHex(String value) {
		sizeHex.setText(value);
		return this;
	}

	private CSVRow setRowOffset(String value) {
		offset.setText(value);
		return this;
	}

}
