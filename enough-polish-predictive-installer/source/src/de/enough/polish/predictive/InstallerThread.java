package de.enough.polish.predictive;

import java.io.DataInputStream;
import javax.microedition.rms.RecordStore;

public class InstallerThread 
implements Runnable
{
	private InstallerMidlet parent;
	
	DataInputStream stream = null;
	TrieInstaller installer = null;
		
	boolean pause = false;
	
	InstallerThread(InstallerMidlet parent)
	{
		this.parent = parent;
	}
	
	public void pause()
	{
		this.pause = true;
	}
	
	public void run()
	{
		try {
			this.installer = new TrieInstaller();
			this.stream = installer.getStream(); 
			
			this.parent.status.setText("Deleting previous installation ...");
			
			String[] storeList = RecordStore.listRecordStores();
			
			if(storeList != null)
			{
				for(int i=0; i<storeList.length; i++)
					if(storeList[i].startsWith(TrieInstaller.PREFIX))
					{
						RecordStore.deleteRecordStore(storeList[i]);
					}
			}
			
			this.parent.status.setText("Installing ...");
			
			int totalBytes = stream.available();
			this.parent.gauge.setMaxValue(totalBytes);
			
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
				
				this.parent.gauge.setValue(totalBytes - stream.available());
				
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
			
			this.parent.status.setText("Finished, the installer will exit in 10 seconds ...");
			this.parent.form.removeCommand( this.parent.cancelCommand );
			this.parent.form.addCommand( this.parent.exitCommand );
			
			Thread.sleep(10000);
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