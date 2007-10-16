//#condition polish.usePolishGui

/*
 * Created on 24-Apr-2007 at 19:20:28.
 * 
 * Copyright (c) 2007 Michael Koch / Enough Software
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

import de.enough.polish.browser.html.HtmlBrowser;
import de.enough.polish.browser.html.HtmlTagHandler;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Style;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

public class RssBrowser
	extends HtmlBrowser
	implements CommandListener
{
	private ItemCommandListener rssItemCommandListener;

	public RssBrowser() {
		this((Style) null);
	}

	public RssBrowser(Style style) {
		this(new DefaultRssItemCommandListener(), style); 
	}

	//#if polish.midp2
	public RssBrowser(javax.microedition.lcdui.ItemCommandListener listener) {
		this( listener, null ); 
	}
	//#endif

	//#if polish.midp2
	public RssBrowser(javax.microedition.lcdui.ItemCommandListener listener, Style style ) {
		super( style );
		new RssTagHandler(HtmlTagHandler.CMD_LINK, (ItemCommandListener) null).register(this);
	}
	//#endif
	
	public RssBrowser(ItemCommandListener listener)
	{
		this( listener, null );
	}

	public RssBrowser(ItemCommandListener listener, Style style)
	{
		super(style);
		this.rssItemCommandListener = listener;
		new RssTagHandler(HtmlTagHandler.CMD_LINK, listener).register(this);
		if (listener instanceof DefaultRssItemCommandListener) {
			DefaultRssItemCommandListener rssListener = (DefaultRssItemCommandListener) listener;
			rssListener.setRssBrowser(this);
			rssListener.setCommandListener(this);
		}
	}

	public boolean handleCommand(Command command)
	{
		if (this.rssItemCommandListener != null
			&& command == RssTagHandler.CMD_GO_TO_ARTICLE) 
		{
			this.rssItemCommandListener.commandAction(command, getFocusedItem());
			return true;
		}

		return super.handleCommand(command);
	}

	public void commandAction(Command command, Displayable displayable)
	{
		handleCommand(command);
	}
}
