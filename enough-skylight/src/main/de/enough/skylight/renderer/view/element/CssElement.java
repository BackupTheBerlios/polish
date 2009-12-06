package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Item;
import de.enough.skylight.dom.DomNode;

public interface CssElement {
	public static class Type {
		public static final int CONTAINING_BLOCK = 0x00;
		public static final int TEXT = 0x01;
		public static final int ELEMENT = 0x02;
		public static final int BREAK = 0x03;
	}
	
	public static class Display {
		public static final String BLOCK_LEVEL = "block";
		public static final String INLINE = "inline";
	}
	
	public static class Position {
		public static final String STATIC = "static";
		public static final String ABSOLUTE = "absolute";
		public static final String RELATIVE = "relative";
	}
	
	public static class Float {
		public static final String NONE = "none";
		public static final String LEFT = "left";
		public static final String RIGHT = "right";
	}
	
	public int getType();
	
	public boolean isDisplay(String display);
	
	public boolean isPosition(String position);
	
	public boolean isFloat(String floating);
	
	public DomNode getNode();
	
	public Item getItem();
	
	public ContainingBlock getParent();
}
