/*
 * Created on 19-Oct-2004 at 13:47:47.
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
package de.enough.polish.dataeditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.enough.polish.util.FileUtil;

/**
 * <p>Manages a complete data-file/definition with data-types, data-entries and so on.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        19-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataManager {
	
	private String definitionName;
	private String dataName;
	private final ArrayList types;
	private final Map typesByName;
	private final ArrayList entries;
	private final Map entriesByName;

	/**
	 * 
	 */
	public DataManager() {
		super();
		this.types = new ArrayList();
		this.typesByName = new HashMap();
		// add default-types:
		DataType[] defaultTypes = DataType.getDefaultTypes();
		for (int i = 0; i < defaultTypes.length; i++) {
			DataType type = defaultTypes[i];
			this.typesByName.put( type.getName(), type );			
		}
		this.entries = new ArrayList();
		this.entriesByName = new HashMap();
	}
	
	public void loadDefinition( File file ) 
	throws JDOMException, IOException
	{
		this.definitionName = file.getName();
		FileInputStream in = new FileInputStream( file );
		SAXBuilder builder = new SAXBuilder( false );
		Document document = builder.build( in );
		Element root = document.getRootElement();
		if ( !root.getName().equals("data-definition") ) {
			throw new JDOMException( "Invalid definition-file: the root-element needs to be <data-definition>.");
		}
		clear();
		// init user-defined data-types:
		List typesList = root.getChildren("type");
		for (Iterator iter = typesList.iterator(); iter.hasNext();) {
			Element typeElement = (Element) iter.next();
			DataType type = new DataType( typeElement, this );
			addDataType( type );
		}
		// init data-entries:
		List entriesList = root.getChildren("entry");
		for (Iterator iter = entriesList.iterator(); iter.hasNext();) {
			Element entryElement = (Element) iter.next();
			DataEntry entry = new DataEntry( entryElement, this );
			addDataEntry( entry );
		}
	}
	
	public void saveDefinition( File file )
	throws IOException
	{
		this.definitionName = file.getName();
		ArrayList linesList = new ArrayList();
		// save start of file:
		linesList.add( "<!-- created by J2ME Polish on " + (new Date()).toString() + " -->" );
		linesList.add( "<data-definition>" );
		// save user-defined types:
		DataType[] dataTypes = getUserDefinedTypes();
		for (int i = 0; i < dataTypes.length; i++) {
			DataType type = dataTypes[i];
			linesList.add( type.getXmlRepresentation() );
		}
		// save data-entries:
		DataEntry[] dataEntries = getDataEntries();
		for (int i = 0; i < dataEntries.length; i++) {
			DataEntry entry = dataEntries[i];
			linesList.add( entry.getXmlRepresentation() );
		}
		// save end of file:
		linesList.add( "</data-definition>" );
		String[] lines = (String[]) linesList.toArray( new String[ linesList.size() ] );
		FileUtil.writeTextFile( file, lines );
	}
	

	public void saveData( File file )
	throws IOException
	{
		this.dataName = file.getName();
		FileOutputStream out = new FileOutputStream( file );
		DataEntry[] dataEntries = getDataEntries();
		for (int i = 0; i < dataEntries.length; i++) {
			DataEntry entry = dataEntries[i];
			if (entry.getNumberOfBytes() > 0) {
				out.write( entry.getData() );
			}
		}
		out.close();
	}

	public void loadData( File file )
	throws IOException
	{
		this.dataName = file.getName();
		byte[] data = new byte[ (int) file.length() ];
		FileInputStream in = new FileInputStream( file );
		int length = in.read( data );
		if (length != data.length ) {
			throw new IllegalStateException("Unable to load data from [" + file.getAbsolutePath() + "]: mismatch of file.length and read data." );
		}
		in.close();
		DataEntry[] dataEntries = getDataEntries();
		int offset = 0;
		for (int i = 0; i < dataEntries.length; i++) {
			DataEntry entry = dataEntries[i];
			entry.setData( data, offset );
			offset += entry.getNumberOfBytes();
		}
	}
	
	public void addDataEntry( DataEntry entry ) {
		this.entries.add( entry );
		this.entriesByName.put( entry.getName(), entry );
	}

	public void insertDataEntry( int index, DataEntry entry ) {
		this.entries.add( index , entry );
	}
	
	public boolean removeDataEntry( DataEntry entry ) {
		this.entriesByName.remove( entry.getName() );
		return this.entries.remove( entry );
	}
	
	public DataEntry removeDataEntry( int index ) {
		DataEntry entry = (DataEntry) this.entries.remove(index);
		if (entry != null) {
			this.entriesByName.remove( entry.getName() );
		}
		return entry;
	}
	
	public DataEntry[] getDataEntries() {
		return (DataEntry[]) this.entries.toArray( new DataEntry[ this.entries.size() ] ); 
	}
	
	public DataEntry getDataEntry( String name ) {
		DataEntry[] dataEntries = getDataEntries();
		for (int i = 0; i < dataEntries.length; i++) {
			DataEntry entry = dataEntries[i];
			if ( name.equals( entry.getName() )) {
				return entry;
			}
		}
		return null;
	}
	
	public CountTerm createCountTerm( String term ) {
		return CountTerm.createTerm( term, this );
	}
	
	public void addDataType( DataType type ) {
		this.types.add( type );
		this.typesByName.put( type.getName(), type );
	}

	/**
	 * @param name
	 * @return
	 */
	public DataType getDataType(String name ) {
		return (DataType) this.typesByName.get( name );
	}
	
	public String getDefinitionName() {
		return this.definitionName;
	}
	
	public String getDataName() {
		return this.dataName;
	}

	/**
	 * @return
	 */
	public DataType[] getUserDefinedTypes() {
		return (DataType[]) this.types.toArray( new DataType[ this.types.size() ] );
	}

	/**
	 * @return
	 */
	public int getNumberOfEntries() {
		return this.entries.size();
	}

	/**
	 * @param index
	 * @return
	 */
	public DataEntry getDataEntry(int index) {
		return (DataEntry) this.entries.get( index );
	}

	/**
	 * 
	 */
	public void clear() {
		this.types.clear();
		this.entries.clear();
		this.entriesByName.clear();
		this.typesByName.clear();
		// add default-types:
		// add default-types:
		DataType[] defaultTypes = DataType.getDefaultTypes();
		for (int i = 0; i < defaultTypes.length; i++) {
			DataType type = defaultTypes[i];
			this.typesByName.put( type.getName(), type );			
		}
	}

	public DataType[] getDataTypes() {
		if (this.types == null) {
			return DataType.getDefaultTypes();
		}
		ArrayList list = new ArrayList();
		// add default-types:
		DataType[] defaultTypes = DataType.getDefaultTypes();
		for (int i = 0; i < defaultTypes.length; i++) {
			DataType type = defaultTypes[i];
			list.add( type );			
		}
		// add user-defined types:
		list.addAll( this.types );
		return (DataType[]) list.toArray( new DataType[ list.size() ] );
	}

	/**
	 * Moves the specified data entry one up.
	 *  
	 * @param index the index of the data entry
	 * @return true when the entry could be pushed upwards
	 */
	public boolean pushUpDataEntry(int index) {
		if ( index > 0 ) {
			DataEntry entry = (DataEntry) this.entries.remove(index);
			if (entry != null) {
				this.entries.add( --index, entry );
				return true;
			}
		}
		return false;
	}

	/**
	 * Moves the specified data entry one down.
	 *  
	 * @param index the index of the data entry
	 * @return true when the entry could be pushed downwards
	 */
	public boolean pushDownDataEntry(int index) {
		if ( index < this.entries.size() - 1 ) {
			DataEntry entry = (DataEntry) this.entries.remove(index);
			if (entry != null) {
				this.entries.add( ++index, entry );
				return true;
			}
		}
		return false;
	}
	
	public String generateJavaCode( String packageName, String className ) {
		StringBuffer buffer = new StringBuffer( this.entries.size() * 2 * 100 );
		// add class-declaration:
		buffer.append("// Generated by J2ME Polish at ").append( new Date().toString() ).append('\n');
		if (packageName != null && packageName.length() > 0) {
			buffer.append("package ").append( packageName ).append(";\n");
		}
		buffer.append("import java.io.*;\n");
		buffer.append("public final class ").append( className ).append(" {\n");
		// add field-definitions:
		DataEntry[] myEntries = getDataEntries();
		for (int i = 0; i < myEntries.length; i++) {
			DataEntry entry = myEntries[i];
			entry.addInstanceDeclaration(buffer);
		}
		// add the constructor:
		buffer.append("\n\tpublic ").append( className ).append("( String dataUrl )\n");
		buffer.append("\tthrows IOException\n\t{\n");
		buffer.append("\t\tInputStream in = getClass().getResourceAsStream( dataUrl );\n");
		// check if resource really exists:
		buffer.append("\t\tif (in == null) {\n" )
			.append("\t\t\tthrow new IllegalArgumentException(\"Unable to open resource [\" + dataUrl + \"]: resource not found: does it start with \\\"/\\\"?\");\n")
			.append("\t\t}\n");
			
		// add the loading of the data:
		for (int i = 0; i < myEntries.length; i++) {
			DataEntry entry = myEntries[i];
			entry.addCode(buffer);
		}		
		// add constructor-end:
		buffer.append("\t} // end of constructor \n\n");
		// add inner classes:
		HashMap internalClassByName = new HashMap();
		for (int i = 0; i < myEntries.length; i++) {
			DataEntry entry = myEntries[i];
			entry.getType().addInternalClass(internalClassByName, buffer);
		}		
		buffer.append("\n} // end of class\n");
		return buffer.toString();
	}

}
