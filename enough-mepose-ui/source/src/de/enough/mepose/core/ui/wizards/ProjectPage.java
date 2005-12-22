package de.enough.mepose.core.ui.wizards;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.enough.mepose.MeposeCoreUIConstants;
import de.enough.mepose.core.model.MeposeModel;
import de.enough.mepose.core.ui.project.PropertyConstants;
import de.enough.swt.widgets.StatusGroup;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class ProjectPage extends WizardPage{

    public static final int OK = 10;
    public static Logger logger = Logger.getLogger(ProjectPage.class);
    
    private NewProjectModel newProjectModel;
    private Text newProjectNameText;
    private Label newProjectNameLabel;
    private StatusGroup newProjectNameStatusGroup;
    private boolean isReady = false;
    
	public ProjectPage(NewProjectModel newProjectOptions) {
		super("wizardPage");
        if(newProjectOptions == null){
            logger.error("Parameter 'newProjectOptions' is null contrary to API.");
            return;
        }
        this.newProjectModel = newProjectOptions;
		setTitle("Create a J2ME Polish Project");
		setDescription("Create a new J2ME Polish Project or convert an existing one.");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 9;
		container.setLayout(layout);
        
        this.newProjectNameStatusGroup = new StatusGroup(container,SWT.NONE);
        this.newProjectNameStatusGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        
        Composite myGroupMainComposite = this.newProjectNameStatusGroup.getMainComposite();
        myGroupMainComposite.setLayout(new GridLayout(2,false));
        
        Group statusGroupGroup = this.newProjectNameStatusGroup.getGroup();
        statusGroupGroup.setText("New Project");
        
        this.newProjectNameLabel = new Label(myGroupMainComposite,SWT.NONE);
        this.newProjectNameLabel.setText("New Project Name:");
        
        this.newProjectNameText = new Text(myGroupMainComposite,SWT.NONE);
//        this.newProjectNameText.setText(this.newProjectOptions.getProjectName());
        this.newProjectNameText.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        this.newProjectNameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                projectNameModified();
            }
        });
        
		Label someLabel = new Label(container,SWT.NONE);
        someLabel.setText("ask the user something");
        
        setControl(container);

        checkGUIState();
	}

//	private void handleBrowse() {
//		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
//				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
//				"Select new file container");
//		if (dialog.open() == ContainerSelectionDialog.OK) {
//			Object[] result = dialog.getResult();
//			if (result.length == 1) {
//				containerText.setText(((Path) result[0]).toString());
//			}
//		}
//	}

//	private void dialogChanged() {
//		IResource container = ResourcesPlugin.getWorkspace().getRoot()
//				.findMember(new Path(getContainerName()));
//		String fileName = getFileName();
//
//		if (getContainerName().length() == 0) {
//			updateStatus("File container must be specified");
//			return;
//		}
//		if (container == null
//				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
//			updateStatus("File container must exist");
//			return;
//		}
//		if (!container.isAccessible()) {
//			updateStatus("Project must be writable");
//			return;
//		}
//		if (fileName.length() == 0) {
//			updateStatus("File name must be specified");
//			return;
//		}
//		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
//			updateStatus("File name must be valid");
//			return;
//		}
//		int dotLoc = fileName.lastIndexOf('.');
//		if (dotLoc != -1) {
//			String ext = fileName.substring(dotLoc + 1);
//			if (ext.equalsIgnoreCase("mpe") == false) {
//				updateStatus("File extension must be \"mpe\"");
//				return;
//			}
//		}
//		updateStatus(null);
//	}

	private void checkGUIState() {
	    projectNameModified();
    }
    
    protected void projectNameModified() {
        String newName = this.newProjectNameText.getText();
        this.newProjectModel.setPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_NAME,newName);
    }

