//#condition !polish.api.wmapi && polish.useWMAPIWrapper && polish.supportsWMAPIWrapper
package de.enough.polish.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;

import de.enough.polish.messaging.MessageConnection;

/**
 * This class is factory for creating new Connection objects.
 * 
 * This class is factory for creating new <tt>Connection</tt> objects.
 * <p>
 * The creation of connections is performed dynamically by looking
 * up a protocol implementation class whose name is formed from the
 * platform name (read from a system property) and the protocol name
 * of the requested connection (extracted from the parameter string
 * supplied by the application programmer).
 * 
 * The parameter string that describes the target should conform
 * to the URL format as described in RFC 2396.
 * This takes the general form:
 * <p>
 * <code>{scheme}:[{target}][{parms}]</code>
 * <p>
 * where:
 * <ul>
 * <li><code>scheme</code> is the name of a protocol such as
 * <em>HTTP</em>.</li>
 * 
 * <li><code>target</code> is normally some kind of network
 * address.</li>
 * 
 * <li><code>parms</code> are formed as a series of equates
 * of the form <tt>;x=y</tt>. For example: <tt>;type=a</tt>.</li>
 * </ul>
 * <p>
 * An optional second parameter may be specified to the open
 * function. This is a mode flag that indicates to the protocol
 * handler the intentions of the calling code. The options here
 * specify if the connection is going to be read (<tt>READ</tt>), written
 * (<tt>WRITE</tt>), or both (<tt>READ_WRITE</tt>). The validity of these flag
 * settings is protocol dependent. For example, a connection
 * for a printer would not allow read access, and would throw
 * an <tt>IllegalArgumentException</tt>. If the mode parameter is not
 * specified, <tt>READ_WRITE</tt> is used by default.
 * <p>
 * An optional third parameter is a boolean flag that indicates
 * if the calling code can handle timeout exceptions. If this
 * flag is set, the protocol implementation may throw an
 * <tt>InterruptedIOException</tt> when it detects a timeout condition.
 * This flag is only a hint to the protocol handler, and it
 * does not guarantee that such exceptions will actually be thrown.
 * If this parameter is not set, no timeout exceptions will be
 * thrown.
 * <p>
 * Because connections are frequently opened just to gain access
 * to a specific input or output stream, convenience
 * functions are provided for this purpose.
 * 
 * See also: <A HREF="../../../javax/microedition/io/DatagramConnection.html"><CODE>DatagramConnection</CODE></A>
 * for information relating to datagram addressing
 * <HR>
 * 
 * 
 * @since CLDC 1.0
 */
public class Connector
{
	/**
	 * Access mode <tt>READ</tt>.
	 */
	public static final int READ = 0;

	/**
	 * Access mode <tt>WRITE</tt>.
	 */
	public static final int WRITE = 1;

	/**
	 * Access mode <tt>READ_WRITE</tt>.
	 */
	public static final int READ_WRITE = 2;

	/**
	 * Creates and opens a Connection.
	 * 
	 * @param name - the URL for the connection
	 * @return a new Connection object
	 * @throws java.lang.IllegalArgumentException - if a parameter is invalid
	 * @throws ConnectionNotFoundException - if the requested connection cannot be made, or the protocol type does not exist
	 * @throws java.io.IOException - if some other kind of I/O error occurs
	 * @throws java.lang.SecurityException - if a requested protocol handler is not permitted
	 */
	public static Connection open(String name) throws java.io.IOException
	{
		return open( name, READ, false );
	}

	/**
	 * Creates and opens a Connection.
	 * 
	 * @param name - the URL for the connection
	 * @param mode - the access mode
	 * @return a new Connection object
	 * @throws java.lang.IllegalArgumentException - if a parameter is invalid
	 * @throws ConnectionNotFoundException - if the requested connection cannot be made, or the protocol type does not exist
	 * @throws java.io.IOException - if some other kind of I/O error occurs
	 * @throws java.lang.SecurityException - if a requested protocol handler is not permitted
	 */
	public static Connection open(String name, int mode) throws java.io.IOException
	{
		return open( name, mode, false );
	}

	/**
	 * Creates and opens a Connection.
	 * 
	 * @param name - the URL for the connection
	 * @param mode - the access mode
	 * @param timeouts - a flag to indicate that the caller wants timeout exceptions
	 * @return a new Connection object
	 * @throws java.lang.IllegalArgumentException - if a parameter is invalid
	 * @throws ConnectionNotFoundException - if the requested connection cannot be made, or the protocol type does not exist
	 * @throws java.io.IOException - if some other kind of I/O error occurs
	 * @throws java.lang.SecurityException - if a requested protocol handler is not permitted
	 */
	public static Connection open(String name, int mode, boolean timeouts) 
	throws IOException
	{
		if (name.startsWith("sms://")) {
			return new MessageConnection( name, mode, timeouts );
		} else {
			return javax.microedition.io.Connector.open(name);
		}
	}

	/**
	 * Creates and opens a connection input stream.
	 * 
	 * @param name - the URL for the connection
	 * @return a DataInputStream
	 * @throws java.lang.IllegalArgumentException - if a parameter is invalid
	 * @throws ConnectionNotFoundException - if the connection cannot be found
	 * @throws java.io.IOException - if some other kind of I/O error occurs
	 * @throws java.lang.SecurityException - if access to the requested stream is not permitted
	 */
	public static DataInputStream openDataInputStream(String name) 
	throws IOException
	{
		return javax.microedition.io.Connector.openDataInputStream(name);
	}

	/**
	 * Creates and opens a connection output stream.
	 * 
	 * @param name - the URL for the connection
	 * @return a DataOutputStream
	 * @throws java.lang.IllegalArgumentException - if a parameter is invalid
	 * @throws ConnectionNotFoundException - if the connection cannot be found
	 * @throws java.io.IOException - if some other kind of I/O error occurs
	 * @throws java.lang.SecurityException - if access to the requested stream is not permitted
	 */
	public static DataOutputStream openDataOutputStream(String name) 
	throws IOException
	{
		return javax.microedition.io.Connector.openDataOutputStream(name);
	}

	/**
	 * Creates and opens a connection input stream.
	 * 
	 * @param name - the URL for the connection
	 * @return an InputStream
	 * @throws java.lang.IllegalArgumentException - if a parameter is invalid
	 * @throws ConnectionNotFoundException - if the connection cannot be found
	 * @throws java.io.IOException - if some other kind of I/O error occurs
	 * @throws java.lang.SecurityException - if access to the requested stream is not permitted
	 */
	public static InputStream openInputStream(String name) 
	throws IOException
	{
		return javax.microedition.io.Connector.openInputStream(name);
	}

	/**
	 * Creates and opens a connection output stream.
	 * 
	 * @param name - the URL for the connection
	 * @return an OutputStream
	 * @throws java.lang.IllegalArgumentException - if a parameter is invalid
	 * @throws ConnectionNotFoundException - if the connection cannot be found
	 * @throws java.io.IOException - if some other kind of I/O error occurs
	 * @throws java.lang.SecurityException - if access to the requested stream is not permitted
	 */
	public static OutputStream openOutputStream(String name) 
	throws IOException
	{
		return javax.microedition.io.Connector.openOutputStream(name);
	}

}
