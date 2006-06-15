/*
 * Created on Jul 26, 2005 at 11:47:07 AM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Jul 26, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class HttpConnectionImpl implements HttpConnection {

    // TODO: after close any method should throw IOException.
    
    private URL url;
    private HttpURLConnection connection;
    private boolean closed;
    private int mode;
    
    // TODO: What ist the default access mode?
    public HttpConnectionImpl(URL url) throws IOException {
        this(url,Connector.READ);
    }
    
    public HttpConnectionImpl(URL url, int  mode) throws IOException  {
        if(url == null){
            throw new IllegalArgumentException("ERROR:HttpConnectionImpl.HttpConnectionImpl(...):Parameter 'url' is null.");
        }
        //
        //TODO: test for valid mode value.
        this.url = url;
        this.mode = mode;
        this.closed = false;
        this.connection = (HttpURLConnection) this.url.openConnection();
        
        if(this.mode == Connector.READ) {
            this.connection.setDoInput(true);
            this.connection.setDoOutput(false);
        }
        if(this.mode == Connector.WRITE) {
            this.connection.setDoInput(false);
            this.connection.setDoOutput(true);
        }
        if(this.mode == Connector.READ_WRITE) {
            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
        }
    }
    
    
    /*
     * @see javax.microedition.io.HttpConnection#getURL()
     */
    public String getURL() {
        return this.url.toString();
    }

    /*
     * @see javax.microedition.io.HttpConnection#getProtocol()
     */
    public String getProtocol() {
        return this.url.getProtocol();
    }

    /*
     * @see javax.microedition.io.HttpConnection#getHost()
     */
    public String getHost() {
        return this.url.getHost();
    }

    /*
     * @see javax.microedition.io.HttpConnection#getFile()
     */
    public String getFile() {
        return this.url.getFile();
    }

    /*
     * @see javax.microedition.io.HttpConnection#getRef()
     */
    public String getRef() {
        return this.url.getRef();
    }

    /*
     * @see javax.microedition.io.HttpConnection#getQuery()
     */
    public String getQuery() {
        return this.url.getQuery();
    }

    /*
     * @see javax.microedition.io.HttpConnection#getPort()
     */
    public int getPort() {
        return this.url.getPort();
    }

    /*
     * @see javax.microedition.io.HttpConnection#getRequestMethod()
     */
    public String getRequestMethod() {
        return this.connection.getRequestMethod();
    }

    /*
     * @see javax.microedition.io.HttpConnection#setRequestMethod(java.lang.String)
     */
    public void setRequestMethod(String method) throws IOException {
       this.connection.setRequestMethod(method);
    }

    /*
     * @see javax.microedition.io.HttpConnection#getRequestProperty(java.lang.String)
     */
    public String getRequestProperty(String key) {
        return this.connection.getRequestProperty(key);
    }

    /*
     * @see javax.microedition.io.HttpConnection#setRequestProperty(java.lang.String, java.lang.String)
     */
    public void setRequestProperty(String key, String value) throws IOException {
        this.connection.setRequestProperty(key,value);
    }

    /*
     * @see javax.microedition.io.HttpConnection#getResponseCode()
     */
    public int getResponseCode() throws IOException {
        return this.connection.getResponseCode();
    }

    /*
     * @see javax.microedition.io.HttpConnection#getResponseMessage()
     */
    public String getResponseMessage() throws IOException {
        return this.connection.getResponseMessage();
    }

    /*
     * @see javax.microedition.io.HttpConnection#getExpiration()
     */
    public long getExpiration() throws IOException {
        return this.connection.getExpiration();
    }

    /*
     * @see javax.microedition.io.HttpConnection#getDate()
     */
    public long getDate() throws IOException {
        return this.connection.getDate();
    }

    /*
     * @see javax.microedition.io.HttpConnection#getLastModified()
     */
    public long getLastModified() throws IOException {
        return this.connection.getLastModified();
    }

    /*
     * @see javax.microedition.io.HttpConnection#getHeaderField(java.lang.String)
     */
    public String getHeaderField(String name) throws IOException {
        return this.connection.getHeaderField(name);
    }

    /*
     * @see javax.microedition.io.HttpConnection#getHeaderFieldInt(java.lang.String, int)
     */
    public int getHeaderFieldInt(String name, int def) throws IOException {
        return this.connection.getHeaderFieldInt(name,def);
    }

    /*
     * @see javax.microedition.io.HttpConnection#getHeaderFieldDate(java.lang.String, long)
     */
    public long getHeaderFieldDate(String name, long def) throws IOException {
        return this.connection.getHeaderFieldDate(name,def);
    }

    /*
     * @see javax.microedition.io.HttpConnection#getHeaderField(int)
     */
    public String getHeaderField(int n) throws IOException {
        return this.connection.getHeaderField(n);
    }

    /*
     * @see javax.microedition.io.HttpConnection#getHeaderFieldKey(int)
     */
    public String getHeaderFieldKey(int n) throws IOException {
        return this.connection.getHeaderFieldKey(n);
    }

    /*
     * @see javax.microedition.io.ContentConnection#getType()
     */
    public String getType() {
        return this.connection.getContentType();
    }

    /*
     * @see javax.microedition.io.ContentConnection#getEncoding()
     */
    public String getEncoding() {
        return this.connection.getContentEncoding();
    }

    /*
     * @see javax.microedition.io.ContentConnection#getLength()
     */
    public long getLength() {
        return this.connection.getContentLength();
    }

    /*
     * @see javax.microedition.io.InputConnection#openInputStream()
     */
    public InputStream openInputStream() throws IOException {
        return this.connection.getInputStream();
    }

    /*
     * @see javax.microedition.io.InputConnection#openDataInputStream()
     */
    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(this.connection.getInputStream());
    }

    /*
     * @see javax.microedition.io.Connection#close()
     */
    public void close() throws IOException {
        this.connection.disconnect();
        this.closed = true;
    }

    /*
     * @see javax.microedition.io.OutputConnection#openOutputStream()
     */
    public OutputStream openOutputStream() throws IOException {
        return this.connection.getOutputStream();
    }

    /*
     * @see javax.microedition.io.OutputConnection#openDataOutputStream()
     */
    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(this.connection.getOutputStream());
    }
}
