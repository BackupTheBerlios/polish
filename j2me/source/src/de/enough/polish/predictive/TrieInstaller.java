//#condition polish.TextField.useDirectInput && !polish.blackberry && polish.usePolishGui 
package de.enough.polish.predictive;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class TrieInstaller {
	
	static final String PREFIX = "predictive";
	static final int MAGIC = 421;
	static final int VERSION = 100;
	
	static final byte OVERHEAD = 2;

	static final byte HEADER_RECORD = 1;
	static final byte CUSTOM_RECORD = 2;
	
	static final byte MAGIC_OFFSET = 0;
	static final byte VERSION_OFFSET = 4;
	static final byte CHUNKSIZE_OFFSET = 8;
	static final byte LINECOUNT_OFFSET = 12;
	
	byte charBuffer[];
	byte integerBuffer[];
	byte byteBuffer[];
	
	int magic 		= 0;
	int version 	= 0;
	int chunkSize 	= 0;
	int lineCount 	= 0;
	
	DataInputStream stream = null;
	
	public TrieInstaller() throws IOException, IllegalArgumentException
	{
		this.stream = new DataInputStream(getClass().getResourceAsStream("/predictive.trie"));
		
		this.magic = this.stream.readInt();
		this.version = this.stream.readInt();
		this.chunkSize = this.stream.readInt();
		this.lineCount = this.stream.readInt();
		
		if(this.magic != MAGIC)
			throw new IllegalArgumentException("The source file predictive.trie is not a dictionary");
		
		if(this.version < 100)
			throw new IllegalArgumentException("The dictionary is an deprecated version, must at least be " + VERSION);
		
		this.charBuffer = new byte[2];
		this.integerBuffer = new byte[4];
		this.byteBuffer = new byte[1];
	}
	
	public void createHeaderRecord(RecordStore store) throws IOException, RecordStoreException
	{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		
		byteStream.write(intToByte(this.magic));
		byteStream.write(intToByte(this.version));
		byteStream.write(intToByte(this.chunkSize));
		byteStream.write(intToByte(this.lineCount));
		
		byte[] bytes = byteStream.toByteArray();
		store.addRecord(bytes, 0, bytes.length);
	}
	
	public void createCustomRecord(RecordStore store) throws RecordStoreException
	{
		byte[] bytes = new byte[0];
		store.addRecord(bytes, 0, bytes.length);
	}
	
	public boolean createStores()
	{
		try
		{
			byte[] nodes;
			RecordStore store = null;
			
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
					
					store = RecordStore.openRecordStore(PREFIX + "_" + storeID, true, RecordStore.AUTHMODE_ANY, true);
					
					if(storeID == 0)
					{
						createHeaderRecord(store);
						createCustomRecord(store);
					}
				}
					
				nodes = this.getRecords(stream, this.lineCount);
				
				count++;
				
				store.addRecord(nodes, 0, nodes.length);
				
			}while(stream.available() > 0);
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
					if(storeList[i].startsWith(PREFIX))
					{
						RecordStore.deleteRecordStore(storeList[i]);
					}
			}
		}
		catch(RecordStoreException e)
		{
			//#debug
			e.printStackTrace();
		}
	}
	
	
	public byte[] getRecords(DataInputStream dataStream, int lineCount) throws EOFException, IOException
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
	
	private byte[] intToByte (final int value) throws IOException 
	{
		integerBuffer[0] = (byte) ((value >> 24) & 0x000000FF);
		integerBuffer[1] = (byte) ((value >> 16) & 0x000000FF);
		integerBuffer[2] = (byte) ((value >> 8) & 0x000000FF);
		integerBuffer[3] = (byte) (value & 0x00FF);
		return integerBuffer;
	} 
	
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
}
