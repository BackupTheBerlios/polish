/*
 * Created on Sep 29, 2006 at 11:09:51 AM.
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
package de.enough.mepose.editor.java;

import java.util.HashMap;

import org.eclipse.ui.IEditorPart;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Sep 29, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class MeposeLayerManager {

    private HashMap layerByEditorPart;
    
    public MeposeLayerManager() {
        this.layerByEditorPart = new HashMap();
    }
    
    public void activateLayer(IEditorPart editorPart) {
        System.out.println("DEBUG:MeposeLayer.activateLayer(...):enter.");
        MeposeLayer meposeLayer = (MeposeLayer)this.layerByEditorPart.get(editorPart);
        if(meposeLayer == null) {
            meposeLayer = createLayer(editorPart);
            this.layerByEditorPart.put(editorPart,meposeLayer);
        }
    }
    

    public void deactivateLayer(IEditorPart editorPart) {
        System.out.println("DEBUG:enclosing_type.enclosing_method(...):enter.");
        this.layerByEditorPart.remove(editorPart);
    }

    protected MeposeLayer createLayer(IEditorPart editorPart) {
        return null;
    }
}
