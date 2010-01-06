//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;

public class ShiftKeyItem extends KeyItem {
	
	public ShiftKeyItem(Keyboard keyboard, String position) {
		this(keyboard, position, null);
	}
	
	public ShiftKeyItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, "S", style);
	}

	protected void apply(boolean doubleclick) {
		getKeyboard().shift(!getKeyboard().isShift());
	}
}
