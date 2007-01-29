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
package de.enough.polish.browser.chtml;

import java.io.IOException;
import java.util.Stack;

import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Graphics;

import de.enough.polish.browser.Browser;
import de.enough.polish.browser.ProtocolHandler;
import de.enough.polish.browser.protocols.HttpProtocolHandler;
import de.enough.polish.browser.protocols.ResourceProtocolHandler;
import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.ui.Gauge;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.containerviews.MIDP2LayoutView;
import de.enough.polish.util.StringTokenizer;

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
public class HtmlBrowser
  extends Browser
{
  /**
   * Helper thread for reading all content in background!
   */
  public class ReadThread extends Thread
  {
    private String url;
    private boolean cancelRequest;
    private boolean isRunning;
    private boolean isWorking;
    
    public synchronized void run()
    {
      this.isRunning = true;

      while (this.isRunning)
      {
        if (this.isRunning && this.url != null)
        {
          this.isWorking = true;
          String url = this.url;
          this.url = null;
            
          if (this.cancelRequest != true)
          {
            goImpl(url);
          }
          
          this.isWorking = false;
          HtmlBrowser.this.requestRepaint();
        }
          
        if (this.cancelRequest == true)
        {
          this.isWorking = false;
          HtmlBrowser.this.requestRepaint();
          this.cancelRequest = false;
          this.url = null;
          loadPage("Anfrage abgebrochen!");
        }
          
        try
        {
          this.isWorking = false;
          HtmlBrowser.this.requestRepaint();
          wait();
        }
        catch (InterruptedException ie)
        {
//          interrupt();
        }
      }
    }
    
    public synchronized void go(String url)
    {
      this.url = url;
      notify();
    }
        
    public void cancel()
    {
      this.cancelRequest = true;
    }
  
    public synchronized void requestStop()
    {
      this.isRunning = false;
      notify();
    }

    public boolean isRunning()
    {
      return this.isRunning;
    }
    
    public boolean isCanceled()
    {
      return this.cancelRequest;
    }
    
    public boolean isWorking()
    {
      return this.isWorking;
    }
  }

  private Stack history = new Stack();
  private HtmlTagHandler htmlTagHandler;
  private ReadThread readThread;

  //#if polish.Browser.PaintDownloadIndicator:defined
  private Gauge gauge;
  //#endif

  public HtmlBrowser()
  {
    this.containerView = new MIDP2LayoutView();

    this.readThread = new ReadThread();
    this.readThread.start();

    //#if polish.Browser.PaintDownloadIndicator:defined
    //#style browserGauge
    this.gauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
    this.gauge.setPreferredSize(100,30);
    //#endif
  }

  public void setHtmlTagHandler(HtmlTagHandler htmlTagHandler)
  {
    this.htmlTagHandler = htmlTagHandler;
  }
  
  public void go(String url)
  {
    if (this.readThread != null)
    {
      if (this.currentDocumentBase != null)
      {
      	this.history.push(this.currentDocumentBase);
      }
      this.readThread.go(url);
    }
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
      //#debug
      e.printStackTrace();
    }
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
  
  protected void handleText(String text)
  {
    if (text.length() > 0)
    {
      StringTokenizer st = new StringTokenizer(text, " \n\t");
      
      while (st.hasMoreTokens())
      {
        String str = st.nextToken();
        StringItem stringItem = null;

        if (this.htmlTagHandler.textBold && this.htmlTagHandler.textItalic)
        {
          //#style browserTextBoldItalic
          stringItem = new StringItem(null, str);
        }
        else if (this.htmlTagHandler.textBold)
        {
          //#style browserTextBold
          stringItem = new StringItem(null, str);
        }
        else if (this.htmlTagHandler.textItalic)
        {
          //#style browserTextItalic
          stringItem = new StringItem(null, str);
        }
        else
        {
          //#style browserText
          stringItem = new StringItem(null, str);
        }
        
        // TODO: Remove me.
//        stringItem.addCommand(new Command("Test", Command.SCREEN, 1));
        add(stringItem);
      }
    }
  }
  
  //#if polish.Browser.PaintDownloadIndicator:defined
  /* (non-Javadoc)
   * @see de.enough.polish.ui.Container#paintContent(int, int, int, int, javax.microedition.lcdui.Graphics)
   */
  protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g)
  {
    super.paintContent(x, y, leftBorder, rightBorder, g);

    if (this.readThread.isWorking())
    {
      this.gauge.paint(x, y, leftBorder, rightBorder, g);
    }
  }
  
  /* (non-Javadoc)
   * @see de.enough.polish.ui.Container#animate()
   */
  public boolean animate()
  {
    boolean result = false;
    
    if (this.readThread.isWorking())
    {
      result = this.gauge.animate();
    }

    return super.animate() | result;
  }
  //#endif
  
  void requestRepaint()
  {
    repaint();
  }
}
