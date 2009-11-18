package de.enough.skylight.renderer;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.StringItem;

import de.enough.polish.ui.Container;
import de.enough.polish.util.StreamUtil;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.Element;
import de.enough.skylight.dom.impl.DomParser;

public class BrowserItem extends Container{

	public static final int LENGTH_OF_FILE_PREFIX = 7;
	
	private Document document;

	public BrowserItem() {
		super(false);
	}
	
	public void setUrl(String htmlUrl) {
		
		if(htmlUrl == null) {
			throw new IllegalArgumentException("The parameter 'htmlUrl' must not be null.");
		}
		
		InputStream inputStream = null;
		if(htmlUrl.startsWith("file://")) {
			String filename = htmlUrl.substring(LENGTH_OF_FILE_PREFIX);
			inputStream = getClass().getResourceAsStream(filename);
		}
		else if(htmlUrl.startsWith("http://")) {
			try {
				HttpConnection connection = (HttpConnection)Connector.open(htmlUrl);
				inputStream = connection.openInputStream();
			} catch (IOException e) {
				throw new IllegalArgumentException("Could not read from URL '"+htmlUrl+"'");
			}
		}
		if(inputStream == null) {
			throw new IllegalArgumentException("Could not read from URL '"+htmlUrl+"'");
		}
		
		String htmlDocument;
		try {
			htmlDocument = StreamUtil.getString(inputStream, null, StreamUtil.DEFAULT_BUFFER);
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not read from URL '"+htmlUrl+"'");
		}
		DomParser domParser = new DomParser();
		this.document = domParser.parseTree(htmlDocument);
	}

	public void render() {
		buildStructure();
	}

	private void buildStructure() {
		Element root = this.document.getDocumentElement();
		String nodeName = root.getNodeName();
		System.out.println(nodeName);
		add(new StringItem("root:",nodeName));
	}

}
