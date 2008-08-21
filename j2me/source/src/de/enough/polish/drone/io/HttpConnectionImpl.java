//#condition polish.android
package de.enough.polish.drone.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

class HttpConnectionImpl implements HttpConnection {
	
	private static final int STATE_SETUP = 0;
	
	private static final int STATE_CONNECTED = 1;
	
	private int state = STATE_SETUP;
		
	private int mode = Connector.READ_WRITE;
	
	private int port = 80;
	private String url = null;
	private String methodStr = GET;
	private String host = null;
	private String query = null;
	private String file = null;
	
	private HttpClient client;
	private HttpMethodBase method;
		
	protected HttpConnectionImpl(String url) {
		this.url = url;
		splitUrl(url);
		this.client = new HttpClient(); 
	}
	
	HttpConnectionImpl(String url, int mode) {
		this.url = url;
		this.mode = mode;
		splitUrl(url);
		this.client = new HttpClient();
	}

	
	private void splitUrl(String url) {
		if (url.indexOf(Connector.HTTP_PREFIX)!=0 || 
				!(url.length()>Connector.HTTP_PREFIX.length()) ) {
			throw new IllegalArgumentException("invalid URL");
		}
		
		//split of leading http
		url = url.substring(Connector.HTTP_PREFIX.length());
		
		//check for query string at the end
		int queryIdx = url.indexOf("?");
		if (queryIdx != -1) {
			if (url.length()>queryIdx+2) {
				this.query = url.substring(queryIdx+1);
			}
			url = url.substring(0, queryIdx-1);
		}		
		
		// capture URL path section of URL
		int urlPathIdx = url.indexOf("/");
		if (urlPathIdx != -1) {
			if (url.length()>urlPathIdx+2) {
				this.file = url.substring(urlPathIdx+1);
			}
			url = url.substring(0, urlPathIdx-1);
		}
		
		// capture port
		int portIdx = url.indexOf(":");
		if (portIdx != -1) {
			if (url.length()>portIdx+2) {
				this.port = Integer.parseInt(url.substring(portIdx+1));
			}
			url = url.substring(0, portIdx-1);
		}
		
		// remaining string should be host
		this.host = url;		
	}
	
//			httpclient = new HttpClient(); 
//			PostMethod httpPost = new PostMethod("http://192.168.1.6/index.php"); 
//			httpPost.setRequestBody(data); 
//			httpPost.setQueryString(data); 
//			httpclient.executeMethod(httpPost); 
//			String serverresponse1 = httpPost.getResponseBodyAsString(); 
//			String serverresponse = httpPost.getQueryString();//sim no reciving 
//			//bundles.putString("serverres", serverresponse); 
//			Toast.makeText(context,serverresponse+" " + "Response from server is : "+ serverresponse1,Toast.LENGTH_LONG).show(); 


	public void close() throws IOException {
		//method.abort();
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}

	public OutputStream openOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//only in setup state
	public void setRequestMethod(String method) throws IOException {
		if (this.state == STATE_SETUP) {
			if (method.equals(GET)) {
				this.methodStr = method;
				this.method = new GetMethod(this.url);
			} else if (method.equals(POST)) {
				this.methodStr = method;
				this.method = new PostMethod(this.url);
			} else {
				throw new IllegalArgumentException("illegal request method");
			}
		} else {
			throw new IOException("already connected");
		}
		
	}
	
	public void setRequestProperty(String key, String value) throws IOException {
		if (this.state == STATE_SETUP) {
			// set param here
		} else {
			throw new IOException("already connected");
		}
	}
	
	//invoke at any time
	public String getRequestMethod() {
		return this.methodStr;
	}
	
	public String getRequestProperty(String key) {
		return null;
	}
	
	public String getURL() {
		return this.url;
	}
	
	public String getQuery() {
		return this.query;
	}
	
	public int getPort() {
		return -1;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public String getProtocol() {
		return null;
	}
		
	public String getFile() {
		return this.file;
	}
		
	public String getRef() {
		return null;
	}
	
	//these calls force transition to connected state
	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(openInputStream());
	}

	public InputStream openInputStream() throws IOException {
		execute();
		return null;
	}
	
	public long getLength() {
		try {
			execute();
		} catch (IOException ex) {}
		return 0;
	}
	
	public String getType() {
		try {
			execute();
		} catch (IOException ex) {}
		return null;
	}
	
	public String getEncoding() {
		try {
			execute();
		} catch (IOException ex) {}
		return null;
	}
	
	public String getHeaderField(String name) throws IOException {
		execute();
		return null;
	}
	
	public String getHeaderField(int n) throws IOException {
		execute();
		return null;
	}
	
	public int getResponseCode()  throws IOException {
		execute();
		return -1;
	}
	
	public String getResponseMessage() throws IOException {
		execute();
		return null;
	}
	
	public int getHeaderFieldInt(String name, int def) throws IOException {
		execute();
		return -1;
	}

	public long getHeaderFieldDate(String name,	long def) throws IOException {
		execute();
		return -1;
	}

	public String getHeaderFieldKey(int n) throws IOException {
		execute();
		return null;
	}
	
	public long getDate() throws IOException {
		execute();
		return -1;
	}
	
	public long getExpiration() throws IOException {
		execute();
		return -1;
	}
	
	public long getLastModified() throws IOException {
		execute();
		return -1;
	}
	
	private synchronized void execute() throws IOException {
		if (this.state == STATE_CONNECTED) {
			return;
		} else {
			this.state = STATE_CONNECTED;
		}
		this.client.executeMethod(this.method);
	}
	
}	