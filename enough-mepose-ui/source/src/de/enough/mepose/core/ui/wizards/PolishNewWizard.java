package de.enough.mepose.core.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import de.enough.mepose.core.model.BuildXMLWriter;
import de.enough.mepose.core.model.MeposeModel;


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
    
    public static Logger logger = Logger.getLogger(PolishNewWizard.class);
    
    private NewProjectModel newProjectModel;
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
        this.newProjectModel = new NewProjectModel(new MeposeModel());
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
        this.projectPage = new ProjectPage(this.newProjectModel);
        this.pathsPage = new PathsPage(this.newProjectModel);
        this.platformPage = new PlatformPage(this.newProjectModel);
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
        createBuildXML();
        makeJavaProject();
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
     * 
     */
    private void makeJavaProject() {
        try {
            this.javaSettingsPage.configureJavaProject(null);
        } catch (CoreException exception) {
            // TODO rickyn handle CoreException
            exception.printStackTrace();
        } catch (InterruptedException exception) {
            // TODO rickyn handle InterruptedException
            exception.printStackTrace();
        }
    }

    private void createBuildXML() {
        BuildXMLWriter buildXMLWriter = new BuildXMLWriter((MeposeModel)this.newProjectModel.getPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_MEPOSEMODEL));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(byteArrayOutputStream);
        buildXMLWriter.writeBuildXML(writer);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        
        IProject project = (IProject)this.newProjectModel.getPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_INSTANCE);
        if(project == null) {
            throw new IllegalStateException("No project instance to generate build.xml in.");
        }
        IFile file = project.getFile("build.xml");
        try {
            if(file.exists()) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                StringBuffer sb = new StringBuffer();
                sb.append(c.get(Calendar.YEAR));
                sb.append(c.get(Calendar.MONTH));
                sb.append(c.get(Calendar.DAY_OF_WEEK_IN_MONTH));
                sb.append(":");
                sb.append(c.get(Calendar.HOUR));
                sb.append(c.get(Calendar.MINUTE));
                sb.append(c.get(Calendar.SECOND));
                IPath path = new Path("build."+sb.toString()+".xml");
                file.move(path,true,true,new NullProgressMonitor());
            }
            file = project.getFile("build.xml");
            file.create(inputStream,true,null);
        } catch (CoreException exception) {
            System.out.println("DEBUG;PolishNewWizard.createBuildXML(...):could not create file:"+exception);
            return;
        }
    }
    
    public void init(IWorkbench workbench, IStructuredSelection newSelection) {
//		this.selection = newSelection;
//        this.newProjectModel.setPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_TOCONVERT, extractProjectFromSelection(newSelection));
	}
    
//    private IProject extractProjectFromSelection(ISelection projectSelection) {
//        if (projectSelection != null && projectSelection.isEmpty() == false
//                && projectSelection instanceof IStructuredSelection) {
//            IStructuredSelection ssel = (IStructuredSelection) projectSelection;
//            if (ssel.size() > 1) {
//                return null;
//            }
//            Object obj = ssel.getFirstElement();
//            if (obj instanceof IJavaProject) {
//                return (IJavaProject)obj;
//            }
//        }
//        return null;
//    }

    public boolean canFinish() {
//        this.newProjectModel.getModelStatus().getType() == Status.TYPE_OK;
        return this.newProjectModel.canFinish();
    }
    
    public boolean performCancel() {
        IProject project = (IProject)this.newProjectModel.getPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_INSTANCE);
        Boolean projectNewlyCreated = ((Boolean)this.newProjectModel.getPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_STATE_CREATED_PROJECT));
        if(project != null && projectNewlyCreated != null && projectNewlyCreated.booleanValue()) {
            try {
                project.delete(true,true,new NullProgressMonitor());
            } catch (CoreException exception) {
                if(logger.isDebugEnabled()) {
                    logger.error("Could not remove project:"+exception);
                }
            }
        }
        return super.performCancel();
    }
    
    
    
}