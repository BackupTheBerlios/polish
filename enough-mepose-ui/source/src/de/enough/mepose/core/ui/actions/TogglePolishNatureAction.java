/*
 * Created on 11.04.2005
 */
package de.enough.mepose.core.ui.actions;

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

import de.enough.mepose.core.MeposeCoreConstants;
import de.enough.mepose.core.ui.plugin.UIPluginActivator;

/**
 * @author rickyn
 */
public class TogglePolishNatureAction implements IObjectActionDelegate{

    private IProject selectedProject;
    private boolean projecthasPolishNature;
    

    public void setActivePart(IAction action, IWorkbenchPart currentTargetPart) {
        //this.proxyAction = action;
        //this.targetPart = currentTargetPart;
    }

    public void run(final IAction action) {
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true,false,new IRunnableWithProgress() {

                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    doRun(action, monitor);
                }
            });
        } catch (InvocationTargetException exception) {
            UIPluginActivator.log("Could not run togglePolishNatureAction",exception);
        } catch (InterruptedException exception) {
            UIPluginActivator.log("Could not run togglePolishNatureAction",exception);
        }
    }

    protected void doRun(IAction action,IProgressMonitor monitor) {
        monitor.beginTask("Toggling Polish Nature",IProgressMonitor.UNKNOWN);
        System.out.println("TogglePolishNatureAction.run(...).enter.");
        if(this.selectedProject != null){
            if( ! this.selectedProject.isOpen()){
                System.out.println("ERROR:TogglePolishNatureAction.run(...):project not open.");
                return;
            }
            try {
                if(this.selectedProject.hasNature(MeposeCoreConstants.ID_NATURE)) {
                    monitor.subTask("Polish Nature found");
                    this.projecthasPolishNature = true;
                }
                else {
                    monitor.subTask("No Polish Nature found");
                    this.projecthasPolishNature = false;
                }
                if(this.projecthasPolishNature) {
                    monitor.subTask("About to remove Polish Nature");
                    removePolishNatureFromProject(this.selectedProject);
                    monitor.subTask("Polish Nature removed.");
                    this.projecthasPolishNature = false;
                }
                else {
                    monitor.subTask("About to install Polish Nature.");
                    addPolishNatureToProject(this.selectedProject,monitor);
                    monitor.subTask("Polish Nature installed.");
                    this.projecthasPolishNature = true;
                }
                
            } catch (CoreException e) {
                // Give a nice explanation to this silly coreexception.
                e.printStackTrace();
            } finally {
                monitor.done();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        if( ! (selection instanceof IStructuredSelection)){
            System.out.println("ERROR:TogglePolishNatureAction.selectionChanged(...):Parameter selection has wrong type.selection:class:"+selection.getClass());
            return;
        }
        IStructuredSelection structuredSelection = (IStructuredSelection) selection; // What a stupid cast. Who comes up with a malformed interface like ISelection??
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
            System.out.println("ERROR:TogglePolishNatureAction.selectionChanged(...):Parameter selection is not an IProject.selection:class:"+exception);
            this.selectedProject = null;
        }
       
    }
    public void addPolishNatureToProject(IProject project,IProgressMonitor monitor) throws CoreException {
        
        if(project.hasNature(MeposeCoreConstants.ID_NATURE)){
            System.out.println("ERROR:PolishEditorPlugin.addPolishNatureToProject(...):Project has Polish nature already.");
            return;
        }
        IProjectDescription description = project.getDescription();
        String[] ids = description.getNatureIds();
        String[] newIds = new String[ids.length+1];
        newIds[0] = MeposeCoreConstants.ID_NATURE;
        System.arraycopy(ids,0,newIds,1,ids.length);
        description.setNatureIds(newIds);
        project.setDescription(description,monitor);
    }

    public void removePolishNatureFromProject(IProject project) throws CoreException{
        
        if( ! project.hasNature(MeposeCoreConstants.ID_NATURE)){
            System.out.println("ERROR:PolishEditorPlugin.removePolishNatureFromProject(...):Project has no Polish nature.");
            return;
        }
        
        IProjectDescription description = project.getDescription();
        String[] ids = description.getNatureIds();
        for(int i = 0; i < ids.length; i++) {
            if(ids[i].equals(MeposeCoreConstants.ID_NATURE)) {
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
