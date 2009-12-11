package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ItemPreinit;

public class LineBox {

	int space;
	
	Container workLine;
	
	Style style;
	
	LineBox parent;
	
	LineBox root;
	
	public static LineBox createForBlock(int space) {
		//#style linebox
		return new LineBox(space, null);
	}
	
	public static LineBox createForInline(LineBox parent, int space) {
		LineBox linebox = null;
		
		if(parent != null) {
			//#style linebox
			linebox = new LineBox(parent.getRemainingSpace(), parent);
			parent.add(linebox.getWorkLine());
			return linebox;
		} else {
			throw new IllegalArgumentException("parent is null");
		}
	}
	
	public LineBox(int space, LineBox parent) {
		this(space,parent,null);
	}
	
	LineBox copy() {
		return new LineBox(this.space, this.parent, this.style);
	}

	public LineBox(int space, LineBox parent, Style style) {
		this.space = space;
		this.parent = parent;
		this.style = style;
		this.workLine = new Container(false,style);
		this.workLine.setView(new ListBoxView());
		
		ItemPreinit.preinit(this.workLine, this.space, Integer.MAX_VALUE);

		if(parent != null) {
			LineBox linebox = this;
			while (linebox != null) {
				if (linebox.getParent() != null) {
					this.root = linebox.getParent();
				}
				linebox = linebox.getParent();
			}
		}
	}
	
	public boolean hasSpace(Item item) {
		if(!item.isInitialized()) {
			ItemPreinit.preinit(item, Integer.MAX_VALUE, Integer.MAX_VALUE);
		}
		
		int itemWidth = item.itemWidth;
		
		int remaining  = getRemainingSpace() - itemWidth;
		
		return remaining > 0;
	}
	
	public int getRemainingSpace() {
		return this.space - this.workLine.itemWidth;
	}
	
	public void add(Item item) {
		this.workLine.add(item);
		
		ItemPreinit.preinit(this.workLine, this.space, Integer.MAX_VALUE);
	}
	
	public LineBox getParent() {
		return this.parent;
	}
	
	public Container getWorkLine() {
		return this.workLine;
	}
	
	public LineBox getRootLine() {
		return this.root;
	}
	
	public String toString() {
		String string = "LineBox [";
		
		for (int i = 0; i < this.workLine.size(); i++) {
			string += this.workLine.get(i);
			if(i != this.workLine.size() -1 ) {
				string += ",";
			}
		}
		
		string += "]";
		
		return string;
	}
}
