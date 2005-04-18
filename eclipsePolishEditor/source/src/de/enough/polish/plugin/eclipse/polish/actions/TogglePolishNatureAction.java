/*
 * Created on 11.04.2005
 */
package de.enough.polish.plugin.eclipse.polish.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import de.enough.polish.plugin.eclipse.polishEditor.PolishEditorPlugin;

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
            try {
                if(this.selectedProject.hasNature(PolishEditorPlugin.POLISH_NATURE_ID)) {
                    this.projecthasPolishNature = true;
                }
                else {
                    this.projecthasPolishNature = false;
                }
                if(this.projecthasPolishNature) {
                    // This is unrelable because maybe the nature could not be removed.
                    PolishEditorPlugin.removePolishNatureFromProject(this.selectedProject);
                    this.projecthasPolishNature = false;
                }
                else {
                    PolishEditorPlugin.addPolishNatureToProject(this.selectedProject);
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
                this.selectedProject = ((IJavaProject)structuredSelection.toList().get(0)).getProject();
            }
        }
        catch(Exception exception){
            System.out.println("ERROR:TogglePolishNatureAction.selectionChanged(...):Parameter selection is not an IProject.selection:class:"+exception);
            this.selectedProject = null;
        }
       
    }

}
