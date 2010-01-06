//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;

public class BlankItem extends KeyItem {

	public BlankItem(Keyboard keyboard, String position) {
		this(keyboard, position, null);
	}
	
	public BlankItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, " ", style);
	}

	protected void apply(boolean doubleclick) {
		// do nothing
	}
}
