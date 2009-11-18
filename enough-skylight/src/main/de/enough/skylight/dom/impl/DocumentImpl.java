package de.enough.skylight.dom.impl;

import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.Attr;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.Element;
import de.enough.skylight.dom.NodeList;
import de.enough.skylight.dom.impl.rhino.DocumentScriptableObject;

public class DocumentImpl extends DomNodeImpl implements Document {

	private HashMap elementsById = new HashMap();
	private DocumentScriptableObject scriptableObject;
	
	public Element createElement(String tagName) {
		// TODO Auto-generated method stub
		return null;
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
	
}
