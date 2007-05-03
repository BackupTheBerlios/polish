package de.enough.polish.sample.rssbrowser;

import javax.microedition.lcdui.Command;

import de.enough.polish.browser.html.HtmlBrowser;
import de.enough.polish.browser.rss.RssBrowser;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Style;

public class BrowserScreen
extends Form
{
  private HtmlBrowser htmlItem;

  public BrowserScreen(String title)
  {
    this(title, null);
  }

  public BrowserScreen(String title, Style style)
  {
    super(title, style);

    //#style browser
    this.htmlItem = new RssBrowser();
    append(this.htmlItem);
  }

  /* (non-Javadoc)
   * @see de.enough.polish.ui.Screen#createCssSelector()
   */
  protected String createCssSelector()
  {
    return "form";
  }

  public void goBack()
  {
    this.htmlItem.goBack();
  }

  public void getURL(String url)
  {
    this.htmlItem.go(url);
  }
  
  public boolean handleCommand(Command command)
  {
    return this.htmlItem.handleCommand(command);
  }
}
