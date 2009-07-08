//#condition polish.android
package de.enough.polish.multimedia;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

import de.enough.polish.util.TextUtil;

public class StreamingMp3Server implements Runnable {

	public static final int PORT_STREAMING_MP3 = 6754;
	public static final String RESPONSE_HEADER = "HTTP/1.1 200 OK\r\nContent-Type: audio/mp3\r\n\r\n";
	
	private ByteBuffer responseHeader;
	private CharsetDecoder decoder;
	
	public StreamingMp3Server() {
		super();
		byte[] bytes;
		try {
			bytes = RESPONSE_HEADER.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// This is unlikely to happen.
			return;
		}
		this.responseHeader = ByteBuffer.wrap(bytes);
		Charset charset = Charset.forName("US-ASCII");
		this.decoder = charset.newDecoder();
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
	//					serverChannel = (ServerSocketChannel) selKey.channel();
						SocketChannel channel = serverChannel.accept();
						if (channel == null) {
							// This case may happen as the key is not synchronized. Its possible that the channel of the key was closed
							// since it was selected.
							continue;
						}
						channel.configureBlocking(false);
				        channel.register (selector, SelectionKey.OP_READ);
					}
					
					if(selectedKey.isReadable()) {
						SocketChannel channel = (SocketChannel)selectedKey.channel();
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
//		Socket socket = channel.socket();
//		InputStream inputStream = socket.getInputStream();
//		InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"US-ASCII");
//		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//		String header = bufferedReader.readLine();
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024*8);
		StringBuffer stringBuffer = new StringBuffer();
		while(channel.read(byteBuffer) > 0) {
//			CharBuffer charBuffer = this.decoder.decode(byteBuffer);
			byte[] array = byteBuffer.array();
			stringBuffer.append(new String(array));
		}
		String header = stringBuffer.toString();
		//#debug
		System.out.println("header:"+header+"XXX");
		int indexOf = header.indexOf("\r");
		if(indexOf != -1) {
			header = header.substring(0,indexOf);
		} else {
			header = header.substring(0,header.indexOf("\n"));
		}
		String[] parts = TextUtil.split(header, ' ');
		if("HEAD".equals(parts[0])) {
			channel.write(this.responseHeader);
		}
		else if("GET".equals(parts[0])) {
			String urlString = parts[1];
			URI uri = URI.create(urlString);
//			String path = uri.getPath();
//			parts = TextUtil.split(path, ' ');
			String query = uri.getQuery();
			String[] fields = TextUtil.split(query, '&');
			String[] filenames = new String[fields.length];
			for (int i = 0; i < fields.length; i++) {
				String field = fields[i];
				filenames[i] = field.substring(field.indexOf('=')+1);
			}
			
			channel.write(this.responseHeader);
			
			for (int i = 0; i < filenames.length; i++) {
				String filename = filenames[i];
				FileInputStream fileInputStream = new FileInputStream(filename);
				FileChannel fileChannel = fileInputStream.getChannel();
				long position = 0;
				long length;
				long count = fileChannel.size();
				do {
					length = fileChannel.transferTo(0, count, channel);
					position = position + length;
					count = count - length;
				}
				while(length > 0);
				fileChannel.close();
//				break;
			}
		}
		else {
			//#debug
			System.err.println("Unknown request:"+parts[0]);
		}
		channel.close();
		
	}
}
