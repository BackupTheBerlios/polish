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
package de.enough.polish.plugin.eclipse.polishEditor.editor;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.text.IJavaPartitions;
import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;




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
    
//    private PropertyChangeListener propertyChangeListener;


//    class PropertyChangeListener implements IPropertyChangeListener{
//
//        /* (non-Javadoc)
//         * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
//         */
//        public void propertyChange(PropertyChangeEvent event) {
//            System.out.println("PolishEditor.PropertyChangeListener.propertyChange():event:property:"+event.getProperty()+".event:newValue:"+event.getNewValue());
//            System.out.println("PolishEditor.PropertyChangeListener.propertyChange().event:source:class:"+event.getSource().getClass());
//        }
//        
//    }
     
    
    public PolishEditor() {
        System.out.println("PolishEditor.PolishEditor():enter.");
//        this.propertyChangeListener = new PropertyChangeListener();
    }
    
    
    public void createPartControl(Composite parent){
        System.out.println("PolishEditor.createPartControl():enter.");
        
        
        IPreferenceStore preferenceStoreJava = this.getPreferenceStore(); // This one origins from JavaEditor and is a chain of 4 stores.
        IPreferenceStore preferenceStorePolish = PolishEditorPlugin.getDefault().getPreferenceStore();

//        preferenceStorePolish.addPropertyChangeListener(this.propertyChangeListener);
        
        
       
        IPreferenceStore[] preferenceStores = new IPreferenceStore[2];
        preferenceStores[0] = preferenceStoreJava;
        preferenceStores[1] = preferenceStorePolish;

        // We are forced to do this kludge because all private methods of suberclasses uses this one preferenceStore.
        // We do need write support so we give others this chainPreferenceStore and when we edit some values
        // we get our own store from the plugin. And thus hardwiring as unforgivable like all others.
        IPreferenceStore preferenceStore = new ChainedPreferenceStore(preferenceStores);
        
        
        IColorManager colorManager = new JavaColorManager();
        
        
        PolishSourceViewerConfiguration polishSourceViewerConfiguration;
        polishSourceViewerConfiguration =  new PolishSourceViewerConfiguration(colorManager,preferenceStore,this,IJavaPartitions.JAVA_PARTITIONING);
        setSourceViewerConfiguration(polishSourceViewerConfiguration);
        
        
        super.createPartControl(parent);
   
    }
    
    
    protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
        System.out.println("PolishEditor.handlePreferenceStoreChanged(...):enter.");
        super.handlePreferenceStoreChanged(event);
    }
}
