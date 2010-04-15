package de.enough.skylight.js;


import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.enough.skylight.dom.impl.ElementImpl;

public class ElementScriptableObject extends DomNodeScriptableObject{

	private ElementImpl elementImpl;

	@Override
	public String getClassName() {
		return "Element";
	}

	public void init(final ElementImpl elementImpl) {
		super.init(elementImpl);
		
//		setPrototype(m);
		
		this.elementImpl = elementImpl;
		defineProperty("setAttribute", new BaseFunction() {
			@Override
			public Object call(Context cx, Scriptable scope,
					Scriptable thisObj, Object[] args) {
				String key = (String)args[0];
				String value = (String)args[1];
				doSetAttribute(key,value);
				return null;
			}
		}, ScriptableObject.PERMANENT);
		defineProperty("getAttribute", new BaseFunction() {
			@Override
			public Object call(Context cx, Scriptable scope,
					Scriptable thisObj, Object[] args) {
				String key = (String)args[0];
				String value = doGetAttribute(key);
				return value;
			}
		}, ScriptableObject.PERMANENT);
//		defineProperty("nodeValue", elementImpl.getNodeValue(), PERMANENT);
//		// TODO: This is not according to the standard but it is nice.
//		defineProperty("value", elementImpl.getNodeValue(), PERMANENT);
		
		setGetterOrSetter("tagName", 0, new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				return elementImpl.getTagName();
			}
		}, false);
	}

	protected String doGetNodeValue() {
		return this.elementImpl.getNodeValue();
	}

	protected String doGetAttribute(String key) {
		return this.elementImpl.getAttribute(key);
	}

	protected void doSetAttribute(String key, String value) {
		this.elementImpl.setAttribute(key, value);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("ElementSO:[");
		buffer.append(this.elementImpl);
		buffer.append("]");
		return buffer.toString();
	}
	
}
