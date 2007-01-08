/*
 * Created on Dec 28, 2006 at 4:30:52 AM.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.sample.rmi;

import java.util.Random;

import de.enough.polish.rmi.RemoteException;

/**
 * <p>Implements the actual service</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Dec 28, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class GameServerImpl implements GameServer {
	
	private Random random;

	public GameServerImpl() {
		this.random = new Random( System.currentTimeMillis() );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.sample.rmi.GameServer#registerUser(java.lang.String, java.lang.String)
	 */
	public GameUser registerUser(String userName, String password)
	throws RemoteException 
	{
		GameUser user = new GameUser( this.random.nextLong() % 9999, userName, this.random.nextInt( 100 ) ); 
		System.out.println("registering user " + user );
		return user;
	}

}
