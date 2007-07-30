package de.enough.polish.predictive;

import java.io.DataInputStream;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
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
	
	protected Command exitCommand = new Command( Locale.get("cmd.exit"), Command.EXIT, 0 );
	protected Command cancelCommand = new Command( Locale.get("cmd.cancel"), Command.CANCEL, 0 );
	
	protected Command yesCommand = new Command( Locale.get("cmd.yes"), Command.OK, 0 );
	protected Command noCommand = new Command( Locale.get("cmd.no"), Command.CANCEL, 0 );
	
	Form form = null;
	
	StringItem info = null;
	StringItem status = null;
	StringItem error = null;
	Gauge gauge = null;
		
	boolean pause = false;
	
	Setup(MIDlet parentMidlet, Displayable returnTo, DataInputStream stream)
	{
		this.returnTo = returnTo;
		this.parentMidlet = parentMidlet;
		
		this.stream = stream;
		
		//#style mainScreen
		this.form = new Form( "Predictive Setup");
		
		this.form.addCommand( this.cancelCommand );
		this.form.setCommandListener( this );
		
		this.info = new StringItem("",Locale.get("text.info"));
		this.status = new StringItem("","");
		this.error = new StringItem("","");
		this.gauge = new Gauge("",true,100,0);
		
		//#style introItem
		this.form.append(info);
		//#style gauge
		this.form.append(gauge);
		//#style statusItem
		this.form.append(status);
		//#style errorItem
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
			
			this.status.setText(Locale.get("text.status.delete"));
			
			String[] storeList = RecordStore.listRecordStores();
			
			if(storeList != null)
			{
				for(int i=0; i<storeList.length; i++)
					if(storeList[i].startsWith(TrieInstaller.PREFIX))
					{
						RecordStore.deleteRecordStore(storeList[i]);
					}
			}
			
			this.status.setText(Locale.get("text.status.install"));
			
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
					
					store = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_" + storeID, true, RecordStore.AUTHMODE_ANY, true);
					
					if(storeID == 0)
					{
						installer.createHeaderRecord(store);
						installer.createCustomRecord(store);
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
						
						store = RecordStore.openRecordStore(TrieInstaller.PREFIX + "_" + storeID, true, RecordStore.AUTHMODE_ANY, true);
						
					}catch(InterruptedException e){}
					this.pause = false;
				}
				
			}while(stream.available() > 0);
			
			store.closeRecordStore();
			
			this.status.setText(Locale.get("text.status.finished"));
			this.form.removeCommand( this.cancelCommand );
			this.form.addCommand( this.exitCommand );
			
			Thread.sleep(10000);
			
			if(this.returnTo == null)
				this.parentMidlet.notifyDestroyed();
			else
				Display.getDisplay(this.parentMidlet).setCurrent(this.returnTo);
			
		} catch (Exception e) {
			this.status.setText(Locale.get("text.error"));
			this.status.setText(e.getMessage());
			
			this.form.removeCommand( this.cancelCommand );
			this.form.addCommand( this.exitCommand );
			
			//#debug error
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
			Alert cancel = new Alert(Locale.get("text.cancel"));
			
			cancel.setString(Locale.get("text.cancel"));
			cancel.addCommand(this.yesCommand);
			cancel.addCommand(this.noCommand);
			
			cancel.setCommandListener(this);
			
			this.pause();
			
			Display.getDisplay(this.parentMidlet).setCurrent(cancel);
		} else if (cmd == this.exitCommand) {
			this.parentMidlet.notifyDestroyed();
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