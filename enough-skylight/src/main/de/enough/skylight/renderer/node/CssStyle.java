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
		
		Font resultFont = baseStyle.getFont();
		
		/*Font baseFont = baseStyle.getFont();
		Font extendFont = extendStyle.getFont();
		
		int baseFontStyle = baseFont.getStyle();
		int extendFontStyle = extendFont.getStyle();
		
		int resultFontStyle = extendFlag(baseFontStyle, extendFontStyle);
		
		resultFont = Font.getFont(0, 0, resultFontStyle);*/
		
		Style textStyle = new Style();
		textStyle.addAttribute("font", resultFont);
		
		return textStyle;
	}
	
	static int extendFlag(int baseFlag, int extendFlag ) {
		return baseFlag | extendFlag;
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
