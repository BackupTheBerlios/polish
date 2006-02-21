/*
 * Created on 30-Jan-2006 at 20:37:57.
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

public class ArraysTest extends TestCase {

	public ArraysTest(String name) {
		super(name);
	}
	
	public void testShellSort() {
		System.out.println("testing shell sort");
		String[] j2meObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		String[] j2seObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		Arrays.shellSort( j2meObjects );
		java.util.Arrays.sort( j2seObjects );
		assertEquals( j2meObjects.length, j2seObjects.length );
		for (int i = 0; i < j2meObjects.length; i++) {
			String j2me = j2meObjects[i];
			String j2se = j2seObjects[i];
			assertEquals( j2se, j2me );
			//System.out.println( j2me );
		}

	
		j2meObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		j2seObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		Arrays.shellSort( j2meObjects );
		java.util.Arrays.sort( j2seObjects );
		assertEquals( j2meObjects.length, j2seObjects.length );
		for (int i = 0; i < j2meObjects.length; i++) {
			String j2me = j2meObjects[i];
			String j2se = j2seObjects[i];
			assertEquals( j2se, j2me );
			//System.out.println( j2me );
		}
	}

	
	public void testSort() {
		System.out.println("testing sort");
		String[] j2meObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		String[] j2seObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		Arrays.sort( j2meObjects );
		java.util.Arrays.sort( j2seObjects );
		assertEquals( j2meObjects.length, j2seObjects.length );
		for (int i = 0; i < j2meObjects.length; i++) {
			String j2me = j2meObjects[i];
			String j2se = j2seObjects[i];
			assertEquals( j2se, j2me );
			System.out.println( j2me );
		}

	
		j2meObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		j2seObjects = new String[]{ "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim", "abc", "aaa", "aad", "acd", "ddd", "berta", "emil", "alfred", "gerhard", "ricky", "robert", "tim" };
		Arrays.sort( j2meObjects );
		java.util.Arrays.sort( j2seObjects );
		assertEquals( j2meObjects.length, j2seObjects.length );
		for (int i = 0; i < j2meObjects.length; i++) {
			String j2me = j2meObjects[i];
			String j2se = j2seObjects[i];
			assertEquals( j2se, j2me );
			System.out.println( j2me );
		}
	}

}
