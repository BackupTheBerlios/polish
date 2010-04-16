package de.enough.skylight.renderer.partition;

import de.enough.polish.ui.Item;
import de.enough.polish.util.ToStringHelper;

public class TextPartition extends Partition{

	int index;
	int length;
	
	public static TextPartition createText(int index, int length, int left, int right, int height, Item parent) {
		return new TextPartition(index, length, left, right, height, parent);
	}
	
	public static TextPartition createWhiteSpace(int index, int left, int right, int height, Item parent) {
		return new TextPartition(index, 1, left, right, height, parent);
	}
	
	TextPartition(int index, int length, int left, int right, int height, Item parent) {
		super(TYPE_TEXT, left, right, height, parent);
		
		this.index = index;
		this.length = length;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public int getLength() {
		return this.length;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.skylight.renderer.partition.Partition#toString()
	 */
	public String toString() {
		return new ToStringHelper("TextPartition").
		add("inline relative X", this.inlineRelativeLeft).
		add("relative X", this.lineboxRelativeX).
		add("relative Y", this.lineboxRelativeY).
		add("width", this.width).
		add("height", this.height).
		add("is newline", hasAttribute(ATTRIBUTE_NEWLINE)).
		add("is whitespace", hasAttribute(ATTRIBUTE_WHITESPACE)).
		add("parent item", this.parentItem).
		add("linebox", this.linebox).
		add("index", this.index).
		add("length", this.length).
		toString();
	}
}
