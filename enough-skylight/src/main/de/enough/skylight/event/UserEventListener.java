package de.enough.skylight.event;

import de.enough.skylight.renderer.node.CssElement;

public interface UserEventListener {
	public void onUserEvent(CssElement element, UserEvent event);
}
