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

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


import com.serifpersia.esp32partitiontool.FileManager;
import com.serifpersia.esp32partitiontool.UI;
import com.serifpersia.esp32partitiontool.UIController;

@SuppressWarnings("serial")
final class JFrameArduino extends JFrame {
	public JFrameArduino() {

		JFrame frame = this;

		setSize(1024, 640);
		setResizable(false);

		setLocationRelativeTo(null);

		JLabel background = new JLabel(new ImageIcon(getClass().getResource("/bg.png")));
		background.setLayout(new BorderLayout());
		setContentPane(background);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
			}
		});

		// [esc] key hides the app
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					frame.setVisible(false);
				}
			}
		});
	}
}


@SuppressWarnings("serial")
public class ESP32PartitionTool extends JFrame implements Tool {

	private JFrame frame = new JFrameArduino();
	private AppSettingsArduino settings;
	private Editor editor;
	private UI contentPane = new UI(frame, getMenuTitle() + " (Arduino IDE)");
	private FileManager fileManager;
	private boolean ui_loaded = false;

	public void init(Editor editor) {
		this.editor = editor;
	}

	public String getMenuTitle() {
		return "ESP32 Partition Tool";
	}

	public void addUI(UI contentPane) {
		fileManager.setUIController(new UIController(contentPane, fileManager));
		frame.add(contentPane);
	}

	private void initGUI() {

		if (settings == null) {
			settings = new AppSettingsArduino(editor);
		} else {
			settings.init();
		}

		if( !settings.platformSupported  ) {
			frame.setVisible(false);
			System.err.println("Only ESP32 devices are supported!");
			return;
		}


		if (fileManager == null) {
			fileManager = new FileManager(contentPane, settings);
		}

		if( ! ui_loaded ) {
			addUI(contentPane);
			ui_loaded = true;
		} else {
			contentPane.reload();
		}

		frame.setFocusable(true);
		frame.requestFocus();
		frame.toFront();

		// prevent repaint problem when reloading CSV
		EventQueue.invokeLater( () -> fileManager.loadDefaultCSV() );

		frame.setVisible(true);
	}

	public void run() {
		initGUI();
	}
}
