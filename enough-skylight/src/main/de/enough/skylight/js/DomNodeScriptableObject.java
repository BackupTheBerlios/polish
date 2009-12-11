package de.enough.skylight.js;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.dom.impl.NodeListImpl;

public class DomNodeScriptableObject extends ScriptableObject {
	
	final class AppendChildFunction extends BaseFunction {
		@Override
		public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
			if(args.length < 1) {
				return null;
			}
			DomNodeImpl newChild = (DomNodeImpl)args[0];
			return DomNodeScriptableObject.this.domNodeImpl.appendChild(newChild);
		}
	}
	final class ReplaceChildFunction extends BaseFunction {
		@Override
		public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
			DomNodeImpl newChild = (DomNodeImpl)args[0];
			DomNodeImpl oldChild = (DomNodeImpl)args[1];
			return DomNodeScriptableObject.this.domNodeImpl.replaceChild(newChild, oldChild);
		}
	}
	
	protected DomNodeImpl domNodeImpl;

	@Override
	public String getClassName() {
		return "Node";
	}
	
	public void init(DomNodeImpl domNodeImpl) {
		this.domNodeImpl = domNodeImpl;
		putConst("ELEMENT_NODE", this, new Integer(1));
		putConst("ATTRIBUTE_NODE", this, new Integer(2));
		putConst("TEXT_NODE", this, new Integer(3));
		putConst("CDATA_SECTION_NODE", this, new Integer(4));
		putConst("ENTITY_REFERENCE_NODE", this, new Integer(5));
		putConst("ENTITY_NODE", this, new Integer(6));
		putConst("PROCESSING_INSTRUCTION_NODE", this, new Integer(7));
		putConst("COMMENT_NODE", this, new Integer(8));
		putConst("DOCUMENT_NODE", this, new Integer(9));
		putConst("DOCUMENT_TYPE_NODE", this, new Integer(10));
		putConst("DOCUMENT_FRAGMENT_NODE", this, new Integer(11));
		putConst("NOTATION_NODE", this, new Integer(12));
		defineProperty("nodeName", this.domNodeImpl.getNodeName(), READONLY|PERMANENT);
		defineProperty("nodeValue", null, PERMANENT);
		defineProperty("nodeType", new Integer(this.domNodeImpl.getNodeType()), READONLY|PERMANENT);
		defineProperty("parentNode", ((DomNodeImpl)this.domNodeImpl.getParentNode()).getScriptable(), READONLY|PERMANENT);
		defineProperty("childNodes", ((NodeListImpl)this.domNodeImpl.getChildNodes()).getScriptable(), READONLY|PERMANENT);
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
