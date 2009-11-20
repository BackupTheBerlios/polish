package de.enough.skylight.renderer.view;

import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NamedNodeMap;

public class TypeUtils {
	public final static boolean isType(DomNode node, int type) {
		return node.getNodeType() == type;
	}
}
