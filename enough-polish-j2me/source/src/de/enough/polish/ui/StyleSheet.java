/*
 * Created on 05-Jan-2004 at 20:41:52.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import de.enough.polish.ui.tasks.ImageTask;
import de.enough.polish.util.Locale;

/**
 * <p>Manages all defined styles of a specific project.</p>
 * <p>This class is actually pre-processed to get the styles specific for the project and the device.</p>
 *
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        05-Jan-2004 - rob creation
 * </pre>
 */
public final class StyleSheet {
	
	protected static Hashtable imagesByName;
	//#ifdef polish.images.backgroundLoad
		private static Hashtable scheduledImagesByName;
		//private static final Boolean TRUE = new Boolean( true );
		private static Timer timer;
	//#endif
	//#ifdef false
		public static Style defaultStyle = null;
		public static Style focusedStyle = null;
		public static Style labelStyle = null; 
		public static Style menuStyle = null;
		private static Hashtable stylesByName = new Hashtable();
	//#endif

	//#if polish.ScreenChangeAnimation.forward:defined
		//#if false
			private static ScreenChangeAnimation forwardAnimation;
			private static ScreenChangeAnimation backwardAnimation;			
		//#endif
		//#= private static ScreenChangeAnimation forwardAnimation = ${polish.ScreenChangeAnimation.forward};
		//#if polish.ScreenChangeAnimation.back:defined
			//#= private static ScreenChangeAnimation backwardAnimation = ${polish.ScreenChangeAnimation.back};
		//#elif polish.ScreenChangeAnimation.backward:defined
			//#= private static ScreenChangeAnimation backwardAnimation = ${polish.ScreenChangeAnimation.backward};
		//#else
			//#abort You need to define the polish.ScreenChangeAnimation.backward screen change animation as well, when you define the forward animation!
		//#endif
	//#endif

	
	
	// do not change the following line!
//$$IncludeStyleSheetDefinitionHere$$//
		
	public static Screen currentScreen;	
	public static Display display;
	public static AnimationThread animationThread;
	//#ifdef polish.i18n.useDynamicTranslations
		public static Command OK_CMD = new Command( Locale.get("polish.command.ok"), Command.OK, 2 );
	//#elifdef polish.command.ok:defined
		//#= public static final Command OK_CMD = new Command("${polish.command.ok}", Command.OK, 2 );
	//#else
		//# public static final Command OK_CMD = new Command("OK", Command.OK, 2 );
	//#endif
	//#ifdef polish.i18n.useDynamicTranslations
		public static Command CANCEL_CMD = new Command(Locale.get("polish.command.cancel"), Command.CANCEL, 2 );
	//#elifdef polish.command.cancel:defined
		//#= public static final Command CANCEL_CMD = new Command("${polish.command.cancel}", Command.CANCEL, 3 );
	//#else
		//# public static final Command CANCEL_CMD = new Command("Cancel", Command.CANCEL, 3 );
	//#endif

	//#if polish.ScreenChangeAnimation.allowConfiguration == true
		public static boolean enableScreenChangeAnimations = true;
	//#endif

	/**
	 * Retrieves the image with the given name.
	 * When the image has been cached before, it will be returned immediately.
	 * When it has not been cached before, it either will be loaded directly
	 * or in a background thread. This behaviour is set in the 
	 * <a href="../../../../definitions/polish_xml.html">polish.xml</a> file.
	 * 
	 * @param url the URL of the Image, e.g. "/background.png"
	 * @param parent the object which needs the image, when the image should be loaded
	 * 		   		in the background, the parent need to implement
	 * 				the ImageConsumer interface when it wants to be notified when
	 * 				the picture has been loaded.
	 * @param cache true when the image should be cached for later retrieval.
	 *              This costs RAM obviously, so you should decide carefully if
	 *              large images should be cached.
	 * @return the image when it either was cached or is loaded directly.
 	 *              When the should be loaded in the background, it will be later
	 *              set via the ImageConsumer.setImage()-method.
	 * @throws IOException when the image could not be loaded directly
	 * @see ImageConsumer#setImage(String, Image)
	 */
	public static Image getImage( String url, Object parent, boolean cache )
	throws IOException 
	{
		// check if the image has been cached before:
		//#if polish.allowImageCaching != false
			if ( imagesByName != null ) {
				Image image = (Image) imagesByName.get( url );
				if (image != null) {
					return image;
				}
			}
		//#endif
		//#if ! polish.images.backgroundLoad
			// when images should be loaded directly, try to do so now:
			//#ifdef polish.classes.ImageLoader:defined
				//#= Image image = ${ classname( polish.classes.ImageLoader ) }.loadImage( url );
			//#else
				Image image = Image.createImage( url );
			//#endif
			//#if polish.allowImageCaching != false
				if (cache) {
					if (imagesByName == null ) {
						imagesByName = new Hashtable();
					}
					imagesByName.put( url, image );
				}
			//#endif
			//# return image;
		//#else
			// when images should be loaded in the background, 
			// tell the background-thread to do so now:		
			if ( ! (parent instanceof ImageConsumer)) {
				//#debug error
				System.out.println("StyleSheet.getImage(..) needs an ImageConsumer when images are loaded in the background!");
				return null;
			}
			if (scheduledImagesByName == null ) {
				scheduledImagesByName = new Hashtable();
			}
			ImageQueue queue = (ImageQueue) scheduledImagesByName.get(url);
			if (queue != null) {
				// this image is already scheduled to load:
				queue.addConsumer((ImageConsumer) parent);
				return null;
			}
			scheduledImagesByName.put( url, new ImageQueue( (ImageConsumer) parent, cache ) );
			if (imagesByName == null ) {
				imagesByName = new Hashtable();
			}
			if (timer == null) {
				timer = new Timer();
			}
			ImageTask task = new ImageTask( url );
			timer.schedule( task, 10 );
			return null;
		//#endif
	}
	
