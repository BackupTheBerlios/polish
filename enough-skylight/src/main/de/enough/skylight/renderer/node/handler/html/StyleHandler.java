package de.enough.skylight.renderer.node.handler.html;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.css.HtmlCssInterpreter;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeUtils;

public class StyleHandler extends HeadNodeHandler {
	
	static String TYPE_TEXT_CSS = "text/css";

	public String getTag() {
		return "style";
	}

	public void handleNode(CssElement element) {
		DomNode node = element.getNode();
		String type = NodeUtils.getAttributeValue(node, "type");
		
		if (type != null && type.equals(TYPE_TEXT_CSS)) {
			DomNode firstChild = element.getNode().getFirstChild();
			
			if(firstChild.getNodeType() == DomNode.TEXT_NODE) {
				String text = firstChild.getNodeValue();
				
				byte[] data = text.getBytes();
				ByteArrayInputStream stream = new ByteArrayInputStream(data);
			
				InputStreamReader reader = new InputStreamReader(stream);
				
				HtmlCssInterpreter interpreter = new HtmlCssInterpreter(reader);
				Hashtable styles = interpreter.getAllStyles();
	
				Enumeration elements = styles.elements();
				while (elements.hasMoreElements()) {
					Style style = (Style) elements.nextElement();
					StyleSheet.getStyles().put(style.name, style);
				}
			}
		}
	}

	public void setContent(CssElement element, Item item) {}
}
