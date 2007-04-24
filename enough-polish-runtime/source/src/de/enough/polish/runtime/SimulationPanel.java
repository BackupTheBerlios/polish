/*
 * Created on 29-Mar-2005 at 15:18:46.
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
package de.enough.polish.runtime;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.microedition.lcdui.Canvas;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import de.enough.polish.swing.ImageAreaButton;

/**
 * <p>Contains the plain vanilla simulation along with buttons for controlling the application.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        29-Mar-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SimulationPanel 
extends JPanel
implements ActionListener
{
	protected final static int LEFT_SOFT_BUTTON = 0;
	protected final static int RIGHT_SOFT_BUTTON = 1;
	protected final static int MODE_CHANGE_BUTTON = 2;
	protected final static int CLEAR_BUTTON = 3;
	protected final static int FIRE_BUTTON = 4;
	protected final static int UP_BUTTON = 5;
	protected final static int LEFT_BUTTON = 6;
	protected final static int DOWN_BUTTON = 7;
	protected final static int RIGHT_BUTTON = 8;
	
	private Simulation simulation;
	private ImageAreaButton phoneControl;
	private String controlName;
	private Control control;
	private final JLayeredPane view;
	

	/**
	 * @param simulation
	 * 
	 */
	public SimulationPanel( Simulation simulation ) {
		super( new BorderLayout() );
		this.view = new JLayeredPane();
		setSimulation( simulation );
		add( this.view, BorderLayout.CENTER );
		//add( simulation, BorderLayout.CENTER );
		//add( this.control, BorderLayout.SOUTH );
		
		/*
		add( simulation, BorderLayout.CENTER );
		
		// create control buttons:
		Image image = SwingUtil.loadIcon("resources/phonecontrol.png", SimulationPanel.class );
		ImageIcon icon = new ImageIcon( image );
		Rectangle[] areas = new Rectangle[]{
				new Rectangle( 6, 0, 56, 27  ), // left soft button
				new Rectangle( 113, 0, 54, 27  ), // right soft button
				new Rectangle( 2, 71, 60, 34  ), // mode change button
				new Rectangle( 114, 68, 52, 35  ), // clear button
				new Rectangle( 76, 41, 21, 18  ), // fire 
				new Rectangle( 73, 21, 27, 18  ), // up 
				new Rectangle( 56, 38, 20, 26  ), // left 
				new Rectangle( 68, 64, 34, 17  ), // down 
				new Rectangle( 100, 36, 18, 29  ) // right
		};
		this.phoneControl = new ImageAreaButton( icon, areas, this );
		add( this.phoneControl, BorderLayout.SOUTH );
		*/
		/*
		JPanel leftPanel = new JPanel( new BorderLayout() );
		leftPanel.add( this.leftSoftButton, BorderLayout.NORTH );
		leftPanel.add( this.changeModeButton, BorderLayout.SOUTH );
		JPanel rightPanel = new JPanel( new BorderLayout() );
		rightPanel.add( this.rightSoftButton, BorderLayout.NORTH );
		rightPanel.add( this.clearButton, BorderLayout.SOUTH );
		JPanel controlPanel = new JPanel( new BorderLayout() );
		controlPanel.add( this.scrollButton, BorderLayout.CENTER );
		controlPanel.add( leftPanel, BorderLayout.WEST );
		controlPanel.add( rightPanel, BorderLayout.EAST );
		add( controlPanel, BorderLayout.SOUTH );
		*/
		
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		this.simulation.requestFocusInWindow();
		Object source = e.getSource();
		if (source == this.phoneControl ) {
			int areaIndex = this.phoneControl.getSelectedArea();
			switch (areaIndex) {
				case LEFT_SOFT_BUTTON: 
					this.simulation.simulateLeftSoftKeyPressedEvent();
					break;
				case RIGHT_SOFT_BUTTON: 
					this.simulation.simulateRightSoftKeyPressedEvent();
					break;
				case MODE_CHANGE_BUTTON: 
					this.simulation.simulateChangeInputModeKeyPressedEvent();
					break;
				case CLEAR_BUTTON: 
					this.simulation.simulateClearKeyPressedEvent();
					break;
				case FIRE_BUTTON: 
					this.simulation.simulateGameActionEvent( Canvas.FIRE );
					break;
				case UP_BUTTON: 
					this.simulation.simulateGameActionEvent( Canvas.UP );
					break;
				case LEFT_BUTTON: 
					this.simulation.simulateGameActionEvent( Canvas.LEFT );
					break;
				case DOWN_BUTTON: 
					this.simulation.simulateGameActionEvent( Canvas.DOWN );
					break;
				case RIGHT_BUTTON: 
					this.simulation.simulateGameActionEvent( Canvas.RIGHT );
					break;
				default:
					System.out.println("Unknown phone control item pressed: " + areaIndex );
			}
		}
	}

	public Simulation getSimulation() {
		return this.simulation;
	}


	/**
	 * @param sim
	 */
	public void setSimulation(Simulation sim) {
		if (sim == null) {
			System.out.println("SimulationPanel: unable to add [null] simulation.");
			return;
		}
		if (this.simulation != null) {
			this.view.remove( this.simulation );
		}
		if (this.control != null) {
			this.view.remove( this.control );
		}
		this.control = null;
		this.controlName = sim.getDevice().getCapability("polish.Emulator.control");
		if ( this.controlName != null ) {
			//System.out.println("SimulationPanel: trying to load control [" + this.controlName + "]");
			try {
				this.control = new ConfiguredControl( sim, "/resources/" + this.controlName + ".ctrl");
				//System.out.println("read control successfully");
			} catch (Exception e) {
				this.control = new DefaultControl( sim );
				e.printStackTrace();
			}
		} else {
			this.control = new DefaultControl( sim );
		}
		Dimension size = new Dimension( this.control.controlWidth, this.control.controlHeight );
		this.view.setPreferredSize( size );
		this.view.setMinimumSize( size );
		this.control.setBounds( 0, 0, this.control.controlWidth, this.control.controlHeight );
		sim.setBounds( this.control.screenX, this.control.screenY, sim.getCanvasWidth(), sim.getCanvasHeight() );
		
		this.view.add( this.control, new Integer(0) );
		this.view.add( sim, new Integer(1) );
		this.simulation = sim;
	}

}
