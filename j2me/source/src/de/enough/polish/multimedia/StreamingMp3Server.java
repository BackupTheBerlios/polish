//#condition polish.android
package de.enough.polish.multimedia;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import de.enough.polish.util.TextUtil;

public class StreamingMp3Server implements Runnable {

	public static final int PORT_STREAMING_MP3 = 6754;
	private static final String CRLF = "\r\n";
	public static final String RESPONSE_HEADER_HEAD; 
	public static final String RESPONSE_HEADER_GET;
	static {
		RESPONSE_HEADER_HEAD =
			"HTTP/1.1 200 OK"+CRLF+
			"Date: Wed, 08 Jul 2009 12:17:24 GMT"+CRLF+
			"Last-Modified: Wed, 08 Jul 2009 12:10:22 GMT"+CRLF+
			"Accept-Ranges: bytes"+CRLF+
			"Connection: Keep-Alive"+CRLF+
			"Content-Type: audio/mpeg"+CRLF+
			CRLF;
		RESPONSE_HEADER_GET =
			"HTTP/1.1 200 OK"+CRLF+
			"Date: Wed, 08 Jul 2009 12:17:24 GMT"+CRLF+
			"Last-Modified: Wed, 08 Jul 2009 12:10:22 GMT"+CRLF+
			"Accept-Ranges: bytes"+CRLF+
			"Connection: close"+CRLF+
			"Content-Type: audio/mpeg"+CRLF+
			CRLF;
	}
	
	public void start() throws IOException {
		Thread thread = new Thread(this);
		thread.start();
	}

	public void run() {
		try {
			Selector selector;
			selector = Selector.open();
			
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			int port = PORT_STREAMING_MP3;
			while(true) {
				try {
					InetSocketAddress endpointSocketAddress = new InetSocketAddress(port);
					serverChannel.socket().bind(endpointSocketAddress);
					break;
				} catch (IOException e) {
					port++;
				}
			}
	
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	
			while (true) {
				selector.select();
				Iterator it = selector.selectedKeys().iterator();
	
				while (it.hasNext()) {
					SelectionKey selectedKey = (SelectionKey) it.next();
					it.remove();
	
					if (selectedKey.isAcceptable()) {
						SocketChannel channel = serverChannel.accept();
						if (channel == null) {
							// This case may happen as the key is not synchronized. Its possible that the channel of the key was closed
							// since it was selected.
							continue;
						}
						handleChannel(channel);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			//TODO: Handle gracefully.
		}
	}
	
	private void handleChannel(SocketChannel channel) throws IOException {
		Socket socket = channel.socket();
		InputStream inputStream = socket.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"US-ASCII");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader,1024*8);
		String httpRequest = bufferedReader.readLine();
		//#debug
		System.out.println("header:"+httpRequest+"XXX");
		String[] parts = TextUtil.split(httpRequest, ' ');
		if("HEAD".equals(parts[0])) {
			handleHead(channel, parts);
		}
		else if("GET".equals(parts[0])) {
			handleGet(channel, parts);
		}
		else {
			//#debug
			System.err.println("Unknown request:"+parts[0]);
		}
		//#debug
		System.out.println("Closing socket stream.");
		//TODO: Put this call in a finally block.
		channel.close();
	}

	private void handleHead(SocketChannel channel, String[] parts) throws IOException {
		OutputStream outputStream = channel.socket().getOutputStream();
		outputStream.write(RESPONSE_HEADER_HEAD.getBytes("US-ASCII"));
		//#debug
		System.out.println("Writing header for HEAD response to socket stream.");
		outputStream.flush();
	}

	private void handleGet(SocketChannel channel, String[] parts) throws IOException {
		String urlString = parts[1];
		String[] filenames = extractFilenamesFromUrl(urlString);
		byte[] dataBuffer = new byte[1024 * 8];
		OutputStream outputStream = channel.socket().getOutputStream();
		try {
			outputStream.write(RESPONSE_HEADER_GET.getBytes("US-ASCII"));
			outputStream.flush();
			//#debug
			System.out.println("Writing header with size '"+RESPONSE_HEADER_GET.length()+"' to socket stream.");
			for (int i = 0; i < filenames.length; i++) {
				String filename = filenames[i];
				FileInputStream fileInputStream = new FileInputStream(filename);
				int numberOfBytesRead = 1;
				
				// Write a file to the stream.
				while(numberOfBytesRead != -1) {
					numberOfBytesRead = fileInputStream.read(dataBuffer);
					//#debug
					System.out.println("Read '"+numberOfBytesRead+"' bytes from file.");
					if(numberOfBytesRead <= 0) {
						break;
					}
					//#debug
					System.out.println("About to write data to socket stream.");
					outputStream.write(dataBuffer,0,numberOfBytesRead);
					outputStream.flush();
					//#debug
					System.out.println("Bytes written to socket stream.");
				}
				fileInputStream.close();
			}
		} finally {
			outputStream.close();
		}
		
	}

	private String[] extractFilenamesFromUrl(String urlString) {
		URI uri = URI.create(urlString);
		String query = uri.getQuery();
		String[] fields = TextUtil.split(query, '&');
		String[] filenames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			filenames[i] = field.substring(field.indexOf('=')+1);
			//#debug
			System.out.println("File in url found:"+filenames[i]);
		}
		return filenames;
	}
}
