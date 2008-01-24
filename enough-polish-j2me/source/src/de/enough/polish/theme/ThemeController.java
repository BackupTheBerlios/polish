//#condition polish.usePolishGui && polish.useThemes
package de.enough.polish.theme;

import java.io.IOException;

/**
 * <p>
 * Provides static methods to retrieve <code>ThemeContainer</code> objects from
 * a theme file.
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
public class ThemeController {
	/**
	 * Returns a container header with the id <code>container</code> from the given stream
	 * @param stream the stream
	 * @param id the id of the container
	 * @return the container
	 */
	public static ThemeContainer getContainerHeader(ThemeStream stream,String id)
	{
		return (ThemeContainer)stream.getIndex().get(id);
	}
	
	/**
	 * Returns a byte array from the stream using the offset and size in <code>container</code>
	 * @param stream the stream to read the array
	 * @param container the container containing the offset and size
	 * @return the byte array
	 * @throws IOException
	 */
	public static byte[] getContainerData(ThemeStream stream, ThemeContainer container) throws IOException
	{
		byte[] data = null;
		
		try
		{
			data = stream.getBytes(container.getOffset(), container.getSize());
		}
		catch(Exception e)
		{
			//#debug error
			System.out.println("Unable to load " + container.getName() + e );
		}
		
		return data;
	}
}
