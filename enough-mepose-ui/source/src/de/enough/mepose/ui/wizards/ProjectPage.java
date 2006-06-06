package de.enough.mepose.ui.wizards;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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

import de.enough.mepose.core.MeposeConstants;
import de.enough.mepose.core.MeposePlugin;
import de.enough.swt.widgets.StatusGroup;
import de.enough.utils.Status;
import de.enough.utils.StatusEvent;
import de.enough.utils.StatusListener;

public class ProjectPage extends WizardPage{

    public static final int OK = 10;
    public static Logger logger = Logger.getLogger(ProjectPage.class);
    
    private NewProjectModel newProjectModel;
    private Text newProjectNameText;
    private Label newProjectNameLabel;
    private StatusGroup newProjectNameStatusGroup;
//    private boolean isReady = false;
    private ProjectPageModel projectPageModel;
    private Group desciptionGroup;
    private Text descriptionText;
    
    private class SimpleStatusGroupUpdater implements StatusListener{
        private StatusGroup statusGroup;
        private String propertyToWatch;
        public SimpleStatusGroupUpdater(StatusGroup statusGroup, String propertyToWatch) {
            this.statusGroup = statusGroup;
            this.propertyToWatch = propertyToWatch;
        }

        public void handleStatusEvent(StatusEvent statusEvent) {
            Status newStatus = statusEvent.getNewStatus();
            String changedProperty = statusEvent.getProperty();
            if(this.propertyToWatch.equals(changedProperty)) {
                String message = newStatus.getMessage();
                switch(newStatus.getType()) {
                    case Status.TYPE_OK: this.statusGroup.setOK(message); break;
                    case Status.TYPE_INFO: this.statusGroup.setWarning(message); break;
                    case Status.TYPE_WARNING: this.statusGroup.setWarning(message); break;
                    case Status.TYPE_ERROR: this.statusGroup.setError(message); break;
                }
            }
        }
    }
    
	public ProjectPage(NewProjectModel newProjectOptions) {
		super("wizardPage");
        if(newProjectOptions == null){
            logger.error("Parameter 'newProjectOptions' is null contrary to API.");
            return;
        }
        this.newProjectModel = newProjectOptions;
		setTitle("Create a J2ME Polish Project");
		setDescription("Create a new J2ME Polish Project or convert an existing one.");
        this.projectPageModel = new ProjectPageModel();
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 9;
		container.setLayout(layout);
        
        this.newProjectNameStatusGroup = new StatusGroup(container,SWT.NONE);
        this.newProjectNameStatusGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        this.newProjectModel.addStatusListener(new SimpleStatusGroupUpdater(this.newProjectNameStatusGroup,NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_NAME));
        
        Composite myGroupMainComposite = this.newProjectNameStatusGroup.getMainComposite();
        myGroupMainComposite.setLayout(new GridLayout(2,false));
        
        this.newProjectNameStatusGroup.setLabelText("New Project");
        
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
        this.newProjectNameText.setFocus();
        
        this.desciptionGroup = new Group(container,SWT.NONE);
        this.desciptionGroup.setText("Project Description");
        this.desciptionGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        this.desciptionGroup.setLayout(new GridLayout(1,false));
        
        this.descriptionText = new Text(this.desciptionGroup,SWT.WRAP);
        this.descriptionText.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        
//		Label someLabel = new Label(container,SWT.NONE);
//        someLabel.setText("ask the user something");
        
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

	private void checkGUIState() {
	    projectNameModified();
    }
    
    protected void projectNameModified() {
        String newName = this.newProjectNameText.getText();
        this.newProjectModel.setPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_NAME,newName);
    }


    public IWizardPage getNextPage() {
        
        String projectDescription = this.descriptionText.getText();
        this.newProjectModel.setProjectDescription(projectDescription);
        
        try {
            createProject();
//            setupProject();
            addNature();
        }
        catch (CoreException exception) {
            if(logger.isDebugEnabled()) {
                logger.error("could not create project:"+exception);
                return null;
            }
        }
        createStuff();
        return super.getNextPage();
    }

