//#condition polish.usePolishGui && polish.useRAG
package de.enough.polish.rag;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.HashMap;

/**
 * <p>
 * Provides static methods to retrieve <code>RagContainer</code> objects from
 * a RAG file.
 * </p>
 * 
 * <p>
 * Copyright (c) 2005, 2006, 2007 Enough Software
 * </p>
 * 
 * <pre>
 * history
 *        10-Dec-2007 - asc creation
 *        11-Dec-2007 - asc added index
 * </pre>
 * 
 * @author Andre Schmidt, andre@enough.de
 */
public class RagController {
	/**
	 * The map storing the indices for checking if a style is present in a resource assembly 
	 */
	public static HashMap INDEX;

	/**
	 * Returns true if <code>INDEX</code> contains an entry named <code>index</code> 
	 * @param index the name of the index
	 * @return true if an entry is present
	 */
	public static boolean hasIndex(String index)
	{
		if(INDEX == null)
		{
			INDEX = new HashMap();
		}
		return (INDEX.get(index) != null);
	}
	
	/**
	 * Stores the container names in an array and stores the array 
	 * to the index.
	 * @param stream the container stream
	 * @param index the name of the index
	 */
	public static void storeIndex(RagStream stream, String index)
	{
		try
		{
			HashMap entries = new HashMap();
			
			int indexLength = stream.readInt();
			
			for (int i = 0; i < indexLength; i++) {
				RagContainer container = new RagContainer();
				
				container.setName(stream.readUTF());
				container.setOffset(stream.readInt());
				container.setSize(stream.readInt());
				
				entries.put(container.getName(),container);
			}
			
			INDEX.put(index, entries);
		}
		catch (EOFException e) {}
		catch (IOException e) 
		{
			System.out.println("unable to create index for rag id " + index);
		}
	}
	
	/**
	 * Returns a container with the id <code>container</code> if it exists in the Index <code>index</code>
	 * @param index the index id
	 * @param container the id of the container
	 * @return the conatiner
	 */
	public static RagContainer getContainer(String index,String container)
	{
		HashMap entries = (HashMap)INDEX.get(index);
		
		if(entries == null)
		{
			System.out.println(index + " is not an index");
			return null;
		}
		
		return (RagContainer)entries.get(container);
	}
	
	/**
	 * Returns a byte array from the stream using the offset and size in <code>container</code>
	 * @param stream the stream to read the array
	 * @param container the container containing the offset and size
	 * @return the byte array
	 * @throws IOException
	 */
	public static byte[] getData(RagStream stream, RagContainer container) throws IOException
	{
		byte[] data = new byte[container.getSize()];
		
		try
		{
			stream.reset();
			stream.skipBytes(container.getOffset());
			stream.readFully(data, 0, container.getSize());
		}
		catch(Exception e)
		{
			//#debug error
			System.out.println("Unable to load " + container.getName() + e );
		}
		
		return data;
	}
}
