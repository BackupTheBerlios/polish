package de.enough.polish.sample.skylightapi;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Form;
import de.enough.skylight.renderer.BrowserItem;

public class Main extends MIDlet{

	@Override
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void pauseApp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void startApp() throws MIDletStateChangeException {
		System.out.println("Enter.");
		
		Form form = new Form("Skylight");
		
		BrowserItem browserItem = new BrowserItem();
		browserItem.setUrl("file:///page1.html");
		form.append(browserItem);
		Display.getDisplay(this).setCurrent(form);
		browserItem.render();
		
	}

}
