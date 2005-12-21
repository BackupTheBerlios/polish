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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.enough.mepose.MeposeCoreUIConstants;
import de.enough.mepose.core.ui.project.PropertyConstants;
import de.enough.swt.widgets.StatusGroup;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class ProjectPage extends WizardPage{

    public static final int OK = 10;
    
    private NewPolishProjectDAO newPolishProjectModel;
    public static Logger logger = Logger.getLogger(ProjectPage.class);
    private Button yesButton;
    private Button noButton;
    private Text newProjectNameText;
    private Label newProjectNameLabel;
    private StatusGroup newProjectNameStatusGroup;
    private boolean isReady = false;
    
	public ProjectPage(NewPolishProjectDAO newProjectOptions) {
		super("wizardPage");
        if(newProjectOptions == null){
            logger.error("Parameter 'newProjectOptions' is null contrary to API.");
            return;
        }
        this.newPolishProjectModel = newProjectOptions;
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
        
//		dialogChanged();
		checkGUIState();
		setControl(container);
	}

//    protected void actionToggleConvertProject() {
//        this.newProjectOptions.setShouldConvertProject(this.yesButton.getSelection());
//        enableNewProjectName( ! this.newProjectOptions.isShouldConvertProject());
//    }

    /**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

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

	/**
	 * Ensures that both text fields are set.
	 */

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
        String projectName = this.newProjectNameText.getText();
        if(projectName == null || projectName.length() == 0) {
//            noGoWizard("The project name must not be null");
            this.newProjectNameStatusGroup.setError("The project name must not be empty");
            return;
        }
        IResource resource = null;
        try {
            resource = ResourcesPlugin.getWorkspace().getRoot().findMember(projectName);
        } catch (Exception e) {
            System.out.println("ERROR:ProjectPage.projectNameModified(...):"+e);
        }
        if(resource != null) {
            if(resource instanceof IProject) {
                setStateError(WARNING,this.newProjectNameStatusGroup,"Project exists and will be converted");
                this.newPolishProjectModel.setProjectToConvert((IProject)resource);
            }
            else {
                this.newProjectNameStatusGroup.setError("Resource exists");
                this.newPolishProjectModel.setProjectToConvert(null);
            }
        }
        else {
            setStateError(OK,this.newProjectNameStatusGroup,"");
            this.newPolishProjectModel.setProjectToConvert(null);
        }
    }

    private void setStateError(int type, StatusGroup statusGroup, String reason) {
        switch(type) {
            case ERROR: statusGroup.setError(reason); this.isReady = false;break;
            case WARNING: statusGroup.setWarning(reason); this.isReady = true; break;
            case OK: statusGroup.setOK(reason); this.isReady = true; break;
            default: return;
        }
        getContainer().updateButtons();
    }
    
    
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
        IProject newProject = this.newPolishProjectModel.getNewProject();
        if(newProject == null) {
            return;
        }
        newProject.open(null);
        String pproperty;
        pproperty = newProject.getPersistentProperty(PropertyConstants.QN_WTK_HOME);
        if(pproperty != null) {
            this.newPolishProjectModel.setWTKHome(new File(pproperty));
        }
        
        pproperty = newProject.getPersistentProperty(PropertyConstants.QN_POLISH_HOME);
        if(pproperty != null) {
            this.newPolishProjectModel.setPolishHome(new File(pproperty));
        }
        
        pproperty = newProject.getPersistentProperty(PropertyConstants.QN_PLATFORMS_SUPPORTED);
        if(pproperty != null) {
            this.newPolishProjectModel.setPolishHome(new File(pproperty));
        }
        
        pproperty = newProject.getPersistentProperty(PropertyConstants.QN_DEVICES_SUPPORTED);
        if(pproperty != null) {
            this.newPolishProjectModel.setPolishHome(new File(pproperty));
        }
        
    }

    private void createProject() throws CoreException {
        IProject projectToConvert = this.newPolishProjectModel.getProjectToConvert();
        if(projectToConvert == null) {
            // Make a new project;
            IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(this.newProjectNameText.getText());
            project.create(new NullProgressMonitor());
            project.open(null);
            this.newPolishProjectModel.setNewProject(project);
            this.newPolishProjectModel.setProjectCreated(true);
        }
        else {
            this.newPolishProjectModel.setNewProject(projectToConvert);
            this.newPolishProjectModel.setProjectCreated(false);
        }
        
        this.newPolishProjectModel.setBasicallyConfigured(true);
    }
    
    public void addNature() throws CoreException {
        IProject newProject = this.newPolishProjectModel.getNewProject();
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
       return this.isReady ;
    }
    
    
    

	
}