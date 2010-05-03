package de.enough.skylight.js.scriptableobjects;


import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.enough.skylight.dom.DOMException;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.dom.impl.NamedNodeMapImpl;

public class NamedNodeMapScriptableObject extends ScriptableObject {

	private NamedNodeMapImpl namedNodeMap;

	public void init(NamedNodeMapImpl namedNodeMap) {
		this.namedNodeMap = namedNodeMap;
		
		defineProperty("length", new Integer(0), READONLY|PERMANENT);
		defineProperty("getNamedItem", null, PERMANENT);
		defineProperty("setNamedItem", new BaseFunction() {
			@Override
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				setNamedItem((DomNodeImpl)args[0]);
				return thisObj;
			}
		}, PERMANENT);
		defineProperty("removeNamedItem", null, PERMANENT);
		defineProperty("item", new BaseFunction() {
			@Override
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				if(args.length == 0) {
					throw new DOMException(DOMException.INVALID_ARGUMENT_ERR,"This method needs one numeric parameter.");
				}
				Object object = args[0];
				if(object instanceof Double) {
					return item(((Double)object).intValue());
				}
				throw new DOMException(DOMException.INVALID_ARGUMENT_ERR,"Unknown type:"+object.getClass());
			}
		}, PERMANENT);
		defineProperty("getNamedItemNS", null, PERMANENT);
		defineProperty("setNamedItemNS", null, PERMANENT);
		defineProperty("removeNamedItemNS", null, PERMANENT);
	}
	
	protected Object item(int index) {
		return this.namedNodeMap.item(index);
	}

	protected void setNamedItem(DomNodeImpl domNodeImpl) {
		this.namedNodeMap.setNamedItem(domNodeImpl);
	}

	@Override
	public String getClassName() {
		return "NamedNodeMap";
	}

	@Override
	public Object get(String name, Scriptable start) {
		if("length".equals(name)) {
			return new Integer(this.namedNodeMap.getLength());
		} else {
			return super.get(name, start);
		}
	}
}
