package de.enough.polish.sample.browser;

import javax.microedition.lcdui.Command;

import de.enough.polish.browser.html.HtmlBrowser;
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
    this.htmlItem = new HtmlBrowser();
    new ChartTagHandler().register(this.htmlItem);
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

  public void go(String url)
  {
    this.htmlItem.go(url);
  }
  
  public boolean handleCommand(Command command)
  {
    return this.htmlItem.handleCommand(command);
  }
}
