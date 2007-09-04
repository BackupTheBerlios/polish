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

//#if polish.doja
import com.nttdocomo.ui.Frame;
//#endif

import de.enough.polish.ui.backgrounds.TranslucentSimpleBackground;
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
 * read/write ticker and title properties were moved to <code>Displayable</code>,
 * <code>Screen's</code> superclass.  The semantics of these methods have not changed.</P>
 * <HR>
 * 
 * @since MIDP 1.0
 * 
 */
public abstract class Screen
//#if polish.Bugs.needsNokiaUiForSystemAlerts && !polish.SystemAlertNotUsed
	//#define tmp.needsNokiaUiForSystemAlerts
//#endif
//#if polish.hasCommandKeyEvents || (polish.key.LeftSoftKey:defined && polish.key.RightSoftKey:defined)
	//#define tmp.hasCommandKeyEvents
//#endif
//#if polish.useFullScreen
	//#if (polish.midp2 && !tmp.needsNokiaUiForSystemAlerts) && (!polish.useMenuFullScreen || tmp.hasCommandKeyEvents)
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
	private final static int POSITION_TOP = 0;
	private final static int POSITION_LEFT = 1;
	
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

	
	//#if tmp.fullScreen || polish.midp1 || (polish.usePolishTitle == true)
		//#define tmp.usingTitle
		protected Item title;
		private boolean excludeTitleForBackground;
		//#ifdef polish.css.title-style
			private Style titleStyle;
		//#endif
		//#if polish.css.title-position
			private boolean paintTitleAtTop = true;
		//#endif	
		//#if polish.Vendor.Motorola || polish.Bugs.ignoreTitleCall
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
	protected Background background;
	protected Border border;
	protected Style style;
	/** the screen height minus the ticker height and the height of the menu bar */
	protected int screenHeight;
	/** the screen height minus the height of the menu bar */
	protected int originalScreenHeight;
	protected int screenWidth;
	//#ifndef polish.skipTicker
		private Ticker ticker;
		//#if polish.Ticker.Position:defined
			//#if top == ${ lowercase(polish.Ticker.Position) }
				//#define tmp.paintTickerAtTop
			//#else
				//#define tmp.paintTickerAtBottom
			//#endif
		//#elif polish.css.ticker-position
			private boolean paintTickerAtTop;
		//#else
			//#define tmp.paintTickerAtBottom
		//#endif
	//#endif
	protected String cssSelector;
	private ForwardCommandListener forwardCommandListener;
	protected Container container;
	private boolean isLayoutCenter;
	private boolean isLayoutRight;
	private boolean isLayoutVCenter;
	private boolean isLayoutBottom;
	private boolean isLayoutHorizontalShrink;
	private boolean isLayoutVerticalShrink;
	private boolean isInitialized;
	//#if polish.ScreenChangeAnimation.forward:defined
		protected Command lastTriggeredCommand;
	//#endif	
	//#if (polish.useMenuFullScreen && tmp.fullScreen) || polish.needsManualMenu
		//#define tmp.menuFullScreen
		/** the real, complete height of the screen - this includes title, subtitle, content and menubar */
		protected int fullScreenHeight;
		protected int menuBarHeight;
		private boolean excludeMenuBarForBackground;
		//#ifdef polish.key.ReturnKey:defined
			private Command backCommand;
		//#endif
		
		private Command okCommand;
		//#if polish.MenuBar.useExtendedMenuBar || polish.classes.MenuBar:defined
			//#if polish.classes.MenuBar:defined
				//#= private final ${polish.classes.MenuBar} menuBar;
			//#else
				private final MenuBar menuBar;
			//#endif
			//#define tmp.useExternalMenuBar
		//#else
			private Command menuSingleLeftCommand;
			private String menuLeftString;
			private Command menuSingleRightCommand;
			private String menuRightString;
			private Container menuContainer;
			private ArrayList menuCommands;
			private boolean menuOpened;
			private Font menuFont;
			private int menuFontColor = 0;
			private int menuBarColor = 0xFFFFFF;
			//#ifdef polish.hasPointerEvents
				private int menuRightCommandX;
				private int menuLeftCommandX;
			//#endif
		//#endif
	//#endif
	/** The currently focused items which has item-commands */
	/*package-private*/ Item focusedItem;
	//#if polish.useScrollBar || polish.classes.ScrollBar:defined
		//#define tmp.useScrollBar
		//#if polish.classes.ScrollBar:defined
			//#style scrollbar?
			//#= protected final ${polish.classes.ScrollBar} scrollBar = new ${polish.classes.ScrollBar}();
		//#else
			//#style scrollbar?
			protected final ScrollBar scrollBar = new ScrollBar();
		//#endif
		//#if polish.css.scrollbar-position
			protected boolean paintScrollBarOnRightSide = true;
		//#endif
		//#if polish.css.show-scrollbar
			protected boolean scrollBarVisible = true;
		//#endif
	//#elif !polish.deactivateScrollIndicator
		//#define tmp.useScrollIndicator
		private boolean paintScrollIndicator;
		private boolean paintScrollIndicatorUp;
		private boolean paintScrollIndicatorDown;
		private int scrollIndicatorColor;
		private int scrollIndicatorX; // left x position of scroll indicator
		private int scrollIndicatorY; // top y position of scroll indicator
		private int scrollIndicatorWidth; // width of the indicator
		private int scrollIndicatorHeight; // width of the indicator
		//#if polish.css.scrollindicator-up-image || polish.css.scrollindicator-down-image 
			private Image scrollIndicatorUpImage; 
			private Image scrollIndicatorDownImage; 
		//#endif
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
	private int marginLeft;
	private int marginRight;
	private int marginTop;
	private int marginBottom;
	//#if polish.css.separate-menubar
		private boolean separateMenubar = true;
	//#endif
	//#if polish.css.repaint-previous-screen
		private boolean repaintPreviousScreen;
		private AccessibleCanvas previousScreen;
		//#if !polish.Bugs.noTranslucencyWithDrawRgb
			private Background previousScreenOverlayBackground;
		//#endif
		//#if polish.ScreenOrientationCanChange && tmp.usingTitle
			private Background previousScreenTitleBackground;
			private int previousScreenTitleHeight;
		//#endif
	//#endif
	protected ScreenStateListener screenStateListener;
	private boolean isScreenChangeDirtyFlag;
	protected ItemStateListener itemStateListener;
