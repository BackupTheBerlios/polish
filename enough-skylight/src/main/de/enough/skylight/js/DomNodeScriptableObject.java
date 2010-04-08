package de.enough.skylight.js;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.enough.skylight.dom.DOMException;
import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.dom.impl.NamedNodeMapImpl;
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
	
	public void init(final DomNodeImpl domNodeImpl) {
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
		
		setGetterOrSetter("nodeName", 0, new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				return domNodeImpl.getNodeName();
			}
		}, false);
		defineProperty("nodeName", this.domNodeImpl.getNodeName(), READONLY|PERMANENT);
		
		setGetterOrSetter("nodeValue",0,new Callable() {
			public Object call(Context cx, Scriptable scope,
					Scriptable thisObj, Object[] args) {
				return domNodeImpl.getNodeValue();
			}
		},false);
		setGetterOrSetter("nodeValue",0,new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				String nodeValue;
				if(args == null || args.length <= 0 || args[0] == null){
					nodeValue = null;
				}else {
					nodeValue = args[0].toString();
					domNodeImpl.setNodeValue(nodeValue);
				}
				return nodeValue;
			}
		},true);
		defineProperty("nodeValue", null, PERMANENT);
		
		
		setGetterOrSetter("nodeType",0,new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				return new Integer(domNodeImpl.getNodeType());
			}
		},false);
		defineProperty("nodeType", new Integer(this.domNodeImpl.getNodeType()), READONLY|PERMANENT);
		
		final DomNodeImpl parentNode = this.domNodeImpl.getParentNode();
		setGetterOrSetter("parentNode",0,new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				if(parentNode != null) {
					return parentNode.getScriptable();
				}
				return null;
			}
		},false);
		
		defineProperty("parentNode", null, READONLY|PERMANENT);
		
		setGetterOrSetter("childNodes", 0, new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				NodeListImpl childNodes = domNodeImpl.getChildNodes();
				if(childNodes != null) {
					return childNodes.getScriptable();
				}
				return null;
			}
		},false);
		defineProperty("childNodes", null, READONLY|PERMANENT);
		
		setGetterOrSetter("firstChild",0,new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				DomNodeImpl firstChild = domNodeImpl.getFirstChild();
				if(firstChild != null) {
					return firstChild.getScriptable();
				}
				return null;
			}
		},false);
		defineProperty("firstChild", null, READONLY|PERMANENT);
		
		setGetterOrSetter("lastChild",0,new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				DomNodeImpl lastChild = domNodeImpl.getLastChild();
				if(lastChild != null) {
					return lastChild.getScriptable();
				}
				return null;
			}
		},false);
		defineProperty("lastChild", null, READONLY|PERMANENT);
		
		setGetterOrSetter("previousSibling",0,new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				DomNodeImpl previousSibling = domNodeImpl.getPreviousSibling();
				if(previousSibling != null) {
					return previousSibling.getScriptable();
				}
				return null;
			}
		},false);
		defineProperty("previousSibling", null, READONLY|PERMANENT);
		
		setGetterOrSetter("nextSibling",0,new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				DomNodeImpl nextSibling = domNodeImpl.getNextSibling();
				if(nextSibling != null) {
					return nextSibling.getScriptable();
				}
				return null;
			}
		},false);
		defineProperty("nextSibling", null, READONLY|PERMANENT);
		
		setGetterOrSetter("attributes",0,new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				NamedNodeMapImpl attributes = domNodeImpl.getAttributes();
				if(attributes != null) {
					return attributes.getScriptable();
				}
				return null;
			}
		},false);
		defineProperty("attributes", this.domNodeImpl.getAttributes(), READONLY|PERMANENT);
		
		setGetterOrSetter("ownerDocument",0, new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				DocumentImpl ownerDocument = domNodeImpl.getOwnerDocument();
				if(ownerDocument != null) {
					return ownerDocument.getScriptable();
				}
				return null;
			}
		},false);
		defineProperty("ownerDocument", null, READONLY|PERMANENT);
		
		setGetterOrSetter("namespaceURI",0, new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				String namespaceURI = domNodeImpl.getNamespaceURI();
				if(namespaceURI != null) {
					return namespaceURI;
				}
				return null;
			}
		},false);
		defineProperty("namespaceURI", null, READONLY|PERMANENT);
		
		setGetterOrSetter("prefix",0,new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				return domNodeImpl.getPrefix();
				
			}
		},false);
		setGetterOrSetter("prefix",0,new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				if(args != null && args[0] != null) {
					throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "Setting the prefix property is not implemented.");
				}
				return null;
			}
		},true);
		defineProperty("prefix", null, PERMANENT);
		
		setGetterOrSetter("localName",0,new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				return domNodeImpl.getLocalName();
			}
		},false);
		defineProperty("localName", null, READONLY|PERMANENT);
		
		defineProperty("insertBefore", new BaseFunction() {
			@Override
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				DomNodeImpl newChild = ((DomNodeScriptableObject)args[0]).domNodeImpl;
				DomNodeImpl refChild = ((DomNodeScriptableObject)args[1]).domNodeImpl;
				return domNodeImpl.insertBefore(newChild, refChild);
			}
		}, PERMANENT);
		
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
				return new Boolean(domNodeImpl.hasAttributes());
			}
			
		}, PERMANENT);
	}

	public DomNodeImpl getAdaptedDomNode() {
		return this.domNodeImpl;
	}

}
