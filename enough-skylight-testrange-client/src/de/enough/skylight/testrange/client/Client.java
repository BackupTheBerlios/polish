package de.enough.skylight.testrange.client;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.Display;
import de.enough.skylight.renderer.node.NodeHandlerDirectory;
import de.enough.skylight.renderer.node.handler.skylight.PropertyHandler;

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
		
		//#if skylight.testrange.type == html
		NodeHandlerDirectory.getInstance().addHtmlHandlers();
		//#elif skylight.testrange.type == rss
		NodeHandlerDirectory.getInstance().addRssHandlers();
		//#endif
		
		NodeHandlerDirectory.getInstance().addHandler(new PropertyHandler());
		
		Browser browser = new Browser(url, Display.getDisplay(this));
		
		Display.getDisplay(this).setCurrent(browser);
	}
	
}
