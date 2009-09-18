package de.enough.skylight.dom.impl;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NamedNodeMap;
import de.enough.skylight.dom.NodeList;

public class DomNodeImpl extends ScriptableObject implements DomNode {

	private DomNode parent;
	private String name;
	private NamedNodeMap attributes;
	private int type;
	private ArrayList childList;
	private String text;
	private Document document;

	protected void init(Document document, DomNode parent, String name, NamedNodeMap attributes, int type) {
		this.document = document;
		this.parent = parent;

        if(this.parent != null) {
            this.parent.appendChild(this);
        }

        this.name = name;
        if(attributes == null) {
        	this.attributes = new NamedNodeMapImpl();
        } else {
        	this.attributes = attributes;
        }
        this.type = type;
        this.childList = new ArrayList(); 
        
        putConst("ELEMENT_NODE", this, new Integer(5));
		defineProperty("nodeName", this.name, READONLY|PERMANENT);
		defineProperty("nodeValue", null, PERMANENT);
		defineProperty("nodeType", new Integer(this.type), READONLY|PERMANENT);
		defineProperty("parentNode", null, READONLY|PERMANENT);
		defineProperty("childNodes", null, READONLY|PERMANENT);
		defineProperty("firstChild", null, READONLY|PERMANENT);
		defineProperty("lastChild", null, READONLY|PERMANENT);
		defineProperty("previousSibling", null, READONLY|PERMANENT);
		defineProperty("nextSibling", null, READONLY|PERMANENT);
		defineProperty("attributes", this.attributes, READONLY|PERMANENT);
		defineProperty("ownerDocument", null, READONLY|PERMANENT);
		defineProperty("namespaceURI", null, READONLY|PERMANENT);
		defineProperty("prefix", null, PERMANENT);
		defineProperty("localName", null, READONLY|PERMANENT);
		defineProperty("insertBefore", null, PERMANENT);
		defineProperty("replaceChild", null, PERMANENT);
		defineProperty("removeChild", null, PERMANENT);
		defineProperty("appendChild", new BaseFunction() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				// TODO:
				return appendChild((DomNode) args[0]);
			}
			
		}, PERMANENT);
		defineProperty("hasChildNodes", null, PERMANENT);
		defineProperty("cloneNode", null, PERMANENT);
		defineProperty("normalize", null, PERMANENT);
		defineProperty("isSupported", null, PERMANENT);
		defineProperty("hasAttributes", new BaseFunction() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				return new Boolean(hasAttributes());
			}
			
		}, PERMANENT);
	}
	//TODO: Should every property be PERMANENT?
	public String getClassName() {
		// TODO: What classname should a ScriptableDomNode have?
		return "Node";
	}
	public DomNode appendChild(DomNode newChild) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}
	public DomNode cloneNode(boolean deep) {
		// TODO Auto-generated method stub
		return null;
	}
	public NamedNodeMap getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}
	public NodeList getChildNodes() {
		// TODO Auto-generated method stub
		return null;
	}
	public DomNode getFirstChild() {
		// TODO Auto-generated method stub
		return null;
	}
	public DomNode getLastChild() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getNamespaceURI() {
		// TODO Auto-generated method stub
		return null;
	}
	public DomNode getNextSibling() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getNodeName() {
		return this.name;
	}
	public int getNodeType() {
		return this.type;
	}
	public String getNodeValue() throws DomException {
		// TODO Auto-generated method stub
		return null;
	}
	public Document getOwnerDocument() {
		return this.document;
	}
	public DomNode getParentNode() {
		return this.parent;
	}
	public String getPrefix() {
		// TODO Auto-generated method stub
		return null;
	}
	public DomNode getPreviousSibling() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean hasAttributes() {
		return this.attributes.getLength() > 0;
	}
	public boolean hasChildNodes() {
		// TODO Auto-generated method stub
		return false;
	}
	public DomNode insertBefore(DomNode newChild, DomNode refChild) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isSupported(String feature, String version) {
		// TODO Auto-generated method stub
		return false;
	}
	public void normalize() {
		// TODO Auto-generated method stub
		
	}
	public DomNode removeChild(DomNode oldChild) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}
	public DomNode replaceChild(DomNode newChild, DomNode oldChild) throws DomException {
		// TODO Auto-generated method stub
		return null;
	}
	public void setNodeValue(String nodeValue) throws DomException {
		// TODO Auto-generated method stub
		
	}
	public void setPrefix(String prefix) throws DomException {
		// TODO Auto-generated method stub
		
	}
	public String toXmlString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString() {
		return this.name;
	}

}
