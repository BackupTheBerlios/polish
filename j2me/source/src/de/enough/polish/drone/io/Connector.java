//#condition polish.android
package de.enough.polish.drone.io;

import java.io.IOException;

public class Connector {
	
	public static final int READ = 1;
	
	public static final int READ_WRITE = 3;
	
	public static final int WRITE = 2;	
	
	private static final int TYPE_SOCKET = 1;
	
	private static final int TYPE_HTTP = 2;
	
	static final String SOCKET_PREFIX = "socket://";
	
	static final String HTTP_PREFIX = "http://";
	
	private Connector() {}
	
	public static Connection open(String name) throws IOException {
		return createConnection(name, READ_WRITE, false);
	}
	
	public static Connection open(String name, int mode) throws IOException {
		return createConnection(name, mode, false);
	}
	
	public static Connection open(String name, int mode, boolean timeouts) throws IOException {
		return createConnection(name, mode, timeouts);
	}
	
	private static Connection createConnection(String name, int mode, boolean timeouts) throws IOException {
		if (mode < READ || mode > READ_WRITE) {
			throw new IllegalArgumentException("invalid connection mode");
		}
		int type = detectConnectionType(name);
		
		switch (type) {
			case TYPE_SOCKET:
				return createSocketConnection(name, mode, timeouts);				
			case TYPE_HTTP:
				return createHttpConnection(name, mode, timeouts);
			default:
				throw new IllegalArgumentException("Unknown connection type");
		}
	}
	
	private static int detectConnectionType(String name) {
		if (name.indexOf(SOCKET_PREFIX)==0) {
			return TYPE_SOCKET;
		}
		else if (name.indexOf(HTTP_PREFIX)==0) {
			return TYPE_HTTP;
		} 
		else {
			return -1;
		}
	}
	
	private static SocketConnection createSocketConnection(String url, int mode,
			boolean timeouts) throws IOException {
		
		return new SocketConnectionImpl(url, mode);		
	}
	
	private static HttpConnection createHttpConnection(String url, int mode,
			boolean timeouts) throws IOException {
		
		return new HttpConnectionImpl(url, mode);		
	}
}