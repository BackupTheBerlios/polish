/*
 * Class name : DocStyler
 * Author     : Jim McLachlan
 * Version    : 1.0
 * Date       : 07-Nov-2005
 * 
 * Copyright (c) 2005-2006, Jim McLachlan (jim@oss-ltd.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.ossltd.mdevinf.ui;

import java.awt.Color;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * A utility class that provides a method for setting up a StyledDocument. 
 * @author Jim McLachlan
 * @version 1.0
 */
public class DocStyler
{
    /**
     * Sets the mDevInf standard styles for <code>JTextPane</code> documents.
     * @param doc the document to prepare
     */
    public static void prepareStyledDocument(StyledDocument doc)
    {
        Style def = StyleContext.getDefaultStyleContext().
                                   getStyle(StyleContext.DEFAULT_STYLE);
        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");
        Style bold = doc.addStyle("bold", regular);
        StyleConstants.setBold(bold, true);
        Style s = doc.addStyle("boldGreen", bold);
        StyleConstants.setForeground(s, new Color(0x00, 0xcc, 0x00));
        s = doc.addStyle("boldRed", bold);
        StyleConstants.setForeground(s, Color.red);
        s = doc.addStyle("boldBlue", bold);
        StyleConstants.setForeground(s, Color.blue);
    }
}
