package de.enough.skylight.renderer.linebox;

import de.enough.polish.util.ArrayList;

public class LineBoxList {
	ArrayList lineboxList;
	
	public LineBoxList() {
		this.lineboxList = new ArrayList();
	}
	
	public void add(LineBox linebox) {
		this.lineboxList.add(linebox);
	}
	
	public LineBox get(int index) {
		return (LineBox)this.lineboxList.get(index);
	}
	
	public int size() {
		return this.lineboxList.size();
	}
}
