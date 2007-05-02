/*
 * Created on Apr 23, 2007 at 9:49:59 PM.
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

import de.enough.polish.preprocess.css.CssAttribute;
import de.enough.polish.styleeditor.EditStyle;
import de.enough.polish.styleeditor.ItemOrScreen;

/**
 * <p>Edits item/screen specific attributes of a style</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Apr 23, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class SpecificAttributesEditor extends CssAttributesEditor {
	
	private String itemOrScreenClassName;


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#writeStyle(EditStyle)
	 */
	public void writeStyle( EditStyle style ) {
		
	}
	

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#readStyle(de.enough.polish.styleeditor.EditStyle)
	 */
	public void readStyle(EditStyle style) {
		this.itemOrScreenClassName = null;
		ItemOrScreen itemOrScreen = style.getItemOrScreen();
		Class itemOrScreenClass = null;
		if (itemOrScreen != null) {
			Object itemOrScreenObj = itemOrScreen.getItemOrScreen();
			if (itemOrScreenObj != null) {
				itemOrScreenClass = itemOrScreenObj.getClass();
				if (this.visual != null) {
					String className = itemOrScreenClass.getName();
					int lastDotPos = className.lastIndexOf('.');
					if (lastDotPos != -1) {
						className = className.substring(lastDotPos+1);
					}
					this.itemOrScreenClassName = className;
				}
			}
		}
		
		// get specific attributes:
		if (itemOrScreenClass == null) {
			setCssAttributes(null);			
		} else {
			CssAttribute[] attributes = this.styleEditor.getApplicableAttributes(itemOrScreenClass);
			setCssAttributes(attributes);
		}
		super.readStyle(style);
		if (this.visual != null) {
			this.visual.setCssAttributes(getAttributes(), getEditors());
		}
	}
	
	public String getItemOrScreenClassName() {
		return this.itemOrScreenClassName;
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.StylePartEditor#getName()
	 */
	public String createName() {
		return "specific attributes";
	}


	
	

}
