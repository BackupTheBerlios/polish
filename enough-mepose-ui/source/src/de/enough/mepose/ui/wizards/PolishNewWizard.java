package de.enough.mepose.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.core.model.BuildXMLWriter;
import de.enough.mepose.core.model.MeposeModel;


public class PolishNewWizard extends Wizard implements INewWizard {
    
    public static Logger logger = Logger.getLogger(PolishNewWizard.class);
    
    private NewProjectModel newProjectModel;
    private PathsPage pathsPage;
    private PlatformPage platformPage;
    private ProjectPage projectPage;
    private JavaCapabilityConfigurationPage javaSettingsPage;

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
        // Do not hand over the monitor as others are going to call beginTask
        // which must be called only once.
        makeJavaProject();
        registerMeposeModel();
	}

    private void registerMeposeModel() {
        MeposePlugin.getDefault().getMeposeModelManager().addModel(this.newProjectModel.getProject(),this.newProjectModel.getMeposeModel());
    }

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
        
//        IProject project = (IProject)this.newProjectModel.getPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_INSTANCE);
        IProject project = this.newProjectModel.getProject();
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
        IPath path = file.getRawLocation();
        File f = path.toFile();
        this.newProjectModel.getMeposeModel().setBuildxml(f);
    }
    
    public void init(IWorkbench workbench, IStructuredSelection newSelection) {
        //
	}
    
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
                MessageDialog.openError(getShell(), "Error", "Could not delete project:"+project.getName());
                return false;
            }
        }
        return super.performCancel();
    }
    
    
    
}