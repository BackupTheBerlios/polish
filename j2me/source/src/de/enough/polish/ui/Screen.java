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
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ui;


import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.Locale;

//#ifdef polish.Screen.imports:defined
	//#include ${polish.Screen.imports}
//#endif


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
//#if polish.Bugs.needsNokiaUiForSystemAlerts && !polish.SystemAlertNotUsed
	//#define tmp.needsNokiaUiForSystemAlerts
//#endif
//#if polish.useFullScreen
	//#if (polish.midp2 && !tmp.needsNokiaUiForSystemAlerts) && (!polish.useMenuFullScreen || polish.hasCommandKeyEvents)
		//#define tmp.fullScreen
		//# extends Canvas
	//#elif polish.classes.fullscreen:defined
		//#define tmp.fullScreen
		//#= extends ${polish.classes.fullscreen}
	//#endif
//#endif
//#if !tmp.fullScreen
	extends Canvas
//#endif
implements AccessibleCanvas
{
	
	//#if tmp.fullScreen || polish.midp1 || (polish.usePolishTitle == true)
		//#define tmp.usingTitle
		protected StringItem title;
		//#ifdef polish.css.title-style
			private Style titleStyle;
		//#endif
		//#ifdef polish.Vendor.Motorola
			//#define tmp.ignoreMotorolaTitleCall
			private boolean ignoreMotorolaTitleCall = true;
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
	private Item subTitle;
	protected int subTitleHeight;
	protected int titleHeight;
	private Background background;
	protected Border border;
	protected Style style;
	/** the screen height minus the ticker height and the height of the menu bar */
	protected int screenHeight;
	/** the screen height minus the height of the menu bar */
	protected int originalScreenHeight;
	protected final int screenWidth;
	//#ifndef polish.skipTicker
		private Ticker ticker;
	//#endif
	protected String cssSelector;
	private ForwardCommandListener cmdListener;
	protected Container container;
	private boolean isLayoutVCenter;
	private boolean isLayoutBottom;
	private boolean isInitialised;
	//#if polish.ScreenChangeAnimation.forward:defined
		protected Command lastTriggeredCommand;
	//#endif	
	//#if (polish.useMenuFullScreen && tmp.fullScreen) || polish.needsManualMenu
		//#define tmp.menuFullScreen
		/** the real, complete height of the screen - this includes title, subtitle, content and menubar */
		protected int fullScreenHeight;
		protected int menuBarHeight;
		//#ifdef polish.key.ReturnKey:defined
			private Command backCommand;
		//#endif
		
		//#if polish.Screen.FireTriggersOkCommand == true
			private Command okCommand;
		//#endif
		//#if polish.MenuBar.useExtendedMenuBar == true
			private final MenuBar menuBar;
			//#define tmp.useExternalMenuBar
		//#else
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
			private Container menuContainer;
			private ArrayList menuCommands;
			private boolean menuOpened;
			private Font menuFont;
			private int menuFontColor = 0;
			//#ifdef polish.ScreenWidth:defined
				//#= private final int menuMaxWidth = (  ${ polish.ScreenWidth } * 2 ) / 3;
			//#else
				private final int menuMaxWidth = ( getWidth() * 2 ) / 3;
			//#endif
			private int menuBarColor = 0xFFFFFF;
			//#ifdef polish.hasPointerEvents
				private int menuRightCommandX;
				private int menuLeftCommandX;
			//#endif
		//#endif
	//#endif
	/** The Gauge Item which should be animated by this screen */
	Item gauge;
	/** The currently focused items which has item-commands */
	private Item focusedItem;
	private boolean paintScrollIndicator;
	private boolean paintScrollIndicatorUp;
	private boolean paintScrollIndicatorDown;
	private int scrollIndicatorColor;
	private int scrollIndicatorX; // left x position of scroll indicator
	private int scrollIndicatorY; // top y position of scroll indicator
	private int scrollIndicatorWidth; // width and height of the indicator
	//#if polish.css.scrollindicator-up-image || polish.css.scrollindicator-down-image 
		private Image scrollIndicatorUpImage; 
		private Image scrollIndicatorDownImage; 
	//#endif		
	//#if tmp.usingTitle || tmp.menuFullScreen
		private boolean showTitleOrMenu = true;
	//#endif
	/** an info text which is shown e.g. when some content is added to textfields */
	private StringItem infoItem;
	/** determines whether the info text should be shown */
	private boolean showInfoItem;
	protected int infoHeight;
	//#if tmp.fullScreen && polish.midp2 && polish.Bugs.fullScreenInPaint
		//#define tmp.fullScreenInPaint
		private boolean isInFullScreenMode;
	//#endif
	//#ifdef polish.css.foreground-image
		private Image foregroundImage;
		private int foregroundX;
		private int foregroundY;
	//#endif
	//#if polish.css.clip-screen-info
		private boolean clipScreenInfo;
	//#endif	
	//#if polish.blackberry
		public boolean keyPressedProcessed;
	//#endif
	protected int contentX;
	protected int contentY;
	protected int contentWidth;
	protected int contentHeight;
	
	/**
	 * Creates a new screen
	 * 
	 * @param title the title, or null for no title
	 * @param style the style of this screen
	 * @param createDefaultContainer true when the default container should be created.
	 */
	public Screen( String title, Style style, boolean createDefaultContainer ) {
		super();
		//#if !(polish.Bugs.fullScreenInShowNotify || polish.Bugs.fullScreenInPaint || tmp.needsNokiaUiForSystemAlerts)
			//#if tmp.fullScreen && polish.midp2
				//# super.setFullScreenMode( true );
			//#endif			
		//#endif
			
		// get the screen dimensions:
		// this is a bit complicated, since Nokia's FullCanvas fucks
		// up when calling super.getHeight(), so we need to use hardcoded values...
		
		//#ifdef tmp.menuFullScreen
			//#if polish.needsManualMenu && !tmp.fullScreen
				this.fullScreenHeight = getHeight();
			//#else
				//#if tmp.needsNokiaUiForSystemAlerts
					//#ifdef polish.NokiaFullCanvasHeight:defined
						//#= this.fullScreenHeight = ${ polish.NokiaFullCanvasHeight };
					//#else
						//# this.fullScreenHeight = getHeight();
					//#endif
				//#else
					//#ifdef polish.FullCanvasHeight:defined
						//#= this.fullScreenHeight = ${ polish.FullCanvasHeight };
					//#else
						//# this.fullScreenHeight = getHeight();
					//#endif
				//#endif
			//#endif
			//TODO this.menuBarHeight is 0 during initialisation...
			this.screenHeight = this.fullScreenHeight; // - this.menuBarHeight;
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
			//# super.setCommandListener(this.cmdListener);
		//#endif
		//#ifdef tmp.useExternalMenuBar
			//#style menubar, menu, default
			this.menuBar = new MenuBar( this );
		//#endif
		setTitle( title );
	}
		
	/**
	 * Initialises this screen before it is painted for the first time.
	 */
	private void init() {
		//#debug
		System.out.println("Initialising screen " + this );
		boolean startAnimationThread = false;
		if (StyleSheet.animationThread == null) {
			StyleSheet.animationThread = new AnimationThread();
			startAnimationThread = true;
		}
		//#ifdef polish.Screen.initCode:defined
			//#include ${polish.Screen.initCode}
		//#endif
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
			//#ifdef tmp.useExternalMenuBar
				this.menuBarHeight = this.menuBar.getItemHeight( this.screenWidth, this.screenWidth );
				this.scrollIndicatorWidth = this.menuBar.contentHeight + this.menuBar.paddingTop + this.menuBar.paddingBottom;
				this.scrollIndicatorX = this.screenWidth / 2 - this.scrollIndicatorWidth / 2;
				this.scrollIndicatorY = this.fullScreenHeight - this.menuBar.marginBottom + 1 - this.scrollIndicatorWidth;
				//System.out.println("without ExternalMenu: scrollIndicatorY=" + this.scrollIndicatorY + ", screenHeight=" + this.screenHeight + ", FullScreenHeight=" + this.fullScreenHeight );	
				//System.out.println("Screen.init: menuBarHeight=" + this.menuBarHeight + " scrollIndicatorWidth=" + this.scrollIndicatorWidth );
			//#else
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
				int localMenuBarHeight = this.menuFont.getHeight();
				//#ifdef polish.Menu.PaddingBottom:defined
					//#= localMenuBarHeight += ${polish.Menu.PaddingBottom};  
				//#endif
				//#ifdef polish.Menu.PaddingTop:defined
					//#= localMenuBarHeight += ${polish.Menu.PaddingTop};  
				//#endif
				//#if !polish.Menu.PaddingBottom:defined && !polish.Menu.PaddingTop:defined
					localMenuBarHeight += 2;
				//#endif
				this.scrollIndicatorWidth = localMenuBarHeight;
				this.scrollIndicatorX = this.screenWidth / 2 - this.scrollIndicatorWidth / 2;
				this.scrollIndicatorY = this.fullScreenHeight - this.scrollIndicatorWidth;
				//System.out.println("without ExternalMenu: scrollIndicatorY=" + this.scrollIndicatorY + ", screenHeight=" + this.screenHeight + ", FullScreenHeight=" + this.fullScreenHeight + ", localMenuBarHeight=" + localMenuBarHeight);	
				//#ifdef polish.Menu.MarginBottom:defined 
					//#= localMenuBarHeight += ${polish.Menu.MarginBottom};
					//#= this.scrollIndicatorY -=  ${polish.Menu.MarginBottom};
				//#endif
				//#ifdef polish.Menu.MarginTop:defined 
					//#= localMenuBarHeight += ${polish.Menu.MarginTop};
				//#endif
				this.menuBarHeight = localMenuBarHeight;
			//#endif
			int diff = this.originalScreenHeight - this.screenHeight;
			this.originalScreenHeight = this.fullScreenHeight - this.menuBarHeight;
			this.screenHeight = this.originalScreenHeight - diff;
			// set position of scroll indicator:
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
			
		//System.out.println("final: scrollIndicatorY=" + this.scrollIndicatorY + ", screenHeight=" + this.screenHeight + ", FullScreenHeight=" + this.fullScreenHeight );	
		if (this.container != null) {
			this.container.screen = this;
		}
		calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
		
		// start the animmation thread if necessary: 
		if (startAnimationThread) {
			StyleSheet.animationThread.start();
		}
		this.isInitialised = true;
	}
	
	/**
	 * Calculates and sets the content area for this screen.
	 * Usually no items are painted outside of the specified area.
	 * This method knows about the title, subtitle, infoarea and ticker
	 * and adjusts the content area accordingly
	 * 
	 * @param x left start of the content area, might later be adjusted by an external scrollindicator
	 * @param y top start of the content area, is adjusted by the title height, subtitle height, 
	 *        info height and maybe ticker height (when the ticker should be painted at the top).
	 * @param width width of the content area, might later be adjusted by an external scrollindicator
	 * @param height height of the content area, is adjusted by the title height, subtitle height, 
	 *        info height and ticker height.
	 */
	protected void calculateContentArea( int x, int y, int width, int height ) {
		//#debug
		System.out.println("setContentArea(" + x + ", " + y + ", " + width + ", " + height + ")");
		int topHeight = this.titleHeight + this.subTitleHeight + this.infoHeight; 
		y += topHeight;
		int tickerHeight = 0;
		//#ifndef polish.skipTicker			
			if (this.ticker != null) {
				tickerHeight = this.ticker.getItemHeight( this.screenWidth, this.screenWidth );
			} 	
		//#endif
		//TODO allow ticker at the top as well	
		height -= topHeight + tickerHeight;
		this.contentX = x;
		this.contentY = y;
		this.contentWidth = width;
		this.contentHeight = height;
		if (this.container != null) {
			this.container.setVerticalDimensions( y, y + height );
		}
	}
	
	

	/**
	 * Initialises this screen and informs all items about being painted soon.
	 */
	public void showNotify() {
		//#debug
		System.out.println("showNotify " + this + " isInitialised=" + this.isInitialised);
		try {
			//#ifdef polish.Screen.showNotifyCode:defined
				//#include ${polish.Screen.showNotifyCode}
			//#endif
			//#if tmp.fullScreen && polish.midp2 && polish.Bugs.fullScreenInShowNotify
				//# super.setFullScreenMode( true );
				//#ifdef polish.FullCanvasHeight:defined
					//#= this.fullScreenHeight = ${polish.FullCanvasHeight};
				//#else
					this.fullScreenHeight = getHeight();
				//#endif
				this.screenHeight = this.fullScreenHeight - this.menuBarHeight;
				this.originalScreenHeight = this.screenHeight;
				this.scrollIndicatorY = this.screenHeight + 1; //- this.scrollIndicatorWidth - 1;
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
			//#if tmp.ignoreMotorolaTitleCall
				this.ignoreMotorolaTitleCall = true;
			//#endif
			//#if tmp.fullScreen && polish.midp2 && !polish.blackberry
				// this is needed on Sony Ericsson for example,
				// since the fullscreen mode is not resumed automatically
				// when the previous screen was in the "normal" mode:
				//#if ! tmp.fullScreenInPaint
					//# super.setFullScreenMode( true );
				//#endif
			//#endif
		
			// init components:
			int	width = this.screenWidth;
			//#ifdef tmp.menuFullScreen
				//#ifdef tmp.useExternalMenuBar
					if (!this.menuBar.isInitialised) {
						this.menuBar.init( width, width );
					}
				//#else
					if (this.menuOpened) {
						if (!this.menuContainer.isInitialised) {
							this.menuContainer.init( width, width );
						}
					} else
				//#endif
			//#endif
			if (this.container != null && !this.container.isInitialised) {
				this.container.init( width, width );
			}
			if (this.gauge != null && !this.gauge.isInitialised) {
				this.gauge.init( width, width );
			}
			//#ifdef tmp.usingTitle
				if (this.title != null && !this.title.isInitialised) {
					this.title.init( width, width );
				}
			//#endif
			//#ifndef polish.skipTicker
				if (this.ticker != null && !this.ticker.isInitialised) {
					this.ticker.init( width, width );
				}
			//#endif		
		} catch (Exception e) {
			//#debug error
			System.out.println("error while calling showNotify" + e );
		}
		//#ifdef polish.Vendor.Siemens
			this.showNotifyTime = System.currentTimeMillis();
		//#endif
	}
	
	/**
	 * Unregisters this screen and notifies all items that they will not be shown anymore.
	 */
	public void hideNotify() {
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
		//#ifdef tmp.ignoreMotorolaTitleCall
			this.ignoreMotorolaTitleCall = true;
		//#endif
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
		if (this.container != null) {
			// use the same style for the container - but ignore the background and border settings:
			this.container.setStyle(style, true);
		}
		this.isLayoutVCenter = (( style.layout & Item.LAYOUT_VCENTER ) == Item.LAYOUT_VCENTER);
		this.isLayoutBottom = ! this.isLayoutVCenter 
							&& (( style.layout & Item.LAYOUT_BOTTOM ) == Item.LAYOUT_BOTTOM);
		//#if polish.css.scrollindicator-up-image
			String scrollUpUrl = style.getProperty("scrollindicator-up-image");
			if (scrollUpUrl != null) {
				try {
					this.scrollIndicatorUpImage = StyleSheet.getImage(scrollUpUrl, null, true);
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load scroll up image" + e );
				}
			} else {
				this.scrollIndicatorUpImage = null;
			}
		//#endif		
		//#if polish.css.scrollindicator-down-image
			String scrollDownUrl = style.getProperty("scrollindicator-down-image");
			if (scrollDownUrl != null) {
				try {
					this.scrollIndicatorDownImage = StyleSheet.getImage(scrollDownUrl, null, true);
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load scroll down image" + e );
				}
			} else {
				this.scrollIndicatorDownImage = null;
			}
		//#endif		
		//#if polish.css.scrollindicator-up-image && polish.css.scrollindicator-down-image
			if (this.scrollIndicatorUpImage != null && this.scrollIndicatorDownImage != null) {
				int height = this.scrollIndicatorUpImage.getHeight() + this.scrollIndicatorDownImage.getHeight();
				int width = Math.max( this.scrollIndicatorUpImage.getWidth(), this.scrollIndicatorDownImage.getWidth() );
				this.scrollIndicatorWidth = width;
				this.scrollIndicatorX = this.screenWidth / 2 - width / 2;
				//#ifdef tmp.menuFullScreen
					//#ifdef tmp.useExternalMenuBar
						this.scrollIndicatorY = this.fullScreenHeight - this.menuBar.marginBottom + 1 - height;
					//#else
						this.scrollIndicatorY = this.fullScreenHeight - height - 1;
					//#endif					
				//#elif polish.vendor.Siemens
					// set the position of scroll indicator for Siemens devices 
					// on the left side, so that the menu-indicator is visible:
					this.scrollIndicatorWidth = width;
					this.scrollIndicatorX = 0;
					this.scrollIndicatorY = this.screenHeight - height - 1;
				//#else
					// set position of scroll indicator:
					this.scrollIndicatorWidth = width;
					this.scrollIndicatorX = this.screenWidth - width - 1;
					this.scrollIndicatorY = this.screenHeight - height - 1;
				//#endif					
			}
		//#endif

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
			
		//#ifdef polish.css.foreground-image
			String foregroundImageUrl = this.style.getProperty("foreground-image");
			if (foregroundImageUrl == null) {
				this.foregroundImage = null;
			} else {
				try {
					this.foregroundImage = StyleSheet.getImage(foregroundImageUrl, null,true);
					//#ifdef polish.css.foreground-x
						Integer xInteger = this.style.getIntProperty("foreground-x");
						if (xInteger != null) {
							this.foregroundX = xInteger.intValue();
						}
					//#endif
					//#ifdef polish.css.foreground-y
						Integer yInteger = this.style.getIntProperty("foreground-y");
						if (yInteger != null) {
							this.foregroundY = yInteger.intValue();
						}
					//#endif
				} catch (IOException e) {
					//#debug error
					System.out.println("Unable to load foreground-image [" + foregroundImageUrl + "]: " + e);
				}
			}
		//#endif
		//#if polish.css.clip-screen-info
			Boolean clipScreenInfoBool = style.getBooleanProperty( "clip-screen-info" );
			if (clipScreenInfoBool != null) {
				this.clipScreenInfo = clipScreenInfoBool.booleanValue();
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
				//#ifdef tmp.useExternalMenuBar
					animated = animated | this.menuBar.animate();
				//#else
					if (this.menuOpened) {
						animated = animated | this.menuContainer.animate();
					} else
				//#endif
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
			return animated;
		} catch (Exception e) {
			//#debug error
			System.out.println("animate() threw an exception" + e );
			return false;
		}
	}
	
	/**
	 * Paints the screen.
	 * When you subclass Screen you should override paintScreen(Graphics g) instead, if possible.
	 * 
	 * @param g the graphics context.
	 * @see #paintScreen(Graphics)
	 */
	public void paint(Graphics g) {
		//#if polish.Bugs.losesFullScreen
			//# super.setFullScreenMode( true );
		//#endif
		//#if tmp.fullScreenInPaint
			if (!this.isInFullScreenMode) {
				//# super.setFullScreenMode( true );
				this.isInFullScreenMode = true;
				//#if tmp.menuFullScreen
					//#ifdef polish.FullCanvasHeight:defined
						//#= this.fullScreenHeight = ${polish.FullCanvasHeight};
					//#else
						this.fullScreenHeight = getHeight();
					//#endif
					this.screenHeight = this.fullScreenHeight - this.menuBarHeight;
					this.originalScreenHeight = this.screenHeight;
					this.scrollIndicatorY = this.screenHeight + 1; //- this.scrollIndicatorWidth - 1 - this.menuBarHeight;
				//#endif
			}
		//#endif
		//#if !tmp.menuFullScreen
			int translateY = g.getTranslateY();
			if (translateY != 0 && this.screenHeight == this.originalScreenHeight) {
				this.screenHeight -= translateY;
				this.scrollIndicatorY -= translateY;
				//#debug
				System.out.println("Adjusting screenheight from " + this.originalScreenHeight + " to " + this.screenHeight );
				if (this.container != null) {
					int y = translateY;
					calculateContentArea( 0, y, this.screenWidth, this.screenHeight - y );
				}
			}
		//#endif
		//#if tmp.fullScreen && polish.FullCanvasSize:defined && polish.Bugs.setClipForFullScreenNeeded
			g.translate( -g.getTranslateX(), -g.getTranslateY() );
			//#= g.setClip( 0, 0, ${polish.FullCanvasWidth}, ${polish.FullCanvasHeight} );
		//#endif
		//#ifdef polish.debug.error
		try {
		//#endif
			// paint background:
			if (this.background != null) {
				//System.out.println("Screen (" + this + ": using background...");
				//#ifdef tmp.menuFullScreen
					this.background.paint(0, 0, this.screenWidth, this.fullScreenHeight, g);
				//#else
					this.background.paint(0, 0, this.screenWidth, this.screenHeight, g);
				//#endif
			} else {
				//System.out.println("Screen (" + this + ": clearing area...");
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
			if (this.subTitle != null) {
				this.subTitle.paint( 0, tHeight, 0, this.screenWidth, g );
				tHeight += this.subTitleHeight;
			}
			int infoItemY = tHeight;
			//#if polish.clip-screen-info
				if (this.showInfoItem && this.clipScreenInfo) {			
					tHeight += this.infoHeight;
				}
			//#endif
			// protect the title, ticker and the full-screen-menu area:
			g.setClip(0, tHeight, this.screenWidth, this.screenHeight - tHeight );
			//g.translate( 0, tHeight );
			// paint content:
			//System.out.println("starting to paint content of screen");
			paintScreen( g );
			//System.out.println("done painting content of screen");
			

			
			//g.translate( 0, - tHeight );
			// allow painting outside of the screen again:
			//#ifdef tmp.menuFullScreen
			 	g.setClip(0, 0, this.screenWidth, this.fullScreenHeight );
			//#else
			 	g.setClip(0, 0, this.screenWidth, this.originalScreenHeight );
			//#endif
			 
			// paint info element:
			if (this.showInfoItem) {			
				this.infoItem.paint( 0, infoItemY, 0, this.screenWidth, g );
			}
 	
			//#ifndef polish.skipTicker
			 	if (this.ticker != null) {
			 		//System.out.println("painting ticker... at 0, " + this.screenHeight );
			 		//TODO allow to paint ticker at the top of the screen...
			 		this.ticker.paint( 0, this.contentY + this.contentHeight, 0, this.screenWidth, g );
			 	}
			//#endif
			
			// paint border:
			if (this.border != null) {
				this.border.paint(0, 0, this.screenWidth, this.screenHeight, g);
			}
			
			//#if polish.ScreenInfo.enable == true
				ScreenInfo.paint( g, tHeight, this.screenWidth );
			//#endif
				
			// paint menu in full-screen mode:
			//#ifdef tmp.menuFullScreen
				//#ifdef tmp.useExternalMenuBar
					this.menuBar.paint(0, this.screenHeight, 0, this.screenWidth, g);
					if (this.menuBar.isOpened) {
						this.paintScrollIndicator = this.menuBar.paintScrollIndicator;
						this.paintScrollIndicatorUp = this.menuBar.canScrollUpwards;
						this.paintScrollIndicatorDown = this.menuBar.canScrollDownwards;
					}
				//#else
					if (this.menuOpened) {
						tHeight -= this.infoHeight;
						int menuHeight = this.menuContainer.getItemHeight(this.menuMaxWidth, this.menuMaxWidth);
						int y = this.originalScreenHeight - menuHeight;
						if (y < tHeight) {
							this.paintScrollIndicator = true;
							this.paintScrollIndicatorUp = (this.menuContainer.yOffset != 0);
							this.paintScrollIndicatorDown = ( (this.menuContainer.focusedIndex != this.menuContainer.size() - 1)
									&& (this.menuContainer.yOffset + menuHeight > this.originalScreenHeight - tHeight)) ;
							y = tHeight; 
							this.menuContainer.setVerticalDimensions(y, this.originalScreenHeight);
						} else {
							this.paintScrollIndicator = false;
						}
						g.setClip(0, tHeight, this.screenWidth, this.originalScreenHeight - tHeight );
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
								//#ifdef polish.i18n.useDynamicTranslations
									menuText = Locale.get( "polish.command.select" ); 
								//#elifdef polish.command.select:defined
									//#= menuText = "${polish.command.select}";
								//#else
									menuText = "Select";
								//#endif
							} else {
								if (this.menuSingleLeftCommand != null) {
									menuText = this.menuSingleLeftCommand.getLabel();
								} else {
									//#ifdef polish.i18n.useDynamicTranslations
										menuText = Locale.get( "polish.command.options" ); 
									//#elifdef polish.command.options:defined
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
							//#ifdef polish.Menu.MarginLeft:defined
								//#= int menuX = ${polish.Menu.MarginLeft};
							//#else
								int menuX = 2;
							//#endif
							//#ifdef polish.Menu.MarginTop:defined
								//#= g.drawString(menuText, menuX, this.originalScreenHeight + ${polish.Menu.MarginTop}, Graphics.TOP | Graphics.LEFT );
							//#else
								g.drawString(menuText, menuX, this.originalScreenHeight + 2, Graphics.TOP | Graphics.LEFT );
							//#endif
							if ( this.menuOpened ) {
								// draw cancel string:
								//#ifdef polish.i18n.useDynamicTranslations
									menuText = Locale.get( "polish.command.cancel" ); 
								//#elifdef polish.command.cancel:defined
									//#= menuText = "${polish.command.cancel}";
								//#else
									menuText = "Cancel";
								//#endif
								//#ifdef polish.Menu.MarginRight:defined
									//#= menuX = ${polish.Menu.MarginRight};
								//#elifdef polish.Menu.MarginLeft:defined
									menuX = 2;
								//#endif
								//#ifdef polish.Menu.MarginTop:defined
									//#= g.drawString(menuText, this.screenWidth - menuX, this.originalScreenHeight + ${polish.Menu.MarginTop}, Graphics.TOP | Graphics.RIGHT );
								//#else
									g.drawString(menuText, this.screenWidth - menuX, this.originalScreenHeight + 2, Graphics.TOP | Graphics.RIGHT );
								//#endif
								//#ifdef polish.hasPointerEvents
									this.menuRightCommandX = this.screenWidth - 2 - this.menuFont.stringWidth( menuText );
								//#endif
							}
						}
						if (this.menuSingleRightCommand != null && !this.menuOpened) {
							g.setColor( this.menuFontColor );
							g.setFont( this.menuFont );
							String menuText = this.menuSingleRightCommand.getLabel();
							//#ifdef polish.Menu.MarginRight:defined
								//#= int menuX = ${polish.Menu.MarginRight};
							//#else
								int menuX = 2;
							//#endif
							//#ifdef polish.Menu.MarginTop:defined
								//#= g.drawString(menuText, this.screenWidth - menuX, this.originalScreenHeight + ${polish.Menu.MarginTop}, Graphics.TOP | Graphics.RIGHT );
							//#else
								g.drawString(menuText, this.screenWidth - menuX, this.originalScreenHeight + 2, Graphics.TOP | Graphics.RIGHT );
							//#endif
							//#ifdef polish.hasPointerEvents
								this.menuRightCommandX = this.screenWidth - menuX 
									- this.menuFont.stringWidth( menuText );
							//#endif
						}
					} // if this.showTitleOrMenu || this.menuOpened
				//#endif
			//#endif
			// paint scroll-indicator in the middle of the menu:
			if (this.paintScrollIndicator) {
				g.setColor( this.scrollIndicatorColor );
				int x = this.scrollIndicatorX;
				int y = this.scrollIndicatorY;
				//System.out.println("paint: this.scrollIndicatorY=" + this.scrollIndicatorY);
				int width = this.scrollIndicatorWidth;
				int halfWidth = width / 2;
				if (this.paintScrollIndicatorUp) {
					//#if polish.css.scrollindicator-up-image
						if (this.scrollIndicatorUpImage != null) {
							g.drawImage(this.scrollIndicatorUpImage, x, y, Graphics.TOP | Graphics.LEFT );
						} else {
					//#endif						
						//#ifdef polish.midp2
							g.fillTriangle(x, y + halfWidth-1, x + width, y + halfWidth-1, x + halfWidth, y );
						//#else
							g.drawLine( x, y + halfWidth-1, x + width, y + halfWidth-1 );
							g.drawLine( x, y + halfWidth-1, x + halfWidth, y );
							g.drawLine( x + width, y + halfWidth-1, x + halfWidth, y );
						//#endif
					//#if polish.css.scrollindicator-up-image
						}
					//#endif
				}
				if (this.paintScrollIndicatorDown) {
					//#if polish.css.scrollindicator-down-image
						if (this.scrollIndicatorDownImage != null) {
							if (this.scrollIndicatorUpImage != null) {
								y += this.scrollIndicatorUpImage.getHeight() + 1;
							}
							g.drawImage(this.scrollIndicatorDownImage, x, y, Graphics.TOP | Graphics.LEFT );
						} else {
					//#endif						
						//#ifdef polish.midp2
							g.fillTriangle(x, y + halfWidth+1, x + width, y + halfWidth+1, x + halfWidth, y + width );
						//#else
							g.drawLine( x, y + halfWidth+1, x + width, y + halfWidth+1 );
							g.drawLine( x, y + halfWidth+1, x + halfWidth, y + width );
							g.drawLine(x + width, y + halfWidth+1, x + halfWidth, y + width );
						//#endif
					//#if polish.css.scrollindicator-down-image
						}
					//#endif
				}
			}
			//#ifdef polish.css.foreground-image
				if (this.foregroundImage != null) {
					g.drawImage( this.foregroundImage, this.foregroundX, this.foregroundY, Graphics.TOP | Graphics.LEFT  );
				}
			//#endif
		
		//#ifdef polish.debug.error
		} catch (RuntimeException e) {
			//#debug error
			System.out.println( "unable to paint screen (" + getClass().getName() + "):" + e );
		}
		//#endif
	}
	
	/**
	 * Paints the screen.
	 * This method also needs to set the protected variables
	 * paintScrollIndicator, paintScrollIndicatorUp and paintScrollIndicatorDown.
	 * 
	 * @param g the graphics on which the screen should be painted
	 * @see #contentX
	 * @see #contentY
	 * @see #contentWidth
	 * @see #contentHeight
	 * @see #paintScrollIndicator
	 * @see #paintScrollIndicatorUp
	 * @see #paintScrollIndicatorDown
	 */
	protected void paintScreen( Graphics g ) {
		int y = this.contentY;
		int x = this.contentX;
		int height = this.contentHeight;
		int width = this.contentWidth;
		int containerHeight = this.container.getItemHeight( width, width);
		this.paintScrollIndicator = false; // defaults to false
		if (containerHeight > height ) {
			this.paintScrollIndicator = true;
			this.paintScrollIndicatorUp = (this.container.yOffset != 0)
				&& (this.container.focusedIndex != 0);
			this.paintScrollIndicatorDown = ( (this.container.focusedIndex != this.container.size() - 1)
					 && (this.container.yOffset + containerHeight > height) );
		} else if (this.isLayoutVCenter) {
			/*
			//#debug
			System.out.println("Screen: adjusting y from [" + y + "] to [" + ( y + (height - containerHeight) / 2) + "] - containerHeight=" + containerHeight);
			*/
			y += ((height - containerHeight) / 2);
		} else if (this.isLayoutBottom) {
			y += (height - containerHeight);
		}
		this.container.paint( x, y, x, x + width, g );
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
		setTitle( s, null );
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
	 * @param s the new title, or null for no title
	 * @param tStyle the new style for the title, is ignored when null
	 */
	public void setTitle( String s, Style tStyle)
	{
		//#debug
		System.out.println("Setting title " + s );
		//#ifdef tmp.ignoreMotorolaTitleCall
			if (s == null) {
				if (this.ignoreMotorolaTitleCall) {
					this.ignoreMotorolaTitleCall = false;
					return;
				}
				//return;
			}
		//#endif
		if (s != null) {
			//#style title, default
			this.title = new StringItem( null, s );
			this.title.screen = this;
			if ( tStyle != null ) {
				this.title.setStyle( tStyle );
			}
			//#ifdef polish.css.title-style
				else if (this.titleStyle != null) {
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
				this.titleHeight = this.title.getItemHeight( this.screenWidth, this.screenWidth );
			//#endif
		} else {
			this.title = null;
			this.titleHeight = 0;
		}
		calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
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
			this.infoItem.screen = this;
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
		
		if (this.isInitialised && this.container != null) {
			int previousContentHeight = this.contentHeight;
			calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
			int differentce = this.contentHeight - previousContentHeight;
			//System.out.println("ADJUSTING CONTAINER.YOFFSET BY " + differentce + " PIXELS");
			this.container.yOffset += differentce;
		} else {
			calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
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
		if (ticker != null) {
			ticker.screen = this;
			// initialise ticker, so that subsequently ticker.itemHeight can be called:
			ticker.getItemHeight(this.screenWidth, this.screenWidth );
			//System.out.println("setTicker(): tickerHeight=" + tickerHeight );
			//this.screenHeight = this.originalScreenHeight - tickerHeight;
		}
		//System.out.println("setTicker(): screenHeight=" + this.screenHeight + ", original=" + this.originalScreenHeight );
		calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
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
	 * 
	 * @param keyCode The code of the pressed key
	 */
	public synchronized void keyPressed(int keyCode) {
		try {
			//#debug
			System.out.println("keyPressed: [" + keyCode + "].");
			int gameAction = -1;
			//#if polish.blackberry
				this.keyPressedProcessed = true;
			//#endif

			//#if tmp.menuFullScreen
				/*
				//#ifdef polish.key.ReturnKey:defined
					//#if  polish.key.ReturnKey == polish.key.ClearKey
						//#define tmp.checkReturnKeyLater
					//#else
						//#= if ( (keyCode == ${polish.key.ReturnKey}) && (this.backCommand != null) ) {
								callCommandListener( this.backCommand );
								repaint();
								//# return;
						//# }
					//#endif
				//#endif
				 * 
				 */
				//#ifdef tmp.useExternalMenuBar
					if (this.menuBar.handleKeyPressed(keyCode, 0)) {
						repaint();
						return;
					}
					if (this.menuBar.isSoftKeyPressed) {
						//#if polish.blackberry
							this.keyPressedProcessed = false;
						//#endif
						return;
					}
				//#else
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
						//#if polish.blackberry
							this.keyPressedProcessed = false;
						//#endif
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
			//#endif
			if (gameAction == -1) {
				gameAction = getGameAction(keyCode);
			}
			//#if (polish.Screen.FireTriggersOkCommand == true) && tmp.menuFullScreen
				if (gameAction == FIRE && keyCode != Canvas.KEY_NUM5 && this.okCommand != null) {
					callCommandListener(this.okCommand);
					return;
				}
			//#endif
			boolean processed = handleKeyPressed(keyCode, gameAction);
			//#ifdef polish.debug.debug
				if (!processed) {
					//#debug
					System.out.println("unable to handle key [" + keyCode + "].");
				}
			//#endif
			//#if tmp.menuFullScreen && polish.key.ReturnKey:defined
			// # if  tmp.checkReturnKeyLater
				if (!processed) {
					//#= if ( (keyCode == ${polish.key.ReturnKey}) && (this.backCommand != null) ) {
							callCommandListener( this.backCommand );
							processed = true;
					//# }
				}
			//#endif
			//#if polish.blackberry
				this.keyPressedProcessed = processed;
			//#endif
			if (processed) {
				repaint();
			}
		} catch (Exception e) {
			//#if !polish.debug.error 
				e.printStackTrace();
			//#endif
			//#debug error
			System.out.println("keyPressed() threw an exception" + e );
		}
	}
	
	
	/**
	 * Just maps the event to the the keyPressed method.
	 * 
	 * @param keyCode the code of the key, which is pressed repeatedly
	 */
	public void keyRepeated(int keyCode) {
		keyPressed(keyCode);
	}

	/**
	 * Is called when a key is released.
	 * 
	 * @param keyCode the code of the key, which has been released
	 */
	public void keyReleased(int keyCode) {
		// ignore
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
	 * @return true when the key-event was processed
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
		//#debug
		System.out.println("adding command [" + cmd.getLabel() + "].");
		int cmdType = cmd.getCommandType();
		//#if polish.Screen.FireTriggersOkCommand == true
			if ( cmdType == Command.OK 
					&&  (this.okCommand == null || this.okCommand.getPriority() < cmd.getPriority() ) ) 
			{
				this.okCommand = cmd;
			}
		//#endif
		//#ifdef polish.key.ReturnKey:defined
			//#if polish.blackberry
			if ( cmdType == Command.BACK || cmdType == Command.CANCEL || 
					(cmdType == Command.EXIT && this.backCommand == null) ) 
			{
				
			//#else
				//# if ( cmd.getCommandType() == Command.BACK ) {
			//#endif
				this.backCommand = cmd;
			}
		//#endif
		//#ifdef tmp.useExternalMenuBar
			this.menuBar.addCommand(cmd);
			if (this.isShown()) {
				repaint();
			}
		//#else
			if (this.menuCommands == null) {
				this.menuCommands = new ArrayList( 6, 50 );
				//#style menu, default
				 this.menuContainer = new Container( true );
			}
			if ( (cmdType == Command.BACK || cmdType == Command.CANCEL ) ) 
			{
				if ( (this.menuSingleRightCommand == null)
						|| (cmd.getPriority() < this.menuSingleRightCommand.getPriority())	)
				{
					// okay set the right menu command:
					if (this.menuSingleRightCommand != null) {
						// the right menu command is replaced by the new one,
						// so insert the original one into the options-menu:
						//#style menuitem, menu, default
						StringItem menuItem = new StringItem( null, this.menuSingleRightCommand.getLabel(), Item.HYPERLINK );
						this.menuContainer.add( menuItem );
						if (this.menuContainer.size() == 1) {
							this.menuSingleLeftCommand = this.menuSingleRightCommand;
						} else {
							this.menuSingleLeftCommand = null;
						}
						this.menuCommands.add( this.menuSingleRightCommand );
					}					
					// this is a command for the right side of the menu:
					this.menuSingleRightCommand = cmd;
					if (isShown()) {
						repaint();
					}
					return;
				}
			}
			//#style menuitem, menu, default
			StringItem menuItem = new StringItem( null, cmd.getLabel(), Item.HYPERLINK );
			if ( this.menuCommands.size() == 0 ) {
				this.menuCommands.add( cmd );
				this.menuContainer.add( menuItem );
				this.menuSingleLeftCommand = cmd;
			} else {
				this.menuSingleLeftCommand = null;
				// there are already several commands,
				// so add this cmd to the appropriate sorted position:
				int priority = cmd.getPriority();
				Command[] myCommands = (Command[]) this.menuCommands.toArray( new Command[ this.menuCommands.size() ]);
				boolean inserted = false;
				for (int i = 0; i < myCommands.length; i++) {
					Command command = myCommands[i];
					if ( cmd == command ) {
						return;
					}
					if (command.getPriority() > priority ) {
						this.menuCommands.add( i, cmd );
						this.menuContainer.add(i, menuItem);
						inserted = true;
						break;
					}
				}
				if (!inserted) {
					this.menuCommands.add( cmd );
					this.menuContainer.add( menuItem );
				}
			}
//			this.menuContainer.add( menuItem );
//			if (this.menuContainer.size() == 1) {
//				this.menuSingleLeftCommand = cmd;
//			} else {
//				this.menuSingleLeftCommand = null;
//			}
//			this.menuCommands.add( cmd );
			if (isShown()) {
				repaint();
			}
		//#endif
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#removeCommand(javax.microedition.lcdui.Command)
	 */
	public void removeCommand(Command cmd) {
		//#ifdef tmp.useExternalMenuBar
			this.menuBar.removeCommand(cmd);
			if (this.isShown()) {
				repaint();
			}
		//#else
			if (this.menuSingleRightCommand == cmd) {
				this.menuSingleRightCommand = null;
				//move another suitable command-item to the right-pos:
				if (this.menuCommands != null) {
					Command[] commands = (Command[]) this.menuCommands.toArray( new Command[ this.menuCommands.size()]);
					for (int i = 0; i < commands.length; i++) {
						Command command = commands[i];
						int type = command.getCommandType(); 
						if ( type == Command.BACK || type == Command.CANCEL ) {
							this.menuContainer.remove( i );
							this.menuCommands.remove( i );
							this.menuSingleRightCommand = command;
							break;
						}
					}
				}
				if (isShown()) {
					repaint();
				}
				return;
			}
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
			} else {
				this.menuContainer.remove(index);			
			}
			if (isShown()) {
				repaint();
			}
		//#endif
	}
	//#endif
	
	/**
	 * Sets the commands of the given item
	 * 
	 * @param item the item which has at least one command 
	 */
	protected void setItemCommands( Item item ) {
		this.focusedItem = item;
		if (item.commands != null) {
			Command[] commands = (Command[]) item.commands.toArray( new Command[item.commands.size()] );
			for (int i = 0; i < commands.length; i++) {
				Command command = commands[i];
				//#ifdef tmp.useExternalMenuBar
					this.menuBar.addCommand(command);
				//#else
					addCommand(command);
				//#endif
			}
		}
		//#ifdef tmp.useExternalMenuBar
			if (isShown()) {
				repaint();
			}
		//#endif
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
				//#ifdef tmp.useExternalMenuBar
					this.menuBar.removeCommand(command);
				//#else
					removeCommand(command);
				//#endif
			}
		}
		this.focusedItem = null;
		//#ifdef tmp.useExternalMenuBar
			if (isShown()) {
				repaint();
			}
		//#endif
	}
	
	/**
	 * Calls the command listener with the specified command.
	 * 
	 * @param cmd the command wich should be issued to the listener
	 */
	protected void callCommandListener( Command cmd ) {
		//#if polish.ScreenChangeAnimation.forward:defined
			this.lastTriggeredCommand = cmd;
		//#endif
		if (this.cmdListener != null) {
			try {
				this.cmdListener.commandAction(cmd, this );
			} catch (Exception e) {
				//#debug error
				System.out.println("Screen: unable to process command [" + cmd.getLabel() + "]" + e  );
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
	 	
	
	//#if polish.debugVerbose && polish.useDebugGui
	public void commandAction( Command command, Displayable screen ) {
		StyleSheet.display.setCurrent( this );
	}
	//#endif
	

	//#ifdef polish.hasPointerEvents
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Canvas#pointerPressed(int, int)
	 */
	public void pointerPressed(int x, int y) {
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
				//#debug
				System.out.println("ScrollIndicator has been clicked... ");
				// the scroll-indicator has been clicked:
				int gameAction;
				if ( (( !this.paintScrollIndicatorUp) || (y > this.scrollIndicatorY + this.scrollIndicatorWidth/2)) && this.paintScrollIndicatorDown) {
					gameAction = Canvas.DOWN;
				} else {
					gameAction = Canvas.UP;
				}
				//#if tmp.menuFullScreen
					//#ifdef tmp.useExternalMenuBar
						if (this.menuBar.isOpened) {
							this.menuBar.handleKeyPressed( 0, gameAction );
						} else {
							handleKeyPressed( 0, gameAction );
						}
					//#else
						if (this.menuOpened) {
							this.menuContainer.handleKeyPressed( 0, gameAction );
						} else {
							handleKeyPressed( 0, gameAction );
						}
					//#endif
				//#else
					handleKeyPressed( 0, gameAction );
				//#endif
				repaint();
				return;
			}
			//#ifdef tmp.menuFullScreen
				//#ifdef tmp.useExternalMenuBar
					if (this.menuBar.handlePointerPressed(x, y)) {
						repaint();
						return;
					}
				//#else
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
								//y -= this.titleHeight;
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
			//#endif
			// let the screen handle the pointer pressing:
			//#ifdef tmp.usingTitle
				//boolean processed = handlePointerPressed( x, y - (this.titleHeight + this.infoHeight + this.subTitleHeight) );
				boolean processed = handlePointerPressed( x, y  );
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
					System.out.println("PointerPressed at " + x + ", " + y + " not processed.");					
				}
			//#endif
		} catch (Exception e) {
			//#debug error
			System.out.println("PointerPressed at " + x + "," + y + " resulted in exception" + e );
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
		if (this.subTitle != null && this.subTitle.handlePointerPressed(x, y)) {
			return true;
		}
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
	
	
	//#if polish.midp2 && !polish.Bugs.needsNokiaUiForSystemAlerts 
	public void sizeChanged(int width, int height) {
		 //#if !polish.Bugs.sizeChangedReportsWrongHeight
			if (!this.isInitialised) {
				return;
			}
			//#debug
			System.out.println("Screen: sizeChanged to width=" + width + ", height=" + height );
			//#ifdef tmp.menuFullScreen
				this.fullScreenHeight = height;
				this.screenHeight = height - this.menuBarHeight;
				this.originalScreenHeight = this.screenHeight;
			//#else
				this.screenHeight = height;
				this.scrollIndicatorY = this.screenHeight - this.scrollIndicatorWidth - 1;
			//#endif
			this.scrollIndicatorY = height - this.scrollIndicatorWidth - 1;
			calculateContentArea( 0, 0, this.screenWidth, this.screenHeight  );
		//#endif
	}
	//#endif
	
	/**
	 * <p>A command listener which forwards commands to the item command listener in case it encounters an item command.</p>
	 *
	 * <p>Copyright Enough Software 2004, 2005</p>

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
			Item item = Screen.this.focusedItem;
			if ((item != null) && (item.itemCommandListener != null) && (item.commands != null)) {
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
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param item the item which is already shown on this screen.
	 */
	public void focus(Item item) {
		int index = this.container.itemsList.indexOf(item);
		if (index != -1) {
			//#debug
			System.out.println("Screen: focusing item " + index );
			this.container.focus( index, item, 0 );
			return;
		}
		//#debug warn
		System.out.println("Screen: unable to focus item (did not find it in the container) " + item);
	}
	
	/**
	 * Sets the subtitle element.
	 * The subtitle is drawn directly below of the title (above the info-item, if there
	 * is any) and is always shown (unless it is null).
	 * 
	 * @param subTitle the new subtitle element.
	 */
	protected void setSubTitle( Item subTitle ) {
		this.subTitle = subTitle;
		if (subTitle == null) {
			this.subTitleHeight = 0;
		} else {
			subTitle.screen = this;
			//#ifdef polish.ScreenWidth:defined
				//#= this.subTitleHeight = subTitle.getItemHeight(${polish.ScreenWidth}, ${polish.ScreenWidth});
			//#else
				this.subTitleHeight = subTitle.getItemHeight( this.screenWidth, this.screenWidth );
			//#endif
		}
		calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
	}
	
	
	//#if polish.Bugs.displaySetCurrentFlickers
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#isShown()
	 */
	public boolean isShown() {
		return (StyleSheet.currentScreen == this);
	}
	//#endif
	
	/**
	 * Retrieves the currently focused item.
	 * 
	 * @return the currently focused item, null when none is focused.
	 */
	public Item getCurrentItem() {
		if (this.container != null) {
			return this.container.focusedItem;
		}
		return null;
	}
		
	
//#ifdef polish.Screen.additionalMethods:defined
	//#include ${polish.Screen.additionalMethods}
//#endif
	
	

}
