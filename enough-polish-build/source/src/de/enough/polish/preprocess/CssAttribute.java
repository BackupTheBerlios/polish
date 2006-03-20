/*
 * Created on 19-Aug-2004 at 14:55:09.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.preprocess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.jdom.Element;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Environment;
import de.enough.polish.util.CastUtil;
import de.enough.polish.util.StringUtil;

/**
 * <p>Represents a non-standard CSS attribute.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        19-Aug-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssAttribute
implements Comparable
{
	
	public final static int STRING = 0;
	public final static int INTEGER = 1;
	public final static int COLOR = 2;
	public final static int BOOLEAN = 3;
	public final static int STYLE = 4;
	public static final int CHAR = 5;
	public static final int OBJECT = 6;
	public static final int IMAGE_URL = 7;
	
	public final static Map TYPES_MAP = new HashMap();
	static {
		TYPES_MAP.put( "color", new Integer( COLOR ) );
		TYPES_MAP.put( "integer", new Integer( INTEGER ) );
		TYPES_MAP.put( "int", new Integer( INTEGER ) );
		TYPES_MAP.put( "boolean", new Integer( BOOLEAN ) );
		TYPES_MAP.put( "bool", new Integer( BOOLEAN ) );
		TYPES_MAP.put( "style", new Integer( STYLE ) );
		TYPES_MAP.put( "string", new Integer( STRING ) );
		TYPES_MAP.put( "char", new Integer( CHAR ) );
		TYPES_MAP.put( "object", new Integer( OBJECT ) );
		TYPES_MAP.put( "imageurl", new Integer( IMAGE_URL ) );
	}
	
	private final String name;
	private final int type;
	private final String[] allowedValues;
	private final String defaultValue;
	private final String appliesTo;
	private final String description;
	private int id;
	private final Map appliesToMap;
	private final Map mappingsByName;
	private final String group;
	private final boolean isBaseAttribute;
	private final boolean isCaseSensitive;
	private final boolean allowsCombinations;
	private final boolean requiresMapping;
	

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
			Integer typeInt = (Integer) TYPES_MAP.get( typeStr.toLowerCase() );
			if (typeInt == null) {
				throw new BuildException("The CSS-attribute-type [" + typeStr + "] is not supported. It needs to be either [integer], [color], [boolean], [style] or [string]. Please check your custom-css-attributes.xml file.");
			} else {
				this.type = typeInt.intValue();
			}
		} else {
			this.type = STRING;
		}
		String allowedValuesStr = definition.getAttributeValue("values");
		if (allowedValuesStr != null) {
			this.allowedValues = StringUtil.splitAndTrim( allowedValuesStr, ',' );
		} else {
			this.allowedValues = null;
		}
		this.appliesTo = definition.getAttributeValue("appliesTo");
		if ( this.appliesTo != null ) {
			String[] appliesChunks = StringUtil.splitAndTrim( this.appliesTo, ',');
			this.appliesToMap = new HashMap();
			for (int i = 0; i < appliesChunks.length; i++) {
				String chunk = appliesChunks[i];
				this.appliesToMap.put( chunk, Boolean.TRUE );
			}
		} else {
			this.appliesToMap = null;
		}
		List mappingsList = definition.getChildren("mapping");
		if (mappingsList == null || mappingsList.size() == 0) {
			this.mappingsByName = null;
		} else {
			this.mappingsByName = new HashMap( mappingsList.size() );
			for (Iterator iter = mappingsList.iterator(); iter.hasNext();) {
				CssMapping mapping = new CssMapping( (Element) iter.next());
				this.mappingsByName.put( mapping.getFrom(), mapping );
			}
		}
		this.description = definition.getAttributeValue("description");
		String idStr = definition.getAttributeValue("id");
		if (idStr != null && !idStr.equals("none")) {
			this.id = Integer.parseInt(idStr);
		} else {
			this.id = -1;
		}
		String groupStr = definition.getAttributeValue("group");
		if (groupStr != null) {
			this.group = groupStr;
			this.isBaseAttribute = "base".equals(groupStr);
		} else {
			this.group = null;
			this.isBaseAttribute = false;
		}
		String isCaseSensitiveStr = definition.getAttributeValue("isCaseSensitive");
		if (isCaseSensitiveStr != null) {
			this.isCaseSensitive = CastUtil.getBoolean( isCaseSensitiveStr );
		} else {
			this.isCaseSensitive = true;
		}
		String allowsCombinationsStr = definition.getAttributeValue("allowsCombinations");
		if (allowsCombinationsStr != null) {
			this.allowsCombinations = CastUtil.getBoolean( allowsCombinationsStr );
		} else {
			this.allowsCombinations = false;
		}
		String requiresMappingStr = definition.getAttributeValue("requiresMapping");
		if (requiresMappingStr != null) {
			this.requiresMapping = CastUtil.getBoolean(requiresMappingStr);
		} else {
			this.requiresMapping = false;
		}
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
	 * @return true when this attribute contains integer values.
	 */
	public boolean isInteger() {
		return (this.type == INTEGER);
	}
	
	/**
	 * Determines whether this attribute contains color values
	 * 
	 * @return true when this attribute contains color values.
	 */
	public boolean isColor() {
		return (this.type == COLOR);
	}
	
	/**
	 * Determines whether this attribute contains boolean values
	 * 
	 * @return true when this attribute contains boolean values.
	 */
	public boolean isBoolean() {
		return (this.type == BOOLEAN);
	}
	
	/**
	 * Determines whether this attribute contains Style values
	 * 
	 * @return true when this attribute 
	 */
	public boolean isStyle() {
		return ( this.type == STYLE );
	}
	
	/**
	 * Determines whether this attribute contains image-url values
	 * 
	 * @return true when this attribute 
	 */
	public boolean isImageUrl() {
		return ( this.type == IMAGE_URL );
	}


	
	/**
	 * Determines whether this attribute contains string values
	 * 
	 * @return true when this attribute contains string values.
	 */
	public boolean isString() {
		return (this.type == STRING);
	}
	
	/**
	 * Checks and transforms the given CSS value for this attribute.
	 * 
	 * @param value the attribute value
	 * @param environment the environment
	 * @return the transformed value or the same value if no transformation is required.
	 * @throws BuildException when a condition is not met or when the value contains conflicting values
	 */
	public String getValue(String value, Environment environment ) {
		if (isBoolean()) {
			if ("true".equals( value ) || "true".equals( value )) {
				if (this.isBaseAttribute) {
					return "true";
				}
				return "Style.TRUE";
			} else if ("false".equals( value ) || "no".equals( value )) {
				if (this.isBaseAttribute) {
					return "false";
				}
				return "Style.FALSE";
			} else {
				throw new BuildException( "Invalid CSS: the attribute \"" + this.name + "\" needs to be eiter \"true\" or \"false\" - the given value \"" + value + "\" is not supported."  );
			}
		} else if (this.type == CHAR) {
			if (value.length() != 1) {
				throw new BuildException( "Invalid CSS: the attribute \"" + this.name + "\" needs to be a character - the given value \"" + value + "\" is not supported."  );
			}
			return value;
		} else if (this.mappingsByName != null) {
			if (!this.allowsCombinations) {
				CssMapping mapping = getMapping(value);
				if (mapping != null) {
					mapping.checkCondition( this.name, value, environment.getBooleanEvaluator() );
					return mapping.getTo();
				} else if (this.requiresMapping) {
					throw new BuildException("Invalid CSS: the attribute \"" + this.name + "\" does not support the value \"" + value + "\".");
				} else {
					// System.out.println("returning unmapped value " + value );
					return value;
				}
			} else {
				// combinations are allowed
				if (!this.isCaseSensitive) {
					value = value.toLowerCase();
				}
				BooleanEvaluator evaluator =  environment.getBooleanEvaluator();
				value = StringUtil.replace(value, " or ", " | ");
				value = StringUtil.replace(value, " and ", " | ");
				value = StringUtil.replace(value, " || ", " | ");
				value = StringUtil.replace(value, " && ", " | ");
				value = value.replace('&', '|');
				value = value.replace(',', '|');
				String[] values = StringUtil.splitAndTrim(value, '|');
				StringBuffer convertedValueBuffer = new StringBuffer();
				for (int i = 0; i < values.length; i++) {
					String singleValue = values[i];
					CssMapping mapping = getMapping(singleValue);
					if (mapping != null) {
						mapping.checkCondition( this.name, value, evaluator );
						convertedValueBuffer.append( mapping.getTo() );
					} else if (this.requiresMapping) {
						throw new BuildException("Invalid CSS: the attribute \"" + this.name + "\" does not support the value \"" + singleValue + "\".");
					} else {
						convertedValueBuffer.append( singleValue );
					}
					if (i != values.length - 1) {
						convertedValueBuffer.append(" | ");
					}
				}
				if (this.isBaseAttribute) {
					return convertedValueBuffer.toString();
				} else if ( isInteger() ){
					return "new Integer( " + convertedValueBuffer.toString() + ")";
				}
			}
		} else if (this.type == INTEGER && this.allowedValues == null) {
			try {
				int intValue = Integer.parseInt( value );
				if (this.isBaseAttribute) {
					return "" + intValue;
				} else {
					return "new Integer(" + intValue + ")";
				}
			} catch (NumberFormatException e) {
				throw new BuildException("Invalid CSS: The attribute [" + this.name + "] needs an integer value. The value [" + value + "] cannot be accepted.");
			}
			
		}
		if (this.allowedValues == null) {
			return value;
		}
		for (int i = 0; i < this.allowedValues.length; i++) {
			if (!this.isCaseSensitive) {
				value = value.toLowerCase();
			}
			if (value.equals( this.allowedValues[i])) {
				if (isInteger()) {
					if (this.isBaseAttribute) {
						return "" + i;
					} else {
						return "new Integer(" + i + ")";
					}
				}
				return value;
			}
		}
		String message = "Invalid CSS: the attribute [" + this.name + "] needs to be one "
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

	/**
	 * @return true when this CSS attribute has a fix list of allowed values
	 */
	public boolean hasFixValues() {
		return (this.allowedValues != null);
	}
	
	/**
	 * Retrieves the position of the given value in list of allowed values.
	 * 
	 * @param value the value
	 * @return the position, a value between 0 and the length of allowed values.
	 *         When the value is invalid, -1 will be returned
	 * @throws NullPointerException when no fix allowed values are defined
	 * @see #hasFixValues()
	 * @see #checkValue(String, BooleanEvaluator) 
	 */
	public int getValuePosition( String value ) {
		for (int i = 0; i < this.allowedValues.length; i++) {
			if (value.equals( this.allowedValues[i])) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Retrieves the ID of this attribute.
	 * 
	 * @return the ID - or -1 if it is not known
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Sets the ID of this attribute.
	 * The ID should only be set, when it was previously not know (== -1).
	 *  
	 * @param id the new ID
	 */
	public void setId( int id ) {
		this.id = id;
	}

	/**
	 * @param className
	 * @return true when the attribute can be used for the given class.
	 */
	public boolean appliesTo(String className) {
		if (this.appliesToMap == null) {
			//System.out.println("CssAttribute.appliesTo=[" + this.appliesTo + "], to [" + className + "] = NO APPLIES MAP DEFINED!");
			return false;
		} else {
			//System.out.println("CssAttribute.appliesTo=[" + this.appliesTo + "], to [" + className + "] = " + (this.appliesToMap.get( className ) != null));
			return (this.appliesToMap.get( className ) != null);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if (o instanceof CssAttribute) {
			return this.name.compareTo( ((CssAttribute)o).name );
		}
		return 0;
	}
	
	private CssMapping getMapping( String value ) {
		if (this.mappingsByName == null) {
			return null;
		} else {
			return (CssMapping) this.mappingsByName.get( value );
		}
	}

	private String getMappingFrom(String value) {
		CssMapping mapping = getMapping(value);
		if (mapping != null) {
			return mapping.getTo();
		}
		return null;
	}
	
	/**
	 * Adds the mappings of the given capability to this one.
	 * With this feature new mappings can be added in custom-css-attributes.xml
	 * 
	 * @param extension the attribute containing further mappings
	 */
	public void add( CssAttribute extension ) {
		this.mappingsByName.putAll( extension.mappingsByName );
	}

	

}
