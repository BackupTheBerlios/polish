package de.enough.skylight.renderer.linebox;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;

public class InlineLineboxList {
	ArrayList lineboxList;
	
	boolean sorted = false;
	
	public InlineLineboxList() {
		this.lineboxList = new ArrayList();
	}
	
	public void add(InlineLinebox linebox) {
		this.lineboxList.add(linebox);
		this.sorted = false;
	}
	
	public void addAll(InlineLineboxList lineboxes) {
		for (int i = 0; i < lineboxes.size(); i++) {
			InlineLinebox linebox = lineboxes.get(i);
			add(linebox);
		}
		this.sorted = false;
	}
	
	public InlineLinebox get(int index) {
		sort();
		return (InlineLinebox)this.lineboxList.get(index);
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
				Arrays.quicksort(this.lineboxList.getInternalArray(), this.lineboxList.size(), InlineLineboxComparator.getInstance());
				this.sorted = true;
			}
		}
	}
}
