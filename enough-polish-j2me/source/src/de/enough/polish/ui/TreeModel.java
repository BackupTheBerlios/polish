//#condition polish.usePolishGui
/*
 * Copyright (c) 2009 Robert Virkus / Enough Software
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

package de.enough.polish.ui;

/**
 * Provides data for a TreeItem.
 * The TreeModel is read when needed, meaning this model allows to realize a lazy loading mechanism of structured data.
 * 
 * @author Robert Virkus
 * @see TreeItem
 */
public interface TreeModel {
	
	/**
	 * Returns the root of the tree. Returns null  only if the tree has no nodes. 
	 * Note: The root element itself is not shown by a TreeItem.
	 * @return the root of the tree
	 */
	public Object getRoot();
	
	/**
	 * Returns the number of children of parent. 
	 * Returns 0 if the node is a leaf or if it has no children. parent must be a node previously obtained from this TreeModel.
	 *  
	 * @param parent a node in the tree, obtained from this TreeModel
	 * @return the number of children of the node parent
	 */
	public int getChildCount( Object parent );
	
	/**
	 * Returns the child of parent at index index  in the parent's child array. parent must be a node previously obtained from this TreeModel. 
	 * This should not return null  if index  is a valid index for parent (that is index &gt;= 0 &&  index &lt; getChildCount(parent)).
	 * 
	 * @param parent a node in the tree, obtained from this TreeModel
	 * @param index the position of the item: index &gt;= 0 &&  index &lt; getChildCount(parent)
	 * @return the child node
	 */
	public Object getChild( Object parent, int index );
	
	/**
	 * Returns true if node is a leaf. 
	 * It is possible for this method to return false  even if node has no children. A directory in a filesystem, for example, may contain no files; the node representing the directory is not a leaf, but it also has no children. 
	 * @param node a node in the tree, obtained from this TreeModel
	 * @return true if the node is a leaf
	 */
	public boolean isLeaf( Object node);

}
