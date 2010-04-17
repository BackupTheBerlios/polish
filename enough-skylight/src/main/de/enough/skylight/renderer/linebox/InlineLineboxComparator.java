package de.enough.skylight.renderer.linebox;

import de.enough.polish.util.Comparator;

public class InlineLineboxComparator implements Comparator {
	
	static InlineLineboxComparator instance;
	
	public static InlineLineboxComparator getInstance() {
		if(instance == null) {
			instance = new InlineLineboxComparator();
		}
		
		return instance;
	}

	public int compare(Object o1, Object o2) {
		InlineLinebox first = (InlineLinebox)o1;
		InlineLinebox second = (InlineLinebox)o2;
		
		if(first.getBlockRelativeTop() < second.getBlockRelativeTop()) {
			return -1;
		} else if (first.getBlockRelativeTop() > second.getBlockRelativeTop()) {
			return 1;
		} 
		
		return 0;
	}

}
