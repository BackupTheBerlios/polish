package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DOMException;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Element;
import de.enough.skylight.dom.Event;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.js.DocumentScriptableObject;

public class DocumentImpl extends DomNodeImpl implements Document {

	private HashMap elementsById = new HashMap();
	private DocumentScriptableObject scriptableObject;
	
	public void init() {
		super.init(null, null, DOCUMENT_NODE);
	}
	
	protected void cacheNodeWithId(String id, DomNodeImpl domNode) {
		this.elementsById.put(id, domNode);
	}
	
	public Attr createAttribute(String name) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public Element createElement(String tagName) {
		ElementImpl elementImpl = new ElementImpl();
		elementImpl.init(this, null, tagName, null, DomNode.ELEMENT_NODE);
		return elementImpl;
	}

	public Event createEvent(String eventType) throws DOMException {
		// TODO: Using this API for generating interal events is not nice as the caller need to cast and he has to do the dispatch
		// himself.
		if("DOMAttrModified".equals(eventType)) {
//			MutationEventImpl event = new MutationEventImpl();
//			event.ini
		}
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

	public String getNodeName() {
		return "#document";
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
	public boolean hasScriptable() {
		return this.scriptableObject != null;
	}

	public String getNodeValue() throws DOMException {
		return null;
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		// Has no effect.
	}
}
