/*
 * Created on 18-Oct-2004 at 23:01:01.
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

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import de.enough.polish.util.CastUtil;
import de.enough.polish.util.TextUtil;

/**
 * <p>Represents a data-type. A type can be user defined and consist of other types.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        18-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataType {
	
	public static final int BYTE_ID = 1;
	public static final int SHORT_ID = 2;
	public static final int INTEGER_ID = 3;
	public static final int LONG_ID = 4;
	public static final int ASCII_STRING_ID = 5;
	public static final int BOOLEAN_ID = 6;
	public static final int USER_DEFINED_ID = 7;

	public static final DataType BYTE = new DataType("Byte", BYTE_ID, 1);
	public static final DataType SHORT = new DataType("Short", SHORT_ID, 2);
	public static final DataType INTEGER = new DataType("Integer", INTEGER_ID, 4);
	public static final DataType LONG = new DataType("Long", LONG_ID, 8);
	public static final DataType ASCII_STRING = new DataType("ASCII-String", ASCII_STRING_ID, -1);
	public static final DataType BOOLEAN = new DataType("Boolean", BOOLEAN_ID, 1);

	private final String name;
	private final int type;
	private final DataType[] subtypes;
	private final int numberOfBytes;
	private final boolean isDynamic;

	/**
	 * Creates a new simple type.
	 * 
	 * @param name the name of this type
	 * @param type the type-ID
	 * @param numberOfBytes the number of bytes which are needed to store this type, -1 if undefined
	 */
	public DataType( String name, int type, int numberOfBytes ) {
		super();
		this.type = type;
		this.numberOfBytes = numberOfBytes;
		this.subtypes = null;
		this.name = name;
		this.isDynamic = (numberOfBytes == -1);
	}

	/**
	 * Creates a new user defined type.
	 * 
	 * @param name the name of this type
	 * @param subtypes the subtypes of this user defined type.
	 */
	public DataType( String name, DataType[] subtypes ) {
		super();
		this.type = USER_DEFINED_ID;
		this.subtypes = subtypes;
		int bytes = 0;
		boolean dynamic = false;
		for (int i = 0; i < subtypes.length; i++) {
			DataType subtype = subtypes[i];
			bytes += subtype.numberOfBytes;
			if (subtype.isDynamic) {
				dynamic = true;
			}
		}
		this.name = name;
		this.isDynamic = dynamic;
		if (dynamic) {
			this.numberOfBytes = -1; 
		} else {
			this.numberOfBytes = bytes;	
		}
	}
	
	/**
	 * @param typeElement
	 * @param dataManager
	 */
	public DataType(Element typeElement, DataManager dataManager) {
		this.type = USER_DEFINED_ID;
		this.name = typeElement.getAttributeValue("name");
		List subtypesList = typeElement.getChildren("subtype");
		int bytes = 0;
		this.subtypes = new DataType[ subtypesList.size() ];
		boolean dynamic = false;
		int index = 0;
		for (Iterator iter = subtypesList.iterator(); iter.hasNext();) {
			Element subtypeElement = (Element) iter.next();
			String subtypeName = subtypeElement.getAttributeValue("type");
			DataType subtype = dataManager.getDataType( subtypeName );
			if (subtype == null) {
				throw new IllegalArgumentException("Unable to init data-type [" + this.name + "]: the subtype [" + subtypeName + "] is not known (yet).");
			}
			this.subtypes[index] = subtype;
			bytes += subtype.numberOfBytes;
			index++;
			if (subtype.isDynamic) {
				dynamic = true;
			}
		}
		this.isDynamic = dynamic;
		if (dynamic) {
			this.numberOfBytes = -1; 
		} else {
			this.numberOfBytes = bytes;	
		}
	}

	public int getType() {
		return this.type;
	}
	
	public DataType[] getSubtypes() {
		return this.subtypes;
	}
	
	public boolean isUserDefined() {
		return (this.type == USER_DEFINED_ID);
	}
	
	public int getNumberOfBytes() {
		return this.numberOfBytes;
	}

	/**
	 * Retrieves the number of bytes for dynamic types, e.g. Strings
	 * 
	 * @param data the raw data
	 * @param offset the offset where this dynamic type starts
	 * @return the number of bytes for this type.
	 */
	public int getNumberOfBytes(byte[] data, int offset) {
		if (this.type == ASCII_STRING_ID ) {
			int numberOfChars = CastUtil.toUnsignedInt( data[ offset ] );
			return (numberOfChars + 1);
		} else if (this.type == USER_DEFINED_ID) {
			int bytes = 0;
			for (int i = 0; i < this.subtypes.length; i++) {
				DataType subtype = this.subtypes[i];
				if (subtype.isDynamic) {
					bytes += subtype.getNumberOfBytes(data, offset + bytes);
				} else {
					bytes += subtype.numberOfBytes;
				}
			}
			return bytes;
		} else {
			throw new IllegalStateException("The type [" + this.name + "] is either not supported or not a dynamic type.");
		}
	}
	
	public String toString( byte[] data ) {
		switch (this.type) {
			case BYTE_ID: 
				return Byte.toString( data[0] );
			case SHORT_ID:
				short shortValue = (short) ( CastUtil.toUnsignedInt(data[0]) |  (data[1] << 8));
				return Short.toString(shortValue);
			case INTEGER_ID:
				int intValue = CastUtil.toUnsignedInt(data[0]) | (CastUtil.toUnsignedInt(data[1]) << 8) | (CastUtil.toUnsignedInt(data[2]) << 16) | (data[3] << 24) ;
				return Integer.toString(intValue);
			case LONG_ID:
				long longValue = CastUtil.toUnsignedInt(data[0]) | (CastUtil.toUnsignedInt(data[1]) << 8) | (CastUtil.toUnsignedInt(data[2]) << 16) | (data[3] << 24) ;
				return Long.toString(longValue);
			case BOOLEAN_ID:
				if (data[0] == 0) {
					return "false";
				} else {
					return "true";
				}
			case ASCII_STRING_ID:
				return new String( data, 1, CastUtil.toUnsignedInt(data[0]));
			case USER_DEFINED_ID:
				StringBuffer buffer = new StringBuffer();
				int startIndex = 0;
				for (int i = 0; i < this.subtypes.length; i++) {
					DataType dataType = this.subtypes[i];
					byte[] subData = new byte[ dataType.numberOfBytes ];
					System.arraycopy(data, startIndex, subData, 0,  dataType.numberOfBytes );
					startIndex += dataType.numberOfBytes; 
					buffer.append( dataType.toString( subData ) );
					if (i != this.subtypes.length - 1) {
						buffer.append(", ");
					}
				}
				return buffer.toString();
			default: 
				throw new IllegalStateException( "The type [" + this.name + "] is currently not supported.");
		}
	}
	
	public byte[] parseDataString( String value ) {
		switch (this.type) {
		case BYTE_ID: 
			return new byte[] { Byte.parseByte( value ) };
		case SHORT_ID:
			short shortValue = Short.parseShort(value);
			byte[] data = new byte[ 2 ];
			data[0] = (byte) (shortValue & 0x00FF);
			data[1] = (byte) ((shortValue >>> 8) & 0x00FF);
			return data; 
		case INTEGER_ID:
			int intValue = Integer.parseInt(value);
			data = new byte[ 2 ];
			data[0] = (byte) (intValue & 0x00FF);
			data[1] = (byte) (intValue >>> 8 );
			return data; 
		case LONG_ID:
			long longValue = Long.parseLong(value);
			data = new byte[ 4 ];
			data[0] = (byte) (longValue & 0x000000FF);
			data[1] = (byte) ((longValue >>> 8) & 0x0000FF);
			data[2] = (byte) ((longValue >>> 16) & 0x00FF);
			data[3] = (byte) (longValue >>> 24);
			return data;
		case BOOLEAN_ID:
			if ("true".equals( value )) {
				return new byte[]{ 1 };
			} else {
				return new byte[]{ 0 };
			}
		case ASCII_STRING_ID:
			int stringLength = value.length();
			if (stringLength > 255 ) {
				throw new IllegalArgumentException("The ASCII-String \"" + value + "\" has too many characters: maximum is 255 - this string has [" + stringLength + "] characters.");
			}
			//System.out.println("getting bytes data for string [" + value + "] with a length of " + stringLength );
			data = new byte[ stringLength + 1];
			data[0] = (byte) stringLength;
			byte[] charData = value.getBytes();
			System.arraycopy( charData, 0, data, 1, stringLength );
			return data;
		case USER_DEFINED_ID:
			data = new byte[ this.numberOfBytes ];
			String[] subvalues = TextUtil.split( value, ", ");
			int startIndex = 0;
			for (int i = 0; i < this.subtypes.length; i++) {
				DataType dataType = this.subtypes[i];
				String subvalue = subvalues[i];
				byte[] subData = dataType.parseDataString( subvalue );
				System.arraycopy(subData, 0, data, startIndex,  dataType.numberOfBytes );
				startIndex += dataType.numberOfBytes; 
			}
			return data;
		default: 
			throw new IllegalStateException( "The type [" + this.name + "] is currently not supported.");
	}
	}

	/**
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param data
	 * @return
	 */
	public int getIntRepresentation(byte[] data) {
		switch (this.type) {
			case BYTE_ID: 
				return data[0];
			case SHORT_ID:
				short shortValue = (short) ( CastUtil.toUnsignedInt( data[0] )  | (data[1] << 8));
				return shortValue;
			case INTEGER_ID:
				int intValue = CastUtil.toUnsignedInt( data[0] ) | (CastUtil.toUnsignedInt(data[1]) << 8) | (CastUtil.toUnsignedInt(data[2]) << 16) | (data[3] << 24) ;
				return intValue;
			case LONG_ID:
				long longValue = data[0] | (data[1] << 8) | (data[2] << 16) | (data[3] << 24) ;
				return (int) longValue;
			case BOOLEAN_ID:
				if (data[0] == 0) {
					return 0;
				} else {
					return 1;
				}
			default: 
				throw new IllegalStateException( "The data-type [" + this.name + "] cannot be used for calculations.");
		}	
	}
	
	/**
	 * @return
	 */
	public String getXmlRepresentation() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t<type name=\"")
			.append( this.name )
			.append( "\">");
		for (int i = 0; i < this.subtypes.length; i++) {
			DataType subtype = this.subtypes[i];
			buffer.append("<subtype type=\"")
				.append( subtype.getName() )
				.append( "\" />");
		}
		buffer.append("</type>");
		return buffer.toString();
	}
	
	public String toString() {
		return this.name;
	}
	
	public static DataType[] getDefaultTypes() {
		return new DataType[]{ BYTE, SHORT, INTEGER, LONG, BOOLEAN, ASCII_STRING };
	}
	
	/**
	 * Determines when this type has a dynamic (i.e. -1) length
	 * @return true when this type is dynamic 
	 */
	public boolean isDynamic() {
		return this.isDynamic;
	}


}
