package de.enough.skylight.renderer.node;

import javax.microedition.lcdui.Font;

import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.skylight.dom.DomNode;

public class CssStyle {
	
	public static Style getTextStyle(Style style) {
		//#debug sl.debug.style
		System.out.println("creating text style from style " + style.name);
		
		Font font = style.getFont();
		Style textStyle = new Style();
		textStyle.addAttribute("font", font);
		return textStyle;
	}
	
	public static Style extendStyle(Style baseStyle, Style extendStyle) {
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
	
	static Style getStyle(NodeHandler handler, DomNode node) {
		Style style = handler.getDefaultStyle();
		
		//#debug sl.debug.style
		System.out.println("default style for " + handler.getTag() + " : " + style.name);
		
		String clazz = NodeUtils.getAttributeValue(node, "class");
		
		if(clazz != null) {
			clazz = clazz.toLowerCase();
			Style classStyle = StyleSheet.getStyle(clazz);
			
			if(classStyle != null) {
				style = classStyle;
				
				//#debug sl.debug.style
				System.out.println("got style for " + handler.getTag() + " : " + clazz);
			} else {
				//#debug error
				System.out.println("style " + clazz + " could not be found");
			} 
		}
		
		return style;
	}
}
