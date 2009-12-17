package de.enough.polish.sample.rss;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.browser.html.FormListener;
import de.enough.polish.browser.protocols.ExternalProtocolHandler;
import de.enough.polish.browser.rss.RssBrowser;
import de.enough.polish.browser.rss.RssItem;
import de.enough.polish.browser.rss.RssTagHandler;
import de.enough.polish.event.ThreadedCommandListener;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.ui.splash.ApplicationInitializer;
import de.enough.polish.ui.splash.InitializerSplashScreen;

/**
 * <p>Shows RSS news feeds</p>
 *
 * <p>Copyright Enough Software 2007 - 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RssMidlet 
extends MIDlet
implements CommandListener, ApplicationInitializer
{
	private static final Command CMD_MAIN_MENU = new Command("Main Menu", Command.BACK, 5 );
	private static final Command CMD_BACK = new Command("Back", Command.BACK, 5 );
	private static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 10);
	//private static final Command CMD_GO = new Command("Go", Command.OK, 2 );

	private Display display;
	private Form browserScreen;
	private RssBrowser rssBrowser;
	private List mainMenu;
	private String defaultRssUrl = "http://www.digg.com/rss/containerscience.xml";
	private Form settingsForm;
	private StylingRssHandler rssTagHandler;

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


	/* (non-Javadoc)
	 * @see de.enough.polish.ui.splash.ApplicationInitializer#initApp()
	 */
	public Displayable initApp() {
		//#style screenMain
		List menu = new List("News Cup", Choice.IMPLICIT);
		//#style itemMain
		menu.append("Quick Launch", null);
		//#style itemMain
		menu.append("Settings", null);
		//#style itemMain
		menu.append("About", null);
		//#style itemMain
		menu.append("Exit", null);
		menu.addCommand( CMD_EXIT );
		menu.setCommandListener(this);
		this.mainMenu = menu;

		//#style rssBrowserForm
		this.browserScreen = new Form("News");
		//#style rssBrowser
		this.rssBrowser = new RssBrowser();
		StylingRssHandler handler = new StylingRssHandler(this.rssBrowser);
		this.rssBrowser.setRssTagHandler(handler);
		//this.rssBrowser.addProtocolHandler("http", new ExternalProtocolHandler("http", this ));
		this.rssTagHandler = handler;

		this.browserScreen.append( this.rssBrowser );
		this.browserScreen.addCommand(CMD_BACK);
		this.browserScreen.addCommand(CMD_EXIT);
		this.browserScreen.addCommand(CMD_MAIN_MENU);
		this.browserScreen.setCommandListener( new ThreadedCommandListener(this) );
		return menu;
	}

	protected void pauseApp(){
		// ignore
	}

	protected void exit() {
		if (this.rssTagHandler != null) {
			try {
				this.rssTagHandler.saveVisitedUrls();
			} catch (IOException e) {
				//#debug error
				System.out.println("Unable to save visited URLs" + e);
			}
		}
		notifyDestroyed();
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
		// nothing to clean up
	}

	public void commandAction(Command command, Displayable displayable) {
		//#debug
		System.out.println("commandAction: cmd=" + command.getLabel() );
		if (displayable == this.mainMenu) {
			if (command == CMD_EXIT) {
				exit();
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
					exit();
					break;
				}
			}
		} else if (displayable == this.browserScreen){
			if (this.rssBrowser.handleCommand(command)) {
				return;
			}
			if (command == CMD_BACK) {
				if (this.rssBrowser.canGoBack()) {
					this.rssBrowser.goBack();
				} else {
					this.rssBrowser.clearHistory();
					this.display.setCurrent( this.mainMenu );
				}
			} else if (command == CMD_MAIN_MENU) {
				this.rssBrowser.clearHistory();
				this.display.setCurrent( this.mainMenu );
			} else if (command == RssTagHandler.CMD_RSS_ITEM_SELECT || command.getLabel().equals("Select")) {
				Item item = UiAccess.cast( this.rssBrowser.getFocusedItem() );
				RssItem rssItem = (RssItem) UiAccess.getAttribute(item, RssItem.ATTRIBUTE_KEY);
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
			exit();
		}
	}

//	public void commandAction(Command command, Item item)
//	{
//	//#debug
//	System.out.println("commandAction: cmd=" + command.getLabel() + " for item " + item);
//	if (command == RssTagHandler.CMD_RSS_ITEM_SELECT) {
//	RssItem rssItem = (RssItem) UiAccess.getAttribute(item, RssTagHandler.ATTR_RSS_ITEM);
//	showRssNewsItem( rssItem );
//	}
//	else {
//	//#debug warn
//	System.err.println("Unhandled command " + command);
//	}
//	}

	private void showRssNewsItem( RssItem rssItem ) {
		if (rssItem != null) {
			//#debug
			System.out.println("showing " + rssItem.getTitle());
			//#style rssDescriptionAlert
			Alert alert = new Alert( rssItem.getTitle(), rssItem.getDescription(), null, null );
			alert.setTimeout(Alert.FOREVER);
			this.display.setCurrent(alert);
		}
	}

	/**
	 * Shows the about screen
	 */
	private void showAbout() {
		//#style aboutAlert
		Alert alert = new Alert("About", "Enough Software (c) 2010\nYou can use this application as a basis for your own apps.\nPowered by J2ME Polish.", null, null);
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
	 * Shows the settings screen
	 */
	private void showSettings() {
		if (this.settingsForm == null) {
			//#style screenSettings
			Form form = new Form("Settings");
			//#style updateIntervalGroup
			ChoiceGroup updateInterval  = new ChoiceGroup("Update Interval: ", ChoiceGroup.EXCLUSIVE);
			//#style updateIntervalItem
			updateInterval.append("never", null);
			//#style updateIntervalItem
			updateInterval.append("5m", null);
			//#style updateIntervalItem
			updateInterval.append("15m", null);
			//#style updateIntervalItem
			updateInterval.append("30m", null);
			//#style updateIntervalItem
			updateInterval.append("1h", null);
			//#style updateIntervalItem
			updateInterval.append("2h", null);
			//#style updateIntervalItem
			updateInterval.append("daily", null);
			//#style updateIntervalItem
			updateInterval.append("weekly", null);
			//#style updateIntervalItem
			updateInterval.append("monthly", null);
			updateInterval.setSelectedIndex(0, true);
			form.append( updateInterval );
			//#style volumeGauge
			Gauge gauge = new Gauge( "Volume: ", true, 100, 20 );
			form.append( gauge );
//			//#style itemInput
//			TextField textField = new TextField("Default URL: ", "http://", 80, TextField.URL );
//			form.append(textField);
			form.setCommandListener( this );
			form.addCommand( CMD_BACK );
			//form.addCommand( CMD_GO );
			this.settingsForm = form;
		}
		this.display.setCurrent( this.settingsForm );
	}

	/**
	 * @param url the URL that should be shown
	 */
	private void showRssBrowser(String url) {
		this.rssBrowser.go(url);
		String title = url.substring( "http://".length() );
		//#style rssBrowserTitle
		this.browserScreen.setTitle( title );
		this.display.setCurrent( this.browserScreen );
	}

	/**
	 * @return the URL of the default RSS feed
	 */
	private String getDefaultRssUrl() {
		return this.defaultRssUrl ;
	}



}