package de.enough.polish.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Provides some useful Stream methods.</p>
 *
 * <p>Copyright Enough Software 2004 - 2008</p>

 * <pre>
 * history
 *        10-Jul-2008 - asc creation
 * </pre>
 * @author Andre Schmidt, andre@enough.de
 */

public class StreamUtil {
	/**
	 * the default length for the buffer
	 */
	public static final int DEFAULT_BUFFER = 1024;
	
	/**
	 * Returns the lines of a InputStream as an ArrayList of Strings
	 * @param stream the stream to read
	 * @param encoding the encoding to use
	 * @return the lines as an ArrayList
	 */
	public static String[] getLines(InputStream stream, String encoding, int bufferLength) throws IOException
	{
		String allLines = getString(stream,encoding,bufferLength);
		
		String[] lines = TextUtil.split(allLines, '\n');
		
		return lines;
	}
	
	/**
	 * Returns the content of a stream as a String
	 * @param stream the stream to read
	 * @param encoding the encoding to use
	 * @return the resulting String
	 * @throws IOException
	 */	
	public static String getString(InputStream stream, String encoding, int bufferLength) throws IOException
	{
		byte[] buffer = new byte[ bufferLength ];
		int read;
		
		StringBuffer result = new StringBuffer();
		
		while((read = stream.read(buffer, 0, bufferLength))  != -1)
		{
			if (encoding != null) {
				result.append(new String( buffer, 0, read, encoding ));
			} else {
				result.append(new String( buffer, 0, read ));				
			}
		}
		
		return result.toString();
	}
}
