/*
 * Created on 11-Apr-2005
 */
package de.enough.polish.plugin.eclipse.polish;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.EditorRegistry;

import de.enough.polish.plugin.eclipse.polishEditor.editor.PolishEditor;



/**
 * @author rickyn
 */
public class PolishNature implements IProjectNature {

    private static final String JAVA_EXTENSION = "*.java";
    IProject project;
    private String previousDefaultEditorForJavaFiles = "";
    
    private EditorRegistry registry = (EditorRegistry)WorkbenchPlugin.getDefault().getEditorRegistry();
    
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProjectNature#configure()
     */
    public void configure() throws CoreException {
        System.out.println("PolishNature.configure():enter.");
        // Error handling should be in place. Do not rely on anything here.
        this.previousDefaultEditorForJavaFiles = this.registry.getDefaultEditor(JAVA_EXTENSION).getId();
        this.registry.setDefaultEditor(JAVA_EXTENSION,PolishEditor.ID);
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProjectNature#deconfigure()
     */
    public void deconfigure() throws CoreException {
        System.out.println("PolishNature.deconfigure():enter.");
        this.registry.setDefaultEditor(JAVA_EXTENSION,this.previousDefaultEditorForJavaFiles);
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
