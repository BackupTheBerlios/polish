//#condition polish.usePolishGui || polish.midp

/*
 * Created on 13-Jan-2007 at 10:12:48.
 * 
 * Copyright (c) 2007 Michael Koch / Enough Software
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
package de.enough.polish.io;

import de.enough.polish.util.HashMap;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * Provides a <code>HttpConnection</code> that supports HTTP redirects.
 * This class is compatible to <code>javax.microedition.io.HttpConnection</code>.
 * 
 * <p>When connecting to an URL and a HTTP redirect is return this class follows
 * the redirect and uses the following HTTP connection. This works over multiple
 * levels. By default five redirects are supported. The number of supported redirects
 * can be tuned by setting the preprocessing variable <code>polish.Browser.MaxRedirects</code>
 * to some integer value.</p>
 *
 * @see HttpConnection
 */
public class RedirectHttpConnection
  implements HttpConnection
{
  //#ifndef polish.Browser.MaxRedirects:defined
  private static final int MAX_REDIRECTS = 5;
  //#else
  //#= private static final int MAX_REDIRECTS = ${polish.Browser.MaxRedirects};
  //#endif

  private String originalUrl;
  private String requestMethod = HttpConnection.GET;
  private HashMap requestProperties;
  private HttpConnection httpConnection;
  private ByteArrayOutputStream byteArrayOutputStream;
  private InputStream inputStream;
  
  /**
   * Creates a new http connection that understands redirects.
   * 
   * @param url the url to connect to
   */
  public RedirectHttpConnection(String url)
  {
    this.originalUrl = url;
  }
  
  /**
   * Creates a new http connection that understands redirects.
   * 
   * @param url the url to connect to
   * @param requestProperties the request properties to be set for each http request
   * 
   * @throws IOException if an error occured
   */
  public RedirectHttpConnection(String url, HashMap requestProperties)
    throws IOException
  {
    this.originalUrl = url;
    this.requestProperties = new HashMap();
    
    // Copy request properites and adjust properties of needed
    // (done in setRequestProperty).
    Object[] keys = requestProperties.keys();
    
    for (int i = 0; i < keys.length; i++)
    {
      setRequestProperty((String) keys[i], (String) requestProperties.get(keys[i]));
    }
  }
  
  /**
   * Makes sure that the http connect got created. This method does redirects until
   * the final connection is created.
   */
  private synchronized void ensureConnectionCreated()
  {
    if (this.httpConnection != null)
    {
      return;
    }
    
    HttpConnection tmpHttpConnection = null;
    InputStream tmpIn = null;
    
    try
    {
      int redirects = 0;
      String url = this.originalUrl;

      while (true)
      {
        tmpHttpConnection = (HttpConnection) Connector.open(url, Connector.READ_WRITE, true);
        tmpHttpConnection.setRequestMethod(this.requestMethod);
        if (this.requestProperties != null) {
	        Object[] keys = this.requestProperties.keys();
	        
	        if (keys != null)
	        {
	          for (int i = 0; i < keys.length; i++)
	          {
	            tmpHttpConnection.setRequestProperty((String) keys[i],
	                                              (String) this.requestProperties.get(keys[i]));
	          }
	        }
        }
        
        // Send POST data if exists.
        if (this.byteArrayOutputStream != null)
        {
          byte[] postData = this.byteArrayOutputStream.toByteArray();
          
          if (postData != null && postData.length > 0)
          {
            OutputStream out = tmpHttpConnection.openOutputStream();
            out.write(postData);
            out.close();
          }
        }

        // Opens the connection.
        tmpIn = tmpHttpConnection.openInputStream();
        int resultCode = tmpHttpConnection.getResponseCode();

        if (resultCode == HttpConnection.HTTP_MOVED_TEMP ||  resultCode == HttpConnection.HTTP_MOVED_PERM || resultCode == HttpConnection.HTTP_SEE_OTHER || resultCode == HttpConnection.HTTP_TEMP_REDIRECT)
        {
          url = tmpHttpConnection.getHeaderField("Location");
          tmpHttpConnection.close();

          if (++redirects > MAX_REDIRECTS)
          {
            // Too many redirects - give up.
            break;
          }

          continue;
        }

        break;
      }
    }
    catch (IOException e)
    {
      //debug
      e.printStackTrace();
    }
    
    this.httpConnection = tmpHttpConnection;
    this.inputStream = tmpIn;
  }
  
  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getDate()
   */
  public long getDate() throws IOException
  {
    ensureConnectionCreated();
    return this.httpConnection.getDate();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getExpiration()
   */
  public long getExpiration() throws IOException
  {
    ensureConnectionCreated();
    return this.httpConnection.getExpiration();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getFile()
   */
  public String getFile()
  {
    ensureConnectionCreated();
    return this.httpConnection.getFile();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getHeaderField(java.lang.String)
   */
  public String getHeaderField(String name) throws IOException
  {
    ensureConnectionCreated();
    return this.httpConnection.getHeaderField(name);
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getHeaderField(int)
   */
  public String getHeaderField(int n) throws IOException
  {
    ensureConnectionCreated();
    return this.httpConnection.getHeaderField(n);
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getHeaderFieldDate(java.lang.String, long)
   */
  public long getHeaderFieldDate(String name, long def) throws IOException
  {
    ensureConnectionCreated();
    return this.httpConnection.getHeaderFieldDate(name, def);
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getHeaderFieldInt(java.lang.String, int)
   */
  public int getHeaderFieldInt(String name, int def) throws IOException
  {
    ensureConnectionCreated();
    return this.httpConnection.getHeaderFieldInt(name, def);
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getHeaderFieldKey(int)
   */
  public String getHeaderFieldKey(int n) throws IOException
  {
    ensureConnectionCreated();
    return this.httpConnection.getHeaderFieldKey(n);
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getHost()
   */
  public String getHost()
  {
    ensureConnectionCreated();
    return this.httpConnection.getHost();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getLastModified()
   */
  public long getLastModified() throws IOException
  {
    ensureConnectionCreated();
    return this.httpConnection.getLastModified();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getPort()
   */
  public int getPort()
  {
    ensureConnectionCreated();
    return this.httpConnection.getPort();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getProtocol()
   */
  public String getProtocol()
  {
    ensureConnectionCreated();
    return this.httpConnection.getProtocol();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getQuery()
   */
  public String getQuery()
  {
    ensureConnectionCreated();
    return this.httpConnection.getQuery();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getRef()
   */
  public String getRef()
  {
    ensureConnectionCreated();
    return this.httpConnection.getRef();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getRequestMethod()
   */
  public String getRequestMethod()
  {
    return this.requestMethod;
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getRequestProperty(java.lang.String)
   */
  public String getRequestProperty(String key)
  {
    return (String) this.requestProperties.get(key);
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getResponseCode()
   */
  public int getResponseCode() throws IOException
  {
    ensureConnectionCreated();
    return this.httpConnection.getResponseCode();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getResponseMessage()
   */
  public String getResponseMessage() throws IOException
  {
    ensureConnectionCreated();
    return this.httpConnection.getResponseMessage();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#getURL()
   */
  public String getURL()
  {
    return this.originalUrl;
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#setRequestMethod(java.lang.String)
   */
  public void setRequestMethod(String requestMethod) throws IOException
  {
    this.requestMethod = requestMethod;
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.HttpConnection#setRequestProperty(java.lang.String, java.lang.String)
   */
  public void setRequestProperty(String key, String value) throws IOException
  {
	  if (this.requestProperties == null) {
		  this.requestProperties = new HashMap();
	  }
    //#if polish.Bugs.HttpIfModifiedSince
    if ("if-modified-since".equals(key.toLowerCase()))
    {
      Date d = new Date(System.currentTimeMillis());
      this.requestProperties.put("IF-Modified-Since", d.toString());
    }
    //#endif

    this.requestProperties.put(key, value);
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.ContentConnection#getEncoding()
   */
  public String getEncoding()
  {
    ensureConnectionCreated();
    return this.httpConnection.getEncoding();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.ContentConnection#getLength()
   */
  public long getLength()
  {
    ensureConnectionCreated();
    return this.httpConnection.getLength();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.ContentConnection#getType()
   */
  public String getType()
  {
    ensureConnectionCreated();
    return this.httpConnection.getType();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.InputConnection#openDataInputStream()
   */
  public DataInputStream openDataInputStream() throws IOException
  {
    // TODO: Needs to be synnchronized and the DataInputStream should only be created once.
    return new DataInputStream(openInputStream());
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.InputConnection#openInputStream()
   */
  public InputStream openInputStream() throws IOException
  {
    ensureConnectionCreated();
    return this.inputStream;
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.Connection#close()
   */
  public void close() throws IOException
  {
    // Check if there is a connection to actually close.
    if (this.httpConnection != null)
    {
      this.httpConnection.close();
      this.httpConnection = null;
    }
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.OutputConnection#openDataOutputStream()
   */
  public DataOutputStream openDataOutputStream() throws IOException
  {
    // TODO: Needs to be synchronized and the DataOutputStream should only be created once.
    return new DataOutputStream( openOutputStream() );
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.OutputConnection#openOutputStream()
   */
  public synchronized OutputStream openOutputStream() throws IOException
  {
    if (this.byteArrayOutputStream == null)
    {
      this.byteArrayOutputStream = new ByteArrayOutputStream();
    }
    
    return this.byteArrayOutputStream;
  }
}