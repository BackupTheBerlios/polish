package de.enough.mepose.core.ui.wizards;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.enough.mepose.core.model.BuildXMLWriter;
import de.enough.mepose.ui.UIPlugin;
import de.enough.swt.widgets.StatusGroup;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class ProjectPage extends WizardPage{

    private NewPolishProjectDAO newProjectOptions;
    public static Logger logger = Logger.getLogger(ProjectPage.class);
    private Button yesButton;
    private Button noButton;
    private Text newProjectNameText;
    private Label newProjectNameLabel;
    private StatusGroup myGroup;
    private boolean isReady = false;
    
	public ProjectPage(NewPolishProjectDAO newProjectOptions) {
		super("wizardPage");
        if(newProjectOptions == null){
            logger.error("Parameter 'newProjectOptions' is null contrary to API.");
            return;
        }
		setTitle("Create a J2ME Polish Project");
		setDescription("Create a new J2ME Polish Project or convert an existing one.");
		this.newProjectOptions = newProjectOptions;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 9;
		container.setLayout(layout);
        
        
//        IJavaProject projectToConvert = this.newProjectOptions.getProjectToConvert();
//        if(projectToConvert != null) {
//            Label label = new Label(container, SWT.NULL);
//            label.setText("Convert project '"+projectToConvert.getResource().getName()+"' to a J2ME Polish project?");
//            
//            Composite buttonComposite = new Composite(container,SWT.NONE);
//            buttonComposite.setLayout(new GridLayout(2,false));
//            
//            this.yesButton = new Button(buttonComposite,SWT.RADIO);
//            this.yesButton.setText("yes");
//            this.yesButton.setSelection(this.newProjectOptions.isShouldConvertProject());
//            this.yesButton.addSelectionListener(new SelectionAdapter() {
//                public void widgetSelected(SelectionEvent e) {
//                    actionToggleConvertProject();
//                }
//            });
//            this.noButton = new Button(buttonComposite,SWT.RADIO);
//            this.noButton.setText("no");
//            this.noButton.setSelection( ! this.newProjectOptions.isShouldConvertProject());
//            this.noButton.addSelectionListener(new SelectionAdapter() {
//                public void widgetSelected(SelectionEvent e) {
//                    actionToggleConvertProject();
//                }
//            });
//            
//        }
        
        this.myGroup = new StatusGroup(container,SWT.NONE);
        this.myGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        
        Composite myGroupMainComposite = this.myGroup.getMainComposite();
        myGroupMainComposite.setLayout(new GridLayout(2,false));
        
        Group myGroupGroupComposite = this.myGroup.getGroup();
        myGroupGroupComposite.setText("New Project");
        
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

    protected void actionToggleConvertProject() {
        this.newProjectOptions.setShouldConvertProject(this.yesButton.getSelection());
        enableNewProjectName( ! this.newProjectOptions.isShouldConvertProject());
    }

    /**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
//		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
//				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
//				"Select new file container");
//		if (dialog.open() == ContainerSelectionDialog.OK) {
//			Object[] result = dialog.getResult();
//			if (result.length == 1) {
//				containerText.setText(((Path) result[0]).toString());
//			}
//		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
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
	}

	private void checkGUIState() {
	    projectNameModified();
    }
    
    protected void projectNameModified() {
        String projectName = this.newProjectNameText.getText();
        if(projectName == null || projectName.length() == 0) {
            noGoWizard("The project name must not be null");
            this.myGroup.setError("The project name must not be null");
            return;
        }
        IResource resource = null;
        try {
            resource = ResourcesPlugin.getWorkspace().getRoot().findMember(projectName);
        } catch (Exception e) {
            System.out.println("ERROR:ProjectPage.projectNameModified(...):"+e);
        }
        if(resource != null) {
            noGoWizard("Project exists and will be converted");
            this.myGroup.setWarning("Project exists and will be converted");
        }
        else {
            goWizard();
            this.myGroup.setOK(null);
        }
    }

    protected void goWizard() {
        this.isReady = true;
        setPageComplete(true);
        
    }

    protected void noGoWizard() {
        noGoWizard("");
    }
    
    protected void noGoWizard(String message) {
        if(message == null){
            logger.error("Parameter 'message' is null contrary to API.");
            message = "An Error Occurred";
        }
        this.isReady = false;
        setPageComplete(false);
    }
    
    protected void enableNewProjectName(boolean enable) {
        this.newProjectNameText.setEnabled(enable);
        this.newProjectNameLabel.setEnabled(enable);
    }

    public IWizardPage getNextPage() {
        try {
            //TODO: Extract this to a class to have this functionality.
            createProject();
            createBuildXML();
        } catch (CoreException exception) {
            if(logger.isDebugEnabled()) {
                logger.error("could not create project:"+exception);
            }
        }
        return super.getNextPage();
    }

    /**
     * 
     */
    private void createBuildXML() {
        BuildXMLWriter buildXMLWriter = new BuildXMLWriter();
        buildXMLWriter.writeBuildXML(new OutputStreamWriter(System.out));
    }

    /**
     * @throws CoreException
     */
    private void createProject() throws CoreException {
        IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(this.newProjectNameText.getText());
        newProject.create(new NullProgressMonitor());
        this.newProjectOptions.setProject(newProject);
    }

    public boolean canFlipToNextPage() {
       return this.isReady ;
    }
    
    
    

	
}