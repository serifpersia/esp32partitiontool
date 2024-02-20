package com.serifpersia.esp32partitiontool;

import javax.swing.JFrame;

public class ESP32PartitionTool extends JFrame {
	private UI contentPane = new UI();

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		// Use EventQueue.invokeLater to ensure Swing components are initialized
		// properly
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ESP32PartitionTool frame = new ESP32PartitionTool();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ESP32PartitionTool() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("ESP32 Partition Tool");
		setSize(800, 400);
		setLocationRelativeTo(null);

		// Set the content pane
		setContentPane(contentPane);

		new UIController(contentPane);
	}
}
