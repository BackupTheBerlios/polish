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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssMapping;
import de.enough.polish.resources.StyleProvider;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.Border;
import de.enough.polish.ui.Style;
import de.enough.polish.util.ReflectionUtil;

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
public class EditStyle
implements StyleProvider
{
	
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
	private Map<CssAttribute, CssAttributeValue> attributesMap;
	private String name;
	private CssAttributeValue background;
	private CssAttributeValue[] backgroundValues;
	private CssAttributeValue border;
	private CssAttributeValue[] borderValues;
	private String fontFace;
	
	/**
	 * Creates a new edit style with an empty style and no item/screen
	 */
	public EditStyle() {
		this( "unknown", new Style(), null );
	}

	public EditStyle( String name, Style style, ItemOrScreen itemOrScreen ) {
		this.name = name;
		this.itemOrScreen = itemOrScreen;
		this.attributesMap = new HashMap<CssAttribute, CssAttributeValue>();
		readStyle( style );
	}
	

	/**
	 * @return the fontColor
	 */
	public String getFontColor() {
	return this.fontColor;}
	

	/**
	 * @param fontFaceAsString
	 */
	public void setFontFace(String fontFaceAsString) {
		this.fontFace = fontFaceAsString;
	}


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
		return this.layout;
	}
	



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
	public void readStyle(Style style) {
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
		//todo: set background, border, font for code generation
	}
	

	/**
	 * Updates the original UI style
	 */
	public void writeStyle() {
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
			//this.style.layout = parseLayout( this.layout );
		}
		
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

	/**
	 * @param value
	 */
	public void addAttribute(CssAttributeValue value) {
		this.style.addAttribute( value.getAttribute().getId(), value.getValue() );
		this.attributesMap.put( value.getAttribute(), value );
	}
	
	public String toSourceCode() {
		StringBuffer buffer = new StringBuffer();
		appendToSource(buffer);
		return buffer.toString();
	}
	
	public void appendToSource( StringBuffer buffer) {
		CssAttribute[] attributes = this.attributesMap.keySet().toArray(new CssAttribute[this.attributesMap.size()]);
		Arrays.sort( attributes );
		buffer.append(this.name).append(" {\n");
		for (int i = 0; i < attributes.length; i++) {
			CssAttribute attribute = attributes[i];
			CssAttributeValue value = this.attributesMap.get(attribute);
			CssMapping mapping = attribute.getMapping( value.getValueAsString() );
			if (mapping != null && mapping.getCondition() != null) {
				buffer.append("\t#if ").append(mapping.getCondition()).append("\n");
				buffer.append("\t\t").append(attribute.getName()).append(": ").append( value.getValueAsString() ).append(";\n");
				buffer.append("\t#endif\n");
			} else {
				// attribute without condition:
				buffer.append("\t").append(attribute.getName()).append(": ").append( value.getValueAsString() ).append(";\n");
			}
		}
		if (this.layout != null) {
			buffer.append("\tlayout: ").append( this.layout ).append(";\n");			
		}
		if ( equals( this.style.marginLeft, this.style.marginRight, this.style.marginTop, this.style.marginBottom)) {
			buffer.append("\tmargin: ").append( this.style.marginLeft ).append(";\n");
		} else {
			buffer.append("\tmargin-left: ").append( this.marginLeft ).append(";\n");
			buffer.append("\tmargin-right: ").append( this.marginRight ).append(";\n");
			buffer.append("\tmargin-top: ").append( this.marginTop ).append(";\n");
			buffer.append("\tmargin-bottom: ").append( this.marginBottom ).append(";\n");
		}
		if ( equals( this.style.paddingLeft, this.style.paddingRight, this.style.paddingTop, this.style.paddingBottom)) {
			buffer.append("\tpadding: ").append( this.style.paddingLeft ).append(";\n");
		} else {
			buffer.append("\tpadding-left: ").append( this.paddingLeft ).append(";\n");
			buffer.append("\tpadding-right: ").append( this.paddingRight ).append(";\n");
			buffer.append("\tpadding-top: ").append( this.paddingTop ).append(";\n");
			buffer.append("\tpadding-bottom: ").append( this.paddingBottom ).append(";\n");
			buffer.append("\tpadding-horizontal: ").append( this.paddingHorizontal ).append(";\n");
			buffer.append("\tpadding-vertical: ").append( this.paddingVertical ).append(";\n");
		}
		if (this.fontFace != null) {
			buffer.append("\tfont {\n");
			buffer.append("\t\tface: ").append( this.fontFace ).append(";\n");
			if (this.fontSize != null) {
				buffer.append("\t\tsize: ").append( this.fontSize ).append(";\n");
			}
			if (this.fontStyle != null) {
				buffer.append("\t\tstyle: ").append( this.fontStyle ).append(";\n");
			}
			if (this.fontColor != null) {
				buffer.append("\t\tcolor: ").append( this.fontColor ).append(";\n");
			}
			buffer.append("\t}\n");
		}
		if (this.background != null) {
			appendGroup( this.background, this.backgroundValues, buffer );			
		}
		if (this.border != null) {
			appendGroup( this.border, this.borderValues, buffer );						
		}
		buffer.append("}\n");
	}

	
	private boolean equals(int num1, int num2, int num3, int num4) {
		return (num1 == num2 && num2 == num3 && num3 == num4 );
	}

	private boolean equals(int num1, int num2, int num3, int num4, int num5, int num6) {
		return (num1 == num2 && num2 == num3 && num3 == num4 && num4 == num5 && num5 == num6 );
	}

	/**
	 * @param background2
	 * @param backgroundValues2
	 * @param buffer
	 */
	private void appendGroup(CssAttributeValue group, CssAttributeValue[] groupValues, StringBuffer buffer) {
		buffer.append("\t").append( group.getAttribute().getName()).append(" {\n");
		buffer.append( "\t\ttype: ").append( group.getValueAsString() ).append(";\n");
		if (groupValues != null) {
			for (int i = 0; i < groupValues.length; i++) {
				CssAttributeValue value = groupValues[i];
				buffer.append("\t\t").append( value.getAttribute().getName() ).append(": ").append( value.getValueAsString() ).append(";\n");
			}
		}
		buffer.append("\t}\n");
	}

	/**
	 * @param value
	 * @param bgValues 
	 */
	public void setBackground(CssAttributeValue value, CssAttributeValue[] bgValues) {
		this.background = value;
		this.backgroundValues = bgValues;
		if (value == null) {
			this.style.background = null;
		} else {
			this.style.background = (Background) value.getValue();
		}
	}

	/**
	 * 
	 */
	public void removeBackground() {
		this.background = null;
		this.style.background = null;
	}

	/**
	 * @param value
	 * @param bordValues
	 */
	public void setBorder(CssAttributeValue value, CssAttributeValue[] bordValues) {
		this.border = value;
		this.borderValues = bordValues;
		if (value == null) {
			this.style.border = null;
		} else {
			this.style.border = (Border) value.getValue();
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.StyleProvider#generateSourceCode(java.lang.StringBuffer)
	 */
	public void generateCssSourceCode(StringBuffer buffer) {
		appendToSource(buffer);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.StyleProvider#getName()
	 */
	public String getName() {
		return this.name;
	}
	
	public String toString() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof StyleProvider) {
			//return this.style.equals(  ((StyleProvider) obj).getStyle() );
			return this.name.equalsIgnoreCase(  ((StyleProvider) obj).getName() );
		}
		return super.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.style.hashCode();
	}

	/**
	 * @param itemOrScreen
	 */
	public void setItemOrScreen(ItemOrScreen itemOrScreen) {
		this.itemOrScreen = itemOrScreen;
	}

	/**
	 * 
	 */
	public void removeAllAttributes() {
		this.attributesMap.clear();
		if (this.style != null) {
			try {
				ReflectionUtil.setField( this.style, "attributeKeys", null);
				ReflectionUtil.setField( this.style, "attributeValues", null);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("...ooops: unable to clear attributeKeys/attributeValues from style: "  + e );
			}
		}
	}
	
	

}
