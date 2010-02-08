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
		this.context.setOptionOnErrorThrowExeption(true);
		this.scope = this.context.initStandardObjects();
		this.scope.put("alert", this.scope, new AlertScriptableObject());
	}
	
	public void setDocument(Document document) {
		DocumentImpl document2 = (DocumentImpl)document;
		this.scope.put("document", this.scope, document2.getScriptable());
	}
	
	public void runScript(DomNodeImpl target, String scriptText) {
		Script script = this.context.compileString(scriptText, "test1", 1);
		Scriptable scriptable = target.getScriptable();
		scriptable.setParentScope(this.scope);
		script.exec(this.context, scriptable);
	}

	public void runFunction(Object onClickFunction) {
		// TODO Auto-generated method stub
		
	}
}
