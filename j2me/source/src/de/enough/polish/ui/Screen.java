//#condition polish.usePolishGui
/*
 * Created on 12-Mar-2004 at 21:46:17.
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui;


import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Debug;

import javax.microedition.lcdui.*;


/**
 * The common superclass of all high-level user interface classes.
 * 
 * The contents displayed and their interaction with the user are defined by
 * subclasses.
 * 
 * <P>Using subclass-defined methods, the application may change the contents
 * of a <code>Screen</code> object while it is shown to the user.  If
 * this occurs, and the
 * <code>Screen</code> object is visible, the display will be updated
 * automatically.  That
 * is, the implementation will refresh the display in a timely fashion without
 * waiting for any further action by the application.  For example, suppose a
 * <code>List</code> object is currently displayed, and every element
 * of the <code>List</code> is
 * visible.  If the application inserts a new element at the beginning of the
 * <code>List</code>, it is displayed immediately, and the other
 * elements will be
 * rearranged appropriately.  There is no need for the application to call
 * another method to refresh the display.</P>
 * 
 * <P>It is recommended that applications change the contents of a
 * <code>Screen</code> only
 * while it is not visible (that is, while another
 * <code>Displayable</code> is current).
 * Changing the contents of a <code>Screen</code> while it is visible
 * may result in
 * performance problems on some devices, and it may also be confusing if the
 * <code>Screen's</code> contents changes while the user is
 * interacting with it.</P>
 * 
 * <P>In MIDP 2.0 the four <code>Screen</code> methods that defined
 * read/write ticker and
 * title properties were moved to <code>Displayable</code>,
 * <code>Screen's</code> superclass.  The
 * semantics of these methods have not changed.</P>
 * <HR>
 * 
 * @since MIDP 1.0
 * 
 */
public abstract class Screen 
//#if polish.useFullScreen && polish.midp2  && (!polish.useMenuFullScreen || polish.hasCommandKeyEvents)
	//#define tmp.fullScreen
	//# extends Canvas
//#elif polish.useFullScreen && polish.classes.fullscreen:defined
	//#define tmp.fullScreen
	//#= extends ${polish.classes.fullscreen}
//#else
	extends Canvas
