/*
 * Created on 30-Nov-2005 at 23:12:37.
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

/**
 * <p>Provides the functionality of the J2SE java.util.HashMap for J2ME applications and uses reference checks for comparing keys.</p>
 * <p>WARNING: Only use this implementation when you can ensure that you always
 *    use the original keys! (see below)
 * </p>
 * <p>In contrast to the java.util.Hashtable (which is available on J2ME platforms),
 *    this implementation is not synchronized and faster. This implementation
 *    also uses only reference checks (==) for testing keys and values on
 *    equality instead of calling equals() for comparing them. This is considerably
 *    faster, but you need to ensure that you only use references (=the original) keys when
 *    storing or retrieving values.
 * </p>
 * <p>This implementation uses chains for resolving collisions, that means
 *    when a key-value pair has the same hash code as a previous inserted item,
 *    the new item is linked to the previous item. Depending on your situation
 *    The OpenAddressingHashMap implementation might be better, especially when you
 *    do not have many collisions (items with the same hash code).
 * </p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        30-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class IntHashMap
{
	
	/** The default capacity is 16 */
	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	/** The default load factor is 75 (=75%), so the HashMap is increased when 75% of it's capacity is reached */ 
	public static final int DEFAULT_LOAD_FACTOR = 75;
	
	private final int loadFactor;	
	private Element[] buckets;
	private final boolean isPowerOfTwo;
	private int size;

	/**
	 * Creates a new HashMap with the default initial capacity 16 and a load factor of 75%. 
	 */
	public IntHashMap() {
		this( DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR );
	}
	
	/**
	 * Creates a new HashMap with the specified initial capacity.
	 * 
	 * @param initialCapacity the initial number of elements that this map can hold without needing to 
	 *        increase it's internal size.
	 */
	public IntHashMap(int initialCapacity ) {
		this( initialCapacity, DEFAULT_LOAD_FACTOR );
	}
	
	


	/**
	 * Creates a new HashMap with the specified initial capacity and the specified load factor.
	 * 
	 * @param initialCapacity the initial number of elements that this map can hold without needing to 
	 *        increase it's internal size.
	 * @param loadFactor the loadfactor in percent, a number between 0 and 100. When the loadfactor is 100,
	 *        the size of this map is only increased after all slots have been filled. 
	 */
	public IntHashMap(int initialCapacity, int loadFactor) {
		initialCapacity = (initialCapacity * 100) / loadFactor;
		// check if initial capacity is a power of 2:
		int capacity = 1;
		while (initialCapacity > capacity) {
			capacity <<= 1;
		}
		this.isPowerOfTwo = (capacity == initialCapacity);
		//System.out.println("isPowerOfTwo: " + this.isPowerOfTwo );
		this.buckets = new Element[ initialCapacity ];
		this.loadFactor = loadFactor;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put( int key, Object value ) {
		if (value == null ) {
			throw new IllegalArgumentException("HashMap cannot accept null key [" + key + "] or value [" + value + "].");
		}
		if ( (this.size * 100) / this.buckets.length > this.loadFactor ) {
			increaseSize();
		}
		
		int index;
		if (this.isPowerOfTwo) {
			index = (key & 0x7FFFFFFF) & (this.buckets.length - 1);
		} else {
			index = (key & 0x7FFFFFFF) % this.buckets.length;
		}
		Element element = this.buckets[ index ];
		if (element == null) {
			element = new Element( key, value );
			this.buckets[index] = element;
			this.size++;
			return null;
		}
		// okay, there is a collision:
		Element lastElement = element;
		do {
			if (element.key == key ) {
				Object oldValue = element.value;
				element.value = value;
				return oldValue;
			}
			lastElement = element;
			element = element.next;
		} while ( element != null );
		// now insert new element at the end of the bucket:
		element = new Element( key, value );
		lastElement.next = element;
		this.size++;
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#get(java.lang.Object)
	 */
	public Object get( int key ) {
		int index;
		if (this.isPowerOfTwo) {
			index = (key & 0x7FFFFFFF) & (this.buckets.length - 1);
		} else {
			index = (key & 0x7FFFFFFF) % this.buckets.length;
		}
		Element element = this.buckets[ index ];
		if (element == null) {
			return null;
		}
		do {
			if (element.key == key ) {
				return element.value;
			}
			element = element.next;
		} while (element != null);
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#remove(java.lang.Object)
	 */
	public Object remove( int key ) {
		int index;
		if (this.isPowerOfTwo) {
			index = (key & 0x7FFFFFFF) & (this.buckets.length - 1);
		} else {
			index = (key & 0x7FFFFFFF) % this.buckets.length;
		}
		Element element = this.buckets[ index ];
		if (element == null) {
			//System.out.println("remove: No bucket found for key " + key + ", containsKey()=" + containsKey(key));
			return null;
		}
		Element lastElement = null;
		do {
			if (element.key == key ) {
				if (lastElement == null) {
					this.buckets[ index ] = element.next;
				} else {
					lastElement.next = element.next;
				}
				this.size--;
				return element.value;
			}
			lastElement = element;
			element = element.next;
		} while (element != null);
		//System.out.println("No element found for key " + key + ", containsKey()=" + containsKey(key));
		return null;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return (this.size == 0);
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#size()
	 */
	public int size() {
		return this.size;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey( int key ) {
		return get( key ) != null;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue( Object value ) {
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			while (element != null) {
				if (element.value == value ) {
					return true;
				}
				element = element.next;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#clear()
	 */
	public void clear() {
		for (int i = 0; i < this.buckets.length; i++) {
			this.buckets[i] = null;
		}
		this.size = 0;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#values()
	 */
	public Object[] values() {
		return values( new Object[ this.size ] );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#values(java.lang.Object[])
	 */
	public Object[] values(Object[] objects) {
		int index = 0;
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			while (element != null) {
				objects[index] = element.value;
				index++;
				element = element.next;
			}
		}
		return objects;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Map#keys()
	 */
	public int[] keys() {
		int[] keys = new int[ this.size ];
		int index = 0;
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			while (element != null) {
				keys[index] = element.key;
				index++;
				element = element.next;
			}
		}
		return keys;
	}
	
	/**
	 * Returns String containing the String representations of all objects of this map.
	 * 
	 * @return the stored elements in a String representation.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer( this.size * 23 );
		buffer.append( super.toString() ).append( "{\n" );
		Object[] values = values();
		for (int i = 0; i < values.length; i++) {
			buffer.append( values[i] );
			buffer.append('\n');
		}
		buffer.append('}');
		return buffer.toString();
	}

	
	/**
	 * Increaases the internal capacity of this map.
	 */
	private void increaseSize() {
		int newCapacity;
		if (this.isPowerOfTwo) {
			newCapacity = this.buckets.length << 1; // * 2
		} else {
			newCapacity = (this.buckets.length << 1) - 1; // * 2 - 1 
		}
		Element[] newBuckets = new Element[ newCapacity ];
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			while (element != null) {				
				int index;
				if (this.isPowerOfTwo) {
					index = (element.key & 0x7FFFFFFF) & (newCapacity - 1);
				} else {
					index = (element.key & 0x7FFFFFFF) % newCapacity;
				}
				Element newElement = newBuckets[ index ];
				if (newElement == null ) {
					newBuckets[ index ] = element;
				} else {
					// add element at the end of the bucket:
					while (newElement.next != null) {
						newElement = newElement.next;
					}
					newElement.next = element;
					
				}
				Element lastElement = element;
				element = element.next;
				lastElement.next = null;
			}
		}
		this.buckets = newBuckets;
	}

	private static final class Element {
		public final int key;
		public Object value;
		public Element next;
		public Element ( int key, Object value ) {
			this.key = key;
			this.value = value;
		}
	}

}
