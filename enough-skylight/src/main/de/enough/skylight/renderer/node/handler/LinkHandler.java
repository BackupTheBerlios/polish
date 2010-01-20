package de.enough.skylight.renderer.node.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import de.enough.polish.browser.css.CssInterpreter;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.css.HtmlCssInterpreter;
import de.enough.skylight.renderer.node.NodeHandler;
import de.enough.skylight.renderer.node.NodeUtils;

public class LinkHandler extends NodeHandler{
	
	static String REL_STYLESHEET = "stylesheet";
	static String TYPE_TEXT_CSS = "text/css";

	public String getTag() {
		return "link";
	}
	
	public Item createContent(DomNode node) {
		return null;
	}

	public void handleNode(DomNode node) {
		String rel = NodeUtils.getAttributeValue(node, "rel");
		String type = NodeUtils.getAttributeValue(node, "type");
		String href = NodeUtils.getAttributeValue(node, "href");
		
		if(	rel != null && rel.equals(REL_STYLESHEET) && 
			type != null && type.equals(TYPE_TEXT_CSS) &&
			href != null) {
			
			try {
				HttpConnection connection = (HttpConnection)Connector.open(href);
				InputStream stream = connection.openInputStream();
				InputStreamReader reader = new InputStreamReader(stream);
				
				HtmlCssInterpreter interpreter = new HtmlCssInterpreter(reader);
				Hashtable styles = interpreter.getAllStyles();
				
				Enumeration elements = styles.elements();
				while(elements.hasMoreElements()) {
					Style style = (Style)elements.nextElement();
					addToStylesheet(StyleSheet.getStyles(), style);
				}
				
			} catch (IOException e) {
				//#debug error
				System.out.println("unable to open stylesheet " + e);
				e.printStackTrace();
			}
		}
	}
	
	void addToStylesheet(Hashtable styles, Style style) {
		styles.put(style.name, style);
	}
}
