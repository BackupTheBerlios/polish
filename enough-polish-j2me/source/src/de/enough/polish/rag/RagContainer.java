package de.enough.polish.rag;

import java.io.InputStream;

import de.enough.polish.io.Serializable;
import de.enough.polish.util.ArrayList;

public class RagContainer implements Serializable{
	private String name;
	private long size;
	private byte[] data;
	
	public byte[] getData() {
		return this.data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getSize() {
		return this.size;
	}
	public void setSize(long size) {
		this.size = size;
	}
}	
