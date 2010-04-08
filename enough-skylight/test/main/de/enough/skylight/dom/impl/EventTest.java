package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.Element;

public class EventTest extends AbstractDomTest /*implements EventListener*/ {

	public void testAttributeModificationEvent() {
		System.out.println("######################### Visual test: Check if the following trace of events is sane.");
		Element element = this.document1.getElementById("id3");
		element.setAttribute("keyblubb", "valuebla");
		System.out.println("######################### Visual test END.");
	}

}
