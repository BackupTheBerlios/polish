package de.enough.polish.util;

/**
 * A helper class to create conforming toString outputs
 * @author Andre
 *
 */
public class ToStringHelper {
	
	static String FORMAT_HEADER = "[";
	
	static String FORMAT_FOOTER = "]";
	
	static String  FORMAT_DESCRIPTION_SEPARATOR = ":";
	
	static String  FORMAT_DESCRIPTION_LIST_SEPARATOR = " , ";
	
	static String  FORMAT_DESCRIPTION_KEYVALUE_SEPARATOR = " : ";
	
	static String  FORMAT_LIST_SEPARATOR = " / ";
	
	/**
	 * The name of the parenting object
	 */
	String name;
	
	/**
	 * The single value descriptions
	 */
	ArrayList attributes;
	
	static ToStringHelper instance;
	
	public static ToStringHelper getInstance(String name) {
		if(instance == null) {
			instance = new ToStringHelper();
		}
		
		instance.setName(name);
		instance.clear();
		
		return instance;
	}
	
	/**
	 * Creates a new ToStringHelper instance
	 * @param name the name to use
	 */
	public ToStringHelper() {
		this.attributes = new ArrayList();
	}
	
	/**
	 * Clear the attribute map
	 */
	void clear() {
		this.attributes.clear();
	}

	/**
	 * sets the name
	 * @param name the name
	 */
	void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the header
	 * @param name the name
	 * @return the header
	 */
	String getHeader(String name) {
		return name + " " + FORMAT_HEADER;
	}
	
	/**
	 * Returns the footer
	 * @return the footer
	 */
	String getFooter() {
		return FORMAT_FOOTER;
	}
	
	/**
	 * sets a numerical value
	 * @param id the id 
	 * @param value the value
	 * @return the ToStringHelper instance
	 */
	public ToStringHelper set(String id, long value) {
		String description = id + FORMAT_DESCRIPTION_SEPARATOR + value;
		this.attributes.add(description);
		return this;
	}

	/**
	 * sets a boolean value
	 * @param id the id 
	 * @param value the value
	 * @return the ToStringHelper instance
	 */
	public ToStringHelper set(String id, boolean value) {
		String description = id + FORMAT_DESCRIPTION_SEPARATOR + value;
		this.attributes.add(description);
		return this;
	}
	
	/**
	 * sets an Object value
	 * @param id the id 
	 * @param value the value
	 * @return the ToStringHelper instance
	 */
	public ToStringHelper set(String id, Object value) {
		String description = id + FORMAT_DESCRIPTION_SEPARATOR + value;
		this.attributes.add(description);
		return this;
	}
	
	/**
	 * sets a String value
	 * @param id the id 
	 * @param value the value
	 * @return the ToStringHelper instance
	 */
	public ToStringHelper set(String id, String value) {
		String description = id + FORMAT_DESCRIPTION_SEPARATOR + "\"" + value + "\"";
		this.attributes.add(description);
		return this;
	}
	
	/**
	 * sets an ArrayList
	 * @param id the id 
	 * @param value the value
	 * @return the ToStringHelper instance
	 */
	public ToStringHelper set(String id, ArrayList value) {
		String description = id + FORMAT_DESCRIPTION_SEPARATOR;
		for (int i = 0; i < value.size(); i++) {
			Object object = value.get(i);
			description += object.toString();
			if(i != this.attributes.size() - 1) {
				description += FORMAT_DESCRIPTION_LIST_SEPARATOR;
			}
		}
		this.attributes.add(description);
		return this;
	}
	
	/**
	 * sets a HashMap
	 * @param id the id 
	 * @param value the value
	 * @return the ToStringHelper instance
	 */
	public ToStringHelper set(String id, HashMap value) {
		String description = id + FORMAT_DESCRIPTION_SEPARATOR;
		Object[] keys = value.keys();
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			Object object = value.get(key);
			description += key.toString() + FORMAT_DESCRIPTION_KEYVALUE_SEPARATOR + object.toString();
			if(i != this.attributes.size() - 1) {
				description += FORMAT_DESCRIPTION_LIST_SEPARATOR;
			}
		}
		this.attributes.add(description);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String result = "";
		result += getHeader(this.name);
		
		for (int i = 0; i < this.attributes.size(); i++) {
			String description = (String)this.attributes.get(i);
			result += description;
			if(i != this.attributes.size() - 1) {
				result += FORMAT_LIST_SEPARATOR;
			}
		}
		
		result += getFooter();
		
		return result;
	}
}
