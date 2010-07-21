package de.enough.skylight.renderer;

import de.enough.polish.content.ContentLoader;
import de.enough.skylight.event.UserEvent;
import de.enough.skylight.renderer.node.CssElement;

public interface ViewportContext {
	
	public void setTitle(String title);
	public String getLocationUrl();
	public String getLocationHost();
	public ContentLoader getContentLoader();
	public void displayPage(String url);
	public void notifyUserEvent(CssElement cssElement, UserEvent event);
}
