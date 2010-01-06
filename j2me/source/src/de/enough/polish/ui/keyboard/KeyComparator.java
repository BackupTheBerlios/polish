//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard;

import de.enough.polish.util.Comparator;

public class KeyComparator implements Comparator{

	public int compare(Object o1, Object o2) {
		KeyItem first = (KeyItem)o1;
		
		if(o2 == null) 
		{
			return 0; 
		}
		
		KeyItem second = (KeyItem)o2;
		
		if(first.getRow() < second.getRow()) {
			return -1;
		} else if(first.getRow() > second.getRow()) {
			return 1;
		} else {
			if(first.getIndex() < second.getIndex()) {
				return -1;
			} else if(first.getIndex() > second.getIndex()) {
				return 1;
			} else {
				return 0;
			} 
		} 
	}
}
