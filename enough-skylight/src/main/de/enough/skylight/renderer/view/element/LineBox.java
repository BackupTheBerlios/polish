package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ItemPreinit;

public class LineBox {

	int space;
	
	Container workLine;
	
	LineBox parent;
	
	LineBox root;
	
	public static LineBox createForBlock(int space, Style style) {
		return new LineBox(space, null, style);
	}
	
	public static LineBox createForInline(LineBox parent, int space, Style style) {
		LineBox linebox = null;
		
		if(parent == null) {
			
		}
		
		if(parent != null) {
			linebox = new LineBox(parent.getRemainingSpace(), parent, style);
			parent.add(linebox.getWorkLine());
			return linebox;
		} else {
			throw new IllegalArgumentException("parent is null");
		}
	}
	
	public static LineBox newline(LineBox base, int space) {
		return null;
	}

	public LineBox(int space, LineBox parent, Style style) {
		this.space = space;
		this.parent = parent;
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
