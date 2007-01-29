/*
 * Created on 11-Jan-2006 at 19:20:28.
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
package de.enough.polish.browser.protocols;

import de.enough.polish.browser.ProtocolHandler;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.util.HashMap;

import java.io.IOException;

import javax.microedition.io.StreamConnection;

/**
 * Protocol handler class to handle HTTP.
 * 
 * The sent user agent is normally the string defined in the
 * <code>microedition.platform</code> system property. You can overwrite
 * this value by defining the <code>polish.Browser.UserAgent</code> variable
 * in your <code>build.xml</code>.
 */
public class HttpProtocolHandler extends ProtocolHandler
{
  //#ifndef polish.Browser.UserAgent:defined
  private static final String USER_AGENT = System.getProperty("microedition.platform");
  //#else
  //#= private static final String USER_AGENT = "${polish.Browser.UserAgent}";
  //#endif

  private HashMap requestProperties;

  /**
   * Creates a new HTTPProtocolHandler object.
   */
  public HttpProtocolHandler()
  {
    super("http");
    
    this.requestProperties = new HashMap();
    init();
  }
  
  /**
   * Creates a new HTTPProtocolHandler object.
   * 
   * @param requestProperties the request properties to use for each request
   * 
   * @throws NullPointerException if requestProperties is null
   */
  public HttpProtocolHandler(HashMap requestProperties)
  {
    super("http");

    this.requestProperties = requestProperties;
    init();
  }

  private void init()
  {
    this.requestProperties.put("gofer-nosession", "true");

    if (this.requestProperties.get("User-Agent") == null)
    {
      this.requestProperties.put("User-Agent", HttpProtocolHandler.USER_AGENT);
    }
    
    //#if polish.Browser.Gzip
    if (this.requestProperties.get("Accept-Endcoding") == null)
    {
      this.requestProperties.put("Accept-Encoding", "gzip");
    }
    //#endif
  }

  /* (non-Javadoc)
   * @see de.enough.polish.browser.ProtocolHandler#getConnection(java.lang.String)
   */
  public StreamConnection getConnection(String url)
    throws IOException
  {
    return new RedirectHttpConnection(url, this.requestProperties);
  }
}
