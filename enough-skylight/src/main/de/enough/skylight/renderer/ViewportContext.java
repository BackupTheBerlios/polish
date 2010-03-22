package de.enough.skylight.renderer;

import de.enough.polish.content.ContentLoader;

public interface ViewportContext {
	
	public String getHost();
	
	public String getUrl();
	
	public ContentLoader getContentLoader();
}
