package de.enough.skylight.renderer.css.handler;

import de.enough.polish.ui.Dimension;
import de.enough.polish.ui.Style;


public abstract class AttributeHandler {
	public static Object VALUE_INVALID = new Object();
	
	public static int TYPE_DIMENSION = 0x00;
	
	public static int TYPE_RANGE = 0x01;
	
	int hash;

	String name;
	
	int type;
	
	Object[] range;
	

	public AttributeHandler() {
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
	
	public void addToStyle(Style style, String text) {
		Object result = getValue(text);
		
		if(result != null && result != AttributeHandler.VALUE_INVALID) {
			addAttribute(style, result);
		} 
	}
	
	public int getHash() {
		return this.hash;
	}
}
