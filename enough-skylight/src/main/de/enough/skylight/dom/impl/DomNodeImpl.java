package de.enough.skylight.dom.impl;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NamedNodeMap;
import de.enough.skylight.dom.NodeList;

public class DomNodeImpl extends ScriptableObject implements DomNode {

	final class AppendChildFunction extends BaseFunction {
		public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
			return appendChild((DomNode) args[0]);
		}
	}
	final class ReplaceChildFunction extends BaseFunction {
		public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
			//TODO: Must the argument property be set in these call methods?
			return replaceChild((DomNodeImpl)args[0], (DomNodeImpl)args[1]);
		}
	}
	private DomNodeImpl parent;
	private String name;
	private NamedNodeMapImpl attributes;
	private int type;
	private NodeListImpl childList;
	private String text;
	private Document document;
	private String value;

	protected void init(Document document, DomNodeImpl parent, String name, NamedNodeMap attributes, int type) {
		this.document = document;
		this.parent = parent;

        if(this.parent != null) {
            this.parent.doAppendChild(this);
        }

        this.name = name;
        if(attributes == null) {
        	this.attributes = new NamedNodeMapImpl();
        	this.attributes.init();
        } else {
        	this.attributes = (NamedNodeMapImpl)attributes;
        }
        this.type = type;
        this.childList = new NodeListImpl();
        // TODO: Add all the other constants.
        putConst("ELEMENT_NODE", this, new Integer(5));
		defineProperty("nodeName", this.name, READONLY|PERMANENT);
		defineProperty("nodeValue", null, PERMANENT);
		defineProperty("nodeType", new Integer(this.type), READONLY|PERMANENT);
		defineProperty("parentNode", this.parent, READONLY|PERMANENT);
		defineProperty("childNodes", this.childList, READONLY|PERMANENT);
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
		defineProperty("replaceChild", new ReplaceChildFunction(), PERMANENT);
		defineProperty("removeChild", null, PERMANENT);
		defineProperty("appendChild", new AppendChildFunction(), PERMANENT);
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
	private void doAppendChild(DomNodeImpl childNode) {
		this.childList.add(childNode);
	}
	//TODO: Should every property be PERMANENT?
	public String getClassName() {
		// TODO: What classname should a ScriptableDomNode have?
		return "Node";
	}
	public DomNode appendChild(DomNode newChild) throws DomException {
		DomNodeImpl newChildImpl = (DomNodeImpl)newChild;
		this.childList.add(newChildImpl);
		newChildImpl.doSetParent(this);
		return newChild;
	}
	private void doSetParent(DomNodeImpl domNodeImpl) {
		this.parent = domNodeImpl;
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
		return this.childList;
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
		return this.value;
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
		this.childList.replace((DomNodeImpl)newChild, (DomNodeImpl)oldChild);
		return oldChild;
	}
	public void setNodeValue(String nodeValue) throws DomException {
		this.value = nodeValue;
	}
	public void setPrefix(String prefix) throws DomException {
		// TODO Auto-generated method stub
		
	}
	public void toXmlString(StringBuffer buffer) {
		if(this.type == TEXT_NODE) {
			buffer.append(getNodeValue());
			return;
		}
		buffer.append("<");
		buffer.append(this.name);
		int numberAttributes = this.attributes.getLength();
		if(numberAttributes > 0) {
			for(int i = 0; i < numberAttributes; i++) {
				AttrImpl attr = (AttrImpl)this.attributes.item(i);
				buffer.append(" ");
				attr.toXmlString(buffer);
			}
		}
		buffer.append(">");
		int numberChildren = this.childList.getLength();
		for(int i = 0; i < numberChildren; i++) {
			DomNode childNode = this.childList.item(i);
			childNode.toXmlString(buffer);
		}
		buffer.append("</");
		buffer.append(this.name);
		buffer.append(">");
	}
	
	public String toString() {
		return this.name;
	}
	public void put(String name, Scriptable start, Object value) {
//		System.out.println("DomNodeImpl.put(name, start, value): Enter. Name:"+name+"X");
		super.put(name, start, value);
	}
	public Object get(String name, Scriptable start) {
		if("parentNode".equals(name)) {
			return this.parent;
		}
		return super.get(name, start);
	}

}