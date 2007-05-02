/*
 * Created on Apr 23, 2007 at 4:26:57 PM.
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
package de.enough.polish.styleeditor;

import java.util.List;

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.ui.Style;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class EditStyle {
	
	private ItemOrScreen itemOrScreen;
	private Style style;
	
	private String layout;
	private String margin;
	private String marginLeft;
	private String marginRight;
	private String marginTop;
	private String marginBottom;
	private String padding;
	private String paddingLeft;
	private String paddingRight;
	private String paddingTop;
	private String paddingBottom;
	private String paddingVertical;
	private String paddingHorizontal;
	private String fontColor;
	private String fontStyle;
	private String fontSize;
	private List<StyleAttribute> styleAttributes;
	
	/**
	 * Creates a new edit style with an empty style and no item/screen
	 */
	public EditStyle() {
		this( new Style(), null );
	}

	public EditStyle( Style style, ItemOrScreen itemOrScreen ) {
		this.itemOrScreen = itemOrScreen;
		setStyle( style );
	}
	

	/**
	 * @return the fontColor
	 */
	public String getFontColor() {
	return this.fontColor;}
	



	/**
	 * @param fontColor the fontColor to set
	 */
	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}



	/**
	 * @return the fontSize
	 */
	public String getFontSize() {
	return this.fontSize;}
	



	/**
	 * @param fontSize the fontSize to set
	 */
	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}



	/**
	 * @return the fontStyle
	 */
	public String getFontStyle() {
	return this.fontStyle;}
	



	/**
	 * @param fontStyle the fontStyle to set
	 */
	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}



	/**
	 * @return the layout
	 */
	public String getLayout() {
	return this.layout;}
	



	/**
	 * @param layout the layout to set
	 */
	public void setLayout(String layout) {
		this.layout = layout;
	}



	/**
	 * @return the margin
	 */
	public String getMargin() {
	return this.margin;}
	



	/**
	 * @param margin the margin to set
	 */
	public void setMargin(String margin) {
		this.margin = margin;
	}



	/**
	 * @return the marginBottom
	 */
	public String getMarginBottom() {
	return this.marginBottom;}
	



	/**
	 * @param marginBottom the marginBottom to set
	 */
	public void setMarginBottom(String marginBottom) {
		this.marginBottom = marginBottom;
	}



	/**
	 * @return the marginLeft
	 */
	public String getMarginLeft() {
	return this.marginLeft;}
	



	/**
	 * @param marginLeft the marginLeft to set
	 */
	public void setMarginLeft(String marginLeft) {
		this.marginLeft = marginLeft;
	}



	/**
	 * @return the marginRight
	 */
	public String getMarginRight() {
	return this.marginRight;}
	



	/**
	 * @param marginRight the marginRight to set
	 */
	public void setMarginRight(String marginRight) {
		this.marginRight = marginRight;
	}



	/**
	 * @return the marginTop
	 */
	public String getMarginTop() {
	return this.marginTop;}
	



	/**
	 * @param marginTop the marginTop to set
	 */
	public void setMarginTop(String marginTop) {
		this.marginTop = marginTop;
	}



	/**
	 * @return the padding
	 */
	public String getPadding() {
	return this.padding;}
	



	/**
	 * @param padding the padding to set
	 */
	public void setPadding(String padding) {
		this.padding = padding;
	}



	/**
	 * @return the paddingBottom
	 */
	public String getPaddingBottom() {
	return this.paddingBottom;}
	



	/**
	 * @param paddingBottom the paddingBottom to set
	 */
	public void setPaddingBottom(String paddingBottom) {
		this.paddingBottom = paddingBottom;
	}



	/**
	 * @return the paddingHorizontal
	 */
	public String getPaddingHorizontal() {
	return this.paddingHorizontal;}
	



	/**
	 * @param paddingHorizontal the paddingHorizontal to set
	 */
	public void setPaddingHorizontal(String paddingHorizontal) {
		this.paddingHorizontal = paddingHorizontal;
	}



	/**
	 * @return the paddingLeft
	 */
	public String getPaddingLeft() {
	return this.paddingLeft;}
	



	/**
	 * @param paddingLeft the paddingLeft to set
	 */
	public void setPaddingLeft(String paddingLeft) {
		this.paddingLeft = paddingLeft;
	}



	/**
	 * @return the paddingRight
	 */
	public String getPaddingRight() {
	return this.paddingRight;}
	



	/**
	 * @param paddingRight the paddingRight to set
	 */
	public void setPaddingRight(String paddingRight) {
		this.paddingRight = paddingRight;
	}



	/**
	 * @return the paddingTop
	 */
	public String getPaddingTop() {
	return this.paddingTop;}
	



	/**
	 * @param paddingTop the paddingTop to set
	 */
	public void setPaddingTop(String paddingTop) {
		this.paddingTop = paddingTop;
	}



	/**
	 * @return the paddingVertical
	 */
	public String getPaddingVertical() {
	return this.paddingVertical;}
	



	/**
	 * @param paddingVertical the paddingVertical to set
	 */
	public void setPaddingVertical(String paddingVertical) {
		this.paddingVertical = paddingVertical;
	}



	/**
	 * @return the style
	 */
	public Style getStyle() {
		return this.style;
	}
	



	/**
	 * @param style the style to set
	 */
	public void setStyle(Style style) {
		this.style = style;
		this.marginLeft = Integer.toString( style.marginLeft );
		this.marginRight = Integer.toString( style.marginRight );
		this.marginTop = Integer.toString( style.marginTop );
		this.marginBottom = Integer.toString( style.marginBottom );
		this.paddingLeft = Integer.toString( style.paddingLeft );
		this.paddingRight = Integer.toString( style.paddingRight );
		this.paddingTop = Integer.toString( style.paddingTop );
		this.paddingBottom = Integer.toString( style.paddingBottom );
		this.paddingVertical = Integer.toString( style.paddingVertical );
		this.paddingHorizontal = Integer.toString( style.paddingHorizontal );
	}
	

	/**
	 * Updates the original UI style
	 */
	public void updateStyle() {
		this.style.marginLeft = parseInteger( this.marginLeft ).intValue();
		this.style.marginRight = parseInteger( this.marginRight ).intValue();
		this.style.marginTop = parseInteger( this.marginTop ).intValue();
		this.style.marginBottom = parseInteger( this.marginBottom ).intValue();
		
		this.style.paddingLeft = parseInteger( this.paddingLeft ).intValue();
		this.style.paddingRight = parseInteger( this.paddingRight ).intValue();
		this.style.paddingTop = parseInteger( this.paddingTop ).intValue();
		this.style.paddingBottom = parseInteger( this.paddingBottom ).intValue();
		this.style.paddingVertical = parseInteger( this.paddingVertical ).intValue();
		this.style.paddingHorizontal = parseInteger( this.paddingHorizontal ).intValue();
		
		if (this.layout != null) {
			this.style.layout = parseLayout( this.layout );
		}
		
	}



	/**
	 * @param layout2
	 * @return
	 */
	private int parseLayout(String layout2) {
		// TODO robertvirkus implement parseLayout
		return 0;
	}


	/**
	 * @return the styleAttributes
	 */
	public List<StyleAttribute> getStyleAttributes() {
	return this.styleAttributes;}
	



	/**
	 * @param styleAttributes the styleAttributes to set
	 */
	public void setStyleAttributes(List<StyleAttribute> styleAttributes) {
		this.styleAttributes = styleAttributes;
	}


	/**
	 * @param value
	 * @return
	 */
	private Integer parseInteger(String value) {
		try {
			return new Integer( Integer.parseInt(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 
	 */
	public ItemOrScreen getItemOrScreen() {
		return this.itemOrScreen;
	}


	/**
	 * @return
	 */
	public int getLayoutAsInt() {
		return this.style.layout;
	}


	/**
	 * @param layout2
	 */
	public void setLayout(int layout) {
		this.layout = null;
		this.style.layout = layout;
	}


	/**
	 * @param attributes
	 */
	public void removeAttributes(CssAttribute[] attributes) {
		for (int i = 0; i < attributes.length; i++) {
			CssAttribute attribute = attributes[i];
			if (attribute != null) {
				removeAttribute( attribute );
			}
		}
		
	}
	
	public void removeAttribute(CssAttribute attribute) {
		this.style.removeAttribute( attribute.getId() );
	}
	

}
