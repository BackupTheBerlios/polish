package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.Element;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.dom.impl.rhino.ElementScriptableObject;

public class ElementImpl extends DomNodeImpl implements Element{

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

	public String getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Attr getAttributeNode(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeList getElementsByTagName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTagName() {
		// TODO: What about uppercase names which are cannonical for html4
		return getNodeName();
	}

	public boolean hasAttribute(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeAttribute(String name) throws DomException {
		// TODO Auto-generated method stub
		
	}

	public Attr removeAttributeNode(Attr oldAttr) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAttribute(String name, String value) throws DomException {
		
	}

	public Attr setAttributeNode(Attr newAttr) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
