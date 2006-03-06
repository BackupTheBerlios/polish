//#condition polish.usePolishGui
/*
 * Created on 29-Jan-2006 at 18:32:09.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui;

import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Allows to use an image along with text within one icon.
 * <p><b>Warning:</b> the item will be transformed into a J2ME Polish internal item in the preprocessing
 *      phase. This does not limit any of the features, but the construct <code>if (item instanceof CustomItem)</code> 
 *      will always return false if it is an IconCustomItem. Use <code>if (item instanceof IconCustomItem)</code> instead.
 *      <br />
 *      This is why this class is final, no subclassing is suported.
 * </p>
 * @author robertvirkus
 *
 */
public final class IconCustomItem 
//#if polish.usePolishGui
	//# extends IconItem 
//#else
	extends CustomItem 
//#endif
{

	public IconCustomItem(String label, String text, Image image ) {
		this(label, text, image, null);
	}

	public IconCustomItem(String label, String text, Image image, Style style ) {
		//#if polish.usePolishGui
			//# super( label, text, image, style  );
		//#else
			super( label );
		//#endif
	}

	protected int getMinContentWidth() {
		return 0;
	}

	protected int getMinContentHeight() {
		return 0;
	}

	protected int getPrefContentWidth(int maxHeight) {
		return 0;
	}

	protected int getPrefContentHeight(int maxWidth) {
		return 1;
	}

	protected void paint(Graphics g, int width, int height) {
		// do nothing
	}

}