//#endif
{
	
	//#if tmp.fullScreen || polish.midp1 || (polish.usePolishTitle == true)
		//#define tmp.usingTitle
		protected StringItem title;
		//#ifdef polish.css.title-style
			private Style titleStyle;
		//#endif
		//#ifdef polish.Vendor.Motorola
			private boolean ignoreMotorolaTitleCall;
		//#endif
	//#endif
	//#ifdef polish.Vendor.Siemens
		// Siemens sometimes calls hideNotify directly
		// after showNotify for some reason.
		// So hideNotify checks how long the screen
		// has been shown - if not long enough,
		// the call will be ignored.
		private long showNotifyTime;
	//#endif
	protected int titleHeight;
	protected Background background;
	protected Border border;
	protected Style style;
	protected int screenHeight;
	private int originalScreenHeight;
	protected final int screenWidth;
	//#ifndef polish.skipTicker
		private Ticker ticker;
	//#endif
	protected String cssSelector;
	private ForwardCommandListener cmdListener;
	protected Container container;
	protected boolean isLayoutVCenter;
	protected boolean isInitialised;
	//#if polish.useFullScreen && polish.api.nokia-ui 
		private boolean isInPaintMethod;
		private long lastPaintTime;
		private boolean repaintRequested;
	//#endif
	//#if (polish.useMenuFullScreen && tmp.fullScreen) || polish.needsManualMenu
		//#define tmp.menuFullScreen
		//#ifdef polish.key.LeftSoftKey:defined
			//#= private final static int LEFT_SOFT_KEY = ${polish.key.LeftSoftKey};
		//#else
			private final static int LEFT_SOFT_KEY = -6;
		//#endif
		//#ifdef polish.key.RightSoftKey:defined
			//#= private final static int RIGHT_SOFT_KEY = ${polish.key.RightSoftKey};
		//#else
			private final static int RIGHT_SOFT_KEY = -7;
		//#endif
		private Command menuSingleLeftCommand;
		private Command menuSingleRightCommand;
		//#ifdef polish.key.ReturnKey:defined
			private Command backCommand;
		//#endif
		private Container menuContainer;
		private ArrayList menuCommands;
		private boolean menuOpened;
		private Font menuFont;
		private int menuFontColor = 0;
		protected int menuBarHeight;
		//#ifdef polish.ScreenWidth:defined
			//#= private final int menuMaxWidth = (  ${ polish.ScreenWidth } * 2 ) / 3;
		//#else
			private final int menuMaxWidth = ( getWidth() * 2 ) / 3;
		//#endif
		private int menuBarColor = 0xFFFFFF;
		private int fullScreenHeight;
		//#ifdef polish.hasPointerEvents
			private int menuRightCommandX;
			private int menuLeftCommandX;
		//#endif
	//#endif
	/** The Gauge Item which should be animated by this screen */
	protected Item gauge;
	/** The currently focused items which has item-commands */
	private Item focusedItem;
	protected boolean paintScrollIndicator;
	protected boolean paintScrollIndicatorUp;
	protected boolean paintScrollIndicatorDown;
	private int scrollIndicatorColor;
	private int scrollIndicatorX; // left x position of scroll indicator
	private int scrollIndicatorY; // top y position of scroll indicator
	private int scrollIndicatorWidth; // width and height of the indicator
	//#if tmp.usingTitle || tmp.menuFullScreen
		private boolean showTitleOrMenu = true;
	//#endif
	/** an info text which is shown e.g. when some content is added to textfields */
	private StringItem infoItem;
	/** determines whether the info text should be shown */
	private boolean showInfoItem;
	private int infoHeight;
	
	/**
	 * Creates a new screen
	 * 
	 * @param title the title, or null for no title
	 * @param style the style of this screen
	 * @param createDefaultContainer true when the default container should be created.
	 */
	public Screen( String title, Style style, boolean createDefaultContainer ) {
		super();
		//#if tmp.fullScreen && polish.midp2 && !polish.Bugs.fullScreenInShowNotify
			super.setFullScreenMode( true );
		//#endif
			
		// get the screen dimensions:
		// this is a bit complicated, since Nokia's FullCanvas fucks
		// up when calling super.getHeight(), so we need to use hardcoded values...
		
		//#ifdef tmp.menuFullScreen
			//#ifdef polish.needsManualMenu
				this.fullScreenHeight = getHeight();
			//#else
				//#ifdef polish.FullCanvasHeight:defined
					//#= this.fullScreenHeight = ${ polish.FullCanvasHeight };
				//#else
					//# this.fullScreenHeight = getHeight();
				//#endif
			//#endif
			this.screenHeight = this.fullScreenHeight - this.menuBarHeight;
		//#else
			this.screenHeight = getHeight();
		//#endif
		this.originalScreenHeight = this.screenHeight;
		
		//#ifdef polish.ScreenWidth:defined
			//#= this.screenWidth = ${ polish.ScreenWidth };
		//#else
			this.screenWidth = getWidth();
		//#endif
						
		// creating standard container:
		if (createDefaultContainer) {
			this.container = new Container( true );
			this.container.screen = this;
		}
		this.style = style;
		this.cmdListener = new ForwardCommandListener();
		//#ifndef tmp.menuFullScreen
			super.setCommandListener(this.cmdListener);
		//#endif
		
		setTitle( title );
	}
		
	/**
	 * Initialises this screen before it is painted for the first time.
	 */
	public void init() {
		if (this.style != null) {
			setStyle( this.style );
		}
		//#ifdef polish.useDynamicStyles
			// check if this screen has got a style:
			if (this.style == null) {
				this.cssSelector = createCssSelector();
				setStyle( StyleSheet.getStyle( this ) );
			} else {
				this.cssSelector = this.style.name;
			}
		//#endif
		//#ifdef tmp.menuFullScreen
			//#ifdef polish.css.style.menu
				Style menustyle = StyleSheet.menuStyle;
			//#else
				//# Style menustyle = this.style;
			//#endif
			if (menustyle != null) {
				Integer colorInt = null;
				if (this.style != null) {
					colorInt = this.style.getIntProperty("menubar-color");
				}
				if (colorInt == null) {
					colorInt = menustyle.getIntProperty("menubar-color");
				}
				if (colorInt != null) {
					this.menuBarColor = colorInt.intValue();
				}
				this.menuFontColor = menustyle.fontColor;
				if (menustyle.font != null) {
					this.menuFont = menustyle.font;
				} else {
					this.menuFont = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM );				
				}			
			} else {
				this.menuFont = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM );
			}
			this.menuBarHeight = this.menuFont.getHeight() + 2;
			int diff = this.originalScreenHeight - this.screenHeight;
			this.originalScreenHeight = this.fullScreenHeight - this.menuBarHeight;
			this.screenHeight = this.originalScreenHeight - diff;
			// set position of scroll indicator:
			this.scrollIndicatorWidth = this.menuBarHeight;
			this.scrollIndicatorX = this.screenWidth / 2 - this.scrollIndicatorWidth / 2;
			this.scrollIndicatorY = this.fullScreenHeight - this.scrollIndicatorWidth;
		//#elif polish.vendor.Siemens
			// set the position of scroll indicator for Siemens devices 
			// on the left side, so that the menu-indicator is visible:
			this.scrollIndicatorWidth = 12;
			this.scrollIndicatorX = 0;
			this.scrollIndicatorY = this.screenHeight - this.scrollIndicatorWidth - 1;
		//#else
			// set position of scroll indicator:
			this.scrollIndicatorWidth = 12;
			this.scrollIndicatorX = this.screenWidth - this.scrollIndicatorWidth;
			this.scrollIndicatorY = this.screenHeight - this.scrollIndicatorWidth - 1;
		//#endif

		if (this.container != null) {
			this.container.screen = this;
			this.container.setVerticalDimensions( 0, this.screenHeight - this.titleHeight );
		}
		
		// start the animmation thread if necessary: 
		if (StyleSheet.animationThread == null) {
			StyleSheet.animationThread = new AnimationThread();
			StyleSheet.animationThread.start();
		}
		this.isInitialised = true;
	}
	
	

	/**
	 * Initialises this screen and informs all items about being painted soon.
	 */
	protected void showNotify() {
		//#debug
		System.out.println("showNotify " + this );
		try {
			//#if tmp.fullScreen && polish.midp2 && polish.Bugs.fullScreenInShowNotify
				super.setFullScreenMode( true );
				//#ifdef polish.FullCanvasHeight:defined
					//#= this.fullScreenHeight = ${polish.FullCanvasHeight};
				//#else
					this.fullScreenHeight = getHeight();
				//#endif
				this.screenHeight = this.fullScreenHeight - this.menuBarHeight;
				this.originalScreenHeight = this.screenHeight;
				this.scrollIndicatorY = this.screenHeight - this.scrollIndicatorWidth - 1;
			//#endif
			if (!this.isInitialised) {
				init();
			}
			// register this screen:
			StyleSheet.currentScreen = this;
			
			// inform all root items that they belong to this screen
			// and that they will be shown soon:
			Item[] items = getRootItems();
			for (int i = 0; i < items.length; i++) {
				Item item = items[i];
				item.screen = this;
				item.showNotify();
			}
			if (this.container != null) {
				this.container.showNotify();
			}
			//#ifdef polish.vendor.Motorola
				this.ignoreMotorolaTitleCall = true;
			//#endif
			//#if tmp.fullScreen && polish.midp2
				// this is needed on Sony Ericsson for example,
				// since the fullscreen mode is not resumed automatically
				// when the previous screen was in the "normal" mode:
				super.setFullScreenMode( true );
			//#endif
		} catch (Exception e) {
			//#debug error
			Debug.debug("error while calling showNotify", e );
		}
		//#ifdef polish.Vendor.Siemens
			this.showNotifyTime = System.currentTimeMillis();
		//#endif
	}
	
	/**
	 * Unregisters this screen and notifies all items that they will not be shown anymore.
	 */
	protected void hideNotify() {
		//#ifdef polish.Vendor.Siemens
			// Siemens sometimes calls hideNotify directly
			// after showNotify for some reason.
			// So hideNotify checks how long the screen
			// has been shown - if not long enough,
			// the call will be ignored:
			if (System.currentTimeMillis() - this.showNotifyTime < 500) {
				//#debug
				System.out.println("Ignoring hideNotify on Siemens");
				return;
			}
		//#endif
		//#debug
		System.out.println("hideNotify " + this);
		// un-register this screen:
		if (StyleSheet.currentScreen == this) {
			StyleSheet.currentScreen = null;
		}
		Item[] items = getRootItems();
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			item.hideNotify();
		}
		if (this.container != null) {
			this.container.hideNotify();
		}
	}
	
	/**
	 * Sets the style of this screen.
	 * 
	 * @param style the style
	 */
	public void setStyle(Style style) {
		//#debug
		System.out.println("Setting screen-style for " + getClass().getName() );
		this.style = style;
		this.background = style.background;
		this.border = style.border;
		this.container.setStyle(style, true);
		this.isLayoutVCenter = (( style.layout & Item.LAYOUT_VCENTER ) == Item.LAYOUT_VCENTER);
		//#ifdef polish.css.scrollindicator-color
			Integer scrollIndicatorColorInt = style.getIntProperty( "scrollindicator-color" );
			if (scrollIndicatorColorInt != null) {
				this.scrollIndicatorColor = scrollIndicatorColorInt.intValue();
			}
		//#endif
		//#if tmp.usingTitle && polish.css.title-style
			this.titleStyle = (Style) style.getObjectProperty("title-style");
			if (this.titleStyle != null && this.title != null) {
				this.title.setStyle(this.titleStyle);
				//#ifdef polish.ScreenWidth:defined
					//#= this.titleHeight = this.title.getItemHeight(${polish.ScreenWidth}, ${polish.ScreenWidth});
				//#else
					this.titleHeight = this.title.getItemHeight( getWidth(), getWidth() );
				//#endif
			}
		//#endif
	}
	
	/**
	 * Animates this Screen.
	 * All embedded items are also animated.
	 * 
	 * @return true when at least one animated item needs a redraw/repaint.
	 */
	public boolean animate() {
		if (!this.isInitialised) {
			return false;
		}
		try {
			boolean animated = false;
			if (this.background != null) {
				animated = this.background.animate();
			}
			//#ifdef tmp.menuFullScreen
				if (this.menuOpened) {
					animated = animated | this.menuContainer.animate();
				} else
			//#endif
			if (this.container != null) {
				animated = animated | this.container.animate();
			}
			if (this.gauge != null) {
				animated = animated | this.gauge.animate();
			}
			//#ifdef tmp.usingTitle
				if (this.title != null) {
					animated = animated | this.title.animate();
				}
			//#endif
			//#ifndef polish.skipTicker
				if (this.ticker != null) {
					animated = animated | this.ticker.animate();
				}
			//#endif
			//#if polish.useFullScreen && polish.api.nokia-ui 
			if (animated || this.repaintRequested) {
				this.repaintRequested = false;
			//#else
				//# if (animated) {
			//#endif
				repaint();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			//#debug error
			Debug.debug("animate() threw an exception", e );
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#paint(javax.microedition.lcdui.Graphics)
	 */
	public final void paint(Graphics g) {
		//#if polish.useFullScreen && polish.api.nokia-ui 
			this.isInPaintMethod = true;
		//#endif
		//#ifdef polish.debug.error
		try {
		//#endif
			// paint background:
			if (this.background != null) {
				//#ifdef tmp.menuFullScreen
					this.background.paint(0, 0, this.screenWidth, this.fullScreenHeight, g);
				//#else
					this.background.paint(0, 0, this.screenWidth, this.screenHeight, g);
				//#endif
			} else {
				g.setColor( 0xFFFFFF );
				//#ifdef tmp.menuFullScreen
					g.fillRect( 0, 0, this.screenWidth, this.fullScreenHeight );
				//#else
					g.fillRect( 0, 0, this.screenWidth, this.screenHeight );
				//#endif
			}
			
			int tHeight = 0;
			//#ifdef tmp.usingTitle
				// paint title:
				if (this.title != null && this.showTitleOrMenu) {
					this.title.paint(0, 0, 0, this.screenWidth, g);
					tHeight = this.titleHeight;
				}
			//#endif
			if (this.showInfoItem) {
				this.infoItem.paint( 0, tHeight, 0, this.screenWidth, g );
				tHeight += this.infoItem.itemHeight;
			}
			// protect the title, ticker and the full-screen-menu area:
			g.setClip(0, tHeight, this.screenWidth, this.screenHeight - tHeight );
			g.translate( 0, tHeight );
			// paint content:
			//System.out.println("starting to paint content of screen");
			paintScreen( g);
			//System.out.println("done painting content of screen");
			
			g.translate( 0, - tHeight );
			// allow painting outside of the screen again:
			//#ifdef tmp.menuFullScreen
			 	g.setClip(0, 0, this.screenWidth, this.fullScreenHeight );
			//#else
			 	g.setClip(0, 0, this.screenWidth, this.originalScreenHeight );
			//#endif
			 
			//#ifndef polish.skipTicker
			 	if (this.ticker != null) {
			 		this.ticker.paint( 0, this.screenHeight, 0, this.screenWidth, g );
			 	}
			//#endif
			
			// paint border:
			if (this.border != null) {
				this.border.paint(0, 0, this.screenWidth, this.screenHeight, g);
			}
			
			// paint menu in full-screen mode:
			//#ifdef tmp.menuFullScreen
				if (this.menuOpened) {
					tHeight -= this.infoHeight;
					int menuHeight = this.menuContainer.getItemHeight(this.menuMaxWidth, this.menuMaxWidth);
					int y = this.originalScreenHeight - menuHeight;
					if (y < tHeight) {
						this.paintScrollIndicator = true;
						this.paintScrollIndicatorUp = (this.menuContainer.yOffset != 0);
						this.paintScrollIndicatorDown = (this.menuContainer.yOffset + menuHeight > this.screenHeight - this.titleHeight);
						y = tHeight; 
						this.menuContainer.setVerticalDimensions(y, this.screenHeight);
					} else {
						this.paintScrollIndicator = false;
					}
					g.setClip(0, tHeight, this.screenWidth, this.screenHeight - tHeight );
					this.menuContainer.paint(0, y, 0, this.menuMaxWidth, g);
				 	g.setClip(0, 0, this.screenWidth, this.fullScreenHeight );
				} 
				if (this.showTitleOrMenu || this.menuOpened) {
					// clear menu-bar:
					if (this.menuBarColor != Item.TRANSPARENT) {
						g.setColor( this.menuBarColor );
						g.fillRect(0, this.originalScreenHeight, this.screenWidth,  this.menuBarHeight );
					}
					if (this.menuContainer != null && this.menuContainer.size() > 0) {
						String menuText = null;
						if (this.menuOpened) {
							//#ifdef polish.command.select:defined
								//#= menuText = "${polish.command.select}";
							//#else
								menuText = "Select";
							//#endif
						} else {
							if (this.menuSingleLeftCommand != null) {
								menuText = this.menuSingleLeftCommand.getLabel();
							} else {
								//#ifdef polish.command.options:defined
									//#= menuText = "${polish.command.options}";
								//#else
									menuText = "Options";				
								//#endif
							}
						}
						//#ifdef polish.hasPointerEvents
							this.menuLeftCommandX = 2 + this.menuFont.stringWidth( menuText );
						//#endif
						g.setColor( this.menuFontColor );
						g.setFont( this.menuFont );
						g.drawString(menuText, 2, this.originalScreenHeight + 2, Graphics.TOP | Graphics.LEFT );
						if ( this.menuOpened ) {
							// draw cancel string:
							//#ifdef polish.command.cancel:defined
								//#= menuText = "${polish.command.cancel}";
							//#else
								menuText = "Cancel";
							//#endif
							g.drawString(menuText, this.screenWidth - 2, this.originalScreenHeight + 2, Graphics.TOP | Graphics.RIGHT );
							//#ifdef polish.hasPointerEvents
								this.menuRightCommandX = this.screenWidth - 2 - this.menuFont.stringWidth( menuText );
							//#endif
						}
					}
					if (this.menuSingleRightCommand != null && !this.menuOpened) {
						g.setColor( this.menuFontColor );
						g.setFont( this.menuFont );
						String menuText = this.menuSingleRightCommand.getLabel();
						g.drawString(menuText, this.screenWidth - 2, this.originalScreenHeight + 2, Graphics.TOP | Graphics.RIGHT );
						//#ifdef polish.hasPointerEvents
							this.menuRightCommandX = this.screenWidth - 2 
								- this.menuFont.stringWidth( menuText );
						//#endif
					}
				} // if this.showTitleOrMenu || this.menuOpened
			//#endif
			// paint scroll-indicator in the middle of the menu:
			if (this.paintScrollIndicator) {
				g.setColor( this.scrollIndicatorColor );
				int x = this.scrollIndicatorX;
				int y = this.scrollIndicatorY;
				int width = this.scrollIndicatorWidth;
				int halfWidth = width / 2;
				if (this.paintScrollIndicatorUp) {
					//#ifdef polish.midp2
						g.fillTriangle(x, y + halfWidth-1, x + width, y + halfWidth-1, x + halfWidth, y );
					//#else
						g.drawLine( x, y + halfWidth-1, x + width, y + halfWidth-1 );
						g.drawLine( x, y + halfWidth-1, x + halfWidth, y );
						g.drawLine( x + width, y + halfWidth-1, x + halfWidth, y );
					//#endif
				}
				if (this.paintScrollIndicatorDown) {
					//#ifdef polish.midp2
						g.fillTriangle(x, y + halfWidth+1, x + width, y + halfWidth+1, x + halfWidth, y + width );
					//#else
						g.drawLine( x, y + halfWidth+1, x + width, y + halfWidth+1 );
						g.drawLine( x, y + halfWidth+1, x + halfWidth, y + width );
						g.drawLine(x + width, y + halfWidth+1, x + halfWidth, y + width );
					//#endif
				}
			}
		//#ifdef polish.debug.error
		} catch (RuntimeException e) {
			//#debug error
			Debug.debug( "unable to paint screen", e );
		}
		//#endif
		//#if polish.useFullScreen && polish.api.nokia-ui 
			this.lastPaintTime = System.currentTimeMillis();
			this.isInPaintMethod = false;
		//#endif
	}
	
	/**
	 * Paints the screen.
	 * This method also needs to set the protected variables
	 * paintScrollIndicator, paintScrollIndicatorUp and paintScrollIndicatorDown.
	 * 
	 * @param g the graphics on which the screen should be painted
	 */
	protected void paintScreen( Graphics g ) {
		int y = 0;
		int containerHeight = this.container.getItemHeight( this.screenWidth, this.screenWidth);
		int availableHeight = this.screenHeight - (this.titleHeight + this.infoHeight);
		this.paintScrollIndicator = false; // defaults to false
		if (containerHeight > availableHeight ) {
			this.paintScrollIndicator = true;
			this.paintScrollIndicatorUp = (this.container.yOffset != 0);
			this.paintScrollIndicatorDown = (this.container.yOffset + containerHeight > availableHeight);
		} else if (this.isLayoutVCenter) {
			y = ((availableHeight - containerHeight) / 2);
		} 
		this.container.paint( 0, y, 0, this.screenWidth, g );
	}
	
	//#ifdef tmp.usingTitle
	/**
	 * Gets the title of the Screen. 
	 * Returns null if there is no title.
	 * 
	 * @return the title of this screen
	 */
	public String getTitle()
	{
		if (this.title == null) {
			return null;
		} else {
			return this.title.getText();
		}
	}
	//#endif

	//#ifdef tmp.usingTitle
	/**
	 * Sets the title of the Screen. If null is given, removes the title.
	 * 
	 * If the Screen is physically visible, the visible effect
	 * should take place no later than immediately
	 * after the callback or
	 * <A HREF="../../../javax/microedition/midlet/MIDlet.html#startApp()"><CODE>startApp</CODE></A>
	 * returns back to the implementation.
	 * 
	 * @param s - the new title, or null for no title
	 */
	public void setTitle( String s)
	{
		//#debug
		System.out.println("Setting title " + s );
		//#ifdef polish.vendor.Motorola
			if (this.ignoreMotorolaTitleCall) {
				this.ignoreMotorolaTitleCall = false;
				return;
			} else {
				this.ignoreMotorolaTitleCall = true;
			}
		//#endif
		if (s != null) {
			//#style title, default
			this.title = new StringItem( null, s );
			//#ifdef polish.css.title-style
				if (this.titleStyle != null) {
					this.title.setStyle( this.titleStyle );
				}
			//#endif
			// the Nokia 6600 has an amazing bug - when trying to refer the
			// field screenWidth, it returns 0 in setTitle(). Obviously this works
			// in other phones and in the simulator, but not on the Nokia 6600.
			// That's why hardcoded values are used here. 
			// The name of the field does not matter by the way. This is 
			// a very interesting behaviour and should be analysed
			// at some point...
			//#ifdef polish.ScreenWidth:defined
				//#= this.titleHeight = this.title.getItemHeight(${polish.ScreenWidth}, ${polish.ScreenWidth});
			//#else
				this.titleHeight = this.title.getItemHeight( getWidth(), getWidth() );
			//#endif
		} else {
			this.title = null;
			this.titleHeight = 0;
		}
		if (this.container != null) {
			this.container.setVerticalDimensions( 0,  this.screenHeight - (this.titleHeight + this.infoHeight) );
		}
		if (this.isInitialised && isShown()) {
			repaint();
		}
	}
	//#endif
	
	/**
	 * Sets the information which should be shown to the user.
	 * The info is shown below the title (if any) and can be designed
	 * using the predefined style "info".
	 * At the moment this feature is only used by the TextField implementation,
	 * when the direct input mode is enabled.
	 * A repaint is not triggered automatically by calling this method.
	 * 
	 * @param infoText the text which will be shown to the user
	 */
	public void setInfo( String infoText ) {
		if (this.infoItem == null) {
			//#style info, default
			this.infoItem = new StringItem( null, infoText );
		} else {
			this.infoItem.setText( infoText );
		}
		if (infoText == null) {
			this.infoHeight = 0;
			this.showInfoItem = false;
		} else {
			this.infoHeight = this.infoItem.getItemHeight(this.screenWidth, this.screenWidth);
			this.showInfoItem = true;
		}
		if (this.container != null) {
			this.container.setVerticalDimensions( 0,  this.screenHeight - (this.titleHeight + this.infoHeight) );
		}
	}

	//#ifndef polish.skipTicker
	/**
	 * Set a ticker for use with this Screen, replacing any previous ticker.
	 * If null, removes the ticker object
	 * from this screen. The same ticker is may be shared by several Screen
	 * objects within an application. This is done by calling setTicker() on
	 * different screens with the same Ticker object.
	 * If the Screen is physically visible, the visible effect
	 * should take place no later than immediately
	 * after the callback or
	 * <CODE>startApp</CODE>
	 * returns back to the implementation.
	 * 
	 * @param ticker - the ticker object used on this screen
	 */
	public void setPolishTicker( Ticker ticker)
	{
		this.ticker = ticker;
		if (ticker == null) {
			this.screenHeight = this.originalScreenHeight;
		} else {
			int tickerHeight = ticker.getItemHeight(this.screenWidth, this.screenWidth );
			this.screenHeight = this.originalScreenHeight - tickerHeight;
		}
		if (isShown()) {
			repaint();
		}
	}
	//#endif
	
	//#ifndef polish.skipTicker
	/**
	 * Gets the ticker used by this Screen.
	 * 
	 * @return ticker object used, or null if no ticker is present
	 */
	public Ticker getPolishTicker()
	{
		return this.ticker;
	}
	//#endif
		
	/**
	 * Handles key events.
	 * 
	 * WARNING: When this method should be overwritten, one need
	 * to ensure that super.keyPressed( int ) is called!
	 */
	protected void keyPressed(int keyCode) {
		//#if polish.debug.error
		try {
		//#endif
			//#debug
			Debug.debug("keyPressed: [" + keyCode + "].");
			int gameAction = -1;
			
			//#if tmp.menuFullScreen
				//#ifdef polish.key.ReturnKey:defined
					//#= if ( (keyCode == ${polish.key.ReturnKey}) && (this.backCommand != null) ) {
							callCommandListener( this.backCommand );
							repaint();
							//# return;
					//# }
				//#endif
				if (keyCode == LEFT_SOFT_KEY) {
					if ( this.menuSingleLeftCommand != null) {
						callCommandListener( this.menuSingleLeftCommand );
						return;
					} else {
						if (!this.menuOpened 
								&& this.menuContainer != null 
								&&  this.menuContainer.size() != 0 ) 
						{
							this.menuOpened = true;
							repaint();
							return;
						} else {
							gameAction = Canvas.FIRE;
						}
					}
				} else if (keyCode == RIGHT_SOFT_KEY) {
					if (!this.menuOpened && this.menuSingleRightCommand != null) {
						callCommandListener( this.menuSingleRightCommand );
						return;
					}
				}
				boolean doReturn = false;
				if (keyCode != LEFT_SOFT_KEY && keyCode != RIGHT_SOFT_KEY ) {
					gameAction = getGameAction( keyCode );
				} else {
					doReturn = true;
				}
				if (this.menuOpened) {
					if (keyCode == RIGHT_SOFT_KEY ) {
						this.menuOpened = false;
					} else if ( gameAction == Canvas.FIRE ) {
						int focusedIndex = this.menuContainer.getFocusedIndex();
						Command cmd = (Command) this.menuCommands.get( focusedIndex );
						this.menuOpened = false;
						callCommandListener( cmd );
					} else { 
						this.menuContainer.handleKeyPressed(keyCode, gameAction);
					}
					repaint();
					return;
				}
				if (doReturn) {
					return;
				}
			//#endif
			if (gameAction == -1) {
				gameAction = getGameAction(keyCode);
			}
			boolean processed = handleKeyPressed(keyCode, gameAction);
			//#ifdef polish.debug.debug
				if (!processed) {
					//#debug
					Debug.debug("unable to handle key [" + keyCode + "].");
				}
			//#endif
			if (processed) {
				//#if polish.useFullScreen && polish.api.nokia-ui
					requestRepaint();
				//#else
					repaint();
				//#endif
			}
		//#if polish.debug.error
		} catch (Exception e) {
			//#debug error
			Debug.debug("keyPressed() threw an exception", e );
		}
		//#endif
	}
	
	
	/**
	 * Just maps the event to the the keyPressed method.
	 * 
	 * @param keyCode the code of the key, which is pressed repeatedly
	 */
	protected void keyRepeated(int keyCode) {
		keyPressed(keyCode);
	}
	
	//#ifdef polish.useDynamicStyles	
	/**
	 * Retrieves the CSS selector for this screen.
	 * The CSS selector is used for the dynamic assignment of styles -
	 * that is the styles are assigned by the usage of the screen and
	 * not by a predefined style-name.
	 * With the #style preprocessing command styles are set in a static way, this method
	 * yields in a faster GUI and is recommended. When in a style-sheet
	 * dynamic styles are used, e.g. "form>p", than the selector of the
	 * screen is needed.
	 * This abstract method needs only be implemented, when dynamic styles
	 * are used: #ifdef polish.useDynamicStyles
	 * 
	 * @return the name of the appropriate CSS Selector for this screen.
	 */
	protected abstract String createCssSelector();	
	//#endif
	
	/**
	 * Retrieves all root-items of this screen.
	 * The root items are those in first hierarchy, in a Form this is 
	 * a Container for example.
	 * The default implementation does return an empty array, since apart
	 * from the container no additional items are used.
	 * Subclasses which use more root items than the container needs
	 * to override this method.
	 * 
	 * @return the root items an array, the array can be empty but not null.
	 */
	protected Item[] getRootItems() {
		return new Item[0];
	}
	
	/**
	 * Handles the key-pressed event.
	 * Please note, that implementation should first try to handle the
	 * given key-code, before the game-action is processed.
	 * 
	 * @param keyCode the code of the pressed key, e.g. Canvas.KEY_NUM2
	 * @param gameAction the corresponding game-action, e.g. Canvas.UP
	 * @return
	 */
	protected boolean handleKeyPressed( int keyCode, int gameAction ) {
		return this.container.handleKeyPressed(keyCode, gameAction);
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#setCommandListener(javax.microedition.lcdui.CommandListener)
	 */
	public void setCommandListener(CommandListener listener) {
		this.cmdListener.realCommandListener = listener;
	}
	
	//#ifdef tmp.menuFullScreen
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#addCommand(javax.microedition.lcdui.Command)
	 */
	public void addCommand(Command cmd) {
		if (this.menuCommands == null) {
			this.menuCommands = new ArrayList( 6, 50 );
			//#style menu, default
			 this.menuContainer = new Container( true );
		}
		//#style menuitem, menu, default
		StringItem menuItem = new StringItem( null, cmd.getLabel(), Item.HYPERLINK );
		int type = cmd.getCommandType(); 
		//#ifdef polish.key.ReturnKey:defined
			if ( type == Command.BACK ) {
				this.backCommand = cmd;
			}
		//#endif
		if ( (this.menuSingleRightCommand == null)
			&& (type == Command.BACK || type == Command.CANCEL ) ) 
		{
			// this is a command for the right side of the menu:
			this.menuSingleRightCommand = cmd;
			if (isShown()) {
				repaint();
			}
			return;
		}
		this.menuContainer.add( menuItem );
		if (this.menuContainer.size() == 1) {
			this.menuSingleLeftCommand = cmd;
		} else {
			this.menuSingleLeftCommand = null;
		}
		this.menuCommands.add( cmd );
		if (isShown()) {
			repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#removeCommand(javax.microedition.lcdui.Command)
	 */
	public void removeCommand(Command cmd) {
		if (this.menuCommands == null) {
			return;
		}
		int index = this.menuCommands.indexOf(cmd);
		if (index == -1) {
			return;
		}
		this.menuCommands.remove(index);
		if (this.menuSingleLeftCommand == cmd ) {
			this.menuSingleLeftCommand = null;
			this.menuContainer.remove(index);			
		} else if (this.menuSingleRightCommand == cmd) {
			this.menuSingleRightCommand = null;
		} else {
			this.menuContainer.remove(index);			
		}
		if (isShown()) {
			repaint();
		}
	}
	//#endif
	
	/**
	 * Sets the commands of the given item
	 * 
	 * @param item the item which has at least one command 
	 */
	protected void setItemCommands( Item item ) {
		this.focusedItem = item;
		// now add any commands which are associated with the item:
		if (item.commands != null) {
			Command[] commands = (Command[]) item.commands.toArray( new Command[item.commands.size()] );
			for (int i = 0; i < commands.length; i++) {
				Command command = commands[i];
				addCommand(command);
			}
		}
	}
	
	/**
	 * Removes the commands of the given item.
	 *  
	 * @param item the item which has at least one command 
	 */
	protected void removeItemCommands( Item item ) {
		if (item.commands != null) {
			Command[] commands = (Command[]) item.commands.toArray( new Command[item.commands.size()] );
			for (int i = 0; i < commands.length; i++) {
				Command command = commands[i];
				removeCommand(command);
			}
		}
		this.focusedItem = null;
	}
	
	/**
	 * Calls the command listener with the specified command.
	 * 
	 * @param cmd the command wich should be issued to the listener
	 */
	protected void callCommandListener( Command cmd ) {
		if (this.cmdListener != null) {
			try {
				this.cmdListener.commandAction(cmd, this );
			} catch (Exception e) {
				//#debug error
				Debug.debug("unable to process command [" + cmd.getLabel() + "]: " + e.getMessage(), e );
			}
		}
	}
	
	/**
	 * Retrieves the available height for this screen.
	 * This is equivalent to the Canvas#getHeight() method.
	 * This method cannot be overriden for Nokia's FullScreen though.
	 * So this method is used insted.
	 * 
	 * @return the available height in pixels.
	 */
	public int getAvailableHeight() {
		return this.screenHeight - this.titleHeight;
	}
	 
	//#if polish.useFullScreen && polish.api.nokia-ui 
	/**
	 * Requests a repaint of the screen.
	 * This request is ignored when the paint method is currently called.
	 * J2ME Polish needs to implement this method because the
	 * Nokia FullCanvas implementation crashes sometimes when calling repaint() in
	 * the middlet of the paint method.
	 * This method is only available when the Nokia FullCanvas is used. 
	 */
	public void requestRepaint() {
		if (this.isInPaintMethod  || (System.currentTimeMillis() - this.lastPaintTime) < 100) {
			this.repaintRequested = true;
			return;
		}
		repaint();
	}
	//#endif
	
	
	//#if polish.debugVerbose && polish.useDebugGui
	public void commandAction( Command command, Displayable screen ) {
		StyleSheet.display.setCurrent( this );
	}
	//#endif
	

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
	 */
	protected void pointerPressed(int x, int y) {
		//#debug
		System.out.println("PointerPressed at " + x + ", " + y );
		try {
			// check for scroll-indicator:
			if (  this.paintScrollIndicator &&
					(x > this.scrollIndicatorX) &&
					(y > this.scrollIndicatorY) &&
					(x < this.scrollIndicatorX + this.scrollIndicatorWidth) &&
					(y < this.scrollIndicatorY + this.scrollIndicatorWidth) ) 
			{
				// the scroll-indicator has been clicked:
				int gameAction;
				if ( (( !this.paintScrollIndicatorUp) || (y > this.scrollIndicatorY + this.scrollIndicatorWidth/2)) && this.paintScrollIndicatorDown) {
					gameAction = Canvas.DOWN;
				} else {
					gameAction = Canvas.UP;
				}
				//#if tmp.menuFullScreen
					if (this.menuOpened) {
						this.menuContainer.handleKeyPressed( 0, gameAction );
					} else {
						handleKeyPressed( 0, gameAction );
					}
				//#else
					handleKeyPressed( 0, gameAction );
				//#endif
				repaint();
				return;
			}
			//#ifdef tmp.menuFullScreen
				// check if one of the command buttons has been pressed:
				if (y > this.screenHeight) {
					if (x >= this.menuRightCommandX && this.menuRightCommandX != 0) {
						if (this.menuOpened) {
							this.menuOpened = false;
						} else if (this.menuSingleRightCommand != null) {
							callCommandListener(this.menuSingleRightCommand );
						}
						repaint();
						return;
					} else if (x <= this.menuLeftCommandX){
						// assume that the left command has been pressed:
						if (this.menuSingleLeftCommand != null) {
							this.callCommandListener(this.menuSingleLeftCommand);
						} else if (this.menuOpened ) {
							// the "SELECT" command has been clicked:
							int focusedIndex = this.menuContainer.getFocusedIndex();
							Command cmd = (Command) this.menuCommands.get( focusedIndex );
							callCommandListener( cmd );						
							this.menuOpened = false;
						} else {
							this.menuOpened = true;
						}
						repaint();
						return;
					}
				} else if (this.menuOpened) {
					this.menuOpened = false;
					// a menu-item could have been selected:
					if (this.menuContainer.handlePointerPressed( x, y )) {
						int focusedIndex = this.menuContainer.getFocusedIndex();
						Command cmd = (Command) this.menuCommands.get( focusedIndex );
						callCommandListener( cmd );						
					} else {
						//#ifdef tmp.useTitle
							y -= this.titleHeight;
						//#endif
						int focusedIndex = this.menuContainer.getFocusedIndex();
						Item item = this.menuContainer.get( focusedIndex );
						if (y > item.yTopPos  && y < item.yBottomPos
								&& x > item.xLeftPos && x < item.xRightPos) {
							Command cmd = (Command) this.menuCommands.get( focusedIndex );
							callCommandListener( cmd );	
						}
					}
					repaint();
					return;
				}
			//#endif
			// let the screen handle the pointer pressing:
			//#ifdef tmp.usingTitle
				boolean processed = handlePointerPressed( x, y - this.titleHeight);
				if (processed) {
					repaint();
				}
			//#else
				//# boolean processed = handlePointerPressed( x, y );
				if (processed) {
					repaint();
				}
			//#endif
			
			//#ifdef polish.debug.debug
				if (!processed) {
					Debug.debug("PointerPressed at " + x + ", " + y + " not processed.");					
				}
			//#endif
		} catch (Exception e) {
			//#debug error
			Debug.debug("PointerPressed at " + x + "," + y + " resulted in exception", e );
		}
	}
	//#endif
	
	//#ifdef polish.hasPointerEvents
	/**
	 * Handles the pressing of a pointer.
	 * This method should be overwritten only when the polish.hasPointerEvents 
	 * preprocessing symbol is defined.
	 * When the screen could handle the pointer pressing, it needs to 
	 * return true.
	 * The default implementation returns the result of calling the container's
	 *  handlePointerPressed-method
	 *  
	 * @param x the x position of the pointer pressing
	 * @param y the y position of the pointer pressing
	 * @return true when the pressing of the pointer was actually handled by this item.
	 */
	protected boolean handlePointerPressed( int x, int y ) {
		return this.container.handlePointerPressed(x, y);
	}
	//#endif
	
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#setFullScreenMode(boolean)
	 */
	public void setFullScreenMode(boolean enable) {
		//#if polish.midp2 && !tmp.fullScreen
			super.setFullScreenMode(enable);
		//#endif
	    //#if tmp.usingTitle || tmp.menuFullScreen
			this.showTitleOrMenu = !enable;
		//#endif
		//#ifdef tmp.menuFullScreen
			if (enable) {
				this.screenHeight = this.fullScreenHeight;
			} else {
				this.screenHeight = this.originalScreenHeight;
				//#if tmp.usingTitle
					//this.screenHeight = this.originalScreenHeight - this.titleHeight;
				//#else
					//this.screenHeight = this.originalScreenHeight;
				//#endif
			}
		//#endif
		repaint();
	}
	
	
	//#ifdef polish.midp2
	protected void sizeChanged(int width, int height) {
		//#ifdef tmp.menuFullScreen
			this.fullScreenHeight = height;
			this.screenHeight = height - this.menuBarHeight;
			this.originalScreenHeight = this.screenHeight;
		//#else
			this.screenHeight = height;
		//#endif
		this.scrollIndicatorY = this.screenHeight - this.scrollIndicatorWidth - 1;
		if (this.container != null) {
			this.container.setVerticalDimensions( 0,  this.screenHeight - (this.titleHeight + this.infoHeight) );
		}
	}
	//#endif
	
	/**
	 * <p>A command listener which forwards commands to the item command listener in case it encounters an item command.</p>
	 *
	 * <p>copyright Enough Software 2004</p>
	 * <pre>
	 * history
	 *        09-Jun-2004 - rob creation
	 * </pre>
	 * @author Robert Virkus, robert@enough.de
	 */
	class ForwardCommandListener implements CommandListener {
		public CommandListener realCommandListener;

		/* (non-Javadoc)
		 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
		 */
		public void commandAction(Command cmd, Displayable thisScreen) {
			//check if the given command is from the currently focused item:
			if ((Screen.this.focusedItem != null) && (Screen.this.focusedItem.itemCommandListener != null)) {
				Item item = Screen.this.focusedItem;
				if ( item.commands.contains(cmd)) {
					item.itemCommandListener.commandAction(cmd, item);
					return;
				}
			}
			// now invoke the usual command listener:
			if (this.realCommandListener != null) {
				this.realCommandListener.commandAction(cmd, thisScreen);
			}
		}
		
	}
}
