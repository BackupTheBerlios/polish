package de.enough.polish.jabber.remote;

import de.enough.polish.im.Account;
import de.enough.polish.im.ID;
import de.enough.polish.im.JabberConnector;
import de.enough.polish.im.Message;
import de.enough.polish.rmi.RemoteClient;
import de.enough.polish.rmi.RemoteException;

public class JabberWorker extends Thread implements Runnable{
	private final static int SLEEP = 500;
	
	boolean quit = false;
	
	JabberConnector connector;
	
	JabberCallback callback;
	
	Account account;
	
	public JabberWorker(Account account, JabberCallback callback)
	{
    	this.callback = callback;
		this.account = account;
	}
	
	public void login()
	{
		System.out.println("Connecting to server ...");
		
		this.connector = (JabberConnector) RemoteClient.open("de.enough.polish.im.JabberConnector","http://localhost:8080/im/server" );
		
		try {
			System.out.println("Logging in ...");
			this.connector.login(this.account);
			this.callback.loginEvent(null);
		} catch (RemoteException e) {
			//#debug
			System.out.println("unable to login " + e);
			this.callback.loginEvent(e);
		}
		
		System.out.println("Starting worker...");
		
		start();
	}
	
	public void run() {
		while(!this.quit)
		{
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {}
			
			Message[] messages = null;
			try {
				messages = this.connector.getMessageEvents(this.account);
			} catch (RemoteException e) {
				//#debug
				System.out.println("unable to receive messages " + e);
			}
			
			if(messages != null && messages.length > 0)
			{
				for (int i = 0; i < messages.length; i++) {
					this.callback.messageEvent(messages[i]);
				}
			}
			
			ID[] ids = null;
			try {
				ids = this.connector.getIdEvents(this.account);
			} catch (RemoteException e) {
				//#debug
				System.out.println("unable to receive ids " + e);
			}
			
			if(ids != null && ids.length > 0)
			{
				for (int i = 0; i < ids.length; i++) {
					this.callback.idEvent(ids[i]);
				}
			}
		}
	}
	
	public void logoff()
	{
		try {
			this.connector.logoff(this.account);
			this.callback.logoffEvent(null);
		} catch (RemoteException e) {
			//#debug
			System.out.println("unable to logoff " + e);
			this.callback.logoffEvent(e);
		}
	}
	
	public void send(Message message)
	{
		try {
			this.connector.send(this.account, message);
		} catch (RemoteException e) {
			//#debug
			System.out.println("unable to send message " + e);
		}
	}

}
