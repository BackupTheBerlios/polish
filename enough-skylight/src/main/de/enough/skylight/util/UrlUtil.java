package de.enough.skylight.util;

import de.enough.skylight.renderer.ViewportContext;

public class UrlUtil {
	static String PREFIX_HTTP = "http://";
	
	static String PREFIX_RESOURCE = "resources://";
	
	public static String completeUrl(String url, ViewportContext context) {
		String result;
		
		String host = context.getLocationHost();

		url = url.trim();
		
		if(url.startsWith(PREFIX_HTTP) || url.startsWith(PREFIX_RESOURCE)) {
			// assuming absolute http / resource url
			result = url;
		} else {
			// assuming relative http url
			result = host + "/" + url;
		}
		
		return result; 
	}
}

