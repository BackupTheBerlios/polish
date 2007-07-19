//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui
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
	static final byte V_OFFSET = 0;
	static final byte CC_OFFSET = 2;
	static final byte CR_OFFSET = 3;
	static final byte NODE_SIZE = 5;
	static final byte COUNT_SIZE = 1;
	
	int lineCount = 0;
	int chunkSize = 0;
	
	private ArrayList 	nodes = null;
	private ArrayList 	newNodes = null;
	private Stack 		prevNodes = null;
	
	private RecordStore store = null;
	private HashMap records = null;
	
	private StringBuffer storeID = null;
	private StringBuffer letters = null;
	
	private int selectedWord = 0;
	
	private boolean empty;
	private boolean wordFound;
	
	int keyCount = 0;

	byte[] record = null;
	
	public TrieReader(RecordStore store, HashMap records, int chunkSize, int lineCount) throws RecordStoreException
	{
		this.nodes 		= new ArrayList();
		this.newNodes 	= new ArrayList();
		
		this.prevNodes 	= new Stack();
				
		this.store = store;
		this.records = records;
		
		this.chunkSize = chunkSize;
		this.lineCount = lineCount;
		
		letters = new StringBuffer(10);
				
		this.empty 		= true;
		this.wordFound	= true;
	}
		
	public void keyNum(int keyCode) throws RecordStoreException
	{	
		int partOffset = 0;
		
		pushNodes();
		
		if(this.nodes.size() == 0)
		{
			this.record = this.getRecord(1);
			partOffset = this.getPartOffset(this.record, getPartID(1));
			this.readNodes(this.record, partOffset, keyCode, null);
			
			setEmpty(this.newNodes.size() == 0);
		}
		else
		{
			for(int nodeIndex = 0; nodeIndex < this.nodes.size(); nodeIndex++)
			{
				TrieNode node = (TrieNode)this.nodes.get(nodeIndex);
				
				if(node.getReference() != 0)
				{
					record = this.getRecord(node.getReference());
					partOffset = this.getPartOffset(record, getPartID(node.getReference()));
					readNodes(record, partOffset, keyCode, node.getWord());
				}
			}
		}
		
		copyNodes(this.newNodes,this.nodes);
		this.newNodes.clear();
		
		setWordFound(this.nodes.size() > 0);
		
		if(!isWordFound())
			popNodes(true);
	}
	
	public void keyClear() throws RecordStoreException
	{	
		popNodes(false);
	}
	
	public ArrayList getNodes()
	{
		return this.nodes;
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
		
		keyCount++;
		
		this.prevNodes.push(newNodes);
	}
	
	private void popNodes(boolean undo)
	{
		if(!isEmpty())
		{
			try
			{
				this.nodes = (ArrayList)this.prevNodes.pop();
				keyCount--;
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
		int recordID 	= getRecordID(id) + TrieInstaller.OVERHEAD % this.chunkSize;
		
		Integer recordMapID = new Integer(recordID);
		byte[] record = (byte[])this.records.get(recordMapID); 
		
		if(record == null)
		{
			System.out.println("new record");
			record = store.getRecord(recordID);
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
			partCount = TrieUtils.byteToByte(record, partOffset);
			
			if(i == (partID - 1))
				return partOffset;
			else
				partOffset += (partCount * NODE_SIZE) + COUNT_SIZE;				
		}
		
		return 0;
	}
	
	private void readNodes(byte[] record, int partOffset, int keyCode, StringBuffer word)
	{
		char value 		= ' ';
		
		byte partCount = TrieUtils.byteToByte(record,partOffset);
		
		setLetters(keyCode);
		
		for(int i= (partOffset + COUNT_SIZE); i < (partOffset + COUNT_SIZE) + (partCount * NODE_SIZE); i = i + NODE_SIZE)
		{
			value = TrieUtils.byteToChar(record, i+V_OFFSET);
			
			for(int j=0;j<letters.length();j++)
			{
				if(value == letters.charAt(j))
				{
					TrieNode node = new TrieNode();
					
					node.appendToWord(word);
					node.appendToWord(value);
					
					if(TrieUtils.byteToByte(record, i+CC_OFFSET) != 0)
						node.setReference(TrieUtils.byteToChar(record, i+CR_OFFSET)); 
					else
						node.setReference((char)0);
					
					this.newNodes.add(node);
				}
			}
		}
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

	private void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public boolean isWordFound() {
		return wordFound;
	}

	private void setWordFound(boolean wordFound) {
		this.wordFound = wordFound;
	}
	
	public int getKeyCount()
	{
		return this.keyCount;
	}
}
