//#condition polish.android
package de.enough.polish.drone.io;

public interface ContentConnection extends StreamConnection {

	public String getEncoding(); 
    
	public long getLength(); 
    
	public String getType(); 
	
}
