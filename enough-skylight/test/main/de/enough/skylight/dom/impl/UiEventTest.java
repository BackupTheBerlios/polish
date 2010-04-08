package de.enough.skylight.dom.impl;

import de.enough.skylight.Services;
import de.enough.skylight.js.AbstractJsTest;

public class UiEventTest extends AbstractJsTest {

	private static EventEmitter eventEmitter = Services.getInstance().getEventEmitter();

	public void testClickEvent() {
		System.out.println("!!!!!!!!!!!!!!!Start of failing test");
		ElementImpl element = this.documentJsEvent.getElementById("id1");
		eventEmitter.emitClickEvent(element, 10,20);
		System.out.println("!!!!!!!!!!!!!!!End of failing test");
		String classAttribute = element.getAttribute("class");
		assertEquals("low", classAttribute);
	}
}
