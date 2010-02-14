package de.enough.skylight.resources;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import de.enough.skylight.renderer.ViewportContext;

public class ResourceLoader {
	static String PREFIX_HTTP = "http://";
	
	static String PREFIX_FILE = "file://";
	
	static ResourceLoader instance;
	
	public static ResourceLoader getInstance() {
		if(instance == null) {
			instance = new ResourceLoader();
		}
		
		return instance;
	}
	
	public InputStream getResourcesAsStream(String url, ViewportContext context) throws IOException {
		InputStream result;
		
		String host = context.getHost();
		
		url = url.trim();
		
		try {
			if(url.startsWith(PREFIX_FILE)) {
				// load resource from file
				String filename = url.substring(PREFIX_FILE.length());
				result = getClass().getResourceAsStream(filename);
			}
			else if(url.startsWith(PREFIX_HTTP)) {
				// load resource over http
				HttpConnection connection = (HttpConnection)Connector.open(url);
				result = connection.openInputStream();
			} else {
				// assuming relative url
				String absoluteUrl = host + "/" + url;
				HttpConnection connection = (HttpConnection)Connector.open(absoluteUrl);
				result = connection.openInputStream();
			}
		} catch (IOException e) {
			//#debug error
			System.out.println("unable to load resource " + url);
			return null;
		}
		
		return result;
	}
}

