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
public class MarginPaddingEditor extends StylePartEditor {
	
	private String margin;
	private String marginLeft;
	private String marginRight;
	private String marginTop;
	private String marginBottom;
	private String padding;
	private String paddingLeft;
	private String paddingRight;
	private String paddingTop;
	private String paddingBottom;
	private String paddingVertical;
	private String paddingHorizontal;


	/**
	 */
	public MarginPaddingEditor() {
		// just create MarginPaddingEditor
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#updateStyle()
	 */
	public void writeStyle(EditStyle style) {
		style.setMargin(this.margin);
		style.setMarginLeft(this.marginLeft);
		style.setMarginRight(this.marginRight);
		style.setMarginTop(this.marginTop);
		style.setMarginBottom(this.marginBottom);
		style.setPadding(this.padding);
		style.setPaddingLeft(this.paddingLeft);
		style.setPaddingRight(this.paddingRight);
		style.setPaddingTop(this.paddingTop);
		style.setPaddingBottom(this.paddingBottom);
		style.setPaddingHorizontal(this.paddingHorizontal);
		style.setPaddingVertical(this.paddingVertical);
	}
	


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#readStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void readStyle(EditStyle style) {
		this.marginLeft = style.getMarginLeft();
		this.marginRight = style.getMarginRight();
		this.marginTop = style.getMarginTop();
		this.marginBottom = style.getMarginBottom();

		this.paddingLeft = style.getPaddingLeft();
		this.paddingRight = style.getPaddingRight();
		this.paddingTop = style.getPaddingTop();
		this.paddingBottom = style.getPaddingBottom();
		this.paddingVertical = style.getPaddingVertical();
		this.paddingHorizontal= style.getPaddingHorizontal();
	}


	/**
	 * @return the margin
	 */
	public String getMargin() {
	return this.margin;}
	


	/**
	 * @param margin the margin to set
	 */
	public void setMargin(String margin) {
		this.margin = margin;
		update();
	}


	/**
	 * @return the marginBottom
	 */
	public String getMarginBottom() {
		return this.marginBottom;
	}
	


	/**
	 * @param marginBottom the marginBottom to set
	 */
	public void setMarginBottom(String marginBottom) {
		this.marginBottom = marginBottom;
		update();
	}


	/**
	 * @return the marginLeft
	 */
	public String getMarginLeft() {
		return this.marginLeft;
	}
	


	/**
	 * @param marginLeft the marginLeft to set
	 */
	public void setMarginLeft(String marginLeft) {
		this.marginLeft = marginLeft;
		update();
	}


	/**
	 * @return the marginRight
	 */
	public String getMarginRight() {
		return this.marginRight;
	}
	


	/**
	 * @param marginRight the marginRight to set
	 */
	public void setMarginRight(String marginRight) {
		this.marginRight = marginRight;
		update();
	}


	/**
	 * @return the marginTop
	 */
	public String getMarginTop() {
		return this.marginTop;
	}
	


	/**
	 * @param marginTop the marginTop to set
	 */
	public void setMarginTop(String marginTop) {
		this.marginTop = marginTop;
		update();
	}


	/**
	 * @return the padding
	 */
	public String getPadding() {
		return this.padding;
	}
	


	/**
	 * @param padding the padding to set
	 */
	public void setPadding(String padding) {
		this.padding = padding;
		update();
	}


	/**
	 * @return the paddingBottom
	 */
	public String getPaddingBottom() {
		return this.paddingBottom;
	}
	


	/**
	 * @param paddingBottom the paddingBottom to set
	 */
	public void setPaddingBottom(String paddingBottom) {
		this.paddingBottom = paddingBottom;
		update();
	}


	/**
	 * @return the paddingHorizontal
	 */
	public String getPaddingHorizontal() {
		return this.paddingHorizontal;
	}
	


	/**
	 * @param paddingHorizontal the paddingHorizontal to set
	 */
	public void setPaddingHorizontal(String paddingHorizontal) {
		this.paddingHorizontal = paddingHorizontal;
		update();
	}


	/**
	 * @return the paddingLeft
	 */
	public String getPaddingLeft() {
		return this.paddingLeft;
	}
	


	/**
	 * @param paddingLeft the paddingLeft to set
	 */
	public void setPaddingLeft(String paddingLeft) {
		this.paddingLeft = paddingLeft;
		update();
	}


	/**
	 * @return the paddingRight
	 */
	public String getPaddingRight() {
		return this.paddingRight;
	}
	


	/**
	 * @param paddingRight the paddingRight to set
	 */
	public void setPaddingRight(String paddingRight) {
		this.paddingRight = paddingRight;
		update();
	}


	/**
	 * @return the paddingTop
	 */
	public String getPaddingTop() {
		return this.paddingTop;
	}
	


	/**
	 * @param paddingTop the paddingTop to set
	 */
	public void setPaddingTop(String paddingTop) {
		this.paddingTop = paddingTop;
		update();
	}


	/**
	 * @return the paddingVertical
	 */
	public String getPaddingVertical() {
		return this.paddingVertical;
	}
	


	/**
	 * @param paddingVertical the paddingVertical to set
	 */
	public void setPaddingVertical(String paddingVertical) {
		this.paddingVertical = paddingVertical;
		update();
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#createName()
	 */
	public String createName() {
		return "margin/padding";
	}
	

}
