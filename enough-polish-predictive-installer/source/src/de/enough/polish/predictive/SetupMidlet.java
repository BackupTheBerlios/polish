package de.enough.polish.predictive;

import java.io.DataInputStream;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.predictive.trie.TrieSetup;
import de.enough.polish.predictive.trie.TrieSetupCallback;

public class SetupMidlet 
extends MIDlet implements TrieSetupCallback
{
	protected TrieSetup setup  = null;
	protected Thread thread = null;
	
	public SetupMidlet() {
	}

     protected void startApp() throws MIDletStateChangeException{
    	 this.setup = new TrieSetup(null);
    	 this.setup.registerListener(this);
    	 this.thread = new Thread(this.setup);
    	 this.thread.start();
     }
     	
     protected void pauseApp(){
          // ignore
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
    	  // ignore
     }

	public void setupFinished(boolean finishedGraceful) {
		notifyDestroyed();
	}	
}