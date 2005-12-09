/*
 * Created on Dec 5, 2005 at 7:06:46 PM.
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
package de.enough.mepose;

import de.enough.mepose.core.ui.preferencePages.InstallationPage;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 5, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public interface MeposeCoreUIConstants {

    public static final String EXTENSION_ID_INSTALLATIONPAGE = InstallationPage.class.getName();
    
    public static final String KEY_IMAGE_LOGO = "logo";
    public static final String KEY_IMAGE_WARNING = "warning";
    public static final String KEY_IMAGE_ERROR = "error";
    public static final String KEY_IMAGE_OK = "ok";
}
