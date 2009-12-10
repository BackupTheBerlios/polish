package de.enough.skylight.renderer.view.element;

import de.enough.polish.ui.Container;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ItemPreinit;

public class LineBox {

	int space;
	
	Container workLine;
	
	LineBox parent;
	
	LineBox rootLine;
	
	public static LineBox append(LineBox parent, int space, Style style) {
		LineBox linebox;
		
		if(parent != null) {
			space = parent.getRemainingSpace();
			linebox = new LineBox(space, parent, style);
		} else {
			linebox = new LineBox(space, parent, style);
		}
		
		if(parent != null) {
			parent.add(linebox.getWorkLine());
		}
		
		return linebox;
	}
	
 
	
	public static LineBox newline(LineBox base, int space) {
		return null;
	}

	LineBox(int space, LineBox parent, Style style) {
		this.space = space;
		this.parent = parent;
		this.workLine = new Container(false,style);
		this.workLine.setView(new ListBoxView());
		
		ItemPreinit.preinit(this.workLine, this.space, Integer.MAX_VALUE);

		if(parent != null) {
			LineBox linebox = this;
			while (linebox != null) {
				if (linebox.getParent() != null) {
					this.rootLine = linebox.getParent();
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
	
	protected LineBox getParent() {
		return this.parent;
	}
	
	protected Container getWorkLine() {
		return this.workLine;
	}
	
	public Container getRootLine() {
		return this.rootLine.getWorkLine();
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
