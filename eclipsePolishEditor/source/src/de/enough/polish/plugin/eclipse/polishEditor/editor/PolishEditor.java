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

import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.core.model.MeposeModel;
import de.enough.polish.Environment;
import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;
import de.enough.polish.plugin.eclipse.polishEditor.editor.occurrenceAnnotations.OccurrencesMarkerManager;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.ICompilationUnitDocumentProvider;
import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;




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
    
    public static final String ID = "de.enough.polish.plugin.eclipse.polishEditor.editor.PolishEditor";
    
    private PropertyChangeListener propertyChangeListener;

    protected OccurrencesMarkerManager occurrencesMarkerManager;
    protected OccurrencesMarkerManager.DefaultConfiguration defaultOccurrenceMarkerManagerConfiguration;
    
    class PropertyChangeListener implements IPropertyChangeListener{

        /* (non-Javadoc)
         * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent event) {
            //System.out.println("PolishEditor.PropertyChangeListener.propertyChange():event:property:"+event.getProperty()+".event:newValue:"+event.getNewValue());
            handlePreferenceStoreChanged(event);
        }
        
    }
    
    private MeposeModel meposeProject;
    private Environment deviceEnvironment;
    
    public PolishEditor() {
        this.propertyChangeListener = new PropertyChangeListener();
        this.occurrencesMarkerManager = new OccurrencesMarkerManager();
        this.defaultOccurrenceMarkerManagerConfiguration = new OccurrencesMarkerManager.DefaultConfiguration();
        }


    public void createPartControl(Composite parent){
        
        // This one origins from JavaEditor and is a chain of 4 stores.
        IPreferenceStore preferenceStoreJava = this.getPreferenceStore();
        IPreferenceStore preferenceStorePolish = PolishEditorPlugin.getDefault().getPreferenceStore();

        preferenceStorePolish.addPropertyChangeListener(this.propertyChangeListener);
   
        IPreferenceStore[] preferenceStores = new IPreferenceStore[2];
        preferenceStores[0] = preferenceStoreJava;
        preferenceStores[1] = preferenceStorePolish;

        // We are forced to do this kludge because all private methods of suberclasses uses this one preferenceStore.
        // We do need write support so we give others this chainPreferenceStore and when we edit some values
        // we get our own store from the plugin. And thus hardwiring as unforgivable like all others.
        IPreferenceStore preferenceStore = new ChainedPreferenceStore(preferenceStores);
        
        // No need to get the ColorManager from somewhere else because there it is also simply instantiated.
        IColorManager colorManager = new JavaColorManager();
       
        PolishSourceViewerConfiguration polishSourceViewerConfiguration;
        polishSourceViewerConfiguration =  new PolishSourceViewerConfiguration(colorManager,preferenceStore,this,IJavaPartitions.JAVA_PARTITIONING);
        setSourceViewerConfiguration(polishSourceViewerConfiguration);
        
//        this.meposeProject = MeposePlugin.getDefault().getMeposeModelFromResource(((PolishSourceViewerConfiguration)getSourceViewerConfiguration()).getProject().getProject());
        this.meposeProject = MeposePlugin.getDefault().getMeposeModelManager().getModel(getJavaProject().getProject());
        
        super.createPartControl(parent);
    }
   
    protected boolean affectsTextPresentation(PropertyChangeEvent event) {
        PolishSourceViewerConfiguration polishSourceViewerConfiguration = (PolishSourceViewerConfiguration)getSourceViewerConfiguration();
        if(polishSourceViewerConfiguration.affectsTextPresentation(event)) {
            return true;
        }
        return super.affectsTextPresentation(event);
    }
    
    protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
        //System.out.println("PolishEditor.handlePreferenceStoreChanged(...):enter.");
        // Here should a change from mark occurences be placed. If e.g. only derectives should be marked.
        super.handlePreferenceStoreChanged(event);
    }
    
    
    protected void updateOccurrenceAnnotations(ITextSelection selection, CompilationUnit astRoot) {
        if(selection == null){
            System.out.println("ERROR:PolishEditor.updateOccurrenceAnnotations(...):Parameter 'selection'is null.");
            return;
        }
        if(astRoot == null){
            System.out.println("ERROR:PolishEditor.updateOccurrenceAnnotations(...):Parameter 'astRoot' is null.");
            return;
        }
        List listOfComments = astRoot.getCommentList(); //Maybe the ast doesnt get freed.
      
        this.occurrencesMarkerManager.updateAnnotations(selection,listOfComments);
        
        super.updateOccurrenceAnnotations(selection, astRoot);
    }
    
    
    // Use this mechanism to get informed about install and uninstall.
    protected void installOccurrencesFinder() {
        //System.out.println("PolishEditor.installOccurrencesFinder().enter");
        ISourceViewer sourceViewer = getSourceViewer();
        if(sourceViewer == null) {
            System.out.println("ERROR:PolishEditor.installOccurrencesFinder():sourceViewer is null.");
            return;
        }
        // Reset the Marker Manager, maybe something has changed.
        this.defaultOccurrenceMarkerManagerConfiguration.setAnnotationModel(sourceViewer.getAnnotationModel());
        this.defaultOccurrenceMarkerManagerConfiguration.setDocument(sourceViewer.getDocument());
        this.occurrencesMarkerManager.configure(this.defaultOccurrenceMarkerManagerConfiguration);
        super.installOccurrencesFinder();
    }
    protected void uninstallOccurrencesFinder() {
        //System.out.println("PolishEditor.uninstallOccurrencesFinder().enter");
        this.occurrencesMarkerManager.removeAnnotations(); // Seems not to work as it complains about beeing not configured.
        super.uninstallOccurrencesFinder();
    }

    /**
     * @return Returns the deviceEnvironment.
     */
    public Environment getDeviceEnvironment() {
        return this.deviceEnvironment;
    }

    
    public MeposeModel getMeposeProject() {
        return this.meposeProject;
    }



    public void setMeposeProject(MeposeModel meposeProject) {
        this.meposeProject = meposeProject;
    }



    /**
     * @param deviceEnvironment The deviceEnvironment to set.
     */
    public void setDeviceEnvironment(Environment deviceEnvironment) {
        if(deviceEnvironment == null){
            throw new IllegalArgumentException("ERROR:PolishEditor.setDeviceEnvironment(...):Parameter 'deviceEnvironment' is null.");
        }
        this.deviceEnvironment = deviceEnvironment;
    }
    
    public IJavaProject getJavaProject() {
        
        IJavaElement element= null;
        IEditorInput input= getEditorInput();
        IDocumentProvider provider= getDocumentProvider();
        if (provider instanceof ICompilationUnitDocumentProvider) {
            ICompilationUnitDocumentProvider cudp= (ICompilationUnitDocumentProvider) provider;
            element= cudp.getWorkingCopy(input);
        } else if (input instanceof IClassFileEditorInput) {
            IClassFileEditorInput cfei= (IClassFileEditorInput) input;
            element= cfei.getClassFile();
        }
        
        if (element == null)
            return null;
        
        return element.getJavaProject();
    }
    
}
