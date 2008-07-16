/*
 * Created on Jun 6, 2008 at 4:50:07 PM.
 * 
 * Copyright (c) 2008 Robert Virkus / Enough Software
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
package de.enough.swt.widgets;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * <br>Copyright Enough Software 2005-2008
 * <pre>
 * history
 *        Jun 6, 2008 - rickyn creation
 * </pre>
 * @author Richard Nkrumah
 */
public class DialogUtils {

    public static void showErrorBox(final String title, final String message) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                Shell activeShell = Display.getDefault().getActiveShell();
                MessageDialog.openError(activeShell, title, message);
            }
        });
    }
}
