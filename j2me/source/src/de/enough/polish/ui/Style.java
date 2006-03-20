//#condition polish.usePolishGui
/*
 * Created on 04-Jan-2004 at 19:43:08.
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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;


/**
 * <p>Style defines the design of any widget.</p>
 * <p>This class is used by the widgets. If you only use the predefined
 *       widgets you do not need to work with this class.
 * </p>
 * @author Robert Virkus, robert@enough.de
 * <pre>
 * history
 *        04-Jan-2004 - rob creation
 * </pre>
 */
public class Style
//#if (polish.useBeforeStyle || polish.useAfterStyle) && polish.images.backgroundLoad
	//#define tmp.imageConsumer
	implements ImageConsumer
//#endif
{
	//#if polish.cldc1.1
		//# public final static Boolean TRUE = Boolean.TRUE;
		//# public final static Boolean FALSE = Boolean.FALSE;
	//#else
		public final static Boolean TRUE = new Boolean( true );
		public final static Boolean FALSE = new Boolean( false );
	//#endif
	//#ifdef polish.useDynamicStyles
		/**
		 * The name of this style. The name is only accessible when
		 * the preprocessing-symbol polish.useDynamicStyles is defined.
		 */
		public final String name;
	//#endif
	//#ifdef polish.useBeforeStyle
		//#ifdef tmp.imageConsumer
			private String beforeUrl;
		//#endif
		/**
		 * The width of the before-element. This value can only be
		 * accessed when the preprocessing-symbol polish.useBeforeStyle is defined.
		 */
		public int beforeWidth;
		/**
		 * The height of the before-element. This value can only be
		 * accessed when the preprocessing-symbol polish.useBeforeStyle is defined.
		 */
		public int beforeHeight;
		/**
		 * The before-element. This value can only be
		 * accessed when the preprocessing-symbol polish.useBeforeStyle is defined.
		 */
		public Image before;
	//#endif
	//#ifdef polish.useAfterStyle
		//#ifdef tmp.imageConsumer
			private String afterUrl;
		//#endif
		/**
		 * The width of the after-element. This value can only be
		 * accessed when the preprocessing-symbol polish.useAfterStyle is defined.
		 */
		public int afterWidth;
		/**
		 * The height of the after-element. This value can only be
		 * accessed when the preprocessing-symbol polish.useAfterStyle is defined.
		 */
		public int afterHeight;
		/**
		 * The after-element. This value can only be
		 * accessed when the preprocessing-symbol polish.useAfterStyle is defined.
		 */
		public Image after;
	//#endif
	public final Background background;
	public final Border border;
	public final Font font;
	public final int fontColor;
	public final int paddingLeft;
	public final int paddingTop;
	public final int paddingRight;
	public final int paddingBottom;
	public final int paddingVertical;
	public final int paddingHorizontal;
	public final int marginLeft;
	public final int marginTop;
	public final int marginRight;
	public final int marginBottom;

	public final int layout;
	
	private final short[] attributeKeys;
	private final Object[] attributeValues;

	/**
	 * Creates a new Style.
	 * 
	 * @param marginLeft the margin in pixels to the next element on the left
	 * @param marginRight the margin in pixels to the next element on the right
	 * @param marginTop the margin in pixels to the next element on the top
	 * @param marginBottom the margin in pixels to the next element on the bottom
	 * @param paddingLeft the padding between the left border and content in pixels
	 * @param paddingRight the padding between the right border and content in pixels
	 * @param paddingTop the padding between the top border and content in pixels
	 * @param paddingBottom the padding between the bottom border and content in pixels
	 * @param paddingVertical the vertical padding between internal elements of an item 
	 * @param paddingHorizontal the horizontal padding between internal elements of an item
	 * @param layout the layout for this style, e.g. Item.LAYOUT_CENTER
	 * @param fontColor the color of the font
	 * @param font the content-font for this style
	 * @param background the background for this style
	 * @param border the border for this style
	 * @param beforeUrl the URL of the before element. This is inserted before items
	 *            with this style. 
	 * @param afterUrl the URL of the after element. This is inserted after items
	 *            with this style.
	 * @param name the name of this style. Is only used when dynamic styles are used.
	 * @param attributeKeys the integer-IDs of any additional attributes. Can be null.
	 * @param attributeValues the values of any additional attributes. Can be null.
	 * 			
	 */
	public Style( int marginLeft, int marginRight, int marginTop, int marginBottom,
			int paddingLeft, int paddingRight, int paddingTop, int paddingBottom, int paddingVertical, int paddingHorizontal,
			int layout,
			int fontColor, Font font,  
			Background background, Border border 
			//#ifdef polish.useBeforeStyle
			, String beforeUrl
			//#endif
			//#ifdef polish.useAfterStyle
			, String afterUrl
			//#endif
			//#ifdef polish.useDynamicStyles
			, String name
			//#endif
			, short[] attributeKeys
			, Object[] attributeValues
			) 
	{
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
		this.paddingLeft = paddingLeft;
		this.paddingRight = paddingRight;
		this.paddingTop = paddingTop;
		this.paddingBottom = paddingBottom;
		this.paddingVertical = paddingVertical;
		this.paddingHorizontal = paddingHorizontal;
		this.layout = layout;
		this.fontColor = fontColor;
		this.font = font;
		this.background = background;
		this.border = border;
		//#ifdef polish.useBeforeStyle
			if (beforeUrl != null) {
				try {
					//#ifdef tmp.imageConsumer
						this.beforeUrl = beforeUrl;
					//#endif
					this.before = StyleSheet.getImage(beforeUrl, this, true);
				} catch (Exception e) {
					//#debug error
					System.out.println("unable to load before-image [" + beforeUrl + "]. " + e );
				}
				if (this.before != null) {
					this.beforeWidth = this.before.getWidth();
					this.beforeHeight = this.before.getHeight();
				}
			}
		//#endif
		//#ifdef polish.useAfterStyle
			if (afterUrl != null) {
				try {
					//#ifdef tmp.imageConsumer
						this.afterUrl = afterUrl;
					//#endif
					this.after = StyleSheet.getImage(afterUrl, this, true);
				} catch (Exception e) {
					//#debug error
					System.out.println("unable to load after-image [" + afterUrl + "]. " + e );
				}
				if (this.after != null) {
					this.afterWidth = this.after.getWidth();
					this.afterHeight = this.after.getHeight();
				}
			}
		//#endif
		//#ifdef polish.useDynamicStyles
		this.name = name;
		//#endif
		this.attributeValues = attributeValues;
		this.attributeKeys = attributeKeys;
		//#ifdef false
			// this is only used, so that the IDE does not complain about unused code:
			getProperty( -1 );
			getIntProperty( -1 );
			getBooleanProperty( -1 );
			getObjectProperty( -1 );
		//#endif
	}
	
	//#ifdef false
	/**
	 * Retrieves a non-standard property of this style.
	 * 
	 * @param propName the name of the property
	 * @return the value of this property as a String. If none has been defined, null will be returned.
	 */
	public String getProperty( String propName ) {
		return propName;
	}
	//#endif
	
	//#ifdef false
	/**
	 * Retrieves a non-standard property of this style.
	 * 
	 * @param propName the name of the property
	 * @return the value of this property. If none has been defined, null will be returned.
	 */
	public Object getObjectProperty( String propName ) {
		return propName;
	}
	//#endif


	//#ifdef false
	/**
	 * Retrieves a non-standard integer property of this style.
	 * 
	 * @param propName the name of the property
	 * @return the value of this property as an Integer object. If none has been defined, null will be returned.
	 */
	public Integer getIntProperty( String propName ) {
		return new Integer( 0 );
	}
	//#endif
	
	//#ifdef false
	/**
	 * Retrieves a non-standard boolean property of this style.
	 * 
	 * @param propName the name of the property
	 * @return the value of this property as an Boolean object. If none has been defined, null will be returned.
	 */
	public Boolean getBooleanProperty( String propName ) {
		return new Boolean( false );
	}
	//#endif

	//#ifdef false
		private String getProperty( int key ) {
	//#else
		//# public String getProperty( int key ) {
	//#endif
		if (this.attributeKeys == null) {
			return null;
		}
		for (int i = 0; i < this.attributeKeys.length; i++ ) {
			if (this.attributeKeys[i] == key) {
				Object value = this.attributeValues[i];
				if (value != null) {
					return value.toString();
				} else {
					return null;
				}
			}
		}
		return null;
	}
		
	//#ifdef false
		private Object getObjectProperty( int key ) {
	//#else
		//# public Object getObjectProperty( int key ) {
	//#endif
		if (this.attributeKeys == null) {
			return null;
		}
		for (int i = 0; i < this.attributeKeys.length; i++ ) {
			if (this.attributeKeys[i] == key) {
				return this.attributeValues[i];
			}
		}
		return null;
	}


	//#ifdef false
		private Integer getIntProperty( int key ) {
	//#else
		//# public Integer getIntProperty( int key ) {
	//#endif
		if (this.attributeKeys == null) {
			return null;
		}
		for (int i = 0; i < this.attributeKeys.length; i++ ) {
			if (this.attributeKeys[i] == key) {
				return (Integer) this.attributeValues[i];
			}
		}
		return null;
	}
	
	//#ifdef false
		private Boolean getBooleanProperty( int key ) {
	//#else
		//# public Boolean getBooleanProperty( int key ) {
	//#endif
		if (this.attributeKeys == null) {
			return null;
		}
		for (int i = 0; i < this.attributeKeys.length; i++ ) {
			if (this.attributeKeys[i] == key) {
				return (Boolean) this.attributeValues[i];
			}
		}
		return null;
	}
	
	
	//#ifdef tmp.imageConsumer
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ImageConsumer#setImage(java.lang.String, javax.microedition.lcdui.Image)
	 */
	public void setImage(String name, Image image) {
		//#ifdef polish.useBeforeStyle
		if (name.equals(this.beforeUrl)) {
			this.before = image;
			this.beforeHeight = image.getHeight();
			this.beforeWidth = image.getWidth();
		}
		//#endif
		//#ifdef polish.useAfterStyle
		if (name.equals(this.afterUrl)) {
			this.after = image;
			this.afterHeight = image.getHeight();
			this.afterWidth = image.getWidth();
		}
		//#endif
	}
	//#endif
	
//#ifdef polish.Style.additionalMethods:defined
	//#include ${polish.Style.additionalMethods}
//#endif
	

}
