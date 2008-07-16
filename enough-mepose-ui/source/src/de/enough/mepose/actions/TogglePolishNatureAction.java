/*
 * Created on 11.04.2005
 */
package de.enough.mepose.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import de.enough.mepose.core.MeposeConstants;
import de.enough.mepose.ui.MeposeUIPlugin;
import de.enough.swt.widgets.DialogUtils;

/**
 * @author rickyn
 */
public class TogglePolishNatureAction implements IObjectActionDelegate{

    private IProject selectedProject;

    public TogglePolishNatureAction() {
        System.out.println("ENTER.");
    }
    
    public void setActivePart(IAction action, IWorkbenchPart currentTargetPart) {
        System.out.println("DEBUG:TogglePolishNatureAction.setActivePart(...):enter.");
    }

    public void run(final IAction action) {
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true,false,new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) {
                    doRun(action, monitor);
                }
            });
        } catch (InvocationTargetException exception) {
            MeposeUIPlugin.log("Could not run togglePolishNatureAction",exception);
        } catch (InterruptedException exception) {
            MeposeUIPlugin.log("Could not run togglePolishNatureAction",exception);
        }
    }

    protected void doRun(IAction action,IProgressMonitor monitor) {
        monitor.beginTask("Toggling Polish Nature",IProgressMonitor.UNKNOWN);
        
        if(this.selectedProject == null || ! this.selectedProject.isOpen()) {
            monitor.done();
            return;
        }
        
        try {
            if(this.selectedProject.hasNature(MeposeConstants.ID_NATURE)) {
                removePolishNatureFromProject(this.selectedProject);
            }
            else {
                addPolishNatureToProject(this.selectedProject,monitor);
            }
        } catch (CoreException e) {
            e.printStackTrace();
//            DialogUtils.showErrorBox("Toggle J2ME Polish Nature","Could not toggle nature because of '"+e+"'");
        } finally {
            monitor.done();
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if( ! (selection instanceof IStructuredSelection)){
            return;
        }
//      What a stupid cast. Who comes up with a malformed interface like ISelection??
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        try{
            if(structuredSelection.size() > 0){
                Object object = structuredSelection.getFirstElement();
                if(object instanceof IAdaptable) {
                    IAdaptable adaptable = (IAdaptable) object;
                    this.selectedProject = (IProject)adaptable.getAdapter(IProject.class);
                }
            }
        }
        catch(Exception exception){
            this.selectedProject = null;
        }
    }
    
    private void addPolishNatureToProject(IProject project,IProgressMonitor monitor) throws CoreException {
        if(project.hasNature(MeposeConstants.ID_NATURE)){
            return;
        }
        IProjectDescription description = project.getDescription();
        String[] ids = description.getNatureIds();
        String[] newIds = new String[ids.length+1];
        newIds[0] = MeposeConstants.ID_NATURE;
        System.arraycopy(ids,0,newIds,1,ids.length);
        description.setNatureIds(newIds);
        project.setDescription(description,monitor);
    }

    private void removePolishNatureFromProject(IProject project) throws CoreException{
        if( ! project.hasNature(MeposeConstants.ID_NATURE)){
            return;
        }
        IProjectDescription description = project.getDescription();
        String[] ids = description.getNatureIds();
        for(int i = 0; i < ids.length; i++) {
            if(ids[i].equals(MeposeConstants.ID_NATURE)) {
                String[] newIds = new String[ids.length-1];
                System.arraycopy(ids,0,newIds,0,i);
                System.arraycopy(ids,i+1,newIds,i,ids.length-i-1);
                description.setNatureIds(newIds);
                project.setDescription(description,new NullProgressMonitor());
                return;
            }
        }
    }
}
