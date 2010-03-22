package de.enough.skylight.content;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import de.enough.skylight.renderer.ViewportContext;

public class ContentUtil {
	static String PREFIX_HTTP = "http://";
	
	static String PREFIX_RESOURCE = "resources://";
	
	public static String completeUrl(String url, ViewportContext context) {
		String result;
		
		String host = context.getHost();

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
	
	public static InputStream getStream(String url) throws IOException{
		if(url.startsWith(PREFIX_RESOURCE)) {
			return url.getClass().getResourceAsStream(url);
		} else if(url.startsWith(PREFIX_HTTP)) {
			HttpConnection connection = (HttpConnection)Connector.open(url);
			return connection.openInputStream();
		} else {
			throw new IOException("unknown url scheme");
		}
	}
}

