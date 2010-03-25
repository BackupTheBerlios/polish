package de.enough.skylight.renderer.node;

import de.enough.polish.ui.Color;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.skylight.dom.DomNode;

public class CssStyle {
	
	static final short FOCUSED_STYLE_KEY = 1;
	
	public static Style createTextStyle(Style baseStyle, Style extendStyle) {
		if(baseStyle != null && extendStyle != null) {
			if(baseStyle.name.equals(extendStyle.name)) {
				return baseStyle;
			}
		}
		
		//#mdebug sl.debug.style
		if(baseStyle != null) {
			System.out.println("creating text style : " + baseStyle.name + " / " + extendStyle.name);
		} else {
			System.out.println("creating text style from " + extendStyle.name);
		}
		//#enddebug
		
		//#style text
		Style resultStyle = new Style();
		
		// get the attributes to extend the style 
		Color extendFontColor = extendStyle.getColorProperty("font-color");
		Integer extendFontStyle = extendStyle.getIntProperty("font-style");
		Integer extendFontSize = extendStyle.getIntProperty("font-size"); 
		Style extendFocusedStyle = (Style)extendStyle.getObjectProperty("focused-style");
		
		// if a base style is given ...
		if(baseStyle == null) {
			resultStyle.name = extendStyle.name;
			resultStyle.addAttribute("font-color", extendFontColor);
			resultStyle.addAttribute("font-style", extendFontStyle);
			resultStyle.addAttribute("font-size", extendFontSize);	
			resultStyle.addAttribute("focused-style", extendFocusedStyle);
		} else {
			// get default size and color from text style
			Integer defaultFontSize = resultStyle.getIntProperty("font-size");
			Color defaultFontColor = resultStyle.getColorProperty("font-color");
			
			// get font attributes from the base style
			Color baseFontColor = baseStyle.getColorProperty("font-color");
			Integer baseFontStyle = baseStyle.getIntProperty("font-style");
			Integer baseFontSize = baseStyle.getIntProperty("font-size");
			
			Color resultTextColor = (Color) extendValue(baseFontColor, extendFontColor, defaultFontColor);
			Integer resultTextStyle = extendFlag(baseFontStyle, extendFontStyle);
			Integer resultTextSize = (Integer) extendValue(baseFontSize, extendFontSize, defaultFontSize);
			
			Style baseFocusedStyle = (Style)baseStyle.getObjectProperty("focused-style");
			
			// add the resulting attributes to the text style
			resultStyle.name = baseStyle.name + "." + extendStyle.name;
			resultStyle.addAttribute("font-color", resultTextColor);
			resultStyle.addAttribute("font-style", resultTextStyle);
			resultStyle.addAttribute("font-size", resultTextSize);
			
			// extend the focused style
			if(extendFocusedStyle != null) {
				Style resultFocusedStyle = createTextStyle(baseFocusedStyle,extendFocusedStyle);
				resultStyle.addAttribute("focused-style", resultFocusedStyle);
			} 
		}
		
		return resultStyle;
	}
	
	static Integer extendFlag(Integer baseFlag, Integer extendFlag ) {
		if(extendFlag == null) {
			return baseFlag;
		} else {
			// extend the flags
			return new Integer( baseFlag.intValue() | extendFlag.intValue() );
		}
	}
	
	static Object extendValue(Object baseValue, Object extendValue, Object defaultValue) {
		// if the value to extend is not the default value ...
		Object resultValue = defaultValue;
		if(extendValue != null && !extendValue.equals(defaultValue)) {
			// set it as the result value
			resultValue = extendValue;
		} else {
			// set the base value as the result value
			resultValue = baseValue;
		}
		
		return resultValue;
	}
	
	static Style createStyle(Style baseStyle, Style extendStyle) {
		if(baseStyle != null && extendStyle != null) {
			if(baseStyle.name.equals(extendStyle.name)) {
				return baseStyle;
			}
		}
		
		//#debug sl.debug.style
		System.out.println("extending style " + baseStyle.name + " with style " + extendStyle.name);
		
		Style result = new Style(baseStyle);
		
		result.name = baseStyle.name + "." + extendStyle.name;
		
		if(extendStyle.layout != Item.LAYOUT_DEFAULT) {
			result.layout = extendStyle.layout;
		}
		
		if(extendStyle.border != null) {
			result.border = extendStyle.border;
		}
				
		if(extendStyle.background != null) {
			result.background = extendStyle.background;
		}
		
		Style baseFocusedStyle = (Style) baseStyle.getObjectProperty("focused-style");
		short[] keys = extendStyle.getRawAttributeKeys();
		
		if(keys != null) {
			for (int i = 0; i < keys.length; i++) {
				short key = keys[i];
				Object value = extendStyle.getObjectProperty(key);
				
				//TODO unstable
//				if(key == FOCUSED_STYLE_KEY && baseFocusedStyle != null && value != null) {
//					Style resultFocusedStyle = createStyle(baseFocusedStyle, (Style)value);
//					result.addAttribute(key, resultFocusedStyle);
//				} else 
				{
					if(value != null) {
						result.addAttribute(key, value);
					}
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
				
				return createStyle(style, classStyle);
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
