package de.enough.rhino;

import junit.framework.TestCase;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.js.Alert;

public class AlertTest extends TestCase {

	private Scriptable scope;
	private Context context;
	
	@Override
	public void setUp() {
		this.context = Context.enter();
		this.context.setOptionOnErrorThrowExeption(true);
		this.scope = this.context.initStandardObjects();
		AssertScriptableObject assertScriptableObject = new AssertScriptableObject();
		assertScriptableObject.init();
		this.scope.put("Assert",this.scope,assertScriptableObject);
		this.scope.put("alert", this.scope, new Alert());
	}
	
	public void testAlertA() {
		Script script = this.context.compileString("alert('Hello World',2,3,null)", "test1", 1);
		script.exec(this.context, this.scope);
	}
}
