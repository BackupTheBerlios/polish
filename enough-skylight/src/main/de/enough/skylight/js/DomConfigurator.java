package de.enough.skylight.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.js.scriptableobjects.DocumentScriptableObject;
import de.enough.skylight.js.scriptableobjects.ElementScriptableObject;
import de.enough.skylight.js.scriptableobjects.NodeListScriptableObject;

public class DomConfigurator implements JsConfigurator {

	public void init(Context context, Scriptable scope) {
		ElementScriptableObject.init(scope, false);
		NodeListScriptableObject.init(scope,false);
		DocumentScriptableObject.init(scope,false);
	}

}
