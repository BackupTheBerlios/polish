// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sun Feb 29 19:10:55 CET 2004
package javax.microedition.io;

import java.io.*;

/**
 * This interface defines the capabilities that an input stream
 * connection must have.
 * <HR>
 * 
 * 
 * @since CLDC 1.0
 */
public interface InputConnection extends Connection
{
	/**
	 * Open and return an input stream for a connection.
	 * 
	 * @return An input stream
	 * @throws IOException - If an I/O error occurs
	 */
	public InputStream openInputStream() throws IOException;

	/**
	 * Open and return a data input stream for a connection.
	 * 
	 * @return An input stream
	 * @throws IOException - If an I/O error occurs
	 */
	public DataInputStream openDataInputStream() throws IOException;

}
