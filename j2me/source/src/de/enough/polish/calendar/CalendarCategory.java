/**
 * 
 */
package de.enough.polish.calendar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.enough.polish.io.Externalizable;
import de.enough.polish.util.ArrayList;

/**
 * Represents a category for a calendar entry (e.g. office, private, holidays, etc).
 * 
 * @author Ramakrishna
 * @author Timon
 */
public class CalendarCategory implements Externalizable {
	private static final int VERSION = 101;
	private String name;
	private String id;
	private transient CalendarCategory parentCategory;
	private ArrayList childCategories;
	private String image;
	private boolean isEnabled;	
	
	/**
	 * Creates a new empty category.
	 */
	public CalendarCategory() {
		this(null, null, null, null, null);
	}

	
	public CalendarCategory(String name){
		this(name, null, null, null, null);
	}
	
	public CalendarCategory(String name, String id){
		this(name, id, null, null, null);
	}
	
	public CalendarCategory(String name, String id, CalendarCategory parentCategory){
		this(name, id, parentCategory, null, null);
	}
	
	public CalendarCategory(String name, String id, CalendarCategory parentCategory, ArrayList childCategories){
		this(name, id, parentCategory, childCategories, null);
	}
	
	public CalendarCategory(String name, String id, CalendarCategory parentCategory, ArrayList childCategories, String image){
		this.name = name;
		this.id = id;
		this.parentCategory = parentCategory;
		this.childCategories = childCategories;
		this.image = image;
	}
	

	public void setName( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CalendarCategory getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(CalendarCategory parentCategory) {
		this.parentCategory = parentCategory;
	}
	
	/**
	 * Retrieves all child categories
	 * @return all sub-categories of this category, might be empty but not null
	 * @see #getEnabledChildCategories()
	 */
	public CalendarCategory[] getChildCategories() {
		if (this.childCategories == null) {
			return new CalendarCategory[0];
		}
		return (CalendarCategory[]) this.childCategories.toArray(new CalendarCategory[ this.childCategories.size()]);
	}
	
	/**
	 * Retrieves all enabled child categories
	 * @return all enabled sub-categories of this category, might be empty but not null
	 * @see #getChildCategories()
	 */
	public CalendarCategory[] getEnabledChildCategories() {
		if (this.childCategories == null) {
			return new CalendarCategory[0];
		}
		ArrayList list = new ArrayList( this.childCategories.size());
		Object[] children = this.childCategories.getInternalArray();
		for (int i = 0; i < children.length; i++) {
			CalendarCategory child = (CalendarCategory) children[i];
			if (child == null) {
				break;
			}
			list.add( child );
		}
		return (CalendarCategory[]) list.toArray( new CalendarCategory[ list.size() ] );
	}

	/**
	 * Adds a sub-category to this category
	 * @param child the sub-category
	 */
	public void addChildCategory( CalendarCategory child) {
		if (this.childCategories == null) {
			this.childCategories = new ArrayList();
		}
		child.setParentCategory(this);
		this.childCategories.add(child);
	}

	/**
	 * Retrieves the specified child category
	 * @param index the index  (starts with 0)
	 * @return the category
	 * @throws ArrayIndexOutOfBoundsException when index is not valid
	 */
	public CalendarCategory getChildCategory(int index) {
		if (this.childCategories == null) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return (CalendarCategory) this.childCategories.get(index);
	}
	
	/**
	 * Retrieves the number of child categories of this category
	 * @return the number of sub categories
	 */
	public int getSizeOfChildCategories() {
		if (this.childCategories == null) {
			return 0;
		}
		return this.childCategories.size();
	}


	/**
	 * Retrieves the child categories' internal array
	 * @return an array of objects that might contain null values, can be emptry but not null
	 */
	public Object[] getChildCategoriesAsInternalArray() {
		if (this.childCategories != null) {
			return this.childCategories.getInternalArray();
		} else {
			return new Object[0];
		}
	}
	
	public ArrayList getChildCategoriesAsList() {
		return this.childCategories;
	}

	public void setChildCategories(ArrayList childCategories) {
		this.childCategories = childCategories;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setParentForChildren(){
		ArrayList children = this.childCategories;
			if(children != null){
			for (int i = 0; i < children.size(); i++) {
				CalendarCategory child = (CalendarCategory) children.get(i);
				child.setParentCategory(this);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#read(java.io.DataInputStream)
	 */
	public void read(DataInputStream in) throws IOException {
		int version = in.readInt();
		if (version != VERSION) {
			throw new IOException("unknown version " + version);
		}
		boolean isNotNull = in.readBoolean();
		if (isNotNull) {
			this.name = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.id = in.readUTF();
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			this.image = in.readUTF();
		}
		if (version > 100) {
			this.isEnabled = in.readBoolean();
		} else {
			this.isEnabled = true;
		}
		isNotNull = in.readBoolean();
		if (isNotNull) {
			int size = in.readInt();
			this.childCategories = new ArrayList( size );
			for (int i = 0; i < size; i++) {
				CalendarCategory child = new CalendarCategory();
				child.read(in);
				child.setParentCategory(this);
				this.childCategories.add(child);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.enough.polish.io.Externalizable#write(java.io.DataOutputStream)
	 */
	public void write(DataOutputStream out) throws IOException {
		out.writeInt( VERSION );
		boolean isNotNull = (this.name != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.name);
		}
		isNotNull = (this.id != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.id);
		}
		isNotNull = (this.image != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeUTF(this.image);
		}
		out.writeBoolean( this.isEnabled );
		isNotNull = (this.childCategories != null);
		out.writeBoolean(isNotNull);
		if (isNotNull) {
			out.writeInt( this.childCategories.size() );
			Object[] objects = this.childCategories.getInternalArray();
			for (int i = 0; i < objects.length; i++) {
				CalendarCategory child = (CalendarCategory) objects[i];
				if (child == null) {
					break;
				}
				child.write(out);
			}
		}

	}

	/**
	 * Checks if this category is enabled.
	 * @return true when this category is enabled
	 */
	public boolean isEnabled() {
		return this.isEnabled;
	}

	/**
	 * Enables or disables this category and all it's parent categories.
	 * Parent categories are only disabled when all children/subcategories have been disabled.
	 * 
	 * @param enabled true when this category should be enabled
	 * @return true when the enabled state of this category was changed 
	 */
	public boolean setEnabled(boolean enabled) {
		if (this.isEnabled != enabled) { 
			this.isEnabled = enabled;
			CalendarCategory p = this.parentCategory;
			while (p != null) {
				if (enabled) {
					if (!p.isEnabled) {
						p.isEnabled = true;
					}
				} else {
					boolean childIsEnabled = false;
					Object[] children = p.getChildCategoriesAsInternalArray();
					for (int i = 0; i < children.length; i++) {
						CalendarCategory child = (CalendarCategory) children[i];
						if (child == null) {
							break;
						}
						if (child.isEnabled) {
							childIsEnabled = true;
							break;
						}
					}
					if (childIsEnabled) {
						break;
					}
					p.isEnabled = false;
				}
				p = p.parentCategory;
			}
			return true;
		}
		return false;
	}


	/**
	 * Generates a global unique ID for this category, which can be used for storing references to categories.
	 * 	 
	 * @return the generated GUID
	 */
	public long getGuid() {
		long guid = 0;
		if (this.id != null) {
			guid = this.id.hashCode();
		} else if (this.name != null) {
			guid = this.name.hashCode();
		}
		if (this.parentCategory != null) {
			guid ^= this.parentCategory.getGuid();
		}
		return guid;
	}


}
