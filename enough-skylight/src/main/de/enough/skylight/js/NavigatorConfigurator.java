package de.enough.skylight.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class NavigatorConfigurator implements JsConfigurator {

	public void init(Context context, Scriptable scope) {
		scope.put("alert", scope, new AlertScriptableObject());
	}

}
