/*
 * Created on 11-Jan-2005 at 18:59:03.
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
package de.enough.polish.preprocess.custom;

import java.util.regex.Matcher;

import de.enough.polish.util.StringList;
import junit.framework.TestCase;

/**
 * <p>Tests the polish preprocessor.</p>
 *
 * <p>copyright Enough Software 2005</p>
 * <pre>
 * history
 *        11-Jan-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishPreprocessorTest extends TestCase {

	
	public PolishPreprocessorTest(String name) {
		super(name);
	}
	
	public void testSetCurrentItem() {
		
		String input = "		display.setCurrentItem( item );";
		Matcher matcher = PolishPreprocessor.SET_CURRENT_ITEM_PATTERN.matcher( input ); 
		assertTrue( matcher.find() );
		assertEquals( "display.setCurrentItem( item )", matcher.group() );
		
		input = "		this.display.setCurrentItem( this.item );";
		matcher = PolishPreprocessor.SET_CURRENT_ITEM_PATTERN.matcher( input ); 
		assertTrue( matcher.find() );
		assertEquals( "this.display.setCurrentItem( this.item )", matcher.group() );

		input = "		display . setCurrentItem( this.item );";
		matcher = PolishPreprocessor.SET_CURRENT_ITEM_PATTERN.matcher( input ); 
		assertTrue( matcher.find() );
		assertEquals( "display . setCurrentItem( this.item )", matcher.group() );
	
		PolishPreprocessor preprocessor = new PolishPreprocessor();
		preprocessor.isUsingPolishGui = true;
		String[] lines = new String[] {
				"	public void testMethod() {",
				"		this.display.setCurrentItem( this.item );",
				"		display.setCurrentItem( item );",
				"		display.setCurrentItem( getMyItem() );",
				"	}"
		};
		StringList list = new StringList( lines );
		
		preprocessor.processClass(list, "TestClass");
		
		lines = list.getArray();
		
		assertEquals(  "		this.item.show( this.display );", lines[1] );
		assertEquals(  "		item.show( display );", lines[2] );
		assertEquals(  "		getMyItem().show( display );", lines[3] );
	}

}
