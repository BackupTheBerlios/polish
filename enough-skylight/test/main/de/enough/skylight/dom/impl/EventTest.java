package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.Element;

public class EventTest extends AbstractDomTest /*implements EventListener*/ {

	public void testAttributeModificationEvent() {
		System.out.println("######################### Visual test: Check if the following trace of events is sane.");
		Element element = this.document1.getElementById("id3");
	
//		element.addEventListener("DOMAttrModified", this, false);
//		element.addEventListener("DOMAttrModified", this, true);

		element.setAttribute("keyblubb", "valuebla");
		System.out.println("######################### Visual test END.");
	}

//	public void handleEvent(Event evt) {
//		DomNodeImpl domNodeImpl = (DomNodeImpl)evt.getCurrentTarget();
//		System.out.println("visiting listener at:"+domNodeImpl.getNodeName());
//	}
}
