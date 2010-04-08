package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.EventTarget;

public interface UserEventEmitter {

	public void emitClickEvent(EventTarget target, int x, int y);
}
