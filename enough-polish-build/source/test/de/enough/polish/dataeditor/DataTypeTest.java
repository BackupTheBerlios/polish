/*
 * Created on 19-Oct-2004 at 23:18:04.
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

import junit.framework.TestCase;

/**
 * <p></p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        19-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataTypeTest extends TestCase {

	public DataTypeTest(String name) {
		super(name);
	}

	public void testByte() {
		DataType type = DataType.BYTE;
		byte[] data = type.parseDataString( "125" );
		assertEquals( 1, data.length );
		assertEquals( (byte) 125, data[0] );
		assertEquals( 125,  type.getIntRepresentation( data ) );
		String string = type.toString( data );
		assertEquals( "125", string );
		
	}
	
	public void testShort() {
		DataType type = DataType.SHORT;
		byte[] data = type.parseDataString( "127" );
		assertEquals( 2, data.length );
		assertEquals( 127, data[0] );
		assertEquals( 0x00, data[1] );
		assertEquals( 127,  type.getIntRepresentation( data ) );
		String string = type.toString( data );
		assertEquals( "127", string );

		data = type.parseDataString( "128" );
		assertEquals( 2, data.length );
		assertEquals( (byte) 0x80, data[0] );
		assertEquals( (byte) 0x00, data[1] );
		assertEquals( 128,  type.getIntRepresentation( data ) );
		string = type.toString( data );
		assertEquals( "128", string );
		
		data = type.parseDataString( "230" );
		assertEquals( 2, data.length );
		assertEquals( (byte) 0xE6, data[0] );
		assertEquals( (byte) 0x00, data[1] );
		assertEquals( 230,  type.getIntRepresentation( data ) );
		string = type.toString( data );
		assertEquals( "230", string );
	}

}
