package de.enough.polish.sample.rssbrowser;

import javax.microedition.lcdui.Command;

import de.enough.polish.browser.rss.RssBrowser;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.Style;

public class BrowserScreen
	extends Form
{
  private RssBrowser htmlItem;

  public BrowserScreen(ItemCommandListener itemCommandListener, String title)
  {
    this(itemCommandListener, title, null);
  }

  public BrowserScreen(ItemCommandListener itemCommandListener, String title, Style style)
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
  
  public boolean processCommand(Command command)
  {
    return this.htmlItem.handleCommand(command);
  }

/**
 * @return
 */
public boolean canGoBack() {
	return this.htmlItem.canGoBack();
}
}
