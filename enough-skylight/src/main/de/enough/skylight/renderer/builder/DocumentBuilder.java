package de.enough.skylight.renderer.builder;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import de.enough.polish.util.StreamUtil;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.impl.DomParser;

public class DocumentBuilder extends Thread{
	
	final static String PREFIX_HTTP = "http://";
	final static String PREFIX_FILE = "file://";

	DomParser domParser;
	
	String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public InputStream open(String url) throws IllegalArgumentException {
		
		if(url == null) {
			throw new IllegalArgumentException("url must not be null");
		}
		
		if(url.startsWith("file://")) {
			String filename = url.substring(PREFIX_FILE.length());
			return getClass().getResourceAsStream(filename);
		}
		else if(url.startsWith("http://")) {
			try {
				HttpConnection connection = (HttpConnection)Connector.open(url);
				return connection.openInputStream();
			} catch (IOException e) {
				throw new IllegalArgumentException("could not open url : " + this.url);
			}
		} else {
			throw new IllegalArgumentException("unknown url format");
		}
	}
	
	public Document build() {
		try {
			InputStream stream = open(this.url);
			Document document = new DomParser().parseDocument(stream);
			return document;
		} catch(Exception e) {
			//#debug error
			System.out.println("error while building document : " + e);
			e.printStackTrace();
			return null;
		}
	}
}
