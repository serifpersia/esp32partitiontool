/*
		ESP32 Partition Tool was developed to facilitate the creation of custom partition schemes
		for ESP32 projects within the Arduino IDE 1.8.x environment.

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

package com.arduino;

import processing.app.Editor;
import processing.app.tools.Tool;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;
import javax.swing.*;

import com.serifpersia.esp32partitiontool.FileManager;
import com.serifpersia.esp32partitiontool.UI;
import com.serifpersia.esp32partitiontool.UIController;

@SuppressWarnings("serial")
public class ESP32PartitionTool extends JFrame implements Tool {

	private AppSettingsArduino settings;

	private JFrame frame;
	private Editor editor;

	private UI contentPane = new UI();
	private FileManager fileManager;

	public void init(Editor editor) {
		this.editor = editor;
	}

	public String getMenuTitle() {
		return "ESP32 Partition Tool";
	}

	public void addUI(UI contentPane) {
		frame.add(contentPane);
	}

	private void initGUI() {

		if (settings == null) {
			settings = new AppSettingsArduino(editor);
		}

		settings.load();

		if (fileManager == null) {
			fileManager = new FileManager(contentPane, settings);
		}

		if (frame == null) {
			frame = new JFrame("ESP32 Partition Tool");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			frame.setSize(1024, 640);
			frame.setResizable(false);

			frame.setLocationRelativeTo(null);

			JLabel background = new JLabel(new ImageIcon(getClass().getResource("/bg.png")));
			background.setLayout(new BorderLayout());
			frame.setContentPane(background);

			addUI(contentPane);

			fileManager.setUIController(new UIController(contentPane, fileManager));
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					frame.setVisible(false);
				}
			});
		} else {
			frame.toFront();
		}

		fileManager.loadDefaultCSV();
		frame.setVisible(true);
	}

	public void run() {
		initGUI();
	}
}
