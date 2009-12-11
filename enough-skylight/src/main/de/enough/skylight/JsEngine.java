package de.enough.skylight;


import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import de.enough.skylight.dom.impl.DomNodeImpl;
import de.enough.skylight.js.AlertScriptableObject;

public class JsEngine {

	private Context context;
	private Scriptable scope;
	
	public JsEngine() {
		this.context = Context.enter();
		this.context.setOptionOnErrorThrowExeption(true);
		this.scope = this.context.initStandardObjects();
		this.scope.put("alert", this.scope, new AlertScriptableObject());
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
