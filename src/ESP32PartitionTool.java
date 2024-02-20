package com.serifpersia.esp32partitiontool;

import processing.app.Editor;
import processing.app.tools.Tool;

import javax.swing.JFrame;

public class ESP32PartitionTool implements Tool {

	private UI contentPane = new UI();

	Editor editor;

	public void init(Editor editor) {
		this.editor = editor;
	}

	public String getMenuTitle() {
		return "ESP32 Partition Tool";
	}

	private void initGUI() {
		System.out.println("Hello World!");

		// Create and show the JFrame
		JFrame frame = new JFrame("ESP32 Partition Tool");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Size and display the frame
		frame.setSize(800, 400);

		// Set frame position to the center of the screen
		frame.setLocationRelativeTo(null);

		// Add panel to frame
		frame.getContentPane().add(contentPane);
		frame.setVisible(true);
	}

	public void run() {
		initGUI();
	}
}
