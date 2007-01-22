/**
 * 
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
  
  public RedirectHttpConnection(String url)
  {
    this.originalUrl = url;
  }
  
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
        Object[] keys = this.requestProperties.keys();
        
        if (keys != null)
        {
          for (int i = 0; i < keys.length; i++)
          {
            tmpHttpConnection.setRequestProperty((String) keys[i],
                                              (String) this.requestProperties.get(keys[i]));
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

        if (resultCode == HttpConnection.HTTP_MOVED_TEMP)
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

        //#ifdef polish.Browser.Gzip
        // Check for Content-Encoding: gzip
        String contentEncoding = tmpHttpConnection.getHeaderField("content-encoding");

        // We support gzip only if we can detect the format...
        if (contentEncoding != null && ("gzip".regionMatches(true, 0, contentEncoding, 0, 4)))
        {
          // GZIP extension....
          //tmpIn = new GzipInputStream(tmpIn);
        }
        //#endif
        
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
    this.httpConnection.close();
  }

  /* (non-Javadoc)
   * @see javax.microedition.io.OutputConnection#openDataOutputStream()
   */
  public DataOutputStream openDataOutputStream() throws IOException
  {
    // TODO: Needs to be synnchronized and the DataOutputStream should only be created once.
    return new DataOutputStream(this.byteArrayOutputStream);
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