/*
 * Created on 06-Mar-2005 at 20:42:51.
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
package de.enough.polish.swing;

import java.awt.AWTEvent;

/**
 * <p>An event that is fired when a change has occurred.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        06-Mar-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ChangeEvent extends AWTEvent {

	public static final int CHANGE_EVENT_ID = RESERVED_ID_MAX + 2342;
	private Object value;

	/**
	 * Creates a new event for the specified source
	 * 
	 * @param source the source of the event, most likely a JComponent like JTextField etc
	 * @param value the new value
	 */
	public ChangeEvent(Object source, Object value ) {
		super(source, CHANGE_EVENT_ID);
		this.value = value;
	}
	
	/**
	 * Retrieves the new value
	 * 
	 * @return the new value
	 */
	public Object getValue() {
		return this.value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}

}
