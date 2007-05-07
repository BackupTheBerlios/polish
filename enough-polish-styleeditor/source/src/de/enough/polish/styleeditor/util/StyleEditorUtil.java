/*
 * Created on May 7, 2007 at 4:50:55 PM.
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
package de.enough.polish.styleeditor.util;

import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.resources.StyleProvider;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.util.ReflectionUtil;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        May 7, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class StyleEditorUtil {
	
	private StyleEditorUtil() {
		// disallow instantiation
	}
	
	public static final void setDefaultStyles( ResourcesProvider resourcesProvider ) {
		StyleProvider style = resourcesProvider.getStyle( "focused" );
		if (style != null) {
			StyleSheet.focusedStyle = style.getStyle();
		}
		style = resourcesProvider.getStyle( "label" );
		if (style != null) {
			StyleSheet.labelStyle = style.getStyle();
		}
		style = resourcesProvider.getStyle( "default" );
		if (style != null) {
			try {
				Style defaultStyle = style.getStyle();
				ReflectionUtil.setStaticField( StyleSheet.class, "defaultStyle", style);
				if (defaultStyle.font != null) {
					ReflectionUtil.setStaticField( StyleSheet.class, "defaultFont", defaultStyle.font);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static final void setDefaultStyles( Screen screen, ResourcesProvider resourcesProvider ) {
		setStyle( screen, resourcesProvider, "title", "title");
		try {
			Item menubar = (Item) ReflectionUtil.getFieldValue(screen, "menuBar");
			if (menubar != null) {
				StyleProvider style = resourcesProvider.getStyle("menubar");
				if (style != null) {
					menubar.setStyle( style.getStyle() );
				}
				setStyle( menubar, resourcesProvider, "leftcommand", "singleLeftCommandItem" );
				setStyle( menubar, resourcesProvider, "rightcommand", "singleRightCommandItem" );
			}
		} catch (Exception e) {
			System.out.println("Warning: unable to set menubar or command style: " + e.toString());
		}	
	}
	
	public static final void setDefaultStyles( Item item, ResourcesProvider resourcesProvider ) {
		setStyle( item, resourcesProvider, "label", "label");
	}


	private static void setStyle(Object object, ResourcesProvider resourcesProvider, String styleName, String fieldName) {
		StyleProvider style = resourcesProvider.getStyle( styleName );
		if (style != null) {
			try {
				Item item = (Item) ReflectionUtil.getFieldValue(object, fieldName);
				if (item != null) {
					item.setStyle( style.getStyle() );
				}
			} catch (Exception e) {
				System.out.println("Warning: unable to set " + styleName + " style: " + e.toString());
			}	
		}
	}
	

}