    private void createStuff() {
        try {
            this.newProjectModel.getProject().getFolder("resources").create(true,true,null);
        } catch (CoreException exception) {
            MeposePlugin.log("Could not create 'resources' folder",exception);
            return;
        }
    }
    
//    private void setupProject() throws CoreException {
        //TODO: Fill in the project properties within the core plugin.
//        IProject newProject = (IProject)this.newProjectModel.getPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_INSTANCE);
//        if(newProject == null) {
//            return;
//        }
//        newProject.open(null);
//        String pproperty;
//        pproperty = newProject.getPersistentProperty(PropertyConstants.QN_WTK_HOME);
//        if(pproperty != null) {
//            this.newProjectModel.getMeposeModel().setPropertyValue(MeposeModel.ID_WTK_HOME,new File(pproperty));
//        }
//        
//        pproperty = newProject.getPersistentProperty(PropertyConstants.QN_POLISH_HOME);
//        if(pproperty != null) {
//            this.newProjectModel.getMeposeModel().setPropertyValue(MeposeModel.ID_POLISH_HOME,new File(pproperty));
//        }
//        
//        pproperty = newProject.getPersistentProperty(PropertyConstants.QN_PLATFORMS_SUPPORTED);
//        if(pproperty != null) {
//            this.newProjectModel.getMeposeModel().setPropertyValue(MeposeModel.ID_PLATFORMS_SUPPORTED,new File(pproperty));
//        }
//        
//        pproperty = newProject.getPersistentProperty(PropertyConstants.QN_DEVICES_SUPPORTED);
//        if(pproperty != null) {
//            this.newProjectModel.getMeposeModel().setPropertyValue(MeposeModel.ID_DEVICES_SUPPORTED,new File(pproperty));
//        }
//        
//    }

    private void createProject() throws CoreException {
        IProject project = (IProject)this.newProjectModel.getPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_TOCONVERT);
        if(project != null) {
            // Set the project to convert to the new project.
            this.newProjectModel.setPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_STATE_CREATED_PROJECT,Boolean.FALSE);
        }
        else {
            // Make a new project;
            project = ResourcesPlugin.getWorkspace().getRoot().getProject(this.newProjectNameText.getText());
            if( ! project.exists()) {
                project.create(new NullProgressMonitor());
            }
            project.open(null);
            this.newProjectModel.setPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_STATE_CREATED_PROJECT,Boolean.TRUE);
        }
//        this.newProjectModel.setPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_INSTANCE,project);
        IPath projectLocation = project.getLocation();
//        this.newProjectModel.getMeposeModel().setPropertyValue(MeposeModel.ID_PATH_PROJECT_FILE,projectLocation.toFile());
        this.newProjectModel.getMeposeModel().setProjectHome(projectLocation.toFile());
        this.newProjectModel.setProjectInstance(project);
        MeposePlugin.getDefault().getMeposeModelManager().addModel(project,this.newProjectModel.getMeposeModel());
    }
    
    private void addNature() throws CoreException {
//        IProject newProject = (IProject)this.newProjectModel.getPropertyValue(NewProjectModel.ID_NEWPROJECTMODEL_PROJECT_INSTANCE);
        IProject newProject = this.newProjectModel.getProject();
        if(newProject == null) {
            return;
        }
        
        IProjectNature nature = newProject.getNature(MeposeConstants.ID_NATURE);
        if(nature != null) {
            // Its already a PolishProject.
            return;
        }
        IProjectDescription projectDescription = newProject.getDescription();
        String[] natureIDs = projectDescription.getNatureIds();
        String[] newNatureIDs = new String[natureIDs.length+1];
        System.arraycopy(natureIDs, 0, newNatureIDs, 0, natureIDs.length);
        // Set as last element.
        newNatureIDs[natureIDs.length] = MeposeConstants.ID_NATURE;
        
        projectDescription.setNatureIds(newNatureIDs);
        newProject.setDescription(projectDescription, null);
    }

    public boolean canFlipToNextPage() {
        return this.projectPageModel.getModelStatus().getType() == Status.TYPE_OK;
    }

    /*
     * @see de.enough.utils.StatusListener#handleStatusEvent(de.enough.utils.StatusEvent)
     */
//    public void handleStatusEvent(StatusEvent statusEvent) {
//        Status newStatus = statusEvent.getNewStatus();
//        
//    }
    
    
    

	
}