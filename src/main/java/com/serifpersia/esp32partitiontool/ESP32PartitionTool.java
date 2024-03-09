/*
	ESP32 Partition Tool was developed to facilitate the creation of custom partition schemes
	for ESP32 projects within the PlatformIO environment.

	Copyright (c) 2024 serifpersia, github.com/serifpersia

	This program is open-source software distributed under the terms of the MIT License.
	You are free to redistribute and/or modify it under the conditions of the MIT License,
	provided that this disclaimer remains intact in the source files.

	MIT License

	Copyright (c) 2024 serifpersia

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
*/

package com.serifpersia.esp32partitiontool;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class ESP32PartitionTool {

	private UI contentPane = new UI();
	private FileManager fileManager; // FileManager instance

	private JFrame frame;

	public static void main(String[] args) {
		ESP32PartitionTool tool = new ESP32PartitionTool();
		tool.init();
	}

	public void addUI(UI contentPane) {
		frame.add(contentPane);
	}

	private void init() {

		this.fileManager = new FileManager(contentPane);

		// Create and show the JFrame
		if (frame == null) {
			frame = new JFrame("ESP32 Partition Tool");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// Size and display the frame
			frame.setSize(1024, 640);
			frame.setResizable(false);

			// Set frame position to the center of the screen
			frame.setLocationRelativeTo(null);

			// Add background image
			JLabel background = new JLabel(new ImageIcon(getClass().getResource("/bg.png")));
			background.setLayout(new BorderLayout());
			frame.setContentPane(background);

			// Add panel to frame
			addUI(contentPane);

			fileManager.setUIController(new UIController(contentPane, fileManager));
			fileManager.loadDefaultCSV();

			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					// Just hide the frame instead of disposing
					frame.setVisible(false);
				}
			});
			// Ensure visibility of the frame
			frame.setVisible(true);
		} else {
			// If the frame is already open, bring it to the front and make it visible
			frame.toFront();
			frame.setVisible(true);
		}
	}
}
