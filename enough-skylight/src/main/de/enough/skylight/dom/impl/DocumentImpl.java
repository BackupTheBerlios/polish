package de.enough.skylight.dom.impl;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NodeList;

public class DocumentImpl extends DomNodeImpl implements Document {

	private HashMap elementsById;

	protected void init() {
		super.init(this, null, "_DOCUMENT", null, DOCUMENT_TYPE_NODE);
		// TODO: This data structure should be created in the parser run.
		this.elementsById = new HashMap();
		
		defineProperty("doctype", null, READONLY|PERMANENT);
		defineProperty("implementation", null, READONLY|PERMANENT);
		defineProperty("documentElement", null, READONLY|PERMANENT);
		defineProperty("createElement", null, EMPTY);
		defineProperty("createDocumentFragment", null, EMPTY);
		defineProperty("createTextNode", null, EMPTY);
		defineProperty("createComment", null, EMPTY);
		defineProperty("createCDATASection", null, EMPTY);
		defineProperty("createProcessingInstruction", null, EMPTY);
		defineProperty("createAttribute", null, EMPTY);
		defineProperty("createEntityReference", null, EMPTY);
		defineProperty("getElementsByTagName", null, EMPTY);
		defineProperty("importNode", null, EMPTY);
		defineProperty("createElementNS", null, EMPTY);
		defineProperty("createAttributeNS", null, EMPTY);
		defineProperty("getElementsByTagNameNS", null, EMPTY);
		defineProperty("getElementById", new BaseFunction() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				return elementsById.get(args[0]);
			}
		}, EMPTY);
	}
	
	public String getClassName() {
		return "Document";
	}
	
	public String toString() {
		return "Document";
	}

	public Object get(String name, Scriptable start) {
		if("documentElement".equals(name)) {
			int numberChildren = this.childList.getLength();
			// Return the first child which is not a processing instruction.
			// TODO: The handling of processing instructions in the DomParser is not existent. Implement something
			// to test a node if it is a processing instruction.
			for(int i = 0; i < numberChildren;i++) {
				DomNodeImpl child = this.childList.internalGetItem(i);
				if(child.getNodeType() == DomNode.ELEMENT_NODE) {
					return child;
				}				
			}
			return null;
		}
		// TODO Auto-generated method stub
		return super.get(name, start);
	}

	public DomNode createElement(String tagName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	void cacheNodeWithId(String id, DomNodeImpl domNode) {
		this.elementsById.put(id, domNode);
	}

	public void toXmlString(StringBuffer buffer) {
//		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		NodeList childNodes = this.getChildNodes();
		if(childNodes.getLength() > 0) {
			childNodes.item(0).toXmlString(buffer);
			
		}
	}
	
}
