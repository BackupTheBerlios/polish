/*
 * Created on 01-Mar-2004 at 15:17:38.
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
package de.enough.polish.preprocess.css;

import java.util.*;

import de.enough.polish.Device;

/**
 * <p>Represents a CSS-style-definition.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        01-Mar-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Style {
	
	private final static Map REFERENCE_ATTRIBUTES = new HashMap();
	static {
		REFERENCE_ATTRIBUTES.put("background", Boolean.TRUE );
		REFERENCE_ATTRIBUTES.put("border", Boolean.TRUE );
		REFERENCE_ATTRIBUTES.put("font", Boolean.TRUE );
	}
	private HashMap properties;
	private HashMap groupsByName;
	private ArrayList groupNamesList;
	private String selector;
	private String parentName;
	private String styleName;
	private boolean isDynamic;
	private String abbreviation;
	private boolean isReferenced;
	private boolean hasReferences;
	private ArrayList referencedStyles;

	
	public Style( String selector, String styleName, boolean isDynamic, String parent, CssBlock cssBlock ) {
		this.selector = selector;
		this.styleName = styleName;
		this.isDynamic = isDynamic;
		this.parentName = parent;
		this.properties = new HashMap();
		this.groupsByName = new HashMap();
		this.groupNamesList = new ArrayList();
		add( cssBlock );
	}

	/**
	 * Creates a new Style.
	 * 
	 * @param style the base style.
	 */
	public Style(Style style) {
		this.properties = new HashMap( style.properties );
		this.groupNamesList = new ArrayList( style.groupNamesList );
		this.selector = style.selector;
		this.styleName = style.styleName;
		this.isDynamic = style.isDynamic;
		this.parentName = style.parentName;
		HashMap source = style.groupsByName;
		HashMap target = new HashMap();
		Set keys = source.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			Object key = iter.next();
			HashMap original = (HashMap) source.get( key );
			HashMap copy = new HashMap( original );
			target.put(key, copy );
		}
		this.groupsByName = target;
	}
	
	public String getParentName() {
		return this.parentName;
	}
	
	/**
	 * Sets all style declarations of the parent.
	 * All styles implicitely extend the default-style. Theuy also
	 * can extend another style explicitely with the "extends" keyword.
	 * 
	 * @param parent the parent of this style.
	 */
	public void setParent( Style parent ) {
		//System.out.println("setting parent [" + parent.getSelector() + "] for style [" + this.selector + "].");
		// set the standard properties:
		Set set = parent.properties.keySet();
		for (Iterator iter = set.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			if ( this.properties.get(key) == null) {
				this.properties.put( key, parent.properties.get(key));
			}
		}
		// set the group properties:
		String[] groupNames = parent.getGroupNames();
		for (int i = 0; i < groupNames.length; i++) {
			String groupName = groupNames[i];
			//System.out.println("group-name=" + groupName);
			// check for cases, in which a style extends another style which uses this style as its focused style:
			HashMap parentGroup = parent.getGroup(groupName);
			//if ("focused".equals(groupName)) {
				//System.out.println("detected focused CSS attribute: " + parentGroup + ", child.selector=" + this.selector );
				String referencedStyleName = (String) parentGroup.get("style");
				if (referencedStyleName != null) {
					if (referencedStyleName.startsWith(".")) {
						referencedStyleName = referencedStyleName.substring(1);
					}
					if ( referencedStyleName.startsWith( parent.selector) ) { //.equalsIgnoreCase( referencedStyleName )) {
						//System.out.println("found style circular reference in child style " + this.selector + ", affected referenced style=" + referencedStyleName + ", parent=" + parent.selector);
						if (parentGroup.size() == 1) {
							// this is just a focused-style: thisStyle reference - ignore
							// and continue with rest:
							continue;
						} else {
							// there is more than the "style" attribute in the "focused" group,
							// so remove
							HashMap parentGroupCopy = new HashMap( parentGroup.size() );
							parentGroupCopy.putAll( parentGroup );
							parentGroupCopy.remove( "style" );
							parentGroup = parentGroupCopy;					
						}
					}
				}
			//}
			HashMap targetGroup = (HashMap) this.groupsByName.get( groupName );
			if (targetGroup == null) {
				//System.out.println("setting group [" + groupName + "].");
				// set the complete group when it is not defined:
				this.groupsByName.put( groupName, new HashMap( parentGroup ) ); 
				this.groupNamesList.add( groupName );
			} else if (targetGroup.get(groupName) == null){
				//System.out.println("setting only new group-properties of  [" + groupName + "].");
				// only set the properties which are not defined yet:
				set = parentGroup.keySet();
				for (Iterator iter = set.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					if ( (targetGroup.get(key) == null) 
							&& !isReferenceAttribute( groupName, key ) ) 
						
					{
						//System.out.println("setting property [" + key + "] with value [" + parentGroup.get(key) + "]." );
						targetGroup.put( key, parentGroup.get(key));
//					} else {
//						System.out.println("skipping property key [" + key + "] with value [" +targetGroup.get(key) +"] for style " + this.selector + ", parent=" + parent.selector );
					}
				}
			}
		}
	}

	/**
	 * Determines whether the given attribute is a reference, currently "font", "background" or "border" 
	 * @param groupName the name of the group
	 * @param attributeName the name of the attribute
	 * @return true when the the given attribute is a reference, currently "font", "background" or "border"
	 */
	private boolean isReferenceAttribute(String groupName, String attributeName) {
		return attributeName.equals(groupName) &&  (REFERENCE_ATTRIBUTES.get( attributeName ) != null);
	}

	/**
	 * Adds the given CSS declarations to this style.
	 * 
	 * @param cssBlock the CSS declarations
	 */
	public void add(CssBlock cssBlock) {
		// check if this style disallows inheritance - in that case all former CSS attribute are forgotten.
		// this is done when the basic style has defined "inherit: false;"
		 Map inheritGroup = this.getGroup("inherit");
		boolean disallowInheritance = (inheritGroup != null) && ("false".equals(inheritGroup.get("inerhit")));
		if (disallowInheritance) {
			this.groupsByName.clear();
			this.groupNamesList.clear();
		}
		this.properties.putAll( cssBlock.getDeclarationsMap() );
		String[] groupNames = cssBlock.getGroupNames();
		for (int i = 0; i < groupNames.length; i++) {
			String groupName = groupNames[i];
			HashMap group = cssBlock.getGroupDeclarations( groupName );
			HashMap targetGroup = (HashMap) this.groupsByName.get( groupName );
			if (targetGroup == null) {
				this.groupsByName.put( groupName, group ); 
				this.groupNamesList.add( groupName );
			} else {
				// check if a type: none; directive has been specified earlier,
				// e.g. "background: none;". This directive is now obsolete,
				// since more specific values are added.
				// But for margins and paddings it is still usefull - so only
				// remove it for border and background:
				if ("border".equals(groupName) || "background".equals(groupName)) {
					targetGroup.remove( groupName );
					// check if the new style definition contains a reference,
					// in which case all previous definitions should be removed:
					if ( group.get(groupName) != null) {
						//System.out.println("clearing style-group " + groupName );
						targetGroup.clear();
					}
				}
				targetGroup.putAll( group );
			}
		}
	}

	/**
	 * Adds another style to this one.
	 * Existing properties will be overwritten.
	 * 
	 * @param style the style
	 */
	public void add(Style style) {
		this.properties.putAll( style.properties );
		String[] groupNames = style.getGroupNames();
		for (int i = 0; i < groupNames.length; i++) {
			String groupName = groupNames[i];
			HashMap group = style.getGroup( groupName );
			HashMap targetGroup = (HashMap) this.groupsByName.get( groupName );
			if (targetGroup == null) {
				this.groupsByName.put( groupName, group ); 
				this.groupNamesList.add( groupName );
			} else {
				// check if a type: none; directive has been specified earlier,
				// e.g. "background: none;". This directive is now obsolete,
				// since more specific values are added.
				// But for margins and paddings it is still usefull - so only
				// remove it for border and background:
				if ("border".equals(groupName) || "background".equals(groupName)) {
					targetGroup.remove( groupName );
				}
				targetGroup.putAll( group );
			}
		}
	}

	
	/**
	 * Retrieves the group with the specified name.
	 * 
	 * @param groupName the name of the group
	 * @return the map containing all defined attributes of the group
	 */
	public HashMap getGroup(String groupName ) {
		return (HashMap) this.groupsByName.get( groupName );
	}

	/**
	 * Removes the group with the specified name from this style.
	 * 
	 * @param groupName the name of the group
	 * @return the map containing all defined attributes of the group
	 */
	public HashMap removeGroup(String groupName ) {
		this.groupNamesList.remove(groupName);
		return (HashMap) this.groupsByName.remove( groupName );
	}	
	/**
	 * Retrieves the names of all stored groups.
	 * 
	 * @return an array with the names of all included groups. 
	 */
	public String[] getGroupNames() {
		return (String[]) this.groupNamesList.toArray( new String[ this.groupNamesList.size() ] );
	}

	/**
	 * Retrieves the name of this style.
	 * 
	 * @return the name of this style.
	 */
	public String getSelector() {
		return this.selector;
	}

	/**
	 * Changes the selector of this style.
	 * 
	 * @param selector the new selector
	 */
	public void setSelector(String selector) {
		this.selector = selector;
	}

	/**
	 * Adds a group to this style.
	 * 
	 * @param groupName the name of the group
	 * @param group the group
	 */
	public void addGroup(String groupName, HashMap group) {
		boolean addName = (this.groupsByName.get( groupName ) == null);
		this.groupsByName.put( groupName, group );
		if (addName) {
			this.groupNamesList.add( groupName );
		}
	}
	
	/**
	 * Creates String representation of this style.
	 * Is used for debugging puposes only,
	 * 
	 * @return the buffer plus contents as a string
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Style ")
			  .append( this.selector )
			  .append(" extends " ).append( this.parentName ).append( ":\n");
		String[] groupNames = getGroupNames();
		for (int i = 0; i < groupNames.length; i++) {
			String name = groupNames[i];
			HashMap group = getGroup(name);
			if (group == null) {
				group = new HashMap();
				group.put( "INVALID GROUP", name  );
			}
			buffer.append(name).append(": ").append( group.toString() ).append("\n");
		}
		return buffer.toString();
	}

	/**
	 * @return Returns true when this style is a dynamic one
	 */
	public boolean isDynamic() {
		return this.isDynamic;
	}
	/**
	 * @return Returns the name of this style, which can be used instead of the selector for java-variables.
	 */
	public String getStyleName() {
		return this.styleName;
	}

	/**
	 * Retrieves the names of all defined attributes of this style.
	 * 
	 * @param device the current device
	 * @return a String array with all names of defined attributes.
	 *         One attribute-name can appear several times.
	 */
	public String[] getDefinedAttributes( Device device ) {
		ArrayList attributes = new ArrayList();
		String[] groupNames = getGroupNames();
		for (int i = 0; i < groupNames.length; i++) {
			String groupName = groupNames[i];
			HashMap group = getGroup(groupName);
			String[] attributesNames = (String[]) group.keySet().toArray( new String[ group.size() ]);
			for (int j = 0; j < attributesNames.length; j++) {
				String attributeName = attributesNames[j];
				if (groupName.equals(attributeName)) {
					attributes.add( attributeName );
				} else {
					String fullName = groupName + "-" + attributeName;
					// check if there is a "columns-width: 23, *;" kind of setting: 
					if ("columns-width".equals(fullName)) {
						String value = (String) group.get( attributeName );
						if (value.indexOf('*') != -1) { // && device.getCapability("ScreenWidth") == null) {
							attributes.add( "columns-width.star");
						}
					}
					attributes.add( fullName );
				}
			}
		}
		return (String[]) attributes.toArray( new String[ attributes.size() ] );
	}

	/**
	 * Retrieves the abbreviation of this style.
	 * 
	 * @return the abbreviation of this style
	 */
	public String getAbbreviation() {
		return this.abbreviation;
	}
	
	/**
	 * Sets the abbreviation of this style.
	 * 
	 * @param abbreviation the new abbreviation of this style
	 */
	public void setAbbreviation( String abbreviation ) {
		this.abbreviation = abbreviation;
	}
	
	/**
	 * Retrieves the names of styles which are referenced by the this style.
	 * 
	 * @return an array with names of referenced styles. Can be empty but not null.
	 */
	public String[] getReferencedStyleNames() {
		ArrayList referencedNames = new ArrayList();
		String[] groupNames = getGroupNames();
		for (int i = 0; i < groupNames.length; i++) {
			String groupName = groupNames[i];
			HashMap group = getGroup(groupName);
			String[] attributesNames = (String[]) group.keySet().toArray( new String[ group.size() ]);
			for (int j = 0; j < attributesNames.length; j++) {
				String attributeName = attributesNames[j];
				if (attributeName.endsWith("style") && !("font".equals(groupName))) {
					referencedNames.add( group.get( attributeName ));
				}
			}
		}
		return (String[]) referencedNames.toArray( new String[ referencedNames.size() ]);
	}
	
	public void setIsReferenced( boolean isReferenced ) {
		this.isReferenced = isReferenced;
	}
	
	public boolean isReferenced() {
		return this.isReferenced;
	}
	
	public void setHasReferences( boolean hasReferences ) {
		this.hasReferences = hasReferences;
	}
	
	public boolean hasReferences() {
		return this.hasReferences;
	}

	/**
	 * Adds a referenced style.
	 * 
	 * @param referencedStyle the style which is referenced by this style
	 */
	public void addReferencedStyle(Style referencedStyle) {
		if (this.referencedStyles == null) {
			this.referencedStyles = new ArrayList();
		}
		this.referencedStyles.add( referencedStyle );
		this.hasReferences = true;
	}
	
	public Style[] getReferencedStyles() {
		if (this.referencedStyles == null) {
			return new Style[0];
		} else {
			return (Style[]) this.referencedStyles.toArray( new Style[ this.referencedStyles.size() ]);
		}
	}

	/**
	 * @param attributeName 
	 * @return
	 */
	public String getAttributeValue(String attributeName) {
		int splitPos = attributeName.indexOf('-');
		if (splitPos == -1) {
			return (String) this.properties.get( attributeName );
		}
		String groupName = attributeName.substring(0, splitPos );
		HashMap group = getGroup(groupName);
		if (group == null) {
			return null;
		}
		return (String) group.get( attributeName.substring( splitPos + 1 ) );
	}
}
