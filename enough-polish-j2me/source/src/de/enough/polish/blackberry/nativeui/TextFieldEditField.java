//#condition polish.blackberry && polish.useNativeGui
/*
 * Created on Jan 22, 2010 at 9:24:41 PM.
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
package de.enough.polish.blackberry.nativeui;

import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import de.enough.polish.ui.ClippingRegion;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.NativeItem;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.TextField;


/**
 * <p>Paints a J2ME Polish field as it behaves normally instead of using a native BlackBerry representation.</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class TextFieldEditField 
extends EditField
implements FieldChangeListener, NativeItem
{
	
	
	protected TextField textField;
	private long lastFieldChangedEvent;
	private boolean isIgnoreValueChange;

	/**
	 * Creates a new custom field with a FIELD_LEFT style
	 * @param parent the parent item
	 */
	public TextFieldEditField(TextField parent) {
		this( parent, FieldHelper.getStyle(parent) );
	}

	/**
	 * Creates a new custom field
	 * @param parent the parent item
	 * @param style the BlackBerry native style 
	 */
	public TextFieldEditField(TextField parent, long style) {
		super(null, parent.getString(), parent.getMaxSize(), getEditStyle(parent, style));
		this.textField = parent;
		this.setChangeListener(this);
	}

	protected static long getEditStyle(TextField parent, long style) {
		int constraints = parent.getConstraints();
		int fieldType = constraints & 0xffff;
		style |= Field.FOCUSABLE;
		if (parent.isEditable()) {
			style |= Field.EDITABLE;				
		} else {
			style |= Field.READONLY;
		}
		if ( fieldType == TextField.DECIMAL || fieldType == TextField.FIXED_POINT_DECIMAL) {
			style |= BasicEditField.FILTER_REAL_NUMERIC;
		} else if (fieldType == TextField.NUMERIC) {
			style |= BasicEditField.FILTER_INTEGER;
		} else if (fieldType == TextField.PHONENUMBER) {
			style |= BasicEditField.FILTER_PHONE;
		} else if (fieldType == TextField.EMAILADDR ) {
			style |= BasicEditField.FILTER_EMAIL;
		} else if ( fieldType == TextField.URL ) {
			style |= BasicEditField.FILTER_URL;
		}
		if ((constraints & TextField.SENSITIVE) == TextField.SENSITIVE) {
			style |= BasicEditField.NO_LEARNING; 
		}
		if ((constraints & TextField.NON_PREDICTIVE) == TextField.NON_PREDICTIVE) {
			style |= BasicEditField.NO_LEARNING; 
		}
		if(parent.isNoNewLine()){
			style |= BasicEditField.NO_NEWLINE;
		}
		return style;
	}

	/* (non-Javadoc)
	 * @see net.rim.device.api.ui.Field#makeContextMenu(net.rim.device.api.ui.ContextMenu, int)
	 */
	protected void makeContextMenu(ContextMenu menu, int index) {
		super.makeContextMenu(menu, index);
		FieldHelper.makeContextMenu(menu, this.textField);
	}
	
	public void fieldChanged(Field field, int context) {
		if (context != FieldChangeListener.PROGRAMMATIC) {
			this.isIgnoreValueChange = true;
				try {
				//#if polish.Bugs.ItemStateListenerCalledTooEarly
					int fieldType = this.textField.getConstraints ()& 0xffff;
					if (fieldType == TextField.NUMERIC || fieldType == TextField.DECIMAL || fieldType == TextField.FIXED_POINT_DECIMAL) {
						this.textField.setString( getText() );				
						this.textField.notifyStateChanged();					
					} else {
						long currentTime = System.currentTimeMillis();
						this.lastFieldChangedEvent = currentTime;
						Screen scr = this.textField.getScreen();
						if (scr != null) {
							scr.setLastInteractionTime(currentTime);
						}
					}
				//#else
					this.textField.setString( getText() );				
					this.textField.notifyStateChanged();
				//#endif
			} finally {
				this.isIgnoreValueChange = false;
			}
		}
	}

	public void onValueChanged(Item parent, Object value) {
		if (!this.isIgnoreValueChange) {
			setText( (String) value );
		}
	}

	public void animate(long currentTime, ClippingRegion repaintRegion) {
		//#if polish.Bugs.ItemStateListenerCalledTooEarly
			if (this.lastFieldChangedEvent != 0 && currentTime - this.lastFieldChangedEvent > 500) {
				this.lastFieldChangedEvent = 0;
				this.isIgnoreValueChange = true;
				try {
					this.textField.setString( getText() );
					this.textField.notifyStateChanged();
				} finally {
					this.isIgnoreValueChange = false;
				}
			}
		//#endif
	}

	public Item getPolishItem() {
		return this.textField;
	}

	
}