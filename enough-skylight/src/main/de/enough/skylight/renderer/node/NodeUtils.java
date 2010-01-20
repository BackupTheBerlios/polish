package de.enough.skylight.renderer.node;

import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NamedNodeMap;

public class NodeUtils {
	public final static String getAttributeValue(DomNode node, String name) {
		NamedNodeMap nodeMap = node.getAttributes();
		
		if(nodeMap != null) {
			Attr attribute = (Attr)nodeMap.getNamedItem(name); 
			if(attribute != null) {
				return attribute.getValue();
			} 
		}
		
		return null;
	}
}
