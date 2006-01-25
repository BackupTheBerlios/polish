/*
 * Created on Jan 25, 2006 at 11:39:33 AM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
package de.enough.mepose.core.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jan 25, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class ErrorHandler {

    public static void error(String shortDesc, String message) {
        if(message == null) {
            message = "";
        }
        MessageBox box = new MessageBox(Display.getDefault().getActiveShell(),SWT.ICON_ERROR|SWT.YES|SWT.NO);
        box.setMessage(message);
        box.setText(shortDesc);
        int status = box.open();
        switch (status) {
            case SWT.YES:
                System.out.println("DEBUG:ErrorHandler.error(...):yes selected");
                break;
            case SWT.NO:
                System.out.println("DEBUG:ErrorHandler.error(...):no selected");
                break;
            default:
                break;
        }
    }
}
