package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.CharacterData;
import de.enough.skylight.dom.DOMException;

public abstract class CharacterDataImpl extends DomNodeImpl implements CharacterData{

	protected StringBuffer buffer;
	
	public void init(DocumentImpl document, DomNodeImpl parent, short type, String data) {
		super.init(document, parent, type);
		this.buffer = new StringBuffer(data);
	}
	
	public void appendData(String arg) throws DOMException {
		this.buffer.append(arg);
	}

	public void deleteData(int offset, int count) throws DOMException {
		try{
			this.buffer.delete(offset,offset+count);
		} catch(Exception e) {
			throw throwDOMException(DOMException.INDEX_SIZE_ERR,e);
		}
	}

	public String getData() throws DOMException {
		return this.buffer.toString();
	}

	public int getLength() {
		return this.buffer.length();
	}

	public void insertData(int offset, String arg) throws DOMException {
		try {
			this.buffer.insert(offset, arg);
		} catch(Exception e) {
			throw throwDOMException(DOMException.INDEX_SIZE_ERR,e);
		}
	}

	/**
	 * @unimplemented
	 */
	public void replaceData(int offset, int count, String arg)
			throws DOMException {
		// Unimplemented.
	}

	public void setData(String data) throws DOMException {
		this.buffer = new StringBuffer(data);
	}

	public String substringData(int offset, int count) throws DOMException {
		try {
			return this.buffer.toString().substring(offset, count);
		} catch(Exception e) {
			throw throwDOMException(DOMException.INDEX_SIZE_ERR,e);
		}
	}
	
	// TODO: This could cause problems with the call stack.
	private DOMException throwDOMException(short code, Throwable t){
		return new DOMException(code, t.getClass().getName()+":"+t.getMessage());
	}

}
