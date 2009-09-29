package de.enough.skylight.dom.impl;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.DomException;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.NamedNodeMap;

public class NamedNodeMapImpl extends ScriptableObject implements NamedNodeMap{
	
	protected DomNode parent;
	protected HashMap hashMap;

	public void init() {
		this.hashMap = new HashMap();
		
		defineProperty("length", new Integer(0), READONLY|PERMANENT);
		defineProperty("getNamedItem", null, PERMANENT);
		defineProperty("setNamedItem", new BaseFunction() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				// TODO: Do sanity checks of the parameters here.
				setNamedItem((DomNode)args[0]);
				return thisObj;
			}
		}, PERMANENT);
		defineProperty("removeNamedItem", null, PERMANENT);
		defineProperty("item", new BaseFunction() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				if(args.length == 0) {
					throw new DomException();
				}
				Object object = args[0];
				if(object instanceof Double) {
					return item(((Double)object).intValue());
				}
				throw new DomException("Unknown type:"+object.getClass());
			}
		}, PERMANENT);
		defineProperty("getNamedItemNS", null, PERMANENT);
		defineProperty("setNamedItemNS", null, PERMANENT);
		defineProperty("removeNamedItemNS", null, PERMANENT);
	}
	
	public int getLength() {
		return this.hashMap.size();
	}

	// TODO: Here a Attr object must be created.
	public DomNode getNamedItem(String name) {
		return (DomNode)this.hashMap.get(name);
	}

	public DomNode getNamedItemNS(String namespaceURI, String localName) throws DomException {
		throw new RuntimeException("This method is not yet implemented");
	}

	public DomNode item(int index) {
		Object[] values = this.hashMap.values();
		if(index >= values.length) {
			return null;
		}
		return (DomNode)values[index];
	}

	public DomNode removeNamedItem(String name) throws DomException {
		// TODO Auto-generated method stub
		throw new RuntimeException("This method is not yet implemented");
	}

	public DomNode removeNamedItemNS(String namespaceURI, String localName) throws DomException {
		// TODO Auto-generated method stub
		throw new RuntimeException("This method is not yet implemented");
	}

	public DomNode setNamedItem(DomNode domNode) throws DomException {
		return (DomNode)this.hashMap.put(domNode.getNodeName(), domNode);
	}

	public DomNode setNamedItemNS(DomNode arg) throws DomException {
		// TODO Auto-generated method stub
		throw new RuntimeException("This method is not yet implemented");
	}

	public void putAttribute(String attributeName, String attributeValue) {
		this.hashMap.put(attributeName,attributeValue);
	}

	public String getClassName() {
		return "NamedNodeMap";
	}

	public Object get(String name, Scriptable start) {
		if("length".equals(name)) {
			return new Double(this.hashMap.size());
		} else {
			return super.get(name, start);
		}
	}

	
}
