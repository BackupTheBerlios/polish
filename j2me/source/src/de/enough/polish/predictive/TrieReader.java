//#condition polish.TextField.useDirectInput && !polish.blackberry
package de.enough.polish.predictive;

import java.util.EmptyStackException;
import java.util.Stack;

import javax.microedition.lcdui.Canvas;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

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
	
	private StringBuffer storeID = null;
	private StringBuffer letters = null;
	
	private int selectedWord = 0;
	
	private boolean empty;
	private boolean wordFound;

	ArrayList results = null;
	
	int size = 0;
	
	public TrieReader(HashMap stores, HashMap records) throws RecordStoreException
	{
		this.nodes 		= new ArrayList();
		this.prevNodes 	= new Stack();
		
		this.results	= new ArrayList();
		
		this.stores = stores;
		this.records = records;
		
		storeID = new StringBuffer(20);
		letters = new StringBuffer(10);
		
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
	
	public ArrayList getResults()
	{
		return this.results;
	}
		
	public void keyNum(int keyCode) throws RecordStoreException
	{	
		byte[] record = null;
		
		int partOffset = 0;
		
		ArrayList newNodes = new ArrayList();
		
		pushNodes();
		
		if(this.nodes.size() == 0)
		{
			record = this.getRecord(1);
			partOffset = this.getPartOffset(record, getPartID(1));
			this.nodes = this.getNodes(record, partOffset, keyCode, null);
			
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
						record = this.getRecord(node.getReference());
						partOffset = this.getPartOffset(record, getPartID(node.getReference()));
						addToNodes(this.getNodes(record, partOffset, keyCode, node.getWord()),newNodes);
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
		results.clear(); 
		for(int i=0; i<nodes.size(); i++)
			results.add(((TrieNode)nodes.get(i)).getWord());
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
			try
			{
				this.nodes = (ArrayList)this.prevNodes.pop();
			}
			catch(EmptyStackException e)
			{
				this.nodes = new ArrayList();
			}
			
			if(!undo)
			{
				setEmpty(this.nodes.size() == 0);
				setWordFound(this.nodes.size() > 0);
			}
		}
	}
	
	private int getRecordID(int id)
	{
		return ((id -1) / this.lineCount) + 1;
	}
	
	private int getPartID(int id)
	{
		return ((id -1) % this.lineCount) + 1;
	}
		
	private byte[] getRecord(int id) throws RecordStoreException
	{
		int recordID 	= getRecordID(id);
		
		storeID.setLength(0);
		storeID.append(TrieInstaller.prefix);
		storeID.append("_");
		storeID.append(recordID - (recordID % this.chunkSize));
		
		RecordStore store = (RecordStore)stores.get(storeID);
		
		if(store == null)
		{	
			/*if(!)
				store = RecordStore.openRecordStore(storeID, this.properties.getVendor(), this.properties.getSuite());
			else
				store = RecordStore.openRecordStore(storeID, false);
			*/
			
			store = RecordStore.openRecordStore(storeID.toString(), false);
			stores.put(storeID, store);
		}
		
		Integer recordMapID = new Integer(recordID + TrieInstaller.OVERHEAD % this.chunkSize);
		byte[] record = (byte[])this.records.get(recordMapID); 
		
		if(record == null)
		{
			record = store.getRecord(recordMapID.intValue());
			this.records.put(recordMapID, record);
		}
		
		return record; 
	}
	
	private int getPartOffset(byte[] record, int partID)
	{
		byte partCount 	= 0;
		int partOffset 	= 0;
		
		for(int i=0; i<partID; i++)
		{
			partCount = byteToByte(record, partOffset);
			
			if(i == (partID - 1))
				return partOffset;
			else
				partOffset += (partCount * NODE_SIZE) + COUNT_SIZE;				
		}
		
		return 0;
	}
	
	/*private byte[] getRecordPart(byte[] record, int partID)
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
	}*/
	
	private byte[] getPart(byte[] bytes, int offset, int partCount)
	{
		byte[] part = new byte[partCount];
		
		size += partCount;
		System.out.println(size);
		
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
	
	private ArrayList getNodes(byte[] record, int partOffset, int keyCode, StringBuffer word)
	{
		ArrayList getNodes = new ArrayList();
		
		char value 		= ' ';
		
		byte partCount = byteToByte(record,partOffset);
		
		setLetters(keyCode);
		
		for(int i= (partOffset + COUNT_SIZE); i < (partOffset + COUNT_SIZE) + (partCount * NODE_SIZE); i = i + NODE_SIZE)
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
			
	private void setLetters(int keyCode)
	{
		this.letters.setLength(0);
		switch(keyCode)
		{
			case Canvas.KEY_NUM1: 	this.letters.append(TextField.CHARACTERS[1]); break; 
			case Canvas.KEY_NUM2: 	this.letters.append(TextField.CHARACTERS[2]); break;
			case Canvas.KEY_NUM3: 	this.letters.append(TextField.CHARACTERS[3]); break;
			case Canvas.KEY_NUM4: 	this.letters.append(TextField.CHARACTERS[4]); break;
			case Canvas.KEY_NUM5: 	this.letters.append(TextField.CHARACTERS[5]); break;
			case Canvas.KEY_NUM6: 	this.letters.append(TextField.CHARACTERS[6]); break;
			case Canvas.KEY_NUM7: 	this.letters.append(TextField.CHARACTERS[7]); break;
			case Canvas.KEY_NUM8: 	this.letters.append(TextField.CHARACTERS[8]); break;
			case Canvas.KEY_NUM9: 	this.letters.append(TextField.CHARACTERS[9]); break;
			default: 				this.letters.append(TextField.CHARACTERS[1]); break;
		}
	}
	
	public void setSelectedWord(int selectedWord) {
		this.selectedWord = selectedWord;
	}
	
	public StringBuffer getSelectedWord()
	{
		if(this.nodes.size() > 0)
			if(this.nodes.get(this.selectedWord) instanceof TrieNode)
				return ((TrieNode)this.nodes.get(this.selectedWord)).getWord();
			else
				return null;
		else
			return null;
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
