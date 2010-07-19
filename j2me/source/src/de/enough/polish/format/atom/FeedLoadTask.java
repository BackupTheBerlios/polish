/*
 * Created on Jul 19, 2010 at 7:23:36 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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
package de.enough.polish.format.atom;

import de.enough.polish.util.Task;

/**
 * <p>Loads a defined feed</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see de.enough.polish.util.TaskThread
 */
public class FeedLoadTask 
implements Task
{
	
	private final AtomFeed atomFeed;
	private final AtomUpdateConsumer consumer;
	private final String url;

	/**
	 * Creates a new feed loading task
	 * @param url the URL of the feed
	 * @param feed the feed
	 * @param consumer the update consumer for the feed
	 */
	public FeedLoadTask( String url, AtomFeed feed, AtomUpdateConsumer consumer) {
		this.url = url;
		this.atomFeed = feed;
		this.consumer = consumer;
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.util.Task#execute()
	 */
	public void execute() throws Exception {
		this.atomFeed.update(this.consumer, this.url);
	}
	
}
