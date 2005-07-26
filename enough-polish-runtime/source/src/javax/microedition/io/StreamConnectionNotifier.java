// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sun Feb 29 19:10:56 CET 2004
package javax.microedition.io;

import java.io.*;

/**
 * This interface defines the capabilities that a connection notifier
 * must have.
 * <HR>
 * 
 * 
 * @since CLDC 1.0
 */
public interface StreamConnectionNotifier extends Connection
{
	/**
	 * Returns a <code>StreamConnection</code> that represents
	 * a server side socket connection.
	 * 
	 * @return A socket to communicate with a client.
	 * @throws IOException - If an I/O error occurs.
	 */
	public StreamConnection acceptAndOpen() throws IOException;

}
