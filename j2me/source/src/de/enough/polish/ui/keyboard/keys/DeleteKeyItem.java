//#condition polish.TextField.useVirtualKeyboard
package de.enough.polish.ui.keyboard.keys;

import de.enough.polish.ui.Style;
import de.enough.polish.ui.keyboard.KeyItem;
import de.enough.polish.ui.keyboard.Keyboard;
import de.enough.polish.ui.keyboard.view.KeyboardView;

public class DeleteKeyItem extends KeyItem {
	
	public DeleteKeyItem(Keyboard keyboard, String position) {
		this(keyboard, position, null);
	}
	
	public DeleteKeyItem(Keyboard keyboard, String position, Style style) {
		super(keyboard, position, "DEL", style);
	}

	protected void apply(boolean doubleclick) {
		Keyboard keyboard = getKeyboard();
		KeyboardView view = keyboard.getKeyboardView();
		String text = view.getText();
		
		if(text.length() > 0)
		{
			view.setText(text.substring(0, text.length() - 1));
		}
	}
}
