package de.enough.skylight.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class DomConfigurator implements JsConfigurator {

	public void init(Context context, Scriptable scope) {
		ElementIdScriptableObject.init(scope, false);
	}

}