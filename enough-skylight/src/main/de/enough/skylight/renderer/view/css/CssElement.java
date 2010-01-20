package de.enough.skylight.renderer.view.css;


public interface CssElement {
	
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
	
	public boolean isDisplay(String display);
	
	public boolean isPosition(String position);
	
	public boolean isFloat(String floating);
}
