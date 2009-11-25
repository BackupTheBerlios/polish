package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.Element;
import de.enough.skylight.dom.Event;
import de.enough.skylight.dom.EventListener;

public class EventTest extends AbstractDomTest implements EventListener {

	public void testAttributeModificationEvent() {
		Element element = this.document1.getElementById("id3");
	
		element.addEventListener("DOMAttrModified", this, false);
		element.addEventListener("DOMAttrModified", this, true);

		element.setAttribute("keyblubb", "valuebla");
	}

	@Override
	public void handleEvent(Event evt) {
		DomNodeImpl domNodeImpl = (DomNodeImpl)evt.getCurrentTarget();
		System.out.println(domNodeImpl.getNodeName());
	}
}
