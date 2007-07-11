package de.enough.polish.predictive;

import java.io.ByteArrayInputStream;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.microedition.lcdui.Canvas;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import de.enough.polish.util.Properties;
import de.enough.polish.ui.TextField;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;

public class TrieReader {
	
	static final String prefix = "predictive";
	
	static final byte V_OFFSET = 0;
	static final byte CC_OFFSET = 2;
	static final byte CR_OFFSET = 3;
	static final byte NODE_SIZE = 5;
	static final byte COUNT_SIZE = 1;
	
	int lineCount = 0;
	int chunkSize = 0;
	
	private ArrayList nodes = null;
	private Stack prevNodes = null;
	
	private HashMap stores = null;
	private HashMap records = null;
	
	private int selectedWord = 0;
	
	private boolean empty;
	private boolean wordFound;

	StringBuffer[] results = null;
	
	public TrieReader(HashMap stores, HashMap records) throws RecordStoreException
	{
		this.nodes 		= new ArrayList();
		this.prevNodes 	= new Stack();
		
		this.stores = stores;
		this.records = records;
		
		getHeader();
				
		this.empty 		= true;
		this.wordFound	= true;
	}
	
	private void getHeader() throws RecordStoreException
	{
		String storeID = prefix + "_0";
		
		RecordStore store = RecordStore.openRecordStore(storeID, false);
		
		byte[] bytes = store.getRecord(TrieInstaller.HEADER_RECORD);
		
		this.chunkSize = byteToInt(bytes, TrieInstaller.CHUNKSIZE_OFFSET);
		this.lineCount = byteToInt(bytes, TrieInstaller.LINECOUNT_OFFSET);
	}
	
	public StringBuffer[] getResults()
	{
		return this.results;
	}
		
	public void keyNum(int keyCode) throws RecordStoreException
	{	
		byte[] record = null;
		ArrayList newNodes = new ArrayList();
		
		pushNodes();
		
		if(this.nodes.size() == 0)
		{
			record = this.getRecord(1,this.lineCount);
			this.nodes = this.getNodes(record, keyCode, "");
			
			setEmpty(nodes.size() == 0);
		}
		else
		{
			for(int nodeIndex = 0; nodeIndex < this.nodes.size(); nodeIndex++)
			{
				if(this.nodes.get(nodeIndex) instanceof TrieNode)
				{
					TrieNode node = (TrieNode)this.nodes.get(nodeIndex);
					
					if(node.getReference() != 0)
					{
						record = this.getRecord(node.getReference(),this.lineCount);
						addToNodes(this.getNodes(record, keyCode, node.getWord()),newNodes);
					}
				}
			}
			
			copyNodes(newNodes,this.nodes);
		}
		
		setWordFound(this.nodes.size() > 0);
		
		if(!isWordFound())
			popNodes(true);
		
		closeStores();
		
		setNodeWords(this.nodes);
	}
	
	public void keyClear() throws RecordStoreException
	{	
		popNodes(false);
		
		setNodeWords(nodes);
	}
			
	public void addToNodes(ArrayList source, ArrayList dest)
	{
		for(int i=0; i<source.size(); i++)
			dest.add(source.get(i));
	}
	
	public void setNodeWords(ArrayList nodes)
	{
		results = new StringBuffer[nodes.size()]; 
		for(int i=0; i<nodes.size(); i++)
		{
			if(results[i] == null)
				results[i] = new StringBuffer(1);
		
			if(nodes.get(i) instanceof TrieNode)
				results[i].append(((TrieNode)nodes.get(i)).getWord());
			else if(nodes.get(i) instanceof StringBuffer)
				results[i].append((StringBuffer)nodes.get(i));
		}
	}
	
	private void copyNodes(ArrayList source, ArrayList dest)
	{
		dest.clear();
		for(int i=0; i < source.size(); i++)
			dest.add(i, source.get(i));
	}
	
	private void pushNodes()
	{
		ArrayList newNodes = new ArrayList();
		
		copyNodes(this.nodes,newNodes);
		this.prevNodes.push(newNodes);
	}
	
	private void popNodes(boolean undo)
	{
		if(!isEmpty())
		{
			ArrayList newNodes = null;
			
			try
			{
				newNodes = (ArrayList)this.prevNodes.pop();
			}
			catch(EmptyStackException e)
			{
				newNodes = new ArrayList();
			}
			
			copyNodes(newNodes,this.nodes);
			
			if(!undo)
			{
				setEmpty(this.nodes.size() == 0);
				setWordFound(this.nodes.size() > 0);
			}
		}
	}
		
