/*
 * Created on Feb 23, 2005
 */
package de.enough.polish.plugin.eclipse.css.parser;

/**
 * @author rickyn
 */
public class CssToken {

	private int offset;
	private int length;
	private String type; //e.g. NAME,COLON
	private String value;
	
	
	
	
	/**
	 * The value may be omitted because we have offset and length within the document. That would
	 * reduce redundancy.
	 * @param offset
	 * @param length
	 * @param type
	 * @param value
	 */
	public CssToken(int offset, int length, String type, String value) {
		super();
		this.offset = offset;
		this.length = length;
		this.type = (type !=null)? type: "";
		this.value = (value != null)? value: "";
	}
	
	/**
	 * In the first round we do not use positions.
	 * @param type
	 * @param value
	 */
	public CssToken(String type, String value){
		this(0,0,type,value);
	}
	
	/**
	 * @return Returns the length.
	 */
	public int getLength() {
		return this.length;
	}
	
	/**
	 * @param length The length to set.
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	/**
	 * @return Returns the offset.
	 */
	public int getOffset() {
		return this.offset;
	}
	/**
	 * @param offset The offset to set.
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return this.type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return this.value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString(){
		StringBuffer result = new StringBuffer();
		result.append(this.offset);
		result.append(":");
		result.append(this.length);
		result.append(":");
		result.append(this.type);
		result.append(":");
		result.append("X");
		result.append(this.value);
		result.append("X");
		return result.toString();
	}
}
