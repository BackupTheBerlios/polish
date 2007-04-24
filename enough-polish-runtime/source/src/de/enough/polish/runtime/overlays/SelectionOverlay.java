/*
 * Created on Apr 20, 2007 at 10:23:19 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.runtime.overlays;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import de.enough.polish.runtime.Simulation;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;

/**
 * <p>Highlights the currently selected item or screen.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 20, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SelectionOverlay 
extends FadingOverlay
implements MouseListener
{

	/**
	 * Creates a selection overlay with the default color ( 0x88ff0000 )
	 */
	public SelectionOverlay() {
		this( 0x88ff0000 );
	}
	
	/**
	 * Creates a new selection overlay with the specified color
	 * @param argbColor the color in0xAARRGGBB format
	 */
	public SelectionOverlay( int argbColor ) {
		super( argbColor );
		setTranslucencyIncreaseDelta( 5 );
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.runtime.SimulationOverlay#registerSimulation(de.enough.polish.runtime.Simulation)
	 */
	public void registerSimulation(Simulation sim) {
		super.registerSimulation(sim);
		sim.addMouseListener( this );
		this.isStopRequested = false;
		Thread thread = new Thread( this );
		thread.start();
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.runtime.SimulationOverlay#deregisterSimulation(de.enough.polish.runtime.Simulation)
	 */
	public void deregisterSimulation(Simulation sim) {
		super.deregisterSimulation(sim);
		sim.removeMouseListener( this );
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent event) {
		if (this.simulation.getCurrentDisplayable() instanceof Screen) {
			Screen screen = (Screen) this.simulation.getCurrentDisplayable();
			int x =  event.getX();
			int y = event.getY();
			Item item = screen.getItemAt( x, y );
			if (item != null) {
				//System.out.println("SelectionOverlay: got item " + item + ", parent=" + item.getParent() );
				setSelectionArea( item.getAbsoluteX(), item.getAbsoluteY(), item.itemWidth, item.itemHeight );
			} else {
				setSelectionArea( 0, 0, screen.getWidth(), screen.getHeight() );
			}
		}

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent event) {
		if (!this.isSelected && this.selectedWidth != 0) {
			setSelectionArea(this.selectedX, this.selectedY, this.selectedWidth, this.selectedHeight);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent event) {
		// ignore
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent event) {
		// ignore
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent event) {
		// ignore
	}

}
