/*
 * Created on Apr 23, 2007 at 3:50:43 PM.
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

import java.util.ArrayList;
import java.util.List;

import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.ColorConverter;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.preprocess.css.CssAttributesManager;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.styleeditor.editors.BackgroundEditor;
import de.enough.polish.styleeditor.editors.BorderEditor;
import de.enough.polish.styleeditor.editors.LayoutEditor;
import de.enough.polish.styleeditor.editors.MarginPaddingEditor;
import de.enough.polish.styleeditor.editors.SpecificAttributesEditor;
import de.enough.polish.styleeditor.editors.TextEditor;
import de.enough.polish.styleeditor.editors.ViewTypeEditor;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;

/**
 * <p>Baseclass for editing a single style.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class StyleEditor {
	
	private final CssAttributesManager attributesManager;
	private final List<StylePartEditor> editors;
	private StyleEditorVisual visual;
	private EditStyle currentEditStyle;
	private List<StyleEditorListener> styleListeners;
	private Environment environment;
	private ResourcesProvider resourcesProvider;

	/**
	 * Creates a new StyleEditor.
	 * 
	 * @param attributesManager
	 * @param environment the environment
	 */
	public StyleEditor( ResourcesProvider resourcesProvider, CssAttributesManager attributesManager, Environment environment ) {
		this.resourcesProvider = resourcesProvider;
		this.attributesManager = attributesManager;
		this.editors = new ArrayList<StylePartEditor>();
		this.environment = environment;
		if (environment.get( ColorConverter.ENVIRONMENT_KEY ) == null) {
			System.out.println("warning: no color converter found.");
			environment.set( ColorConverter.ENVIRONMENT_KEY, new ColorConverter() );
		}
	}
	
	public void setVisual( StyleEditorVisual visual ) {
		this.visual = visual;
	}
	
	public void addPartEditor( StylePartEditor partEditor ) {
		partEditor.setStyleEditor(this);
		if (this.visual != null) {
			StylePartEditorVisual partVisual = this.visual.getPartEditorVisual(partEditor);
			partEditor.setVisual( partVisual );
		}
		this.editors.add(partEditor);
	}
	
	public void addStyleListener ( StyleEditorListener listener ) {
		if (this.styleListeners == null) {
			this.styleListeners = new ArrayList<StyleEditorListener>();
		}
		this.styleListeners.add(listener);
	}
	
	/**
	 * Edits the specified style
	 * 
	 * @param style the style that should be added
	 */
	public void editStyle( EditStyle style ) {
		this.currentEditStyle = style;
		if (this.visual != null) {
			this.visual.editStyle( style );
		}
		for (StylePartEditor editor : this.editors) {
			editor.setStyle( style );
		}
	}
	
	/**
	 * Edits the specified style
	 * 
	 * @param itemOrScreen the item/screen that might contain a style that should be added
	 * @param style the style that belongs to the item/screen. When null the user is asked for specifying that style
	 * @return the edited style if there is a style attached, otherwise null is returned
	 */
	public EditStyle editStyle( ItemOrScreen itemOrScreen, Style style ) {
		EditStyle editStyle = null;
		if (style != null) {
			editStyle = (EditStyle) this.resourcesProvider.getStyle(style.name); 
		}
		
		if (editStyle == null) {
			// notify visual about a necessary style selection dialog:
			if (this.visual != null) {
				this.visual.chooseOrCreateStyleFor( itemOrScreen, this.resourcesProvider.getStyles() );
			}
			return null;
		} else {
			// okay, we have style to edit:
			editStyle.setItemOrScreen(itemOrScreen);
			editStyle( editStyle );
			return editStyle;
		}
	}
	
	public EditStyle attachStyle( ItemOrScreen itemOrScreen, String styleName ) { 
		EditStyle editStyle = (EditStyle) this.resourcesProvider.getStyle( styleName );
		if (editStyle == null) {
			Style style = new Style();
			style.name = styleName;
			editStyle = new EditStyle( styleName, style, itemOrScreen);
			this.resourcesProvider.addStyle(styleName, editStyle);
		} else {
			editStyle.setItemOrScreen(itemOrScreen);
		}
		Object itemOrScreenObj = itemOrScreen.getItemOrScreen();
	    if (itemOrScreenObj instanceof Item) {
	        UiAccess.setStyle( (Item)itemOrScreenObj, editStyle.getStyle() );
	    } else {
	        UiAccess.setStyle( (Screen)itemOrScreenObj, editStyle.getStyle() );
	    }

		if (this.styleListeners != null) {
			for (StyleEditorListener listener : this.styleListeners) {
				listener.notifyStyleAttached(itemOrScreen, editStyle);
			}
		}
		editStyle( editStyle );
		return editStyle;
	}

	/**
	 * 
	 */
	public void notifyStyleUpdated() {
		this.currentEditStyle.writeStyle();
		if (this.styleListeners != null) {
			for (StyleEditorListener listener : this.styleListeners) {
				listener.notifyStyleUpdated(this.currentEditStyle);
			}
		}
	}

	/**
	 * 
	 */
	public void addDefaultPartEditors() {
		addPartEditor( new MarginPaddingEditor() );
		addPartEditor( new LayoutEditor() );
		addPartEditor( new TextEditor() );
		addPartEditor( new BackgroundEditor() );
		addPartEditor( new BorderEditor() );
		addPartEditor( new ViewTypeEditor() );
		addPartEditor( new SpecificAttributesEditor() );
	}

	/**
	 * @param name
	 * @return
	 */
	public CssAttribute getAttribute( String name ) {
		if (this.attributesManager == null) {
			return null;
		}
		return this.attributesManager.getAttribute( name );
	}
	
	public CssAttribute[] getApplicableAttributes( Class targetClass ) {
		if (this.attributesManager == null) {
			return new CssAttribute[0];
		}
		return this.attributesManager.getApplicableAttributes( targetClass );
	}

	/**
	 * @return
	 */
	public Environment getEnvironment() {
		return this.environment;
	}

	/**
	 * @return
	 */
	public ResourcesProvider getResourcesProvider() {
		return this.resourcesProvider;
	}

	/**
	 * @param itemOrScreen
	 * @return
	 */
	public String getChooseOrCreateDescription(ItemOrScreen itemOrScreen) {
		Object itemOrScreenObj = itemOrScreen.getItemOrScreen();
        return itemOrScreenObj instanceof Item ? 
                "This item has no attached style, please enter the desired name: "
                :
                "This screen has no attached style, please enter the desired name: ";
	}

	/**
	 * @param itemOrScreen
	 * @return
	 */
	public String getChooseOrCreateSuggestion(ItemOrScreen itemOrScreen) {
		Object itemOrScreenObj = itemOrScreen.getItemOrScreen();
		if (itemOrScreenObj instanceof Item && ((Item)itemOrScreenObj).isFocused) {
			return "focused";
		}
        String defaultName = itemOrScreenObj.getClass().getName();
        if (defaultName.lastIndexOf('.' ) != -1) {
            defaultName = defaultName.substring( defaultName.lastIndexOf('.' ) + 1 );
        }
        return "my" + defaultName;
	}

}
