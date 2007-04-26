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

import de.enough.polish.browser.chtml.HtmlBrowser;
import de.enough.polish.ui.Style;

public class RssBrowser
	extends HtmlBrowser
{
	public RssBrowser()
	{
		new RssTagHandler().register(this);
	}

	public RssBrowser(Style style)
	{
		super(style);
		new RssTagHandler().register(this);
	}
}
