package de.enough.polish.sample.rmi;

import de.enough.polish.rmi.Remote;
import de.enough.polish.rmi.RemoteException;




public interface XMLRPCSampleServer extends Remote{

	/**
	 * Test method to sum two integers up
	 */
	public int XMLRPCSampleServer__sum(int i, int j) throws RemoteException;
	
	/**
	 * Test method to subduct two integers up
	 */
	public int XMLRPCSampleServer__sub(int i, int j) throws RemoteException;
}
