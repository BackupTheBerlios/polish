/*
 * Created on 05-Sep-2004 at 21:55:37.
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
 * <p>Tests the ConvertUtil</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        05-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ConvertUtilTest extends TestCase {

	public ConvertUtilTest(String name) {
		super(name);
	}
	
	public void testConvert() {
		assertEquals( new Long(1024), ConvertUtil.convert("1024b", "bytes") );
		assertEquals( new Long(1024), ConvertUtil.convert("1024b", "b") );
		assertEquals( new Double(1), ConvertUtil.convert("1024b", "kilobytes") );
		assertEquals( new Double(1), ConvertUtil.convert("1024b", "kb") );
		assertEquals( new Double( 1D / 1024 ), ConvertUtil.convert("1024b", "megabytes") );
		assertEquals( new Double( 1D / 1024 ), ConvertUtil.convert("1024b", "mb") );
		assertEquals( new Double( 1D / (1024*1024) ), ConvertUtil.convert("1024b", "gigabytes") );
		assertEquals( new Double( 1D / (1024*1024) ), ConvertUtil.convert("1024b", "gb") );
		assertEquals( "NOKIA", ConvertUtil.convert("nokia", "uppercase") );
		assertEquals( "nokia", ConvertUtil.convert("noKIa", "lowercase") );
	}

}
