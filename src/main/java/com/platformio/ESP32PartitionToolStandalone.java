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

package com.platformio;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import com.serifpersia.esp32partitiontool.FileManager;
import com.serifpersia.esp32partitiontool.UI;
import com.serifpersia.esp32partitiontool.UIController;

// local implementation of rounded borders to overwrite global styles
@SuppressWarnings("serial")
final class CustomBorder extends AbstractBorder {
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		super.paintBorder(c, g, x, y, width, height);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setPaint( new Color(0xcc, 0xcc, 0xcc) );
		Shape shape = new RoundRectangle2D.Float(1, 1, c.getWidth()-2, c.getHeight()-2, 5, 5);
		g2d.draw(shape);
	}
}

@SuppressWarnings("serial")
final class JFrameStandalone extends JFrame {
	public JFrameStandalone() {
		// apply some style fixes globally
		CompoundBorder borderTextField = BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0), new CustomBorder() );
		CompoundBorder borderComboBox = BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder(2, 2, 2, 2), new CustomBorder() );

		UIManager.put("TextField.background", Color.WHITE);
		UIManager.put("TextField.border", borderTextField);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Size and display the frame
		setSize(800, 564);
		//setResizable(false);

		// Set frame position to the center of the screen
		setLocationRelativeTo(null);

		// Add background image
		JLabel background = new JLabel(new ImageIcon(getClass().getResource("/bg.png")));
		background.setLayout(new BorderLayout());
		setContentPane(background);

		JFrame frame = this;

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Just hide the frame instead of disposing
				frame.setVisible(false);
			}
		});

		// [esc] key closes the app
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
			}
		});
	}
}


public class ESP32PartitionToolStandalone {

	private JFrame frame = new JFrameStandalone();

	private UI contentPane = new UI(frame, "ESP32 Partition Tool (Standalone)");
	private FileManager fileManager; // FileManager instance
	private AppSettingsStandalone settings;

	public static void main(String[] args) {
		ESP32PartitionToolStandalone tool = new ESP32PartitionToolStandalone();
		tool.init(args);
	}

	public void addUI(UI contentPane) {
		frame.add(contentPane);
	}

	private void init(String[] args) {
		settings = new AppSettingsStandalone(args);
		fileManager = new FileManager(contentPane, settings);
		fileManager.setUIController(new UIController(contentPane, fileManager));
		// Add panel to frame
		addUI(contentPane);
		frame.setFocusable(true);
		frame.requestFocus();
		fileManager.loadDefaultCSV();
		frame.setVisible(true);
	}

}
