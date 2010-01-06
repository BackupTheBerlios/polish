//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;

public class ModeKeyItem extends KeyItem {
	
	public ModeKeyItem(Keyboard keyboard, String position) {
		this(keyboard,position, null);
	}
	
	public ModeKeyItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, "M", style);
	}

	protected void apply(boolean doubleclick) {
		Keyboard keyboard = getKeyboard();
		
		int currentMode = keyboard.getMode();
		
		if(currentMode == keyboard.getPrimaryMode()) {
			keyboard.setMode(keyboard.getSecondaryMode());
		} else {
			keyboard.setMode(keyboard.getPrimaryMode());
		}
	}
}
