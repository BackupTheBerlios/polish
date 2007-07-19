//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui
package de.enough.polish.predictive;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

import de.enough.polish.ui.Form;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.HashMap;

public class TrieProvider {
	private RecordStore store = null;
	private HashMap records   = null; 
	
	private int chunkSize = 0;
	private int lineCount = 0;
	
	private TrieCustom custom = null;
	private Form customForm = null;
	private TextField customField = null;
	
	public TrieProvider()
	{
		try
		{
			System.out.println("new provider");
			
			this.store 	= RecordStore.openRecordStore(TrieInstaller.PREFIX + "_0", false);
			
			this.records = new HashMap();
			
			byte[] bytes = this.store.getRecord(TrieInstaller.HEADER_RECORD);
			
			this.chunkSize = TrieUtils.byteToInt(bytes, TrieInstaller.CHUNKSIZE_OFFSET);
			this.lineCount = TrieUtils.byteToInt(bytes, TrieInstaller.LINECOUNT_OFFSET);
			
			this.custom = new TrieCustom();
			
			this.customForm = new Form("Add new word");
			
			this.customField = new TextField("Word:","",50,TextField.ANY);
			
			this.customForm.append(customField);
			this.customForm.addCommand( StyleSheet.CANCEL_CMD );
			this.customForm.addCommand( StyleSheet.OK_CMD );
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

	public Form getCustomForm() {
		return customForm;
	}

	public TextField getCustomField() {
		return customField;
	}
}
