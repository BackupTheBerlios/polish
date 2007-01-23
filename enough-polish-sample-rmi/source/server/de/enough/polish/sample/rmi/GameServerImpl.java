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

import javax.servlet.http.HttpSession;

import de.enough.polish.rmi.RemoteException;
import de.enough.polish.rmi.RemoteHttpServlet;

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
public class GameServerImpl
extends RemoteHttpServlet
implements GameServer 
{
	
	private Random random;

	/**
	 * Creates a new Game Server Implementation.
	 */
	public GameServerImpl() {
		this.random = new Random( System.currentTimeMillis() );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.sample.rmi.GameServer#registerUser(java.lang.String, java.lang.String)
	 */
	public GameUser registerUser(long time, String userName, String password)
	throws RemoteException, DuplicateUserException
	{
		if ( (time & 1) == 0) {
			System.out.println("throwing DuplicateUserException!");
			throw new DuplicateUserException("It's a dupe!", userName );
		}
		HttpSession session = getSession();
		Integer numberOfTries = (Integer) session.getAttribute("registration.number");
		if (numberOfTries == null) {
			session.setAttribute("registration.number", new Integer(1) );
		} else {
			if (numberOfTries.intValue() > 4) {
				System.out.println("Too many registration trials...");
				session.setAttribute("registration.number", new Integer(1) );
				throw new DuplicateUserException("too many registrations...", userName );
			} else {
				session.setAttribute("registration.number", new Integer( numberOfTries.intValue() + 1 ) );
			}
		
		}
		GameUser user = new GameUser( this.random.nextLong() % 9999, userName, this.random.nextInt( 100 ) ); 
		System.out.println("registering user " + user + ", time=" + time);
		return user;
	}

	public GameHighscore storeHighscore(GameHighscore highscore, boolean flag) throws RemoteException {
		System.out.println("Storing highscore, name=" + highscore.getName() + ", flag=" + flag  );
		int[] points = highscore.getPoints();
		for (int i = 0; i < points.length; i++) {
			System.out.println( i + "=" + points[i]);
		}
		return new GameHighscore( highscore.getName() + " (serialized)", highscore.getPoints()  );
		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.sample.rmi.GameServer#ping(long)
	 */
	public boolean ping(long time) throws RemoteException {
		System.out.println("ping at client-time of " + time );
		return true;
	}

}