//	private ArrayList stateNotifyQueue;
	private final Object paintLock = new Object();
	private ArrayList itemCommands;
	private Object data;
	/* The last time in ms when the user interacted with this screen. This value is used for stopping
	 * animations after a period of inactivity. This defaults to 3 minutes, but it can be set with the preprocessing
	 * variable polish.Animation.MaxIdleTime (integer with the number of ms, 60000 is one minute).
	 */
	protected long lastInteractionTime;


	/**
	 * Creates a new screen, this constructor can be used together with the //#style directive.
	 * 
	 * @param title the title, or null for no title
	 * @param createDefaultContainer true when the default container should be created.
	 */
	public Screen( String title, boolean createDefaultContainer ) {
		this( title, null, createDefaultContainer );
	}

	/**
	 * Creates a new screen, this constructor can be used together with the //#style directive.
	 * 
	 * @param title the title, or null for no title
	 * @param style the style of this screen
	 * @param createDefaultContainer true when the default container should be created.
	 */
	public Screen( String title, boolean createDefaultContainer, Style style ) {
		this( title, style, createDefaultContainer );
	}

	/**
	 * Creates a new screen
	 * 
	 * @param title the title, or null for no title
	 * @param style the style of this screen
	 * @param createDefaultContainer true when the default container should be created.
	 */
	public Screen( String title, Style style, boolean createDefaultContainer ) {
		super();
		//#if tmp.useScrollBar
			this.scrollBar.screen = this;
		//#endif
						
		// creating standard container:
		if (createDefaultContainer) {
			this.container = new Container( true );
			this.container.screen = this;
			this.container.isFocused = true;
		}
		this.style = style;
		this.forwardCommandListener = new ForwardCommandListener();
		//#ifndef tmp.menuFullScreen
			super.setCommandListener(this.forwardCommandListener);
		//#endif
		//#ifdef tmp.useExternalMenuBar
			//#style menubar, menu, default
			this.menuBar = new MenuBar( this );
		//#endif
		//#if tmp.fullScreen && polish.midp2 && !(polish.Bugs.fullScreenInPaint || tmp.needsNokiaUiForSystemAlerts)
			super.setFullScreenMode( true );
		//#endif
		setTitle( title );
	}
		
	/**
	 * Initialises this screen before it is painted for the first time.
	 */
	private void init() {
		//#debug
		System.out.println("Initialising screen " + this );
		//#if tmp.fullScreen && polish.midp2 && !(polish.Bugs.fullScreenInPaint || tmp.needsNokiaUiForSystemAlerts)
			super.setFullScreenMode( true );
		//#endif
			
		//#ifdef tmp.menuFullScreen
			//#if polish.Bugs.requiresHardcodedCanvasDimensionsInFullScreenMode && polish.FullCanvasHeight:defined
				//#= this.fullScreenHeight = ${polish.FullCanvasHeight};
			//#else
				this.fullScreenHeight = getHeight();
			//#endif
		//#endif
		this.screenHeight = getHeight();
		this.originalScreenHeight = this.screenHeight;		
		//#if tmp.menuFullScreen && polish.Bugs.requiresHardcodedCanvasDimensionsInFullScreenMode && polish.FullCanvasWidth:defined
			//#= this.screenWidth = ${polish.FullCanvasWidth};
		//#else
			this.screenWidth = getWidth();
		//#endif
		
		if (this.screenWidth == 0) {
			//#if polish.FullCanvasSize:defined && tmp.menuFullScreen
				//#= this.screenWidth = ${polish.FullCanvasWidth};
				//#= this.fullScreenHeight = ${polish.FullCanvasHeight};
			//#else
				return;
			//#endif
		} 
		
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
				int availableScreenWidth = this.screenWidth;
				//#if polish.css.separate-menubar
					if (!this.separateMenubar) {
						availableScreenWidth -= (this.marginLeft + this.marginRight);
					}
				//#endif
				//#if polish.ScreenOrientationCanChange
					if (this.screenWidth > this.screenHeight) {
						this.menuBar.setOrientationVertical( true );
					} else {
						this.menuBar.setOrientationVertical( false );
					}
				//#endif
				this.menuBarHeight = this.menuBar.getSpaceBottom( availableScreenWidth, this.fullScreenHeight);
				//#if tmp.useScrollIndicator
					int scrollWidth = this.menuBar.contentHeight + this.menuBar.paddingTop + this.menuBar.paddingBottom;
					int scrollHeight = scrollWidth;
					//#if polish.css.scrollindicator-up-image
						if (this.scrollIndicatorUpImage != null) {
							scrollWidth = this.scrollIndicatorUpImage.getWidth(); 
							scrollHeight = this.scrollIndicatorUpImage.getHeight();
						}
					//#elif polish.css.scrollindicator-down-image
						if (this.scrollIndicatorDownImage != null) {
							scrollWidth = this.scrollIndicatorDownImage.getWidth(); 
							scrollHeight = this.scrollIndicatorDownImage.getHeight();
						}
					//#endif
					this.scrollIndicatorWidth = scrollWidth;
					this.scrollIndicatorHeight = scrollHeight;
					this.scrollIndicatorX = (this.screenWidth >> 1) - (scrollWidth >> 1);
					int space = Math.max( 0, this.menuBarHeight - ((scrollHeight << 1) + 1) );
					//System.out.println("space=" + space + ", menubarHEIGHT="+ this.menuBarHeight);
					this.scrollIndicatorY = (this.fullScreenHeight - this.menuBarHeight) + (space >> 1);
				//#endif
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
					this.menuFontColor = menustyle.getFontColor();
					if (menustyle.font != null) {
						this.menuFont = menustyle.font;
					} else {
						this.menuFont = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM );				
					}			
				} else {
					this.menuFont = Font.getFont( Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM );
				}
				int localMenuBarHeight = this.menuFont.getHeight();
				//#ifdef polish.MenuBar.PaddingBottom:defined
					//#= localMenuBarHeight += ${polish.MenuBar.PaddingBottom};  
				//#endif
				//#ifdef polish.MenuBar.PaddingTop:defined
					//#= localMenuBarHeight += ${polish.MenuBar.PaddingTop};  
				//#endif
				//#if !polish.MenuBar.PaddingBottom:defined && !polish.MenuBar.PaddingTop:defined
					localMenuBarHeight += 2;
				//#endif
				//#if tmp.useScrollIndicator
					//# int scrollWidth = localMenuBarHeight;
					//# int scrollHeight = scrollWidth >> 1;
					//#if polish.css.scrollindicator-up-image
						if (this.scrollIndicatorUpImage != null) {
							scrollWidth = this.scrollIndicatorUpImage.getWidth(); 
							scrollHeight = this.scrollIndicatorUpImage.getHeight();
						}
					//#elif polish.css.scrollindicator-down-image
						if (this.scrollIndicatorDownImage != null) {
							scrollWidth = this.scrollIndicatorDownImage.getWidth(); 
							scrollHeight = this.scrollIndicatorDownImage.getHeight();
						}
					//#endif
					this.scrollIndicatorWidth = scrollWidth;
					this.scrollIndicatorHeight = scrollHeight;
					this.scrollIndicatorX = this.screenWidth / 2 - scrollWidth / 2;
					//# int space = Math.max( 0, localMenuBarHeight - ((scrollHeight << 1) + 1) );
					this.scrollIndicatorY = (this.fullScreenHeight - localMenuBarHeight) + (space >> 1);
				//#endif
				//System.out.println("without ExternalMenu: scrollIndicatorY=" + this.scrollIndicatorY + ", screenHeight=" + this.screenHeight + ", FullScreenHeight=" + this.fullScreenHeight + ", localMenuBarHeight=" + localMenuBarHeight);	
				//#ifdef polish.MenuBar.MarginBottom:defined 
					//#= localMenuBarHeight += ${polish.MenuBar.MarginBottom};
					//#= this.scrollIndicatorY -=  ${polish.MenuBar.MarginBottom};
				//#endif
				//#ifdef polish.MenuBar.MarginTop:defined 
					//#= localMenuBarHeight += ${polish.MenuBar.MarginTop};
				//#endif
				//#if polish.doja
					localMenuBarHeight = 0;
				//#endif
				this.menuBarHeight = localMenuBarHeight;
				updateMenuTexts();
			//#endif
			int diff = this.originalScreenHeight - this.screenHeight;
			this.originalScreenHeight = this.fullScreenHeight - this.menuBarHeight;
			this.screenHeight = this.originalScreenHeight - diff;
			// set position of scroll indicator:
		//#elif polish.vendor.Siemens && tmp.useScrollIndicator
			// set the position of scroll indicator for Siemens devices 
			// on the left side, so that the menu-indicator is visible:
			//# int scrollWidth = 12;
			//#if polish.css.scrollindicator-up-image
				if (this.scrollIndicatorUpImage != null) {
					scrollWidth = this.scrollIndicatorUpImage.getWidth(); 
				}
			//#elif polish.css.scrollindicator-down-image 
				if (this.scrollIndicatorDownImage != null) {
					scrollWidth = this.scrollIndicatorDownImage.getWidth(); 
				}
			//#endif
			this.scrollIndicatorWidth = scrollWidth;
			this.scrollIndicatorX = 0;
			this.scrollIndicatorY = this.screenHeight - (this.scrollIndicatorWidth + 1);
		//#elif tmp.useScrollIndicator
			// set position of scroll indicator:
			//# int scrollWidth = 12;
			//#if polish.css.scrollindicator-up-image
				if (this.scrollIndicatorUpImage != null) {
					scrollWidth = this.scrollIndicatorUpImage.getWidth(); 
				}
			//#elif polish.css.scrollindicator-down-image 
				if (this.scrollIndicatorDownImage != null) {
					scrollWidth = this.scrollIndicatorDownImage.getWidth(); 
				}
			//#endif
			this.scrollIndicatorWidth = scrollWidth;
			this.scrollIndicatorX = this.screenWidth - this.scrollIndicatorWidth;
			this.scrollIndicatorY = this.screenHeight - (this.scrollIndicatorWidth + 1);
		//#endif
			
		//System.out.println("final: scrollIndicatorY=" + this.scrollIndicatorY + ", screenHeight=" + this.screenHeight + ", FullScreenHeight=" + this.fullScreenHeight );	
		if (this.container != null) {
			this.container.screen = this;
		}
		int availableWidth = this.screenWidth - this.marginLeft + this.marginRight;
		//#if tmp.usingTitle
			if (this.title != null) {
				this.titleHeight = this.title.getItemHeight( availableWidth, availableWidth ); 
			}
		//#endif
		//#if !tmp.menuFullScreen
			this.screenHeight = getHeight();
		//#elif tmp.useExternalMenuBar
			this.menuBar.relativeY = this.screenHeight;
		//#endif
		calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
		
		this.isInitialized = true;
		// start the animmation thread if necessary: 
		if (startAnimationThread) {
			StyleSheet.animationThread.start();
		}
	}
	
	/**
	 * Reinitializes this screen and it's content area.
	 */
	protected void requestInit() {
		this.isInitialized = false;
		calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
		if (isShown()) {
			repaint();
		}
	}

		
	/**
	 * Calculates and sets the content area for this screen.
	 * Usually no items are painted outside of the specified area.
	 * This method knows about the title, subtitle, infoarea and ticker
	 * and adjusts the content area accordingly
	 * 
	 * @param x left start of the content area, might later be adjusted by an external scrollindicator
	 * @param y top start of the content area, is adjusted by the top margin, title height, subtitle height, 
	 *        info height and maybe ticker height (when the ticker should be painted at the top).
	 * @param width width of the content area, might later be adjusted by an external scrollindicator
	 * @param height height of the content area, is adjusted by the title height, subtitle height, 
	 *        info height and ticker height.
	 */
	protected void calculateContentArea( int x, int y, int width, int height ) {
		//#debug
		System.out.println("calculateContentArea(" + x + ", " + y + ", " + width + ", " + height + ")");
		if (width < 1 || height < 1 ) {
			//#debug info
			System.out.println("invalid content dimension, width=" + width + ", height=" + height);
			return;
		}
		
		int availableWidth = this.screenWidth - this.marginLeft - this.marginRight;
		//#if tmp.usingTitle
			if (this.title != null) {
				this.titleHeight = this.title.getItemHeight( availableWidth, availableWidth );
			}
		//#endif
		if (this.subTitle != null) {
			this.subTitle.relativeX = this.marginLeft;
			this.subTitle.relativeY = this.titleHeight;
			this.subTitleHeight = this.subTitle.getItemHeight( availableWidth, availableWidth );
		}
		x += this.marginLeft;
		width -= this.marginLeft + this.marginRight;
		y += this.marginTop;
		height -= this.marginTop + this.marginBottom;
		//#if !polish.css.title-position || !tmp.usingTitle
			int topHeight = this.titleHeight + this.subTitleHeight + this.infoHeight;
		//#else
			//# int topHeight = this.subTitleHeight + this.infoHeight;
			if (this.paintTitleAtTop) {
				topHeight += this.titleHeight;
			} else {
				height -= this.titleHeight;
			}
		//#endif
		y += topHeight;
		//#if tmp.useExternalMenuBar
			int space;
			//#if polish.MenuBar.Position:defined
				space = this.menuBar.getSpaceLeft( this.screenWidth, this.fullScreenHeight );
				x += space;
				width -= space;
				space = this.menuBar.getSpaceRight( this.screenWidth, this.fullScreenHeight );
				width -= space;
				space = this.menuBar.getSpaceTop( this.screenWidth, this.fullScreenHeight );
				y += space;
				height -= space;
			//#endif
				// this is already deducted...
				//			space = this.menuBar.getSpaceBottom( this.screenWidth, this.fullScreenHeight );
				//			height -= space;
		//#endif
		//#ifndef polish.skipTicker			
			int tickerHeight = 0;
			if (this.ticker != null) {
				tickerHeight = this.ticker.getItemHeight( width, width );
			} 	
			height -= topHeight + tickerHeight;
		//#else
			height -= topHeight;	
		//#endif
		//#if tmp.useScrollBar
			//#if polish.css.show-scrollbar
				if ( this.scrollBarVisible ) {
			//#endif
					if ( this.container != null ) {
						this.scrollBar.scrollBarHeight = height;
						//System.out.println("calculateContentArea for " + this + ": container.isInitialised=" + this.container.isInitialised );
						int scrollBarWidth = this.scrollBar.getItemWidth(width, width);
						int containerHeight = this.container.getItemHeight(width - scrollBarWidth, width - scrollBarWidth);
						if (containerHeight > height) {
							//System.out.println("calculateContentArea for" + this + ": scrollBar is required for containerHeight of " + containerHeight + ", availableHeight=" + height );					
							width -= scrollBarWidth;
						} else {
							//System.out.println("calculateContentArea for" + this + ": scrollBar is NOT required for containerHeight of " + containerHeight + ", availableHeight=" + height );					
							this.container.requestFullInit();
						}
					}
			//#if polish.css.show-scrollbar
				}
			//#endif
		//#endif
			
		// now set the content coordinates:	
		this.contentX = x;
		//#ifndef polish.skipTicker			
			//#if tmp.paintTickerAtTop
				y += tickerHeight;
			//#elif polish.css.ticker-position && !polish.TickerPosition:defined
				if (this.paintTickerAtTop) {
					y += tickerHeight;
				}
			//#endif
		//#endif
		this.contentY = y;
		this.contentWidth = width;
		this.contentHeight = height;
		//#debug
		System.out.println("calculateContentArea: x=" + this.contentX + ", y=" + this.contentY + ", width=" + this.contentWidth + ", height=" + this.contentHeight);
		if (this.container != null) {
			this.container.setScrollHeight( height );
		}
	}
	
	

	/**
	 * Initialises this screen and informs all items about being painted soon.
	 */
	public void showNotify() {
		//#debug
		System.out.println("showNotify " + this + " isInitialised=" + this.isInitialized);
		try {
			//#ifdef polish.Screen.showNotifyCode:defined
				//#include ${polish.Screen.showNotifyCode}
			//#endif
			//#if   tmp.fullScreen && polish.midp2 && polish.Bugs.fullScreenInShowNotify
				super.setFullScreenMode( true );
				this.isInitialized = false;
			//#elif tmp.fullScreen && polish.midp2 && !polish.blackberry && !tmp.fullScreenInPaint && !polish.Bugs.displaySetCurrentFlickers
				// this is needed on Sony Ericsson for example,
				// since the fullscreen mode is not resumed automatically
				// when the previous screen was in the "normal" mode:
				super.setFullScreenMode( true );
			//#endif
			if (!this.isInitialized) {
				init();
			}
			//#if polish.css.repaint-previous-screen
				if (this.repaintPreviousScreen) {
					//#if !polish.Bugs.noTranslucencyWithDrawRgb
						//#if polish.color.overlay:defined
							//#= this.previousScreenOverlayBackground = new TranslucentSimpleBackground( ${polish.color.overlay} );
						//#else
							this.previousScreenOverlayBackground = new TranslucentSimpleBackground( 0xAA000000 );
						//#endif
					//#endif
					Displayable currentDisplayable = StyleSheet.display.getCurrent();
					if (currentDisplayable == this) {
						currentDisplayable = StyleSheet.currentScreen;
					}
					if ( currentDisplayable != this && currentDisplayable instanceof AccessibleCanvas) {
						Screen screen = currentDisplayable instanceof Screen ? (Screen) currentDisplayable : null;
						if (screen != null) {
							// detect circles within previous-screen-queue:
							Screen previous = screen;
							while (previous != null) {
								if (previous.previousScreen instanceof Screen) {
									previous = (Screen) previous.previousScreen;
									if (previous == this) {
										currentDisplayable = (Displayable) previous.previousScreen;
										screen = currentDisplayable instanceof Screen ? (Screen) currentDisplayable : null;
										break;
									}
								} else {
									break;
								}
							}
						}
						//#if polish.ScreenOrientationCanChange && tmp.usingTitle
							if ( screen != null && this.screenWidth > this.screenHeight ) {
								Background titleBg = null;
								if (screen.title != null) {
									titleBg = screen.title.background;
									this.previousScreenTitleHeight = screen.titleHeight;
								} 
								if (titleBg == null && this.title != null) {
									titleBg = this.title.background;
									this.previousScreenTitleHeight = this.titleHeight;
								}
								this.previousScreenTitleBackground = titleBg;
							}
						//#endif
						if (this.previousScreen != null && screen != null) {
							if (screen.previousScreen != this) {
								this.previousScreen = screen; //(AccessibleCanvas) currentDisplayable;
							} else {
								screen.previousScreen = null;
//									System.out.println("1: showNotify of " + this + ", current=" + currentDisplayable + ", current.previous=" + screen.previousScreen );
							}
						} else {
//								System.out.println("2: showNotify of " + this + ", current=" + currentDisplayable);
							this.previousScreen = (AccessibleCanvas) currentDisplayable;
						}
					}
				}
			//#endif
			
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
			//#ifndef polish.skipTicker
				if (this.ticker != null) {
					this.ticker.showNotify();
				}
			//#endif
			//#if tmp.ignoreMotorolaTitleCall
				this.ignoreMotorolaTitleCall = true;
			//#endif
		
			// init components:
			int	width = this.screenWidth - (this.marginLeft + this.marginRight);
			//#ifdef tmp.menuFullScreen
				//#ifdef tmp.useExternalMenuBar
					if (!this.menuBar.isInitialized) {
						//#if polish.css.separate-menubar
							if (this.separateMenubar) {
								this.menuBar.init( width, width );								
							} else {
								this.menuBar.init( this.screenWidth, this.screenWidth );								
							}
						//#else
							this.menuBar.init( this.screenWidth, this.screenWidth );								
						//#endif
					}
				//#else
					if (this.menuOpened) {
						if (!this.menuContainer.isInitialized) {
							this.menuContainer.init( width, width );
						}
					} else
				//#endif
			//#endif
			if (this.container != null && !this.container.isInitialized) {
				this.container.init( width, width );
			}
			//#ifdef tmp.usingTitle
				if (this.title != null && !this.title.isInitialized) {
					this.title.init( width, width );
				}
			//#endif
			//#ifndef polish.skipTicker
				if (this.ticker != null && !this.ticker.isInitialized) {
					this.ticker.init( width, width );					
				}
			//#endif
			calculateContentArea(0, 0, this.screenWidth, this.screenHeight);

		} catch (Exception e) {
			//#debug error
			System.out.println("error while calling showNotify" + e );
		}

		// register this screen:
		StyleSheet.currentScreen = this;

		//#ifdef polish.Vendor.Siemens
			this.showNotifyTime = System.currentTimeMillis();
		//#endif
		//#if polish.ScreenInfo.enable
			ScreenInfo.setScreen( this );
		//#endif
		this.lastInteractionTime = System.currentTimeMillis();
	}
	
	/**
	 * Unregisters this screen and notifies all items that they will not be shown anymore.
	 */
	public void hideNotify() {
		//#if polish.css.repaint-previous-screen
			//TODO when the previousScreen reference is removed, there is no way how several popus can be handled correctly
			// e.g. an input form with a TextField as a popup that allows to enter symbols in another popup.
			// Previously the symbol-popup was painted in the background after returning to the input-popup.
			// However, not removing the reference might result in "memory leaks" when references to popup-screens are hold
			// while the "parent" screen should be removed from the memory. However - we just take this risk for now.
		 	// (rob, 2007-07-09)
			// this.previousScreen = null;
			//#if !polish.Bugs.noTranslucencyWithDrawRgb
				this.previousScreenOverlayBackground = null;
			//#endif
			//#if polish.ScreenOrientationCanChange && tmp.usingTitle
				this.previousScreenTitleBackground = null;
			//#endif
		//#endif
		//#ifdef polish.Vendor.Siemens
			// Siemens sometimes calls hideNotify directly
			// after showNotify for some reason.
			// So hideNotify checks how long the screen
			// has been shown - if not long enough,
			// the call will be ignored:
			//TODO this should be handled by a bug rather than doing it for the vendor... but: Siemens is dead anyhow ;-)
			if (System.currentTimeMillis() - this.showNotifyTime < 500) {
				//#debug
				System.out.println("Ignoring hideNotify on Siemens");
				return;
			}
		//#endif
		//#debug
		System.out.println("hideNotify " + this);
		//#if !polish.css.repaint-previous-screen
			// un-register this screen:
			if (StyleSheet.currentScreen == this) {
				StyleSheet.currentScreen = null;
			}
		//#endif
		Item[] items = getRootItems();
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			item.hideNotify();
		}
		if (this.container != null) {
			this.container.hideNotify();
		}
		//#ifndef polish.skipTicker
			if (this.ticker != null) {
				this.ticker.hideNotify();
			}
		//#endif
		//#ifdef tmp.ignoreMotorolaTitleCall
			this.ignoreMotorolaTitleCall = true;
		//#endif
		//#if polish.ScreenInfo.enable
			// de-register screen from ScreenInfo element:
			if (ScreenInfo.item.screen == this ) {
				ScreenInfo.setScreen( null );
			}
		//#endif
	}
	
	/**
	 * Sets the style of this screen.
	 * 
	 * @param style the style
	 */
	public void setStyle(Style style) {
		if (style != this.style && this.style != null ) {
			this.style.releaseResources();
		}
	 	//#debug
		System.out.println("Setting screen-style for " + getClass().getName() );
		this.style = style;
		this.background = style.background;
		this.border = style.border;
		this.marginTop = style.marginTop;
		this.marginBottom = style.marginBottom;
		this.marginLeft = style.marginRight;
		this.marginRight = style.marginRight;
		if (this.container != null) {
			// use the same style for the container - but ignore the background and border settings:
			this.container.setStyle(style, true);
		}
		this.isLayoutVCenter = (( style.layout & Item.LAYOUT_VCENTER ) == Item.LAYOUT_VCENTER);
		this.isLayoutBottom = !this.isLayoutVCenter 
							&& (( style.layout & Item.LAYOUT_BOTTOM ) == Item.LAYOUT_BOTTOM);
		this.isLayoutCenter = (( style.layout & Item.LAYOUT_CENTER ) == Item.LAYOUT_CENTER);
		this.isLayoutRight = !this.isLayoutCenter
							&& (( style.layout & Item.LAYOUT_RIGHT ) == Item.LAYOUT_RIGHT);
		this.isLayoutHorizontalShrink = (style.layout & Item.LAYOUT_SHRINK) == Item.LAYOUT_SHRINK; 
		this.isLayoutVerticalShrink = (style.layout & Item.LAYOUT_VSHRINK) == Item.LAYOUT_VSHRINK; 
		//#if polish.css.scrollindicator-up-image && tmp.useScrollIndicator
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
		//#if polish.css.scrollindicator-down-image && tmp.useScrollIndicator
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
		//#if polish.css.scrollindicator-up-image && polish.css.scrollindicator-down-image && tmp.useScrollIndicator
			if (this.scrollIndicatorUpImage != null && this.scrollIndicatorDownImage != null) {
				int height = this.scrollIndicatorUpImage.getHeight() + this.scrollIndicatorDownImage.getHeight();
				int width = Math.max( this.scrollIndicatorUpImage.getWidth(), this.scrollIndicatorDownImage.getWidth() );
				this.scrollIndicatorWidth = width;
				this.scrollIndicatorX = this.screenWidth / 2 - width / 2;
				//#ifdef tmp.menuFullScreen
					//#ifdef tmp.useExternalMenuBar
						this.scrollIndicatorY = this.fullScreenHeight - (this.menuBar.marginBottom + 1 + height);
					//#else
						this.scrollIndicatorY = this.fullScreenHeight - (height + 1);
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

		//#if polish.css.scrollindicator-color && tmp.useScrollIndicator
			Integer scrollIndicatorColorInt = style.getIntProperty( "scrollindicator-color" );
			if (scrollIndicatorColorInt != null) {
				this.scrollIndicatorColor = scrollIndicatorColorInt.intValue();
			}
		//#endif
		//#if tmp.usingTitle && polish.css.title-style
			this.titleStyle = (Style) style.getObjectProperty("title-style");
			if (this.titleStyle != null && this.title != null) {
				this.title.setStyle(this.titleStyle);
				int width = this.screenWidth - (this.marginLeft + this.marginRight);
				this.titleHeight = this.title.getItemHeight( width, width );
			} else {
		//#endif
		 //#if tmp.usingTitle
		 	if (this.title != null && this.title.isInitialized) {
		 		this.title.isInitialized = false;
				int width = this.screenWidth - (this.marginLeft + this.marginRight);
				this.titleHeight = this.title.getItemHeight( width, width );
		 	}
		 //#endif
		//#if tmp.usingTitle && polish.css.title-style				
			}
		//#endif
		//#if tmp.usingTitle && polish.css.title-position
			Integer titlePositionInt = style.getIntProperty("title-position");
			if (titlePositionInt == null && this.title != null && this.title.style != null) {
				titlePositionInt = this.title.style.getIntProperty("title-position");
			}
			if (titlePositionInt != null) {
				this.paintTitleAtTop = (titlePositionInt.intValue() == POSITION_TOP);
			}
			
		//#endif
		//#if tmp.useScrollBar && polish.css.scrollbar-style
			Style scrollbarStyle = (Style) style.getObjectProperty("scrollbar-style");
			if (scrollbarStyle != null) {
				this.scrollBar.setStyle( scrollbarStyle );
			}
		//#endif
		//#if tmp.useScrollBar && polish.css.scrollbar-position
			Integer scrollBarPositionInt = style.getIntProperty( "scrollbar-position" );
			if (scrollBarPositionInt == null && this.scrollBar.style != null) {
				scrollBarPositionInt = this.scrollBar.style.getIntProperty( "scrollbar-position" );
			}
			if (scrollBarPositionInt != null) {
				this.paintScrollBarOnRightSide = (scrollBarPositionInt.intValue() != POSITION_LEFT);
			}
		//#endif
		//#if tmp.useScrollBar && polish.css.show-scrollbar
			Boolean showScrollBarBool = style.getBooleanProperty("show-scrollbar");
			if (showScrollBarBool != null) {
				this.scrollBarVisible = showScrollBarBool.booleanValue();
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
		//#if polish.css.menubar-style  && tmp.useExternalMenuBar
			Style menuBarStyle = (Style) style.getObjectProperty("menubar-style");
			if (menuBarStyle != null) {
				this.menuBar.setStyle( menuBarStyle );
			}
		//#endif

		//#ifndef polish.skipTicker
			//#if polish.css.ticker-position
				Integer tickerPositionInt = style.getIntProperty("ticker-position");
				if (tickerPositionInt == null && this.ticker != null && this.ticker.style != null) {
					tickerPositionInt = this.ticker.style.getIntProperty("ticker-position");
				}
				if (tickerPositionInt != null) {
					this.paintTickerAtTop = (tickerPositionInt.intValue() == POSITION_TOP);
				}
			//#endif
		//#endif
		//#if polish.css.repaint-previous-screen
			Boolean repaintPreviousScreenBool = style.getBooleanProperty("repaint-previous-screen");
			if (repaintPreviousScreenBool != null) {
				this.repaintPreviousScreen = repaintPreviousScreenBool.booleanValue();
			}
		//#endif
		//#if polish.css.separate-menubar
			Boolean separateMenubarBool = style.getBooleanProperty("separate-menubar");
			if (separateMenubarBool != null) {
				this.separateMenubar = separateMenubarBool.booleanValue();
			}
		//#endif
		//#if tmp.menuFullScreen
			//#if polish.css.background-bottom
				Integer backgroundBottomInt = style.getIntProperty("background-bottom");
				if (backgroundBottomInt != null) {
					this.excludeMenuBarForBackground = backgroundBottomInt.intValue() == 1; 
				}
			//#endif
		//#endif
		//#ifdef tmp.usingTitle
			//#if polish.css.background-top	
				Integer backgroundTopInt = style.getIntProperty("background-top");
				if (backgroundTopInt != null) {
					this.excludeTitleForBackground = backgroundTopInt.intValue() == 1; 
				}
			//#endif
		//#endif
	}
	
	/**
	 * Animates this screen.
	 * Subclasses can override this method to create animations.
	 * All embedded items are also animated.
	 * 
	 * @param currentTime the current time in milliseconds
	 * @param repaintRegion the repaint area that needs to be updated when this item is animated
	 */
	public void animate( long currentTime, ClippingRegion repaintRegion) {
		if (!this.isInitialized) {
			return;
		}
		
		synchronized (this.paintLock) {
			try {
				boolean animated = false;
				if (this.background != null) {
					animated = this.background.animate();
				}
				if (this.border != null) {
					animated |= this.border.animate();
				}
				// for ensured backward compatibility call the standard animate method:
				animated |= animate();
				if (animated) {
					//#ifdef tmp.menuFullScreen
						repaintRegion.addRegion( 0,0, this.screenWidth, this.fullScreenHeight );
					//#else
						repaintRegion.addRegion( 0,0, this.screenWidth, this.screenHeight );
					//#endif
				}
				//#ifdef tmp.menuFullScreen
					//#ifdef tmp.useExternalMenuBar
						this.menuBar.animate( currentTime, repaintRegion );
					//#else
						if (this.menuOpened) {
							this.menuContainer.animate( currentTime, repaintRegion );
						} else
					//#endif
				//#endif
				if (this.container != null) {
					this.container.animate( currentTime, repaintRegion );
				}
				//#ifdef tmp.usingTitle
					if (this.title != null) {
						this.title.animate( currentTime, repaintRegion );
					}
				//#endif
				//#if polish.ScreenInfo.enable
					if (ScreenInfo.item != null && ScreenInfo.isVisible()) {
						ScreenInfo.item.animate( currentTime, repaintRegion );
					}
				//#endif
			} catch (Exception e) {
				//#debug error
				System.out.println("animate(currentTime, repaintRegion) threw an exception" + e );
			}
		}	
	}

	
	/**
	 * Animates this Screen.
	 * It's recommended to use animate( long, ClippingRegion ) instead for performance reasons.
	 * 
	 * @return true when at least one animated item needs a redraw/repaint.
	 * @see #animate(long, ClippingRegion)
	 */
	public boolean animate() {
		return false;
	}
	
	//#if polish.enableSmileySupport
	//# public void paint(javax.microedition.lcdui.Graphics g) {
	//#	de.enough.polish.ui.smiley.Graphics graphics = new de.enough.polish.ui.smiley.Graphics( g );
	//#	paint (graphics);
	//# }
	//#endif
	
	/**
	 * Paints the screen.
	 * When you subclass Screen you should override paintScreen(Graphics g) instead, if possible.
	 * 
	 * @param g the graphics context.
	 * @see #paintScreen(Graphics)
	 */
	public void paint(Graphics g) {
		//System.out.println("Painting screen "+ this + ", background == null: " + (this.background == null) + ", clipping=" + g.getClipX() + "/" + g.getClipY() + " - " + g.getClipWidth() + "/" + g.getClipHeight() );
		if (!this.isInitialized) {
			init();
		}
		synchronized (this.paintLock ) {
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
						//#if tmp.useScrollIndicator
							this.scrollIndicatorY = this.screenHeight + 1; //- this.scrollIndicatorWidth - 1 - this.menuBarHeight;
						//#endif
					//#endif
				}
			//#endif
				// test code for JP8 preview
//			//#if tmp.menuFullScreen
//				g.setClip( 0, 0, this.screenWidth, this.fullScreenHeight );
//			//#endif
			//#if !tmp.menuFullScreen
				// not needed since sizeChanged() takes care for that now (robert, 2007-07-28)
//				int translateY = g.getTranslateY();
//				if (false && translateY != 0 && this.screenHeight == this.originalScreenHeight) {
//					this.screenHeight -= translateY;
//					//#if tmp.useScrollIndicator 
//						this.scrollIndicatorY -= translateY;
//					//#endif
//					//#debug
//					System.out.println("Adjusting screenheight from " + this.originalScreenHeight + " to " + this.screenHeight );
//					if (this.container != null) {
//						int y = translateY;
//						calculateContentArea( 0, y, this.screenWidth, this.screenHeight - y );
//					}
//				}
			//#endif
			//#if tmp.fullScreen && polish.FullCanvasSize:defined && polish.Bugs.setClipForFullScreenNeeded
				g.translate( -g.getTranslateX(), -g.getTranslateY() );
				//#= g.setClip( 0, 0, ${polish.FullCanvasWidth}, ${polish.FullCanvasHeight} );
			//#endif
			//#if polish.debug.error
			try {
			//#endif
				//#if polish.css.repaint-previous-screen
					if (this.repaintPreviousScreen && this.previousScreen != null) {
						this.previousScreen.paint(g);
						//#if !polish.Bugs.noTranslucencyWithDrawRgb
							if (this.previousScreenOverlayBackground != null) {
								int height;
								//#if tmp.menuFullScreen
									height = this.fullScreenHeight;
								//#else
									height = this.screenHeight;
								//#endif
								this.previousScreenOverlayBackground.paint(0, 0, this.screenWidth, height, g);
							}
						//#endif
						//#if polish.ScreenOrientationCanChange && tmp.usingTitle
							if ( this.previousScreenTitleBackground != null ) {
								this.previousScreenTitleBackground.paint(0, 0, this.screenWidth, this.previousScreenTitleHeight, g );
							}
						//#endif
					}
				//#endif
				int sWidth = this.screenWidth - this.marginLeft - this.marginRight;
				int leftBorder = this.marginLeft;
				int rightBorder = leftBorder + sWidth;
				int sHeight;
				//#ifdef tmp.menuFullScreen
					sHeight = this.fullScreenHeight - this.marginTop - this.marginBottom;
				//#else
					sHeight = this.screenHeight - this.marginTop - this.marginBottom;
				//#endif
				int topBorder = this.marginTop;
				//#if tmp.useExternalMenuBar
					int space;
					//#if polish.MenuBar.Position:defined
						space = this.menuBar.getSpaceLeft( this.screenWidth, this.fullScreenHeight );
						leftBorder += space;
						sWidth -= space;
						rightBorder -= space;
						space = this.menuBar.getSpaceRight( this.screenWidth, this.fullScreenHeight );
						sWidth -= space;
						rightBorder -= space;
						space = this.menuBar.getSpaceTop( this.screenWidth, this.fullScreenHeight );
						topBorder += space;
						sHeight -= space;
					//#endif
					space = this.menuBar.getSpaceBottom( this.screenWidth, this.fullScreenHeight );
					sHeight -= space;
				//#endif		
				if (this.isLayoutHorizontalShrink) {
					int contWidth = this.contentWidth;
					if (this.container != null) {
						contWidth = this.container.getItemWidth(sWidth, sWidth); 
						//#ifdef tmp.usingTitle
							if (this.title != null && this.title.itemWidth > contWidth ) {
								this.titleHeight = this.title.getItemHeight( contWidth, contWidth );
							}
						//#endif
					}
					sWidth = contWidth;
					//System.out.println("is horizontal shrink - from sWidth=" + (this.screenWidth - this.marginLeft - this.marginRight) + ", to=" + sWidth );					
					if (this.isLayoutRight) {
						leftBorder = rightBorder - sWidth;
					} else if (this.isLayoutCenter) {
						int diff = (rightBorder - leftBorder - sWidth) >> 1;
						leftBorder += diff;
						rightBorder -= diff;
//						leftBorder = (this.screenWidth - sWidth) >> 1;
//						rightBorder = this.screenWidth - leftBorder;
					} else { // layout left:
						rightBorder = leftBorder + sWidth;
//						rightBorder = this.screenWidth - sWidth;
					}
					//System.out.println("leftBorder=" + leftBorder + ", rightBorder=" + rightBorder );
				}
				
				if (this.isLayoutVerticalShrink) {
					int contHeight = this.contentHeight;
					if (this.container != null) {
						int height = this.container.getItemHeight(sWidth, sWidth);
						if (height < contHeight) {
							contHeight = height;  
						}
					}
//					//#if tmp.menuFullScreen
//						sHeight = contHeight + this.titleHeight + this.menuBarHeight;
//					//#else
						sHeight = contHeight + this.titleHeight;
//					//#endif
					//System.out.println("isLayoutVerticalShrink - sHeight: from=" + (this.fullScreenHeight - this.marginTop - this.marginBottom) + ", to=" + sHeight + ", contentHeight=" + this.contentHeight + ", topBorder=" + topBorder );
					if (this.isLayoutBottom) {
//						//#ifdef tmp.menuFullScreen
//							topBorder = this.fullScreenHeight - (this.marginBottom + sHeight + 1);
//						//#else
							topBorder = this.screenHeight - (this.marginBottom + sHeight + 1);
//						//#endif
						//System.out.println("bottom -> topBorder=" + topBorder + ", contY=>" + (topBorder + this.titleHeight) );
					} else if (this.isLayoutVCenter) {
//						//#ifdef tmp.menuFullScreen
//							topBorder = (this.fullScreenHeight - (this.marginBottom + this.marginBottom))/2 - sHeight/2;
//						//#else
							topBorder = (this.screenHeight - (this.marginBottom + this.marginTop + sHeight)) >> 1;
//						//#endif						 
						//System.out.println("vcenter -> topBorder=" + topBorder + ", contY=>" + (topBorder + this.titleHeight) );
					}
				}

				int clipY = g.getClipY();
				// paint background:
				if (this.background != null) {
					int backgroundHeight = sHeight;
					int backgroundY = topBorder;
					//#ifdef tmp.menuFullScreen
						if (!this.excludeMenuBarForBackground) {
							//#if tmp.useExternalMenuBar
								backgroundHeight += this.menuBar.getSpaceBottom( this.screenWidth, this.fullScreenHeight );
							//#else
								backgroundHeight += this.menuBarHeight;
							//#endif
						}
					//#endif
					//#ifdef tmp.usingTitle
						if (this.excludeTitleForBackground) {
							backgroundHeight -= this.titleHeight;
							backgroundY += this.titleHeight;
						}
					//#endif
					//System.out.println("Screen (" + this + ": painting background at leftBorder=" + leftBorder + ", backgroundY=" + backgroundY + ", backgroundHeight=" + backgroundHeight + ", screenHeight=" + this.screenHeight + ", fullscreenHeight=" + this.fullScreenHeight + ", titleHeight=" + this.titleHeight + ", excludeTitleForBackground=" + excludeTitleForBackground);
					this.background.paint(leftBorder, backgroundY, sWidth, backgroundHeight, g);
				} else {
					//System.out.println("Screen (" + this + ": clearing area...");
					int backgroundHeight = sHeight;
					int backgroundY = topBorder;
					//#ifdef tmp.menuFullScreen
						if (this.excludeMenuBarForBackground) {
							backgroundHeight = this.screenHeight - this.marginTop - this.marginBottom + 1;
						}
					//#endif
					//#ifdef tmp.usingTitle
						if (this.excludeTitleForBackground) {
							backgroundHeight -= this.titleHeight;
							backgroundY += this.titleHeight;
						}
					//#endif
					g.setColor( 0xFFFFFF );
					g.fillRect( leftBorder, backgroundY, sWidth, backgroundHeight );
				}
				
				int topHeight = topBorder;
				//#ifdef tmp.usingTitle
					//#if polish.css.title-position
						if (this.paintTitleAtTop) {
					//#endif
							// paint title:
							if (this.title != null && this.showTitleOrMenu) {
								if (clipY < topBorder + this.titleHeight) {
									this.title.paint( leftBorder, topBorder, leftBorder, rightBorder, g);
//									System.out.println("clipY=" + clipY + ", topBorder=" + topBorder + ", titleHeight=" + this.titleHeight);
//								} else {
//									g.setColor( 0x00ff00 );
//									g.fillRect( 10, clipY, 10, 10 );
								}
								topHeight += this.titleHeight;
							}
					//#if polish.css.title-position
						}
					//#endif
				//#endif
				if (this.subTitle != null) {
					this.subTitle.paint( leftBorder, topHeight, leftBorder, rightBorder, g );
					this.subTitle.relativeY = topHeight;
					topHeight += this.subTitleHeight;
				}
				//#ifndef polish.skipTicker			
					//#if tmp.paintTickerAtTop
						if (this.ticker != null) {
							this.ticker.relativeY = topHeight;
							this.ticker.relativeX = leftBorder;
							this.ticker.paint( leftBorder, topHeight, leftBorder, rightBorder, g);
							topHeight += this.ticker.itemHeight;
						}
					//#elif polish.css.ticker-position && !polish.TickerPosition:defined
						if (this.paintTickerAtTop && this.ticker != null) {
							this.ticker.relativeY = topHeight;
							this.ticker.relativeX = leftBorder;
							this.ticker.paint( leftBorder, topHeight, leftBorder, rightBorder, g);
							topHeight += this.ticker.itemHeight;
						}
					//#endif
				//#endif
	
				int infoItemY = topHeight;
				//#if polish.clip-screen-info
					if (this.showInfoItem && this.clipScreenInfo) {			
						topHeight += this.infoHeight;
					}
				//#endif
				//System.out.println("topHeight=" + topHeight + ", contentY=" + contentY);
				
				// paint content:
				paintScreen( g );
	//				g.setColor( 0xff0000 );
	//				g.drawRect(g.getClipX() + 1 , g.getClipY() + 1 , g.getClipWidth() - 2 ,  g.getClipHeight() - 2);
					
				//#if tmp.useScrollBar
					//#if polish.css.show-scrollbar
						if ( this.scrollBarVisible ) {
					//#endif
							if (this.container != null && this.container.itemHeight > this.contentHeight) {
								// paint scroll bar: - this.container.yOffset
								//#debug
								System.out.println("Screen/ScrollBar: container.contentY=" + this.container.contentY + ", container.internalY=" +  this.container.internalY + ", container.yOffset=" + this.container.yOffset + ", container.height=" + this.container.availableHeight + ", container.relativeY=" + this.container.relativeY);
								
								int scrollX = sWidth + this.marginLeft 
											- this.scrollBar.initScrollBar(sWidth, this.contentHeight, this.container.itemHeight, this.container.yOffset, this.container.internalY, this.container.internalHeight, this.container.focusedIndex, this.container.size() );
								//TODO allow scroll bar on the left side
								this.scrollBar.relativeX = scrollX;
								this.scrollBar.relativeY = this.contentY;
								this.scrollBar.paint( scrollX , this.contentY, scrollX, rightBorder, g);
								//g.setColor( 0x00ff00 );
								//g.drawRect( this.contentX, this.contentY, this.contentWidth - 3, this.contentHeight );
								//System.out.println("scrollbar: width=" + scrollBar.itemWidth + ", backgroundWidth=" + scrollBar.backgroundWidth + ", height=" + scrollBar.itemHeight + ", backgroundHeight=" + scrollBar.backgroundHeight + ", contentY=" + contentY + ", contentHeight=" + this.contentHeight );
							}
					//#if polish.css.show-scrollbar
						}
					//#endif
				//#endif
					

//				//#ifdef tmp.menuFullScreen
//				 	g.setClip(0, 0, this.screenWidth, this.fullScreenHeight );
//				//#else
//				 	g.setClip(0, 0, this.screenWidth, this.originalScreenHeight );
//				//#endif
				 
//				 // remove test code
//				 g.setColor( 0x00ff00 );
//				 g.drawRect( leftBorder, topHeight, sWidth, this.screenHeight - topHeight  );
//				 g.drawRect( leftBorder + 1, topHeight + 1, sWidth - 2, this.screenHeight - topHeight -2 );
//				 g.drawLine( leftBorder, topHeight, leftBorder + sWidth, this.screenHeight );
//				 g.drawString( this.screenWidth + "x" + this.screenHeight, 60, 30, Graphics.TOP | Graphics.LEFT );
//				 g.setColor( 0xff0000 );
//				 g.drawRect( this.contentX, this.contentY, this.contentWidth, this.contentHeight );
//				 g.drawRect( this.contentX + 1, this.contentY + 1, this.contentWidth - 2, this.contentHeight -2 );
//				 g.drawLine( this.contentX, this.contentY, this.contentX + this.contentWidth, this.contentY + this.contentHeight );
//				 g.drawString( this.contentWidth+ "x" + this.contentHeight , 60, 60, Graphics.TOP | Graphics.LEFT );
				 
				// paint info element:
				if (this.showInfoItem) {			
					this.infoItem.paint( this.marginLeft, infoItemY, this.marginLeft, rightBorder, g );
				}
	 	
				int bottomY = this.contentY + this.contentHeight;
				//#ifndef polish.skipTicker			
					//#if tmp.paintTickerAtBottom
						if (this.ticker != null) {
							this.ticker.relativeX = this.marginLeft;
							this.ticker.relativeY = bottomY;
							this.ticker.paint( this.marginLeft, bottomY, this.marginLeft, rightBorder, g);
							bottomY += this.ticker.itemHeight;
						}
					//#elif polish.css.ticker-position && !polish.TickerPosition:defined
						if (!this.paintTickerAtTop && this.ticker != null) {
							this.ticker.relativeX = this.marginLeft;
							this.ticker.relativeY = bottomY;
							this.ticker.paint( this.marginLeft, bottomY, this.marginLeft, rightBorder, g);
							bottomY += this.ticker.itemHeight;
						}
					//#endif
				//#endif
				//#if tmp.usingTitle && polish.css.title-position
						if (!this.paintTitleAtTop) {
							// paint title:
							if (this.title != null && this.showTitleOrMenu) {
								this.title.paint( leftBorder, bottomY, leftBorder, rightBorder, g);
								//bottomY += this.titleHeight;
							}
						}
				//#endif
				
				// paint border:
				if (this.border != null) {
					this.border.paint(leftBorder, topBorder-1, sWidth, sHeight, g );
				}
				
				//#if polish.ScreenInfo.enable
					ScreenInfo.paint( g, topHeight, this.screenWidth );
				//#endif
					
				// paint menu in full-screen mode:
				int menuLeftX = 0;
				int menuRightX = this.screenWidth;
				int menuY = this.screenHeight; // + this.marginBottom;
				//#if polish.css.separate-menubar
					if (!this.separateMenubar) {
						menuLeftX = leftBorder;
						menuRightX = rightBorder;
						menuY = this.screenHeight - this.marginBottom;
					}
				//#endif
				//#ifdef tmp.menuFullScreen
					//#ifdef tmp.useExternalMenuBar
						//#if polish.MenuBar.Position == right
							menuLeftX = menuRightX - this.menuBar.getItemWidth( this.screenWidth, this.screenWidth ) - 1;
							menuY = 0;
						//#endif
						this.menuBar.paint(menuLeftX, menuY, menuLeftX, menuRightX, g);
						//#if tmp.useScrollIndicator
							if (this.menuBar.isOpened) {
								this.paintScrollIndicator = this.menuBar.paintScrollIndicator;
								this.paintScrollIndicatorUp = this.menuBar.canScrollUpwards;
								this.paintScrollIndicatorDown = this.menuBar.canScrollDownwards;
							}
						//#endif
					//#else
						if (this.menuOpened) {
							topHeight -= this.infoHeight;
							int menuHeight = this.menuContainer.getItemHeight(this.screenWidth, this.screenWidth);
							int y = this.originalScreenHeight - (menuHeight + 1);
							if (y < topHeight) {
								//#if tmp.useScrollIndicator
								this.paintScrollIndicator = true;
								this.paintScrollIndicatorUp = (this.menuContainer.yOffset != 0);
								this.paintScrollIndicatorDown = ( (this.menuContainer.focusedIndex != this.menuContainer.size() - 1)
										&& (this.menuContainer.yOffset + menuHeight > this.originalScreenHeight - topHeight)) ;
								//#endif
								y = topHeight; 
							//#if tmp.useScrollIndicator
							} else {
								this.paintScrollIndicator = false;
							//#endif
							}
							this.menuContainer.setScrollHeight(this.originalScreenHeight-topHeight);
							//g.setClip(0, topHeight, this.screenWidth, this.originalScreenHeight - topHeight );
							this.menuContainer.paint(menuLeftX, y, menuLeftX, menuLeftX + this.screenWidth, g);
						 	//g.setClip(0, 0, this.screenWidth, this.fullScreenHeight );
						} 
						//#if !polish.doja
							if (this.showTitleOrMenu || this.menuOpened) {
								// clear menu-bar:
								if (this.menuBarColor != Item.TRANSPARENT) {
									g.setColor( this.menuBarColor );
									//TODO check use menuY instead of this.originalScreenHeight?
									g.fillRect(menuLeftX, menuY, menuRightX,  this.menuBarHeight );
								}
								g.setColor( this.menuFontColor );
								g.setFont( this.menuFont );
								String menuText = this.menuLeftString;
								if (menuText != null) {
									//#ifdef polish.MenuBar.MarginLeft:defined
										//#= menuLeftX += ${polish.MenuBar.MarginLeft};
									//#elifdef polish.MenuBar.PaddingLeft:defined
										//#= menuLeftX += ${polish.MenuBar.PaddingLeft};
									//#else
										menuLeftX += 2;
									//#endif
									//#ifdef polish.MenuBar.MarginTop:defined
										//#= g.drawString(menuText, menuX, this.originalScreenHeight + ${polish.MenuBar.MarginTop}, Graphics.TOP | Graphics.LEFT );
									//#else
										g.drawString(menuText, menuLeftX, this.originalScreenHeight + 2, Graphics.TOP | Graphics.LEFT );
									//#endif
									
								}
								menuText = this.menuRightString;
								if (menuText != null) {
									//#ifdef polish.MenuBar.MarginRight:defined
										//#= menuRightX -= ${polish.MenuBar.MarginRight};
									//#elifdef polish.MenuBar.PaddingRight:defined
										//#= menuRightX -= ${polish.MenuBar.PaddingRight};
									//#elifdef polish.MenuBar.MarginLeft:defined
										menuRightX -= 2;
									//#endif

									//#ifdef polish.MenuBar.MarginTop:defined
										//#= g.drawString(menuText, menuRightX, this.originalScreenHeight + ${polish.MenuBar.MarginTop}, Graphics.TOP | Graphics.RIGHT );
									//#else
										g.drawString(menuText, menuRightX, this.originalScreenHeight + 2, Graphics.TOP | Graphics.RIGHT );
									//#endif
									//#ifdef polish.hasPointerEvents
										this.menuRightCommandX = menuRightX - this.menuFont.stringWidth( menuText );
									//#endif
								}
							} // if this.showTitleOrMenu || this.menuOpened
						//#endif
					//#endif
				//#endif
						
				//#if tmp.useScrollIndicator
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
									g.drawImage(this.scrollIndicatorUpImage, x, y, Graphics.LEFT | Graphics.TOP );
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
									//#if polish.css.scrollindicator-down-image
										if (this.scrollIndicatorUpImage != null) {
											y += this.scrollIndicatorUpImage.getHeight() + 1;
										} else {
											y += halfWidth;
										}
									//#else
										y += halfWidth;
									//#endif
									g.drawImage(this.scrollIndicatorDownImage, x, y, Graphics.LEFT | Graphics.TOP );
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
				//#endif
				//#ifdef polish.css.foreground-image
					if (this.foregroundImage != null) {
						g.drawImage( this.foregroundImage, this.foregroundX, this.foregroundY, Graphics.TOP | Graphics.LEFT  );
					}
				//#endif
					
//					g.setColor( 0xff);
//					g.drawRect( g.getClipX(), g.getClipY(), g.getClipWidth() - 2, g.getClipHeight() - 2 );
			//#if polish.debug.error
			} catch (RuntimeException e) {
				//#debug error
				System.out.println( "unable to paint screen (" + getClass().getName() + "):" + e );
			}
			//#endif
		}
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
		
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipWidth = g.getClipWidth();
		int clipHeight = g.getClipHeight();

		int y = this.contentY;
		int x = this.contentX;
		int height = this.contentHeight;
		int width = this.contentWidth;
//		g.setColor( 0x00ff00 );
//		g.drawRect( x, y, width, height);
		
		g.clipRect(x, y, width, height );
		
		
		if ( g.getClipHeight() > 0 ) {
			int containerHeight = this.container.getItemHeight( width, width);
			//#if tmp.useScrollIndicator
				this.paintScrollIndicator = false; // defaults to false
			//#endif
			if (containerHeight > this.container.availableHeight ) {
				//#if tmp.useScrollIndicator
					this.paintScrollIndicator = true;
					this.paintScrollIndicatorUp = (this.container.yOffset != 0);
						//&& (this.container.focusedIndex != 0);
					this.paintScrollIndicatorDown = (  
							( (this.container.focusedIndex != this.container.size() - 1) 
									|| (this.container.focusedItem != null && this.container.focusedItem.itemHeight > this.container.availableHeight) )
							 && (this.container.getScrollYOffset() + containerHeight > this.container.availableHeight) );
				//#endif
			} else if (this.isLayoutVCenter) {
				/*
				//#debug
				System.out.println("Screen: adjusting y from [" + y + "] to [" + ( y + (height - containerHeight) / 2) + "] - containerHeight=" + containerHeight);
				*/
				y += ((height - containerHeight) >> 1);
				this.container.availableHeight = height - ((height - containerHeight) >> 1);
			} else if (this.isLayoutBottom) {
				y += (height - containerHeight);
				this.container.availableHeight = containerHeight;
	//			System.out.println("content: y=" + y + ", contentY=" + this.contentY + ", contentHeight="+ this.contentHeight + ", containerHeight=" + containerHeight);
			}
			int containerWidth = this.container.itemWidth;
			if (this.isLayoutCenter) {
				int diff = (width - containerWidth) >> 1;
				x += diff;
				width -= (width - containerWidth);
			} else if (this.isLayoutRight) {
				int diff = width - containerWidth;
				x += diff;
				width -= diff;
			}
			this.container.relativeX = x;
			this.container.relativeY = y;
			//System.out.println("screen: content: x=" + x + ", rightBorder=" + (x + width) );
			//System.out.println("content: y=" + y + ", bottom=" + (y + height) );
			this.container.paint( x, y, x, x + width, g );
//			g.setColor(0x00ff00);
//			g.drawString( Integer.toString(g.getClipHeight()), x, y, Graphics.TOP | Graphics.LEFT );
//		} else {
//			g.setClip( clipX, clipY, clipWidth, clipHeight );
//			g.setColor(0x00ff00);
//			g.fillRect( clipX, clipY, 10, 10 );
		}
		
		// allow painting outside of the content area again:
		g.setClip( clipX, clipY, clipWidth, clipHeight );

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
		//#if polish.Bugs.getTitleRequiresNull
			//# return null;
		//#else
			if (this.title == null) {
				return null;
			} else if ( !(this.title instanceof StringItem ) ) {
				return null;
			} else {
				return ((StringItem)this.title).getText();
			}
		//#endif
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
	 * @param text the new title, or null for no title
	 * @param tStyle the new style for the title, is ignored when null
	 */
	public void setTitle( String text, Style tStyle)
	{
		//#debug
		System.out.println("Setting title " + text );
		//#ifdef tmp.ignoreMotorolaTitleCall
			if (text == null) {
				if (this.ignoreMotorolaTitleCall) {
					this.ignoreMotorolaTitleCall = false;
					return;
				}
				//return;
			}
		//#endif
		if (text != null) {
			//#style title, default
			this.title = new StringItem( null, text );
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
//			//#ifdef polish.ScreenWidth:defined
//				//#= int width = ${polish.ScreenWidth}  - (this.marginLeft + this.marginRight);
//			//#else
//				int width = this.screenWidth - (this.marginLeft + this.marginRight);
//			//#endif
//			this.titleHeight = this.title.getItemHeight( width, width );			
		} else {
			this.title = null;
			this.titleHeight = 0;
		}
		if (this.isInitialized && super.isShown()) {
			if (this.title != null) {
				int width = this.screenWidth - this.marginLeft - this.marginRight;
				this.titleHeight = this.title.getItemHeight( width, width );
			}
			calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
			//this.isInitialized = false;
			repaint();
		}
	}
	//#endif
	
	/**
	 * Sets an Item as the title for this screen.
	 * WARNING: You must not call setTitle(String) after calling this method anymore!
	 * 
	 * @param item the title Item 
	 */
	public void setTitle(Item item) {
		//#ifdef tmp.usingTitle
		this.title = item;
		if (item != null){
			//#ifdef polish.ScreenWidth:defined
				//#= int width = ${polish.ScreenWidth}  - (this.marginLeft + this.marginRight);
			//#else
				int width = this.screenWidth - (this.marginLeft + this.marginRight);
			//#endif
			if (width > 1) {
				this.titleHeight = this.title.getItemHeight( width, width );
			}
			item.screen = this;
		} else {
			this.titleHeight = 0;
		}
		if (this.isInitialized && super.isShown()) {
			calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
			repaint();
		}
		//#endif
	}

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
		
		if (this.isInitialized && this.container != null) {
			int previousContentHeight = this.contentHeight;
			calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
			int differentce = this.contentHeight - previousContentHeight;
			//System.out.println("ADJUSTING CONTAINER.YOFFSET BY " + differentce + " PIXELS FOR CONTAINER " + this.container );
			this.container.yOffset += differentce;
		} else {
			//System.out.println("!!!NOT ADJUSTING CONTAINER.YOFFSET for container " + this.container );
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
		setPolishTicker( ticker, null );
	}
	//#endif
	
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
	 * @param ticker the ticker object used on this screen
	 * @param tickerStyle the style of the ticker
	 */
	public void setPolishTicker( Ticker ticker, Style tickerStyle )
	{
		//#debug
		System.out.println("setting ticker " + ticker);
		if (this.ticker != null) {
			this.ticker.hideNotify();
		}
		this.ticker = ticker;
		if (ticker != null) {
			if (tickerStyle != null) {
				ticker.setStyle(tickerStyle);
			}
			ticker.screen = this;
		}
		//System.out.println("setTicker(): screenHeight=" + this.screenHeight + ", original=" + this.originalScreenHeight );
		if (super.isShown()) {
			calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
			if (ticker != null) {
				ticker.showNotify();
			}
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
	public void keyPressed(int keyCode) {
		this.lastInteractionTime = System.currentTimeMillis();
		synchronized (this.paintLock) {
			try {
				//#debug
				System.out.println("keyPressed: [" + keyCode + "].");
				int gameAction = -1;
				//#if polish.blackberry
					this.keyPressedProcessed = true;
				//#endif
	

				try {
					gameAction = getGameAction(keyCode);
				} catch (Exception e) { // can happen when code is a  LEFT/RIGHT softkey on SE devices, for example
					//#debug warn
					System.out.println("Unable to get game action for key code " + keyCode + ": " + e );
				}
				boolean processed = false;
				//#if tmp.menuFullScreen
					boolean letTheMenuBarProcessKey;
					//#ifdef tmp.useExternalMenuBar
						letTheMenuBarProcessKey = this.menuBar.isOpened;
					//#else
						letTheMenuBarProcessKey = this.menuOpened;
					//#endif
					//#if polish.Bugs.SoftKeyMappedToFire
						//#if polish.key.LeftSoftKey:defined && polish.key.RightSoftKey:defined
							//#= if (gameAction == FIRE && (keyCode == ${polish.key.LeftSoftKey} || keyCode == ${polish.key.RightSoftKey} )) {
						//#else
						if (gameAction == FIRE && (keyCode == -6 || keyCode == -7)) {
						//#endif
							letTheMenuBarProcessKey = true;
							gameAction = 0;
						}
					//#endif
					if (!letTheMenuBarProcessKey) {
				//#endif
						processed = handleKeyPressed(keyCode, gameAction);
				//#if tmp.menuFullScreen
					}
				//#endif
				
				//#ifdef polish.debug.debug
					if (!processed) {
						//#debug
						System.out.println("unable to handle key [" + keyCode + "].");
					}
				//#endif
				//#if tmp.menuFullScreen
					if (!processed) {
						//#ifdef tmp.useExternalMenuBar
							if (this.menuBar.handleKeyPressed(keyCode, gameAction)) {
								repaint();
								return;
							}
							if (this.menuBar.isSoftKeyPressed) {
								//System.out.println("menubar detected softkey " + keyCode );
								//#if polish.blackberry
									this.keyPressedProcessed = false;
								//#endif
								return;
							}
							//System.out.println("menubar did not handle " + keyCode );
						//#else
							// internal menubar is used:
							if (keyCode == LEFT_SOFT_KEY) {
								if ( this.menuSingleLeftCommand != null) {
									callCommandListener( this.menuSingleLeftCommand );
									return;
								} else {
									if (!this.menuOpened 
											&& this.menuContainer != null 
											&&  this.menuContainer.size() != 0 ) 
									{
										openMenu( true );
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
							if (keyCode == LEFT_SOFT_KEY || keyCode == RIGHT_SOFT_KEY ) {
								//#if polish.blackberry
									this.keyPressedProcessed = false;
								//#endif
								doReturn = true;
							}
							if (this.menuOpened) {
								if (keyCode == RIGHT_SOFT_KEY ) {
									int selectedIndex = this.menuContainer.getFocusedIndex();
									if (!this.menuContainer.handleKeyPressed(0, LEFT)
											|| selectedIndex != this.menuContainer.getFocusedIndex() ) 
									{
										openMenu( false );
									}
		//						} else if ( gameAction == Canvas.FIRE ) {
		//							int focusedIndex = this.menuContainer.getFocusedIndex();
		//							Command cmd = (Command) this.menuCommands.get( focusedIndex );
		//							this.menuOpened = false;
		//							callCommandListener( cmd );
								} else { 
									processed = this.menuContainer.handleKeyPressed(keyCode, gameAction);
									//#if polish.key.ReturnKey:defined && polish.key.ReturnKey != polish.key.ClearKey
									if ((!processed)
										//#= && ( (keyCode == ${polish.key.ReturnKey}) && this.menuOpened ) 
									) {
										openMenu( false );
									}
									//#endif
								}
								repaint();
								return;
							}
							if (doReturn) {
								return;
							}
						//#endif
					}
				//#endif
				//#if (polish.Screen.FireTriggersOkCommand == true) && tmp.menuFullScreen
					if (gameAction == FIRE && keyCode != Canvas.KEY_NUM5 && this.okCommand != null) {
						callCommandListener(this.okCommand);
						return;
					}
				//#endif
				//#if tmp.menuFullScreen && polish.key.ReturnKey:defined
					if (!processed) {
						//#= if ( (keyCode == ${polish.key.ReturnKey}) && (this.backCommand != null) ) {
							//#debug
							System.out.println("keyPressed: invoking commandListener for " + this.backCommand.getLabel() );
							callCommandListener( this.backCommand );
							processed = true;
						//# }
					}
				//#endif
				//#if polish.blackberry
					this.keyPressedProcessed = processed;
				//#endif
				//#if tmp.menuFullScreen
					if (!processed && gameAction == FIRE && keyCode != Canvas.KEY_NUM5 && this.okCommand != null) {
						callCommandListener(this.okCommand);
					}
				//#endif
				if (processed) {
					notifyScreenStateChanged();
					repaint();
				}
			} catch (Exception e) {
				//#if !polish.debug.error 
					e.printStackTrace();
				//#endif
				//#debug error
				System.out.println("keyPressed() threw an exception" + e );
			} finally {
				this.isScreenChangeDirtyFlag = false;
			}
		}
	}
	
	
	/**
	 * Just maps the event to the the keyPressed method.
	 * 
	 * @param keyCode the code of the key, which is pressed repeatedly
	 */
	public void keyRepeated(int keyCode) {
		//synchronized (this.paintLock) {
			//#debug
			System.out.println("keyRepeated(" + keyCode + ")");
			this.lastInteractionTime = System.currentTimeMillis();
			int gameAction = 0;
			try {
				gameAction = getGameAction( keyCode );
			} catch (Exception e) { // can happen when code is a  LEFT/RIGHT softkey on SE devices, for example
				//#debug warn
				System.out.println("Unable to get game action for key code " + keyCode + ": " + e );
			}
			//#if tmp.menuFullScreen
				//#ifdef tmp.useExternalMenuBar
					if (this.menuBar.handleKeyRepeated(keyCode, gameAction)) {
						repaint();
						return;
					} else if (this.menuBar.isOpened) {
						return;
					}
				//#else
					if (this.menuOpened  && this.menuContainer != null ) {
						if (this.menuContainer.handleKeyRepeated(keyCode, gameAction)) {
							repaint();
						}
						return;
					}
	
				//#endif
			//#endif
			boolean handled = handleKeyRepeated( keyCode, gameAction );
			if ( handled ) {
				repaint();
			}
		//}
	}

	/**
	 * Is called when a key is released.
	 * 
	 * @param keyCode the code of the key, which has been released
	 */
	public void keyReleased(int keyCode) {
		//#debug
		System.out.println("keyReleased(" + keyCode + ")");
		this.lastInteractionTime = System.currentTimeMillis();
		int gameAction = 0;
		try {
			gameAction = getGameAction( keyCode );
		} catch (Exception e) { // can happen when code is a  LEFT/RIGHT softkey on SE devices, for example
			//#debug warn
			System.out.println("Unable to get game action for key code " + keyCode + ": " + e );
		}
		//#if tmp.menuFullScreen
			//#ifdef tmp.useExternalMenuBar
				if (this.menuBar.handleKeyReleased(keyCode, gameAction)) {
					repaint();
					return;
				} else if (this.menuBar.isOpened) {
					return;
				}
			//#else
				if (this.menuOpened  && this.menuContainer != null ) {
					if (this.menuContainer.handleKeyReleased(keyCode, gameAction)) {
						repaint();
					}
					return;
				}

			//#endif
		//#endif
		boolean handled = handleKeyReleased( keyCode, gameAction );
		if ( handled ) {
			repaint();
		}
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
		if (this.container == null) {
			return false;
		}
		return this.container.handleKeyPressed(keyCode, gameAction);
	}
	
	/**
	 * Handles the key-repeated event.
	 * Please note, that implementation should first try to handle the
	 * given key-code, before the game-action is processed.
	 * 
	 * @param keyCode the code of the repeated key, e.g. Canvas.KEY_NUM2
	 * @param gameAction the corresponding game-action, e.g. Canvas.UP
	 * @return true when the key-event was processed
	 */
	protected boolean handleKeyRepeated( int keyCode, int gameAction ) {
		if (this.container == null) {
			return false;
		}
		return this.container.handleKeyRepeated(keyCode, gameAction);
	}
	
	/**
	 * Handles the key-released event.
	 * Please note, that implementation should first try to handle the
	 * given key-code, before the game-action is processed.
	 * 
	 * @param keyCode the code of the released key, e.g. Canvas.KEY_NUM2
	 * @param gameAction the corresponding game-action, e.g. Canvas.UP
	 * @return true when the key-event was processed
	 */
	protected boolean handleKeyReleased( int keyCode, int gameAction ) {
		if (this.container == null) {
			return false;
		}
		return this.container.handleKeyReleased(keyCode, gameAction);
	}

	
	/**
	 * Sets the screen listener for this screen.
	 * 
	 * @param listener the listener that is notified whenever the user changes the internal state of this screen.
	 */
	public void setScreenStateListener( ScreenStateListener listener ) {
		this.screenStateListener = listener;
	}
	
	/**
	 * Notifies the screen state change listener about a change in this screen.
	 */
	public void notifyScreenStateChanged() {
		if (this.screenStateListener != null && !this.isScreenChangeDirtyFlag) {
			this.isScreenChangeDirtyFlag = true;
			this.screenStateListener.screenStateChanged( this );
		}
	}

	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#setCommandListener(javax.microedition.lcdui.CommandListener)
	 */
	public void setCommandListener(CommandListener listener) {
		this.forwardCommandListener.realCommandListener = listener;
	}
	
	/**
	 * Retrieves the asscociated command listener of this screen (if any).
	 * 
	 * @return the command listener or null when none has been registered before.
	 */
	public CommandListener getCommandListener() {
		return this.forwardCommandListener.realCommandListener;
	}

	//#if tmp.menuFullScreen && !tmp.useExternalMenuBar
	private void updateMenuTexts() {
		//#debug
		System.out.println("updating menu-command texts");
		String left = null;
		String right = null;
		int menuLeftX = 0;
		int menuRightX = this.screenWidth;
		//#if polish.css.separate-menubar
			if (!this.separateMenubar) {
				menuLeftX = this.marginLeft;
				menuRightX -= this.marginRight;
			}
		//#endif
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
			left = menuText;
			//#ifdef polish.hasPointerEvents
				if (this.menuFont != null) {
					this.menuLeftCommandX = menuLeftX + this.menuFont.stringWidth( menuText );
				}
			//#endif
			if ( this.menuOpened ) {
				// set cancel string:
				//#ifdef polish.i18n.useDynamicTranslations
					menuText = Locale.get( "polish.command.cancel" ); 
				//#elifdef polish.command.cancel:defined
					//#= menuText = "${polish.command.cancel}";
				//#else
					menuText = "Cancel";
				//#endif
				//#ifdef polish.MenuBar.MarginRight:defined
					//#= menuRightX -= ${polish.MenuBar.MarginRight};
				//#elifdef polish.MenuBar.PaddingRight:defined
					//#= menuRightX -= ${polish.MenuBar.PaddingRight};
				//#elifdef polish.MenuBar.MarginLeft:defined
					menuRightX -= 2;
				//#endif
				right = menuText;
			}
		}
		if (this.menuSingleRightCommand != null && !this.menuOpened) {
			String menuText = this.menuSingleRightCommand.getLabel();
			//#ifdef polish.MenuBar.MarginRight:defined
				//#= menuRightX -= ${polish.MenuBar.MarginRight};
			//#elifdef polish.MenuBar.PaddingRight:defined
				//#= menuRightX -= ${polish.MenuBar.PaddingRight};
			//#elifdef polish.MenuBar.MarginLeft:defined
				menuRightX -= 2;
			//#endif
			//#ifdef polish.hasPointerEvents
				if (this.menuFont != null) {
					this.menuRightCommandX = menuRightX - this.menuFont.stringWidth( menuText );
				}
			//#endif
			right = menuText;
		}
		this.menuLeftString = left;
		this.menuRightString = right;
		//#if polish.doja
			Frame frame = (Frame) ((Object)this);
			frame.setSoftLabel( Frame.SOFT_KEY_1, left );
			frame.setSoftLabel( Frame.SOFT_KEY_2, right );
		//#endif
	}
	//#endif
	
	//#if tmp.menuFullScreen && !tmp.useExternalMenuBar
	private void openMenu( boolean open ) {
		if (!open && this.menuOpened) {
			this.menuContainer.hideNotify();
		} else if (open && !this.menuOpened) {
			//#if !polish.MenuBar.focusFirstAfterClose
				// focus the first item again, so when the user opens the menu again, it will be "fresh" again
				this.menuContainer.focus(0);
			//#endif
			this.menuContainer.showNotify();
		}
		this.menuOpened = open;
		updateMenuTexts();
	}
	//#endif

	//#ifdef tmp.menuFullScreen
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#addCommand(javax.microedition.lcdui.Command)
	 */
	public void addCommand(Command cmd) {
		//#style menuitem, menu, default
		addCommand( cmd );
	}
	//#endif
	
	//#ifdef tmp.menuFullScreen
	/**
	 * Adds a command to this screen with the specified style.
	 * 
	 * @param cmd the command
	 * @param commandStyle the style for the command
	 */
	public void addCommand(Command cmd, Style commandStyle ) {
		//#debug
		System.out.println("adding command [" + cmd.getLabel() + "] to screen [" + this + "].");
		//#if tmp.useExternalMenuBar && polish.vendor.siemens
			if (this.menuBar == null) {
				//#debug
				System.out.println("Ignoring command [" + cmd.getLabel() + "] that is added while Screen is not even initialized.");
				return;
			}
		//#endif
		int cmdType = cmd.getCommandType();
		if ( cmdType == Command.OK 
				&&  (this.okCommand == null || this.okCommand.getPriority() < cmd.getPriority() ) ) 
		{
			this.okCommand = cmd;
		}
		//#ifdef polish.key.ReturnKey:defined
			else if ( (cmdType == Command.BACK || cmdType == Command.CANCEL || cmdType == Command.EXIT ) 
				&& ( this.backCommand == null || cmd.getPriority() < this.backCommand.getPriority() )  ) 
			{
				//#debug
				System.out.println("setting new backcommand=" + cmd.getLabel() + " for screen " + this + " " + getTitle() );
				this.backCommand = cmd;
			}
		//#endif
		//#ifdef tmp.useExternalMenuBar
			this.menuBar.addCommand(cmd, commandStyle);
			//TODO adjust for other menubar positions
			if (this.menuBarHeight == 0 && this.isInitialized) {
				int availableWidth = this.screenWidth;
				//#if polish.css.separate-menubar
					if (!this.separateMenubar) {
						availableWidth -= (this.marginLeft + this.marginRight);
					}
				//#endif
				this.menuBarHeight = this.menuBar.getSpaceBottom( availableWidth, this.fullScreenHeight );
				this.screenHeight = this.fullScreenHeight - this.menuBarHeight;
			}
			if (super.isShown()) {
				repaint();
			}
		//#else
			if (this.menuCommands == null) {
				this.menuCommands = new ArrayList( 6, 50 );
				//#style menu, default
				 this.menuContainer = new Container( true );
				 this.menuContainer.screen = this;
				 if (this.menuContainer.style != null) {
					 //System.out.println("setting style for menuContainer " + this.menuContainer);
					 this.menuContainer.setStyle( this.menuContainer.style );
				 }
				 this.menuContainer.layout |= Item.LAYOUT_SHRINK;
			}
			if (cmd == this.menuSingleLeftCommand 
					|| cmd == this.menuSingleRightCommand 
					|| this.menuCommands.contains(cmd)) 
			{
				// do not add an existing command again...
				//#debug
				System.out.println("Ignoring existing command " + cmd.getLabel() );
				return;
			}
			if ( (cmdType == Command.BACK || cmdType == Command.CANCEL || cmdType == Command.EXIT) ) 
			{
				if ( (this.menuSingleRightCommand == null)
						|| (cmd.getPriority() < this.menuSingleRightCommand.getPriority())	)
				{
					// okay set the right menu command:
					if (this.menuSingleRightCommand != null) {
						// the right menu command is replaced by the new one,
						// so insert the original one into the options-menu:
						CommandItem menuItem = new CommandItem( this.menuSingleRightCommand, this.menuContainer, commandStyle  );
						menuItem.screen = this;
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
					updateMenuTexts();
					repaint();
					return;
				}
			}
			CommandItem menuItem = new CommandItem( cmd, this.menuContainer, commandStyle  );
			menuItem.screen = this;
			if ( this.menuCommands.size() == 0 ) {
				// using this command as the single-left-command:
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
						// ignore existing command:
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
			updateMenuTexts();
			repaint();
		//#endif
	}
	//#endif
	
	/**
	 * Retrieves the CommandItem used for rendering the specified command. 
	 * 
	 * @param command the command
	 * @return the corresponding CommandItem or null when this command is not present in this MenuBar.
	 */
	public CommandItem getCommandItem(Command command) {
		//#if tmp.menuFullScreen
			//#ifdef tmp.useExternalMenuBar
				//# return this.menuBar.getCommandItem( command );
			//#else
				int index = this.menuCommands.indexOf(command);
				if (index != -1) {
					return (CommandItem) this.menuContainer.get(index);
				} else {
					for (int i = 0; i < this.menuContainer.size(); i++) {
						CommandItem item = (CommandItem) this.menuContainer.get(i);
						item = item.getChild(command);
						if (item != null) {
							return item;
						}
					}
				}
			//#endif
		//#endif
		//#ifndef tmp.useExternalMenuBar
			return null;
		//#endif
	}

	/**
	 * Removes all commands from this screen.
	 * This option is only available when the "menu" fullscreen mode is activated.
	 */
	public void removeAllCommands() {
		//#ifdef tmp.menuFullScreen
			//#ifdef tmp.useExternalMenuBar
				this.menuBar.removeAllCommands();
			//#else
				this.menuCommands.clear();
				this.menuContainer.clear();
				updateMenuTexts();
			//#endif
			if (super.isShown()) {
				repaint();
			}
		//#endif
	}

	
	/**
	 * Adds the given command as a subcommand to the specified parent command.
	 * 
	 * @param child the child command
	 * @param parent the parent command
	 */
	public void addSubCommand(Command child, Command parent) {
		//#style menuitem, menu, default
		addSubCommand( child, parent );
	}
	
	/**
	 * Adds the given command as a subcommand to the specified parent command.
	 * 
	 * @param child the child command
	 * @param parent the parent command
	 * @param commandStyle the style for the command
	 * @throws IllegalStateException when the parent command has not been added before
	 */
	public void addSubCommand(Command child, Command parent, Style commandStyle) {
		//#if !tmp.menuFullScreen
			addCommand( child );
		//#else
			//#debug
			System.out.println("Adding subcommand " + child.getLabel() );
			//#ifdef tmp.useExternalMenuBar
				this.menuBar.addSubCommand( child, parent, commandStyle );
			//#else
				// find parent CommandItem, could be tricky, especially when there are nested commands over several layers
				if ( this.menuCommands == null ) {
					throw new IllegalStateException();
				}
				int index = this.menuCommands.indexOf( parent );
				CommandItem parentCommandItem = null;
				if (index != -1) {
					// found it:
					parentCommandItem = (CommandItem) this.menuContainer.get( index );
				} else {
					// search through all commands
					for ( int i=0; i < this.menuContainer.size(); i++ ) {
						CommandItem item = (CommandItem) this.menuContainer.get( i );
						parentCommandItem = item.getChild( parent );
						if ( parentCommandItem != null ) {
							break;
						}
					}
				}
				if ( parentCommandItem == null ) {
					throw new IllegalStateException();
				}
				parentCommandItem.addChild( child, commandStyle );
				if (parent == this.menuSingleLeftCommand) {
					this.menuSingleLeftCommand = null;
				}
				if (this.menuOpened) {
					//this.isInitialized = false;
					repaint();
				}				
			//#endif	
			if (super.isShown()) {
				repaint();
			}
		//#endif
	}

	
	//#ifdef tmp.menuFullScreen
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#removeCommand(javax.microedition.lcdui.Command)
	 */
	public void removeCommand(Command cmd) {
		//#debug
		System.out.println("removing command " + cmd.getLabel() + " from screen " + this );
		//#ifdef tmp.useExternalMenuBar
			this.menuBar.removeCommand(cmd);
			if (super.isShown()) {
				repaint();
			}
		//#else
			if (this.okCommand == cmd) {
				this.okCommand = null;
			}
			if (this.menuSingleRightCommand == cmd) {
				this.menuSingleRightCommand = null;
				//move another suitable command-item to the right-pos:
				if (this.menuCommands != null) {
					Object[] commands = this.menuCommands.getInternalArray();
					for (int i = 0; i < commands.length; i++) {
						Command command = (Command) commands[i];
						if (command == null) {
							break;
						}
						int type = command.getCommandType(); 
						if ( type == Command.BACK || type == Command.CANCEL ) {
							//System.out.println("removing right command [" + cmd.getLabel() + "], now using " + command.getLabel() + ", menuContainer=" + this.menuContainer.size() + ", menuCommands=" + this.menuCommands.size() );
							this.menuContainer.remove( i );
							this.menuCommands.remove( i );
							this.menuSingleRightCommand = command;
							break;
						}
					}
				}
				updateMenuTexts();
				repaint();
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
			updateMenuTexts();
			repaint();
		//#endif
	}
	//#endif
	
	/**
	 * Sets the commands of the given item
	 * 
	 * @param item the item which has at least one command 
	 * @see #removeItemCommands(Item)
	 */
	protected void setItemCommands( Item item ) {
		this.focusedItem = item;
		if (item.commands != null) {
			Object[] commands = item.commands.getInternalArray();
			// register item commands, so that later onwards only commands that have been actually added
			// will be removed:
			if (this.itemCommands == null) {
				this.itemCommands = new ArrayList( commands.length );
			}
			for (int i = 0; i < commands.length; i++) {
				Command command = (Command) commands[i];
				if (command == null) {
					break;
				}
				// workaround for cases where the very same command has been added to both an item as well as this screen:
				//System.out.println("scren: add ItemCommand " + command.getLabel() );
				boolean addCommand;
				//#ifdef tmp.useExternalMenuBar
					addCommand = !this.menuBar.commandsList.contains( command );
				//#elif tmp.menuFullScreen
					addCommand = ( ( this.menuCommands == null) ||  !this.menuCommands.contains( command) );
				//#else
					addCommand = true;
				//#endif
				if (addCommand) {
					addCommand(command);
					this.itemCommands.add( command );
				}
			}
		} else if (this.itemCommands != null) {
			this.itemCommands.clear();
		}
		//#ifdef tmp.useExternalMenuBar
			if (super.isShown()) {
				repaint();
			}
		//#endif
	}
	
	/**
	 * Removes the commands of the given item.
	 *  
	 * @param item the item which has at least one command 
	 * @see #setItemCommands(Item)
	 */
	protected void removeItemCommands( Item item ) {
		if (item.commands != null && this.itemCommands != null) {
			// use the Screen's itemCommands list, since in this list only commands that are only present on the item
			// are listed (not commands that are also present on the screen).
			Object[] commands = this.itemCommands.getInternalArray();
			for (int i = 0; i < commands.length; i++) {
				Command command = (Command) commands[i];
				if (command == null) {
					break;
				}
				//#ifdef tmp.useExternalMenuBar
					this.menuBar.removeCommand(command);
					if (this.menuBar.size() ==0) {
						this.menuBarHeight = 0;
					}
				//#else
					removeCommand(command);
				//#endif
			}
		}
		if (this.focusedItem == item) {
			this.focusedItem = null;
		}
		//#ifdef tmp.useExternalMenuBar
			if (super.isShown()) {
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
		//System.out.println("Screen.callCommandListener() for command " + cmd.getLabel() );
		//#ifdef tmp.useExternalMenuBar
			this.menuBar.setOpen(false);
		//#elif tmp.menuFullScreen
			openMenu(false);
		//#endif
		//#if polish.ScreenChangeAnimation.forward:defined
			this.lastTriggeredCommand = cmd;
		//#endif
		if (this.forwardCommandListener != null) {
			try {
				this.forwardCommandListener.commandAction(cmd, this );
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
		this.lastInteractionTime = System.currentTimeMillis();
		try {
			// check for scroll-indicator:
			//#if tmp.useScrollIndicator
				//TODO support pointer events for scroll bar
				if (  this.paintScrollIndicator &&
						(x > this.scrollIndicatorX) &&
						(y > this.scrollIndicatorY) &&
						(x < this.scrollIndicatorX + this.scrollIndicatorWidth) &&
						(y < this.scrollIndicatorY + (this.scrollIndicatorHeight << 1) + 1) ) 
				{
					//#debug
					System.out.println("ScrollIndicator has been clicked... ");
					// the scroll-indicator has been clicked:
					int gameAction;
					if ( (( !this.paintScrollIndicatorUp) || (y > this.scrollIndicatorY + this.scrollIndicatorHeight)) && this.paintScrollIndicatorDown) {
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
			//#endif
			//#ifdef tmp.menuFullScreen
				//#ifdef tmp.useExternalMenuBar
					if (this.menuBar.handlePointerPressed(x - this.menuBar.relativeX, y - this.menuBar.relativeY)) {
						repaint();
						return;
					}
				//#else
					// check if one of the command buttons has been pressed:
					if (y > this.screenHeight) {
//						System.out.println("pointer at x=" + x + ", menuLeftCommandX=" + menuLeftCommandX );
						if (x >= this.menuRightCommandX && this.menuRightCommandX != 0) {
							if (this.menuOpened) {
								openMenu( false );
							} else if (this.menuSingleRightCommand != null) {
								callCommandListener(this.menuSingleRightCommand );
							}
							repaint();
							return;
						} else if (x <= this.menuLeftCommandX){
							// assume that the left command has been pressed:
//							System.out.println("x <= this.menuLeftCommandX: open=" + this.menuOpened );
							if (this.menuOpened ) {
								// the "SELECT" command has been clicked:
								this.menuContainer.handleKeyPressed(0, Canvas.FIRE);
								openMenu( false );
							} else if (this.menuSingleLeftCommand != null) {								
								this.callCommandListener(this.menuSingleLeftCommand);
							} else {
								openMenu( true );
								//this.menuOpened = true;
							}
							repaint();
							return;
						}
					} else if (this.menuOpened) {
						// a menu-item could have been selected:
						int menuY = this.originalScreenHeight - (this.menuContainer.itemHeight + 1);
						if (!this.menuContainer.handlePointerPressed( x, y - menuY )) {
							openMenu( false );
						}
						repaint();
						return;
					}
				//#endif
			//#endif
			if (this.subTitle != null && this.subTitle.handlePointerPressed(x - this.subTitle.relativeX, y - this.subTitle.relativeY)) {
				return;
			}
			//#if tmp.useScrollBar
				if (this.scrollBar.handlePointerPressed( x - this.scrollBar.relativeX, y - this.scrollBar.relativeY )) {
					return;
				}
			//#endif
			// let the screen handle the pointer pressing:
			boolean processed = handlePointerPressed( x, y  );
			//#ifdef tmp.usingTitle
				//boolean processed = handlePointerPressed( x, y - (this.titleHeight + this.infoHeight + this.subTitleHeight) );
				if (processed) {
					notifyScreenStateChanged();
					repaint();
				}
			//#else
				if (processed) {
					repaint();
				}
			//#endif
			
			// #ifdef polish.debug.debug
				if (!processed) {
					//#debug
					System.out.println("PointerPressed at " + x + ", " + y + " not processed.");					
				}
			// #endif
		} catch (Exception e) {
			//#debug error
			System.out.println("PointerPressed at " + x + "," + y + " resulted in exception" + e );
		} finally {
			this.isScreenChangeDirtyFlag = false;
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
	 * @param x the absolute x position of the pointer pressing
	 * @param y the absolute y position of the pointer pressing
	 * @return true when the pressing of the pointer was actually handled by this item.
	 */
	protected boolean handlePointerPressed( int x, int y ) {
		if (this.container == null) {
			return false;
		}
		return this.container.handlePointerPressed(x - this.container.relativeX, y - this.container.relativeY );
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
			if (!this.isInitialized) {
				return;
			}
			//#debug
			System.out.println("Screen: sizeChanged to width=" + width + ", height=" + height );
			boolean doInit = false;
		
			//#ifdef tmp.menuFullScreen
				doInit = width != this.screenWidth || height != this.fullScreenHeight;
				if (doInit) {
					this.fullScreenHeight = height;
					this.screenHeight = height - this.menuBarHeight;
					this.originalScreenHeight = this.screenHeight;
					this.screenWidth = width;
				}
			//#else
				doInit = width != this.screenWidth || height != this.originalScreenHeight;
				if (doInit) {
					this.originalScreenHeight = this.screenHeight;
					this.screenWidth = width;
					this.screenHeight = height;
				}
			//#endif
			//System.out.println("sizeChanged - doInit=" + doInit);
			if (doInit) {
				if (this.container != null) {
					this.container.requestFullInit();
				}
				//#if tmp.usingTitle
					if (this.title != null) {
						this.title.isInitialized = false;
					}
				//#endif
				init();
				//calculateContentArea( 0, 0, this.screenWidth, this.screenHeight  ); done within init()
			}
		//#endif
	}
	//#endif
	
	/**
	 * @return the usable screen height
	 */
	public int getScreenHeight() {
		int result;
		//#ifdef tmp.menuFullScreen
			result = this.fullScreenHeight;
		//#else
			result = this.screenHeight;
		//#endif
		return result;
	}
	
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
		/** the original command listener set by the programmer */
		public CommandListener realCommandListener;

		/* (non-Javadoc)
		 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
		 */
		public void commandAction(Command cmd, Displayable thisScreen) {
			//check if the given command is from the currently focused item:
			Item item = Screen.this.getCurrentItem();
			//#debug
			System.out.println("FowardCommandListener: processing command " + cmd.getLabel() + " for item " + Screen.this.focusedItem + " and screen " + Screen.this );
			if ((item != null) && (item.itemCommandListener != null) && (item.commands != null)) {
				if ( item.commands.contains(cmd)) {
					//System.out.println("forwarding straight to ItemCommandListener " + item.itemCommandListener);
					item.itemCommandListener.commandAction(cmd, item);
					return;
				}
			} else if (item instanceof Container) {
				item = ((Container) item).getFocusedItem();
				while (item != null) {
					if ((item.itemCommandListener != null) && (item.commands != null) && item.commands.contains(cmd) ) {
						//System.out.println("forwarding at last  to ItemCommandListener " + item.itemCommandListener);
						item.itemCommandListener.commandAction(cmd, item);
						return;
					}
					//System.out.println("did not find command or command listener in item " + item + ", listener=" + item.itemCommandListener );
					if (item instanceof Container) {
						item = ((Container) item).getFocusedItem();						
					} else {
						item = null;
					}
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
		focus( item, false );
	}
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param item the item which is already shown on this screen.
	 * @param force true when the item should be focused even when it is inactive (like a label for example)
	 */
	public void focus(Item item, boolean force ) {
		int index;
		if (item == null) {
			index = -1;
		} else {
			index = this.container.itemsList.indexOf(item);
			if (index == -1) {
				ArrayList children = new ArrayList();
				children.add( item );
				Item parent = item.parent;
				while (parent != null) {
					if (parent.parent == this.container) {
						index = this.container.itemsList.indexOf(parent);
						if (index != -1) {
							// found the item!
							focus( index, parent, force );
							//System.out.println("found nested item " + parent + " - number of parents=" + children.size());
							for (int i=children.size() - 1; i>=0; i--) {
								Item child = (Item) children.get(i);
								if (child.appearanceMode != Item.PLAIN || force ) {
									Container parentContainer = (Container) child.parent;
									//System.out.println("focusing " + child + " in parent " + parentContainer);
									parentContainer.focus( parentContainer.indexOf(child), child, 0 );
								} else {
									//System.out.println("child is not focussable: " + child);
									return;
								}
							}
							return;
						}
					}
					children.add( parent );
					parent = parent.parent;
				}
			}
		}
		focus( index, item, force );
	}
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param index the index of the item which is already shown on this screen.
	 */
	public void focus(int index) {
		Item item = null;
		if (index != -1) {
			item = this.container.get(index);
		}
		focus( index, item, false );
	}
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param index the index of the item which is already shown on this screen.
	 * @param force true when the item should be focused even when it is inactive (like a label for example)
	 */
	public void focus(int index, boolean force) {
		Item item = null;
		if (index != -1) {
			item = this.container.get(index);
		}
		focus( index, item, force );
	}
	
	/**
	 * Focuses the specified item.
	 * 
	 * @param index the index of the item which is already shown on this screen.
	 * @param item the item which is already shown on this screen.
	 * @param force true when the item should be focused even when it is inactive (like a label for example)
	 */
	public void focus(int index, Item item, boolean force) {
		if (index != -1 && item != null && (item.appearanceMode != Item.PLAIN || force ) ) {
			//#debug
			System.out.println("Screen: focusing item " + index );
			this.container.focus( index, item, 0 );
			if (index == 0) {
				this.container.setScrollYOffset( 0, false );
			}
		} else if (index == -1) {
			this.container.focus( -1 );
		} else {
			//#debug warn
			System.out.println("Screen: unable to focus item (did not find it in the container or is not activatable) " + index);
		}
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
//			this.subTitle.relativeY = this.titleHeight;
//			//#ifdef polish.ScreenWidth:defined
//				//#= this.subTitleHeight = subTitle.getItemHeight(${polish.ScreenWidth}, ${polish.ScreenWidth});
//			//#else
//				this.subTitleHeight = subTitle.getItemHeight( this.screenWidth, this.screenWidth );
//			//#endif
		}
		if (this.isInitialized) {
			calculateContentArea( 0, 0, this.screenWidth, this.screenHeight );
		}
	}
	
	//#if polish.Bugs.displaySetCurrentFlickers  && polish.useFullScreen
	/**
	 * Determines whether the screen is currently shown.
	 * When the screen is shown but the menu is openend, this method return false.
	 * 
	 * @return <code>true</code> if the screen is currently shown, <code>false</code> otherwise
	 * @see javax.microedition.lcdui.Displayable#isShown()
	 */
	public boolean isShown() {
		return (StyleSheet.currentScreen == this);
	}
	//#endif
	
	// when returning false in isShown(), repaint() will not trigger an actual repaint() on Sony Ericsson devices
	// so we cannot implement this behavior differently
//	//#if polish.Bugs.displaySetCurrentFlickers || tmp.menuFullScreen
//	/**
//	 * Determines whether the screen is currently shown.
//	 * When the screen is shown but the menu is openend, this method return false.
//	 * 
//	 * @return <code>true</code> if the screen is currently shown, <code>false</code> otherwise
//	 * @see javax.microedition.lcdui.Displayable#isShown()
//	 */
//	public boolean isShown() {
//		boolean isShown;
//		//#if polish.Bugs.displaySetCurrentFlickers
//			isShown = (StyleSheet.currentScreen == this);
//		//#else
//			isShown = super.isShown();
//		//#endif
//		//#if tmp.menuFullScreen
//			//#if tmp.useExternalMenuBar
//				isShown &= this.menuBar != null && !this.menuBar.isOpened;
//			//#else
//				isShown &= !this.menuOpened;
//			//#endif
//		//#endif
//		return isShown;
//	}
//	//#endif
	
	/**
	 * Retrieves the currently focused item.
	 * 
	 * @return the currently focused item, null when none is focused.
	 */
	public Item getCurrentItem() {
		if (this.container != null) {
			if (this.container.autoFocusEnabled) {
				Item[] items = this.container.getItems();
				for (int i = 0; i < items.length; i++) {
					if (i >= this.container.autoFocusIndex && items[i].appearanceMode != Item.PLAIN) {
						return items[i];
					}
				}
			}
			return this.container.focusedItem;
		}
		return this.focusedItem;
	}
	
	/**
	 * Retrieves the index of the currently focused item.
	 * 
	 * @return  the index of the currently focused item, -1 when no item has been selected
	 */
	public int getCurrentIndex() {
		if (this.container != null) {
			return this.container.focusedIndex;
		}
		return -1;
	}

	/**
	 * Checks whether the commands menu of the screen is currently opened.
	 * Useful when overriding the keyPressed() method.
	 * 
	 * @return true when the commands menu is opened.
	 */
	public boolean isMenuOpened() {
		//#if !tmp.menuFullScreen
			//# return false;
		//#elif tmp.useExternalMenuBar
			//# return this.menuBar.isOpened;
		//#else
			return this.menuOpened;
		//#endif

	}
	
	/**
	 * Closes the commands menu of this screen.
	 * This can only be done when the fullscreen mode is set to "menu" in your build.xml script.
	 */
	public void closeMenu() {
		//#if tmp.menuFullScreen
			//#if tmp.useExternalMenuBar
				this.menuBar.setOpen(false);
			//#else
				openMenu( false );
			//#endif
		//#endif
	}

	
	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this item.
	 * The default implementation does release any background resources.
	 */
	public void releaseResources() {
		if (this.background != null) {
			this.background.releaseResources();
		}
		if (this.container != null) {
			this.container.releaseResources();
		}
		//#ifdef tmp.menuFullScreen
			//#ifdef tmp.useExternalMenuBar
				this.menuBar.releaseResources();
			//#else
				this.menuContainer.releaseResources();
			//#endif
		//#endif
		//#ifdef tmp.usingTitle
			if (this.title != null) {
				this.title.releaseResources();
			}
		//#endif
		//#ifndef polish.skipTicker
			if (this.ticker != null) {
				this.ticker.releaseResources();
			}
		//#endif	
	}

	/**
	 * Scrolls this screen by the given amount.
	 * 
	 * @param amount the number of pixels, positive values scroll upwards, negative scroll downwards
	 */
	public void scrollRelative(int amount) {
		if (this.container != null) {
			this.container.setScrollYOffset( this.container.getScrollYOffset() + amount );
			repaint();
		}
	}

	/**
	 * Attaches data to this screen.
	 * This mechanism can be used to add business logic to screens.
	 * 
	 * @param data the screen specific data
	 * @see UiAccess#setData(Screen, Object)
	 * @see UiAccess#getData(Screen)
	 */
	public void setScreenData(Object data) {
		this.data = data;
	}
	
	/**
	 * Retrieves screen specific data.
	 * This mechanism can be used to add business logic to screens.
	 * 
	 * @return any screen specific data or null when no data has been attached before
	 * @see UiAccess#setData(Screen, Object)
	 * @see UiAccess#getData(Screen)
	 */
	public Object getScreenData() {
		return this.data;
	}
	
	/**
	 * Locates and returns the item at the given coordinate.
	 * 
	 * @param x horizontal position in pixels
	 * @param y vertical position in pixels
	 * @return the found item or null when no item is at the specific coordinate
	 */
	public Item getItemAt( int x, int y ) {
		Item item = null;
		//#ifdef tmp.menuFullScreen
			//#ifdef tmp.useExternalMenuBar
				item = this.menuBar.getItemAt( x - this.menuBar.relativeX, y - this.menuBar.relativeY );
				if (item != null) {
					return item;
				}
			//#else
				if (this.menuOpened) {
					item = this.menuContainer.getItemAt( x - this.menuContainer.relativeX, y - this.menuContainer.relativeY );
				} else
			//#endif
		//#endif
		if (this.container != null) {
			item = this.container.getItemAt( x - this.container.relativeX, y - this.container.relativeY );
			if (item != null) {
				return item;
			}
		}
		//#ifdef tmp.usingTitle
			if (this.title != null) {
				//item = this.title.getItemAt( x - this.title.relativeX, y - this.title.relativeY );
				item = this.title.getItemAt( x - this.title.relativeX, y - this.title.relativeY );
				if (item != null) {
					return item;
				}
			}
		//#endif
		//#ifndef polish.skipTicker
			if (this.ticker != null) {
				item = this.ticker.getItemAt( x - this.ticker.relativeX, y - this.ticker.relativeY );
				if (item != null) {
					return item;
				}
			}
		//#endif
		//#if polish.ScreenInfo.enable
			if (ScreenInfo.item != null && ScreenInfo.isVisible()) {
				item = ScreenInfo.item.getItemAt( x - ScreenInfo.item.relativeX, y - ScreenInfo.item.relativeY );
				if (item != null) {
					return item;
				}
			}
		//#endif
		return null;
	}

	/**
	 * Retrieves the style currently used by this screen.
	 * 
	 * @return this screen's style
	 */
	public Style getScreenStyle() {
		return this.style;
	}
	
	/**
	 * Sets the <code>ItemStateListener</code> for the <code>Screen</code>, 
	 * replacing any previous <code>ItemStateListener</code>. 
	 * If
	 * <code>iListener</code> is <code>null</code>, simply
	 * removes the previous <code>ItemStateListener</code>.
	 * 
	 * @param iListener the new listener, or null to remove it
	 */
	public void setItemStateListener( ItemStateListener iListener)
	{
		this.itemStateListener = iListener;
	}
	
	/**
	 * Determines if the given keycode belongs to a softkey
	 * 
	 * @param keyCode the keycode
	 * @return true when the key code represents a softkey
	 */
	public boolean isSoftKey( int keyCode ) {
		if (keyCode == LEFT_SOFT_KEY
				|| keyCode == RIGHT_SOFT_KEY
				//#if polish.key.MiddleSoftKey:defined
					//#= || keyCode == ${polish.key.MiddleSoftKey}
				//#endif
				) 
		{
			return true;
		}
		return false;
	}
	
	//#if polish.LibraryBuild
	/**
	 * Sets the <code>ItemStateListener</code> for the <code>Screen</code>, 
	 * replacing any previous <code>ItemStateListener</code>. 
	 * If
	 * <code>iListener</code> is <code>null</code>, simply
	 * removes the previous <code>ItemStateListener</code>.
	 * 
	 * @param iListener the new listener, or null to remove it
	 */
	public void setItemStateListener( javax.microedition.lcdui.ItemStateListener iListener ) {
		throw new RuntimeException("Unable to use standard ItemStateListener in a screen.");
	}
	//#endif

	
	/**
	 * Adds the given item to the queue for state notifications.
	 * The ItemStateListener will be called at the next possibility.
	 * 
	 * @param item the item which contents have been edited.
	 */
	protected void notifyStateListener( Item item ) {
		if (this.itemStateListener != null) {
			this.itemStateListener.itemStateChanged(item);
		}

//		if (this.itemStateListener != null) {
//			if (this.stateNotifyQueue == null) {
//				this.stateNotifyQueue = new ArrayList();
//			}
//			synchronized (this.stateNotifyQueue) {
//				this.stateNotifyQueue.add( item );
//			}
//			//#debug
//			System.out.println("added item " + item + " to stateNotifyQueue with listener " + this.itemStateListener + ", size of queue=" + this.stateNotifyQueue.size() + " to form " + this  );
//		}
	}
	
//	/**
//	 * Notifies the ItemStateListener about the changes which occurred to the items.
//	 */
//	protected void notifyStateListener() {
//		if (this.stateNotifyQueue != null && this.itemStateListener != null) {
//			//Item lastItem = null;
//			while (this.stateNotifyQueue.size() > 0) {
//				Item item;
//				synchronized (this.stateNotifyQueue) {
//					item = (Item) this.stateNotifyQueue.remove(0);
//				}
//				//if (item != lastItem) { // 2007-08-06: sometimes there are two subsequent fast changes, so this has to be forwarded correctly:
//					//#debug
//					System.out.println("notifying ItemStateListener for item " + item + " and form " + this ); 
//					this.itemStateListener.itemStateChanged(item);
//					//lastItem = item;
//				//}
//			}
//			//#debug
//			System.out.println("done notifying ItemStateListener."); 
//		}
//	}

	/**
	 * Checks if the keyboard (if any) is currently accessible by the application.
	 * This is useful for devices that can be opened by the user like the Nokia/E70.
	 * 
	 * @return true when the keyboard (if there is one) is accessible by the application.
	 */
	protected boolean isKeyboardAccessible() {
		//#if polish.key.supportsAsciiKeyMap.condition == open
			return getWidth() > getHeight();
		//#elif polish.key.supportsAsciiKeyMap && !polish.key.supportsAsciiKeyMap.condition:defined
			//# return true;
		//#else
			//# return false;
		//#endif
	}

	


		
	
//#ifdef polish.Screen.additionalMethods:defined
	//#include ${polish.Screen.additionalMethods}
//#endif
	
	

}