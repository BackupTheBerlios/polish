package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;


public abstract class CssAttributeHandler {
	public static Object VALUE_INVALID = new Object();
	
	public static int TYPE_DIMENSION = 0x00;
	
	public static int TYPE_RANGE = 0x01;
	
	public static int TYPE_COLOR = 0x02;
	
	public static int TYPE_BOOLEAN = 0x03;
	
	int hash;

	String name;
	
	int type;
	
	Object[] range;
	

	public CssAttributeHandler() {
		this.name = getName();
		this.hash = this.name.hashCode();
		this.type = getType();
		this.range = getRange();
	}

	protected abstract String getName();
	
	protected abstract int getType();
	
	protected abstract void addAttribute(Style style, Object value);
	
	Object getValue(String text) {
		if(this.type == TYPE_RANGE) {
			if(this.range != null) {
				return getEntry(text,this.range);
			} else {
				//#debug error
				System.out.println("getRange() is not implemented for ranged css attribute " + this.name);
			}
		} else if(this.type == TYPE_DIMENSION) {
			return getDimension(text); 
		} else if(this.type == TYPE_COLOR) {
			return getColor(text); 
		} else if(this.type == TYPE_BOOLEAN) {
			return getBoolean(text); 
		} else {
			//#debug error
			System.out.println("invalid type for handler " + this.name);
		}
		
		return null;
	}

	Object[] getRange() {
		return null;
	}
	
	protected Object getEntry(String text, Object[] range) {
		for (int index = 0; index < this.range.length; index++) {
			Object entry = this.range[index];
			if (entry.equals(text)) {
				return entry;
			}
		}
		
		//#debug error
		System.out.println(text + " is not a valid value for " + this.name);
		
		return VALUE_INVALID;
	}
	
	protected Object getDimension(String text) {
		return new Dimension(text);
	}
	
	protected Object getColor(String text) {
		int color = Integer.parseInt(text);
		return new Color(color);
	}
	
	protected Object getBoolean(String text) {
		if(text.equals("true")) {
			return new Boolean(true);
		} else if(text.equals("false")) {
			return new Boolean(false);
		} else {
			return VALUE_INVALID;
		}
	}
	
	public void addToStyle(Style style, String text) {
		Object result = getValue(text);
		
		if(result != null) { 
			if(result != CssAttributeHandler.VALUE_INVALID) {
				addAttribute(style, result);
			} else {
				//#debug error
				System.out.println("invalid value for " + getName());
			}
		}
	}
	
	public int getHash() {
		return this.hash;
	}
}
