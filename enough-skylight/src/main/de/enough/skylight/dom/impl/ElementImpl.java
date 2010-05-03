package de.enough.skylight.dom.impl;


import org.mozilla.javascript.Scriptable;

import de.enough.skylight.Services;
import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DOMException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Element;
import de.enough.skylight.dom.MutationEvent;

public class ElementImpl extends DomNodeImpl implements Element{

	private Scriptable scriptableObject;
	private String tagName;
	private NamedNodeMapImpl attributes;
	private String value;

	@Override
	public Scriptable getScriptable() {
		if(this.scriptableObject == null) {
			this.scriptableObject = Services.getInstance().getJsEngine().newObject("Element", new Object[] {this});
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
	public NamedNodeMapImpl getAttributes() {
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

	public AttrImpl getAttributeNode(String name) {
		return (AttrImpl) this.attributes.getNamedItem(name);
	}

	public NodeListImpl getElementsByTagName(String name) {
		throw new RuntimeException("Method not implemented.");
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
		throw new RuntimeException("Method not implemented.");
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
		throw new RuntimeException("Method not implemented.");
	}

	@Override
	public boolean hasScriptable() {
		return this.scriptableObject != null;
	}

	public String getNodeName() {
		return this.tagName;
	}

	public String getNodeValue() throws DOMException {
		return this.value;
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		String oldValue = this.value;
		if((oldValue == null && nodeValue != null) || (oldValue != null &&  ! oldValue.equals(nodeValue))) {
			this.value = nodeValue;
			Services.getInstance().getEventEmitter().emitDomCharacterDataModifiedEvent(this, oldValue, nodeValue);
		}
	}
	
	@Override
	protected String getAdditionalProperties() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("name='");
		buffer.append(this.tagName);
		buffer.append("'");
		return buffer.toString();
	}
	
}
