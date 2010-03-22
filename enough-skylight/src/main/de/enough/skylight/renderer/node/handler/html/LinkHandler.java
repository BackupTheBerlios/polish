package de.enough.skylight.renderer.node.handler.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.skylight.content.ContentUtil;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.ViewportContext;
import de.enough.skylight.renderer.css.HtmlCssInterpreter;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.NodeUtils;

public class LinkHandler extends HeadNodeHandler {
	final static String PREFIX_HTTP = "http://";
	final static String PREFIX_RESOURCES = "resources://";
	
	static String REL_STYLESHEET = "stylesheet";
	static String TYPE_TEXT_CSS = "text/css";

	public String getTag() {
		return "link";
	}

	public void handleNode(CssElement element) {
		DomNode node = element.getNode();
		String rel = NodeUtils.getAttributeValue(node, "rel");
		String type = NodeUtils.getAttributeValue(node, "type");
		String href = NodeUtils.getAttributeValue(node, "href");
		ViewportContext context = element.getViewport().getContext();

		if (rel != null && rel.equals(REL_STYLESHEET) && type != null
				&& type.equals(TYPE_TEXT_CSS) && href != null) {

			try {
				String url = ContentUtil.completeUrl(href, context);
				InputStream stream = ContentUtil.getStream(url);
				
				InputStreamReader reader = new InputStreamReader(stream);

				HtmlCssInterpreter interpreter = new HtmlCssInterpreter(reader);
				Hashtable styles = interpreter.getAllStyles();

				Enumeration elements = styles.elements();
				while (elements.hasMoreElements()) {
					Style style = (Style) elements.nextElement();
					StyleSheet.getStyles().put(style.name, style);
				}

			} catch (IOException e) {
				//#debug error
				System.out.println("unable to open stylesheet " + e);
				e.printStackTrace();
			}
		}
	}

	public void setContent(CssElement element, Item item) {}
}
