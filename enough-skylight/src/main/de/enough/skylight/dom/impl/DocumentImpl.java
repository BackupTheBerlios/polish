package de.enough.skylight.dom.impl;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;

public class DocumentImpl extends DomNodeImpl implements Document {

	private DomNodeImpl documentElement;
	private HashMap elementsById;

	protected void init(DomNodeImpl parent) {
		this.documentElement = parent;
		// TODO: This data structure should be created in the parser run.
		this.elementsById = new HashMap();
		
		defineProperty("doctype", null, READONLY|PERMANENT);
		defineProperty("implementation", null, READONLY|PERMANENT);
		defineProperty("documentElement", this.documentElement, READONLY|PERMANENT);
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

	public DomNode createElement(String tagName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	void cacheNodeWithId(String id, DomNodeImpl domNode) {
		DomNode parentNode = domNode.getParentNode();
		this.elementsById.put(id, parentNode);
	}
	
}
