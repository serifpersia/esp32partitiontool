package com.serifpersia.esp32partitiontool;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

import static javax.swing.text.html.HTML.Tag.IMPLIED;
import static javax.swing.text.html.HTML.Tag.P;


@SuppressWarnings("serial")
class AboutPanel extends JEditorPane {

	public AboutPanel() {
		createPanel();
	}

	private void createPanel() {

		setEditable(false);
		setEditorKit(new CustomHTMLEditorKit());
		setFont( UI.defaultFont.deriveFont(Font.PLAIN, 12) );
		setPreferredSize(new Dimension(400, 300));

		final String fontFace = "<font face=sans-serif>";
		final String boxpadding = "padding-top: 0px;padding-right: 10px;padding-bottom: 10px;padding-left: 10px;";
		final String titleSpanned = "<span style=\"background-color: #d7a631\">&nbsp;ESP32&nbsp;</span>"
				+ "<span style=\"background-color: #bf457a\">&nbsp;Partition&nbsp;</span>"
				+ "<span style=\"background-color: #42b0f5\">&nbsp;Tool&nbsp;</span>"
				+ "<span style=\"background-color: #9a41c2\">&nbsp;v1.4&nbsp;</span>";
		final String title = "<h2 align=center style=\"color: #ffffff;\">" + titleSpanned + "</h2>";
		final String description = "<p>"+l10n.getString("aboutPanel.description")+"</p>";
		final String projectlink = "<p><b>"+l10n.getString("aboutPanel.source")+":</b><br>https://github.com/serifpersia/esp32partitiontool</p>";
		final String copyright = "<p><b>Copyright (c) 2024 @serifpersia</b><br>https://github.com/serifpersia</p>";
		final String credits = "<p><b>"+l10n.getString("aboutPanel.contributors")+":</b><br>serifpersia, tobozo, SzyZuu, zeromem0</p>";
		final String message = "<html>" + fontFace + title + "<div style=\"" + boxpadding + "\">" + description + projectlink
				+ copyright + credits + "</div></html>";

		// fixes weird font problem with the standalone version
		putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		setContentType("text/html");
		setText(message);
	}

	class CustomHTMLEditorKit extends HTMLEditorKit {

		private final ViewFactory viewFactory = new HTMLFactory() {
			@Override
			public View create(Element elem) {
				AttributeSet attrs = elem.getAttributes();
				Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
				Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
				if (o instanceof Tag) {
					HTML.Tag kind = (HTML.Tag) o;
					if (IMPLIED == kind) return new WrappableParagraphView(elem); // <pre>
					if (P == kind) return new WrappableParagraphView(elem); // <p>
				}
				return super.create(elem);
			}
		};

		@Override
		public ViewFactory getViewFactory() {
			return this.viewFactory;
		}
	}

	class WrappableParagraphView extends javax.swing.text.html.ParagraphView {

		public WrappableParagraphView(Element elem) {
			super(elem);
		}

		@Override
		public float getMinimumSpan(int axis) {
			return View.X_AXIS == axis ? 0 : super.getMinimumSpan(axis);
		}
	}


}
