/*
 * Created on 11-Apr-2005
 */
package de.enough.polish.plugin.eclipse.polish;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorDescriptor;
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
    private String defaultJavaEditor = "org.eclipse.jdt.ui.CompilationUnitEditor";
    
    private EditorRegistry registry = (EditorRegistry)WorkbenchPlugin.getDefault().getEditorRegistry();
    
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProjectNature#configure()
     */
    public void configure() throws CoreException {
        System.out.println("PolishNature.configure():enter.");
        // Error handling should be in place. Do not rely on anything here.
        /*
        IEditorDescriptor editorDescriptor = this.registry.getDefaultEditor(JAVA_EXTENSION);
        if( editorDescriptor == null) {
            this.previousDefaultEditorForJavaFiles = this.defaultJavaEditor;
        }
        else {
            this.previousDefaultEditorForJavaFiles = editorDescriptor.getId();
        }
        this.registry.setDefaultEditor(JAVA_EXTENSION,PolishEditor.ID);
//      this.registry.saveAssociations(); // Do we need this?
 */
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProjectNature#deconfigure()
     */
    public void deconfigure() throws CoreException {
        System.out.println("PolishNature.deconfigure():enter.");
        // Just for the case that we are out of sync.
//        if(PolishEditor.ID.equals(this.previousDefaultEditorForJavaFiles)) {
//            this.previousDefaultEditorForJavaFiles = this.defaultJavaEditor;
//        }
        //this.registry.setDefaultEditor(JAVA_EXTENSION,this.previousDefaultEditorForJavaFiles);
//      this.registry.saveAssociations(); // Do we need this?
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
