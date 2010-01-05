package de.enough.skylight.dom.impl;


import org.mozilla.javascript.Scriptable;

import de.enough.skylight.Services;
import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DOMException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Element;
import de.enough.skylight.dom.MutationEvent;
import de.enough.skylight.dom.NamedNodeMap;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.js.ElementScriptableObject;

public class ElementImpl extends DomNodeImpl implements Element{

	private ElementScriptableObject scriptableObject;
	private String tagName;
	private NamedNodeMapImpl attributes;

	@Override
	public Scriptable getScriptable() {
		if(this.scriptableObject == null) {
			this.scriptableObject = new ElementScriptableObject();
			this.scriptableObject.init(this);
		}
		return this.scriptableObject;
	}

	// TODO: Do sanity checks on all init methods.
	public void init(DocumentImpl document, DomNodeImpl parent, String tagName, NamedNodeMapImpl attributes, int type) {
		super.init(document, parent, type);
		this.tagName = tagName;
		this.attributes = attributes;
	}

	@Override
	public NamedNodeMap getAttributes() {
		return this.attributes;
	}

	@Override
	public boolean hasAttributes() {
		return this.attributes.getLength() > 0;
	}

	public String getAttribute(String name) {
		DomNode attributeNode = this.attributes.getNamedItem(name);
		if(attributeNode == null) {
			return "";
		}
		return attributeNode.getNodeValue();
	}

	public Attr getAttributeNode(String name) {
		return (Attr)this.attributes.getNamedItem(name);
	}

	public NodeList getElementsByTagName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTagName() {
		return this.tagName;
	}

	public boolean hasAttribute(String name) {
		return this.attributes.getNamedItem(name) != null;
	}

	public void removeAttribute(String name) throws DOMException {
		this.attributes.removeNamedItem(name);
	}

	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAttribute(String name, String value) throws DOMException {
		AttrImpl attribute = (AttrImpl)this.attributes.getNamedItem(name);
		String previousValue;
		if(attribute == null) {
			attribute = new AttrImpl();
			attribute.init(this.ownerDocument,this, name, value);
			previousValue = null;
		} else {
			previousValue = attribute.getValue();
			attribute.setNodeValue(value);
		}
		this.attributes.setNamedItem(attribute);
		Services.getInstance().getEventEmitter().emitDomAttrModifiedEvent(this, this, previousValue, value, name, MutationEvent.MODIFICATION);
	}

	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasScriptable() {
		return this.scriptableObject != null;
	}

	public String getNodeName() {
		return this.tagName;
	}

	public String getNodeValue() throws DOMException {
		return null;
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		// Has no effect.
	}
	
	@Override
	protected void toStringOfProperties(StringBuffer buffer) {
		buffer.append("name='");
		buffer.append(this.tagName);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Element:[");
		toStringOfProperties(buffer);
		buffer.append(",");
		super.toStringOfProperties(buffer);
		return buffer.toString();
	}
	
}
