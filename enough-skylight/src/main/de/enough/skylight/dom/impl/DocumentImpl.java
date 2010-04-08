package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.DOMException;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.js.DocumentScriptableObject;

public class DocumentImpl extends DomNodeImpl implements Document,ScriptableAdapter {

	private HashMap elementsById = new HashMap();
	private DocumentScriptableObject scriptableObject;
	
	public void init() {
		super.init(null, null, DOCUMENT_NODE);
	}
	
	protected void cacheNodeWithId(String id, ElementImpl domNode) {
		this.elementsById.put(id, domNode);
	}
	
	public Attr createAttribute(String name) throws DOMException {
		// TODO Auto-generated method stub
		return null;
	}

	public ElementImpl createElement(String tagName) {
		ElementImpl element = new ElementImpl();
		element.init(this, null, tagName, null, DomNode.ELEMENT_NODE);
		return element;
	}

	public EventImpl createEvent(String eventType) throws DOMException {
		// TODO: Using this API for generating interal events is not nice as the caller need to cast and he has to do the dispatch
		// himself.
		if("DOMAttrModified".equals(eventType)) {
//			MutationEventImpl event = new MutationEventImpl();
//			event.ini
		}
		return null;
	}

	public ElementImpl getDocumentElement() {
		int numberOfChildren = this.childList.getLength();
		for(int i = numberOfChildren -1; i >= 0;i--) {
			DomNodeImpl domNodeImpl = this.childList.item(i);
			if(domNodeImpl instanceof ElementImpl) {
				return (ElementImpl)domNodeImpl;
			}
		}
		return null;
		
	}

	public ElementImpl getElementById(String elementId) {
		return (ElementImpl)this.elementsById.get(elementId);
	}
	
	public NodeListImpl getElementsByTagName(String tagname) {
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

	@Override
	protected String getAdditionalProperties() {
		return null;
	}
}
