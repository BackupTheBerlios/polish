//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;
import de.enough.polish.ui.keyboard.view.KeyboardView;

public class ClearKeyItem extends KeyItem {
	
	public ClearKeyItem(Keyboard keyboard, String position) {
		this(keyboard, position, null);
	}
	
	public ClearKeyItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, "CLR", style);
	}

	protected void apply(boolean doubleclick) {
		Keyboard keyboard = getKeyboard();
		KeyboardView view = keyboard.getKeyboardView();
		String text = view.getText();
		
		if(text.length() > 0)
		{
			view.setText("");
		}
	}
}
