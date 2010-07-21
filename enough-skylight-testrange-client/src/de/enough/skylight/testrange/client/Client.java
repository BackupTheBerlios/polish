package de.enough.skylight.testrange.client;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.Display;
import de.enough.skylight.renderer.node.NodeHandlerDirectory;

public class Client extends MIDlet{
	
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {}

	protected void pauseApp() {}

	protected void startApp() throws MIDletStateChangeException {
		String url = null;
		
		//#if skylight.testrange.url:defined
		//#= url = "${skylight.testrange.url}";
		//#endif
		
		if(url == null) {
			//#debug error
			System.out.println("url is not set, please set skylight.testrange.url in your user.properties");
			return;
		}
		
		NodeHandlerDirectory.getInstance().addHtmlHandlers();
		Browser browser = new Browser(Display.getDisplay(this));
		browser.displayPage(url);
		
		Display.getDisplay(this).setCurrent(browser);
	}
	
}
