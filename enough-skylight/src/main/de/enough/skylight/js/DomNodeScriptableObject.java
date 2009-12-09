package de.enough.skylight.js;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.impl.DomNodeImpl;

public class DomNodeScriptableObject extends ScriptableObject {
	
	final class AppendChildFunction extends BaseFunction {
		@Override
		public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
			if(args.length < 1) {
				return null;
			}
			return appendChild((DomNodeImpl) args[0]);
		}
	}
	final class ReplaceChildFunction extends BaseFunction {
		@Override
		public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
			return replaceChild((DomNodeImpl)args[0], (DomNodeImpl)args[1]);
		}
	}
	
	private DomNodeImpl domNodeImpl;

	protected DomNode appendChild(DomNodeImpl newChild) {
		return this.domNodeImpl.appendChild(newChild);
	}
	
	protected DomNode replaceChild(DomNodeImpl newChild, DomNodeImpl oldChild) {
		return this.domNodeImpl.replaceChild(newChild, oldChild);
	}
	
	@Override
	public String getClassName() {
		// TODO: What classname should a ScriptableDomNode have?
		return "Node";
	}
	
	public void init(DomNodeImpl domNodeImpl) {
		this.domNodeImpl = domNodeImpl;
		putConst("ELEMENT_NODE", this, new Integer(5));
		defineProperty("nodeName", this.domNodeImpl.getNodeName(), READONLY|PERMANENT);
		defineProperty("nodeValue", null, PERMANENT);
		defineProperty("nodeType", new Integer(this.domNodeImpl.getNodeType()), READONLY|PERMANENT);
		defineProperty("parentNode", this.domNodeImpl.getParentNode(), READONLY|PERMANENT);
		defineProperty("childNodes", this.domNodeImpl.getChildNodes(), READONLY|PERMANENT);
		defineProperty("firstChild", null, READONLY|PERMANENT);
		defineProperty("lastChild", null, READONLY|PERMANENT);
		defineProperty("previousSibling", null, READONLY|PERMANENT);
		defineProperty("nextSibling", null, READONLY|PERMANENT);
		defineProperty("attributes", this.domNodeImpl.getAttributes(), READONLY|PERMANENT);
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
			@Override
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				return new Boolean(hasAttributes());
			}
			
		}, PERMANENT);
	}

	protected boolean hasAttributes() {
		return this.domNodeImpl.hasAttributes();
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		if("parentNode".equals(name)) {
			return this.domNodeImpl.getParentNode();
		}
		return super.get(name, start);
	}
}
