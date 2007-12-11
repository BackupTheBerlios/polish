//#condition polish.usePolishGui && polish.useRAG
package de.enough.polish.rag;

import java.io.InputStream;

import de.enough.polish.io.Serializable;
import de.enough.polish.util.ArrayList;

/**
 * <p>Stores a byte array with name and size to store or load from a RAG file</p>
 *
 * <p>Copyright (c) 2005, 2006, 2007 Enough Software</p>
 * <pre>
 * history
 *        10-Dec-2007 - asc creation
 * </pre>
 * @author Andre Schmidt, andre@enough.de
 */

public class RagContainer implements Serializable{
	private String name;
	private int size;
	private int offset;
	private byte[] data;
	
	/**
	 * @return the byte array
	 */
	public byte[] getData() {
		return this.data;
	}
	
	/**
	 * Set the byte array
	 * @param data the byte array
	 */	
	public void setData(byte[] data) {
		this.data = data;
	}
	
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
}	
