/*
 * Created on Mar 30, 2006 at 10:53:20 AM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.sample.framedform;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.util.IntHashMap;

/**
 * <p>Provides an example how to use the FramedForm</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 30, 2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FramedFormDemo 
extends MIDlet
implements ItemStateListener, CommandListener
{

	private static final IntHashMap COLOR_MAP = new IntHashMap();
	private static final int WHITE = 0;
	private static final int RED = 1;
	private static final int YELLOW = 2;
	private static final int GREEN = 3;
	private static final int BLUE = 4;
	private static final int BLACK = 5;
	static {
		COLOR_MAP.put(WHITE, new Integer( 0xFFFFFF ) );
		COLOR_MAP.put(RED, new Integer( 0xFF0000 ) );
		COLOR_MAP.put(YELLOW, new Integer( 0xFFFF00 ) );
		COLOR_MAP.put(GREEN, new Integer( 0x00FF00 ) );
		COLOR_MAP.put(BLUE, new Integer( 0x0000FF ) );
		COLOR_MAP.put(BLACK, new Integer( 0x000000 ) );
	}
	private GradientItem gradientItem;
	private ChoiceGroup topColorGroup;
	private ChoiceGroup middleColorGroup;
	private ChoiceGroup bottomColorGroup;
	
	private Command exitCmd = new Command("Exit", Command.EXIT, 1 );

	/**
	 * Creates a new MIDlet.
	 */
	public FramedFormDemo() {
		super();
		//#debug
		System.out.println("Creating FramedFormDemo MIDlet.");
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException {
		//#debug
		System.out.println("Starting FramedFormDemo MIDlet.");
		
		//#style gradientFramedForm
		FramedForm form = new FramedForm( "Framed Form" );
		this.gradientItem = new GradientItem( 20 );
		form.append( Graphics.RIGHT,  this.gradientItem );
		this.topColorGroup = createColorChoiceGroup("top: ");
		this.topColorGroup.setSelectedIndex(WHITE, true);
		form.append( this.topColorGroup );
		this.middleColorGroup = createColorChoiceGroup("middle: ");
		this.middleColorGroup.setSelectedIndex(YELLOW, true);
		form.append( this.middleColorGroup );
		this.bottomColorGroup = createColorChoiceGroup("bottom: ");
		this.bottomColorGroup.setSelectedIndex(BLUE, true);
		form.append( this.bottomColorGroup );
		
		form.addCommand( this.exitCmd );
		form.setItemStateListener( this );
		form.setCommandListener( this );
		
		Display display = Display.getDisplay( this );
		display.setCurrent( form );
		
		//#debug
		System.out.println("FramedFormDemo MIDlet is up and running.");

	}

	private ChoiceGroup createColorChoiceGroup(String label) {
		//#style colorChoiceGroup
		ChoiceGroup group = new ChoiceGroup( label, ChoiceGroup.EXCLUSIVE );
		//#style colorChoiceItem
		group.append( "white", null );
		//#style colorChoiceItem
		group.append( "red", null );
		//#style colorChoiceItem
		group.append( "yellow", null );
		//#style colorChoiceItem
		group.append( "green", null );
		//#style colorChoiceItem
		group.append( "blue", null );
		//#style colorChoiceItem
		group.append( "black", null );
		return group;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp() {
		// ignore
	}

	/* (non-Javadoc)
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		// nothing to free
	}

	public void itemStateChanged(Item item) {
		// this will be on of the color chooser groups:
		if (item instanceof ChoiceGroup) {
			int selectedIndex = ((ChoiceGroup) item).getSelectedIndex();
			int color = ((Integer) COLOR_MAP.get(selectedIndex) ).intValue();
			if ( item == this.topColorGroup ) {
				this.gradientItem.setTopColor( color );
			} else if ( item == this.middleColorGroup ) {
				this.gradientItem.setMidColor( color );
			} else {
				this.gradientItem.setBottomColor( color );
			}
		}
		
	}

	public void commandAction(Command cmd, Displayable disp) {
		// currently only the Exit command is present/supported
		notifyDestroyed();
	}

}
