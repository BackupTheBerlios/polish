package de.enough.polish.sample.rssbrowser;

import de.enough.polish.browser.html.HtmlTagHandler;
import de.enough.polish.browser.rss.RssBrowser;
import de.enough.polish.browser.rss.RssItem;
import de.enough.polish.browser.rss.RssTagHandler;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.ItemCommandListener;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.ui.splash.ApplicationInitializer;
import de.enough.polish.ui.splash.InitializerSplashScreen;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class BrowserMidlet 
	extends MIDlet
	implements CommandListener, ItemCommandListener, ApplicationInitializer
{
	private static final Command CMD_BACK = new Command("Back", Command.SCREEN, 5 );
	private static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 10);
	private static final Command CMD_GO = new Command("Go", Command.OK, 2 );

	private Display display;
	private BrowserScreen browserScreen;
	private List mainMenu;
	private String defaultRssUrl = "http://www.digg.com/rss/containerscience.xml";
	private Form settingsForm;

     protected void startApp()
     	throws MIDletStateChangeException
     {
          this.display = Display.getDisplay(this);
          
          try {
	          Image splashImage = Image.createImage("/splash.png");
	          InitializerSplashScreen splashScreen = new InitializerSplashScreen(this.display, splashImage, 0xffffff, null, 0, this  );
	          this.display.setCurrent( splashScreen );
          } catch (Exception e) {
        	  //#debug error
        	  System.out.println("Unable to create splash screen" + e);
        	  Displayable disp = initApp();
        	  this.display.setCurrent( disp );
          }
     }

     protected void pauseApp(){
          // ignore
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
          // nothing to clean up
     }

	public void commandAction(Command command, Displayable displayable) {
		// #debug
		System.out.println("commandAction: cmd=" + command.getLabel() );
		if (displayable == this.mainMenu) {
			if (command == CMD_EXIT) {
				notifyDestroyed();
			} else {
				int index = this.mainMenu.getSelectedIndex();
				switch (index) {
				case 0:
					showRssBrowser( getDefaultRssUrl() );
					break;
				case 1:
					showSettings();
					break;
				case 2:
					showAbout();
					break;
				case 3:
					notifyDestroyed();
					break;
				}
			}
		} else if (displayable == this.browserScreen){
			if (this.browserScreen.processCommand(command)) {
				return;
			}
	
			if (command == CMD_BACK) {
				if (this.browserScreen.canGoBack()) {
					this.browserScreen.goBack();
				} else {
					this.display.setCurrent( this.mainMenu );
				}
			} else if (command == RssTagHandler.CMD_RSS_ITEM_SELECT || command.getLabel().equals("Select")) {
				Item item = UiAccess.getFocusedItem(this.browserScreen);
				if ( ((Object)item) instanceof RssBrowser) {
					RssBrowser browser =(RssBrowser)((Object)item);
					item = browser.getFocusedItem();
				}
				RssItem rssItem = (RssItem) item.getAttribute(RssTagHandler.ATTR_RSS_ITEM);
				showRssNewsItem( rssItem );
			}
		} else if (displayable == this.settingsForm) {
			if (command == CMD_BACK) {
				this.display.setCurrent( this.mainMenu );
			} else {
				TextField textField = (TextField) UiAccess.getFocusedItem(this.settingsForm);
				String url = textField.getString();
				if (!url.startsWith("http://")) {
					url = "http://" + url;
				}
				showRssBrowser(url);
			}
		}
		if (command == CMD_EXIT) {
			notifyDestroyed();
		}
	}
	
	public void commandAction(Command command, Item item)
	{
		System.out.println("commandAction: cmd=" + command.getLabel() + " for item " + item);
		if (command == RssTagHandler.CMD_RSS_ITEM_SELECT) {
			RssItem rssItem = (RssItem) item.getAttribute(RssTagHandler.ATTR_RSS_ITEM);
			showRssNewsItem( rssItem );
		}
		else if (command == HtmlTagHandler.CMD_LINK) {
			this.browserScreen.processCommand(command);
		}
		else {
			//#debug warn
			System.err.println("Unhandled command " + command);
		}
	}
	
	private void showRssNewsItem( RssItem rssItem ) {
		if (rssItem != null) {
			System.out.println("showing " + rssItem.getTitle());
			//#style rssDescriptionAlert
			Alert alert = new Alert( rssItem.getTitle(), rssItem.getDescription(), null, null );
			alert.setTimeout(Alert.FOREVER);
			this.display.setCurrent(alert);
		}
	}

	/**
	 * 
	 */
	private void showAbout() {
		//#style aboutAlert
        Alert alert = new Alert("About", "Enough Software (c) 2007", null, null);
        alert.setTimeout(Alert.FOREVER);
        try {
	          Image splashImage = Image.createImage("/splash.png");
	          alert.setImage(splashImage);
        } catch (Exception e) {
        	//#debug error
        	System.out.println("Unable to load splash image" + e );
        }
        this.display.setCurrent( alert );
	}

	/**
	 * 
	 */
	private void showSettings() {
		if (this.settingsForm == null) {
			//#style myList
			Form form = new Form("Enter URL");
			//#style myTextField
			TextField textField = new TextField("URL: ", "http://", 80, TextField.ANY );
			form.append(textField);
			form.setCommandListener( this );
			form.addCommand( CMD_BACK );
			form.addCommand( CMD_GO );
			this.settingsForm = form;
		}
		this.display.setCurrent( this.settingsForm );
	}

	/**
	 * @param defaultRssUrl2
	 */
	private void showRssBrowser(String url) {
		this.browserScreen.getURL(url);
		String title = url.substring( "http://".length() );
		//#style rssBrowserTitle
		this.browserScreen.setTitle( title );
		this.display.setCurrent( this.browserScreen );
	}

	/**
	 * @return
	 */
	private String getDefaultRssUrl() {
		return this.defaultRssUrl ;
	}

	

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.splash.ApplicationInitializer#initApp()
	 */
	public Displayable initApp() {
		 //#style myList
        List menu = new List("RSS Menu", Choice.IMPLICIT);
        //#style myChoiceItem
        menu.append("Quick Launch", null);
        //#style myChoiceItem
        menu.append("Go to URL", null);
        //#style myChoiceItem
        menu.append("About", null);
        //#style myChoiceItem
        menu.append("Exit", null);
        menu.addCommand( CMD_EXIT );
        menu.setCommandListener(this);
        this.mainMenu = menu;
        
        //#style rssBrowserForm
        BrowserScreen screen = new BrowserScreen(this, null);
        screen.addCommand(CMD_BACK);
        screen.addCommand(CMD_EXIT);
        screen.setCommandListener( this );
        this.browserScreen = screen;
        
        try { Thread.sleep(1000); } catch (Exception e) { }
        
		return menu;
	}
}