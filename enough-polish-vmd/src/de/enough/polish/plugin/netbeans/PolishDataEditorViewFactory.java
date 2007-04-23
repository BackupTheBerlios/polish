/*
 * PolishDataEditorViewFactory.java
 *
 * Created on March 27, 2007, 11:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;

import de.enough.polish.plugin.netbeans.project.PolishProjectSupport;
import org.netbeans.modules.vmd.api.io.DataEditorView;
import org.netbeans.modules.vmd.api.io.DataEditorViewFactory;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.modules.vmd.api.io.ProjectUtils;
import org.netbeans.modules.vmd.midp.components.MidpDocumentSupport;

/**
 *
 * @author dave
 */
public class PolishDataEditorViewFactory implements DataEditorViewFactory {
    
    public DataEditorView createEditorView(DataObjectContext context) {
        if (MidpDocumentSupport.PROJECT_TYPE_MIDP.equals (context.getProjectType())  &&  PolishProjectSupport.isPolishProject (ProjectUtils.getProject(context)))
            return new PolishDataEditorView (context);
        return null;
    }
    
}
