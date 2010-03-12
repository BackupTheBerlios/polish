package de.enough.skylight.renderer.linebox;

import de.enough.polish.util.Comparator;

public class LineboxComparator implements Comparator {
	
	static LineboxComparator instance;
	
	public static LineboxComparator getInstance() {
		if(instance == null) {
			instance = new LineboxComparator();
		}
		
		return instance;
	}

	public int compare(Object o1, Object o2) {
		Linebox first = (Linebox)o1;
		Linebox second = (Linebox)o2;
		
		if(first.getBlockRelativeTop() < second.getBlockRelativeTop()) {
			return -1;
		} else if (first.getBlockRelativeTop() > second.getBlockRelativeTop()) {
			return 1;
		} 
		
		return 0;
	}

}
