package de.enough.skylight.renderer.view.css;

import java.io.Reader;

import de.enough.polish.browser.css.CssInterpreter;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ArrayList;

public class HtmlCssInterpreter extends CssInterpreter{

	ArrayList attributeHandler;
	
	public HtmlCssInterpreter(Reader reader) {
		super(reader);

		this.attributeHandler = new ArrayList();
		
		load();
	}
	
	void load() {
		addAttributeHandler(new DisplayHandler());
		addAttributeHandler(new FloatHandler());
		addAttributeHandler(new PositionHandler());
	}
	
	void addAttributeHandler(CssAttributeHandler handler) {
		this.attributeHandler.add(handler);
	}
	
	CssAttributeHandler getAttributeHandler(String name) {
		int hashCode = name.hashCode();
		for (int i = 0; i < this.attributeHandler.size(); i++) {
			CssAttributeHandler handler = (CssAttributeHandler)this.attributeHandler.get(i);
			if(hashCode == handler.getHashCode()) {
				return handler;
			}
		}
		
		return null;
	}
	
	protected void addAttribute(String name, String value, Style style) {
		super.addAttribute(name, value, style);
	
		CssAttributeHandler handler = getAttributeHandler(name);
		
		if(handler != null) {
			handler.addToStyle(style, value);
		} else {
			//#debug warn
			System.out.println("unable to find css handler for " + name);
		}
	}	
}
