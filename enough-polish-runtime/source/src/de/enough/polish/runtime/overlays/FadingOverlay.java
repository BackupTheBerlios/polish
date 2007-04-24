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

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import de.enough.polish.runtime.Simulation;
import de.enough.polish.runtime.SimulationOverlay;

/**
 * <p>Baseclass for overlays that want to hightlight areas by a color that fades aways.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 20, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class FadingOverlay 
extends SimulationOverlay
implements Runnable
{

	protected int selectedX;
	protected int selectedY;
	protected int selectedWidth;
	protected int selectedHeight;
	protected boolean isSelected;
	protected boolean isStopRequested;
	protected int argbColor;
	protected BufferedImage overlay;
	protected int[] overlayData;
	protected int currentTranslucency;
	protected int startTranslucency;
	protected int translucencyIncreaseDelta = 10;
	
	/**
	 * Creates a new fading overlay with the default color (0x88ff0000)
	 */
	protected FadingOverlay() {
		this( 0x88ff0000 );
	}
	
	/**
	 * Creates a new fading overlay with the specified color
	 * @param argbColor the color in0xAARRGGBB format
	 */
	protected FadingOverlay( int argbColor ) {
		this.argbColor = argbColor;
		this.startTranslucency = argbColor >>> 24;
		this.currentTranslucency = this.startTranslucency;
	}

	protected void createOverlay( int width, int height ) {
		int[] rgbData = new int[width];
		for (int i = 0; i < rgbData.length; i++) {
			rgbData[i] = this.argbColor;
		}
		BufferedImage image;
		if (this.overlay == null || this.overlay.getWidth() != width || this.overlay.getHeight() != height) {
			int type = BufferedImage.TYPE_INT_ARGB;
			image = new BufferedImage( width, height, type );
		} else {
			image = this.overlay;
		}
		image.setRGB(0, 0, width, height, rgbData, 0, 0 );
		this.selectedWidth = width;
		this.selectedHeight = height;
		this.overlayData = rgbData;
		this.overlay = image;
		this.currentTranslucency = this.startTranslucency;
	}
	
	protected void increaseTranslucencyForOverlay() {
		this.currentTranslucency -= this.translucencyIncreaseDelta;
		if (this.currentTranslucency <= 0) {
			clearSelectionArea();
		} else {
			int color = (this.argbColor & 0x00ffffff) | (this.currentTranslucency << 24);
			int[] rgbData = this.overlayData;
			for (int i = 0; i < rgbData.length; i++) {
				rgbData[i] = color;
			}
			this.overlay.setRGB(0, 0, this.overlay.getWidth(), this.overlay.getHeight(), rgbData, 0, 0 );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.runtime.SimulationOverlay#paintOverlay(java.awt.Graphics, int, int)
	 */
	public void paintOverlay(Graphics g, int width, int height) {
		BufferedImage image = this.overlay;
		if (this.isSelected && image != null) {
			g.drawImage( image, this.selectedX, this.selectedY, this.selectedWidth, this.selectedHeight, null );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.runtime.SimulationOverlay#registerSimulation(de.enough.polish.runtime.Simulation)
	 */
	public void registerSimulation(Simulation sim) {
		super.registerSimulation(sim);
		this.isStopRequested = false;
		Thread thread = new Thread( this );
		thread.start();
	}
	
	

	/* (non-Javadoc)
	 * @see de.enough.polish.runtime.SimulationOverlay#deregisterSimulation(de.enough.polish.runtime.Simulation)
	 */
	public void deregisterSimulation(Simulation sim) {
		super.deregisterSimulation(sim);
		this.isStopRequested = true;
	}
	
	/**
	 * Sets the selection area.
	 * Calls createOverlay implicitely.
	 * 
	 * @param x horizontal start
	 * @param y vertical start
	 * @param width width of the selected area
	 * @param height height of the selected area
	 */
	protected void setSelectionArea( int x, int y, int width, int height ) {
		this.selectedX = x;
		this.selectedY = y;
		createOverlay(width, height);
		this.isSelected = true;
		this.simulation.repaint();
	}

	/**
	 * Removes the selection.
	 */
	public void clearSelectionArea() {
		this.isSelected = false;
		this.simulation.repaint();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (!this.isStopRequested) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}
			if (this.isSelected){
				increaseTranslucencyForOverlay();
				this.simulation.repaint();
			}
		}
	}

	/**
	 * @return the translucencyIncreaseDelta
	 */
	public int getTranslucencyIncreaseDelta() {
		return this.translucencyIncreaseDelta;
	}
	

	/**
	 * Sets the amount by which the translucency should be increased in each animation step
	 * @param translucencyIncreaseDelta the amount to set, default is 10 (allowed values are between 1 and 255)
	 */
	public void setTranslucencyIncreaseDelta(int translucencyIncreaseDelta) {
		this.translucencyIncreaseDelta = translucencyIncreaseDelta;
	}
	

}
