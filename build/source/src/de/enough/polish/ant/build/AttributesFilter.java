/*
 * Created on 15-Jul-2004 at 02:45:52.
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
package de.enough.polish.ant.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.tools.ant.BuildException;

import de.enough.polish.Variable;
import de.enough.polish.util.TextUtil;

/**
 * <p>Sorts JAD or MANIFEST attributes in a user-specific way.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        15-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AttributesFilter {
	
	ArrayList filterElements;

	/**
	 * Creates a new unitialised attributs filter.
	 */
	public AttributesFilter() {
		// initialisation is done with the setter methods
	}
	
	public void addText( String text ) {
		String[] definitions = TextUtil.splitAndTrim( text, ',' );
		this.filterElements = new ArrayList( definitions.length );
		for (int i = 0; i < definitions.length; i++) {
			String definition = definitions[i];
			FilterElement element = new FilterElement( definition );
			if (element.isRest && (i != definitions.length - 1) ) {
				throw new BuildException("The attribute-element [" + definition 
						+ "] can only be placed at the end of the attributes-filter-list.");
			}
			this.filterElements.add( element );
		}
	}
	
	/**
	 * Filters the given attributes.
	 * 
	 * @param attributesMap a hash map containing the available attributes
	 *        with the attribute-names as keys.
	 * @return an array of attributes in the correct order,
	 *         not all given attributes are guaranteed to be included.
	 */
	public Variable[] filterAttributes( HashMap attributesMap ) {
		ArrayList attributesList = new ArrayList();
		for (int i = 0; i < this.filterElements.size(); i++) {
			FilterElement filter = (FilterElement) this.filterElements.get( i );
			Variable[] attributes = filter.extractAttributes(attributesMap);
			for (int j = 0; j < attributes.length; j++) {
				Variable attribute = attributes[j];
				attributesList.add( attribute );
			}
		}
		return (Variable[]) attributesList.toArray( new Variable[ attributesList.size() ] );
	}
	
	/**
	 * <p>A filter element is responsible for extracting the specified attribute(s) in the correct order.</p>
	 *
	 * <p>copyright Enough Software 2004</p>
	 * <pre>
	 * history
	 *        15-Jul-2004 - rob creation
	 * </pre>
	 * @author Robert Virkus, j2mepolish@enough.de
	 */
	class FilterElement {
		boolean isOptional;
		boolean isPattern;
		boolean isRest;
		boolean isRequired;
		String definition;
		
		/**
		 * Creates a new filter.
		 * 
		 * @param definition the definition of the attribute:
		 * 		either the name (for required attributes), 
		 * 		the name followed by a question-mark (for optional attributes),
		 *      the name-beginning followed by a star (for optional attributes 
		 * 		starting with the specified sequence),
		 * 		or just a star for specifying any other attributes.
		 */
		public FilterElement( String definition ) {
			if (definition.endsWith("?")) {
				this.isOptional = true;
				this.definition = definition.substring(0, definition.length() -1 );
			} else if (definition.equals("*")) {
				this.isRest = true;
			} else if (definition.endsWith("*")) {
				this.isPattern = true;
				this.definition = definition.substring(0, definition.length() -1 );
			} else {
				this.isRequired = true;
				this.definition = definition;
			}
		}
		
		/**
		 * Extracts the attributes from the map.
		 * 
		 * @param map the map from which the found attributes will be removed.
		 *        The map contains the attributes by its name.
		 * @return the found attributes for this filter in the correct order
		 */
		public Variable[] extractAttributes( HashMap map ) {
			if (this.isRequired) { 
				Variable attribute = (Variable) map.remove( this.definition );
				if (attribute == null) {
					throw new BuildException("The required JAD or MANIFEST attribute [" 
							+ this.definition + "] was not found. " +
							"Either adjust your filter-settings or add this attribute.");
				}
				return new Variable[]{ attribute };
			} else if (this.isOptional) {
				Variable attribute = (Variable) map.remove( this.definition );
				if (attribute == null) {
					return new Variable[0];
				} else {
					return new Variable[]{ attribute };					
				}
			} else if (this.isPattern) {
				// get all attributes which start with the specified sequence:
				ArrayList foundAttributes = new ArrayList();
				String[] names = (String[]) map.keySet().toArray( new String[ map.size() ]);
				for (int i = 0; i < names.length; i++) {
					String name = names[i];
					if (name.startsWith( this.definition )) {
						foundAttributes.add( name );
					}
				}
				names = (String[]) foundAttributes.toArray( new String[ foundAttributes.size() ]);
				Arrays.sort( names );
				Variable[] attributes = new Variable[ names.length ];
				for (int i = 0; i < attributes.length; i++) {
					attributes[i] = (Variable) map.remove( names[i] );
				}
				return attributes;
			} else {
				// just return the remaining attributes:
				String[] names = (String[]) map.keySet().toArray( new String[ map.size() ]);
				Arrays.sort( names );
				Variable[] attributes = new Variable[ names.length ];
				for (int i = 0; i < attributes.length; i++) {
					attributes[i] = (Variable) map.remove( names[i] );
				}
				return attributes;				
			}
		}
	}

}
