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
package de.enough.polish.browser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Stack;

import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.browser.protocols.HttpProtocolHandler;
import de.enough.polish.browser.protocols.ResourceProtocolHandler;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.io.StringReader;
import de.enough.polish.util.HashMap;
import de.enough.polish.xml.PullParser;
import de.enough.polish.xml.XmlPullParser;

//#if polish.LibraryBuild
//# import de.enough.polish.ui.FakeContainerCustomItem;
//#else
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Item;
//#endif
import de.enough.polish.ui.Style;

/**
 * TODO: Write good docs.
 * 
 * polish.Browser.UserAgent
 * polish.Browser.MaxRedirects
 * polish.Browser.Gzip
 * polish.Browser.POISupport
 * polish.Browser.PaintDownloadIndicator
 * 
 * @see HttpProtocolHandler
 * @see ResourceProtocolHandler
 * @see RedirectHttpConnection
 */
public abstract class Browser
//#if polish.LibraryBuild
	//#  extends FakeContainerCustomItem
//#else
  extends Container
//#endif
implements Runnable
 {
  private static final String BROKEN_IMAGE = "resource://broken.png";

  private HashMap imageCache = new HashMap();

  protected String currentDocumentBase = null;
  protected HashMap protocolHandlers = new HashMap();
  protected HashMap tagHandlers = new HashMap();
  
  protected Stack history = new Stack();
  //#if polish.Browser.PaintDownloadIndicator
  	protected Gauge loadingIndicator;
  //#endif
  
  private Thread loadingThread;
  private boolean isRunning;
  private boolean isWorking;
  private boolean isCancelRequested;

private String nextUrl;



  /**
   * Creates a new Browser without any protocol handlers, tag handlers or style.
   */
  public Browser()
  {
	  //#style browser?
    this( (String[])null, (TagHandler[])null, (ProtocolHandler[]) null );
  }
  
  /**
   * Creates a new Browser without any protocol handler or tag handlers.
   * 
   * @param style the style of this browser
   */
  public Browser( Style style )
  {
    this( (String[])null, (TagHandler[])null, (ProtocolHandler[]) null, style );
  }

  /**
   * Creates a new Browser with the specified handlers and style.
   * 
   * @param tagHandlers the tag handlers
   */
  public Browser( ProtocolHandler[] protocolHandlers )
  {
	  //#if polish.css.style.browser
	  	//#style browser
	  	//# this(protocolHandlers);
	  //#else
	  	this(protocolHandlers, null);
	  //#endif
  }
  /**
   * Creates a new Browser with the specified handlers and style.
   * 
   * @param tagHandlers the tag handlers
   */
  public Browser( ProtocolHandler[] protocolHandlers, Style style )
  {
	  this( (String[])null, (TagHandler[])null, protocolHandlers, style);
  }


  /**
   * Creates a new Browser with the specified handlers and style.
   * 
   * @param tagHandlers the tag handlers
   * @param protocolHandlers the protocol handlers
   */
  public Browser(  String[] tagNames, TagHandler[] tagHandlers, ProtocolHandler[] protocolHandlers )
  {
	  //#if polish.css.style.browser
	  	//#style browser
	  	//# this(tagNames, tagHandlers, protocolHandlers);
	  //#else
	  	this(tagNames,tagHandlers, protocolHandlers, null);
	  //#endif
  }

  /**
   * Creates a new Browser with the specified handlers and style.
   * 
   * @param tagNames the names of the tags that the taghandler should handle (this allows to use a single taghandler for several tags)
   * @param tagHandlers the tag handlers
   * @param protocolHandlers the protocol handlers
   * @param style the style of this browser
   */
  public Browser( String[] tagNames, TagHandler[] tagHandlers, ProtocolHandler[] protocolHandlers, Style style )
  {
    super( true, style );
    if (tagHandlers != null && tagNames != null && tagNames.length == tagHandlers.length) {
	    for (int i = 0; i < tagHandlers.length; i++) {
	    	TagHandler handler = tagHandlers[i];
			addTagHandler(tagNames[i], handler);
		}
    }
    if (protocolHandlers != null) {
    	for (int i = 0; i < protocolHandlers.length; i++) {
			ProtocolHandler handler = protocolHandlers[i];
			addProtocolHandler( handler );
			
		}
    }
    //#if polish.Browser.PaintDownloadIndicator
	    //#style browserDownloadIndicator
	    this.loadingIndicator = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
	//#endif
	this.loadingThread = new Thread( this );
	this.loadingThread.start();
  }
  
  /**
   * Instantiates and returns the default tag handlers for "http", "https" and "resource" URLs.
   * 
   * @return new default tag handlers
   * @see HttpProtocolHandler
   * @see ResourceProtocolHandler
   */
  public static ProtocolHandler[] getDefaultProtocolHandlers() {
	  return new ProtocolHandler[] { new HttpProtocolHandler("http"), new HttpProtocolHandler("https"), new ResourceProtocolHandler("resource") };  
  }


  public void addTagCommand(String tagName, Command command)
  {
	  TagHandler handler = getTagHandler(tagName);
	  
    if (handler != null )
    {
		  handler.addTagCommand( tagName, command );
	  }
  }
  
  public void addAttributeCommand(String attributeName, String attributeValue, Command command)
  {
    addAttributeCommand(null, attributeName, attributeValue, command);
  }
  
  public void addAttributeCommand(String tagName, String attributeName, String attributeValue, Command command)
  {
	  TagHandler handler = getTagHandler(tagName);
	  
    if (handler != null )
    {
		  handler.addAttributeCommand( tagName, attributeName, attributeValue, command );
	  }
  }

  public void addProtocolHandler(ProtocolHandler handler)
  {
    this.protocolHandlers.put(handler.getProtocolName(), handler);
  }
  
  public void addProtocolHandler(String protocolName, ProtocolHandler handler)
  {
    this.protocolHandlers.put(protocolName, handler);
  }
  
  protected ProtocolHandler getProtocolHandler(String protocolName)
  {
    return (ProtocolHandler) this.protocolHandlers.get(protocolName);
  }

  protected ProtocolHandler getProtocolHandlerForURL(String url)
    throws IOException
  {
    int pos = url.indexOf(':');
    
    if (pos < 0)
    {
      throw new IOException("malformed url");
    }
    
    String protocol = url.substring(0, pos);
    ProtocolHandler handler = (ProtocolHandler) this.protocolHandlers.get(protocol);
    
    if (handler == null)
      throw new IOException("protocol handler not found");
    
    return handler;
  }
  
  public void addTagHandler(String tagName, TagHandler handler)
  {
    this.tagHandlers.put(new TagHandlerKey(tagName), handler);
  }
  
  public void addTagHandler(String tagName, String attributeName, String attributeValue, TagHandler handler)
  {
    TagHandlerKey key = new TagHandlerKey(tagName, attributeName, attributeValue);
    
    //#debug
    System.out.println("Browser.addTagHandler: adding key: " + key);
    
    this.tagHandlers.put(key, handler);
  }
  
  public TagHandler getTagHandler(String tagName)
  {
    TagHandlerKey key = new TagHandlerKey(tagName);
    return (TagHandler) this.tagHandlers.get(key);
  }
  
  public TagHandler getTagHandler(String tagName, String attributeName, String attributeValue)
  {
    TagHandlerKey key = new TagHandlerKey(tagName, attributeName, attributeValue);
    return (TagHandler) this.tagHandlers.get(key);
  }
  
  /**
   * @param parser the parser to read the page from
   */
  private void parsePage(PullParser parser)
  {
    // Clear out all items in the browser.
    clear();
    
    // Clear image cache when visiting a new page.
    this.imageCache.clear();
    
    while (parser.next() != PullParser.END_DOCUMENT)
    {
      if (parser.getType() == PullParser.START_TAG
          || parser.getType() == PullParser.END_TAG)
      {
        boolean openingTag = parser.getType() == PullParser.START_TAG;

        //#debug
        System.out.println( "looking for handler for " + parser.getName()  + ", openingTag=" + openingTag );
        
        HashMap attributeMap = new HashMap();
        TagHandler handler = getTagHandler(parser, attributeMap);
        
        if (handler != null)
        {
          //#debug
          System.out.println("Michael: Calling handler: " + parser.getName() + " " + attributeMap);
          
          handler.handleTag(this, parser, parser.getName(), openingTag, attributeMap);
        }
        else
        {
          //#debug
        	System.out.println( "no handler for " + parser.getName() );
        }
      }
      else if (parser.getType() == PullParser.TEXT)
      {
        handleText(parser.getText().trim());
      }
      else
      {
    	  System.out.println("unknown type: " + parser.getType() + ", name=" + parser.getName());
      }
    } // end while (parser.next() != PullParser.END_DOCUMENT)

    //#debug
    System.out.println("end of document...");
  }

  private TagHandler getTagHandler(PullParser parser, HashMap attributeMap)
  {
    TagHandlerKey key;
    TagHandler handler = null;
    
    for (int i = 0; i < parser.getAttributeCount(); i++)
    {
      String attributeName = parser.getAttributeName(i);
      String attributeValue = parser.getAttributeValue(i);
      attributeMap.put(attributeName, attributeValue);
      
      key = new TagHandlerKey(parser.getName(),
                              attributeName,
                              attributeValue);
      handler = (TagHandler) this.tagHandlers.get(key);
      
      if (handler != null)
      {
        break;
      }
    }
    
    if (handler == null)
    {
      key = new TagHandlerKey(parser.getName());
      handler = (TagHandler) this.tagHandlers.get(key);
    }
    
    return handler;
  }
  
  /**
   * @param text
   */
  protected abstract void handleText(String text);

  /**
   * @param reader
   * 
   * @throws IOException of an error occurs
   */
  public void loadPage(Reader reader)
    throws IOException
  {
    XmlPullParser xmlReader = new XmlPullParser(reader);
    xmlReader.relaxed = true;
    parsePage(xmlReader);
  }

  /**
   * "http://foo.bar.com/baz/blah.html" => "http://foo.bar.com"
   * <p>
   * "resource://baz/blah.html" => "resource://"
   */
  protected String protocolAndHostOf(String url)
  {
    if ("resource://".regionMatches(true, 0, url, 0, 11))
    {
      return "resource://";
    }
    else if ("http://".regionMatches(true, 0, url, 0, 7))
    {
      int hostStart = url.indexOf("//");
      // figure out what error checking to do here
      hostStart+=2;
      // look for next '/'. If none, assume rest of string is hostname
      int hostEnd = url.indexOf("/", hostStart);
    
      if (hostEnd != -1)
      {
        return url.substring(0, hostEnd);
      }
      else
      {
        return url;
      }
    }
    else
    {
      // unsupported protocol
      return url;
    }
  }

  /**
   * Takes a possibly relative URL, and generate an absolute URL, merging with
   * the current documentbase if needed.
   * 
   * <ol>
   * <li> If URL starts with http:// or resource:// leave it alone
   * <li> If URL starts with '/', prepend document base protocol and host name.
   * <li> Otherwise, it's a relative URL, so prepend current document base and
   * directory path.
   * </ol>
   * 
   * @param url the (possibly relative) URL
   * @return absolute URL
   */
  public String makeAbsoluteURL(String url)
  {
    //#debug debug
    System.out.println("makeAbsoluteURL: currentDocumentBase = " + this.currentDocumentBase);
    
    // If no ":", assume it's a relative link, (no protocol),
    // and append current page
    if (url.indexOf("://") != -1)
    {
      return url;
    }
    else if (url.startsWith("/"))
    {
      if ("resource://".regionMatches(true, 0, this.currentDocumentBase, 0, 11))
      {
        // we need to strip a leading slash if it's a local resource, i.e.,
        // "resource://" + "/foo.png" => "resource://foo.png"
        return protocolAndHostOf(this.currentDocumentBase) + url.substring(1);
      }
      else
      {
        // for HTTP, we don't need to strip the leading slash, i.e.,
        // "http://foo.bar.com" + "/foo.png" => "http://foo.bar.com/foo.png"
        return protocolAndHostOf(this.currentDocumentBase) + url;
      }
    }
    else
    {
      // It's a relative url, so merge it with the current document
      // path.
      String prefix = protocolAndPathOf(this.currentDocumentBase);
      
      if (prefix.endsWith("/"))
      {
        return prefix + url;
      }
      else
      {
        return prefix + "/" + url;
      }
    }
  }

  public void loadPage(String document)
  {
    try
    {
      loadPage(new StringReader(document));
    }
    catch (IOException e)
    {
      // StringReader never throws an IOException.
    }
  }

  public void loadPage(InputStream in)
    throws IOException
  {
	  if (in == null)
	  {
		  throw new IOException("no input stream");
	  }

    loadPage(new InputStreamReader(in));
  }

  private Image loadImageInternal(String url)
  {
    Image image = (Image) this.imageCache.get(url);

    if (image == null)
    {
      try
      {
        ProtocolHandler handler = getProtocolHandlerForURL(url);
        
        StreamConnection connection = handler.getConnection(url);
        InputStream is = connection.openInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int bytesRead;

        do
        {
          bytesRead = is.read(buf);
          
          if (bytesRead > 0)
          {
            bos.write(buf, 0, bytesRead);
          }
        }
        while (bytesRead >= 0);
        
        buf = bos.toByteArray();
        
        //#debug
        System.out.println("Image requested: " + url);

        image = Image.createImage(buf, 0, buf.length);
        this.imageCache.put(url, image);
        return image;
      }
      catch (Exception e)
      {
        // TODO: Implement proper error handling.
        
        //#debug debug
        e.printStackTrace();
        
        return null;
      }
    }
    
    return image;
  }

  public Image loadImage(String url)
  {
    Image image = loadImageInternal(url);
    
    if (image == null)
    {
      image = loadImageInternal(BROKEN_IMAGE);
    }
    
    if (image == null)
    {
      image = Image.createImage(10, 10);
      Graphics g = image.getGraphics();
      g.setColor(0xFFFFFF);
      g.fillRect(0, 0, 10, 10);
      g.setColor(0xFF0000);
      g.drawLine(0, 0, 10, 10);
      g.drawLine(0, 10, 10, 0);
    }
    
    return image;
  }

  /**
   * "http://foo.bar.com/baz/boo/blah.html" => "http://foo.bar.com/baz/boo"
   * <br>
   " "http://foo.bar.com" => "http://foo.bar.com"
   * <br>
   * "resource://baz/blah.html" => "resource://baz"
   * <br>
   * "resource://blah.html" => "resource://"
   */
  protected String protocolAndPathOf (String url)
  {
    // 1. Look for query args, or end of string.
    // 2. from there, scan backward for first '/', 
    // 3. cut the string there.
    // figure out what error checking to do here
  
    int end = url.indexOf('?');
    
    if (end == -1)
    {
      end = url.length()-1;
    }
        
    int hostStart = url.indexOf("//");
    // figure out what error checking to do here
    hostStart += 2;
  
    int lastSlash = url.lastIndexOf('/', end);
  
    // RESOURCE urls have no host portion, so return everything between
    // the "resource://" and last slash, if it exists.
    if ("resource://".regionMatches(true, 0, url, 0, 11))
    {
      if ((lastSlash == -1) || (lastSlash <= hostStart))
      {
        return "resource://";
      }

      return url.substring(0, lastSlash);
    }
    else
    {
      if ((lastSlash == -1) || (lastSlash <= hostStart))
      {
        return url;
      }
      
      return url.substring(0, lastSlash);
    }
  }
  
  public boolean handleCommand(Command command)
  {
    TagHandler[] handlers = (TagHandler[])
      this.tagHandlers.values(new TagHandler[this.tagHandlers.size()]);
    
    for (int i = 0; i < handlers.length; i++)
    {
      if (handlers[i].handleCommand(command))
      {
        return true;
      }
    }
    
    return false;
  }
  
  protected void goImpl(String url)
  {
    try
    {
      // Throws an exception if no handler found.
      ProtocolHandler handler = getProtocolHandlerForURL(url);
    
      this.currentDocumentBase = url;
    
      StreamConnection connection = handler.getConnection(url);
      
      if (connection != null)
      {
        loadPage(connection.openInputStream());
        connection.close();
      }
    }
    catch (IOException e)
    {
      //#debug error
      e.printStackTrace();
    }
  }
  
  //////////////// download indicator handling /////////////
  
  //#if polish.Browser.PaintDownloadIndicator
  
  /* (non-Javadoc)
   * @see de.enough.polish.ui.Container#initContent(int, int)
   */
  protected void initContent(int firstLineWidth, int lineWidth) {
  	super.initContent(firstLineWidth, lineWidth);
  	// when there is a loading indicator, we need to specify the minmum size:
  	int width = this.loadingIndicator.getItemWidth( lineWidth, lineWidth );
  	if (width > this.contentWidth) {
  		this.contentWidth = width;
  	}
  	int height = this.loadingIndicator.itemHeight;
  	if (height > this.contentHeight) {
  		this.contentHeight = height;
  	}
  }
  
  /* (non-Javadoc)
   * @see de.enough.polish.ui.Container#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
   */
  protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g)
  {
    super.paintContent(x, y, leftBorder, rightBorder, g);

    if (this.isWorking)
    {
    	int lineWidth = rightBorder - leftBorder;
      	int liHeight = this.loadingIndicator.getItemHeight( lineWidth, lineWidth );
      	int liLayout = this.loadingIndicator.getLayout();
      	if ( (liLayout & LAYOUT_VCENTER) == LAYOUT_VCENTER ) {
      		y += this.contentHeight>>1 + liHeight>>1;
      	} else if ( (liLayout & LAYOUT_BOTTOM ) == LAYOUT_BOTTOM ) {
      		y += this.contentHeight - liHeight;
      	}
      	this.loadingIndicator.paint(x, y, leftBorder, rightBorder, g);
    }
  }
  
  /* (non-Javadoc)
   * @see de.enough.polish.ui.Container#animate()
   */
  public boolean animate()
  {
    boolean result = false;
    
    if (this.isWorking)
    {
      result = this.loadingIndicator.animate();
    }

    return super.animate() | result;
  }
  //#endif
 
  
  ////////////////  downloading thread /////////////////
  public void run()
  {
    this.isRunning = true;

    while (this.isRunning)
    {
      if (this.isRunning && this.nextUrl != null)
      {
        this.isWorking = true;
        String url = this.nextUrl;
        this.nextUrl = null;
          
        if (this.isCancelRequested != true)
        {
          goImpl(url);
        }
        
        this.isWorking = false;
        repaint();
      }
        
      if (this.isCancelRequested == true)
      {
        this.isWorking = false;
        repaint();
        this.isCancelRequested = false;
        this.nextUrl = null;
        loadPage("Request canceled");
      }
        
      try
      {
        this.isWorking = false;
        synchronized( this.loadingThread ) {
        	this.loadingThread.wait();
        }
      }
      catch (InterruptedException ie)
      {
//        interrupt();
      }
    } // end while(isRunning)
  } // end run()
  
  protected void schedule(String url)
  {
    this.nextUrl = url;
    synchronized( this.loadingThread ) {
    	System.out.println("notifying thread for url " + url);
    	this.loadingThread.notify();
    }
    System.out.println("done...");
  }
      
  public void cancel()
  {
    this.isCancelRequested = true;
  }

  public synchronized void requestStop()
  {
    this.isRunning = false;
    synchronized( this.loadingThread ) {
    	this.loadingThread.notify();
    }
  }

  public boolean isRunning()
  {
    return this.isRunning;
  }
  
  public boolean isCanceled()
  {
    return this.isCancelRequested;
  }
  
  public boolean isWorking()
  {
    return this.isWorking;
  }
  
  
  //////////////////////////// History //////////////////////////////
  
  public void go(String url)
  {
      if (this.currentDocumentBase != null)
      {
      	this.history.push(this.currentDocumentBase);
      }
      schedule(url);
  }
  

  
  public void go(int historySteps)
  {
    String document = null;
    
    while (historySteps > 0 && this.history.size() > 0)
    {
      document = (String) this.history.pop();
      historySteps--;
    }
    
    if (document != null)
    {
      goImpl(document);
    }
  }
  
  public void followLink()
  {
    Item item = getFocusedItem();
    String href = (String) item.getAttribute("href");
    
    if (href != null)
    {
      go(makeAbsoluteURL(href));
    }
  }
  
  public void goBack()
  {
    go(1);
  }
  
  public boolean canGoBack()
  {
    return this.history.size() > 0;
  }
  
  public void clearHistory()
  {
    this.history.removeAllElements();
  }
}
