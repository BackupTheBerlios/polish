package de.enough.polish.rag;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * <p>
 * Provides a generic way to access a DataInputStream for RAG.
 * Introduced for Samsung devices which don't support mark() and reset()
 * in a DataInputStream
 * </p>
 * 
 * <p>
 * Copyright (c) 2008 Enough Software
 * </p>
 * 
 * <pre>
 * history
 *        23-Jan-2008 - asc creation
 * </pre>
 * 
 * @author Andre Schmidt, andre@enough.de
 */
public class RagStream {
	DataInputStream stream;
	Object midlet;
	String file;
	boolean markSupported;
	
	/**
	 * Constructor for RagStream
	 * @param midlet the midlet object (can as well be any object, used for getClass().getResourceAsStream())
	 * @param file the full path of the file to stream
	 */
	public RagStream(Object midlet, String file)
	{
		this.midlet = midlet;
		this.file = file;
		
		this.stream = new DataInputStream(this.midlet.getClass().getResourceAsStream(this.file));
		
		this.markSupported = stream.markSupported();
		
		if(this.markSupported)
		{
			try
			{
				stream.mark(stream.available());
			}
			catch(IOException e)
			{
				//#debug
				System.out.println("unable to mark stream " + e);
			}
		}
	}
	
	/**
	 * Reads an int using DataInputStream.readInt()
	 * @return the read int
	 * @throws IOException
	 */
	public int readInt() throws IOException
	{
		return this.stream.readInt();
	}
	
	/**
	 * Reads a String using DataInputStream.readUTF()
	 * @return the read String
	 * @throws IOException
	 */
	public String readUTF() throws IOException
	{
		return this.stream.readUTF();
	}
	
	/** 
	 * Resets the stream to the start. If the stream doesn't support mark() and reset(),
	 * the stream is reopened.
	 * @throws IOException
	 */
	public void reset() throws IOException
	{
		if(this.markSupported)
		{
			this.stream.reset();
		}
		else
		{
			this.stream = new DataInputStream(this.midlet.getClass().getResourceAsStream(this.file));
		}
	}
	
	/**
	 * Skips the given number of bytes using DataInputStream.skipbytes()
	 * @param bytes the number of bytes
	 * @throws IOException
	 */
	public void skipBytes(int bytes) throws IOException
	{
		this.stream.skipBytes(bytes);
	}
	
	/**
	 * Reads bytes in to a <code>byte</code> array using DataInputStream.readFully()
	 * @param data the <code>byte</code> array to fill
	 * @param offset the offset inside the <code>byte</code> array
	 * @param length the number of bytes to read from the stream
	 * @throws IOException
	 */
	public void readFully(byte[] data, int offset, int length) throws IOException
	{
		this.stream.readFully(data, offset, length);
	}
}
