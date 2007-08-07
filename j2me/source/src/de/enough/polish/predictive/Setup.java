//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput && (polish.Bugs.sharedRmsRequiresSigning || polish.predictive.useLocalRMS || polish.predictive.Setup)
package de.enough.polish.predictive;

import java.io.DataInputStream;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;

import de.enough.polish.util.Locale;

public class Setup 
implements Runnable, CommandListener
{
	private Displayable returnTo;
	private MIDlet parentMidlet;
	
	DataInputStream stream = null;
	TrieInstaller installer = null;
	
	protected Command exitCommand = new Command( Locale.get("polish.predictive.setup.cmd.exit"), Command.EXIT, 0 );
	protected Command cancelCommand = new Command( Locale.get("polish.predictive.setup.cmd.cancel"), Command.CANCEL, 0 );
	
	protected Command yesCommand = new Command( Locale.get("polish.predictive.setup.cmd.yes"), Command.OK, 0 );
	protected Command noCommand = new Command( Locale.get("polish.predictive.setup.cmd.no"), Command.CANCEL, 0 );
	
	List list = null;
	
	Form form = null;
	
	StringItem info = null;
	StringItem status = null;
	StringItem error = null;
	Gauge gauge = null;
		
	boolean pause = false;
	
	public Setup(MIDlet parentMidlet, Displayable returnTo, boolean showGauge, DataInputStream stream)
	{
		this.returnTo = returnTo;
		this.parentMidlet = parentMidlet;
		
		this.stream = stream;
		
		//#style setupForm
		this.form = new Form( Locale.get("polish.predictive.setup.title") );
		
		this.form.addCommand( this.cancelCommand );
		this.form.setCommandListener( this );
		
		this.info = new StringItem("",Locale.get("polish.predictive.setup.info"));
		this.status = new StringItem("","");
		this.error = new StringItem("","");
		this.gauge = new Gauge("",true,100,0);
		
		//#style setupIntro
		this.form.append(info);
		
		if(showGauge)
		{
			//#style setupGauge
			this.form.append(gauge);
		}
		
		//#style setupStatus
		this.form.append(status);
		//#style setupError
		this.form.append(error);
		
		Display.getDisplay(this.parentMidlet).setCurrent(this.form);
	}
	
	public void pause()
	{
		this.pause = true;
	}
	
	public void run()
	{
		try {
			this.installer = new TrieInstaller(this.stream);
			
			this.status.setText(Locale.get("polish.predictive.setup.status.delete"));
			
			String[] storeList = RecordStore.listRecordStores();
			
			if(storeList != null)
			{
				for(int i=0; i<storeList.length; i++)
					if(storeList[i].startsWith(TrieInstaller.PREFIX))
					{
						RecordStore.deleteRecordStore(storeList[i]);
					}
			}
			
			this.status.setText(Locale.get("polish.predictive.setup.status.install"));
			
			int totalBytes = stream.available();
			this.gauge.setMaxValue(totalBytes);
			
			byte[] nodes;
			RecordStore store = null;
			
			int count = 0;
			int storeID = 0;
			
			do
			{
				nodes = null;
				
				if((count % installer.getChunkSize()) == 0)
				{
					if(store != null)
					{
						store.closeRecordStore();
						storeID += installer.getChunkSize();
					}
					
					//#if (polish.Bugs.sharedRmsRequiresSigning || polish.predictive.useLocalRMS)
					//#= store = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_" + storeID, true); 
					//#else
					store = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_" + storeID, true, RecordStore.AUTHMODE_ANY, true);
					//#endif
					
					if(storeID == 0)
					{
						installer.createHeaderRecord(store);
						installer.createCustomRecord(store);
						installer.createOrderRecord(store);
					}
				}
					
				nodes = installer.getRecords(stream, installer.getLineCount());
				
				this.gauge.setValue(totalBytes - stream.available());
				
				count++;
				
				store.addRecord(nodes, 0, nodes.length);
				
				if(this.pause)
				{
					try
					{
						store.closeRecordStore();
						
						synchronized (this){
							this.wait();
						}
						
						//#if !polish.Bugs.sharedRmsRequiresSigning || !polish.predictive.useLocalRMS
						//#= store = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_" + storeID, true, RecordStore.AUTHMODE_ANY, true);
						//#else
						store = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_" + storeID, true);
						//#endif
						
					}catch(InterruptedException e){}
					this.pause = false;
				}
				
			}while(stream.available() > 0);
			
			store.closeRecordStore();
			
			this.status.setText(Locale.get("polish.predictive.setup.status.finished"));
			this.form.removeCommand( this.cancelCommand );
			this.form.addCommand( this.exitCommand );
			
			Thread.sleep(10000);
			
			if(this.returnTo == null)
				this.parentMidlet.notifyDestroyed();
			else
				Display.getDisplay(this.parentMidlet).setCurrent(this.returnTo);
			
		} catch (Exception e) {
			this.status.setText(Locale.get("polish.predictive.setup.error"));
			this.status.setText(e.getMessage());
			
			this.form.removeCommand( this.cancelCommand );
			this.form.addCommand( this.exitCommand );
			
			e.printStackTrace();
		}
	}

	public Form getForm() {
		return form;
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == this.cancelCommand) {
			Alert cancel = new Alert("Predictive Setup");
			
			cancel.setString(Locale.get("polish.predictive.setup.cancel"));
			cancel.addCommand(this.yesCommand);
			cancel.addCommand(this.noCommand);
			
			cancel.setCommandListener(this);
			
			this.pause();
			
			Display.getDisplay(this.parentMidlet).setCurrent(cancel);
		} else if (cmd == this.exitCommand) {
			if(this.returnTo == null)
				this.parentMidlet.notifyDestroyed();
			else
				Display.getDisplay(this.parentMidlet).setCurrent(this.returnTo);
		} 
		
		if(disp instanceof Alert)
		{
			if(cmd == this.yesCommand)
			{
				this.parentMidlet.notifyDestroyed();
			} 
			else if(cmd == this.noCommand)
			{
				synchronized (this){
					this.notify();
				}
				
				Display.getDisplay(this.parentMidlet).setCurrent(this.form);
			}
		}
	}
}