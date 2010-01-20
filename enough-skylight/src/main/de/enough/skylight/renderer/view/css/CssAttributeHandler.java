package de.enough.skylight.renderer.view.css;

import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;


public abstract class CssAttributeHandler {
	public static Object VALUE_INVALID = new Object();
	
	public static int TYPE_DIMENSION;
	
	public static int TYPE_RANGE;
	
	int hashCode;

	String name;
	
	int type;
	
	Object[] range;
	

	public CssAttributeHandler() {
		this.name = getName();
		this.hashCode = this.name.hashCode();
		this.type = getType();
		this.range = getRange();
	}

	abstract String getName();
	
	abstract int getType();
	
	abstract void addAttribute(Style style, Object value);
	
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
	
	protected void addToStyle(Style style, String text) {
		Object result = getValue(text);
		
		if(result != null && result != CssAttributeHandler.VALUE_INVALID) {
			addAttribute(style, result);
		} 
	}
	
	protected int getHashCode() {
		return this.hashCode;
	}
}
