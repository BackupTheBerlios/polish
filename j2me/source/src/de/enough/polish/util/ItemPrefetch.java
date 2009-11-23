//#condition polish.usePolishGui
package de.enough.polish.util;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;

public class ItemPrefetch {
	public static void prefetch(Item item) throws IllegalArgumentException{
		Screen screen = item.getScreen();
		if(screen == null) {
			throw new IllegalArgumentException("item has no screen");
		}
		
		Prefetch.prefetch(screen);
	}
}
