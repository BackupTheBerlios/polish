//#condition polish.usePolishGui && polish.useRAG
package de.enough.polish.rag;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import de.enough.polish.io.Serializable;
import de.enough.polish.io.Serializer;
import de.enough.polish.util.ArrayList;

/**
 * <p>Provides static methods to retrieve <code>RagContainer</code> objects from a RAG file.</p>
 *
 * <p>Copyright (c) 2005, 2006, 2007 Enough Software</p>
 * <pre>
 * history
 *        10-Dec-2007 - asc creation
 * </pre>
 * @author Andre Schmidt, andre@enough.de
 */
public class RagStream {
	
	/**
	 * Returns the <code>RagContainer</code> object labeled by <code>id</code>
	 * @param id the label/id of the container in the stream
	 * @return the <code>RagContainer</code>
	 */
	public static RagContainer getContainer(DataInputStream stream, String id)
	{
		try
		{
			while(true)
			{
				String containerId = stream.readUTF();
				long containerSize = stream.readLong();
				
				if(containerId.equals(id))
				{
					RagContainer container = new RagContainer();
					container.setName(containerId);
					container.setSize(containerSize);
					
					byte[] data = readBytes(stream, (int)containerSize);
					
					container.setData(data);
					
					return container;
				}
				else
				{
					//Skip the stream to the next container
					stream.skip(containerSize);
				}
			}
		}
		catch (EOFException e) {}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Reads bytes from a stream
	 * @param stream the stream 
	 * @param size the number of bytes to be read
	 * @return
	 */
	private static byte[] readBytes(DataInputStream stream, int size)
	{
		byte[] data = new byte[(int)size];
		
		try {
			stream.read(data, 0, (int)size);
		} catch (IOException e) {
			System.out.println("unable to read bytes from rag file " + e);
		}
		
		return data;
	}
}	
