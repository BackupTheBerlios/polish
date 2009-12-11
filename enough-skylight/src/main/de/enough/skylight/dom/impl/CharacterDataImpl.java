package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.CharacterData;
import de.enough.skylight.dom.DomException;

public abstract class CharacterDataImpl extends DomNodeImpl implements CharacterData{

	protected StringBuffer buffer;
	
	public void init(DocumentImpl document, DomNodeImpl parent, short type, String data) {
		super.init(document, parent, type);
		this.buffer = new StringBuffer(data);
	}
	
	public void appendData(String arg) throws DomException {
		this.buffer.append(arg);
	}

	public void deleteData(int offset, int count) throws DomException {
		try{
			this.buffer.delete(offset,offset+count);
		} catch(Exception e) {
			throw throwDomException(e);
		}
	}

	public String getData() throws DomException {
		return this.buffer.toString();
	}

	public int getLength() {
		return this.buffer.length();
	}

	public void insertData(int offset, String arg) throws DomException {
		try {
			this.buffer.insert(offset, arg);
		} catch(Exception e) {
			throw throwDomException(e);
		}
	}

	public void replaceData(int offset, int count, String arg)
			throws DomException {
		// TODO Auto-generated method stub
	}

	public void setData(String data) throws DomException {
		this.buffer = new StringBuffer(data);
	}

	public String substringData(int offset, int count) throws DomException {
		try {
			return this.buffer.toString().substring(offset, count);
		} catch(Exception e) {
			throw throwDomException(e);
		}
	}
	
	// TODO: This could cause problems with the call stack.
	private DomException throwDomException(Throwable t) throws DomException{
		return new DomException(t.getClass().getName()+":"+t.getMessage());
	}

}
