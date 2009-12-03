package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Element;
import de.enough.skylight.dom.Event;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.dom.impl.rhino.DocumentScriptableObject;

public class DocumentImpl extends DomNodeImpl implements Document {

	private HashMap elementsById = new HashMap();
	private DocumentScriptableObject scriptableObject;
	
	public Element createElement(String tagName) {
		ElementImpl elementImpl = new ElementImpl();
		elementImpl.init(this, null, tagName, null, DomNode.ELEMENT_NODE);
		return elementImpl;
	}
	
	protected void cacheNodeWithId(String id, DomNodeImpl domNode) {
		this.elementsById.put(id, domNode);
	}

	@Override
	public void toXmlString(StringBuffer buffer) {
//		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		NodeList childNodes = this.getChildNodes();
		if(childNodes.getLength() > 0) {
			childNodes.item(0).toXmlString(buffer);
			
		}
	}

	public Attr createAttribute(String name) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}

	public Element getDocumentElement() {
		return (Element)getLastChild();
	}

	public Element getElementById(String elementId) {
		return (Element) this.elementsById.get(elementId);
	}

	public NodeList getElementsByTagName(String tagname) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Scriptable getScriptable() {
		if(this.scriptableObject == null) {
			this.scriptableObject = new DocumentScriptableObject();
			this.scriptableObject.init(this);
		}
		return this.scriptableObject;
	}

	@Override
	public Event createEvent(String eventType) throws DomException {
		// TODO: Using this API for generating interal events is not nice as the caller need to cast and he has to do the dispatch
		// himself.
		if("DOMAttrModified".equals(eventType)) {
//			MutationEventImpl event = new MutationEventImpl();
//			event.ini
		}
		return null;
	}
	
	@Override
	public boolean hasScriptable() {
		return this.scriptableObject != null;
	}
}
