/*
 * Created on May 4, 2007 at 1:39:37 AM.
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
package de.enough.polish.resources.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import de.enough.polish.resources.ColorProvider;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.resources.StyleProvider;
import de.enough.polish.resources.swing.ResourcesTreeModel.ResourceTreeNode;

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        May 4, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourcesTree 
extends JTree
implements TreeSelectionListener
{
	private boolean ignoreEvents;
	List<StyleSelectionListener> styleSelectionListeners;
	List<ColorSelectionListener> colorSelectionListeners;
	private final ResourcesTreeModel resourcesTreeModel;
	

	protected ResourcesTree( ResourcesTreeModel model ) {
		super( model );
		this.resourcesTreeModel = model;
		addTreeSelectionListener(this);
	}
	
	public static ResourcesTree getInstance( ResourcesProvider resourcesProvider ) {
		ResourcesTreeModel model = ResourcesTreeModel.getInstance(resourcesProvider);
		return new ResourcesTree( model );
	}
	
	public void addStyleSelectionListener( StyleSelectionListener listener ) {
		if (this.styleSelectionListeners == null) {
			this.styleSelectionListeners = new ArrayList<StyleSelectionListener>();
		}
		this.styleSelectionListeners.add(listener);
	}
	
	public void addColorSelectionListener( ColorSelectionListener listener ) {
		if (this.colorSelectionListeners == null) {
			this.colorSelectionListeners = new ArrayList<ColorSelectionListener>();
		}
		this.colorSelectionListeners.add(listener);
	}
	
	public void addStyle( StyleProvider style ) {
		this.resourcesTreeModel.addStyle(style);
		this.resourcesTreeModel.reload();
	}

	public void addColor( ColorProvider color ) {
		this.resourcesTreeModel.addColor(color);
		this.resourcesTreeModel.reload();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent event) {
		if (this.ignoreEvents) {
			return;
		}
		ResourcesTreeModel.ResourceTreeNode node = (ResourceTreeNode) event.getPath().getLastPathComponent();
		Object value = node.getValue();
		if (value instanceof String) {
			return;
		}
		if (value instanceof StyleProvider && this.styleSelectionListeners != null) {
			for (StyleSelectionListener listener : this.styleSelectionListeners) {
				listener.notifyStyleSelected( (StyleProvider)value );
			}
		}
		if (value instanceof ColorProvider && this.colorSelectionListeners != null) {
			for (ColorSelectionListener listener : this.colorSelectionListeners) {
				listener.notifyColorSelected( (ColorProvider)value );
			}
		}
		
	}
	
	public void selectStyle( StyleProvider styleProvider ) {
		this.ignoreEvents = true;
		System.out.println("tree: trying to select style " + styleProvider.getStyle().name);
		TreePath path = this.resourcesTreeModel.getTreePathFor( styleProvider );
		if (path != null) {
			super.getSelectionModel().setSelectionPath( path );
		}
		this.ignoreEvents = false;
	}
}
