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
   * Creates a new HTTPProtocolHandler object with "http" as it's protocol.
   */
  public HttpProtocolHandler()
  {
    this("http",  new HashMap() );
  }
  
  /**
   * Creates a new HTTPProtocolHandler object with "http" as it's protocol.
   * 
   * @param requestProperties the request properties to use for each request
   */
  public HttpProtocolHandler(HashMap requestProperties)
  {
    this("http", requestProperties );
  }

  /**
   * Creates a new HTTPProtocolHandler object.
   * 
   * @param protocolName the protocolname (usually "http" or "https")
   */
  public HttpProtocolHandler(String protocolName)
  {
	    this(protocolName,  new HashMap() );
  }
  /**
   * Creates a new HTTPProtocolHandler object.
   * 
   * @param protocolName the protocolname (usually "http" or "https")
   * @param requestProperties the request properties to use for each request
   */
  public HttpProtocolHandler(String protocolName, HashMap requestProperties)
  {
    super( protocolName );

    this.requestProperties = requestProperties;
    if (requestProperties != null) {
	    if (requestProperties.get("User-Agent") == null)
	    {
	      requestProperties.put("User-Agent", HttpProtocolHandler.USER_AGENT);
	    }
    }
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
