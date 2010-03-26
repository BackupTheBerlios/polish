package de.enough.skylight.renderer;

import de.enough.polish.content.ContentLoader;

public interface ViewportContext {
	
	public String getLocationHost();
	
	public String getLocationUrl();
	
	public ContentLoader getContentLoader();
	
	public void setLocation(String url);
}
