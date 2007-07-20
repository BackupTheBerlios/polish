package de.enough.polish.predictive;

import java.io.DataInputStream;
import javax.microedition.rms.RecordStore;

public class InstallerThread 
implements Runnable
{
	private InstallerMidlet parent;
	
	DataInputStream stream = null;
	TrieInstaller installer = null;
	RecordStore store = null;
	
	boolean quit = false;
	
	InstallerThread(InstallerMidlet parent)
	{
		this.parent = parent;
	}
	
	public void pause()
	{
		this.installer.pause();
	}
	
	public void run()
	{
		try {
			installer = new TrieInstaller();
			
			this.parent.status.setText("Deleting previous installation ...");
			
			installer.deleteAllStores();
			
			this.parent.status.setText("Installing ...");
			
			installer.createStores(this.parent.gauge);
			
			this.parent.status.setText("Finished, the installer will exit in 5 seconds ...");
			this.parent.form.removeCommand( this.parent.cancelCommand );
			this.parent.form.addCommand( this.parent.exitCommand );
			
			Thread.sleep(5000);
			this.parent.notifyDestroyed();
			
		} catch (Exception e) {
			this.parent.status.setText("An error has occured:");
			this.parent.status.setText(e.getMessage());
			
			this.parent.form.removeCommand( this.parent.cancelCommand );
			this.parent.form.addCommand( this.parent.exitCommand );
			
			//#debug error
			e.printStackTrace();
		}
	}
}