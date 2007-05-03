package de.enough.polish.sample.rssbrowser;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;

import de.enough.polish.browser.html.HtmlBrowser;
import de.enough.polish.browser.rss.RssBrowser;
import de.enough.polish.browser.rss.RssItem;
import de.enough.polish.browser.rss.RssTagHandler;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Style;

public class BrowserScreen
	extends Form
	implements ItemCommandListener
{
  private Display display;
  private HtmlBrowser htmlItem;

  public BrowserScreen(Display display, String title)
  {
    this(display, title, null);
  }

  public BrowserScreen(Display display, String title, Style style)
  {
    super(title, style);

    this.display = display;

    //#style browser
    this.htmlItem = new RssBrowser(this);
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

  public void commandAction(Command command, Item item)
  {
	  if (command == RssTagHandler.CMD_RSS_ITEM_SELECT) {
		  RssItem rssItem = (RssItem) item.getAttribute(RssTagHandler.RSS_ITEM);

		  if (rssItem != null) {
			  //#style rssItemView
			  Alert alert = new Alert( rssItem.getTitle(), rssItem.getDescription(), null, AlertType.ERROR );
			  alert.setTimeout(Alert.FOREVER);
			  this.display.setCurrent(alert);
		  }
	  }
  }
}
