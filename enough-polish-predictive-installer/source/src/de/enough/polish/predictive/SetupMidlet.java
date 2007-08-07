package de.enough.polish.predictive;

import java.io.DataInputStream;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class SetupMidlet 
extends MIDlet
{
	protected Setup setup  = null;
	protected Thread thread = null;
	
	public SetupMidlet() {
	}

     protected void startApp() throws MIDletStateChangeException{
    	 DataInputStream stream = new DataInputStream(getClass().getResourceAsStream("/predictive.trie"));
    	 
    	 setup  = new Setup(this, null, true, stream);
 		 thread = new Thread(setup);
    	 thread.start();
     }
     	
     protected void pauseApp(){
          // ignore
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
    	  // ignore
     }

	
}