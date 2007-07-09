package de.enough.polish.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import de.enough.polish.util.TextUtil;

/**
 * Reads a property list (key and element pairs) from the input stream
 * and stores it in the internal <code>Hashtable</code>. 
 * The stream is assumed to be using the ISO 8859-1 character encoding; 
 * that is each byte is one Latin1 character.
 * 
 * <p>Copyright (c) 2007 Enough Software</p>
 * <pre>
 * history
 *        09-Jul-2007 - asc creation
 * </pre>
 * 
 * @author Andre Schmidt
 */
public class Properties extends Hashtable {
	/**
	 * Reads a line from a stream. It is expected that the line ends
	 * with \r and \n
	 * 
	 * @param stream the stream to read
	 * @return the read line
	 * @throws IOException
	 */
	private String readLine(InputStream stream) throws EOFException, IOException
	{
		byte character;
		String result = "";
		
		character = (byte)stream.read();
		while((char)character != '\r')
		{
			if(character == -1)
				throw new EOFException();
			
			result += (char)character;
			character = (byte)stream.read();
		}
		
		//Skip newline
		stream.skip(1);
		
		return result;
	}
	
	/**
	 * Returns the property value corresponding to the given key 
	 * @param key the key
	 * @return the value corresponding to the key
	 */
	public Object getProperty(String key)
	{
		return this.get(key);
	}
	
	/**
	 * Inserts a property with the given key and the corresponding value
	 * @param key the key to be placed into this property list.
     * @param value the value corresponding to key
	 */
	public void setProperty(String key, String value)
	{
		this.put(key, value);
	}
	
	/**
	 * Reads a property list (key and element pairs) from the input stream. 
	 * The stream is assumed to be using the ISO 8859-1 character encoding; 
	 * that is each byte is one Latin1 character.
	 * @param inStream the input stream.
	 * @throws IOException if an error occurred when reading from the input stream.
	 */
	public void load(InputStream inStream) throws IOException
	{
		try
		{
			String line = "";
			
			while(true)
			{
				line = readLine(inStream);
				
				if(line.length() == 0)
					throw new IOException("The properties stream is empty");
				
				if(line.charAt(0) != '#')
				{
					String[] values = TextUtil.split(line, '=');
					
					if(values.length == 0)
						throw new IOException("The properties stream is corrupt");
					
					try
					{
						Integer number = Integer.valueOf(values[1]);
						this.put(values[0], number);
					}
					catch(NumberFormatException ex)
					{
						this.put(values[0], values[1]);
					}
				}
			}
		}
		catch(EOFException e){}
		catch(IOException e){e.printStackTrace();}
	}
	
	/**
	 * Returns an enumeration of all the keys in this property list 
	 * @return an enumeration of all the keys in this property list
	 */
	public Enumeration propertyNames()
	{
		return this.elements();
	}
	
}
