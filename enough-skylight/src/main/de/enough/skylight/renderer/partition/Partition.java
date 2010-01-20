package de.enough.skylight.renderer.partition;

import de.enough.polish.ui.DebugHelper;
import de.enough.polish.ui.Item;


public class Partition {
	
	int left;
	
	int right;
	
	int height = 0;
	
	boolean newline = false;
	
	boolean whitespace = false;
	
	Item parent;
	
	public Partition(int left, int right, int height, Item parent) {
		this.left = left;
		this.right = right;
		this.height = height;
		this.parent = parent;
	}
	
	public void setPartition(Partition partition) {
		this.left = partition.getLeft();
		this.right = partition.getRight();
		this.height = partition.getHeight();
		this.parent = partition.getParent();
			
		if(partition.isNewline()) {
			this.newline = true;
		}
	}
	
	public int getLeft() {
		return this.left;
	}

	public void setLeft(int left) {
		this.left = left;
	}
	
	public int getRight() {
		return this.right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getWidth() {
		return this.right - this.left;
	}
	
	public void setNewline(boolean newline) {
		this.newline = newline;
	}
	
	public boolean isNewline() {
		return this.newline;
	}
	
	public void setWhitespace(boolean whitespace) {
		this.whitespace = whitespace;
	}
	
	public boolean isWhitespace() {
		return this.whitespace;
	}
	
	public Item getParent() {
		return this.parent;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		Partition partition = (Partition)object;
		return getLeft() == partition.getLeft(); 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Partition [" + this.left + "," + this.right + "," + this.height + "," + this.newline + "," + this.whitespace + "," + this.parent + "]";
	}
	
}
