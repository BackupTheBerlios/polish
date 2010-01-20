package de.enough.skylight.renderer.node;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.IntHashMap;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.handler.BodyHandler;
import de.enough.skylight.renderer.node.handler.BrHandler;
import de.enough.skylight.renderer.node.handler.DefaultHandler;
import de.enough.skylight.renderer.node.handler.DivHandler;
import de.enough.skylight.renderer.node.handler.EmHandler;
import de.enough.skylight.renderer.node.handler.FormHandler;
import de.enough.skylight.renderer.node.handler.H1Handler;
import de.enough.skylight.renderer.node.handler.InputHandler;
import de.enough.skylight.renderer.node.handler.LinkHandler;
import de.enough.skylight.renderer.node.handler.NodeHandler;
import de.enough.skylight.renderer.node.handler.PHandler;
import de.enough.skylight.renderer.node.handler.SpanHandler;
import de.enough.skylight.renderer.node.handler.TitleHandler;

public class NodeHandlerDirectory {
	HashMap nameDirectory;
	
	static NodeHandlerDirectory instance;
	
	public static NodeHandler getHandler(DomNode node) {
		if(instance == null) {
			instance = new NodeHandlerDirectory();
			instance.load();
		}
		
		return instance.getNodeHandler(node);
	}
	
	void load() {
		this.nameDirectory = buildNameDirectory();
	}
	
	void addHandler(HashMap directory, NodeHandler handler) {
		directory.put(handler.getTag(), handler);
	}
	
	HashMap buildNameDirectory() {
		HashMap directory = new HashMap();
		
		addHandler(directory,new FormHandler());
		addHandler(directory,new InputHandler());
		addHandler(directory,new H1Handler());
		addHandler(directory,new BrHandler());
		addHandler(directory,new TitleHandler());
		addHandler(directory,new EmHandler());
		addHandler(directory,new BodyHandler());
		addHandler(directory,new DivHandler());
		addHandler(directory,new SpanHandler());
		addHandler(directory,new PHandler());
		addHandler(directory,new LinkHandler());
		
		return directory;
	}
	
	NodeHandler getNodeHandler(DomNode node) {
		String name = node.getNodeName();
		
		NodeHandler result = null;
		
		if(name != null) {
			 name = name.toLowerCase().trim();
			 result = (NodeHandler)this.nameDirectory.get(name);
		}
		
		if(result != null) {
			return result;
		} else {
			return DefaultHandler.getInstance();
		}
	}
}
