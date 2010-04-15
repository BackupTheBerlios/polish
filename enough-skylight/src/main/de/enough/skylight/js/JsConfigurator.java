package de.enough.skylight.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public interface JsConfigurator {

	public void init(Context context, Scriptable scope);

}
