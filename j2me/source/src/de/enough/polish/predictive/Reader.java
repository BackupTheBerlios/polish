package de.enough.polish.predictive;

import java.util.Stack;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;

public class Reader {
	
	final int V_OFFSET = 0;
	final int CC_OFFSET = 2;
	final int CR_OFFSET = 3;
	final int NODE_SIZE = 5;
	final int COUNT_SIZE = 1;
	
	ArrayList nodes = null;
	Stack prevNodes = null;
	
	String code = "";
	
	HashMap stores = null;
	String prefix = "";
	
	int chunkSize = 0;
	int lineCount = 0;
	
	public Reader(String prefix, Properties prop) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException
	{
		this.nodes 		= new ArrayList();
		this.prevNodes 	= new Stack();
		
		this.code = "";
		
		this.stores = new HashMap();
		this.prefix = prefix;
		this.chunkSize = prop.getChunkSize();	
		this.lineCount = prop.getLineCount();
	}
		
	public String[] keyNum(char key) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException
	{	
		pushNodes();
		
		byte[] record = null;
		ArrayList newNodes = new ArrayList();
		
		this.code += key;
		
		if(nodes.size() == 0)
		{
			record = this.getRecord(1,this.lineCount);
			nodes = this.getNodes(record, key, "");
		}
		else
		{
			for(int nodeIndex = 0; nodeIndex < nodes.size(); nodeIndex++)
			{
				Node node = (Node)nodes.get(nodeIndex);
				
				if(node.getReference() != 0)
				{
					record = this.getRecord(node.getReference(),this.lineCount);
					addToNodes(this.getNodes(record, key, node.getWord()),newNodes);
				}
			}
			
			copyNodes(newNodes,nodes);
		}
		
		closeStores();
		return getNodeWords(nodes);
	}
	
	public String[] keyClear() throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException
	{	
		if(this.code.length() > 0)
		{
			popNodes();
			return getNodeWords(nodes);
		}
		else
			return null;
	}
	
	public void keySpace()
	{
		this.nodes.clear();
		this.prevNodes.removeAllElements();
		this.code = "";
	}
		
	public void addToNodes(ArrayList source, ArrayList dest)
	{
		for(int i=0; i<source.size(); i++)
			dest.add(source.get(i));
		/*{
			if(((TrieNode)source.get(i)).getReference() != 0)
				if(dest.size() > 0)
					dest.add(dest.size() - 1, source.get(i));
				else
					dest.add(source.get(i));
			else
				dest.add(0,source.get(i));
		}*/
	}
	
	public String[] getNodeWords(ArrayList nodes)
	{
		String[] words = new String[nodes.size()]; 
		for(int i=0; i<nodes.size(); i++)
			words[i] = ((Node)nodes.get(i)).getWord();
		
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
	
	private void popNodes()
	{
		ArrayList newNodes = (ArrayList)this.prevNodes.pop();
		
		if(newNodes != null)
			copyNodes(newNodes,this.nodes);
		else
			this.nodes.clear();
	}
		
	private byte[] getRecord(int id, int lineCount) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException
	{
		int recordID 	= ((id-1) / lineCount) + 1; 
		int partID 		= ((id-1) % lineCount) + 1;
		
		String storeID = this.prefix + ":" + (recordID - (recordID % this.chunkSize));
		
		RecordStore store = (RecordStore)stores.get(storeID);
		
		if(store == null)
		{
			store = RecordStore.openRecordStore(storeID, "Enough Software", "T9Installer");
			stores.put(storeID, store);
		}
		
		byte[] record = store.getRecord(recordID % this.chunkSize); 

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
					Node node = new Node();
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
	
	/*private int byteToInt(byte[] bytes, int offset)
	{
		return 	(0xff & bytes[offset + 3]) |
        		(0xff & bytes[offset + 2]) << 8 |
        		(0xff & bytes[offset + 1]) << 16 |
        		bytes[offset + 0] << 24;
	}*/
		
	private String getLetters(char code)
	{
		switch(code)
		{
			case '1': 	return " \'-";
			case '2': 	return "abcä";
			case '3': 	return "def";
			case '4': 	return "ghi";
			case '5': 	return "jkl";
			case '6': 	return "mnoö";
			case '7': 	return "pqrsß";
			case '8': 	return "tuvü";
			case '9': 	return "wxyz";
			default: 	return " \'-";
		}
	}
}