//    private void setStateError(int type, StatusGroup statusGroup, String reason) {
//        switch(type) {
//            case ERROR: statusGroup.setError(reason); this.isReady = false;break;
//            case WARNING: statusGroup.setWarning(reason); this.isReady = true; break;
//            case OK: statusGroup.setOK(reason); this.isReady = true; break;
//            default: return;
//        }
//        getContainer().updateButtons();
//    }
    
    
//    protected void goWizard() {
//        this.isReady = true;
//        setPageComplete(true);
//        
//    }
//
//    protected void noGoWizard() {
//        noGoWizard("");
//    }
//    
//    protected void noGoWizard(String message) {
//        if(message == null){
//            logger.error("Parameter 'message' is null contrary to API.");
//            message = "An Error Occurred";
//        }
//        this.isReady = false;
//        setPageComplete(false);
//    }
    
    protected void enableNewProjectName(boolean enable) {
        this.newProjectNameText.setEnabled(enable);
        this.newProjectNameLabel.setEnabled(enable);
    }

    public IWizardPage getNextPage() {
        try {
            createProject();
            setupProject();
            addNature();
        }
        catch (CoreException exception) {
            if(logger.isDebugEnabled()) {
                logger.error("could not create project:"+exception);
            }
        }
        return super.getNextPage();
    }

    

    private void setupProject() throws CoreException {
        IProject newProject = (IProject)this.newProjectModel.getPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_INSTANCE);
        if(newProject == null) {
            return;
        }
        newProject.open(null);
        String pproperty;
        pproperty = newProject.getPersistentProperty(PropertyConstants.QN_WTK_HOME);
        if(pproperty != null) {
            this.newProjectModel.getMeposeModel().setPropertyValue(MeposeModel.ID_WTK_HOME,new File(pproperty));
        }
        
        pproperty = newProject.getPersistentProperty(PropertyConstants.QN_POLISH_HOME);
        if(pproperty != null) {
            this.newProjectModel.getMeposeModel().setPropertyValue(MeposeModel.ID_POLISH_HOME,new File(pproperty));
        }
        
        pproperty = newProject.getPersistentProperty(PropertyConstants.QN_PLATFORMS_SUPPORTED);
        if(pproperty != null) {
            this.newProjectModel.getMeposeModel().setPropertyValue(MeposeModel.ID_PLATFORMS_SUPPORTED,new File(pproperty));
        }
        
        pproperty = newProject.getPersistentProperty(PropertyConstants.QN_DEVICES_SUPPORTED);
        if(pproperty != null) {
            this.newProjectModel.getMeposeModel().setPropertyValue(MeposeModel.ID_DEVICES_SUPPORTED,new File(pproperty));
        }
        
    }

    private void createProject() throws CoreException {
        IProject project = (IProject)this.newProjectModel.getPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_TOCONVERT);
        if(project != null) {
            // Set the project to convert to the new project.
            this.newProjectModel.setPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_INSTANCE,project);
            this.newProjectModel.setPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_STATE_CREATED_PROJECT,Boolean.FALSE);
        }
        else {
            // Make a new project;
            project = ResourcesPlugin.getWorkspace().getRoot().getProject(this.newProjectNameText.getText());
            project.create(new NullProgressMonitor());
            project.open(null);
            this.newProjectModel.setPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_INSTANCE,project);
            this.newProjectModel.setPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_STATE_CREATED_PROJECT,Boolean.TRUE);
        }
    }
    
    public void addNature() throws CoreException {
        IProject newProject = this.newProjectModel.getNewProject();
        if(newProject == null) {
            return;
        }
        
        IProjectNature nature = newProject.getNature(MeposeCoreUIConstants.ID_NATURE);
        if(nature != null) {
            // Its already a PolishProject.
            return;
        }
        IProjectDescription projectDescription = newProject.getDescription();
        String[] natureIDs = projectDescription.getNatureIds();
        String[] newNatureIDs = new String[natureIDs.length+1];
        System.arraycopy(natureIDs, 0, newNatureIDs, 0, natureIDs.length);
        // As last element.
        newNatureIDs[natureIDs.length] = MeposeCoreUIConstants.ID_NATURE;
        
        projectDescription.setNatureIds(newNatureIDs);
        newProject.setDescription(projectDescription, null);
    }

    public boolean canFlipToNextPage() {
        XXX We need a getModelStatus
       return this.isReady ;
    }
    
    
    

	
}