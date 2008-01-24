//#condition polish.usePolishGui && polish.useThemes
package de.enough.polish.theme;

import de.enough.polish.io.Serializable;

/**
 * <p>Stores a byte array with name and size to store or load from a theme file</p>
 *
 * <p>Copyright (c) 2005, 2006, 2007 Enough Software</p>
 * <pre>
 * history
 *        10-Dec-2007 - asc creation
 * </pre>
 * @author Andre Schmidt, andre@enough.de
 */

public class ThemeContainer implements Serializable{
	private String name;
	private int size;
	private int offset;
	private byte[] data;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Sets the name
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the size
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * Sets the size. Must be the length of the byte array.
	 * @param size the size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Sets the offset.
	 * @param offset the offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Sets the data.
	 * @param data the data
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
}	