	//#ifdef polish.images.backgroundLoad
	/**
	 * Notifies the GUI items which requested images about the successful loading of thoses images.
	 * 
	 * @param name the URL of the image
	 * @param image the image 
	 */
	public static void notifyImageConsumers( String name, Image image ) {
		ImageQueue queue = (ImageQueue) scheduledImagesByName.remove(name);
		if (queue != null) {
			if (queue.cache) {
				imagesByName.put( name, image );
			}
			queue.notifyConsumers(name, image);
			if (true) {
				return;
			}
			if (currentScreen != null) {
				currentScreen.repaint();
			}
		}
	}
	//#endif
	
	/**
	 * Gets the style with the specified name.
	 * 
	 * @param name the name of the style
	 * @return the specified style or null when no style with the given 
	 * 	       name has been defined.
	 */
	public static Style getStyle( String name ) {
		return (Style) stylesByName.get( name );
	}
	
	//#ifdef polish.useDynamicStyles
	/**
	 * Retrieves the style for the given item.
	 * This function is only available when the &lt;buildSetting&gt;-attribute
	 * [useDynamicStyles] is enabled.
	 * This function allows to set styles without actually using the preprocessing-
	 * directive //#style. Beware that this dynamic style retrieval is not as performant
	 * as the direct-style-setting with the //#style preprocessing directive.
	 *  
	 * @param item the item for which the style should be retrieved
	 * @return the appropriate style. When no specific style is found,
	 *         the default style is returned.
	 */
	public static Style getStyle( Item item ) {
		if (item.screen == null) {
			//#debug error
			System.out.println("unable to retrieve style for item [" + item.getClass().getName() + "] without screen.");
			return defaultStyle;
		}
		String itemCssSelector = item.cssSelector;
		String screenCssSelector = item.screen.cssSelector;
		Style style = null;
		String fullStyleName;
		StringBuffer buffer = new StringBuffer();
		buffer.append( screenCssSelector );
		if (item.parent == null) {
			//#debug
			System.out.println("item.parent == null");
			buffer.append('>').append( itemCssSelector );
			fullStyleName = buffer.toString();
			style = (Style) stylesByName.get( fullStyleName );
			if (style != null) {
				return style;
			}
			style = (Style) stylesByName.get( screenCssSelector + " " + itemCssSelector );
		} else if (item.parent.parent == null) {
			//#debug
			System.out.println("Item has one parent.");
			// this item is propably in a form or list,
			// typical hierarchy is for example "form>container>p"                                                 
			Item parent = item.parent;
			String parentCssSelector = parent.cssSelector;
			if (parentCssSelector == null) {
				parentCssSelector = parent.createCssSelector();
			}
			//#debug
			System.out.println( parent.getClass().getName() + "-css-selector: " + parentCssSelector );
			buffer.append('>').append( parentCssSelector )
				  .append('>').append( itemCssSelector );
			fullStyleName = buffer.toString();
			//#debug
			System.out.println("trying " + fullStyleName);
			style = (Style) stylesByName.get( fullStyleName );
			if (style != null) {
				return style;
			}
			// 1. try: "screen item":
			String styleName = screenCssSelector + " " + itemCssSelector;
			//#debug
			System.out.println("trying " + styleName);
			style = (Style) stylesByName.get( styleName );
			if (style == null) {
				// 2. try: "screen*item":
				styleName = screenCssSelector + "*" + itemCssSelector;
				//#debug
				System.out.println("trying " + styleName);
				style = (Style) stylesByName.get( styleName );
				if (style == null) {
					// 3. try: "parent>item"
					styleName = parentCssSelector + ">" + itemCssSelector;
					//#debug
					System.out.println("trying " + styleName);
					style = (Style) stylesByName.get( styleName );
					if (style == null) {
						// 4. try: "parent item"
						styleName = parentCssSelector + " " + itemCssSelector;
						//#debug
						System.out.println("trying " + styleName);
						style = (Style) stylesByName.get( styleName );
					}
				}
			}
			//#debug
			System.out.println("found style: " + (style != null));
		} else {
			//#debug
			System.out.println("so far unable to set style: complex item setup");
			// this is a tiny bit more complicated....
			fullStyleName = null;
		}
		if (style == null) {
			// try just the item:
			//#debug
			System.out.println("trying " + itemCssSelector);
			style = (Style) stylesByName.get( itemCssSelector );
			if (style == null) {
				//#debug
				System.out.println("Using default style for item " + item.getClass().getName() );
				style = defaultStyle;
			}
			//#ifdef polish.debug.debug
				else {
					//#debug
					System.out.println("Found style " + itemCssSelector );
				}
			//#endif
		}
		if ( fullStyleName != null && style != null ) {
			stylesByName.put( fullStyleName, style );
		}
		return style;
	}
	//#endif

