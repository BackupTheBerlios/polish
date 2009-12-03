package de.enough.polish.test;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;

public class LineBox {
	
	Container line;
	
	int space;
	
	LineBox parent;

	public LineBox(int space) {
		this(space, null);
	}
	
	public LineBox(int space, Style style) {
		this.space = space;
		this.parent = parent;
		this.line = new Container(false,style);
		UiAccess.init(this.line, space, space, Integer.MAX_VALUE);
	}
	
	void initContainer() {
		UiAccess.init(this.line, space, space, Integer.MAX_VALUE);
	}
	
	public boolean hasSpace(Item item) {
		if(!item.isInitialized()) {
			UiAccess.init(item, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		}
		
		int itemWidth = item.itemWidth;
		
		int remaining  = getRemainingSpace() - itemWidth;
		
		return remaining > 0;
	}
	
	public int getRemainingSpace() {
		return this.space - this.line.itemWidth;
	}
	
	public void add(Item item) {
		this.line.add(item);
		
		UiAccess.init(this.line, space, space, Integer.MAX_VALUE);
	}
	
	public Container getLine() {
		return this.line;
	}
}
