/*
 * Created on Dec 18, 2008 at 1:13:33 AM.
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
package de.enough.polish.rmi.bluetooth;

import javax.bluetooth.BluetoothStateException;

import de.enough.polish.rmi.Remote;

/**
 * <p>Abstracts from different bluetooth protocols.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface BluetoothRemoteService
{

	/**
	 * Starts the service
	 * @throws BluetoothStateException when the service cannot be started 
	 */
	public void start() throws BluetoothStateException;
	
	/**
	 * Stops the service
	 */
	public void stop();
	
	/**
	 * Initializes this service
	 * @param serviceImplementation the service implementation
	 * @param uuid the UUID, for generating a UUID you can visit http://www.famkruithof.net/uuid/uuidgen
	 */
	public void init( Remote serviceImplementation, String uuid );

}