	//#ifdef polish.useDynamicStyles
	/**
	 * Retrieves a dynamic style for the given screen.
	 * 
	 * @param screen the screen for which a style should be retrieved
	 * @return the style for the given screen.
	 */
	public static Style getStyle(Screen screen) {
		Style style = (Style) stylesByName.get( screen.cssSelector );
		if (style == null) {
			return defaultStyle;
		}
		return style;
	}		
	//#endif
	
	//#if polish.css.screen-change-animation || polish.ScreenChangeAnimation.forward:defined
	/**
	 * Includes an animation while changing the screen.
	 *  
	 * @param display the display
	 * @param nextDisplayable the new screen, animations are only included for de.enough.polish.ui.Screen classes
	 */
	public static void setCurrent( Display display, Displayable nextDisplayable ) {
		if ( nextDisplayable instanceof AccessibleCanvas ) {
			//#if polish.ScreenChangeAnimation.allowConfiguration == true
				if (!enableScreenChangeAnimations) {
					//#if polish.Bugs.displaySetCurrentFlickers
						MasterCanvas.setCurrent(display, nextDisplayable);
					//#else
						display.setCurrent( nextDisplayable );						
					//#endif
					return;	
				}
			//#endif

			try {
				Screen nextScreen = null;
				if ( nextDisplayable instanceof Screen ) {
					nextScreen = (Screen) nextDisplayable;
				}
				ScreenChangeAnimation screenAnimation = null;
				Displayable lastDisplayable = null;
				//#if polish.Bugs.displaySetCurrentFlickers
					if ( MasterCanvas.instance != null ) {
						//# lastDisplayable = MasterCanvas.instance.currentDisplayable;
					}
				//#else
					lastDisplayable = display.getCurrent();
				//#endif
	
				Screen lastScreen = null;
				Style screenstyle = null;
				//#if polish.ScreenChangeAnimation.forward:defined
					if (lastDisplayable != null && lastDisplayable instanceof Screen) {
						lastScreen = (Screen) lastDisplayable;
						Command lastCommand = lastScreen.lastTriggeredCommand;
						if (lastCommand != null && lastCommand.getCommandType() == Command.BACK ) {
							screenAnimation = backwardAnimation;
							screenstyle = lastScreen.style;
						}
					}
					if ( screenAnimation == null ) {
						screenAnimation = forwardAnimation;
						if (nextScreen != null) {
							screenstyle = nextScreen.style;
						}
					}
				//#else					
					if (nextScreen != null && nextScreen.style != null) {
						screenstyle = nextScreen.style;
						screenAnimation = (ScreenChangeAnimation) screenstyle.getObjectProperty("screen-change-animation");
					}
					if (lastDisplayable != null && lastDisplayable instanceof ScreenChangeAnimation ) {
						//#debug
						System.out.println("StyleSheet: last displayable is a ScreenChangeAnomation" );
						lastDisplayable = ((ScreenChangeAnimation) lastDisplayable).nextDisplayable;
					}
					if (lastDisplayable != null && lastDisplayable instanceof Screen) {
						//#debug
						System.out.println("StyleSheet: last displayble is a Screen");
						lastScreen = (Screen) lastDisplayable;
						if (screenAnimation == null && lastScreen.style != null) {
							screenstyle = lastScreen.style;
							screenAnimation = (ScreenChangeAnimation) screenstyle.getObjectProperty("screen-change-animation");
							//#debug
							System.out.println("StyleSheet: Using screen animation of last screen");
						}
					}
					//#if polish.Screen.showScreenChangeAnimationOnlyForScreen
						if ( nextScreen == null ) {
							screenAnimation = null;
						}
					//#endif
					if ( screenAnimation == null ) {
						//#debug
						System.out.println("StyleSheet: found no screen animation");
						//#if polish.Bugs.displaySetCurrentFlickers
							MasterCanvas.setCurrent(display, nextDisplayable);
						//#else
							display.setCurrent( nextDisplayable );						
						//#endif
						return;
					}
				//#endif
				
				int width;
				int height;
				//#if polish.FullCanvasSize:defined
					//#= width = ${polish.FullCanvasWidth};
					//#= height = ${polish.FullCanvasHeight};
				//#else
					//#if polish.midp2
						width = nextDisplayable.getWidth();
						height = nextDisplayable.getHeight();
					//#else
						if (nextScreen != null) {
							width = lastScreen.getWidth();
							height = lastScreen.getHeight();
						} else if (nextDisplayable instanceof Canvas) {
							width = ((Canvas) nextDisplayable).getWidth();
							height = ((Canvas) nextDisplayable).getHeight();
						} else {
							Canvas canvas = new Canvas() {
								public void paint( Graphics g) {}
							};
							width = canvas.getWidth();
							height = canvas.getHeight();
						}
					//#endif
				//#endif
				Image lastScreenImage = Image.createImage(width, height);
				Graphics g = lastScreenImage.getGraphics(); 
				if ( lastDisplayable != null && lastDisplayable instanceof AccessibleCanvas) {
					//#debug
					System.out.println("StyleSheet: last screen is painted");
					( (AccessibleCanvas)lastDisplayable).paint( g );
				//#if polish.ScreenChangeAnimation.blankColor:defined
					} else {
						//#= g.setColor( ${polish.ScreenChangeAnimation.blankColor} );
						g.fillRect( 0, 0, width, height );
				//#endif
				}
				Image nextScreenImage = Image.createImage(width, height);
				g = nextScreenImage.getGraphics();
				AccessibleCanvas nextCanvas = (AccessibleCanvas) nextDisplayable;
				nextCanvas.showNotify();
				nextCanvas.paint( g );
				//#debug
				System.out.println("StyleSheet: showing screen animation " + screenAnimation.getClass().getName() );
				if ( screenstyle == null ) {
					screenstyle = defaultStyle;
				}
				screenAnimation.show( screenstyle, display, width, height, lastScreenImage, nextScreenImage, nextCanvas, nextDisplayable );
			} catch (Exception e) {
				//#debug error
				System.out.println("Screen: unable to start screen change animation" + e );
				//#if polish.Bugs.displaySetCurrentFlickers
					MasterCanvas.setCurrent(display, nextDisplayable);
				//#else
					display.setCurrent( nextDisplayable );						
				//#endif
			}
			
		} else {
			//#if polish.Bugs.displaySetCurrentFlickers
				MasterCanvas.setCurrent(display, nextDisplayable);
			//#else
				display.setCurrent( nextDisplayable );						
			//#endif
		}
	}
	//#endif
	
	/**
	 * Releases all (memory intensive) resources such as images or RGB arrays of this style sheet.
	 */
	public static void releaseResources() {
		//#if polish.allowImageCaching != false
		if (imagesByName != null) {
			imagesByName.clear();
		}
		//#endif
		//#ifdef polish.useDynamicStyles
			Enumeration enumeration = stylesByName.elements();
			while (enumeration.hasMoreElements()) {
				Style style = (Style) enumeration.nextElement();
				style.releaseResources();
			}
		//#endif
		//#ifdef polish.StyleSheet.releaseResources:defined
			//#include ${polish.StyleSheet.releaseResources}
		//#endif
	}

	
	
//#ifdef polish.StyleSheet.additionalMethods:defined
	//#include ${polish.StyleSheet.additionalMethods}
//#endif

}
