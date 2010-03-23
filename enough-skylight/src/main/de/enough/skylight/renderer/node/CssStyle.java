package de.enough.skylight.renderer.node;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.skylight.dom.DomNode;

public class CssStyle {
	
	public static Style getTextStyle(Style baseStyle, Style extendStyle) {
		//#style text
		Style resultStyle = new Style();
		
		// get default size and color from text style
		Integer defaultFontSize = resultStyle.getIntProperty("font-size");
		Color defaultFontColor = resultStyle.getColorProperty("font-color");
		
		// get the attributes to extend the style 
		Color extendFontColor = extendStyle.getColorProperty("font-color");
		Integer extendFontStyle = extendStyle.getIntProperty("font-style");
		Integer extendFontSize = extendStyle.getIntProperty("font-size"); 
		Style extendFocusedStyle = (Style)extendStyle.getObjectProperty("focused-style");
		
		// if a base style is given ...
		if(baseStyle != null) {
			// get font attributes from the base style
			Color baseFontColor = baseStyle.getColorProperty("font-color");
			Integer baseFontStyle = baseStyle.getIntProperty("font-style");
			Integer baseFontSize = baseStyle.getIntProperty("font-size");
			
			Color resultTextColor = defaultFontColor;
			// if the color to extend is not the default color ...
			if(!extendFontColor.equals(defaultFontColor)) {
				// set it as the result color
				resultTextColor = extendFontColor;
			} else {
				// set the base color as the result color
				resultTextColor = baseFontColor;
			}
			
			// extend the flags
			Integer resultTextStyle = extendFlag(baseFontStyle, extendFontStyle);
			
			// if the size to extend is not the default size ...
			Integer resultTextSize = defaultFontSize;
			if(!extendFontSize.equals(defaultFontSize)) {
				// set it as the result size
				resultTextSize = extendFontSize;
			} else {
				// set the base size as the result size
				resultTextSize = baseFontSize;
			}
			
			Style baseFocusedStyle = (Style)baseStyle.getObjectProperty("focused-style");
			
			// extend the focused style
			Style resultFocusedStyle = extendStyle(baseFocusedStyle,extendFocusedStyle);
			
			// add the resulting attributes to the text style
			resultStyle.addAttribute("font-color", resultTextColor);
			resultStyle.addAttribute("font-style", resultTextStyle);
			resultStyle.addAttribute("font-size", resultTextSize);
			resultStyle.addAttribute("focused-style", resultFocusedStyle);
		} else {
			resultStyle.addAttribute("font-color", extendFontColor);
			resultStyle.addAttribute("font-style", extendFontStyle);
			resultStyle.addAttribute("font-size", extendFontSize);	
			resultStyle.addAttribute("focused-style", extendFocusedStyle);
		}
		
		return resultStyle;
	}
	
	static Integer extendFlag(Integer baseFlag, Integer extendFlag ) {
		return new Integer( baseFlag.intValue() | extendFlag.intValue() );
	}
	
	static Style extendStyle(Style baseStyle, Style extendStyle) {
		//#debug sl.debug.style
		System.out.println("extending style " + baseStyle.name + " with style " + extendStyle.name);
		
		Style result = new Style(baseStyle);
		
		result.name = extendStyle.name;
		
		if(extendStyle.layout != Item.LAYOUT_DEFAULT) {
			result.layout = extendStyle.layout;
		}
		
		if(extendStyle.border != null) {
				result.border = extendStyle.border;
		}
				
		if(extendStyle.background != null) {
			result.background = extendStyle.background;
		}
		
		short[] keys = extendStyle.getRawAttributeKeys();
		
		if(keys != null) {
			for (int i = 0; i < keys.length; i++) {
				short key = keys[i];
				Object value = extendStyle.getObjectProperty(key);
				if(value != null) {
					result.addAttribute(key, value);
				}
			}
		}
		
		return result;
	}
	
	static Style getStyle(CssElement element) {
		NodeHandler handler = element.getHandler();
		DomNode node = element.getNode();
		
		String clazz = NodeUtils.getAttributeValue(node, "class");
		
		Style style = handler.getDefaultStyle(element);
		
		if(clazz != null) {
			clazz = clazz.toLowerCase();
			Style classStyle = StyleSheet.getStyle(clazz);
			 
			if(classStyle != null) {
				//#debug sl.debug.style
				System.out.println("create style for " + handler.getTag() + " : " + clazz);
				
				return extendStyle(style, classStyle);
			} else {
				//#debug error
				System.out.println("style " + clazz + " could not be found");
			} 
		}
		
		//#debug sl.debug.style
		System.out.println("returning default style for " + handler.getTag() + " : " + style.name);
		
		return style;
	}
	
	public static void apply(Style style, Item item) {
		item.setStyle(style);
		
		if(item.isFocused) {
			Style focusedStyle = item.getFocusedStyle();
			if(focusedStyle != null) {
				item.setStyle(focusedStyle);
			}
		} 
	}
}
