//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput
package de.enough.polish.predictive;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.ui.Form;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.HashMap;

public class TrieProvider {
	private boolean init = false;
	
	private RecordStore store = null;
	private HashMap records   = null; 
	
	private int chunkSize = 0;
	private int lineCount = 0;
	
	private int maxRecords = 5;
	
	private TrieCustom custom = null;
	private Form customForm = null;
	private TextField customField = null;
	
	public TrieProvider()
	{
		this.init = false;
	}
	
	public void init() throws RecordStoreException
	{
		//#if polish.Bugs.sharedRmsRequiresSigning || polish.predictive.localRMS
		//#= this.store 	= RecordStore.openRecordStore(TrieInstaller.PREFIX + "_0", false);
		//#else
		this.store 	= RecordStore.openRecordStore(TrieInstaller.PREFIX + "_0","Enough Software","PredictiveSetup");
		//#endif
		
		this.records = new HashMap();
		
		byte[] bytes = this.store.getRecord(TrieInstaller.HEADER_RECORD);
		
		this.chunkSize = TrieUtils.byteToInt(bytes, TrieInstaller.CHUNKSIZE_OFFSET);
		this.lineCount = TrieUtils.byteToInt(bytes, TrieInstaller.LINECOUNT_OFFSET);
		
		this.custom = new TrieCustom();
		
		this.customForm = new Form("Add new word");
		
		this.customForm.addCommand( StyleSheet.CANCEL_CMD );
		this.customForm.addCommand( StyleSheet.OK_CMD );
		
		this.init = true;
	}
	
	public boolean isInit()
	{
		return this.init;
	}
	
	public byte[] getRecord(int id, int hash) throws RecordStoreException
	{
		Integer recordMapID = new Integer(id);
		TrieRecord record = (TrieRecord)this.records.get(recordMapID); 
		
		if(record == null)
		{
			record = new TrieRecord(id,store.getRecord(id));
			
			this.records.put(recordMapID, record);
		}
		
		record.addReference();
		
		return record.getRecord();
	}
	
	public void releaseRecords()
	{
		Object[] keys = this.records.keys();
		
		Integer keyToRemove = null;
		int	references = 0;
		
		while(keys.length > maxRecords)
		{
			references = 1000;
			
			for(int i=0; i<keys.length; i++)
			{
				Integer key = (Integer)keys[i];
				TrieRecord record = (TrieRecord)this.records.get(key);
				
				if(record.getReferences() <= references)
				{
					keyToRemove = (Integer)keys[i];
					references = record.getReferences();
				}
			}
			
			this.records.remove(keyToRemove);
			
			keys = this.records.keys();
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

	public RecordStore getStore() {
		return store;
	}

	public Form getCustomForm() {
		return customForm;
	}

	public TextField getCustomField() {
		return customField;
	}

	public void setCustomField(TextField customField) {
		this.customField = customField;
	}
}
