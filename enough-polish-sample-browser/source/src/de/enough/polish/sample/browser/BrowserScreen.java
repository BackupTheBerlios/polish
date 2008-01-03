package de.enough.polish.sample.browser;

import de.enough.polish.browser.html.HtmlBrowser;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.Style;

public class BrowserScreen
extends Form
{
  private HtmlBrowser htmlBrowser;

  public BrowserScreen(String title)
  {
    this(title, null);
  }

  public BrowserScreen(String title, Style style)
  {
    super(title, style);

    //#style browser
    this.htmlBrowser = new HtmlBrowser();
    new ChartTagHandler( this.htmlBrowser.getTagHandler("div")).register(this.htmlBrowser);
    append(this.htmlBrowser);
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
    this.htmlBrowser.goBack();
  }

  public void go(String url)
  {
    this.htmlBrowser.go(url);
  }
  
}
