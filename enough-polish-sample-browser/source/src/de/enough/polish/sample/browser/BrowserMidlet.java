package de.enough.polish.sample.browser;

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
	
	private Command cmdBack = new Command("Back", Command.BACK, 9);
	private Command cmdExit = new Command("Exit", Command.EXIT, 10 );
	private BrowserScreen browserScreen;

     protected void startApp() throws MIDletStateChangeException{
          Display display = Display.getDisplay( this );
          if (this.browserScreen == null) {
	          //#style browserScreen
	          BrowserScreen screen = new BrowserScreen("HTML Browser");
	          screen.go( "resource://index.html");
	          screen.addCommand( this.cmdBack );
	          screen.addCommand( this.cmdExit );
	          screen.setCommandListener( this );
	          this.browserScreen = screen;
          }
          display.setCurrent( this.browserScreen );
     }

     protected void pauseApp(){
          // ignore
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
          // just exit
     }

	public void commandAction(Command cmd, Displayable disp) {
	    //#debug
		System.out.println("BrowserMidlet.commandAction for cmd=" + cmd.getLabel() );
		if (cmd == this.cmdBack) {
			this.browserScreen.goBack();
		} else if (cmd == this.cmdExit) {
			//destroyApp( true );
			notifyDestroyed();
		}
		
	}

}