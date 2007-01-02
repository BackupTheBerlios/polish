
/*
 * Created on Dec 20, 2006 at 11:09:51 AM.
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
package de.enough.polish.rmi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import de.enough.polish.io.Serializer;

/**
 * <p>The base implementation of client code</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        Dec 20, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RemoteClient implements Runnable {
	
	private static int RMI_VERSION = 100; // = 1.0.0
	
	private final Vector callQueue;
	private String url;
	
	/**
	 * Createsa new client.
	 * 
	 * @param url the url of the server, e.g. http://myserver.com/myservice
	 */
	protected RemoteClient( String url ) {
		this.url = url;
		this.callQueue = new Vector( 3 );
		Thread thread = new Thread( this );
		thread.start();
	}
	
	/**
	 * Retrieves a new remote client implementation for the specified remote interface.
	 * 
	 * @param remoteInterfaceName the name of the remote interface;
	 * @param url the URL of the server, e.g. http://myserver.com/myservice
	 * @return the stub which is capable of connecting to the server
	 */
	public static Remote open( String remoteInterfaceName, String url ) {
		return null;
	}
	
	/**
	 * Calls a remote method.
	 * 
	 * @param name the method name
	 * @param parameters any parameters, can be null
	 * @return a return value for methods; void methods return null
	 * @throws RemoteException when a checked or an unchecked exception has occurred on the server side or the connection failed
	 */
	protected Object callMethod( String name, Object[] parameters ) throws RemoteException {
		RemoteCall call = new RemoteCall( name, parameters );
		this.callQueue.addElement( call );
		synchronized( this.callQueue) {
			this.callQueue.notify();
		}
		synchronized (call) {
			try {
				call.wait();
			} catch (InterruptedException e) {
				// ignore
			}
		}
		
		RemoteException remoteException = call.getRaisedException();
		if (remoteException != null) {
			throw remoteException;
		}
		return call.getReturnValue();
	}

	public void run() {
		while (true) {
			// wait for a new remote call:
			synchronized( this.callQueue ) {
				try {
					this.callQueue.wait();
				} catch (InterruptedException e) {
					// ignore
				}
			}
			// check for call:
			while ( this.callQueue.size() != 0 ) {
				RemoteCall call = (RemoteCall) this.callQueue.elementAt(0);
				this.callQueue.removeElementAt(0);
				HttpConnection connection = null;
				try {
					connection = (HttpConnection) Connector.open( this.url, Connector.READ_WRITE );
					// write parameters:
					DataOutputStream out = connection.openDataOutputStream();
					out.writeInt( RMI_VERSION );
					out.writeUTF( call.getName() );
					Serializer.serialize( call.getParameters(), out);
					// send request and read return values:
					DataInputStream in = connection.openDataInputStream();
					int status = connection.getResponseCode();
					if (status != HttpConnection.HTTP_OK) {
						call.setRaisedException( new RemoteException("Server responded with response code " + status ) );
					} else {
						// okay, call succeeded at least partially:
						int remoteCallStatus = in.readInt();
						switch ( remoteCallStatus ) {
						case Remote.STATUS_OK:
							call.setReturnValue( Serializer.deserialize(in) );
							break;
						case Remote.STATUS_CHECKED_EXCEPTION:
							Throwable exception = (Throwable) Serializer.deserialize(in);
							call.setRaisedException( new RemoteException( exception ) );
							break;
						case Remote.STATUS_UNCHECKED_EXCEPTION:
							String message = in.readUTF();
							call.setRaisedException( new RemoteException( message ) );
						default:
							call.setRaisedException( new RemoteException( "unknown RMI status: " + remoteCallStatus ) );
						}
					}
				} catch (IOException e) {
					// create new RemoteException for this:
					call.setRaisedException( new RemoteException( e.toString() ) );					
				} finally {
					if (connection != null) {
						try {
							connection.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}
				synchronized( call ) {
					call.notify();
				}
			} // while there are queued calls
		} // while true
	} // run

}
