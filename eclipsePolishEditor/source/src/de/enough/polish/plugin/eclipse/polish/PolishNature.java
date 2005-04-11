/*
 * Created on 11-Apr-2005
 */
package de.enough.polish.plugin.eclipse.polish;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;



/**
 * @author rickyn
 */
public class PolishNature implements IProjectNature {

    IProject project;
    
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProjectNature#configure()
     */
    public void configure() throws CoreException {
        System.out.println("PolishNature.configure():enter.");
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProjectNature#deconfigure()
     */
    public void deconfigure() throws CoreException {
        System.out.println("PolishNature.deconfigure():enter.");
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProjectNature#getProject()
     */
    public IProject getProject() {
        return this.project;
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
     */
    public void setProject(IProject project) {
        this.project = project;        
    }

}
