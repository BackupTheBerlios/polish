package de.enough.skylight.dom.impl;


import org.mozilla.javascript.Scriptable;
import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Element;
import de.enough.skylight.dom.MutationEvent;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.js.ElementScriptableObject;

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
		DomNode attributeNode = getAttributes().getNamedItem(name);
		if(attributeNode == null) {
			return "";
		}
		return attributeNode.getNodeValue();
	}

	public Attr getAttributeNode(String name) {
		return (Attr)getAttributes().getNamedItem(name);
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
		return getAttributes().getNamedItem(name) != null;
	}

	public void removeAttribute(String name) throws DomException {
		getAttributes().removeNamedItem(name);
	}

	public Attr removeAttributeNode(Attr oldAttr) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAttribute(String name, String value) throws DomException {
		AttrImpl attribute = (AttrImpl)getAttributes().getNamedItem(name);
		String previousValue;
		if(attribute == null) {
			attribute = new AttrImpl();
			attribute.init(this, name, value);
			previousValue = null;
		} else {
			previousValue = attribute.getValue();
			attribute.setNodeValue(value);
		}
		getAttributes().setNamedItem(attribute);
		EventEmitter.getInstance().fireDomAttrModifiedEvent(this, this, previousValue, value, name, MutationEvent.MODIFICATION);
	}

	public Attr setAttributeNode(Attr newAttr) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasScriptable() {
		return this.scriptableObject != null;
	}
	
}
