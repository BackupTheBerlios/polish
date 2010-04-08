package de.enough.skylight.dom.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import de.enough.polish.xml.SimplePullParser;
import de.enough.polish.xml.XmlPullParser;
import de.enough.skylight.dom.Document;
import de.enough.skylight.dom.DomNode;

/**
 * This is the new parser implementation to create a valid DOM tree.
 */
public class DomParser {

	// TODO: Rename to parseDocument.
	public Document parseDocument(String documentText) {
		try {
			return parseDocument(documentText, null);
		} catch (UnsupportedEncodingException e) {
			//#debug error
			System.out.println("Unable to parse stream in default encoding: " + e);
			throw new RuntimeException( e.toString() );
		}
	}

	
	public Document parseDocument(String documentText, String encoding) throws UnsupportedEncodingException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(documentText.getBytes());
		return parseDocument(byteArrayInputStream, encoding);
	}

	
	public Document parseDocument(InputStream in) {
		try {
			return parseDocument(in, null);
		} catch (UnsupportedEncodingException e) {
			//#debug error
			System.out.println("Unable to parse stream in default encoding: " + e);
			throw new RuntimeException( e.toString() );
		}
	}

	public Document parseDocument(InputStream in, String encoding) throws UnsupportedEncodingException {
		XmlPullParser parser;
	    InputStreamReader inputStreamReader;
	    if (encoding != null) {
	    	inputStreamReader = new InputStreamReader(in, encoding);
	    } else {
	    	inputStreamReader = new InputStreamReader(in);
	    }
	
	    try {
	        parser = new XmlPullParser(inputStreamReader);
	    } catch (IOException exception) {
	        throw new RuntimeException("Could not create xml parser."+exception);
	    }
	    DocumentImpl document = new DocumentImpl();
	    document.init();
	    DomNodeImpl currentNode = document;
	    String newName;
	    int newType;
	    
	    try {
	        while ((parser.next()) != SimplePullParser.END_DOCUMENT) {
	            newName = parser.getName();
	            newType = parser.getType();
	            switch(newType){
	            	case SimplePullParser.START_TAG:
	            		NamedNodeMapImpl attributeMap = new NamedNodeMapImpl();
		            	ElementImpl newElement = new ElementImpl();
		            	newElement.init(document,currentNode,newName,attributeMap,DomNode.ELEMENT_NODE);
		                int attributeCount = parser.getAttributeCount(); 
		                if (attributeCount > 0) {
		                	
		                	for (int i = 0; i < attributeCount; i++) {
		                		String attributeName = parser.getAttributeName(i);
		                		String attributeValue = parser.getAttributeValue(i);
		                		AttrImpl attribute = new AttrImpl();
		                		attribute.init(document, newElement, attributeName, attributeValue);
		                		attributeMap.setNamedItem(attribute);
		                	}
		                }
		                currentNode = newElement;
	            		break;
	            		
	            	case SimplePullParser.END_TAG:
	            		currentNode = currentNode.getParentNode();
	            		break;
	            		
	            	case SimplePullParser.TEXT:
	            		String text = parser.getText();
		                if(text != null) {
		                	text = text.trim();
		                	if(text.length() == 0) {
		                		continue;
		                	}
		                }
		                TextImpl newTextNode = new TextImpl();
		                newTextNode.init(document,currentNode,text);
	            		break;
	            		
	            	default:
	            		throw new RuntimeException("The parsing type '"+newType+"' is unknown.");
	            }
	        }
	    } catch (Exception exception) {
	    	exception.printStackTrace();
	        throw new RuntimeException("parse error:"+exception);
	    }
	    return document;
	}

}