/*
 * Created on Aug 4, 2008 at 10:00:59 PM.
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
package de.enough.polish.app.bluetoothscreener;

import java.io.IOException;

import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.microedition.io.Connector;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class L2CapServer extends Thread
{

	private boolean stopRequested;
	private final String url;
	private BluetoothRemoteDesktopScreen screener;

	/**
	 * @param screener 
	 * @param url the connection URL
	 * 
	 */
	public L2CapServer( BluetoothRemoteDesktopScreen screener, String url )
	{
		this.screener = screener;
		this.url = url;
	}
	
	/** Waits for incoming connections */
	public void run() {
		while (!this.stopRequested) {
            try
			{
				L2CAPConnectionNotifier notifier = (L2CAPConnectionNotifier)Connector.open(this.url);
				L2CAPConnection connection = notifier.acceptAndOpen();
				this.screener.process( connection );
			} catch (IOException e)
			{
				this.screener.handleError( this, e );
			}

		}
	}

	public void requestStop() {
		this.stopRequested = true;
	}
}
