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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.ICompilationUnitDocumentProvider;
import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;

import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.core.model.MeposeModel;
import de.enough.mepose.core.model.MeposeModelManager;
import de.enough.polish.Environment;
import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;
import de.enough.polish.plugin.eclipse.polishEditor.editor.occurrenceAnnotations.OccurrencesMarkerManager;




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
    
    /**
     * 
     * <br>Copyright Enough Software 2005
     * <pre>
     * history
     *        May 30, 2006 - rickyn creation
     * </pre>
     * @author Richard Nkrumah, Richard.Nkrumah@enough.de
     */
    public class ChangeMeposeModelFocusListener implements IPartListener {

        public void partActivated(IWorkbenchPart part) {
            if(part instanceof PolishEditor) {
                PolishEditor polishEditor = (PolishEditor)part;
                polishEditor.getDeviceDropdownChooserContributionItem().setMeposeModel(polishEditor.getMeposeModel());
            }
        }
        public void partBroughtToTop(IWorkbenchPart part) {
            // TODO rickyn implement partBroughtToTop
        }
        public void partClosed(IWorkbenchPart part) {
            // TODO rickyn implement partClosed

        }
        public void partDeactivated(IWorkbenchPart part) {
            // TODO rickyn implement partDeactivated

        }
        public void partOpened(IWorkbenchPart part) {
            // TODO rickyn implement partOpened

        }
        protected void updateChooser() {
            IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            if(activeEditor instanceof PolishEditor) {
                PolishEditor polishEditor = (PolishEditor)activeEditor;
                polishEditor.getDeviceDropdownChooserContributionItem().setMeposeModel(polishEditor.getMeposeModel());
            }
        }
    }
    
    public static final String ID = "de.enough.polish.plugin.eclipse.polishEditor.editor.PolishEditor";
    
    private PropertyChangeListener propertyChangeListener;

    protected OccurrencesMarkerManager occurrencesMarkerManager;
    protected OccurrencesMarkerManager.DefaultConfiguration defaultOccurrenceMarkerManagerConfiguration;
    
    class PropertyChangeListener implements IPropertyChangeListener{

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
        MeposeModelManager meposeModelManager = MeposePlugin.getDefault().getMeposeModelManager();
        IProject project = getJavaProject().getProject();
        this.meposeProject = meposeModelManager.getModel(project);
        if(this.meposeProject == null) {
            meposeModelManager.addModel(project,new MeposeModel());
            this.meposeProject = meposeModelManager.getModel(project);
        }
        
        super.createPartControl(parent);

        // The SemanticHighlightingReconciler is a ReconcileListener which will be called at the editor start
        // and which will erase everything the normal reconciler has done so far.
        
        Field semanticManagerField;
        try {
            semanticManagerField = getClass().getSuperclass().getSuperclass().getDeclaredField("fSemanticManager");
            semanticManagerField.setAccessible(true);
            Object semanticManager = semanticManagerField.get(this);
            
            Field presenterField = semanticManager.getClass().getDeclaredField("fPresenter");
            presenterField.setAccessible(true);
            Object presenter = presenterField.get(semanticManager);
            
            Field presentationReconcilerField = presenter.getClass().getDeclaredField("fPresentationReconciler");
            presentationReconcilerField.setAccessible(true);
            Object presentationReconciler = presentationReconcilerField.get(presenter);
            
            Field damagersField = presentationReconciler.getClass().getSuperclass().getDeclaredField("fDamagers");
            Field repairersField = presentationReconciler.getClass().getSuperclass().getDeclaredField("fRepairers");
            damagersField.setAccessible(true);
            repairersField.setAccessible(true);
            Map damagers = (Map)damagersField.get(presentationReconciler);
            Map repairers = (Map)repairersField.get(presentationReconciler);
            
            RuleBasedScanner polishLineScanner = ((PolishSourceViewerConfiguration)getSourceViewerConfiguration()).getSinglelineCommentScanner();
            Map[] maps = new Map[] {damagers,repairers};
            for (int i = 0; i < maps.length; i++) {
                Map map = maps[i];
                Object damagerRepairer = map.get("__java_singleline_comment");
                Field scannerField = damagerRepairer.getClass().getDeclaredField("fScanner");
                scannerField.setAccessible(true);
                scannerField.set(damagerRepairer,polishLineScanner);
            }
            
        } catch (SecurityException exception) {
            // TODO rickyn handle SecurityException
            exception.printStackTrace();
        } catch (IllegalArgumentException exception) {
            // TODO rickyn handle IllegalArgumentException
            exception.printStackTrace();
        } catch (NoSuchFieldException exception) {
            // TODO rickyn handle NoSuchFieldException
            exception.printStackTrace();
        } catch (IllegalAccessException exception) {
            // TODO rickyn handle IllegalAccessException
            exception.printStackTrace();
        }

        IToolBarManager toolBarManager = getEditorSite().getActionBars().getToolBarManager();

        IContributionItem contributionItem = getDeviceDropdownChooserContributionItem();
        
        DeviceDropdownChooserContributionItem deviceDropdownChooserContributionItem;
        if(contributionItem == null) {
            deviceDropdownChooserContributionItem = new DeviceDropdownChooserContributionItem(getMeposeModel());
            toolBarManager.add(deviceDropdownChooserContributionItem);
        }
        getSite().getPage().addPartListener(new ChangeMeposeModelFocusListener());
//        IWorkbench workbench = PlatformUI.getWorkbench();
//        IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
//        IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
//        activePage.addPartListener();
    }
   
    public DeviceDropdownChooserContributionItem getDeviceDropdownChooserContributionItem() {
        IToolBarManager toolBarManager = getEditorSite().getActionBars().getToolBarManager();
        return (DeviceDropdownChooserContributionItem)toolBarManager.find(DeviceDropdownChooserContributionItem.class.getName());
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
        if(astRoot == null) {
            return;
        }
        
        List listOfComments = astRoot.getCommentList(); //Maybe the ast doesnt get freed.
      
        this.occurrencesMarkerManager.updateAnnotations(selection,listOfComments);
        
        super.updateOccurrenceAnnotations(selection, astRoot);
    }
    
    public Method findMethod(Class start, String methodName, Class[] args) {
        Method firstDeclared = null;
        for (Class current = start.getSuperclass(); current != Object.class && firstDeclared == null; current = current.getSuperclass()) {
            try {
                firstDeclared = current.getDeclaredMethod(methodName, args);
            } catch (Exception e) {
                // totally ignore this... we don't give a shit if the method is not there
            }
        }
        return firstDeclared;
    }
    
    protected void installOccurrencesFinder() {
        doInstallOccurrencesFinder();
        super.installOccurrencesFinder();
    }
    
    
    // Use this mechanism to get informed about install and uninstall.
    protected void installOccurrencesFinder(boolean force) {
        doInstallOccurrencesFinder();
        super.installOccurrencesFinder(force);
    }
    
    private void doInstallOccurrencesFinder() {
        ISourceViewer sourceViewer = getSourceViewer();
        if(sourceViewer == null) {
            System.out.println("ERROR:PolishEditor.installOccurrencesFinder():sourceViewer is null.");
            PolishEditorPlugin.log("installOccurrencesFinder():sourceViewer is null.");
            return;
        }
        // Reset the Marker Manager, maybe something has changed.
        this.defaultOccurrenceMarkerManagerConfiguration.setAnnotationModel(sourceViewer.getAnnotationModel());
        this.defaultOccurrenceMarkerManagerConfiguration.setDocument(sourceViewer.getDocument());
        this.occurrencesMarkerManager.configure(this.defaultOccurrenceMarkerManagerConfiguration);
    }
    
    protected void uninstallOccurrencesFinder() {
        this.occurrencesMarkerManager.removeAnnotations(); // Seems not to work as it complains about beeing not configured.
        super.uninstallOccurrencesFinder();
    }

    public Environment getDeviceEnvironment() {
        return this.deviceEnvironment;
    }

    
    public MeposeModel getMeposeModel() {
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
