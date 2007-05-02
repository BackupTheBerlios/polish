/*
 * Created on Apr 29, 2007 at 8:41:03 PM.
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
package de.enough.polish.styleeditor.editors;

import java.util.ArrayList;
import java.util.Arrays;

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssMapping;
import de.enough.polish.preprocess.css.ParameterizedCssMapping;
import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.util.ReflectionUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 29, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class ParameterizedStyleEditor extends StylePartEditor {
	
	protected CssAttributesEditor attributesEditor;
	
	protected Object instantiateDefault(CssAttribute attribute, String fromName) {
		ParameterizedCssMapping mapping = (ParameterizedCssMapping) attribute.getMapping(fromName);
		if (mapping != null) {
			try {
				Class mappedClass = Class.forName( mapping.getToClassName() );
				
				CssAttribute[] parameterAttributes = mapping.getParameters();
				Object[] parameters = new Object[ parameterAttributes.length ];
				for (int i = 0; i < parameterAttributes.length; i++) {
					CssAttribute parameterAttribute = parameterAttributes[i];
					Object parameter = parameterAttribute.instantiateDefault( getEnvironment() );
					parameters[i] = parameter;
				}
				return ReflectionUtil.newInstance( mappedClass, parameters );
			} catch (Exception e) {
				System.out.println("Warning: unable to invoke constructor: " + e.toString() );
				e.printStackTrace();
			}
		}		
		return null;
	}
	
	/**
	 * Retrieves all mapped names that have a valid to attribute.
	 * 
	 * @param attribute the CssAttribute
	 * @return an array of all names
	 * @throws NullPointerException when the attribute is null
	 */
	protected String[] getMappingNames(CssAttribute attribute) {
		CssMapping[] mappings = attribute.getMappings();
		ArrayList mappingsList = new ArrayList();
		for (int i = 0; i < mappings.length; i++) {
			CssMapping mapping = mappings[i];
			if (mapping.getTo() != null) {
				mappingsList.add( mapping.getFrom() );
			}
		}
		String[] names = (String[]) mappingsList.toArray( new String[ mappingsList.size() ] );
		Arrays.sort(names);
		return names;
	}
	
	public CssAttributesEditor getAttributesEditor( CssAttribute attribute, String fromName, EditStyle style ) {
		ParameterizedCssMapping mapping = (ParameterizedCssMapping) attribute.getMapping(fromName);
		if (mapping == null) {
			return null;
		}
		CssAttribute[] parameters = mapping.getParameters();
		CssAttributesEditor editor = new CssAttributesEditor( this.styleEditor );
		editor.setName( fromName );
		editor.setParent( this );
		editor.setCssAttributes(parameters);
		editor.setStyle(style);
		return editor;
	}
	

	protected Object instantiate(CssAttribute attribute, String from, Object[] attributeValues) {
		ParameterizedCssMapping mapping = (ParameterizedCssMapping) attribute.getMapping(from);
		String className = mapping.getTo();
		try {
//			System.out.println("instantiating " + className + " with attributeValues=");
//			for (int i = 0; i < attributeValues.length; i++) {
//				Object object = attributeValues[i];
//				System.out.println(i + ": " + object );
//			}
			return ReflectionUtil.newInstance(Class.forName(className), attributeValues);
		} catch (Exception e) {
			// TODO robertvirkus handle IllegalArgumentException
			e.printStackTrace();
		}
		return null;
	}

	
	

}
