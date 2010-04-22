package de.enough.skylight.dom.impl;

import de.enough.skylight.Services;
import de.enough.skylight.js.AbstractJsTest;

public class UiEventTest extends AbstractJsTest {

	private static EventEmitter eventEmitter = Services.getInstance().getEventEmitter();

	public void testClickEvent() {
		ElementImpl element = this.documentJsEvent.getElementById("id1");
		eventEmitter.emitClickEvent(element, 10,20);
		String classAttribute = element.getAttribute("class");
		assertEquals("low", classAttribute);
	}
}
