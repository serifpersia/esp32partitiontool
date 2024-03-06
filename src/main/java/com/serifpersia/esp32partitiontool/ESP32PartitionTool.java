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

package com.serifpersia.esp32partitiontool;

import processing.app.Editor;
import processing.app.tools.Tool;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;
import javax.swing.*;



final class JFrameWithBgImage extends JFrame {

	private JLabel background;

	public JFrameWithBgImage() {
		super("ESP32 Partition Tool");

		final int minFrameWidth  = 1024;
		final int minFrameHeight = 640;
		final int maxFrameWidth  = 1280;
		final int maxFrameHeight = 800;

		setVisible(true);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMaximumSize(new Dimension(maxFrameWidth, maxFrameHeight));
		setMinimumSize(new Dimension(minFrameWidth, minFrameHeight));
		setSize(new Dimension(minFrameWidth, minFrameHeight));
		setResizable(false);

		ImageIcon imageIcon = new ImageIcon(UIController.class.getResource("/resources/bg.png"));
		Image scaledImage   = getScaledImage( imageIcon.getImage(), 1024, 640 );
		background          = new JLabel(new ImageIcon(scaledImage));

		add( background );
		background.setLayout(new BorderLayout(0, 0));
	}

	public void addUI(UI contentPane) {
		background.add( contentPane );
	}


	private Image getScaledImage(Image srcImg, int w, int h){
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}

}


public class ESP32PartitionTool implements Tool {

	private UI contentPane = new UI();
	private FileManager fileManager; // FileManager instance

	Editor editor;
	private JFrame frame;

	public static void main(String[] args) {
		ESP32PartitionTool tool = new ESP32PartitionTool();
		tool.run();
	}

	public void init(Editor editor) {
		this.editor = editor;
		// Pass the Editor instance when creating FileManager
		this.fileManager = new FileManager(contentPane, editor);
	}

	public String getMenuTitle() {
		return "ESP32 Partition Tool";
	}

	private void initGUI() {

		fileManager.loadProperties();

		// Create and show the JFrame
		if (frame == null) {
			//frame = new JFrame("ESP32 Partition Tool");
			frame = new JFrameWithBgImage();

			// Add UI to frame
			frame.addUI( contentPane );

			fileManager.setUIController( new UIController(contentPane, fileManager) );

			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					// save window dimensions
					fileManager.saveProperties();
					// Just hide the frame instead of disposing
					frame.setVisible(false);
				}
			});

			// Set frame position to the center of the screen
			frame.setLocationRelativeTo(null);
		} else {
			// If the frame is already open, bring it to the front and make it visible
			frame.toFront();
		}

		if( fileManager.canRun() ) {
			frame.setVisible(true);
			fileManager.loadCSV();
		}

	}

	public void run() {
		initGUI();
	}
}
