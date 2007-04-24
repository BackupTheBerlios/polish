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
import java.awt.event.MouseMotionListener;

import javax.microedition.lcdui.Displayable;

import de.enough.polish.runtime.Simulation;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;

/**
 * <p>Highlights the item or screen that the mouse currently hovers about.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 20, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class HoverOverlay 
extends FadingOverlay
implements MouseListener, MouseMotionListener
{

	
	/**
	 * Creates a hover overlay with the default color ( 0x88ffffff )
	 */
	public HoverOverlay() {
		this( 0x88ffffff );
	}
	
	/**
	 * Creates a new hover overlay with the specified color
	 * @param argbColor the color in0xAARRGGBB format
	 */
	public HoverOverlay( int argbColor ) {
		super( argbColor );
	}

	
	/* (non-Javadoc)
	 * @see de.enough.polish.runtime.SimulationOverlay#registerSimulation(de.enough.polish.runtime.Simulation)
	 */
	public void registerSimulation(Simulation sim) {
		super.registerSimulation(sim);
		sim.addMouseListener(this);
		sim.addMouseMotionListener(this);
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.runtime.SimulationOverlay#deregisterSimulation(de.enough.polish.runtime.Simulation)
	 */
	public void deregisterSimulation(Simulation sim) {
		super.deregisterSimulation(sim);
		sim.removeMouseListener(this);
		sim.removeMouseMotionListener(this);
	}


	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent event) {
		// ignore
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent event) {
		setSelectionArea(0, 0, this.simulation.getCanvasWidth() - 1, this.simulation.getCanvasHeight() - 1);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent event) {
		clearSelectionArea();
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

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent event) {
		mouseMoved( event );
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent event) {
		
		// highlight currently selected item:
		Displayable displayable = this.simulation.getCurrentDisplayable();
		if (displayable instanceof Screen) {
			Screen screen  = (Screen) displayable;
			int x = event.getX();
			int y = event.getY();
			Item item = screen.getItemAt(x, y);
			if (item != null) {
				setSelectionArea( item.getAbsoluteX(), item.getAbsoluteY(), item.itemWidth, item.itemHeight);
			} else {
				setSelectionArea( 0, 0, this.simulation.getCanvasWidth() - 1, this.simulation.getCanvasHeight() - 1);
			}
		}
	}
	

}
