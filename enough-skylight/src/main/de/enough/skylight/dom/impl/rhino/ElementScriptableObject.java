package de.enough.skylight.dom.impl.rhino;

import de.enough.skylight.dom.impl.ElementImpl;

public class ElementScriptableObject extends DomNodeScriptableObject{

	private ElementImpl elementImpl;

	@Override
	public String getClassName() {
		return "Element";
	}

	public void init(ElementImpl elementImpl) {
		super.init(elementImpl);
		this.elementImpl = elementImpl;
	}

}
