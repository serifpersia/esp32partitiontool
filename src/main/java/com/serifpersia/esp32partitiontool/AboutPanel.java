package com.serifpersia.esp32partitiontool;

import javax.swing.*;

@SuppressWarnings("serial")
class AboutPanel extends JEditorPane {

	public AboutPanel() {
		createPanel();
	}

	private void createPanel() {
		final String boxpadding = "padding-top: 0px;padding-right: 10px;padding-bottom: 10px;padding-left: 10px;";
		final String titleSpanned = "<span style=\"background-color: #d7a631\">&nbsp;ESP32&nbsp;</span>"
				+ "<span style=\"background-color: #bf457a\">&nbsp;Partition&nbsp;</span>"
				+ "<span style=\"background-color: #42b0f5\">&nbsp;Tool&nbsp;</span>"
				+ "<span style=\"background-color: #9a41c2\">&nbsp;v1.3&nbsp;</span>";
		final String title = "<h2 align=center style=\"color: #ffffff;\">" + titleSpanned + "</h2>";
		final String description = "<p>The ESP32 Partition Tool is a utility designed to ease the manipulation<br>"
				+ "of custom partition schemes in the Arduino IDE 1.8.x & PlatformIO environment.<br>"
				+ "This tool aims to simplify the process of creating custom partition<br>"
				+ "schemes for ESP32 projects.</p>";
		final String projectlink = "<p><b>Source:</b><br>https://github.com/serifpersia/esp32partitiontool</p>";
		final String copyright = "<p><b>Copyright (c) 2024 @serifpersia</b><br>https://github.com/serifpersia</p>";
		final String credits = "<p><b>Contributors:</b><br>serifpersia, tobozo</p>";
		final String message = "<html>" + title + "<div style=\"" + boxpadding + "\">" + description + projectlink
				+ copyright + credits + "</div></html>";

		setContentType("text/html");
		setText(message);
	}

}
