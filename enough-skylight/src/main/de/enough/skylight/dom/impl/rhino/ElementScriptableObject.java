package de.enough.skylight.dom.impl.rhino;

import org.mozilla.javascript.ScriptableObject;

import de.enough.skylight.dom.impl.ElementImpl;

public class ElementScriptableObject extends ScriptableObject{

	private ElementImpl elementImpl;

	@Override
	public String getClassName() {
		return "Element";
	}

	public void init(ElementImpl elementImpl) {
		this.elementImpl = elementImpl;
	}

}
