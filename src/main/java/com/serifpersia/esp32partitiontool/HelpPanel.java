package com.serifpersia.esp32partitiontool;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class HelpPanel extends JPanel {

	private JButton nextButton;
	private JPanel helpPanel;
	private JPanel helpTipPanel;
	private JLabel helpTipLabel;
	private ImageIcon infoIcon;
	private int helpTipIndex = 0;

	private String[] helpTips = { l10n.getString("helpPanel.tip1"), l10n.getString("helpPanel.tip2"),
			l10n.getString("helpPanel.tip3"), l10n.getString("helpPanel.tip4"), l10n.getString("helpPanel.tip5"),
			l10n.getString("helpPanel.tip6") };

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
		helpTipLabel.setFont(UI.defaultFont.deriveFont(Font.PLAIN, 12));

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
		nextButton = new JButton(" " + l10n.getString("helpPanel.nextTip") + " >> ");
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
