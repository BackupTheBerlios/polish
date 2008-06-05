package de.enough.polish.jabber.remote;

import de.enough.polish.im.ID;
import de.enough.polish.im.Message;

public interface JabberCallback {
	public void loginEvent(Exception exception);
	public void logoffEvent(Exception exception);
	
	public void messageEvent(Message message);
	public void idEvent(ID id);
}
