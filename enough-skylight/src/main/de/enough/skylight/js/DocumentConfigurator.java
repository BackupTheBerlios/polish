package de.enough.skylight.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import de.enough.skylight.dom.impl.DocumentImpl;
// TODO: Not needed at the moment.
public class DocumentConfigurator implements JsConfigurator{

	private final DocumentImpl document;

	public DocumentConfigurator(DocumentImpl document) {
		this.document = document;
	}

	public void init(Context context, Scriptable scope) {
		scope.put("document", scope, this.document.getScriptable());
	}
}
