/*
 * Created on Apr 23, 2007 at 9:44:25 PM.
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
package de.enough.polish.styleeditor.editors;

import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.styleeditor.StylePartEditor;
import de.enough.polish.ui.Item;

/**
 * <p>Edits the margins and paddings of a style</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class LayoutEditor extends StylePartEditor {
	
	private int layout;


	/**
	 */
	public LayoutEditor() {
		// create a new editor
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#updateStyle()
	 */
	public void writeStyle( EditStyle style ) {
		style.setLayout(this.layout);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#setStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void readStyle(EditStyle style) {
		this.layout = style.getLayoutAsInt();
	}
	
	public boolean isLayoutLeft() {
		return ((this.layout & Item.LAYOUT_LEFT) == Item.LAYOUT_LEFT)
			&& (( this.layout & Item.LAYOUT_RIGHT) != Item.LAYOUT_RIGHT)
			&& (( this.layout & Item.LAYOUT_CENTER) != Item.LAYOUT_CENTER);
	}

	public boolean isLayoutRight() {
		return (( this.layout & Item.LAYOUT_RIGHT) == Item.LAYOUT_RIGHT)
			&& (( this.layout & Item.LAYOUT_CENTER) != Item.LAYOUT_CENTER);
	}
	
	public boolean isLayoutHorizontalCenter() {
		return  (( this.layout & Item.LAYOUT_CENTER) == Item.LAYOUT_CENTER);
	}
	
	public boolean isLayoutTop() {
		return ((this.layout & Item.LAYOUT_TOP) == Item.LAYOUT_TOP)
			&& (( this.layout & Item.LAYOUT_BOTTOM) != Item.LAYOUT_BOTTOM)
			&& (( this.layout & Item.LAYOUT_VCENTER) != Item.LAYOUT_VCENTER);
	}

	public boolean isLayoutBottom() {
		return (( this.layout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM)
			&& (( this.layout & Item.LAYOUT_VCENTER) != Item.LAYOUT_VCENTER);
	}
	
	public boolean isLayoutVerticalCenter() {
		return  (( this.layout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER);
	}
	
	public boolean isLayoutTopLeft() {
		return isLayoutTop() & isLayoutLeft();
	}

	public boolean isLayoutTopCenter() {
		return isLayoutTop() & isLayoutHorizontalCenter();
	}
	
	public boolean isLayoutTopRight() {
		return isLayoutTop() & isLayoutRight();
	}
	
	public boolean isLayoutVerticalCenterLeft() {
		return isLayoutVerticalCenter() & isLayoutLeft();
	}

	public boolean isLayoutVerticalCenterCenter() {
		return isLayoutVerticalCenter() & isLayoutHorizontalCenter();
	}
	
	public boolean isLayoutVerticalCenterRight() {
		return isLayoutVerticalCenter() & isLayoutRight();
	}
	
	public boolean isLayoutBottomLeft() {
		return isLayoutBottom() & isLayoutLeft();
	}

	public boolean isLayoutBottomCenter() {
		return isLayoutBottom() & isLayoutHorizontalCenter();
	}
	
	public boolean isLayoutBottomRight() {
		return isLayoutBottom() & isLayoutRight();
	}



	public void setLayoutLeft() {
		this.layout = this.layout & ~Item.LAYOUT_CENTER | Item.LAYOUT_LEFT;
		update();
	}

	public void setLayoutRight() {
		this.layout = this.layout & ~Item.LAYOUT_CENTER | Item.LAYOUT_RIGHT;
		update();
	}

	public void setLayoutHorizontalCenter() {
		this.layout = this.layout | Item.LAYOUT_CENTER;
		update();
	}
	
	public void setLayoutTop() {
		this.layout = this.layout & ~Item.LAYOUT_VCENTER | Item.LAYOUT_TOP;
		update();
	}

	public void setLayoutBottom() {
		this.layout = this.layout & ~Item.LAYOUT_VCENTER | Item.LAYOUT_BOTTOM;
		update();
	}

	public void setLayoutVerticalCenter() {
		this.layout = this.layout | Item.LAYOUT_VCENTER;
		update();
	}
	
	
	/**
	 * @return
	 */
	public boolean isLayoutHorizontalExpand() {
		return ((this.layout & Item.LAYOUT_EXPAND) == Item.LAYOUT_EXPAND);
	}
	
	public boolean isLayoutHorizontalShrink() {
		return ((this.layout & Item.LAYOUT_SHRINK) == Item.LAYOUT_SHRINK);
	}

	public boolean isLayoutVerticalExpand() {
		return ((this.layout & Item.LAYOUT_VEXPAND) == Item.LAYOUT_VEXPAND);
	}
	
	public boolean isLayoutVerticalShrink() {
		return ((this.layout & Item.LAYOUT_VSHRINK) == Item.LAYOUT_VSHRINK);
	}

	public void setLayoutHorizontalExpand() {
		setLayoutHorizontalExpand(true);
	}
	public void setLayoutHorizontalExpand(boolean enable) {
		if (enable) {			
			this.layout |= Item.LAYOUT_EXPAND;
		} else {
			this.layout &= ~Item.LAYOUT_EXPAND;
		}
		update();
	}
	
	public void setLayoutHorizontalShrink() {
		setLayoutHorizontalShrink(true);
	}
	public void setLayoutHorizontalShrink(boolean enable) {
		if (enable) {			
			this.layout |= Item.LAYOUT_SHRINK;
		} else {
			this.layout &= ~Item.LAYOUT_SHRINK;
		}
		update();
	}

	public void setLayoutVerticalExpand() {
		setLayoutVerticalExpand(true);
	}
	public void setLayoutVerticalExpand(boolean enable) {
		if (enable) {			
			this.layout |= Item.LAYOUT_VEXPAND;
		} else {
			this.layout &= ~Item.LAYOUT_VEXPAND;
		}
		update();
	}

	public void setLayoutVerticalShrink() {
		setLayoutVerticalShrink(true);
	}
	public void setLayoutVerticalShrink(boolean enable) {
		if (enable) {			
			this.layout |= Item.LAYOUT_VSHRINK;
		} else {
			this.layout &= ~Item.LAYOUT_VSHRINK;
		}
		update();
	}
	
	public boolean isNewLineAfter() {
		return ((this.layout & Item.LAYOUT_NEWLINE_AFTER) == Item.LAYOUT_NEWLINE_AFTER);
	}

	public boolean isNewLineBefore() {
		return ((this.layout & Item.LAYOUT_NEWLINE_BEFORE) == Item.LAYOUT_NEWLINE_BEFORE);
	}
	
	public void setNewLineAfter() {
		setNewLineAfter( true );
	}
	
	public void setNewLineAfter(boolean enable) {
		if (enable) {			
			this.layout |= Item.LAYOUT_NEWLINE_AFTER;
		} else {
			this.layout &= ~Item.LAYOUT_NEWLINE_AFTER;
		}
		update();
	}

	public void setNewLineBefore() {
		setNewLineBefore( true );
	}
	
	public void setNewLineBefore(boolean enable) {
		if (enable) {			
			this.layout |= Item.LAYOUT_NEWLINE_BEFORE;
		} else {
			this.layout &= ~Item.LAYOUT_NEWLINE_BEFORE;
		}
		update();
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#getName()
	 */
	public String createName() {
		return "layout";
	}


	/**
	 * @return
	 */
	public boolean isLayoutDefault() {
		return (this.layout == 0);
	}


	

	

}
