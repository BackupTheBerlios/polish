package de.enough.skylight.renderer.linebox;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;

public class LineboxList {
	ArrayList lineboxList;
	
	boolean sorted = false;
	
	public LineboxList() {
		this.lineboxList = new ArrayList();
	}
	
	public void add(Linebox linebox) {
		this.lineboxList.add(linebox);
		this.sorted = false;
	}
	
	public void addAll(LineboxList lineboxes) {
		for (int i = 0; i < lineboxes.size(); i++) {
			Linebox linebox = lineboxes.get(i);
			add(linebox);
		}
		this.sorted = false;
	}
	
	public Linebox get(int index) {
		sort();
		return (Linebox)this.lineboxList.get(index);
	}
	
	public int size() {
		return this.lineboxList.size();
	}
	
	public void clear() {
		this.lineboxList.clear();
		this.sorted = false;
	}
	
	public void sort() {
		if(this.sorted) {
			return;
		} else {
			if(this.lineboxList.size() > 1) {
				Arrays.quicksort(this.lineboxList.getInternalArray(), this.lineboxList.size(), LineboxComparator.getInstance());
				this.sorted = true;
			}
		}
	}
}
