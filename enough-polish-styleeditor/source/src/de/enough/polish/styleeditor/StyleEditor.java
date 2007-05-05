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
import de.enough.polish.ui.Style;

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
	private final StyleEditorVisual visual;
	private EditStyle style;
	private List<StyleEditorListener> styleListeners;
	private Environment environment;
	private ResourcesProvider resourcesProvider;

	/**
	 * Creates a new StyleEditor.
	 * 
	 * @param attributesManager
	 * @param environment the environment
	 * @param visual the visualization, can be null
	 */
	public StyleEditor( ResourcesProvider resourcesProvider, CssAttributesManager attributesManager, Environment environment, StyleEditorVisual visual  ) {
		this.resourcesProvider = resourcesProvider;
		this.attributesManager = attributesManager;
		this.visual = visual;
		this.editors = new ArrayList<StylePartEditor>();
		this.environment = environment;
		if (environment.get( ColorConverter.ENVIRONMENT_KEY ) == null) {
			System.out.println("warning: no color converter found.");
			environment.set( ColorConverter.ENVIRONMENT_KEY, new ColorConverter() );
		}
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
	public void setStyle( EditStyle style ) {
		this.style = style;
		if (this.visual != null) {
			this.visual.setStyle( style );
		}
		for (StylePartEditor editor : this.editors) {
			editor.setStyle( style );
		}
	}

	/**
	 * 
	 */
	public void notifyStyleUpdated() {
		this.style.writeStyle();
		if (this.styleListeners != null) {
			for (StyleEditorListener listener : this.styleListeners) {
				listener.notifyStyleUpdated(this.style);
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

}
