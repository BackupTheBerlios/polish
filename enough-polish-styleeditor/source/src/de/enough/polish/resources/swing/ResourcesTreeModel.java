/*
 * Created on May 4, 2007 at 1:42:27 AM.
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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.enough.polish.resources.ColorProvider;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.resources.StyleProvider;

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
public class ResourcesTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = 287464883854438828L;
	private final ResourceTreeNode rootNode;
	private final ResourceTreeNode colorsNode;
	private final ResourceTreeNode stylesNode;
	
	private ResourcesTreeModel(ResourceTreeNode rootNode, ResourceTreeNode colorsNode, ResourceTreeNode stylesNode ) {
		super(rootNode);
		this.rootNode = rootNode;
		this.colorsNode = colorsNode;
		this.stylesNode = stylesNode;
	}
	
	
	public void addStyle( StyleProvider style ) {
		this.stylesNode.addChild(style);
	}
	
	public void addColor( ColorProvider color ) {
		this.colorsNode.addChild(color);
	}
	
	
	
	public static ResourcesTreeModel getInstance( ResourcesProvider resourcesProvider ) {
		ResourceTreeNode root = new ResourceTreeNode(null, "Resources");
		ResourceTreeNode colorsNode = root.addChild( "Colors" );
		ColorProvider[] colors = resourcesProvider.getColors();
		for (int i = 0; i < colors.length; i++) {
			ColorProvider color = colors[i];
			colorsNode.addChild(color);
		}
		ResourceTreeNode stylesNode = root.addChild( "Styles" );
		StyleProvider[] styles = resourcesProvider.getStyles();
		for (int i = 0; i < styles.length; i++) {
			StyleProvider style = styles[i];
			stylesNode.addChild(style);
		}
		return new ResourcesTreeModel( root, colorsNode, stylesNode );
	}
	

	/**
	 * @param styleProvider
	 * @return
	 */
	public TreePath getTreePathFor( Object value ) {
		TreePath path = new TreePath(this.rootNode);
		Object[] roots = this.rootNode.children.toArray();
		for (int i = 0; i < roots.length; i++) {
			ResourceTreeNode node = (ResourceTreeNode) roots[i];
			Object[] children = node.children.toArray();
			for (int j = 0; j < children.length; j++) {
				ResourceTreeNode childNode = (ResourceTreeNode) children[j];
				if (childNode.getValue().equals( value) ) {
					path = path.pathByAddingChild( node ).pathByAddingChild(childNode);
					return path;
				}
			}
		}
		return null;
	}
	

	static class ResourceTreeNode implements TreeNode {
		
		private final String name;
		private final List<TreeNode> children;
		private final TreeNode parent;
		private final Object value;

		public ResourceTreeNode( TreeNode parent, Object value ) {
			this.name = value.toString();
			this.parent = parent;
			this.value = value;
			this.children = new ArrayList();		
		}
		
		public ResourceTreeNode addChild( Object value ) {
			ResourceTreeNode node = new ResourceTreeNode( this, value );
			this.children.add(node);
			return node;
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeNode#children()
		 */
		public Enumeration children() {
			return this.children();
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeNode#getAllowsChildren()
		 */
		public boolean getAllowsChildren() {
			return true;
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeNode#getChildAt(int)
		 */
		public TreeNode getChildAt(int index) {
			return this.children.get(index);
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeNode#getChildCount()
		 */
		public int getChildCount() {
			return this.children.size();
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
		 */
		public int getIndex(TreeNode node) {
			return this.children.indexOf(node);
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeNode#getParent()
		 */
		public TreeNode getParent() {
			return this.parent;
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeNode#isLeaf()
		 */
		public boolean isLeaf() {
			return (this.children.size() == 0);
		}
		
		public String toString() {
			return this.name;
		}
		
		public Object getValue() {
			return this.value;
		}
		
	}

}
