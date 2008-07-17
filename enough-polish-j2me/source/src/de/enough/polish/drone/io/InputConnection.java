//#condition polish.android
package de.enough.polish.drone.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public interface InputConnection extends Connection {
	
	public InputStream openInputStream() throws IOException;
	
	public DataInputStream openDataInputStream() throws IOException;

}
