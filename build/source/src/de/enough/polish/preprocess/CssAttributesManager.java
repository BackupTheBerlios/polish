/*
 * Created on 19-Aug-2004 at 15:21:38.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * <p>Manages all CSS attribute definitions loaded from [standard-css-attributes.xml] and [custom-css-aatributes.xml].</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        19-Aug-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssAttributesManager {
	
	private final HashMap attributesByName;

	/**
	 * Creates a new CSS attributes manager.
	 * 
	 * @param is the input stream for the [standard-css-attributes.xml] file.
	 */
	public CssAttributesManager( InputStream is ) {
		this.attributesByName = new HashMap();
		addCssAttributes( is );
	}

	/**
	 * Adds the attributes from the given xml-file.
	 * 
	 * @param is the input stream for the [standard-css-attributes.xml] file.
	 * @throws BuildException when there are duplicate CSS entries or the InputStream is null. 
	 */
	public void addCssAttributes(InputStream is) {
		if (is == null) {
			throw new BuildException("Unable to load CSS attributes from NULL input stream. Please report this error to j2mepolish@enough.de.");
		}
		SAXBuilder builder = new SAXBuilder( false );
		Document document;
		try {
			document = builder.build( is );
		} catch (JDOMException e) {
			throw new BuildException("Unable to read [custom-css-attributes.xml]: " + e, e );
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Unable to read [custom-css-attributes.xml] or [standard-css-attributes.xml]: " + e, e );
		}
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element definition = (Element) iter.next();
			CssAttribute attribute = new CssAttribute( definition );
			Object existingAttribute = this.attributesByName.get( attribute.getName() );
			if (existingAttribute != null) {
				throw new BuildException("Duplicate CSS-attributes-definition: The attribute [" + attribute.getName() + "] is defined twice. Please remove one definition from [custom-css-attributes.xml]." );
			}
			this.attributesByName.put( attribute.getName(), attribute );
		}
	}
	
	/**
	 * Retrieves the specified attribute.
	 * 
	 * @param name the name of the attribute
	 * @return either the attribute or null, when the specified attribute has not been defined within the appropriate XML files.
	 */
	public CssAttribute getAttribute( String name ) {
		return (CssAttribute) this.attributesByName.get( name );
	}

	/**
	 * @return all registered CSS attributes
	 */
	public CssAttribute[] getAttributes() {
		return (CssAttribute[]) this.attributesByName.values().toArray(  new CssAttribute[ this.attributesByName.size() ] );
	}

	/**
	 * Retrieves all attributes for the given class.
	 * 
	 * @param fullClassName the name of the class, e.g. "de.enough.polish.ui.StringItem"
	 * @return all applicable attributes for the given class
	 */
	public CssAttribute[] getApplicableAttributes( String fullClassName ) {
		ArrayList list = new ArrayList();
		CssAttribute[] attributes = getAttributes();
		int lastDotPos = fullClassName.lastIndexOf('.');
		String className = fullClassName; 
		if (lastDotPos != -1) {
			className = fullClassName.substring( lastDotPos + 1 );
		}
		for (int i = 0; i < attributes.length; i++) {
			CssAttribute attribute = attributes[i];
			if ( attribute.appliesTo( className ) || attribute.appliesTo( fullClassName ) ) {
				list.add( attribute );
			}
		}
		return (CssAttribute[]) list.toArray( new CssAttribute[ list.size() ] );
	}

	/**
	 * Retrieves the attribute with the given ID-key.
	 * 
	 * @param id the ID of the desired attribute
	 * @return the attribute, null when it was not found
	 */
	public CssAttribute getAttribute(int id) {
		CssAttribute[] attributes = getAttributes();
		for (int i = 0; i < attributes.length; i++) {
			CssAttribute attribute = attributes[i];
			if (attribute.getId() == id) {
				return attribute;
			}
		}
		return null;
	}

}
