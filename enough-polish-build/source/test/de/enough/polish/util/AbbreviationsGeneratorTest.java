/*
 * Created on 23-Jul-2004 at 13:56:42.
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

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * <p>Tests teh AbbreviationsGenerator</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        23-Jul-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AbbreviationsGeneratorTest extends TestCase {

	public AbbreviationsGeneratorTest(String name) {
		super(name);
	}
	
	public void testCreation() {
		HashMap map = new HashMap();
		AbbreviationsGenerator generator = new AbbreviationsGenerator( map );
		String abbreviation = generator.getAbbreviation("test-property1", true );
		assertEquals( "0", abbreviation );
		assertEquals( "0", map.get("test-property1"));
		assertEquals( 1, map.size() );
		abbreviation = generator.getAbbreviation("test-property2", true );
		assertEquals( "1", abbreviation );
		assertEquals( "1", map.get("test-property2") );
		assertEquals( 2, map.size() );
		
		map = new HashMap();
		generator.setAbbreviationsMap(map);
		abbreviation = generator.getAbbreviation("test-property1", true );
		assertEquals( "0", abbreviation );
		assertEquals( "0", map.get("test-property1"));
		assertEquals( 1, map.size() );
		abbreviation = generator.getAbbreviation("test-property2", true );
		assertEquals( "1", abbreviation );
		assertEquals( "1", map.get("test-property2") );
		assertEquals( 2, map.size() );
	}
	
	public void testLastAbbreviation() {
		HashMap map = new HashMap();
		map.put( "test-property1", "a" );
		map.put( "test-property2", "A" );
		AbbreviationsGenerator generator = new AbbreviationsGenerator( map );
		String abbreviation = generator.getAbbreviation("test-property1", true );
		assertEquals( "a", abbreviation );
		assertEquals( "a", map.get("test-property1"));
		assertEquals( 2, map.size() );
		abbreviation = generator.getAbbreviation("test-property2", true );
		assertEquals( "A", abbreviation );
		assertEquals( "A", map.get("test-property2") );
		assertEquals( 2, map.size() );
		abbreviation = generator.getAbbreviation("test-property3", true );
		assertEquals( "b", abbreviation );
		assertEquals( "b", map.get("test-property3") );
		assertEquals( 3, map.size() );
		
		map.put("test-property4", "0");
		generator.setAbbreviationsMap(map);
		abbreviation = generator.getAbbreviation("test-property1", true );
		assertEquals( "a", abbreviation );
		assertEquals( "a", map.get("test-property1"));
		assertEquals( 4, map.size() );
		abbreviation = generator.getAbbreviation("test-property2", true );
		assertEquals( "A", abbreviation );
		assertEquals( "A", map.get("test-property2") );
		assertEquals( 4, map.size() );
		abbreviation = generator.getAbbreviation("test-property3", true );
		assertEquals( "b", abbreviation );
		assertEquals( "b", map.get("test-property3") );
		assertEquals( 4, map.size() );
		abbreviation = generator.getAbbreviation("test-property4", true );
		assertEquals( "0", abbreviation );
		assertEquals( "0", map.get("test-property4") );
		assertEquals( 4, map.size() );
		abbreviation = generator.getAbbreviation("test-property5", true );
		assertEquals( "c", abbreviation );
		assertEquals( "c", map.get("test-property5") );
		assertEquals( 5, map.size() );
		
		map.put("test-property6", "c0");
		map.put("test-property7", "C1");
		generator.setAbbreviationsMap(map);
		abbreviation = generator.getAbbreviation("test-property8", true );
		assertEquals( "c1", abbreviation );
		assertEquals( "c1", map.get("test-property8") );

		map.put("test-property6", "cx");
		map.put("test-property7", "Cx");
		generator.setAbbreviationsMap(map);
		abbreviation = generator.getAbbreviation("test-property9", true );
		assertEquals( "cy", abbreviation );
		assertEquals( "cy", map.get("test-property9") );
		
		map.put("test-property6", "cz");
		map.put("test-property7", "Cz");
		generator.setAbbreviationsMap(map);
		abbreviation = generator.getAbbreviation("test-property10", true );
		assertEquals( "d0", abbreviation );
		assertEquals( "d0", map.get("test-property10") );
	}

}
