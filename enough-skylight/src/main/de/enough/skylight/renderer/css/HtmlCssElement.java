package de.enough.skylight.renderer.css;


public interface HtmlCssElement {
	
	public static class Display {
		public static final String BLOCK_LEVEL = "block";
		public static final String INLINE = "inline";
                public static final String INLINE_BLOCK = "inline-block";
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
	
	public static class TextAlign {
		public static final String LEFT = "left";
		public static final String CENTER = "center";
		public static final String RIGHT = "right";
	}
	
	public static class VerticalAlign {
		public static final String TOP = "top";
		public static final String MIDDLE = "middle";
		public static final String BOTTOM = "bottom";
	}
	
	public boolean isDisplay(String display);
	
	public boolean isPosition(String position);
	
	public boolean isFloat(String floating);
	
	public boolean isTextAlign(String textAlign);
	
	public boolean isVerticalAlign(String verticalAlign);
}
