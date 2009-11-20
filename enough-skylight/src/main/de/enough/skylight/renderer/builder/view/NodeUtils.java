package de.enough.skylight.renderer.builder.view;

import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NamedNodeMap;

public class NodeUtils {
	public final static String getAttributeValue(DomNode node, String name) {
		NamedNodeMap nodeMap = node.getAttributes();
		Attr attribute = (Attr)nodeMap.getNamedItem(name); 
		return attribute.getValue();
	}
	
	public final static boolean isType(DomNode node, int type) {
		return node.getNodeType() == type;
	}
}
