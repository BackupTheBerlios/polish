//#condition polish.usePolishGui && polish.blackberry

/*
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
/*
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.blackberry.ui;

import java.util.Date;
import java.util.TimeZone;

import de.enough.polish.ui.StyleSheet;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.DateField;

/**
 * <p>A field used by de.enough.polish.ui.DateField for input of dates on blackberry platforms.</p>
 *
 * <p>copyright Enough Software 2006</p>
 * <pre>
 * history
 *        18.09.2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishDateField extends DateField {

    private boolean isFocused;
    public boolean processKeyEvents = true;
	private int fontColor;

	/**
	 * Creates a new date field
	 *
	 * @param date the date
   * @param inputMode the input mode for the date, either
	 * DateField.DATE, DateField.DATE_TIME or DateField.TIME.
   *
   * @see javax.microedition.lcdui.DateField
	 */
	public PolishDateField( Date date, int inputMode ) {
		this( getTime( date ), getDateFormat( inputMode), EDITABLE );
	}
	
	private static final long getTime( Date date ) {
		if (date == null) {
			return System.currentTimeMillis();
		} else {
			return date.getTime();
		}
	}
	
	private static DateFormat getDateFormat( int mode ) {
		int blackberryMode;
		switch (mode) {
		case DateField.TIME:
			blackberryMode = DateFormat.TIME_DEFAULT;
			break;
		case DateField.DATE_TIME:
			blackberryMode = DateFormat.DATETIME_DEFAULT;
			break;
		default:
			blackberryMode = DateFormat.DATE_DEFAULT;
		}
		return DateFormat.getInstance( blackberryMode );
	}

	/**
	 * Creates a new date field
	 *
	 * @param date the date in ms from 1970-01-01
	 * @param dateFormat the format
	 * @param style the editing style, e.g. Field.EDITABLE
	 */
	public PolishDateField(long date, DateFormat dateFormat, long style) {
		super(null, date, dateFormat, style);
	}

	public void focusAdd( boolean draw ) {
        //System.out.println("DateField: focusAdd (" + getText() + ")");
        super.focusAdd( draw );
        this.isFocused = true;
	}
	
	public void focusRemove() {
	        //System.out.println("DateField: focusRemove (" + getText() + ")");
	        super.focusRemove();
	        this.isFocused = false;
	}
	
	 public void paint( net.rim.device.api.ui.Graphics g ) {
         if (this.isFocused && !StyleSheet.currentScreen.isMenuOpened()) {
       	  //g.setFont( this.font.font );
       	  g.setColor( this.fontColor );
             super.paint( g );
         }
   }
	 
     public void layout( int width, int height) {
         super.layout( width, height );
     }
   
	public void setFont(Font font, int textColor) {
		super.setFont( font.font );
		this.fontColor = textColor;
	}

	public void setFont(javax.microedition.lcdui.Font font, int textColor) {
		// never used anyhow, since javax.microedition.lcdui.Font is mapped to Font
	}

	public void setPaintPosition(int x, int y ) {
       this.isFocused = true;
       super.setPosition(x, y);
	}

	public void setInputMode(int mode) {
		DateFormat format = getDateFormat(mode);
		Object bbLock = UiApplication.getEventLock();
		synchronized (bbLock) {
			setFormat( format );
		}
	}

	public static final DateFormat getDateFormat(int mode, TimeZone timeZone) {
		DateFormat format = getDateFormat( mode );
		//TODO RIM API says it's possible to set timezone on DateFormat, however no applicable methods can be found
		return format;
	}
}
