package de.enough.polish.drone.test;

import java.io.IOException;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.Locale;

//#ifdef polish.debugEnabled
import de.enough.polish.util.Debug;
//#endif
	
/**
 * <p>Shows a demonstration of the possibilities of J2ME Polish.</p>
 *
 * <p>Copyright Enough Software 2004 - 2008</p>

 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TestMidlet extends MIDlet implements CommandListener{
	
	Display display;
	Form form;
	
	Command cmdQuit = new Command("Quit",Command.EXIT,0);
	
	public TestMidlet() {}

	protected void startApp() throws MIDletStateChangeException {
		this.form = new Form("TestMidlet");
		
		Image image = null;
		try {
			image = Image.createImage("/girl.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//#style image
		ImageItem item = new ImageItem(null,image,0,"girl");
		this.form.append(item);
		this.form.append("Hello Android");
		this.form.addCommand(cmdQuit);
		this.form.setCommandListener(this);
		
		this.display = Display.getDisplay(this);
		this.display.setCurrent( this.form );
	}

	
	
	protected void pauseApp() {
		// ignore
	}
	
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// just quit
	}

	public void commandAction(Command cmd, Displayable disp) {
		if(cmd == cmdQuit)
		{
			notifyDestroyed();
		}
	}
}
