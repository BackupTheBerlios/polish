/*
 * Created on 05-Dec-2005 at 09:44:29.
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
package de.enough.polish.util;

import junit.framework.TestCase;

public class DrawUtilTest extends TestCase {

	public DrawUtilTest(String name) {
		super(name);
	}
	
	public void testGetGradient() {
		int[] gradient = DrawUtil.getGradient( 0xFFFFFF, 0x000000, 3);
		assertEquals( 3, gradient.length );
		assertEquals( 0xFFFFFF, gradient[0] );
		assertEquals( 0, gradient[2] );

		gradient = DrawUtil.getGradient( 0xFFFFFF, 0x000000, 2);
		assertEquals( 2, gradient.length );
		assertEquals( 0xFFFFFF, gradient[0] );
		assertEquals( 0, gradient[1] );

		
		for (int i = 0; i < gradient.length; i++) {
			int color = gradient[i];
			System.out.println( Integer.toHexString(color));
		}
	}

}
