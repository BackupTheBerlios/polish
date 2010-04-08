package de.enough.skylight.js;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.dom.impl.ElementImpl;

public class DocumentScriptableObject extends ScriptableObject {

	protected DocumentImpl documentImpl;

	public void init(final DocumentImpl documentImpl) {
		this.documentImpl = documentImpl;
		defineProperty("doctype", null, READONLY|PERMANENT);
		defineProperty("implementation", null, READONLY|PERMANENT);
		
		setGetterOrSetter("documentElement", 0, new Callable() {
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				ElementImpl documentElement = documentImpl.getDocumentElement();
				if(documentElement != null) {
					return documentElement.getScriptable();
				}
				return null;
			}
		}, false);
		defineProperty("documentElement", null, READONLY|PERMANENT);
		
		defineProperty("createElement", new BaseFunction() {
			@Override
			public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
				String tagName = (String)args[0];
				return documentImpl.createElement(tagName).getScriptable();
			}
		}, PERMANENT);
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
					throw new EvaluatorException("The function 'getElementById' needs one parameter but got '"+args.length+"'.");
//					Context.reportRuntimeError("The function 'getElementById' needs one parameter but got '"+args.length+"'.");
				}
				String id = (String)args[0];
				ElementImpl element = documentImpl.getElementById(id);
				if(element != null) {
					return element.getScriptable();
				}
				return null;
			}
		}, EMPTY);
	}
	
	@Override
	public String getClassName() {
		return "Document";
	}
}
