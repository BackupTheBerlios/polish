/*
 * Created on 19-Aug-2004 at 14:55:09.
 * 
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.preprocess;

import org.apache.tools.ant.BuildException;
import org.jdom.Element;

import de.enough.polish.util.TextUtil;

/**
 * <p>Represents a non-standard CSS attribute.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        19-Aug-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssAttribute {
	
	public final static int STRING = 0;
	public final static int INTEGER = 1;
	public final static int COLOR = 2;
	public final static int BOOLEAN = 3;
	public final static int STYLE = 4;
	
	private final String name;
	private final int type;
	private final String[] allowedValues;
	private final String defaultValue;
	private final String appliesTo;
	private final String description;

	/**
	 * Creates a new CSS-attribute
	 *  
	 * @param definition the XML definition of this attribute.
	 */
	public CssAttribute(Element definition) {
		this.name = definition.getAttributeValue("name");
		if (this.name == null) {
			throw new BuildException("All CSS-attributes need to define the attribute [name]. Please check the files [standard-css-attributes.xml] and [custom-css-attributes.xml].");
		}
		this.defaultValue = definition.getAttributeValue("default");
		String typeStr = definition.getAttributeValue("type");
		if (typeStr != null) {
			if ("color".equals( typeStr )) {
				this.type = COLOR;
			} else if ("integer".equals( typeStr )) {
				this.type = INTEGER;
			} else if ("boolean".equals( typeStr )) {
				this.type = BOOLEAN;
			} else if ("style".equals( typeStr )) {
				this.type = STYLE;
			} else if ("string".equals( typeStr )){
				this.type = STRING;
			} else {
				throw new BuildException("The CSS-attribute-type [" + typeStr + "] is not supported. It needs to be either [integer], [color], [boolean], [style] or [string].");
			}
		} else {
			this.type = STRING;
		}
		String allowedValuesStr = definition.getAttributeValue("values");
		if (allowedValuesStr != null) {
			this.allowedValues = TextUtil.splitAndTrim( allowedValuesStr, ',' );
		} else {
			this.allowedValues = null;
		}
		this.appliesTo = definition.getAttributeValue("appliesTo");
		this.description = definition.getAttributeValue("description");
	}

	/**
	 * @return Returns the allowedValues.
	 */
	public String[] getAllowedValues() {
		return this.allowedValues;
	}
	/**
	 * @return Returns the defaultValue.
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return this.type;
	}
	
	/**
	 * Determines whether this attribute contains integer values
	 * 
	 * @return true when this attribute containts integer values.
	 */
	public boolean isInteger() {
		return (this.type == INTEGER);
	}
	
	/**
	 * Determines whether this attribute contains color values
	 * 
	 * @return true when this attribute containts color values.
	 */
	public boolean isColor() {
		return (this.type == COLOR);
	}
	
	/**
	 * Determines whether this attribute contains boolean values
	 * 
	 * @return true when this attribute containts boolean values.
	 */
	public boolean isBoolean() {
		return (this.type == BOOLEAN);
	}
	
	/**
	 * Determines whether this attribute contains string values
	 * 
	 * @return true when this attribute containts string values.
	 */
	public boolean isString() {
		return (this.type == STRING);
	}
	
	/**
	 * Determines whether the given value is allowed.
	 * 
	 * @param value the actual value
	 * @throws BuildException when the given value is not allowed by this attribute.
	 */
	public void checkValue( String value ) {
		if (this.allowedValues == null) {
			return;
		}
		for (int i = 0; i < this.allowedValues.length; i++) {
			if (value.equals( this.allowedValues[i])) {
				return;
			}
		}
		String message = "Invalid CSS: the attribute [" + this.name + "] needs one"
					+ "of the following values: [";
		for (int i = 0; i < this.allowedValues.length; i++) {
			message += this.allowedValues[i];
			if (i < this.allowedValues.length - 1) {
				message += "], [";
			}
		}		
		message += "]. The value [" + value + "] is not supported.";
		throw new BuildException( message );
	}
	
	/**
	 * Determines whether the given value is the same as the default value of this attribute.
	 * 
	 * @param value the value defined by the user
	 * @return true when the given value is the default-value.
	 */
	public boolean isDefault( String value ) {
		return value.equals( this.defaultValue );
	}
	/**
	 * @return Returns the class-names for which this attribute can be used.
	 */
	public String getAppliesTo() {
		return this.appliesTo;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return this.description;
	}
}
