package de.enough.polish.sample.rmi;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;



public class XMLRPCSampleMidlet extends MIDlet {

	/*
	 * (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		Display.getDisplay(this).setCurrent(new CallServerForm(this));

	}
	
	public void quitApplication(){
		try {
			destroyApp(true);
		} catch (MIDletStateChangeException e) {
			//#debug warn
			System.out.println("Exeption when destroying application: " + e);
		}
		notifyDestroyed();
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

}
