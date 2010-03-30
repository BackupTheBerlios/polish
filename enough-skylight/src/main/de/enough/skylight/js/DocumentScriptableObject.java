package de.enough.skylight.js;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.Element;
import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.dom.impl.ElementImpl;

public class DocumentScriptableObject extends ScriptableObject {

	private DocumentImpl documentImpl;

	public void init(DocumentImpl documentImpl) {
		this.documentImpl = documentImpl;
		defineProperty("doctype", null, READONLY|PERMANENT);
		defineProperty("implementation", null, READONLY|PERMANENT);
		defineProperty("documentElement", null, READONLY|PERMANENT);
		defineProperty("createElement", null, PERMANENT);
		defineProperty("createDocumentFragment", null, PERMANENT);
		defineProperty("createTextNode", null, PERMANENT);
		defineProperty("createComment", null, PERMANENT);
		defineProperty("createCDATASection", null, PERMANENT);
		defineProperty("createProcessingInstruction", null, PERMANENT);
		defineProperty("createAttribute", null, PERMANENT);
		defineProperty("createEntityReference", null, PERMANENT);
		defineProperty("getElementsByTagName", null, PERMANENT);
		defineProperty("importNode", null, PERMANENT);
		defineProperty("createElementNS", null, PERMANENT);
		defineProperty("createAttributeNS", null, PERMANENT);
		defineProperty("getElementsByTagNameNS", null, PERMANENT);
		defineProperty("getElementById", new BaseFunction() {
			@Override
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				if(args.length < 1) {
					throw new RuntimeException("The function 'getElementById' needs one parameter but got '"+args.length+"'.");
				}
				ElementImpl element = (ElementImpl)getElementById((String)args[0]);
				if(element != null) {
					return element.getScriptable();
				}
				return null;
			}
		}, EMPTY);
	}
	
	protected Element getElementById(String id) {
		return this.documentImpl.getElementById(id);
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		if("documentElement".equals(name)) {
			int numberChildren = this.documentImpl.getChildNodes().getLength();
			// Return the first child which is not a processing instruction.
			// TODO: The handling of processing instructions in the DomParser is not existent. Implement something
			// to test a node if it is a processing instruction.
			for(int i = 0; i < numberChildren;i++) {
				DomNodeImpl child = (DomNodeImpl)this.documentImpl.getChildNodes().item(i);
				if(child.getNodeType() == DomNode.ELEMENT_NODE) {
					return child.getScriptable();
				}				
			}
			return null;
		}
		return super.get(name, start);
	}
	
	@Override
	public String getClassName() {
		return "Document";
	}
}
