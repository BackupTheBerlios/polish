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
	private static final int VERSION = 100;
	private String name;
	private String id;
	private transient CalendarCategory parentCategory;
	private ArrayList childCategories;
	private String image;	
	
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

	public ArrayList getChildCategories() {
		return childCategories;
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
		ArrayList children = this.getChildCategories();
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
}
