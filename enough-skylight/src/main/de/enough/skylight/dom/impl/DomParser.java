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

	public Document parseTree(String document) {
		try {
			return parseDocument(document, null);
		} catch (UnsupportedEncodingException e) {
			//#debug error
			System.out.println("Unable to parse stream in default encoding: " + e);
			throw new RuntimeException( e.toString() );
		}
	}

	
	public Document parseDocument(String document, String encoding) throws UnsupportedEncodingException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(document.getBytes());
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
	    DomNodeImpl root = new DomNodeImpl();
	    root.init(document,null, null, null, DomNode.DOCUMENT_NODE);
	    document.init(root);
	    
	    DomNode currentNode = root;
	    String newName;
	    int newType;
	    
	    try {
	        while ((parser.next()) != SimplePullParser.END_DOCUMENT) {
	            newName = parser.getName();
	            newType = parser.getType();
	            
	            if(newType == SimplePullParser.START_TAG) {	
	            	DomNodeImpl newNode = new DomNodeImpl();
	            	NamedNodeMapImpl attributeMap = new NamedNodeMapImpl();
	                newNode.init(document,currentNode,newName,attributeMap,DomNode.ELEMENT_NODE);
	                attributeMap.init(newNode);
	                int attributeCount = parser.getAttributeCount(); 
	                if (attributeCount > 0) {
	                	
	                	for (int i = 0; i < attributeCount; i++) {
	                		String attributeName = parser.getAttributeName(i);
	                		String attributeValue = parser.getAttributeValue(i);
	                		AttrImpl attribute = new AttrImpl();
	                		attribute.init(newNode, attributeName, attributeValue);
	                		attributeMap.setNamedItem(attribute);
	                	}
	                }
	                currentNode = newNode;
	            }
	            
	            else if(newType == SimplePullParser.END_TAG) {
	                currentNode = currentNode.getParentNode();
	            }
	            
	            else if(newType == SimplePullParser.TEXT) {
	                String text = parser.getText();
	                DomNodeImpl newNode = new DomNodeImpl();
	                newNode.init(document,currentNode,null,null,DomNode.TEXT_NODE);
	                newNode.setNodeValue(text);
	            }
	        }
	    } catch (Exception exception) {
	    	exception.printStackTrace();
	        throw new RuntimeException("parse error:"+exception);
	    }
	    return document;
	}

}