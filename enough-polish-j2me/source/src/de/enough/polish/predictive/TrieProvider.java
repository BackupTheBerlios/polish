//#condition polish.TextField.useDirectInput && !polish.blackberry
package de.enough.polish.predictive;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.util.HashMap;

public class TrieProvider {
	private RecordStore store = null;
	private TrieCustom custom = null;
	private HashMap records   = null; 
	
	private int chunkSize = 0;
	private int lineCount = 0;
	
	public TrieProvider()
	{
		try
		{
			System.out.println("new provider");
			
			this.store 	= RecordStore.openRecordStore(TrieInstaller.PREFIX + "_0", false);
			
			this.records = new HashMap();
			this.custom = new TrieCustom();
			
			byte[] bytes = this.store.getRecord(TrieInstaller.HEADER_RECORD);
			
			this.chunkSize = TrieUtils.byteToInt(bytes, TrieInstaller.CHUNKSIZE_OFFSET);
			this.lineCount = TrieUtils.byteToInt(bytes, TrieInstaller.LINECOUNT_OFFSET);
		}
		catch(RecordStoreException e)
		{
			e.printStackTrace();
		}
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public TrieCustom getCustom() {
		return custom;
	}

	public int getLineCount() {
		return lineCount;
	}

	public HashMap getRecords() {
		return records;
	}

	public RecordStore getStore() {
		return store;
	}
	
	
	
}
