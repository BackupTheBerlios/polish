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
	private static final Command CMD_BACK = new Command("Back", Command.SCREEN, 1 );
	private static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 10);

	private Display display;
	private BrowserScreen browserScreen;

     protected void startApp()
     	throws MIDletStateChangeException
     {
          this.display = Display.getDisplay(this);

          //#style browserScreen
          BrowserScreen screen = new BrowserScreen(this, "RssBrowser");
          screen.getURL("http://www.digg.com/rss/containerscience.xml");
          screen.addCommand(CMD_BACK);
          screen.addCommand(CMD_EXIT);
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

		if (command == CMD_BACK) {
			this.browserScreen.goBack();
		}
		else if (command == CMD_EXIT) {
			notifyDestroyed();
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