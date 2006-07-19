/**
 * 
 */
package de.enough.sudoku;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.util.Locale;

/**
 * @author Simon Schmitt
 *
 */
public class Sudoku extends MIDlet implements javax.microedition.lcdui.CommandListener {
	private Display display;
	private List mainMenu;
	private Form aboutForm;
	private Form settingstForm;
	private Form highScoreForm;
	private Form helpForm;
	private Command exit;
	private Command cmdReturn;
	
	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {

		if (this.mainMenu==null){
			this.mainMenu = new List( "Sudoku Master", List.IMPLICIT );
			
			this.mainMenu.append( Locale.get( "menu.StartGame"), null);
			this.mainMenu.append( Locale.get( "menu.Highscore"), null);
			this.mainMenu.append( Locale.get( "menu.Settings"), null);
			this.mainMenu.append( Locale.get( "menu.About"), null);
			this.mainMenu.append( Locale.get( "menu.Help"), null);
			this.mainMenu.append( Locale.get( "menu.Exit"), null);
			
			this.mainMenu.setCommandListener(this);	
			Display.getDisplay(this).setCurrent(this.mainMenu);
			
			this.cmdReturn = new Command( Locale.get( "cmd.Ok" ), Command.BACK, 2 );
		}
		this.display = Display.getDisplay( this );
		this.display.setCurrent( this.mainMenu );
	}
	
	public void commandAction (Command cmd, Displayable screen){
		// check the commands
		if (cmd == this.exit) {
			notifyDestroyed();
		} else if (cmd == this.cmdReturn){
			// return to menu
			this.display.setCurrent(this.mainMenu);
		}
		
		//check the list
		if (cmd==List.SELECT_COMMAND && screen==this.mainMenu){
			switch (((List) screen).getSelectedIndex()) {
			case 0:
				// TODO: ask for dificulty level
				//this.gameScreen.start();
				//this.display.setCurrent( this.gameScreen );
				break;
			case 1: 
				// show the highscore
				showHighscore();
				break;
			case 2:
				// settings
				showSettings();
				break;
			case 3: 
				// about
				showAbout();
				break;
			case 4:
				// help
				showHelp();
				break;
			case 5:
				//exit
				// TODO ask before
				notifyDestroyed();
				break;
			}
			//System.out.println(""+((List) screen).getSelectedIndex());
		}
	}
	
	/**
	 * intialize if neccessary and show the settingsScreen 
	 */
	private void showHighscore() {
		// load the screen
		if (this.highScoreForm==null){			
			
			this.highScoreForm = new Form( Locale.get("title.Highscore") );
			this.highScoreForm.append( "all winners" );
			
			this.highScoreForm.addCommand( this.cmdReturn );
			this.highScoreForm.setCommandListener( this );
		}
		// show the highscore
		this.display.setCurrent( this.highScoreForm );
	}
	
	
	/**
	 * intialize if neccessary and show the settingsScreen 
	 */
	private void showSettings() {
		// load the screen
		if (this.settingstForm==null){			
			
			this.settingstForm = new Form( Locale.get("title.Settings") );
			this.settingstForm.append( "lots of cool settings" );
			
			this.settingstForm.addCommand( this.cmdReturn );
			this.settingstForm.setCommandListener( this );
		}
		// show the Settings
		this.display.setCurrent( this.settingstForm );
	}
	
	/**
	 * intialize if neccessary and show the about information 
	 */
	private void showAbout() {
		// load the about screen
		if (this.aboutForm==null){			
			///#style aboutScreen
			this.aboutForm = new Form( Locale.get("title.About") );
			//#style .aboutText, focused, default
			this.aboutForm.append( Locale.get("about.text"));
			
			this.aboutForm.addCommand( this.cmdReturn );
			this.aboutForm.setCommandListener( this );
		}
		// show the aboutscreen
		this.display.setCurrent( this.aboutForm );
	}
	
	/**
	 * intialize if neccessary and show the settingsScreen 
	 */
	private void showHelp() {
		// load the about screen
		if (this.helpForm==null){			
			
			this.helpForm = new Form( Locale.get("title.Help") );
			this.helpForm.append( Locale.get("help.text") );
			
			this.helpForm.addCommand( this.cmdReturn );
			this.helpForm.setCommandListener( this );
		}
		// show the aboutscreen
		this.display.setCurrent( this.helpForm );
	}
	
}
