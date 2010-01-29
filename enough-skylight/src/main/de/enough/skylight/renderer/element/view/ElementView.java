package de.enough.skylight.renderer.element.view;

import javax.microedition.lcdui.Graphics;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemView;
import de.enough.polish.ui.UiAccess;

public class ElementView extends ItemView {
	
	protected void initContent(Item parent, int firstLineWidth, int availWidth,
			int availHeight) {
		UiAccess.initContent(parent, firstLineWidth, availWidth, availHeight);
	}

	protected void paintContent(Item parent, int x, int y, int leftBorder,
			int rightBorder, Graphics g) {
		UiAccess.paintContent(parent, x, y, leftBorder, rightBorder, g);
	}

	public boolean handleKeyPressed(int keyCode, int gameAction) {
		return super.handleKeyPressed(keyCode, gameAction);
	}

	public boolean handlePointerPressed(int x, int y) {
		return super.handlePointerPressed(x, y);
	}
}
