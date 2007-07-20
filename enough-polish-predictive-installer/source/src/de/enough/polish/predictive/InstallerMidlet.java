package de.enough.polish.predictive;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class InstallerMidlet 
extends MIDlet
implements CommandListener
{
	protected Form form;
	protected Command exitCommand = new Command( "Exit", Command.EXIT, 3 );
	protected Command cancelCommand = new Command( "Cancel", Command.CANCEL, 3 );
	
	protected Command yesCommand = new Command( "Yes", Command.OK, 0 );
	protected Command noCommand = new Command( "No", Command.CANCEL, 0 );
	
	protected StringItem intro = null;
	protected StringItem status = null;
	protected StringItem error = null;
	protected Gauge gauge = null;
	
	protected InstallerThread installer  = null;
	protected Thread thread = null;
	
	public InstallerMidlet() {
		//#style mainScreen
		this.form = new Form( "Predictive Setup");
		
		this.form.addCommand( this.cancelCommand );
		this.form.setCommandListener( this );
		
		this.intro = new StringItem("","Installing the dictionary used for predictive input. Please wait a moment ...");
		this.status = new StringItem("","");
		this.error = new StringItem("","");
		this.gauge = new Gauge("",true,100,0);

		//#style introItem
		this.form.append(intro);
		//#style gauge
		this.form.append(gauge);
		//#style statusItem
		this.form.append(status);
		//#style errorItem
		this.form.append(error);
		
		installer  = new InstallerThread(this);
		thread = new Thread(installer);
	}

     protected void startApp() throws MIDletStateChangeException{
    	 thread.start();
    	 Display.getDisplay( this ).setCurrent( this.form );
     }
     	
     protected void pauseApp(){
          // ignore
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
    	  // ignore
     }

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command cmd, Displayable disp) {
		if (cmd == this.cancelCommand) {
			Alert cancel = new Alert("Cancel");
			
			cancel.setString("Do you really want to cancel the installation ? This may result in unwanted effects.");
			cancel.addCommand(this.yesCommand);
			cancel.addCommand(this.noCommand);
			
			cancel.setCommandListener(this);
			
			installer.pause();
			Display.getDisplay(this).setCurrent(cancel);
		} else if (cmd == this.exitCommand) {
			notifyDestroyed();
		} 
		
		if(disp instanceof Alert)
		{
			if(cmd == this.yesCommand)
			{
				notifyDestroyed();
			} 
			else if(cmd == this.noCommand)
			{
				synchronized (installer){
					installer.notify();
				}
				
				Display.getDisplay(this).setCurrent(this.form);
			}
		}
		
	}
}