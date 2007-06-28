package de.enough.polish.predictive;

import java.util.EmptyStackException;
import java.util.Stack;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;

public class TrieReader {
	
	final int V_OFFSET = 0;
	final int CC_OFFSET = 2;
	final int CR_OFFSET = 3;
	final int NODE_SIZE = 5;
	final int COUNT_SIZE = 1;
	
	private ArrayList nodes = null;
	private Stack prevNodes = null;
	
	private HashMap stores = null;
	
	private TrieProperties properties = null;
	
	private int selectedWord = 0;
	
	private boolean empty;
	private boolean wordFound;

	String[] results = null;
	
	public TrieReader(String prefix, TrieProperties properties) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException
	{
		this.nodes 		= new ArrayList();
		this.prevNodes 	= new Stack();
		
		this.stores = new HashMap();
		
		this.properties = properties;
		
		this.empty 		= true;
		this.wordFound	= true;
	}
	
	public String[] getResults()
	{
		return this.results;
	}
		
	public void keyNum(char key) throws RecordStoreException
	{	
		byte[] record = null;
		ArrayList newNodes = new ArrayList();
		
		pushNodes();
		
		if(this.nodes.size() == 0)
		{
			record = this.getRecord(1,this.properties.getLineCount());
			this.nodes = this.getNodes(record, key, "");
			
			setEmpty(nodes.size() == 0);
		}
		else
		{
			for(int nodeIndex = 0; nodeIndex < this.nodes.size(); nodeIndex++)
			{
				TrieNode node = (TrieNode)this.nodes.get(nodeIndex);
				
				if(node.getReference() != 0)
				{
					record = this.getRecord(node.getReference(),this.properties.getLineCount());
					addToNodes(this.getNodes(record, key, node.getWord()),newNodes);
				}
			}
			
			copyNodes(newNodes,this.nodes);
		}
		
		setWordFound(this.nodes.size() > 0);
		
		if(!isWordFound())
			popNodes(true);
		
		closeStores();
		results = getNodeWords(this.nodes);
	}
	
	public void keyClear() throws RecordStoreException
	{	
		popNodes(false);
		results = getNodeWords(nodes);
	}
			
	public void addToNodes(ArrayList source, ArrayList dest)
	{
		for(int i=0; i<source.size(); i++)
			dest.add(source.get(i));
	}
	
	public String[] getNodeWords(ArrayList nodes)
	{
		String[] words = new String[nodes.size()]; 
		for(int i=0; i<nodes.size(); i++)
			words[i] = ((TrieNode)nodes.get(i)).getWord();
		
		return words;
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
		
	private byte[] getRecord(int id, int lineCount) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException
	{
		int recordID 	= ((id-1) / lineCount) + 1; 
		int partID 		= ((id-1) % lineCount) + 1;
		
		String storeID = this.properties.getPrefix() + ":" + (recordID - (recordID % this.properties.getChunkSize()));
		
		RecordStore store = (RecordStore)stores.get(storeID);
		
		if(store == null)
		{	
			if(!properties.isLocalRMS())
				store = RecordStore.openRecordStore(storeID, this.properties.getVendor(), this.properties.getSuite());
			else
				store = RecordStore.openRecordStore(storeID, false);
			
			store = RecordStore.openRecordStore(storeID, false);
			stores.put(storeID, store);
		}
		
		byte[] record = store.getRecord(recordID % this.properties.getChunkSize()); 

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
	
	private ArrayList getNodes(byte[] record, char code, String word)
	{
		ArrayList getNodes = new ArrayList();
		
		char value 		= ' ';
		String letters	= "";
				
		letters = getLetters(code);
		
		for(int i=0; i<record.length; i=i+NODE_SIZE)
		{
			value = byteToChar(record, i+V_OFFSET);
			
			for(int j=0;j<letters.length();j++)
			{
				if(value == letters.charAt(j))
				{
					TrieNode node = new TrieNode();
					node.setWord(word + value);
					
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
			
	private String getLetters(char code)
	{
		switch(code)
		{
			case '1': 	return "\"\'-.,:;!?";
			case '2': 	return "abcä";
			case '3': 	return "def";
			case '4': 	return "ghi";
			case '5': 	return "jkl";
			case '6': 	return "mnoö";
			case '7': 	return "pqrsß";
			case '8': 	return "tuvü";
			case '9': 	return "wxyz";
			default: 	return "\"\'-.,:;!?";
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
			return ((TrieNode)this.nodes.get(this.selectedWord)).getWord();
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
