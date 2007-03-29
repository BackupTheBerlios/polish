/*
 * PolishDataEditorViewFactory.java
 *
 * Created on March 27, 2007, 11:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;

import org.netbeans.api.project.Project;
import org.netbeans.modules.vmd.api.io.DataEditorView;
import org.netbeans.modules.vmd.api.io.DataEditorViewFactory;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.modules.vmd.api.io.ProjectUtils;
import org.netbeans.modules.vmd.midp.components.MidpDocumentSupport;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;

/**
 *
 * @author dave
 */
public class PolishDataEditorViewFactory implements DataEditorViewFactory {
    
    private static final String PROP_USE_POLISH_PROJECT = "polish.project.useJ2MEPolish"; // NOI18N
    
    public DataEditorView createEditorView(DataObjectContext context) {
        if (MidpDocumentSupport.PROJECT_TYPE_MIDP.equals (context.getProjectType())  &&  isPolishProjectUsed (context))
            return new PolishDataEditorView (context);
        return null;
    }
    
    private static boolean isPolishProjectUsed (DataObjectContext context) {
        Project project = ProjectUtils.getProject(context);
        if (project == null)
            return false;
        AntProjectHelper helper = project.getLookup ().lookup (AntProjectHelper.class);
        if (helper == null)
            return false;
        EditableProperties ep = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);
        String value = ep.getProperty (PROP_USE_POLISH_PROJECT);
        return "true".equalsIgnoreCase(value)  ||  "yes".equalsIgnoreCase(value);
    }

}
