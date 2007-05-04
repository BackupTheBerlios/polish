package de.enough.polish.sample.rssbrowser;

import de.enough.polish.browser.html.HtmlTagHandler;
import de.enough.polish.browser.rss.RssItem;
import de.enough.polish.browser.rss.RssTagHandler;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class BrowserMidlet 
	extends MIDlet
	implements CommandListener, ItemCommandListener
{
	private Command cmdBack = new Command("Back", Command.SCREEN, Command.BACK );

	private Display display;
	private BrowserScreen browserScreen;

     protected void startApp()
     	throws MIDletStateChangeException
     {
          this.display = Display.getDisplay(this);

          //#style browserScreen
          BrowserScreen screen = new BrowserScreen(this, "BrowserItem");
          screen.getURL("http://www.digg.com/rss/containerscience.xml");
          screen.addCommand( this.cmdBack );
          screen.setCommandListener( this );
          this.display.setCurrent( screen );
          this.browserScreen = screen;
     }

     protected void pauseApp(){
          // TODO: Implement this method.
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
          // TODO: Implement this method.
     }

	public void commandAction(Command command, Displayable displayable) {
		//#debug
		System.out.println("browserMidlet, cmd=" + command.getLabel() );

		if (this.browserScreen.handleCommand(command)) {
			return;
		}

		if (command == this.cmdBack) {
			this.browserScreen.goBack();
		}
		
	}

	public void commandAction(Command command, Item item)
	{
		if (command == RssTagHandler.CMD_RSS_ITEM_SELECT) {
			RssItem rssItem = (RssItem) item.getAttribute(RssTagHandler.ATTR_RSS_ITEM);

			if (rssItem != null) {
				//#style rssItemView
				Alert alert = new Alert( rssItem.getTitle(), rssItem.getDescription(), null, AlertType.ERROR );
				alert.setTimeout(Alert.FOREVER);
				this.display.setCurrent(alert);
			}
		}
		else if (command == HtmlTagHandler.CMD_LINK) {
			this.browserScreen.handleCommand(command);
		}
		else {
			System.err.println("Unhandled command " + command);
		}
	}
}