//#condition polish.android
package de.enough.polish.drone.io;

import java.io.IOException;

public class ConnectionNotFoundException extends IOException {
	
	public ConnectionNotFoundException() {
		super();
	}
	
	public ConnectionNotFoundException(String s) {
		super(s);
	}
}
