package com.serifpersia.esp32partitiontool;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class HelpPanel extends JPanel {

	private JButton nextButton;
	private JPanel helpPanel;
	private JPanel helpTipPanel;
	private JLabel helpTipLabel;
	private ImageIcon infoIcon;
	private int helpTipIndex = 0;

	private String[] helpTips = { "The default export path for partitions.csv is the sketch directory.",
			"If no partitions.csv file is found is the sketch directory, then the partition selected under <b>Tools > Partition schemes</b> will be used.",
			"Partitions like nvs or any other small partitions before the app partition need their value to be a multiple of 4.",
			"Partitions before the first app partition should have a total of 28 kB so the offset for the first app partition will always be correct at 0x10000 offset. Any other configuration will cause the ESP32 board to not function properly.",
			"The app partition needs to be at 0x10000, and following partitions have to be a multiple of 64.",
			"The app partition needs to be a minimum of 1024 kB in size." };

	public HelpPanel() {
		createPanel();
	}

	private void createPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		createHelpTipPanel();
		creatHelpPanel();
		createNextButton();

		Box box = new Box(BoxLayout.Y_AXIS);
		box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		box.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(0, 0, 10, 0)));
		box.add(Box.createVerticalGlue());
		box.add(helpPanel); // insert panel
		box.add(Box.createVerticalGlue());
		box.add(nextButton, BorderLayout.SOUTH);

		add(box);

		Dimension helpPanelSize = new Dimension(350, 200);
		setPreferredSize(helpPanelSize);
		setMaximumSize(helpPanelSize);
	}

	private void createHelpTipPanel() {
		infoIcon = new ImageIcon(getClass().getResource("/hint.png"));

		helpTipPanel = new JPanel();
		helpTipPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		helpTipPanel.setLayout(new BoxLayout(helpTipPanel, BoxLayout.Y_AXIS));
		helpTipPanel.setAlignmentY(JLabel.TOP);
		helpTipPanel.setAlignmentX(JLabel.LEFT);

		helpTipLabel = new JLabel();
		helpTipLabel.setLayout(new BoxLayout(helpTipLabel, BoxLayout.X_AXIS));
		helpTipLabel.setVerticalAlignment(JLabel.TOP);
		helpTipLabel.setVerticalTextPosition(JLabel.TOP);
		helpTipLabel.setIcon(infoIcon);
		helpTipLabel.setFont( UI.defaultFont.deriveFont(Font.PLAIN, 12) );

		helpTipPanel.add(helpTipLabel);

		setHelpPanelTip();
	}

	private void setHelpPanelTip() {
		helpTipLabel.setVisible(false);
		helpTipIndex = helpTipIndex % helpTips.length;
		helpTipLabel.setText("<html><p>" + helpTips[helpTipIndex] + "</p></html>");
		helpTipLabel.setVisible(true);
	}

	private void creatHelpPanel() {
		helpPanel = new JPanel();
		helpPanel.setLayout(new BorderLayout(0, 0));
		helpPanel.add(helpTipPanel);
	}

	private void createNextButton() {
		nextButton = new JButton(" Next tip >> ");
		nextButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				helpTipIndex++;
				setHelpPanelTip();
			}
		});
	}

}
