package de.enough.polish.sample.rssbrowser;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class BrowserMidlet 
extends MIDlet
implements CommandListener
{
	
	private Command cmdBack = new Command("Back", Command.SCREEN, Command.BACK );
	private BrowserScreen browserScreen;

     protected void startApp() throws MIDletStateChangeException{
          Display display = Display.getDisplay( this );
          //#style browserScreen
          BrowserScreen screen = new BrowserScreen("BrowserItem");
          screen.getURL("http://www.digg.com/rss/containerscience.xml");
          screen.addCommand( this.cmdBack );
          screen.setCommandListener( this );
          display.setCurrent( screen );
          this.browserScreen = screen;
     }

     protected void pauseApp(){
          // TODO: Implement this method.
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
          // TODO: Implement this method.
     }

	public void commandAction(Command cmd, Displayable arg1) {
    //#debug
		System.out.println("browserMidlet, cmd=" + cmd.getLabel() );
    if (this.browserScreen.handleCommand(cmd))
    {
      return;
    }
		if (cmd == this.cmdBack) {
			this.browserScreen.goBack();
		}
		
	}

}