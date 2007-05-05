/*
 * Created on May 4, 2007 at 9:55:07 AM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.styleeditor;

import de.enough.polish.resources.ColorProvider;
import de.enough.polish.ui.Color;
import de.enough.polish.util.ReflectionUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        May 4, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class EditColor
implements ColorProvider
{
	private final String name;
	private Color color;
	private ColorProvider referencedColor;

	public EditColor(ColorProvider referencedColor){ 
		this( null, null, referencedColor );
	}

	public EditColor(Color color){ 
		this( null, color, null );
	}

	public EditColor(String name, ColorProvider referencedColor){
		this( name, null, referencedColor );
	}

	public EditColor(String name, Color color){
		this( name, color, null );
	}
	
	public EditColor(String name, Color color, ColorProvider referencedColor ){
		if (color == null && referencedColor == null) {
			throw new IllegalArgumentException("color or reference must not be null");
		}
		this.name = name;
		this.color = color;
		this.referencedColor = referencedColor;
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ColorProvider#getColor()
	 */
	public Color getColor() {
		if (this.referencedColor != null) {
			return this.referencedColor.getColor();
		}
		return this.color;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ColorProvider#getName()
	 */
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ColorProvider#generateCssSourceCode(java.lang.StringBuffer)
	 */
	public void generateCssSourceCode(StringBuffer buffer) {
		buffer.append( this.name ).append(": ").append( toStringValue() );
	}
	
	public String toStringValue() {
		if (this.referencedColor != null) {
			return this.referencedColor.getName();
		}
		if (this.color.isTransparent()) {
			return "transparent";
		}
		if (this.color.isDynamic()) {
			try {
				int dynamicValue = ReflectionUtil.getIntField( this.color, "argb");
				switch (dynamicValue) { 
				case Color.COLOR_BACKGROUND: return "COLOR_BACKGROUND";
				case Color.COLOR_BORDER: return "COLOR_BORDER";
				case Color.COLOR_FOREGROUND: return "COLOR_FOREGROUND";
				case Color.COLOR_HIGHLIGHTED_BACKGROUND: return "COLOR_HIGHLIGHTED_BACKGROUND";
				case Color.COLOR_HIGHLIGHTED_BORDER: return "COLOR_HIGHLIGHTED_BORDER";
				case Color.COLOR_HIGHLIGHTED_FOREGROUND: return "COLOR_HIGHLIGHTED_BORDER";
				}
			} catch (Exception e) {
				throw new IllegalStateException("Unable to retrieve Color.argb field: " + e.toString(), e );
			}
		}
		int colorValue = this.color.getColor();
		String hex = Integer.toHexString(colorValue);
		StringBuffer buffer = new StringBuffer();
		buffer.append( '#' );
		int remaining = 6 - hex.length();
		if (remaining == -1) {
			remaining = 1;
		}
		while (remaining > 0) {
			remaining--;
			buffer.append('0');
		}
		buffer.append(hex);
		return buffer.toString(); 
 	}
	
	public String toString() {
		return this.name;
	}

	/**
	 * @param value
	 */
	public void setColor(Color value) {
		if (value == null) {
			throw new IllegalArgumentException("null not allowed");
		}
		this.referencedColor = null;
		this.color = value;
	}
	
	public void setColorReference(ColorProvider value) {
		if (value == null) {
			throw new IllegalArgumentException("null not allowed");
		}
		this.referencedColor = value;
		this.color = null;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.resources.ColorProvider#getReferencedColor()
	 */
	public ColorProvider getColorReference() {
		return this.referencedColor;
	}

}
