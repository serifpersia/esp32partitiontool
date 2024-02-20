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

	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTextField textField_9;
	private JTextField textField_10;
	private JTextField textField_11;
	private JTextField textField_12;
	private JTextField textField_13;
	private JTextField textField_14;
	private JTextField textField_15;
	private JTextField textField_16;
	private JTextField textField_17;
	private JTextField textField_18;
	private JTextField textField_19;
	private JTextField textField_20;
	private JTextField textField_21;
	private JTextField textField_22;
	private JTextField textField_23;
	private JTextField textField_24;
	private JTextField textField_25;
	private JTextField textField_26;
	private JTextField textField_27;
	private JTextField textField_28;
	private JTextField textField_29;
	private JTextField textField_30;
	private JTextField textField_31;
	private JTextField textField_32;
	private JTextField textField_33;
	private JTextField textField_34;
	private JTextField textField_35;
	private JTextField textField_36;
	private JTextField textField_37;
	private JTextField textField_38;
	private JTextField textField_39;
	private JTextField textField_40;
	private JTextField textField_41;
	private JTextField textField_42;

	public UI() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel csv_GenPanel = new JPanel();
		add(csv_GenPanel);
		csv_GenPanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_GenLabel = new JLabel("Partitions");
		csv_GenLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		csv_GenLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_GenPanel.add(csv_GenLabel, BorderLayout.NORTH);

		JPanel csv_RootPanel = new JPanel();
		csv_GenPanel.add(csv_RootPanel, BorderLayout.CENTER);
		csv_RootPanel.setLayout(new GridLayout(0, 6, 0, 0));

		JPanel csv_EnablePanel = new JPanel();
		csv_RootPanel.add(csv_EnablePanel);
		csv_EnablePanel.setLayout(new BorderLayout(0, 0));

		JLabel csv_EnableLabel = new JLabel("Enable");
		csv_EnableLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_EnablePanel.add(csv_EnableLabel, BorderLayout.NORTH);

		JPanel csv_EnableInnerPanel = new JPanel();
		csv_EnablePanel.add(csv_EnableInnerPanel, BorderLayout.CENTER);
		csv_EnableInnerPanel.setLayout(new GridLayout(10, 0, 0, 0));

		JCheckBox chckbxNewCheckBox = new JCheckBox("Enable");
		chckbxNewCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		csv_EnableInnerPanel.add(chckbxNewCheckBox);

		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Enable");
		chckbxNewCheckBox_1.setHorizontalAlignment(SwingConstants.CENTER);
		csv_EnableInnerPanel.add(chckbxNewCheckBox_1);

		JCheckBox chckbxNewCheckBox_2 = new JCheckBox("Enable");
		chckbxNewCheckBox_2.setHorizontalAlignment(SwingConstants.CENTER);
		csv_EnableInnerPanel.add(chckbxNewCheckBox_2);

		JCheckBox chckbxNewCheckBox_3 = new JCheckBox("Enable");
		chckbxNewCheckBox_3.setHorizontalAlignment(SwingConstants.CENTER);
		csv_EnableInnerPanel.add(chckbxNewCheckBox_3);

		JCheckBox chckbxNewCheckBox_4 = new JCheckBox("Enable");
		chckbxNewCheckBox_4.setHorizontalAlignment(SwingConstants.CENTER);
		csv_EnableInnerPanel.add(chckbxNewCheckBox_4);

		JCheckBox chckbxNewCheckBox_5 = new JCheckBox("Enable");
		chckbxNewCheckBox_5.setHorizontalAlignment(SwingConstants.CENTER);
		csv_EnableInnerPanel.add(chckbxNewCheckBox_5);

		JCheckBox chckbxNewCheckBox_6 = new JCheckBox("Enable");
		chckbxNewCheckBox_6.setHorizontalAlignment(SwingConstants.CENTER);
		csv_EnableInnerPanel.add(chckbxNewCheckBox_6);

		JCheckBox chckbxNewCheckBox_7 = new JCheckBox("Enable");
		chckbxNewCheckBox_7.setHorizontalAlignment(SwingConstants.CENTER);
		csv_EnableInnerPanel.add(chckbxNewCheckBox_7);

		JCheckBox chckbxNewCheckBox_8 = new JCheckBox("Enable");
		chckbxNewCheckBox_8.setHorizontalAlignment(SwingConstants.CENTER);
		csv_EnableInnerPanel.add(chckbxNewCheckBox_8);

		JCheckBox chckbxNewCheckBox_9 = new JCheckBox("Enable");
		chckbxNewCheckBox_9.setHorizontalAlignment(SwingConstants.CENTER);
		csv_EnableInnerPanel.add(chckbxNewCheckBox_9);

		JPanel csv_PartitionName = new JPanel();
		csv_RootPanel.add(csv_PartitionName);
		csv_PartitionName.setLayout(new BorderLayout(0, 0));

		JLabel csv_PartitionNameLabel = new JLabel("Name");
		csv_PartitionNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		csv_PartitionName.add(csv_PartitionNameLabel, BorderLayout.NORTH);

		JPanel csv_PartitionNameInnerPanel = new JPanel();
		csv_PartitionName.add(csv_PartitionNameInnerPanel, BorderLayout.CENTER);
		csv_PartitionNameInnerPanel.setLayout(new GridLayout(10, 0, 0, 0));

		textField = new JTextField();
		csv_PartitionNameInnerPanel.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		csv_PartitionNameInnerPanel.add(textField_1);

		textField_2 = new JTextField();
		textField_2.setColumns(10);
		csv_PartitionNameInnerPanel.add(textField_2);

		textField_3 = new JTextField();
		textField_3.setColumns(10);
		csv_PartitionNameInnerPanel.add(textField_3);

		textField_4 = new JTextField();
		textField_4.setColumns(10);
		csv_PartitionNameInnerPanel.add(textField_4);

		textField_5 = new JTextField();
		textField_5.setColumns(10);
		csv_PartitionNameInnerPanel.add(textField_5);

		textField_6 = new JTextField();
		textField_6.setColumns(10);
		csv_PartitionNameInnerPanel.add(textField_6);

		textField_7 = new JTextField();
		textField_7.setColumns(10);
		csv_PartitionNameInnerPanel.add(textField_7);

		textField_8 = new JTextField();
		textField_8.setColumns(10);
		csv_PartitionNameInnerPanel.add(textField_8);

		textField_9 = new JTextField();
		textField_9.setColumns(10);
		csv_PartitionNameInnerPanel.add(textField_9);

		JPanel panel = new JPanel();
		csv_RootPanel.add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel_1 = new JLabel("Type");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel_1, BorderLayout.NORTH);

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(10, 0, 0, 0));

		JComboBox<?> comboBox = new JComboBox<Object>();
		panel_1.add(comboBox);

		JComboBox<?> comboBox_1 = new JComboBox<Object>();
		panel_1.add(comboBox_1);

		JComboBox<?> comboBox_2 = new JComboBox<Object>();
		panel_1.add(comboBox_2);

		JComboBox<?> comboBox_3 = new JComboBox<Object>();
		panel_1.add(comboBox_3);

		JComboBox<?> comboBox_4 = new JComboBox<Object>();
		panel_1.add(comboBox_4);

		JComboBox<?> comboBox_5 = new JComboBox<Object>();
		panel_1.add(comboBox_5);

		JComboBox<?> comboBox_6 = new JComboBox<Object>();
		panel_1.add(comboBox_6);

		JComboBox<?> comboBox_7 = new JComboBox<Object>();
		panel_1.add(comboBox_7);

		JComboBox<?> comboBox_8 = new JComboBox<Object>();
		panel_1.add(comboBox_8);

		JComboBox<?> comboBox_9 = new JComboBox<Object>();
		panel_1.add(comboBox_9);

		JPanel panel_2 = new JPanel();
		csv_RootPanel.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel_2 = new JLabel("SubType");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(lblNewLabel_2, BorderLayout.NORTH);

		JPanel panel_3 = new JPanel();
		panel_2.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new GridLayout(10, 0, 0, 0));

		textField_31 = new JTextField();
		textField_31.setColumns(10);
		panel_3.add(textField_31);

		textField_32 = new JTextField();
		textField_32.setColumns(10);
		panel_3.add(textField_32);

		textField_33 = new JTextField();
		textField_33.setColumns(10);
		panel_3.add(textField_33);

		textField_34 = new JTextField();
		textField_34.setColumns(10);
		panel_3.add(textField_34);

		textField_35 = new JTextField();
		textField_35.setColumns(10);
		panel_3.add(textField_35);

		textField_36 = new JTextField();
		textField_36.setColumns(10);
		panel_3.add(textField_36);

		textField_30 = new JTextField();
		textField_30.setColumns(10);
		panel_3.add(textField_30);

		textField_37 = new JTextField();
		textField_37.setColumns(10);
		panel_3.add(textField_37);

		textField_38 = new JTextField();
		textField_38.setColumns(10);
		panel_3.add(textField_38);

		textField_39 = new JTextField();
		textField_39.setColumns(10);
		panel_3.add(textField_39);

		JPanel panel_4 = new JPanel();
		csv_RootPanel.add(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel_3 = new JLabel("Size(kB)");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(lblNewLabel_3, BorderLayout.NORTH);

		JPanel panel_5 = new JPanel();
		panel_4.add(panel_5, BorderLayout.CENTER);
		panel_5.setLayout(new GridLayout(10, 0, 0, 0));

		textField_10 = new JTextField();
		textField_10.setColumns(10);
		panel_5.add(textField_10);

		textField_11 = new JTextField();
		textField_11.setColumns(10);
		panel_5.add(textField_11);

		textField_12 = new JTextField();
		textField_12.setColumns(10);
		panel_5.add(textField_12);

		textField_13 = new JTextField();
		textField_13.setColumns(10);
		panel_5.add(textField_13);

		textField_14 = new JTextField();
		textField_14.setColumns(10);
		panel_5.add(textField_14);

		textField_15 = new JTextField();
		textField_15.setColumns(10);
		panel_5.add(textField_15);

		textField_16 = new JTextField();
		textField_16.setColumns(10);
		panel_5.add(textField_16);

		textField_17 = new JTextField();
		textField_17.setColumns(10);
		panel_5.add(textField_17);

		textField_18 = new JTextField();
		textField_18.setColumns(10);
		panel_5.add(textField_18);

		textField_19 = new JTextField();
		textField_19.setColumns(10);
		panel_5.add(textField_19);

		JPanel panel_6 = new JPanel();
		csv_RootPanel.add(panel_6);
		panel_6.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel_4 = new JLabel("Offset(hex)");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		panel_6.add(lblNewLabel_4, BorderLayout.NORTH);

		JPanel panel_7 = new JPanel();
		panel_6.add(panel_7, BorderLayout.CENTER);
		panel_7.setLayout(new GridLayout(10, 0, 0, 0));

		textField_21 = new JTextField();
		textField_21.setEditable(false);
		textField_21.setColumns(10);
		panel_7.add(textField_21);

		textField_22 = new JTextField();
		textField_22.setEditable(false);
		textField_22.setColumns(10);
		panel_7.add(textField_22);

		textField_23 = new JTextField();
		textField_23.setEditable(false);
		textField_23.setColumns(10);
		panel_7.add(textField_23);

		textField_24 = new JTextField();
		textField_24.setEditable(false);
		textField_24.setColumns(10);
		panel_7.add(textField_24);

		textField_25 = new JTextField();
		textField_25.setEditable(false);
		textField_25.setColumns(10);
		panel_7.add(textField_25);

		textField_26 = new JTextField();
		textField_26.setEditable(false);
		textField_26.setColumns(10);
		panel_7.add(textField_26);

		textField_27 = new JTextField();
		textField_27.setEditable(false);
		textField_27.setColumns(10);
		panel_7.add(textField_27);

		textField_28 = new JTextField();
		textField_28.setEditable(false);
		textField_28.setColumns(10);
		panel_7.add(textField_28);

		textField_29 = new JTextField();
		textField_29.setEditable(false);
		textField_29.setColumns(10);
		panel_7.add(textField_29);

		textField_20 = new JTextField();
		textField_20.setEditable(false);
		textField_20.setColumns(10);
		panel_7.add(textField_20);

		JPanel csv_PartitionsVisual = new JPanel();
		csv_GenPanel.add(csv_PartitionsVisual, BorderLayout.SOUTH);
		csv_PartitionsVisual.setLayout(new BorderLayout(0, 0));

		JPanel panel_8 = new JPanel();
		csv_PartitionsVisual.add(panel_8, BorderLayout.SOUTH);

		JLabel lblNewLabel_5 = new JLabel("Visuals TO DO:");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel_8.add(lblNewLabel_5);

		JPanel panel_9 = new JPanel();
		csv_PartitionsVisual.add(panel_9, BorderLayout.NORTH);

		JLabel lblNewLabel = new JLabel("Flash Size");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_9.add(lblNewLabel);

		JComboBox<?> comboBox_11 = new JComboBox<Object>();
		panel_9.add(comboBox_11);

		JButton btnNewButton = new JButton("Generate CSV");
		panel_9.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Generate Bin");
		panel_9.add(btnNewButton_1);

		JPanel panel_10 = new JPanel();
		add(panel_10, BorderLayout.EAST);
		panel_10.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel_6 = new JLabel("SPIFFS");
		lblNewLabel_6.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_6.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel_10.add(lblNewLabel_6, BorderLayout.NORTH);

		JPanel panel_11 = new JPanel();

		panel_10.add(panel_11, BorderLayout.CENTER);
		panel_11.setLayout(new GridLayout(4, 0, 0, 0));

		JPanel panel_12 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_12.getLayout();
		flowLayout.setVgap(30);
		panel_11.add(panel_12);

		JLabel lblNewLabel_7 = new JLabel("Block Size");
		lblNewLabel_7.setHorizontalAlignment(SwingConstants.CENTER);
		panel_12.add(lblNewLabel_7);

		textField_40 = new JTextField();
		textField_40.setHorizontalAlignment(SwingConstants.CENTER);
		panel_12.add(textField_40);
		textField_40.setColumns(5);
		textField_40.setPreferredSize(new Dimension(textField_40.getPreferredSize().width, 30));

		JPanel panel_13 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_13.getLayout();
		flowLayout_1.setVgap(30);
		panel_11.add(panel_13);

		JLabel lblNewLabel_7_1 = new JLabel("Page Size");
		lblNewLabel_7_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel_13.add(lblNewLabel_7_1);

		textField_41 = new JTextField();
		textField_41.setHorizontalAlignment(SwingConstants.CENTER);
		textField_41.setColumns(5);
		textField_41.setPreferredSize(new Dimension(textField_41.getPreferredSize().width, 30));
		panel_13.add(textField_41);

		JPanel panel_14 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_14.getLayout();
		flowLayout_2.setVgap(30);
		panel_11.add(panel_14);

		JLabel lblNewLabel_7_2 = new JLabel("Offset(x)");
		lblNewLabel_7_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel_14.add(lblNewLabel_7_2);

		textField_42 = new JTextField();
		textField_42.setHorizontalAlignment(SwingConstants.CENTER);
		textField_42.setColumns(5);
		textField_42.setPreferredSize(new Dimension(textField_42.getPreferredSize().width, 30));
		panel_14.add(textField_42);

		JPanel panel_15 = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) panel_15.getLayout();
		flowLayout_3.setVgap(30);
		panel_11.add(panel_15);

		JButton btnNewButton_2 = new JButton("Generate SPIFFS");
		panel_15.add(btnNewButton_2);
	}

}
