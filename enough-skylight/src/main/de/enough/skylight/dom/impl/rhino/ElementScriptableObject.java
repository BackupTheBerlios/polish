package de.enough.skylight.dom.impl.rhino;

import org.mozilla.javascript.Scriptable;

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

	@Override
	public Object get(String name, Scriptable start) {
		if("tagName".equals(name)) {
			return this.elementImpl.getTagName();
		}
		return super.get(name, start);
	}
	
	

}
