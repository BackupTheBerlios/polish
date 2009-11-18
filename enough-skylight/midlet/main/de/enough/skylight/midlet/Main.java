package de.enough.skylight.midlet;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.Display;
import de.enough.polish.ui.Form;

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
		Form form = new Form("HtmlContainer");
//		HtmlContainer htmlContainer = new HtmlContainer();
//		Document document = new DomParser().parseTree("html/test1.html");
//		htmlContainer.setDocument(document);
//		form.append(htmlContainer);
		Display.getDisplay(this).setCurrent(form);
	}

}
