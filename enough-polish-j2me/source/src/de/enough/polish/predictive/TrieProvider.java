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
	
	private int version		= 0;
	private int chunkSize 	= 0;
	private int lineCount 	= 0;
	
	private int maxRecords = 5;
	
	private TrieCustom custom = null;
	private TrieOrder order = null;
	
	private Form customForm = null;
	private TextField customField = null;
	
	public TrieProvider()
	{
		this.init = false;
	}
	
	public void init() throws RecordStoreException
	{
		//#if polish.Bugs.sharedRmsRequiresSigning || polish.predictive.uselocalRMS
		//#= this.store 	= RecordStore.openRecordStore(TrieInstaller.PREFIX + "_0", false);
		//#else
		this.store 	= RecordStore.openRecordStore(TrieInstaller.PREFIX + "_0","Enough Software","PredictiveSetup");
		//#endif
		
		this.records = new HashMap();
		
		byte[] bytes = this.store.getRecord(TrieInstaller.HEADER_RECORD);
		
		this.version	= TrieUtils.byteToInt(bytes, TrieInstaller.VERSION_OFFSET);
		
		if(this.version != TrieInstaller.VERSION)
		{
			throw new RecordStoreException("");
		}
		
		this.chunkSize 	= TrieUtils.byteToInt(bytes, TrieInstaller.CHUNKSIZE_OFFSET);
		this.lineCount 	= TrieUtils.byteToInt(bytes, TrieInstaller.LINECOUNT_OFFSET);
		
		this.custom = new TrieCustom();
		
		this.order = new TrieOrder();
		
		this.customForm = new Form("Add new word");
		
		this.customForm.addCommand( StyleSheet.CANCEL_CMD );
		this.customForm.addCommand( StyleSheet.OK_CMD );
		
		this.init = true;
	}
	
	public boolean isInit()
	{
		return this.init;
	}
	
	public byte[] getRecord(int id) throws RecordStoreException
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
	
	public void setRecord(int id, byte[] bytes) throws RecordStoreException
	{
		Integer recordMapID = new Integer(id);
		TrieRecord record = (TrieRecord)this.records.get(recordMapID);
		
		if(record == null)
		{
			record = new TrieRecord(id,bytes);
			this.records.put(recordMapID, record);
		}
		
		this.store.setRecord(id, bytes, 0, bytes.length);
	}
	
	public void releaseRecords()
	{
		if (this.records == null) {
			return;
		}
		Object[] keys = this.records.keys();
		
		Integer keyToRemove = null;
		int	references = 0;
		
		while(keys.length > this.maxRecords)
		{
			references = 1000;
			
			for(int i=0; i<keys.length; i++)
			{
				Integer key = (Integer)keys[i];
				TrieRecord record = (TrieRecord)this.records.get(key);
				
				if(record.getReferences() <= references && record.getId() != TrieInstaller.CUSTOM_RECORD && record.getId() != TrieInstaller.ORDER_RECORD)
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
	
	public TrieOrder getOrder() {
		return order;
	}

	public int getLineCount() {
		return lineCount;
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
