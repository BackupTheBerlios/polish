package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Style;
import de.enough.skylight.dom.DomNode;

public interface Element {
	
	public static class Display {
		public static String BLOCK_LEVEL = "block";
		public static String INLINE = "inline";
	}
	
	public static class Position {
		public static String STATIC = "static";
		public static String ABSOLUTE = "absolute";
		public static String RELATIVE = "relative";
	}
	
	public static class Float {
		public static String NONE = "none";
		public static String LEFT = "left";
		public static String RIGHT = "right";
	}
	
	public DomNode getNode();
	
	public boolean isDisplay(String display);
	
	public boolean isPosition(String position);
	
	public boolean isFloat(String floating);
	
	public void setStyle(Style style);
}
