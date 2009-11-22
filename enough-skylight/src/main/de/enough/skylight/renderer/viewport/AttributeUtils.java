package de.enough.skylight.renderer.viewport;

import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NamedNodeMap;

public class AttributeUtils {
	public final static String getValue(DomNode node, String name) {
		NamedNodeMap nodeMap = node.getAttributes();
		Attr attribute = (Attr)nodeMap.getNamedItem(name); 
		if(attribute != null) {
			return attribute.getValue();
		} else {
			return null;
		}
	}
}
