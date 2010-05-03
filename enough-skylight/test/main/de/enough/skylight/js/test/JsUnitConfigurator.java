package de.enough.skylight.js.test;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.js.JsConfigurator;

public class JsUnitConfigurator implements JsConfigurator {

	public void init(Context context, Scriptable scope) {
		AssertScriptableObject assertScriptableObject = new AssertScriptableObject();
		assertScriptableObject.init();
		scope.put("Assert",scope,assertScriptableObject);
	}

}
