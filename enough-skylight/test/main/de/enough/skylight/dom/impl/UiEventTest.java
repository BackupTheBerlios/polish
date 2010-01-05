package de.enough.skylight.dom.impl;

import de.enough.skylight.Services;
import de.enough.skylight.dom.Element;

public class UiEventTest extends AbstractDomTest {

	private static EventEmitter eventEmitter = Services.getInstance().getEventEmitter();

	public void testClickEvent() {
		Element element = this.documentJsEvent.getElementById("id1");
		eventEmitter.emitClickEvent(element, 10,20);
		String classAttribute = element.getAttribute("class");
		assertEquals("low", classAttribute);
	}
}
