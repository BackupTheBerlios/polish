package de.enough.skylight.dom.impl;

import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.DOMException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NamedNodeMap;

public class NamedNodeMapImpl implements NamedNodeMap{
	
	protected DomNode parent;
	protected HashMap hashMap = new HashMap();

	public int getLength() {
		return this.hashMap.size();
	}

	// TODO: Here a Attr object must be created.
	public DomNode getNamedItem(String name) {
		return (DomNode)this.hashMap.get(name);
	}

	public DomNode item(int index) {
		Object[] values = this.hashMap.values();
		if(index >= values.length) {
			return null;
		}
		return (DomNode)values[index];
	}

	public DomNode removeNamedItem(String name) throws DOMException {
		// TODO Auto-generated method stub
		throw new RuntimeException("This method is not yet implemented");
	}

	public DomNode setNamedItem(DomNode domNode) throws DOMException {
		return (DomNode)this.hashMap.put(domNode.getNodeName(), domNode);
	}

	public void putAttribute(String attributeName, String attributeValue) {
		this.hashMap.put(attributeName,attributeValue);
	}

}
