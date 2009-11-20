package de.enough.polish.skylight.test;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Form;
import de.enough.skylight.renderer.Browser;

public class Test extends MIDlet{

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {}

	protected void pauseApp() {}

	protected void startApp() throws MIDletStateChangeException {
		Form form = new Form("Skylight");
		
		Browser browserItem = new Browser();
		form.append(browserItem);
		Display.getDisplay(this).setCurrent(form);
		
		browserItem.setUrl("file:///appointments.html");
	}

}
