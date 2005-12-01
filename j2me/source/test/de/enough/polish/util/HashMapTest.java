/*
 * Created on 01-Dec-2005 at 00:04:40.
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


import java.util.Hashtable;

import junit.framework.TestCase;

public class HashMapTest extends TestCase {
	
	private static final int TEST_RUNS = 10000;

	public HashMapTest(String name) {
		super(name);
	}
	
	public void testPut() {
		System.out.println(">>> put()");
		HashMap map = new HashMap();
		long time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object previous = map.put( "" + i, new Integer( i ) );
			assertEquals( i + 1, map.size() );
			assertEquals( null, previous );
		}
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for putting " + TEST_RUNS + " values into de.enough.polish.util.HashMap.");
		
		java.util.HashMap j2semap = new java.util.HashMap(); 
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object previous = j2semap.put( "" + i, new Integer( i ) );
			assertEquals( i + 1, j2semap.size() );
			assertEquals( null, previous );
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for putting " + TEST_RUNS + " values into java.util.HashMap.");
		
		Hashtable table = new Hashtable(); 
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object previous = table.put( "" + i, new Integer( i ) );
			assertEquals( i + 1, table.size() );
			assertEquals( null, previous );
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for putting " + TEST_RUNS + " values into java.util.Hashtable.");

	}
	
	
	public void testRemove() {
		System.out.println(">>> remove()");
		Object[] keys = new Object[ TEST_RUNS / 10 ];
		
		HashMap map = new HashMap();
		int j=0;
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = "" + i;
			map.put( key, new Integer( i ) );
			if (j < TEST_RUNS/10) {
				keys[j] = key;
				j++;
			}
		}
		int size = TEST_RUNS;
		long time = System.currentTimeMillis();
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			Object previous = map.remove(key);
			assertNotNull( previous );
			assertEquals( --size, map.size() );
		}
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for removing " + TEST_RUNS/10 + " keys from de.enough.polish.util.HashMap.");
		
		java.util.HashMap j2semap = new java.util.HashMap(); 
		time = System.currentTimeMillis();
		j = 0;
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = "" + i;
			j2semap.put( key, new Integer( i ) );
			if (j < TEST_RUNS/10) {
				keys[j] = key;
				j++;
			}
		}
		size = TEST_RUNS;
		time = System.currentTimeMillis();
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			Object previous = j2semap.remove(key);
			assertNotNull( previous );
			assertEquals( --size, j2semap.size() );
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for removing " + TEST_RUNS/10 + " keys from java.util.HashMap.");
		
		Hashtable table = new Hashtable(); 
		time = System.currentTimeMillis();
		j = 0;
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = "" + i;
			table.put( key, new Integer( i ) );
			if (j < TEST_RUNS/10) {
				keys[j] = key;
				j++;
			}
		}
		size = TEST_RUNS;
		time = System.currentTimeMillis();
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			Object previous = table.remove(key);
			assertNotNull( previous );
			assertEquals( --size, table.size() );
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for removing " + TEST_RUNS/10 + " keys from java.util.Hashtable.");

	}
	
	public void testGet() {
		System.out.println(">>> get()");
		HashMap map = new HashMap();
		long time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = "" + i;
			Object value = new Integer( i );
			map.put( key, value );
			assertEquals( value, map.get( key ));
		}
		
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS + " keys/values in de.enough.polish.util.HashMap.");
		
		java.util.HashMap j2semap = new java.util.HashMap(); 
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = "" + i;
			Object value = new Integer( i );
			j2semap.put( key, value );
			assertEquals( value, j2semap.get( key ));
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS + " keys/values in java.util.HashMap.");
		
		Hashtable table = new Hashtable(); 
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = "" + i;
			Object value = new Integer( i );
			table.put( key, value );
			assertEquals( value, table.get( key ));
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS + " keys/values in java.util.Hashtable.");
	}
	
	public void testContains() {
		System.out.println(">>> contains()");
		HashMap map = new HashMap();
		long time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = "" + i;
			map.put( key, new Integer( i ) );
			assertTrue( map.containsKey( key ));
			assertTrue( map.containsValue( new Integer( i ) ));
		}
		
		long neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS + " keys/values in de.enough.polish.util.HashMap.");
		
		java.util.HashMap j2semap = new java.util.HashMap(); 
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = "" + i;
			j2semap.put( key, new Integer( i ) );
			assertTrue( j2semap.containsKey( key ));
			assertTrue( j2semap.containsValue( new Integer( i ) ));
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS + " keys/values in java.util.HashMap.");
		
		Hashtable table = new Hashtable(); 
		time = System.currentTimeMillis();
		for (int i = 0; i < TEST_RUNS; i++) {
			Object key = "" + i;
			table.put( key, new Integer( i ) );
			assertTrue( table.containsKey( key ));
			assertTrue( table.containsValue( new Integer( i ) ));
		}
		neededTime = System.currentTimeMillis() - time;
		System.out.println("needed " + neededTime + "ms for checking " + TEST_RUNS + " keys/values in java.util.Hashtable.");
	}


}
