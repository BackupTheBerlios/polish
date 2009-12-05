package de.enough.skylight.renderer.viewport;

import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NamedNodeMap;

public class NodeUtils {
	public final static String getAttributeValue(DomNode node, String name) {
		NamedNodeMap nodeMap = node.getAttributes();
		Attr attribute = (Attr)nodeMap.getNamedItem(name); 
		if(attribute != null) {
			return attribute.getValue();
		} else {
			return null;
		}
	}
}
