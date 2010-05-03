package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.DOMException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NamedNodeMap;
import de.enough.skylight.js.scriptableobjects.NamedNodeMapScriptableObject;

public class NamedNodeMapImpl implements NamedNodeMap, ScriptableAdapter{
	
	protected DomNodeImpl parent;
	protected HashMap hashMap = new HashMap();
	private NamedNodeMapScriptableObject scriptableObject;

	public int getLength() {
		return this.hashMap.size();
	}

	public DomNodeImpl getNamedItem(String name) {
		return (DomNodeImpl) this.hashMap.get(name);
	}

	// TODO: The internal storage must not be a HashMap as the ordering of items is not stable.
	public DomNodeImpl item(int index) {
		Object[] values = this.hashMap.values();
		if(index >= values.length) {
			return null;
		}
		return (DomNodeImpl)values[index];
	}

	public DomNodeImpl removeNamedItem(String name) throws DOMException {
		// TODO Auto-generated method stub
		throw new RuntimeException("This method is not yet implemented");
	}

	public DomNodeImpl setNamedItem(DomNode domNode) throws DOMException {
		return (DomNodeImpl)this.hashMap.put(domNode.getNodeName(), domNode);
	}

	public void putAttribute(String attributeName, String attributeValue) {
		this.hashMap.put(attributeName,attributeValue);
	}
	
	public Scriptable getScriptable() {
		if(this.scriptableObject == null) {
			this.scriptableObject = new NamedNodeMapScriptableObject();
			this.scriptableObject.init(this);
		}
		return this.scriptableObject;
	}

	public boolean hasScriptable() {
		return this.scriptableObject != null;
	}

}
