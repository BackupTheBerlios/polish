//#condition polish.android
package de.enough.polish.drone.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

class SocketConnectionImpl implements SocketConnection {
	
	private int mode = -1;
	
	private int dstPort;
	
	private String dstName;
	
	private Socket internalSocket;

    private SocketConnectionImpl() {}
    
    SocketConnectionImpl(String url, int mode) throws IOException {
    	this.mode = mode;
    	splitUrl(url);
//    	InetAddress address = InetAddress.getByName(dstName);
//    	internalSocket = new Socket(address, dstPort);
    	internalSocket = new Socket(dstName, dstPort);
    }
        
	private void splitUrl(String url) {
		if (url.indexOf(Connector.SOCKET_PREFIX)!=0 || 
				url.equals(Connector.SOCKET_PREFIX)) {
			throw new IllegalArgumentException("invalid URL");
		}
		//cut off prefix
		url = url.substring(Connector.SOCKET_PREFIX.length());
		//find ":"
		int splitIdx = url.indexOf(":");
		if (splitIdx == -1 || !(url.length()>splitIdx+2) ) {
			throw new IllegalArgumentException("Invalid URL format");
		}
		this.dstName = url.substring(0, splitIdx);
		String portStr = url.substring(splitIdx+1);
		this.dstPort = Integer.parseInt(portStr);		
	}

	public void close() throws IOException {
		internalSocket.close();
	}
	
	public InputStream openInputStream() throws IOException {
		if (mode == Connector.WRITE) {
			throw new IOException("connection is write only");
		} else {
			return internalSocket.getInputStream();
		}
	}
	
	public OutputStream openOutputStream() throws IOException {
		if (mode == Connector.READ) {
			throw new IOException("connection is read only");
		} else {
			return internalSocket.getOutputStream();			
		}
	}
	
	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(openInputStream());
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}
	
	public String getAddress() {
		return internalSocket.getInetAddress().toString();
	}
	
	public String getLocalAddress() {
		return internalSocket.getLocalAddress().toString();
	}
	
	public int getLocalPort() {
		return internalSocket.getLocalPort();
	}
	
	public int getPort() {
		return internalSocket.getPort();
	}
	
	public void setSocketOption(byte option, int value) throws IOException {
		switch (option) {
			case DELAY:
				if (value==0) {
					internalSocket.setTcpNoDelay(false);
				} else {
					internalSocket.setTcpNoDelay(true);						
				}
				break;
			case KEEPALIVE:
				if (value==0) {
					internalSocket.setKeepAlive(false);
				} else {
					internalSocket.setKeepAlive(true);						
				}
				break;
			case LINGER:
				if (value<=0) {
					internalSocket.setSoLinger(false, 0);
				} else {
					internalSocket.setSoLinger(true, value);						
				}
				break;
			case RCVBUF:
				internalSocket.setReceiveBufferSize(value);
				break;
			case SNDBUF:
				internalSocket.setSendBufferSize(value);
				break;
			default:
				throw new IllegalArgumentException("invalid socket option");
		}
	}
	
	public int getSocketOption(byte option) throws IOException {
		switch (option) {
			case DELAY:
				if (internalSocket.getTcpNoDelay()) {
					return 1;						
				}
				else {
					return 0;
				}
			case KEEPALIVE:
				if (internalSocket.getKeepAlive()) {
					return 1;
				} else {
					return 0;						
				}
			case LINGER:
				return internalSocket.getSoLinger();
			case RCVBUF:
					return internalSocket.getReceiveBufferSize();
			case SNDBUF:
				return internalSocket.getSendBufferSize();
			default:
				throw new IllegalArgumentException("invalid socket option");
		}
	}
}
