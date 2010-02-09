package de.enough.skylight.js;


import org.mozilla.javascript.BaseFunction;
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

	public void init(ElementImpl elementImpl) {
		super.init(elementImpl);
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
		defineProperty("nodeValue", "", PERMANENT);
		// TODO: This is not according to the standard but it is nice.
		defineProperty("value", "", PERMANENT);
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
	public Object get(String name, Scriptable start) {
		if("tagName".equals(name)) {
			return this.elementImpl.getTagName();
		}
		if("value".equals(name) || "nodeValue".equals(name)) {
			return this.elementImpl.getNodeValue();
		}
		return super.get(name, start);
	}
	
	
	
	@Override
	public void put(String name, Scriptable start, Object value) {
		if("value".equals(name)) {
			if(value instanceof String) {
				this.elementImpl.setNodeValue((String)value);
				return;
			} else {
				throw new RuntimeException("The parameter must be a string.");
			}
		}
		super.put(name, start, value);
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
