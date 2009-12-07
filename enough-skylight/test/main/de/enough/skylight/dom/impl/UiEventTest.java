package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.Element;

public class UiEventTest extends AbstractDomTest {

	public void testClickEvent() {
		Element element = this.document1.getElementById("id3");
		MouseEventImpl event = EventEmitter.getInstance().fireClickEvent(element, 10,20);
		if (!event.isPreventDefault()) {
			
		}
	}
}
