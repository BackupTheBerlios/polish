package de.enough.skylight.js;


import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.dom.impl.DomNodeImpl;

public class JsEngine {

	private Context context;
	private Scriptable scope;
	
	public JsEngine() {
		this.context = Context.enter();
		this.scope = this.context.initStandardObjects();
		this.context.setOptionOnErrorThrowExeption(true);
	}
	
	public void init(JsConfigurator jsConfigurator) {
		jsConfigurator.init(this.context, this.scope);
	}
	
	public void setDocument(Document document) {
		DocumentImpl document2 = (DocumentImpl)document;
		this.scope.put("document", this.scope, document2.getScriptable());
	}
	
	/**
	 * Runs a script. This script has a toplevel scope so no return statements are allowed.
	 * @param target the DomNode object which will be the 'this' object in the call.
	 * @param scriptText
	 * @return
	 */
	public Object runScript(DomNodeImpl target, String scriptText) {
		Script script = this.context.compileString(scriptText, "test1", 1);
		Scriptable scriptable = target.getScriptable();
		scriptable.setParentScope(this.scope);
		Object result = script.exec(this.context, scriptable);
		return result;
	}
	
	/**
	 * Runs a script. This script has a toplevel scope so no return statements are allowed.
	 * @param scriptText
	 * @return
	 */
	public Object runScript(String scriptText) {
		Script script = this.context.compileString(scriptText, "test1", 1);
		Object result = script.exec(this.context, this.scope);
		return result;
	}

	public void runFunction(Object onClickFunction) {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * Use this method to create Javascript objects as if a constructor function was called.
	 * @param constructorName
	 * @param args
	 * @return
	 */
	public Scriptable newObject(String constructorName, Object[] args) {
		return this.context.newObject(this.scope, constructorName, args);
	}
}
