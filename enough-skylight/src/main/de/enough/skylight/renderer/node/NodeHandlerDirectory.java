package de.enough.skylight.renderer.node;

import de.enough.polish.util.HashMap;
import de.enough.skylight.dom.DomNode;
import de.enough.skylight.renderer.node.handler.html.AHandler;
import de.enough.skylight.renderer.node.handler.html.BHandler;
import de.enough.skylight.renderer.node.handler.html.BodyHandler;
import de.enough.skylight.renderer.node.handler.html.BrHandler;
import de.enough.skylight.renderer.node.handler.html.DivHandler;
import de.enough.skylight.renderer.node.handler.html.EmHandler;
import de.enough.skylight.renderer.node.handler.html.FormHandler;
import de.enough.skylight.renderer.node.handler.html.H1Handler;
import de.enough.skylight.renderer.node.handler.html.IHandler;
import de.enough.skylight.renderer.node.handler.html.ImgHandler;
import de.enough.skylight.renderer.node.handler.html.InputHandler;
import de.enough.skylight.renderer.node.handler.html.LinkHandler;
import de.enough.skylight.renderer.node.handler.html.PHandler;
import de.enough.skylight.renderer.node.handler.html.SpanHandler;
import de.enough.skylight.renderer.node.handler.html.StrongHandler;
import de.enough.skylight.renderer.node.handler.html.StyleHandler;
import de.enough.skylight.renderer.node.handler.html.TextHandler;
import de.enough.skylight.renderer.node.handler.html.TitleHandler;

public class NodeHandlerDirectory {
	HashMap directory;
	
	static NodeHandlerDirectory instance;
	
	NodeHandler defaultHandler = DefaultHandler.getInstance();
	
	private NodeHandlerDirectory() {
		this.directory = new HashMap();
	}
	
	public static NodeHandlerDirectory getInstance() {
		if(instance == null) {
			instance = new NodeHandlerDirectory();
		}
		
		return instance;
	}
	
	public void addHtmlHandlers() {
		// head elements
		addHandler(new TitleHandler());
		addHandler(new StyleHandler());
		addHandler(new LinkHandler());
		// form elements
		addHandler(new FormHandler());
		addHandler(new InputHandler());
		// block elements
		addHandler(new BodyHandler());
		addHandler(new DivHandler());
		addHandler(new PHandler());
		addHandler(new SpanHandler());
		addHandler(new AHandler());
		// text elements
		addHandler(new H1Handler());
		addHandler(new BrHandler());
		addHandler(new EmHandler());
		addHandler(new IHandler());
		addHandler(new StrongHandler());
		addHandler(new BHandler());
		addHandler(new TextHandler());
		addHandler(new ImgHandler());
	}
	
	public void setDefaultHandler(NodeHandler handler) {
		this.defaultHandler = handler;
	}
	
	public void addHandler(NodeHandler handler) {
		String tag = handler.getTag();
		if(this.directory.get(tag) != null) {
			//#debug warn
			System.out.println("node handler for " + tag + " already exists in directory, overwritten");
		} 
		
		this.directory.put(handler.getTag(), handler);
	}
	
	public NodeHandler getNodeHandler(DomNode node) {
		String name = node.getNodeName();
		
		NodeHandler result = null;
		
		if(name != null) {
			 name = name.toLowerCase().trim();
			 result = (NodeHandler)this.directory.get(name);
		}
		
		if(result != null) {
			return result;
		} else {
			if(this.defaultHandler != null) {
				return DefaultHandler.getInstance();
			} else {
				return null;
			}
		}
	}
}
