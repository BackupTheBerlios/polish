//#condition !polish.blackberry && polish.usePolishGui && polish.TextField.usePredictiveInput
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
	
	static final byte OVERHEAD = 3;

	static final int HEADER_RECORD = 1;
	static final int CUSTOM_RECORD = 2;
	static final int ORDER_RECORD = 3;
	
	static final byte MAGIC_OFFSET = 0;
	static final byte VERSION_OFFSET = 4;
	static final byte CHUNKSIZE_OFFSET = 8;
	static final byte LINECOUNT_OFFSET = 12;
	
	int magic 		= 0;
	int version 	= 0;
	int chunkSize 	= 0;
	int lineCount 	= 0;
	
	DataInputStream stream = null;
	
	boolean cancel = false;
	boolean pause = false;
	
	public TrieInstaller(DataInputStream stream) throws IOException, IllegalArgumentException
	{
		this.stream = stream;
		
		this.magic = this.stream.readInt();
		this.version = this.stream.readInt();
		this.chunkSize = this.stream.readInt();
		this.lineCount = this.stream.readInt();
		
		if(this.magic != MAGIC)
			throw new IllegalArgumentException("The source file predictive.trie is not a dictionary");
		
		if(this.version < 100)
			throw new IllegalArgumentException("The dictionary is an deprecated version, must at least be " + VERSION);
	}
	
	public void createHeaderRecord(RecordStore store) throws IOException, RecordStoreException
	{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		
		byteStream.write(TrieUtils.intToByte(this.magic));
		byteStream.write(TrieUtils.intToByte(this.version));
		byteStream.write(TrieUtils.intToByte(this.chunkSize));
		byteStream.write(TrieUtils.intToByte(this.lineCount));
		
		byte[] bytes = byteStream.toByteArray();
		store.addRecord(bytes, 0, bytes.length);
	}
	
	public void createCustomRecord(RecordStore store) throws RecordStoreException
	{
		byte[] bytes = new byte[0];
		store.addRecord(bytes, 0, bytes.length);
	}
	
	public void createOrderRecord(RecordStore store) throws RecordStoreException
	{
		byte[] bytes = new byte[0];
		store.addRecord(bytes, 0, bytes.length);
	}
	
	public byte[] getRecords(DataInputStream dataStream, int lineCount) throws EOFException, IOException
	{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		
		try
		{
			for(int i=0; i<lineCount; i++)
			{
				byte recordCount = dataStream.readByte();
				byteStream.write(TrieUtils.byteToByte(recordCount));
					
				for(int j=0;j<recordCount;j++)
				{
					char value = dataStream.readChar();
					byte childCount = dataStream.readByte();
					char childReference = dataStream.readChar();
					
					byteStream.write(TrieUtils.charToByte(value));
					byteStream.write(TrieUtils.byteToByte(childCount));
					byteStream.write(TrieUtils.charToByte(childReference));
				}
			}
		}catch(EOFException e){}
		
		return byteStream.toByteArray();
	}
	
	public int getChunkSize() {
		return chunkSize;
	}

	public int getLineCount() {
		return lineCount;
	}

	public int getMagic() {
		return magic;
	}

	public int getVersion() {
		return version;
	}

	public DataInputStream getStream() {
		return stream;
	}
}
