/*
 * Created on 29-Oct-2004 at 19:24:06.
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
package de.enough.polish.util;

import junit.framework.TestCase;

/**
 * <p>Tests the CastUtil helper class.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        29-Oct-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CastUtilTest extends TestCase {


	public CastUtilTest(String name) {
		super(name);
	}
	
	public void testToUnsignedInt() {
		byte b = (byte) 0x0F;
		assertEquals( 0x0F, CastUtil.toUnsignedInt( b ) );
		b = (byte) 0xFF;
		assertEquals( 0xFF, CastUtil.toUnsignedInt( b ) );
		b = (byte) 0xF0;
		assertEquals( 0xF0, CastUtil.toUnsignedInt( b ) );
		b = (byte) 0xF1;
		assertEquals( 0xF1, CastUtil.toUnsignedInt( b ) );
		b = (byte) 0x1F;
		assertEquals( 0x1F, CastUtil.toUnsignedInt( b ) );
	}

}
