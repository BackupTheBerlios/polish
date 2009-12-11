package de.enough.skylight.js;

import org.mozilla.javascript.ScriptableObject;

import de.enough.skylight.dom.impl.TextImpl;

public class TextScriptableObject extends ScriptableObject{

	private TextImpl textImpl;

	@Override
	public String getClassName() {
		return "Text";
	}

	public void init(TextImpl textImpl) {
		this.textImpl = textImpl;
	}

}
