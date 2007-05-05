/*
 * Created on Apr 23, 2007 at 10:30:10 PM.
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
package de.enough.polish.styleeditor.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.styleeditor.StylePartEditorVisual;
import de.enough.polish.styleeditor.editors.CssAttributeEditor;
import de.enough.polish.styleeditor.swing.editors.SwingCssAttributeEditor;
import de.enough.polish.util.StringUtil;

/**
 * <p>Baseclass for </p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class SwingStylePartEditor 
extends JPanel 
implements StylePartEditorVisual, ActionListener 
{
	private int index;
	protected static final String NO_SELECTION = "<none>";
	
	protected boolean isIgnoreActionEvents;
	protected StylePartEditor partEditor;
	protected final JPanel attributesPanel;

	protected SwingStyleEditor swingStyleEditor;
	
	protected SwingStylePartEditor() {
		super( new BorderLayout() );
		
		this.attributesPanel = new JPanel( new BorderLayout() );
	}

	
	protected abstract void handleActionPerformed(  StylePartEditor stylePartEditor, ActionEvent event );
	protected abstract void updateGui( StylePartEditor stylePartEditor );


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditorVisual#setStylePartEditor(de.enough.polish.styleeditor.StylePartEditor)
	 */
	public void setPartEditor(StylePartEditor partEditor) {
		this.partEditor = partEditor;
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {
		if (!this.isIgnoreActionEvents) {
			this.isIgnoreActionEvents = true;
			handleActionPerformed(this.partEditor, event);
			this.isIgnoreActionEvents = false;
		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditorVisual#notifyStyleUpdated()
	 */
	public void notifyStyleUpdated() {
		this.isIgnoreActionEvents = true;
		updateGui( this.partEditor );
		this.isIgnoreActionEvents = false;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditorVisual#setCssAttributes(de.enough.polish.preprocess.css.CssAttribute[], de.enough.polish.styleeditor.editors.CssAttributeEditor[])
	 */
	public void setCssAttributes(CssAttribute[] attributes, CssAttributeEditor[] editors) {
		//System.out.println("SwingStylePartEditor: adding " + (editors == null ? "null" : editors.length) + " editors with " + (attributes == null ? "null" : editors.length) + " attributes to " + this);
		this.attributesPanel.removeAll();
		if (attributes != null) {
			JPanel panel = new JPanel( new GridLayout( editors.length, 2, 5, 2 ) );
			for (int i = 0; i < editors.length; i++) {
				CssAttributeEditor attributeEditor = editors[i];
				if (attributeEditor == null) {
					continue;
				}
				String className = attributeEditor.getClass().getName();
				className = StringUtil.replace(className, "de.enough.polish.styleeditor.", "de.enough.polish.styleeditor.swing.");
				int lastDotPos = className.lastIndexOf('.');
				className = className.substring(0, lastDotPos+1) + "Swing" + className.substring(lastDotPos+1);
				try {
					SwingCssAttributeEditor swingEditor = (SwingCssAttributeEditor) Class.forName(className).newInstance();
					//swingEditor.setPartEditor(attributeEditor); this is done implicetely in the next line:
					attributeEditor.setVisual(swingEditor);
					swingEditor.notifyStyleUpdated();
					panel.add( swingEditor );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			this.attributesPanel.add(panel, BorderLayout.NORTH );
		}
		SwingUtilities.updateComponentTreeUI(this);
	}


	/**
	 * @param editor
	 */
	public void setParent(SwingStyleEditor editor) {
		this.swingStyleEditor = editor;
	}


	/**
	 * @return the index
	 */
	public int getIndex() {
		return this.index;
	}
	


	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	
	
}
