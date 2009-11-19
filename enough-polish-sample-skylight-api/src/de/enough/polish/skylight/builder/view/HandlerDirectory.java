package de.enough.polish.skylight.builder.view;

import java.io.IOException;
import java.io.InputStream;

import de.enough.polish.skylight.builder.view.handler.DefaultHandler;
import de.enough.polish.skylight.builder.view.handler.FormHandler;
import de.enough.polish.skylight.builder.view.handler.InputHandler;
import de.enough.polish.skylight.builder.view.handler.TextHandler;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.IntHashMap;
import de.enough.polish.util.Properties;
import de.enough.skylight.dom.DomNode;

public class HandlerDirectory {
	HashMap nameDirectory;
	
	IntHashMap typeDirectory;
	
	static DefaultHandler defaultHandler = new DefaultHandler();
	
	static HandlerDirectory instance;
	
	public static NodeHandler getHandler(DomNode node) {
		if(instance == null) {
			instance = new HandlerDirectory();
			instance.load();
		}
		
		return instance.getNodeHandler(node);
	}
	
	void load() {
		this.nameDirectory = buildNameDirectory();
		this.typeDirectory = buildTypeDirectory();
	}
	
	HashMap buildNameDirectory() {
		HashMap directory = new HashMap();
		
		directory.put("form", new FormHandler());
		directory.put("input", new InputHandler());
		
		return directory;
	}
	
	IntHashMap buildTypeDirectory() {
		IntHashMap directory = new IntHashMap();
		
		directory.put(DomNode.TEXT_NODE, new TextHandler());
		
		return directory;
	}
	
	NodeHandler getNodeHandler(DomNode node) {
		String name = node.getNodeName();
		int type = node.getNodeType();
		
		NodeHandler result;
		if(type == DomNode.ELEMENT_NODE) {
			result = (NodeHandler)this.nameDirectory.get(name);
		} else {
			result = (NodeHandler)this.typeDirectory.get(type);
		}
		
		if(result != null) {
			return result;
		} else {
			return defaultHandler;
		}
	}
}
