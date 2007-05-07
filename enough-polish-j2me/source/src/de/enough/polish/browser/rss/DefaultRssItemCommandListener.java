//#condition polish.usePolishGui
/*
 * Created on May 7, 2007 at 12:09:44 PM.
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
package de.enough.polish.browser.rss;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;

import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.UiAccess;

/**
 * <p>Displays an alert showing the description of the currently selected RSS item.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        May 7, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class DefaultRssItemCommandListener implements ItemCommandListener {

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.ItemCommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Item)
	 */
	public void commandAction(Command command, Item item) {
		if (command == RssTagHandler.CMD_RSS_ITEM_SELECT) {
			RssItem rssItem = (RssItem) UiAccess.getAttribute(item, RssTagHandler.ATTR_RSS_ITEM);

			if (rssItem != null && StyleSheet.display != null) {
				//#style rssDescriptionAlert
				Alert alert = new Alert( rssItem.getTitle(), rssItem.getDescription(), null, AlertType.INFO);
				alert.setTimeout(Alert.FOREVER);
				StyleSheet.display.setCurrent(alert);
			}
		}

	}

}
