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
	
	public void addAll(LineBoxList lineboxes) {
		for (int i = 0; i < lineboxes.size(); i++) {
			LineBox linebox = lineboxes.get(i);
			add(linebox);
		}
	}
	
	public LineBox get(int index) {
		return (LineBox)this.lineboxList.get(index);
	}
	
	public int size() {
		return this.lineboxList.size();
	}
	
	public void clear() {
		this.lineboxList.clear();
	}
}
