package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.skylight.dom.impl.rhino.ElementScriptableObject;

public class ElementImpl extends DomNodeImpl{

	private ElementScriptableObject scriptableObject;

	@Override
	public Scriptable getScriptable() {
		if(this.scriptableObject == null) {
			this.scriptableObject = new ElementScriptableObject();
			this.scriptableObject.init(this);
		}
		return this.scriptableObject;
	}

	@Override
	public void init(DocumentImpl document, DomNodeImpl parent, String name, NamedNodeMapImpl attributes, int type) {
		super.init(document, parent, name, attributes, type);
	}
	
}
