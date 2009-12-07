package de.enough.skylight;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.js.Alert;

public class JsEngine {

	private Context context;
	private Scriptable scope;
	
	public JsEngine() {
		this.context = Context.enter();
		this.context.setOptionOnErrorThrowExeption(true);
		this.scope = this.context.initStandardObjects();
		this.scope.put("alert", this.scope, new Alert());
	}
	
	public void runScript(String scriptText) {
		Script script = this.context.compileString(scriptText, "test1", 1);
		script.exec(this.context, this.scope);
	}

	public void runFunction(Object onClickFunction) {
		// TODO Auto-generated method stub
		
	}
}
