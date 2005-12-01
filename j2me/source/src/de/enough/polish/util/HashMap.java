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
 * <p>Provides the functionality of the J2SE java.util.HashMap for J2ME applications.</p>
 * <p>In contrast to the java.util.Hashtable (which is available on J2ME platforms),
 *    this implementation is not synchronized and considerably faster.
 * </p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        30-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HashMap {
	
	/** The default capacity is 11 */
	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	/** The default load factor is 75 (=75%), so the HashMap is increased when 75% of it's capacity is reached */ 
	public static final int DEFAULT_LOAD_FACTOR = 75;
	
	private final int loadFactor;	
	private Element[] buckets;
	private int size;

	/**
	 * Creates a new HashMap with the default initial capacity 16 and a load factor of 75%. 
	 */
	public HashMap() {
		this( DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR );
	}
	
	/**
	 * Creates a new HashMap with the specified initial capacity.
	 * 
	 * @param initialCapacity the initial size of the map, remember that the default load factor 
	 *        is 75%, you if you know the maximum size ahead of time, you need to calculate 
	 *        <code>initialCapacity=maxSize * 4 / 3</code>, when you use this constructor.
	 */
	public HashMap(int initialCapacity ) {
		this( initialCapacity, DEFAULT_LOAD_FACTOR );
	}
	
	


	/**
	 * Creates a new HashMap with the specified initial capacity and the specified load factor.
	 * 
	 * @param initialCapacity the initial size of the map.
	 * @param loadFactor the loadfactor in percent, a number between 0 and 100. When the loadfactor is 100,
	 *        the size of this map is only increased after all slots have been filled. 
	 */
	public HashMap(int initialCapacity, int loadFactor) {
		this.buckets = new Element[ initialCapacity ];
		this.loadFactor = loadFactor;
	}
	
	public Object put( Object key, Object value ) {
		if (key == null || value == null ) {
			throw new IllegalArgumentException("HashMap cannot accept null key [" + key + "] or value [" + value + "].");
		}
		if ( (this.size * 100) / this.buckets.length > this.loadFactor ) {
			increaseSize();
		}
		
		this.size++;
		int hashCode = key.hashCode();
		int index = hashCode & (this.buckets.length - 1);
		Element element = this.buckets[ index ];
		if (element == null) {
			this.buckets[index] = new Element( hashCode, key, value );
			return null;
		}
		// okay, there is a collision:
		Element lastElement = element;
		do {
			if (element.key.equals( key )) {
				Object oldValue = element.value;
				element.value = value;
				return oldValue;
			}
			lastElement = element;
			element = element.next;
		} while ( element != null );
		// now insert new element at the end of the bucket:
		element = new Element( hashCode, key, value );
		lastElement.next = element;
		return null;
	}
	
	public Object get( Object key ) {
		int index = key.hashCode() & (this.buckets.length - 1);
		Element element = this.buckets[ index ];
		if (element == null) {
			return null;
		}
		do {
			if (element.key.equals( key )) {
				return element.value;
			}
			element = element.next;
		} while (element != null);
		return null;
	}
	
	public Object remove( Object key ) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		int index = key.hashCode() & (this.buckets.length - 1);
		Element element = this.buckets[ index ];
		if (element == null) {
			return null;
		}
		Element lastElement = null;
		do {
			if (element.key.equals( key )) {
				if (lastElement == null) {
					this.buckets[ index ] = null;
				} else {
					lastElement.next = element.next;
				}
				this.size--;
				return element.value;
			}
			lastElement = element;
			element = element.next;
		} while (element != null);
		return null;
	}
	
	public boolean isEmpty() {
		return (this.size == 0);
	}
	
	public int size() {
		return this.size;
	}
	
	public boolean containsKey( Object key ) {
		return get( key ) != null;
	}

	public boolean containsValue( Object value ) {
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			while (element != null) {
				if (element.value.equals( value )) {
					return true;
				}
				element = element.next;
			}
		}
		return false;
	}
	
	public void clear() {
		for (int i = 0; i < this.buckets.length; i++) {
			this.buckets[i] = null;
		}
		this.size = 0;
	}
	
	public Object[] values() {
		return values( new Object[ this.size ] );
	}

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

	private void increaseSize() {
		int newCapacity = this.buckets.length << 1;
		Element[] newBuckets = new Element[ newCapacity ];
		for (int i = 0; i < this.buckets.length; i++) {
			Element element = this.buckets[i];
			while (element != null) {
				int index = element.hashCode & (newCapacity -1);
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

	private final class Element {
		public final Object key;
		public final int hashCode;
		public Object value;
		public Element next;
		public Element ( int hashCode, Object key, Object value ) {
			this.hashCode = hashCode;
			this.key = key;
			this.value = value;
		}
	}

}
