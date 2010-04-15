package de.enough.skylight.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class JsUnitConfigurator implements JsConfigurator {

	public void init(Context context, Scriptable scope) {
		AssertScriptableObject assertScriptableObject = new AssertScriptableObject();
		assertScriptableObject.init();
		scope.put("Assert",scope,assertScriptableObject);
	}

}
