package de.enough.skylight.renderer.node;

import javax.microedition.lcdui.Font;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.skylight.dom.DomNode;

public class CssStyle {
	
	public static Style getTextStyle(Style baseStyle, Style extendStyle) {
		//#debug sl.debug.style
		System.out.println("creating text style from style " + baseStyle.name);
		
		Font font = baseStyle.getFont();
		Style textStyle = new Style();
		textStyle.addAttribute("font", font);
		
		return textStyle;
	}
	
	public static Integer extendFlag(Integer baseFlag, Integer extendFlag ) {
		if(baseFlag != null && extendFlag != null) {
			return new Integer(baseFlag.intValue() | extendFlag.intValue());
		} else {
			return null;
		}
	}
	
	public static Style extendStyle(Style baseStyle, Style extendStyle) {
		//#debug sl.debug.style
		System.out.println("extending style " + baseStyle.name + " with style " + extendStyle.name);
		
		Style result = new Style(baseStyle);
		
		if(extendStyle.layout != Item.LAYOUT_DEFAULT) {
			result.layout = extendStyle.layout;
		}
		
		if(extendStyle.border != null) {
				result.border = extendStyle.border;
		}
				
		if(extendStyle.background != null) {
			result.background = extendStyle.background;
		}
		
//		Integer baseFontStyle = baseStyle.getIntProperty("font-style");
//		Integer extendFontStyle = baseStyle.getIntProperty("font-style");
//		Integer fontStyle = extendStyleFlag(baseFontStyle, extendFontStyle);
//		if(fontStyle != null) {
//			result.addAttribute("font-style", fontStyle);
//		}
		
		short[] keys = extendStyle.getRawAttributeKeys();
		
		for (int i = 0; i < keys.length; i++) {
			short key = keys[i];
			Object value = extendStyle.getObjectProperty(key);
			if(value != null) {
				result.addAttribute(key, value);
			}
		}
		
		return result;
	}
	
	static Style getStyle(CssElement element) {
		NodeHandler handler = element.getHandler();
		DomNode node = element.getNode();
		
		String clazz = NodeUtils.getAttributeValue(node, "class");
		
		if(clazz != null) {
			clazz = clazz.toLowerCase();
			Style classStyle = StyleSheet.getStyle(clazz);
			
			if(classStyle != null) {
				//#debug sl.debug.style
				System.out.println("create style for " + handler.getTag() + " : " + clazz);
				
				return classStyle;
			} else {
				//#debug error
				System.out.println("style " + clazz + " could not be found");
			} 
		}
		
		Style defaultStyle = handler.getDefaultStyle(element);
		//#debug sl.debug.style
		System.out.println("returning default style for " + handler.getTag() + " : " + defaultStyle.name);
		
		return defaultStyle;
	}
}
