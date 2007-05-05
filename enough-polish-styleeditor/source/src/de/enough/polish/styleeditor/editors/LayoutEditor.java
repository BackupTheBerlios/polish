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
		//System.out.println( "LayoutEditor.writeStyle: layout=" + Integer.toHexString(this.layout));
		style.setLayout( this.layout );
		style.setLayout( getLayoutAsString() );
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#setStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void readStyle(EditStyle style) {
		this.layout = style.getLayoutAsInt();
	}
	
	/**
	 * @return
	 */
	private String getLayoutAsString() {
		StringBuffer buffer = new StringBuffer();
		if (isHorizontalCenter()) {
			buffer.append("horizontal-center");
		} else if (isHorizontalRight()) {
			buffer.append("right");
		} else {
			buffer.append("left");
		}
		if (isVerticalCenter()) {
			buffer.append( " | vertical-center");
		} else if (isVerticalBottom()) {
			buffer.append( " | bottom");
		} else if (isVerticalTop()) {
			buffer.append( " | top");
		}
		if (isHorizontalExpand()) {
			buffer.append(" | horizontal-expand");
		} else if (isHorizontalShrink()){
			buffer.append(" | horizontal-shrink");
		}
		if (isVerticalExpand()) {
			buffer.append(" | vertical-expand");
		} else if (isVerticalShrink()){
			buffer.append(" | vertical-shrink");
		}
		if (isNewLineAfter()) {
			buffer.append(" | newline-after");
		}
		if (isNewLineBefore()) {
			buffer.append(" | newline-before");
		}
		return buffer.toString();
	}
	
	public boolean isHorizontalLeft() {
		return ((this.layout & Item.LAYOUT_LEFT) == Item.LAYOUT_LEFT)
			&& (( this.layout & Item.LAYOUT_RIGHT) != Item.LAYOUT_RIGHT)
			&& (( this.layout & Item.LAYOUT_CENTER) != Item.LAYOUT_CENTER);
	}

	public boolean isHorizontalRight() {
		return (( this.layout & Item.LAYOUT_RIGHT) == Item.LAYOUT_RIGHT)
			&& (( this.layout & Item.LAYOUT_CENTER) != Item.LAYOUT_CENTER);
	}
	
	public boolean isHorizontalCenter() {
		return  (( this.layout & Item.LAYOUT_CENTER) == Item.LAYOUT_CENTER);
	}
	
	public boolean isVerticalTop() {
		return ((this.layout & Item.LAYOUT_TOP) == Item.LAYOUT_TOP)
			&& (( this.layout & Item.LAYOUT_BOTTOM) != Item.LAYOUT_BOTTOM)
			&& (( this.layout & Item.LAYOUT_VCENTER) != Item.LAYOUT_VCENTER);
	}

	public boolean isVerticalBottom() {
		return (( this.layout & Item.LAYOUT_BOTTOM) == Item.LAYOUT_BOTTOM)
			&& (( this.layout & Item.LAYOUT_VCENTER) != Item.LAYOUT_VCENTER);
	}
	
	public boolean isVerticalCenter() {
		return  (( this.layout & Item.LAYOUT_VCENTER) == Item.LAYOUT_VCENTER);
	}
	
	public boolean isVerticalTopAndHorizontalLeft() {
		return isVerticalTop() & isHorizontalLeft();
	}

	public boolean isVerticalTopAndHorizontalCenter() {
		return isVerticalTop() & isHorizontalCenter();
	}
	
	public boolean isVerticalTopAndHorizontalRight() {
		return isVerticalTop() & isHorizontalRight();
	}
	
	public boolean isVerticalCenterAndHorizontalLeft() {
		return isVerticalCenter() & isHorizontalLeft();
	}

	public boolean isVerticalCenterAndHorizontalCenter() {
		return isVerticalCenter() & isHorizontalCenter();
	}
	
	public boolean isVerticalCenterAndHorizontalRight() {
		return isVerticalCenter() & isHorizontalRight();
	}
	
	public boolean isVerticalBottomAndHorizontalLeft() {
		return isVerticalBottom() & isHorizontalLeft();
	}

	public boolean isVerticalBottomAndHorizontalCenter() {
		return isVerticalBottom() & isHorizontalCenter();
	}
	
	public boolean isVerticalBottomHorizontalRight() {
		return isVerticalBottom() & isHorizontalRight();
	}



	public void setHorizontalLeft() {
		this.layout = this.layout & ~Item.LAYOUT_CENTER | Item.LAYOUT_LEFT;
		update();
	}

	public void setHorizontalRight() {
		this.layout = this.layout & ~Item.LAYOUT_CENTER | Item.LAYOUT_RIGHT;
		update();
	}

	public void setHorizontalCenter() {
		this.layout = this.layout | Item.LAYOUT_CENTER;
		update();
	}
	
	public void setVerticalTop() {
		this.layout = this.layout & ~Item.LAYOUT_VCENTER | Item.LAYOUT_TOP;
		update();
	}

	public void setVerticalBottom() {
		this.layout = this.layout & ~Item.LAYOUT_VCENTER | Item.LAYOUT_BOTTOM;
		update();
	}

	public void setVerticalCenter() {
		this.layout = this.layout | Item.LAYOUT_VCENTER;
		update();
	}
	
	
	/**
	 * @return
	 */
	public boolean isHorizontalExpand() {
		return ((this.layout & Item.LAYOUT_EXPAND) == Item.LAYOUT_EXPAND);
	}
	
	public boolean isHorizontalShrink() {
		return ((this.layout & Item.LAYOUT_SHRINK) == Item.LAYOUT_SHRINK);
	}

	public boolean isVerticalExpand() {
		return ((this.layout & Item.LAYOUT_VEXPAND) == Item.LAYOUT_VEXPAND);
	}
	
	public boolean isVerticalShrink() {
		return ((this.layout & Item.LAYOUT_VSHRINK) == Item.LAYOUT_VSHRINK);
	}

	public void setHorizontalExpand() {
		setHorizontalExpand(true);
	}
	public void setHorizontalExpand(boolean enable) {
		if (enable) {			
			this.layout |= Item.LAYOUT_EXPAND;
		} else {
			this.layout &= ~Item.LAYOUT_EXPAND;
		}
		update();
	}
	
	public void setHorizontalShrink() {
		setHorizontalShrink(true);
	}
	public void setHorizontalShrink(boolean enable) {
		if (enable) {			
			this.layout |= Item.LAYOUT_SHRINK;
		} else {
			this.layout &= ~Item.LAYOUT_SHRINK;
		}
		update();
	}

	public void setVerticalExpand() {
		setVerticalExpand(true);
	}
	public void setVerticalExpand(boolean enable) {
		if (enable) {			
			this.layout |= Item.LAYOUT_VEXPAND;
		} else {
			this.layout &= ~Item.LAYOUT_VEXPAND;
		}
		update();
	}

	public void setVerticalShrink() {
		setVerticalShrink(true);
	}
	public void setVerticalShrink(boolean enable) {
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
	public boolean isHorizontalDefault() {
		return (this.layout == 0);
	}


	

	

}
