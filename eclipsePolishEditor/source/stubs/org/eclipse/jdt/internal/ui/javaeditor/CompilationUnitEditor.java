package org.eclipse.jdt.internal.ui.javaeditor;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaOutlinePage;
import org.eclipse.jdt.internal.ui.text.java.IJavaReconcilingListener;
import org.eclipse.ui.IEditorInput;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jun 7, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class CompilationUnitEditor extends JavaEditor implements IJavaReconcilingListener{

    protected void installOccurrencesFinder(boolean force) {
        
    }
    
    protected void installOccurrencesFinder() {
        
    }
    
    /*
     * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#getElementAt(int)
     */
    protected IJavaElement getElementAt(int offset) {
        // TODO rickyn implement getElementAt
        return null;
    }

    /*
     * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#getCorrespondingElement(org.eclipse.jdt.core.IJavaElement)
     */
    protected IJavaElement getCorrespondingElement(IJavaElement element) {
        // TODO rickyn implement getCorrespondingElement
        return null;
    }

    /*
     * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#setOutlinePageInput(org.eclipse.jdt.internal.ui.javaeditor.JavaOutlinePage, org.eclipse.ui.IEditorInput)
     */
    protected void setOutlinePageInput(JavaOutlinePage page, IEditorInput input) {
        // TODO rickyn implement setOutlinePageInput
        
    }

    /*
     * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#getInputJavaElement()
     */
    protected IJavaElement getInputJavaElement() {
        // TODO rickyn implement getInputJavaElement
        return null;
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.IJavaReconcilingListener#aboutToBeReconciled()
     */
    public void aboutToBeReconciled() {
        // TODO rickyn implement aboutToBeReconciled
        
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.IJavaReconcilingListener#reconciled(org.eclipse.jdt.core.dom.CompilationUnit, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void reconciled(CompilationUnit ast, boolean forced, IProgressMonitor progressMonitor) {
        // TODO rickyn implement reconciled
        
    }


}
