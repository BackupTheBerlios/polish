/*
 * Created on 13-Dec-2005 at 16:20:01.
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

public class TextUtilTest extends TestCase {

	public TextUtilTest(String name) {
		super(name);
	}
	
	public static void testReplaceFirst() {
		String input;
		String search;
		String replacement;
		
		input = "hello123world";
		search = "123";
		replacement = " ";
		assertEquals( "hello world", TextUtil.replaceFirst(input, search, replacement));
		
		input = "hello123world123";
		search = "123";
		replacement = " ";
		assertEquals( "hello world123", TextUtil.replaceFirst(input, search, replacement));
		
		input = "123hello world";
		search = "123";
		replacement = "";
		assertEquals( "hello world", TextUtil.replaceFirst(input, search, replacement));

		input = "123hello world";
		search = "123";
		replacement = "comonohbabycommon";
		assertEquals( "comonohbabycommonhello world", TextUtil.replaceFirst(input, search, replacement));
	}
	
	public static void testReplaceLast() {
		String input;
		String search;
		String replacement;
		
		input = "hello123world";
		search = "123";
		replacement = " ";
		assertEquals( "hello world", TextUtil.replaceLast(input, search, replacement));
		
		input = "hello123world123";
		search = "123";
		replacement = " ";
		assertEquals( "hello123world ", TextUtil.replaceLast(input, search, replacement));
		
		input = "123hello world";
		search = "123";
		replacement = "";
		assertEquals( "hello world", TextUtil.replaceLast(input, search, replacement));

		input = "123hello world123";
		search = "123";
		replacement = "";
		assertEquals( "123hello world", TextUtil.replaceLast(input, search, replacement));

		input = "123hello world";
		search = "123";
		replacement = "comonohbabycommon";
		assertEquals( "comonohbabycommonhello world", TextUtil.replaceLast(input, search, replacement));

		input = "123hello world123";
		search = "123";
		replacement = "comonohbabycommon";
		assertEquals( "123hello worldcomonohbabycommon", TextUtil.replaceLast(input, search, replacement));
	}

	
	public static void testReplace() {
		String input;
		String search;
		String replacement;
		
		input = "hello123world";
		search = "123";
		replacement = " ";
		assertEquals( "hello world", TextUtil.replace(input, search, replacement));
		
		input = "hello123world123";
		search = "123";
		replacement = " ";
		assertEquals( "hello world ", TextUtil.replace(input, search, replacement));
		
		input = "123hello world";
		search = "123";
		replacement = "";
		assertEquals( "hello world", TextUtil.replace(input, search, replacement));

		input = "123hello world123";
		search = "123";
		replacement = "";
		assertEquals( "hello world", TextUtil.replace(input, search, replacement));

		input = "123hello world";
		search = "123";
		replacement = "comonohbabycommon";
		assertEquals( "comonohbabycommonhello world", TextUtil.replace(input, search, replacement));

		input = "123hello world123";
		search = "123";
		replacement = "comonohbabycommon";
		assertEquals( "comonohbabycommonhello worldcomonohbabycommon", TextUtil.replace(input, search, replacement));
		
	}

}
