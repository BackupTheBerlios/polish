package de.enough.mepose.core.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;


/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "mpe". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class PolishNewWizard extends Wizard implements INewWizard {
//	private FirstWizardPage firstPage;
//	private WizardPage firstPage;
    private ISelection selection;
    private NewPolishProjectDAO newPolishProjectDAO;
    private PathsPage pathsPage;
    private PlatformPage platformPage;
    private ProjectPage projectPage;
    private JavaCapabilityConfigurationPage javaSettingsPage;

	/**
	 * Constructor for PolishNewWizard.
	 */
	public PolishNewWizard() {
		super();
		setNeedsProgressMonitor(true);
        this.newPolishProjectDAO = new NewPolishProjectDAO();
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
//	    this.firstPage = new FirstWizardPage(this.newProjectOptions);
        this.projectPage = new ProjectPage(this.newPolishProjectDAO);
        this.pathsPage = new PathsPage(this.newPolishProjectDAO);
        this.platformPage = new PlatformPage(this.newPolishProjectDAO);
        this.javaSettingsPage = new JavaCapabilityConfigurationPage();
        addPage(this.projectPage);
		addPage(this.pathsPage);
        addPage(this.platformPage);
        addPage(this.javaSettingsPage);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
//		final String containerName = firstPage.getContainerName();
//		final String fileName = firstPage.getFileName();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
			    try{
                    doFinish(monitor);
                }
                catch(Exception exception) {
                    throw new InvocationTargetException(exception);
                }
                finally {
                    monitor.done();
                }
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
            MessageDialog.openError(getShell(), "Error", "The operation was interrupted.");
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	protected void doFinish(IProgressMonitor monitor){
        
        // Sanity check
        // Create IProject
        // call JavaCapabilityConfigurationPage.createJavaProject
        // generate build.xml
        
		// create a sample file
//		monitor.beginTask("Creating " + fileName, 2);
//		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//		IResource resource = root.findMember(new Path(containerName));
//		if (!resource.exists() || !(resource instanceof IContainer)) {
//			throwCoreException("Container \"" + containerName + "\" does not exist.");
//		}
//		IContainer container = (IContainer) resource;
//		final IFile file = container.getFile(new Path(fileName));
//		try {
//			InputStream stream = openContentStream();
//			if (file.exists()) {
//				file.setContents(stream, true, true, monitor);
//			} else {
//				file.create(stream, true, monitor);
//			}
//			stream.close();
//		} catch (IOException e) {
//		}
//		monitor.worked(1);
//		monitor.setTaskName("Opening file for editing...");
//		getShell().getDisplay().asyncExec(new Runnable() {
//			public void run() {
//				IWorkbenchPage page =
//					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
////				try {
//////					IDE.openEditor(page, file, true);
////				} catch (PartInitException e) {
////				}
//			}
//		});
//		monitor.worked(1);
	}
	
	/**
	 * We will initialize file contents with a sample text.
	 */

//	private InputStream openContentStream() {
//		String contents =
//			"This is the initial file contents for *.mpe file that should be word-sorted in the Preview page of the multi-page editor";
//		return new ByteArrayInputStream(contents.getBytes());
//	}
//
//	private void throwCoreException(String message) throws CoreException {
//		IStatus status =
//			new Status(IStatus.ERROR, "de.enough.mepose.ui.UIPlugin", IStatus.OK, message, null);
//		throw new CoreException(status);
//	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection newSelection) {
		this.selection = newSelection;
        this.newPolishProjectDAO.setProjectToConvert(extractProjectFromSelection(this.selection));
	}
    
    private IJavaProject extractProjectFromSelection(ISelection projectSelection) {
        if (projectSelection != null && projectSelection.isEmpty() == false
                && projectSelection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) projectSelection;
            if (ssel.size() > 1) {
                return null;
            }
            Object obj = ssel.getFirstElement();
            if (obj instanceof IJavaProject) {
                return (IJavaProject)obj;
            }
        }
        return null;
    }

    public boolean canFinish() {
        return this.newPolishProjectDAO.isBasicallyConfigured();
    }
    
    
}