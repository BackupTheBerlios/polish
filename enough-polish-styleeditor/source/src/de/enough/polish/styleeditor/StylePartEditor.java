/*
 * Created on Apr 23, 2007 at 3:55:26 PM.
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

import de.enough.polish.Environment;
import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.styleeditor.editors.ParameterizedStyleEditor;
import de.enough.polish.ui.Style;

/**
 * <p>An editor responsible for editing a specific part of a style.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class StylePartEditor {

	protected StyleEditor styleEditor;
	protected StylePartEditorVisual visual;
	protected EditStyle editStyle;
	private StylePartEditor parent;
	protected String name;

	public StylePartEditor() {
		// creates new instance
	}
	
	public void setStyleEditor( StyleEditor styleEditor ) {
		this.styleEditor = styleEditor;		
	}
	
	
	public void setParent(StylePartEditor editor) {
		this.parent = editor;
	}
	
	protected void update() {
		writeStyle(this.editStyle);
		if (this.parent != null) {
			//System.out.println(getClass().getName() + " calling update on parent " + this.parent);
			this.parent.update();
		} else if (this.styleEditor != null) {
			//System.out.println(getClass().getName() + " calling update on styleEditor " + this.styleEditor);
			this.styleEditor.notifyStyleUpdated();
		}
	}
	
	public abstract String createName();
	
	public abstract void writeStyle( EditStyle style );
	
	public abstract void readStyle( EditStyle style );
	

	/**
	 * @param partVisual
	 */
	public void setVisual(StylePartEditorVisual partVisual) {
		this.visual = partVisual;
		partVisual.setPartEditor(  this );
	}

	/**
	 * @param style
	 */
	public void setStyle(EditStyle style) {
		this.editStyle = style;
		readStyle(style);
		if (this.visual != null) {
			this.visual.notifyStyleUpdated();
		}
	}

	/**
	 * @return the name of this editor
	 */
	public String getName() {
		if (this.name == null) {
			this.name = createName();
		}
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name; 
	}

	
	
	public CssAttribute getAttribute( String name ) {
		return this.styleEditor.getAttribute( name );
	}
	
	public CssAttribute[] getApplicableAttributes( Class targetClass ) {
		return this.styleEditor.getApplicableAttributes( targetClass );
	}

	
	public Environment getEnvironment() {
		return this.styleEditor.getEnvironment();
	}
	
	public ResourcesProvider getResourcesProvider() {
		return this.styleEditor.getResourcesProvider();
	}

	
}
