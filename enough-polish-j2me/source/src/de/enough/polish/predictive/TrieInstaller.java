package de.enough.polish.predictive;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import javax.microedition.lcdui.StringItem;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;

import de.enough.polish.io.Serializer;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.Properties;

public class TrieInstaller {
	
	byte charBuffer[];
	byte integerBuffer[];
	byte byteBuffer[];
	
	Properties properties = null;
	
	String prefix = "";
	int lineCount = 0;
	int chunkSize = 0;
	
	int totalBytes = 0;
	
	DataInputStream trieStream = null;
	DataInputStream headerStream = null;
	
	public TrieInstaller()
	{
		this.headerStream = new DataInputStream(getClass().getResourceAsStream("/predictive.header"));
		
		try
		{
			this.properties = new Properties();
			this.properties.load(this.headerStream);
			
			this.prefix 	= (String)this.properties.getProperty("trie.prefix");
			this.lineCount 	= ((Integer)this.properties.getProperty("trie.lineCount")).intValue();
			this.chunkSize 	= ((Integer)this.properties.getProperty("trie.lineCount")).intValue();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		this.trieStream = new DataInputStream(getClass().getResourceAsStream("/predictive.trie"));
		
		try
		{
			this.totalBytes = trieStream.available();
		}catch(IOException e){
			//#debug
			e.printStackTrace();
		}
		
		this.charBuffer = new byte[2];
		this.integerBuffer = new byte[4];
		this.byteBuffer = new byte[1];
	}
	
	public boolean createHeaderStore(StringItem status)
	{
		try
		{
			RecordStore store = null;
			byte[]header = null;
			
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		    DataOutputStream dataStream = new DataOutputStream(byteStream); 
			
			Serializer.serialize(this.properties, dataStream);
			
			header = byteStream.toByteArray();
			
			store = RecordStore.openRecordStore(this.prefix + ":header", true, RecordStore.AUTHMODE_ANY, true);
			
			store.addRecord(header, 0, header.length);
			
			store.closeRecordStore();
			
			return true;
		}
		catch(Exception e)
		{
			//#debug
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean createStores(StringItem status)
	{
		try
		{
			byte[] nodes;
			RecordStore store = null;
			
			int currentBytes = 0;
			int totalBytes = trieStream.available();
			
			int count = 0;
			int storeID = 0;
			
			do
			{
				nodes = null;
				
				if((count % this.chunkSize) == 0)
				{
					if(store != null)
					{
						store.closeRecordStore();
						storeID += this.chunkSize;
					}
					
					store = RecordStore.openRecordStore(this.prefix + ":" + storeID, true, RecordStore.AUTHMODE_ANY, true);
				}
					
				nodes = this.getRecord(trieStream, this.lineCount);
				
				count++;
				
				store.addRecord(nodes, 0, nodes.length);
				
				currentBytes += nodes.length;
				
				status.setText(currentBytes + " von " + totalBytes + " Bytes geschrieben");				
			}while(trieStream.available() > 0);
		}	
		catch(IOException e)
		{
			//#debug
			e.printStackTrace();
			return false;
		}
		catch(Exception e)
		{
			//#debug
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void deleteAllStores()
	{
		try
		{
			String[] storeList = RecordStore.listRecordStores();
			
			if(storeList != null)
			{
				for(int i=0; i<storeList.length; i++)
					if(storeList[i].startsWith(this.prefix))
					{
						RecordStore.deleteRecordStore(storeList[i]);
					}
			}
		}catch(RecordStoreNotFoundException e)
		{
			//#debug
			e.printStackTrace();
		}
		catch(RecordStoreException e)
		{
			//#debug
			e.printStackTrace();
		}
	}
	
	
	public byte[] getRecord(DataInputStream dataStream, int lineCount) throws EOFException, IOException
	{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		
		try
		{
			for(int i=0; i<lineCount; i++)
			{
				byte recordCount = dataStream.readByte();
				byteStream.write(byteToByte(recordCount));
					
				for(int j=0;j<recordCount;j++)
				{
					char value = dataStream.readChar();
					byte childCount = dataStream.readByte();
					char childReference = dataStream.readChar();
					
					byteStream.write(charToByte(value));
					byteStream.write(byteToByte(childCount));
					byteStream.write(charToByte(childReference));
				}
			}
		}catch(EOFException e){}
		
		return byteStream.toByteArray();
	}
	
	/*private byte[] intToByte (final int value) throws IOException 
	{
		integerBuffer[0] = (byte) ((value >> 24) & 0x000000FF);
		integerBuffer[1] = (byte) ((value >> 16) & 0x000000FF);
		integerBuffer[2] = (byte) ((value >> 8) & 0x000000FF);
		integerBuffer[3] = (byte) (value & 0x00FF);
		return integerBuffer;
	} */  
	
	private byte[] byteToByte (final byte value) throws IOException 
	{
		byteBuffer[0] = value;
		return byteBuffer;
	}   
	
	private byte[] charToByte (final char value) throws IOException {
		charBuffer[0] = (byte) ((value >> 8) & 0x000000FF);
		charBuffer[1] = (byte) (value & 0x00FF);
		return charBuffer;
	}
	
	public int getTotal()
	{
		return this.totalBytes;
	}
}
