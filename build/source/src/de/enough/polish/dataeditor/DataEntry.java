/*
 * Created on 18-Oct-2004 at 23:15:02.
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

import java.util.ArrayList;

import org.jdom.Element;

/**
 * <p>Represents a data-field within a binary file.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        18-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataEntry {
	
	private String name;
	private DataType type;
	private int count;
	private CountTerm countTerm;
	private byte[] data;
	private final ArrayList dependentEntries;
	private int[] numbersOfBytes;

	/**
	 * @param name
	 * @param type
	 * 
	 */
	public DataEntry( String name, DataType type ) {
		super();
		this.name = name;
		this.dependentEntries = new ArrayList();
		setType( type );
		setCount( 1 );
	}

	/**
	 * @param entryElement
	 * @param manager
	 */
	public DataEntry(Element entryElement, DataManager manager) {
		this.name = entryElement.getAttributeValue("name");
		this.dependentEntries = new ArrayList();
		DataType dataType = manager.getDataType( entryElement.getAttributeValue("type"));
		setType( dataType );
		setCount( entryElement.getAttributeValue("count"), manager );
	}

	public String[] getDataAsString() {
		String[] result = new String[ this.count ];
		if (!this.type.isDynamic()) {
			int numberOfBytes = this.type.getNumberOfBytes();
			for (int i = 0; i < this.count; i++) {
				byte[] dataPart = new byte[ numberOfBytes ];
				System.arraycopy( this.data, i * numberOfBytes, dataPart, 0, numberOfBytes );
				result[i] = this.type.toString( dataPart ); 
			}
		} else {
			if (this.data == null) {
				for (int i = 0; i < this.count; i++) {
					result[i] = "null";
				}
 			} else {
 				int bytes = 0;
 				for (int i = 0; i < this.count; i++) {
 					int numberOfBytes = this.numbersOfBytes[i];
 					byte[] dataPart = new byte[ numberOfBytes ];
 					System.arraycopy( this.data, bytes, dataPart, 0, numberOfBytes );
 					result[i] = this.type.toString( dataPart );
 					bytes += numberOfBytes;
 				}
 			}
		}
		return result;
	}
	
	public void setDataAsString( String dataStr ) {
		if (this.count != 1) {
			throw new IllegalStateException("Cannot set data as String for a DataEntry with a count different than one (" + this.count + ")" );
		}
		setDataAsString( new String[]{ dataStr } );
	}

	public void setDataAsString( String[] dataStr ) {
		if (dataStr.length != this.count ) {
			throw new IllegalArgumentException("The to-be-set data has a different count [" + dataStr.length + "] than the allowed number [" + this.count + "].");
		}
		if (!this.type.isDynamic()) {
			int numberOfBytes = this.type.getNumberOfBytes();
			for (int i = 0; i < this.count; i++) {
				byte[] dataPart = this.type.parseDataString( dataStr[i] );
				System.arraycopy( dataPart, 0, this.data, i * numberOfBytes, numberOfBytes );
			}
		} else {
			// this is a dynamic entry
			int numberOfBytes = 0;
			byte[][] dataParts = new byte[ this.count ][];
			this.numbersOfBytes = new int[ this.count ];
			for (int i = 0; i < this.count; i++) {
				byte[] dataPart = this.type.parseDataString( dataStr[i] );
				this.numbersOfBytes[i] = dataPart.length;
				numberOfBytes += dataPart.length;
				dataParts[i] = dataPart;
			}
			this.data = new byte[ numberOfBytes ];
			int index = 0;
			for (int i = 0; i < this.count; i++) {
				byte[] dataPart = dataParts[i];
				System.arraycopy( dataPart, 0, this.data, index, dataPart.length );
				index += dataPart.length;
			}
		}
		notifyDependentEntries();
	}

	public void setCount( int count ) {
		if (count == this.count) {
			// ignore the setting of the same count:
			return;
		}
		this.count = count;
		if (!this.type.isDynamic()) {
			byte[] newData = new byte[ this.type.getNumberOfBytes() * count ];
			if (this.data == null || this.data.length == 0) {
				this.data = newData;
				return;
			}
			int minLength = Math.min( newData.length, this.data.length );
			System.arraycopy( this.data, 0, newData, 0, minLength );
			this.data = newData;
			this.countTerm = null;
		} else {
			this.data = null;
		}
	}
	
	public void setCount( CountTerm term ) {
		if (this.countTerm != null) {
			DataEntry[] parents = this.countTerm.getOperants();
			for (int i = 0; i < parents.length; i++) {
				DataEntry entry = parents[i];
				entry.dependentEntries.remove( this );
			}
		}
		setCount( term.calculateCount() );
		this.countTerm = term;
		DataEntry[] parents = term.getOperants();
		for (int i = 0; i < parents.length; i++) {
			DataEntry entry = parents[i];
			entry.dependentEntries.add( this );
		}
	}

	/**
	 * @param value either a number or a count term
	 * @param manager
	 */
	public void setCount(String value, DataManager manager ) {
		try {
			int intValue = Integer.parseInt( value );
			setCount( intValue );
		} catch (NumberFormatException e) {
			// okay, this is more likely a complex count term:
			CountTerm term = CountTerm.createTerm(value, manager );
			setCount( term );
		}
	}
	
	public void updateCount() {
		if (this.countTerm != null) {
			CountTerm term = this.countTerm;
			setCount( this.countTerm.calculateCount() );
			this.countTerm = term;
		}
	}
		
	/**
	 * @param data
	 * @param offset
	 */
	public void setData(byte[] data, int offset) {
		if (!this.type.isDynamic()) {
			byte[] dataSection = new byte[ getNumberOfBytes() ];
			System.arraycopy( data, offset, dataSection, 0 , dataSection.length );
			this.data = dataSection;
		} else {
			// this is a dynamic type:
			this.numbersOfBytes = new int[ this.count ];
			int numberOfBytes = 0;
			for (int i = 0; i < this.count; i++) {
				int number = this.type.getNumberOfBytes( data, offset );
				this.numbersOfBytes[i] = number;
				numberOfBytes += number;
			}
			byte[] dataSection = new byte[ numberOfBytes ];
			System.arraycopy( data, offset, dataSection, 0 , numberOfBytes );
			this.data = dataSection;
		}
		notifyDependentEntries();
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	public void setType( DataType type ) {
		this.type = type;
		if (type.isDynamic()) {
			this.data = null;
		} else {
			this.data = new byte[ type.getNumberOfBytes() * this.count ];
		}
	}
	
	public DataType getType() {
		return this.type;
	}
	
	public int getCount() {
		if (this.countTerm != null ) {
			return this.countTerm.calculateCount();
		} else {
			return this.count;
		}
	}
	
	public String getCountAsString() {
		if (this.countTerm != null) {
			return this.countTerm.toString();
		} else {
			return "" + this.count;
		}
	}
	
	public int getNumberOfBytes() {
		if (this.type.isDynamic()) {
			if (this.numbersOfBytes == null) {
				return 0;
			} else {
				int numberOfBytes = 0;
				for (int i = 0; i < this.numbersOfBytes.length; i++) {
					numberOfBytes += this.numbersOfBytes[i];
				}
				return numberOfBytes;
			}
		} else {
			// this is a normal static type
			return this.count * this.type.getNumberOfBytes();			
		}
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	public int getDataAsInt() {
		return this.type.getIntRepresentation( this.data );
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDataTypeName() {
		return this.type.getName();
	}
	
	private void notifyDependentEntries() {
		DataEntry[] entries = (DataEntry[]) this.dependentEntries.toArray( new DataEntry[ this.dependentEntries.size() ] );
		for (int i = 0; i < entries.length; i++) {
			DataEntry entry = entries[i];
			entry.updateCount();
		}
	}

	/**
	 * @return
	 */
	public String getXmlRepresentation() {
		StringBuffer  buffer = new StringBuffer();
		buffer.append( "\t<entry name=\"" )
			.append( this.name )
			.append( "\" type=\"" )
			.append( this.type.getName() )
			.append( "\" count=\"" );
		if (this.countTerm != null) {
			buffer.append( this.countTerm.toString() );
		} else {
			buffer.append( this.count );
		}
		buffer.append("\" />");
		return buffer.toString();
	}	
	
	public void addInstanceDeclaration( StringBuffer buffer ) {
		this.type.addInstanceDeclaration(getCount(), this.name, buffer);
	}
	
	public void addCode( StringBuffer buffer ) {
		this.type.addCode(getCountAsString(), this.name, buffer);
	}
}
