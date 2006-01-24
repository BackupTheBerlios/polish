/*
 * Created on 11-Apr-2005
 */
package de.enough.mepose.core.project;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import de.enough.mepose.core.CorePlugin;



/**
 * 
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        May 27, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishNature implements IProjectNature {

    IProject project;
    
    public void configure() throws CoreException {
        ProjectPersistence p = new ProjectPersistence();
        Map map = p.getMapFromProject(this.project);
        CorePlugin.getDefault().getMeposeModelManager().addModel(this.project,map);
    }

    public void deconfigure() throws CoreException {
        System.out.println("PolishNature.deconfigure():enter.");
        //TODO:remove properties from the project.
    }

    public IProject getProject() {
        return this.project;
        
    }

    public void setProject(IProject project) {
        // TODO: Create a MeposeModel from the project.
        this.project = project;        
    }
}
