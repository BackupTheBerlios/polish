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
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import de.enough.polish.runtime.DisplayChangeListener;
import de.enough.polish.runtime.Simulation;
import de.enough.polish.runtime.SimulationManager;
import de.enough.polish.swing.SwingApplication;

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
		//this.simulation = this.manager.loadSimulation("Nokia/6600", "de.enough.polish.demo.DemoMidlet");
		this.simulation = this.manager.loadSimulation("Nokia/6600", "de.navigon.lithium3.Lithium3Midlet");
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
		contentPane.setLayout( new BorderLayout() );
		JLabel label = new JLabel("title");
		contentPane.add( label, BorderLayout.NORTH );
		JLayeredPane view = new JLayeredPane();
		view.setPreferredSize( new Dimension( 200, 200));
		label = new JLabel("first");
		label.setOpaque(true);
		label.setBackground( Color.WHITE );
		label.setBounds( 0, 50, 100, 100 );
		view.add( label, new Integer(0) );
		label = new JLabel("second");
		label.setOpaque(true);
		label.setBackground( Color.YELLOW );
		label.setBounds( 20, 40, 100, 100 );
		view.add( label, new Integer(1) );
		contentPane.add( view, BorderLayout.CENTER );
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
