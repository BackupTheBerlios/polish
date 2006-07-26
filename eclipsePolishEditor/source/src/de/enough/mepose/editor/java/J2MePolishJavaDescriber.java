package de.enough.mepose.editor.java;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;
/*
 * Created on Jun 22, 2006 at 2:29:05 PM.
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

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jun 22, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class J2MePolishJavaDescriber implements ITextContentDescriber {

    public int describe(Reader contents, IContentDescription description)
                                                                         throws IOException {
        return VALID;
    }

    public int describe(InputStream contents, IContentDescription description)
                                                                              throws IOException {
        if(description != null) {
            description.setProperty(IContentDescription.CHARSET,"ISO-8859-1");
        }
        return VALID;
    }

    public QualifiedName[] getSupportedOptions() {
        return new QualifiedName[0];
    }
}