	private byte[] getRecord(int id, int lineCount) throws RecordStoreException
	{
		int recordID 	= ((id -1) / lineCount) + 1; 
		int partID 		= ((id -1) % lineCount) + 1;
		
		String storeID = this.prefix + "_" + (recordID - (recordID % this.chunkSize));
		
		RecordStore store = (RecordStore)stores.get(storeID);
		
		if(store == null)
		{	
			/*if(!)
				store = RecordStore.openRecordStore(storeID, this.properties.getVendor(), this.properties.getSuite());
			else
				store = RecordStore.openRecordStore(storeID, false);
			*/
			
			store = RecordStore.openRecordStore(storeID, false);
			stores.put(storeID, store);
		}
		
		Integer recordMapID = new Integer(recordID + TrieInstaller.OVERHEAD % this.chunkSize);
		byte[] record = (byte[])this.records.get(recordMapID); 
		
		if(record == null)
		{
			record = store.getRecord(recordMapID);
			this.records.put(recordMapID, record);
		}
		
		return getRecordPart(record,partID); 
	}
	
	private byte[] getRecordPart(byte[] record, int partID)
	{
		byte partCount 	= 0;
		int partOffset 	= 0;
		
		for(int i=0; i<partID; i++)
		{
			partCount = byteToByte(record, partOffset);
			
			if(i == (partID - 1))
				return getPart(record, partOffset + COUNT_SIZE, partCount * NODE_SIZE);
			else
				partOffset += (partCount * NODE_SIZE) + COUNT_SIZE;				
		}
		
		return null;
	}
	
	private byte[]getPart(byte[] bytes, int offset, int partCount)
	{
		byte[] part = new byte[partCount];
		
		for(int i=0; i<partCount; i++)
			part[i] = bytes[offset + i];
		
		return part;
	}
	
	public void closeStores() throws RecordStoreNotOpenException, RecordStoreException
	{
		Object[] storeObjects = stores.values();
		
		for(int i=0; i<storeObjects.length; i++)
			((RecordStore)storeObjects[i]).closeRecordStore();
		
		stores.clear();
	}
	
	private ArrayList getNodes(byte[] record, int keyCode, String word)
	{
		ArrayList getNodes = new ArrayList();
		
		char value 		= ' ';
		String letters	= "";
				
		letters = getLetters(keyCode);
		
		for(int i=0; i<record.length; i=i+NODE_SIZE)
		{
			value = byteToChar(record, i+V_OFFSET);
			
			for(int j=0;j<letters.length();j++)
			{
				if(value == letters.charAt(j))
				{
					TrieNode node = new TrieNode();
					node.appendToWord(word);
					node.appendToWord(value);
					
					if(byteToByte(record, i+CC_OFFSET) != 0)
						node.setReference((int)byteToChar(record, i+CR_OFFSET)); 
					else
						node.setReference(0);
					
					getNodes.add(node);
				}
			}
		}
		
		return getNodes;
	}
	
	private int byteToInt(byte[] bytes, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (bytes[i + offset] & 0x000000FF) << shift;
		}
		return value;
    }
	
	private char byteToChar(byte[] bytes, int offset)
	{
		int high = bytes[offset] & 0xff;
		int low = bytes[offset+1] & 0xff;
		return (char)((int)( high << 8 | low ));
	}
		
	private byte byteToByte(byte[] bytes, int offset)
	{
		return bytes[offset];
	}
			
	private String getLetters(int keyCode)
	{
		switch(keyCode)
		{
			case Canvas.KEY_NUM1: 	return TextField.CHARACTERS[1];
			case Canvas.KEY_NUM2: 	return TextField.CHARACTERS[2];
			case Canvas.KEY_NUM3: 	return TextField.CHARACTERS[3];
			case Canvas.KEY_NUM4: 	return TextField.CHARACTERS[4];
			case Canvas.KEY_NUM5: 	return TextField.CHARACTERS[5];
			case Canvas.KEY_NUM6: 	return TextField.CHARACTERS[6];
			case Canvas.KEY_NUM7: 	return TextField.CHARACTERS[7];
			case Canvas.KEY_NUM8: 	return TextField.CHARACTERS[8];
			case Canvas.KEY_NUM9: 	return TextField.CHARACTERS[9];
			default: 				return TextField.CHARACTERS[1];
		}
	}
	
	public void reset()
	{
		this.nodes.clear();
		this.prevNodes.removeAllElements();
		this.setEmpty(true);
		this.setWordFound(false);
	}

	public void setSelectedWord(int selectedWord) {
		this.selectedWord = selectedWord;
	}
	
	public String getSelectedWord()
	{
		if(this.nodes.size() > 0)
			if(this.nodes.get(this.selectedWord) instanceof TrieNode)
				return ((TrieNode)this.nodes.get(this.selectedWord)).getWord();
			else if (this.nodes.get(this.selectedWord) instanceof StringBuffer)
				return ((StringBuffer)this.nodes.get(this.selectedWord)).toString();
			else 
				return "";
		else
			return "";
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public boolean isWordFound() {
		return wordFound;
	}

	public void setWordFound(boolean wordFound) {
		this.wordFound = wordFound;
	}
}
