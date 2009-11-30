package de.enough.skylight.renderer.viewport;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.IntHashMap;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.view.handler.BodyHandler;
import de.enough.skylight.renderer.view.handler.BrHandler;
import de.enough.skylight.renderer.view.handler.DefaultHandler;
import de.enough.skylight.renderer.view.handler.DivHandler;
import de.enough.skylight.renderer.view.handler.EmHandler;
import de.enough.skylight.renderer.view.handler.FormHandler;
import de.enough.skylight.renderer.view.handler.H1Handler;
import de.enough.skylight.renderer.view.handler.InputHandler;
import de.enough.skylight.renderer.view.handler.LinkHandler;
import de.enough.skylight.renderer.view.handler.PHandler;
import de.enough.skylight.renderer.view.handler.SpanHandler;
import de.enough.skylight.renderer.view.handler.TitleHandler;

public class HandlerDirectory {
	HashMap nameDirectory;
	
	IntHashMap typeDirectory;
	
	static DefaultHandler defaultHandler = new DefaultHandler();
	
	static HandlerDirectory instance;
	
	public static ElementHandler getHandler(DomNode node) {
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
		directory.put("br", new BrHandler());
		directory.put("title", new TitleHandler());
		directory.put("em", new EmHandler());
		directory.put("body", new BodyHandler());
		directory.put("div", new DivHandler());
		directory.put("span", new SpanHandler());
		directory.put("p", new PHandler());
		directory.put("link", new LinkHandler());
		
		return directory;
	}
	
	IntHashMap buildTypeDirectory() {
		IntHashMap directory = new IntHashMap();
		
		return directory;
	}
	
	ElementHandler getNodeHandler(DomNode node) {
		String name = node.getNodeName();
		
		if(name != null) {
			 name = name.toLowerCase().trim();
		}
		
		int type = node.getNodeType();
		
		ElementHandler result;
		if(type == DomNode.ELEMENT_NODE) {
			result = (ElementHandler)this.nameDirectory.get(name);
		} else {
			result = (ElementHandler)this.typeDirectory.get(type);
		}
		
		if(result != null) {
			return result;
		} else {
			return DefaultHandler.getInstance();
		}
	}
}
