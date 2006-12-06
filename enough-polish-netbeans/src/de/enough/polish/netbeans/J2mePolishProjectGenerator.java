/*
 * J2mePolishProjectGenerator.java
 *
 * Created on December 6, 2006, 3:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.enough.polish.netbeans;

import de.enough.polish.ide.swing.DeviceSelector;
import de.enough.polish.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.filesystems.FileObject;

/**
 * A factory for creating J2ME Polish projects.
 * 
 * @author Robert Virkus
 */
public class J2mePolishProjectGenerator {
    
    /** Creates a new instance of J2mePolishProjectGenerator */
    private J2mePolishProjectGenerator() {
        // allow only static access
    }
    
    public static Project generateProjectFromTemplate( String name, File projectDir, File templateDir, 
            DeviceSelector targetDevices, Map buildProperties ) 
    throws IOException 
    {
        FileUtil.copyDirectoryContents( templateDir, projectDir, false);
        //ProjectManager manager = ProjectManager.getDefault();
        //FileObject projectFileObject = org.openide.filesystems.FileUtil.toFileObject(projectDir);
        //Project project = manager.findProject( projectFileObject );
        //return project;
        return null;
    }
    
}
