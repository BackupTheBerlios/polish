/*
 * Created on 22-Apr-2005 at 18:54:42.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.polish;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * <p>Manages the available extensions.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        22-Apr-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ExtensionManager {
	
	public static final String TYPE_PLUGIN = "plugin";
	public static final String TYPE_PROPERTY_FUNCTION = "propertyfunction";
	public static final String TYPE_PREPROCESSOR = "preprocessor";
	public static final String TYPE_POSTCOMPILER = "postcompiler";
	public static final String TYPE_OBFUSCATOR = "obfucator";
	public static final String TYPE_RESOURCE_RENAMER = "resourcerenamer";
	public static final String TYPE_PACKAGER = "packager";
	public static final String TYPE_FINALIZER = "finalizer";
	
	
	private final Map repository;
	private final Project antProject;

	/**
	 * @param antProject
	 * @param is
	 * @throws IOException
	 * @throws JDOMException
	 * 
	 */
	public ExtensionManager( Project antProject, InputStream is ) throws JDOMException, IOException {
		super();
		this.antProject = antProject;
		this.repository = new HashMap();
		loadExtensions( is );
	}
	
	private void loadExtensions( InputStream is ) 
	throws JDOMException, IOException 
	{
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( is );
		List xmlList = document.getRootElement().getChildren();
		for (Iterator iter = xmlList.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			try {
				Extension extension = Extension.getInstance( element, this.antProject, this );
				Map store = (Map) this.repository.get( extension.getType() );
				if ( store == null ) {
					store = new HashMap();
					this.repository.put( extension.getType(), store );
				}
				store.put( extension.getName(), extension );
			} catch (Exception e) {
				System.out.println("Unable to load extension [" + element.getChildTextTrim("class") + "]: " + e.toString() );
			}
		}
	}
	
	/**
	 * Retrieves an extension.
	 * 
	 * @param type the type of the extension, e.g. "propertyfunction"
	 * @param name the name of the extensio, e.g. "uppercase"
	 * @return the extension, null when the type or the name is not known
	 */
	public Extension getExtension( String type, String name ) { 
		Map store = (Map) this.repository.get( type );
		if ( store == null ) {
			return null;
		} else {
			return (Extension) store.get( name );
		}
		
	}
	
	
	public void preInitialize( Device device, Locale locale ) {
		// call preInitialize on the registered plugins:
	}
	
	

}
