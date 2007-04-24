/*
 * Created on Apr 20, 2007 at 9:04:04 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.runtime;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;

/**
 * <p>A listener that is notified when a the user clicks into the simulation panel.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 20, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface SelectionListener {
	/**
	 * The user selected an item.
	 * 
	 * @param item the selected item
	 * @param style the item's style
	 * @param x horizontal clicking position
	 * @param y vertical clicking position
	 */
	public void notifyItemSelected( Item item, Style style, int x, int y );

	/**
	 * The user selected a screen.
	 * 
	 * @param screen the selected screen
	 * @param style the item's style
	 * @param x horizontal clicking position
	 * @param y vertical clicking position
	 */
	public void notifyScreenSelected( Screen screen, Style style, int x, int y );
	
	
	/**
	 * Clears the selection.
	 */
	public void clearSelection();

}
