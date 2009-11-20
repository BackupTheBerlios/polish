package de.enough.skylight.renderer.builder.view;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.IntHashMap;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.builder.view.handler.DefaultHandler;
import de.enough.skylight.renderer.builder.view.handler.FormHandler;
import de.enough.skylight.renderer.builder.view.handler.H1Handler;
import de.enough.skylight.renderer.builder.view.handler.InputHandler;

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
		directory.put("h1", new H1Handler());
		
		return directory;
	}
	
	IntHashMap buildTypeDirectory() {
		IntHashMap directory = new IntHashMap();
		
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
