/*
 * Created on 29-Dec-2005 at 14:19:00.
 * 
 * Copyright (c) 2004, 2005 Robert Virkus / Enough Software
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
package de.enough.polish.runtime.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import de.enough.polish.runtime.DisplayChangeListener;
import de.enough.polish.runtime.Simulation;
import de.enough.polish.runtime.SimulationDevice;
import de.enough.polish.runtime.SimulationManager;
import de.enough.polish.runtime.SimulationPanel;
import de.enough.polish.runtime.overlays.HoverOverlay;
import de.enough.polish.runtime.overlays.SelectionOverlay;
import de.enough.polish.swing.SwingApplication;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.ChoiceGroup;
import de.enough.polish.ui.Form;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.ui.backgrounds.SimpleBackground;

/**
 * <p>Provides a GUI for running simualations.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>
 * <pre>
 * history
 *        29-Dec-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SwingSimulator
extends SwingApplication
implements DisplayChangeListener
{
	
	private final SimulationManager manager;
	private Simulation simulation;


	/**
	 * @param configuration
	 * @param systemExitOnQuit
	 * @throws Exception
	 * 
	 */
	public SwingSimulator( Map configuration, boolean systemExitOnQuit )
	throws Exception
	{
		super("J2ME Polish Simulator", systemExitOnQuit );
		this.manager = new SimulationManager( configuration );
		String device = (String) configuration.get("device");
		if (device == null) {
			device = System.getProperty("device");
			if (device == null) {
				device = "Nokia/6630";
			}
		}
		String midletClass = (String) configuration.get("midlet");
		if (midletClass == null) {
			midletClass = System.getProperty("midlet");
			if (midletClass == null) {
				throw new IllegalStateException("no midlet defined");
			}
		}
		this.simulation = this.manager.loadSimulation(device, midletClass);
		this.simulation.setDisplayChangeListener( this );
		this.simulation.simulateGameActionEvent( Canvas.DOWN );
		Container contentPane = getContentPane();
		contentPane.add( this.simulation );
		pack();
	}
	
	public SwingSimulator()
	throws Exception
	{
		super("J2ME Polish Simulator", true );
		this.manager = null;
		Container contentPane = getContentPane();
		Simulation simulation = new Simulation( new SimulationDevice() );
		Form form = new Form("Hello World");
		form.append("first line");
		form.append("second line");
		form.append("third line");
		form.append("fourth line");
		ChoiceGroup group = new ChoiceGroup( "choice:", ChoiceGroup.MULTIPLE );
		group.append( "first choice", null );
		group.append( "second choice", null );
		form.append( group );
		form.addCommand( new Command("Command", Command.SCREEN, 2 ));
		form._callShowNotify();
		Background background = new SimpleBackground( 0xffff00 );
		UiAccess.setBackground(form, background);
		simulation.setCurrent( form );
		simulation.addOverlay( new SelectionOverlay() );
		simulation.addOverlay( new HoverOverlay() );
		contentPane.add( simulation );
		pack();
	}

	public static void main(String[] args) 
	throws Exception 
	{
		if (args.length > 0) {
			SwingSimulator simulator = new SwingSimulator();
			simulator.setVisible(true);			
			return;
		}
		Map configuration = new HashMap();
		String wtkHome = System.getProperty("wtk.home");
		if (wtkHome == null) {
			wtkHome = "/home/enough/dev/WTK2.2";
		}
		configuration.put("wtk.home",  wtkHome );
		String polishHome = System.getProperty("polish.home");
		if (polishHome == null) {
			polishHome = "/home/enough/J2ME-Polish";
		}
		configuration.put("polish.home", polishHome );
		SwingSimulator simulator = new SwingSimulator( configuration, true );
		simulator.setVisible(true);
	}

	public void displayChanged(Displayable displayable) {
		MIDlet midlet = this.simulation.getMIDlet();
		// TODO use reflection for getting to know the current screen		
	}
}
