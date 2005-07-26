/*
 * Created on 28-Dec-2004 at 22:55:01.
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
package de.enough.polish.runtime;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.swing.JComponent;

import de.enough.polish.Device;

/**
 * <p>Provides a simulation of a device that is capable of changing fullscreen-mode, simulating key- and pointer-events and so on.</p>
 * <p>Main focus is to provide a test environment for automatic (JUnit) tests of MIDlets.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>
 * <pre>
 * history
 *        28-Dec-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Simulation
extends JComponent 
implements MouseListener, KeyListener
{
	private static final long serialVersionUID = -5601752053686017794L;
	private static final int DEFAULT_LARGE_FONT_SIZE = 22;
	private static final int DEFAULT_MEDIUM_FONT_SIZE = 18;
	private static final int DEFAULT_SMALL_FONT_SIZE = 15;
	
	private static HashMap simulationsByMidlet = new HashMap();
	
	private final Device device;
	private int canvasWidth;
	private int canvasHeight;
	private static Simulation currentDeviceSimulator;
	private final boolean isColorDevice;
	private final int numberOfColors;
	private final int numberOfAlphaLevels;
	private final javax.microedition.lcdui.Graphics graphics;
	private MIDlet midlet;
	private final BufferedImage displayImage;
	private Dimension minimumSize;
	//private final ArrayList repaintQueue;
	private final int fontSizeSmall;
	private final int fontSizeMedium;
	private final int fontSizeLarge;
	private String midletClassName;
	private SimulationManager simulationManager;
	private Displayable currentDisplayable;
	private DisplayChangeListener displayChangeListener;
	private boolean isDeleteKey;
	private boolean useFixKeys;
	private int clearKey;
	private int leftSoftKey;
	private int rightSoftKey;
	private int changeInputKey;
	private boolean isDisposed;

	/**
	 * Creates a new device simulator.
	 * @param manager
	 * 
	 * @param device the actual XML based device
	 */
	public Simulation( SimulationManager manager, Device device ) {
		super();
		this.simulationManager = manager;
		//this.repaintQueue = new ArrayList();
		String canvasWidthStr = device.getCapability("polish.FullCanvasWidth");
		String canvasHeightStr = device.getCapability("polish.FullCanvasHeight");
		if (canvasWidthStr == null ) {
			canvasWidthStr = device.getCapability("polish.ScreenWidth");
			canvasHeightStr = device.getCapability("polish.ScreenHeight");
		}
		this.canvasWidth = Integer.parseInt( canvasWidthStr );
		this.canvasHeight = Integer.parseInt( canvasHeightStr );
		//System.out.println("Simulation: canvasHeight of " + device.getIdentifier() + "=" + this.canvasHeight );
		currentDeviceSimulator = this;
		String bitsPerPixelStr =  device.getCapability("polish.BitsPerPixel");
		if (bitsPerPixelStr == null) {
			this.isColorDevice = false;
			this.numberOfColors = 2;
		} else {
			int bitsPerColor = Integer.parseInt( bitsPerPixelStr );
			this.numberOfColors = (int) Math.pow( 2D, bitsPerColor );
			this.isColorDevice = (this.numberOfColors > 2);
		}
		String alphaLevelsStr = device.getCapability("polish.AlphaLevels");
		if (alphaLevelsStr == null) {
			this.numberOfAlphaLevels = 2;
		} else {
			this.numberOfAlphaLevels = Integer.parseInt( alphaLevelsStr );
		}
		String fontSizeStr = device.getCapability("polish.Font.small");
		if (fontSizeStr != null) {
			this.fontSizeSmall = Integer.parseInt( fontSizeStr );
		} else {
			this.fontSizeSmall = DEFAULT_SMALL_FONT_SIZE;
		}
		fontSizeStr = device.getCapability("polish.Font.medium");
		if (fontSizeStr != null) {
			this.fontSizeMedium = Integer.parseInt( fontSizeStr );
		} else {
			this.fontSizeMedium = DEFAULT_MEDIUM_FONT_SIZE;
		}
		fontSizeStr = device.getCapability("polish.Font.large");
		if (fontSizeStr != null) {
			this.fontSizeLarge = Integer.parseInt( fontSizeStr );
		} else {
			this.fontSizeLarge = DEFAULT_LARGE_FONT_SIZE;
		}
		this.device = device;
		BufferedImage image =
		    new BufferedImage(this.canvasWidth,this.canvasHeight,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();
		this.displayImage = image;
		this.minimumSize = new Dimension( this.canvasWidth, this.canvasHeight );
		setPreferredSize( this.minimumSize );
		//java.awt.Graphics awtGraphics =  null;
		this.graphics = new javax.microedition.lcdui.Graphics( g, this );
		//Thread repaintThread = new Thread( this );
		//repaintThread.start();
		addMouseListener( this );
		addKeyListener( this );
		setFocusable( true );
	}

	/**
	 * Retrieves the width of the canvas.
	 * This depends on the fullscreen mode of the MIDlet.
	 * 
	 * @return the current width of the canvas.
	 */
	public int getCanvasWidth() {
		return this.canvasWidth;
	}

	/**
	 * Retrieves the height of the canvas.
	 * This depends on the fullscreen mode of the MIDlet.
	 * 
	 * @return the current height of the canvas.
	 */
	public int getCanvasHeight() {
		return this.canvasHeight;
	}
	
	public void loadMIDlet( String className ) 
	throws ClassNotFoundException, InstantiationException, IllegalAccessException, MIDletStateChangeException 
	{
		this.midletClassName = className;
		// load class:
		Class midletClass = Class.forName( className );
		// call constructor
		this.midlet = (MIDlet) midletClass.newInstance();
		simulationsByMidlet.put( this.midlet, this );
		this.midlet._callStartApp();
	}
	
	public void simulateKeyPressedEvent( int keyEvent ) {
		if (this.currentDisplayable instanceof Canvas) {
			//System.out.println("simulate key press: " + keyEvent);
			Canvas canvas = (Canvas) this.currentDisplayable; 
			canvas._callKeyPressed( keyEvent );
			//canvas.repaint();
		}
	}

	public void simulateGameActionEvent( int gameAction ) {
		if (this.currentDisplayable instanceof Canvas) {
			//System.out.println("simulate gameAction: " + gameAction);
			Canvas canvas = (Canvas) this.currentDisplayable; 
			canvas._callKeyPressed( canvas.getKeyCode(gameAction) );
			//canvas.repaint();
		}
	}
	
	public void simulateKeyReleasedEvent( int keyEvent ) {
		
	}

	public void simulateKeyRepeatedEvent( int keyEvent ) {
		
	}

	public void simulatePointerPressedEvent( int x, int y ) {
		if (this.currentDisplayable instanceof Canvas) {
			((Canvas)this.currentDisplayable)._callPointerPressed( x, y );
		}		
	}

	public void simulatePointerReleasedEvent( int x, int y  ) {
		
	}
	
	/**
	 * @return true when the device supports color graphics
	 */
	public boolean isColor() {
		return this.isColorDevice;
	}

	/**
	 * @return the current simulation of all played simulations
	 */
	public static Simulation getCurrentSimulation() {
		return currentDeviceSimulator;
	}

	/**
	 * @return the number of colors supported by this device
	 */
	public int getNumberOfColors() {
		return this.numberOfColors;
	}

	/**
	 * @return the number of alpha levels supported by this device
	 */
	public int getNumberOfAlphaLevels() {
		return this.numberOfAlphaLevels;
	}

	/**
	 * @param displayable
	 */
	public void requestRepaint(Displayable displayable ) {
		if (displayable != this.currentDisplayable ) {
			//System.out.println("Simulator: ignoring repaint request");
			return;
		}
		
		if (displayable instanceof Canvas) {
			//System.out.println("repaint for screenclass: " + displayable.getClass().getName() );
			this.graphics.reset();
			((Canvas) displayable)._paint( this.graphics );
			repaint();
		} else {
			System.out.println("Unable to paint unknown screenclass: " + displayable.getClass().getName() );
		}
		//synchronized ( this.repaintQueue ) {
		//	this.repaintQueue.add( displayable );
		//}
	}
	
	

	protected void paintComponent(Graphics g) {
		g.drawImage( this.displayImage, 0, 0, null );
	}
	
	
	public Dimension getMinimumSize() {
		return this.minimumSize;
	}
	
	public Dimension getPreferredSize() {
		return this.minimumSize;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	public void run() {
		while (!this.isDisposed) {
			Displayable displayable = null;
			synchronized ( this.repaintQueue ) {
				if (this.repaintQueue.size() != 0) {
					displayable = (Displayable) this.repaintQueue.remove( 0 );
				}
			}
			if (displayable != null) {
				try {
					if (displayable instanceof Canvas) {
						this.graphics.reset();
						((Canvas) displayable)._paint( this.graphics );
						repaint();
					} else {
						System.out.println("Unable to paint unknown screenclass: " + displayable.getClass().getName() );
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Simulation: unable to repaint screen [" + displayable.getClass().getName() + "]: " + e.toString());
				}
			}
			try {
				Thread.sleep( 100 );
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}
	 */
	
	public void dispose() {
		this.isDisposed = true;
		Display._removeSimulation( this );
	}

	public int getFontSizeLarge() {
		return this.fontSizeLarge;
	}
	public int getFontSizeMedium() {
		return this.fontSizeMedium;
	}
	public int getFontSizeSmall() {
		return this.fontSizeSmall;
	}

	/**
	 * Restarts this simulation.
	 * 
	 * @throws MIDletStateChangeException when the corresponding MIDlet could not be restarted.
	 */
	public void restart() throws MIDletStateChangeException {
		if (this.midlet != null) {
			try {
				this.midlet = (MIDlet) this.midlet.getClass().newInstance();
				simulationsByMidlet.put( this.midlet, this );
			} catch (Exception e) {
				System.err.println( "Unable to restart the MIDlet: " + e.toString() );
				e.printStackTrace();
				throw new MIDletStateChangeException( "Unable to restart the MIDlet: " + e.toString() );
			}
			this.midlet._callStartApp();
		}
	}

	/**
	 * @return
	 */
	public BufferedImage getDisplayImage() {
		return this.displayImage;
	}

	/**
	 * Retrieves a copy of this simulation
	 * 
	 * @return a copy of this simulation
	 */
	public Simulation getCopy() {
		Simulation copy = new Simulation( this.simulationManager, this.device );
		if (this.useFixKeys) {
			copy.setFixKeyCodes(this.clearKey, this.leftSoftKey, this.rightSoftKey, this.changeInputKey);
		}
		if (this.midletClassName != null) {
			try {
				copy.loadMIDlet( this.midletClassName );
			} catch (Exception e) {
				System.out.println("Unable to create a copy of the simulation with class [" + this.midletClassName + "].");
				e.printStackTrace();
			}
		}
		return copy;
	}
	
	/**
	 * @param deviceIdentifier
	 * @return
	 * @throws MIDletStateChangeException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public Simulation getCopy(String deviceIdentifier) throws ClassNotFoundException, InstantiationException, IllegalAccessException, MIDletStateChangeException {
		Simulation copy = this.simulationManager.loadSimulation(deviceIdentifier, this.midletClassName);
		if (this.useFixKeys) {
			copy.setFixKeyCodes(this.clearKey, this.leftSoftKey, this.rightSoftKey, this.changeInputKey);
		}
		return copy;
	}

	/**
	 * @param midlet
	 * @return
	 */
	public static Simulation getSimulation(MIDlet midlet) {
		Simulation simulation = (Simulation) simulationsByMidlet.get( midlet );
		if (simulation == null) {
			return getCurrentSimulation();
		} else {
			return simulation;
		}
	}
	
	public Device getDevice() {
		return this.device;
	}

	/**
	 * @return
	 */
	public String getClassNameWithoutPackage() {
		if (this.midletClassName == null) {
			return null;
		}
		int lastDotPos = this.midletClassName.lastIndexOf('.');
		return this.midletClassName.substring( lastDotPos + 1 );
	}

	public Displayable getCurrentDisplayable(){
		return this.currentDisplayable;
	}

	/**
	 * Simulates the pressing of the left soft key
	 */
	public void simulateLeftSoftKeyPressedEvent() {
		if (this.useFixKeys) {
			simulateKeyPressedEvent( this.leftSoftKey );
			return;
		}
		String keyStr = this.device.getCapability("polish.key.LeftSoftKey");
		if (keyStr != null) {
			simulateKeyPressedEvent( Integer.parseInt(keyStr) );
		} else {
			simulateKeyPressedEvent( -6 );
		}		
	}

	/**
	 * Simulates the pressing of the right soft key
	 */
	public void simulateRightSoftKeyPressedEvent() {
		if (this.useFixKeys) {
			simulateKeyPressedEvent( this.rightSoftKey );
			return;
		}
		String keyStr = this.device.getCapability("polish.key.RightSoftKey");
		if (keyStr != null) {
			simulateKeyPressedEvent( Integer.parseInt(keyStr) );
		} else {
			simulateKeyPressedEvent( -7 );
		}		
	}

	/**
	 * Simulates the pressing of the clear soft key
	 */
	public void simulateClearKeyPressedEvent() {
		if (this.useFixKeys) {
			simulateKeyPressedEvent( this.clearKey );
			return;
		}
		String keyStr = this.device.getCapability("polish.key.ClearKey");
		if (keyStr != null) {
			simulateKeyPressedEvent( Integer.parseInt(keyStr) );
		} else {
			simulateKeyPressedEvent( -8 );
		}		
	}
	
	/**
	 * Simulates the pressing of the key that changes the input mode
	 */
	public void simulateChangeInputModeKeyPressedEvent() {
		if (this.useFixKeys) {
			simulateKeyPressedEvent( this.changeInputKey );
			return;
		}
		String keyStr = this.device.getCapability("polish.key.ChangeInputModeKey");
		if (keyStr != null) {
			simulateKeyPressedEvent( Integer.parseInt(keyStr) );
		} else {
			simulateKeyPressedEvent( Canvas.KEY_POUND );
		}		
	}

	/**
	 * @param nextDisplayable
	 */
	public void setCurrent(Displayable nextDisplayable) {
		
		//System.out.println("simulator: setting current " + nextDisplayable.getClass().getName() );
		this.currentDisplayable = nextDisplayable;
		requestRepaint( nextDisplayable );
		//synchronized ( this.repaintQueue ) {
		//	this.repaintQueue.clear();
		//	this.repaintQueue.add(nextDisplayable);
		//}
		if (this.displayChangeListener != null) {
			this.displayChangeListener.displayChanged(nextDisplayable);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		//System.out.println("mouse clicked: " + e );
		if (this.currentDisplayable instanceof Canvas) {
			((Canvas)this.currentDisplayable)._callPointerPressed( e.getX(), e.getY() );
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		// System.out.println("mouse entered");
		// ignore		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		// System.out.println("mouse exited");
		// ignore
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		// ignore
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		// ignore
	}

	/**
	 * @return
	 */
	public MIDlet getMIDlet() {
		return this.midlet;
	}

	/**
	 * @return Returns the displayChangeListener.
	 */
	public DisplayChangeListener getDisplayChangeListener() {
		return this.displayChangeListener;
	}
	/**
	 * @param displayChangeListener The displayChangeListener to set.
	 */
	public void setDisplayChangeListener(
			DisplayChangeListener displayChangeListener) {
		this.displayChangeListener = displayChangeListener;
	}
	
	public void setImageListener( ImageListener listener ) {
		this.graphics.setImageListener( listener );
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		// ignore
		int keyCode = e.getKeyCode();
		if (e.isActionKey()) {
			if ( keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_KP_LEFT ) {
				simulateGameActionEvent( Canvas.LEFT );
			} else if ( keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_KP_RIGHT ) {
				simulateGameActionEvent( Canvas.RIGHT );
			} else if ( keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_KP_UP ) {
				simulateGameActionEvent( Canvas.UP );
			} else if ( keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_KP_DOWN ) {
				simulateGameActionEvent( Canvas.DOWN );
			}
		} else if (keyCode == KeyEvent.VK_DELETE) {
			this.isDeleteKey = true;
		} else if ( keyCode == KeyEvent.VK_ENTER ) {
			simulateGameActionEvent( Canvas.FIRE );
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		this.isDeleteKey = false;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
		char input = e.getKeyChar();
		if (input == '\b' || this.isDeleteKey) {
			simulateClearKeyPressedEvent();
		} else {
			this.currentDisplayable._processInput( input );
		}
	}
	
	public void setFixKeyCodes( int clearKey, int leftSoftKey, int rightSoftKey, int changeInputKey ) {
		this.useFixKeys = true;
		this.clearKey = clearKey;
		this.leftSoftKey = leftSoftKey;
		this.rightSoftKey = rightSoftKey;
		this.changeInputKey = changeInputKey;
	}

}
