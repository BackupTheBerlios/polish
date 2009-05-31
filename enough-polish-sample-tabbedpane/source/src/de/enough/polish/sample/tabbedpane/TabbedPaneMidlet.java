package de.enough.polish.sample.tabbedpane;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.browser.html.HtmlBrowser;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.TabbedFormListener;
import de.enough.polish.ui.TabListener;
import de.enough.polish.ui.TabbedPane;
import de.enough.polish.ui.UiAccess;

/**
 * <p>Demonstrates a simple browser app using the HtmlBrowser component of J2ME Polish.</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TabbedPaneMidlet 
extends MIDlet
implements CommandListener, TabListener, TabbedFormListener
{
	
	private Command cmdBack = new Command("Back", Command.BACK, 9);
	private Command cmdExit = new Command("Exit", Command.EXIT, 10 );
	private TabbedPane tabbedPane;
	private List screenMainMenu;

     protected void startApp() throws MIDletStateChangeException{
          Display display = Display.getDisplay( this );
          if (this.tabbedPane == null) {
        	  init();
          }
          display.setCurrent( this.tabbedPane );
     }

     private void init() {
    	 //#style tabbedPane
    	 this.tabbedPane = new TabbedPane(null);
    	 this.tabbedPane.addTabListener(this);
    	 this.tabbedPane.setTabbedFormListener(this);
    	 
         //#style mainMenuScreen
         Form form = new Form(null);
         try {
        	 Image img = Image.createImage("/map.png");
        	 form.append(img);
         } catch (Exception e) {
        	 //#debug error
        	 System.out.println("unable to load image" + e);
         }
         form.addCommand( new Command("New Message", Command.ITEM, 2));
         form.addCommand( new Command("New Contact", Command.ITEM, 3));
         form.addCommand( new Command("Back", Command.BACK, 2));
        
         //#style mapTitle
         ChoiceGroup group = new ChoiceGroup(null, ChoiceGroup.MULTIPLE);
         for (int i=0; i<7; i++) {
        	 //#style mapTitleItem
        	 group.append(null, null);
         }
         UiAccess.setTitle(form, group);
         
         String[] names = {"Menu", null, "Inbox", "Settings"};
         for (int i=0; i<4; i++) {
        	 if (i == 1) {
                 //#style tabIcon
                 this.tabbedPane.addTab(form, null, "Contacts");

        	 } else {
        		 String name = names[i];
		         //#style mainMenuScreen
		         List list = new List(name, List.IMPLICIT);
		         list.setCommandListener(this);
	        	 if (i == 0) {
		        	 //#style mainMenuItemAnimated
		        	 list.append( "New Message", null);		        		 
		        	 //#style mainMenuItemAnimated
		        	 list.append( "Contacts", null);		        		 
		        	 //#style mainMenuItemAnimated
		        	 list.append( "Inbox", null);		        		 
		        	 //#style mainMenuItemAnimated
		        	 list.append( "Outbox", null);		        		 
	        	 } else {
			         for (int j = 0; j < 10; j++) {
			        	 //#style mainMenuItem
			        	 list.append( name + " " + j, null);
			         }
	        	 }
		         //#style tabIcon
		         this.tabbedPane.addTab(list, null, name);
		         if (i == 0) {
		        	 this.screenMainMenu = list;
		         }
        	 }
         }
         
	}

	protected void pauseApp(){
          // ignore
     }

     protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
          // just exit
     }

	public void commandAction(Command cmd, Displayable disp) {
	    //#debug
		System.out.println("TabbedPaneMidlet.commandAction for cmd=" + cmd.getLabel() );
		if (disp == this.screenMainMenu) {
			int selectedIndex = this.screenMainMenu.getSelectedIndex();
			String text = this.screenMainMenu.getString( selectedIndex );
			//#style screenMessage
			Form form = new Form("Thanks");
			form.append("You have selected " + text + ".\nThank you.");
			form.addCommand(this.cmdBack);
			form.setCommandListener(this);
			if (selectedIndex < 2) {
				this.tabbedPane.setCurrentTab(form);
			} else {
				// set a new tab icon:
				//#style customTabIcon
				this.tabbedPane.setCurrentTab(form, null, "Changed");
			}
		} else  if (cmd == this.cmdBack) {
			// currently only possible from subscreen of main menu:
			int selectedIndex = this.screenMainMenu.getSelectedIndex();
			if (selectedIndex >= 2) {
				// revert the tab icon:
				//#style tabIcon
				this.tabbedPane.setCurrentTab( this.screenMainMenu, null, "Menu" );
			} else {
				this.tabbedPane.setCurrentTab( this.screenMainMenu );
			}
		} else if (cmd == this.cmdExit) {
			//destroyApp( true );
			notifyDestroyed();
		}
	}

	public void tabChangeEvent(Screen tab) {
		System.out.println("tabChangeEvent: " + tab);		
	}

	public void notifyTabChangeCompleted(int oldTabIndex, int newTabIndex) {
		System.out.println("notifyTabChangeCompleted( old=" + oldTabIndex + ", new=" + newTabIndex + ")");
	}

	public boolean notifyTabChangeRequested(int oldTabIndex, int newTabIndex) {
		System.out.println("notifyTabChangeRequested( old=" + oldTabIndex + ", new=" + newTabIndex + ")");
		return true;
	}


}