/*
 * Created on Mar 23, 2005 at 11:43:21 AM.
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
package de.enough.polish.plugin.eclipse.polishEditor;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.text.IJavaPartitions;
import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;



/**
 * <p>This editor is heavily inspired by the ajdt plugin.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 23, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishEditor extends CompilationUnitEditor {
    
    
    public void createPartControl(Composite parent){
        System.out.println("PolishEditor.createPartControl():enter.");
        IPreferenceStore preferenceStore = this.getPreferenceStore();
        IColorManager colorManager = new JavaColorManager();
        PolishSourceViewerConfiguration polishSourceViewerConfiguration;
        polishSourceViewerConfiguration =  new PolishSourceViewerConfiguration(colorManager,preferenceStore,this,IJavaPartitions.JAVA_PARTITIONING);
        setSourceViewerConfiguration(polishSourceViewerConfiguration);
        
        super.createPartControl(parent);
    }
}
