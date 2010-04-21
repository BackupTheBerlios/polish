/**
 * 
 */
package de.enough.polish.calendar;

import de.enough.polish.io.Serializable;
import de.enough.polish.util.ArrayList;

/**
 * @author Ramakrishna
 * @author Timon
 */
public class CalendarCategory implements Serializable {
	private String name;
	private transient CalendarCategory parentCategory;
	private ArrayList childCategories;
	private String image;	
	
	public CalendarCategory(String name){
		this(name, null);
	}
	
	public CalendarCategory(String name, CalendarCategory parentCategory){
		this(name, parentCategory, null);
	}
	
	public CalendarCategory(String name, CalendarCategory parentCategory, ArrayList childCategories){
		this(name, parentCategory, childCategories, null);
	}
	
	public CalendarCategory(String name, CalendarCategory parentCategory, ArrayList childCategories, String image){
		this.name = name;
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
}
