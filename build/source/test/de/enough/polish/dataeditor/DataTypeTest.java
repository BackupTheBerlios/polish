/*
 * Created on 24-Feb-2005 at 21:56:12.
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
package de.enough.polish.dataeditor;

import junit.framework.TestCase;
import java.io.*;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        24-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DataTypeTest extends TestCase {

	public DataTypeTest(String name) {
		super(name);
	}
	
	public void testDataFile() throws IOException {
		Test test = new Test( new FileInputStream("/home/enough/J2ME-Polish/bin/test" ));
		assertEquals( 110, test.byteValue );
		assertEquals( 1265, test.shortValue );
		assertEquals( 51, test.posY );
		assertEquals( 31, test.posX );
		assertEquals( 5660, test.intValue );
	}
	
//	 Generated by J2ME Polish at Thu Feb 24 21:54:59 CET 2005
	final class Test {
		public final byte byteValue;
		public final int posX;
		public final short posY;
		public final int intValue;
		public final short shortValue;

		public Test( InputStream plainIn )
		throws IOException
		{
			DataInputStream in = new DataInputStream( plainIn );
			try {
			this.byteValue = in.readByte();
			this.shortValue = in.readShort();
			this.posY = (short) in.readUnsignedByte();
			this.posX = in.readUnsignedShort();
			this.intValue = in.readInt();
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				//#debug error
				System.out.println("Unable to load data" + e);
				throw new IOException( e.toString() );
			} finally {
				try {
					in.close();
				} catch (Exception e) {
					//#debug error
					System.out.println("Unable to close input stream" + e);
				}
			}
		} // end of constructor 


	} // end of class


}
