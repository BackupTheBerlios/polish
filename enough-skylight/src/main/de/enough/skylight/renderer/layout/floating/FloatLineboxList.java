package de.enough.skylight.renderer.layout.floating;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Arrays;
import de.enough.skylight.renderer.element.BlockContainingBlock;

public class FloatLineboxList {
	ArrayList lineboxList;
	
	boolean sorted = false;
	
	public FloatLineboxList() {
		this.lineboxList = new ArrayList();
	}
	
	public void add(FloatLinebox linebox) {
		this.lineboxList.add(linebox);
		this.sorted = false;
	}
	
	public void addAll(FloatLineboxList lineboxes) {
		for (int i = 0; i < lineboxes.size(); i++) {
			FloatLinebox linebox = lineboxes.get(i);
			add(linebox);
		}
		this.sorted = false;
	}
	
	public FloatLinebox get(int index) {
		return (FloatLinebox)this.lineboxList.get(index);
	}
	
	public int size() {
		return this.lineboxList.size();
	}
	
	public void clear() {
		this.lineboxList.clear();
		this.sorted = false;
	}
	
	FloatLinebox getLineboxByBlock(BlockContainingBlock block) {
		for (int i = size(); i > 0; --i) {
			FloatLinebox linebox = get(i);
			if(linebox.getBlock().equals(block)) {
				return linebox;
			}
		}
		
		return null;
	}
	
	FloatLinebox getLineboxInLine(int relativeY) {
		for (int i = 0; i < size(); i++) {
			FloatLinebox linebox = get(i);
			if(linebox.isInLine(relativeY)) {
				return linebox;
			}
		}
		
		return null;
	}
}
