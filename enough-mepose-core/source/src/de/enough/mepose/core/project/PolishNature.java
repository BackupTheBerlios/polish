/*
 * Created on 11-Apr-2005
 */
package de.enough.mepose.core.project;

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import de.enough.mepose.core.CorePlugin;
import de.enough.mepose.core.model.MeposeModel;



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
        System.out.println("DEBUG:PolishNature.configure(...):enter.");
        
//        if(CorePlugin.getDefault().getMeposeModelManager().getModel(this.project) != null) {
            // Everything is fine. We have a model for this project and do not need to configure it.
//            return;
//        }
        ProjectPersistence p = new ProjectPersistence();
        // Search for existing properties.
        Map map = p.getMapFromProject(this.project);
        
        computeInitialProperties(map);
        
        CorePlugin.getDefault().getMeposeModelManager().addModel(this.project,map);
    }

    public void deconfigure() throws CoreException {
        System.out.println("PolishNature.deconfigure():enter.");
        CorePlugin.getDefault().getMeposeModelManager().removeModel(this.project);
        //TODO:remove properties from the project.
    }

    public IProject getProject() {
        return this.project;
        
    }

    public void setProject(IProject project) {
        // TODO: Create a MeposeModel from the project.
        this.project = project;        
    }
    
    private Map computeInitialProperties(Map map) {
        String projectHome = getProject().getLocation().toString();
        map.put(MeposeModel.ID_PATH_PROJECT_FILE,projectHome);
        File buildXmlFile = this.project.getFile("build.xml").getRawLocation().toFile();
        if(buildXmlFile.exists()) {
            map.put(MeposeModel.ID_PATH_BUILDXML_FILE,buildXmlFile.getAbsolutePath());
        }
        return map;
    }
}
