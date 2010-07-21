package de.enough.skylight.renderer.builder;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import de.enough.skylight.dom.impl.DocumentImpl;
import de.enough.skylight.dom.impl.DomParser;

public class DocumentManager extends Thread{
	
	final static String PREFIX_HTTP = "http://";
	final static String PREFIX_FILE = "file://";

	private InputStream open(String url) throws IllegalArgumentException {
		
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
				throw new IllegalArgumentException("could not open url : " + url);
			}
		} else {
			throw new IllegalArgumentException("unknown url format");
		}
	}
	
	public DocumentImpl build(String urlPath) {
		try {
			InputStream stream = open(urlPath);
			DocumentImpl document = new DomParser().parseDocument(stream);
			return document;
		} catch(Exception e) {
			//#debug error
			System.out.println("error while building document : " + e);
			e.printStackTrace();
			return null;
		}
	}
}
