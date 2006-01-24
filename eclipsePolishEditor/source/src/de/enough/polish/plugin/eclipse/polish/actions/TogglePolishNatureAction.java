/*
 * Created on 11.04.2005
 */
package de.enough.polish.plugin.eclipse.polish.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import de.enough.mepose.core.MeposeCoreConstants;

/**
 * @author rickyn
 */
public class TogglePolishNatureAction implements IObjectActionDelegate{

    //private IAction proxyAction;
    //private IWorkbenchPart targetPart;
    private IProject selectedProject;
    private boolean projecthasPolishNature;
    
    public TogglePolishNatureAction(){
        /*
         * 
         */
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart currentTargetPart) {
       
        //this.proxyAction = action;
        //this.targetPart = currentTargetPart;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        System.out.println("TogglePolishNatureAction.run(...).enter.");
        System.out.println(action.getId());
        if(this.selectedProject != null){
            if( ! this.selectedProject.isOpen()){
                System.out.println("DEBUG:TogglePolishNatureAction.run(...):project not open.");
                return;
            }
            try {
                if(this.selectedProject.hasNature(MeposeCoreConstants.ID_NATURE)) {
                    System.out.println("Found a polish nature.");
                    this.projecthasPolishNature = true;
                }
                else {
                    System.out.println("NO polish nature found.");
                    this.projecthasPolishNature = false;
                }
                if(this.projecthasPolishNature) {
                   System.out.println("About to remove nature.");
                    // This is unrelable because maybe the nature could not be removed.
                    removePolishNatureFromProject(this.selectedProject);
                    System.out.println("nature removed.");
                    this.projecthasPolishNature = false;
                }
                else {
                    System.out.println("about to install nature.");
                    addPolishNatureToProject(this.selectedProject);
                    System.out.println("nature installed.");
                    this.projecthasPolishNature = true;
                }
                
            } catch (CoreException e) {
                // Give a nice explanation to this silly coreexception.
                e.printStackTrace();
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
                System.out.println("selection is of class:"+structuredSelection.toList().get(0).getClass());
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
    public void addPolishNatureToProject(IProject project) throws CoreException {
        
        if(project.hasNature(MeposeCoreConstants.ID_NATURE)){
            System.out.println("ERROR:PolishEditorPlugin.addPolishNatureToProject(...):Project has Polish nature already.");
            return;
        }
        IProjectDescription description = project.getDescription();
        String[] ids = description.getNatureIds();
        String[] newIds = new String[ids.length+1];
        System.arraycopy(ids,0,newIds,0,ids.length);
        newIds[ids.length] = MeposeCoreConstants.ID_NATURE;
        description.setNatureIds(newIds);
        project.setDescription(description,new NullProgressMonitor());
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
