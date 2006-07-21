package de.enough.mepose.launcher;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.enough.mepose.core.MeposeConstants;
import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.core.model.MeposeModel;

public class MIDletLaunchConfigurationTab
    extends AbstractLaunchConfigurationTab
{
  private Text projectNameText;
  private Text jadFileText;
  private Text hostNameText;
  private Text portText;
  private Text polishHomeText;
  private Text wtkHomeText;
    
  /**
   * Returns the current Java element context from which to initialize
   * default settings, or <code>null</code> if none.
   * 
   * @return Java element context.
   */
  // Copied from JavaLaunchConfigurationTab.
//  protected IJavaElement getContext() {
//    IWorkbenchPage page = MeposeUIPlugin.getActivePage();
//    if (page != null) {
//      ISelection selection = page.getSelection();
//      if (selection instanceof IStructuredSelection) {
//        IStructuredSelection ss = (IStructuredSelection)selection;
//        if (!ss.isEmpty()) {
//          Object obj = ss.getFirstElement();
//          if (obj instanceof IJavaElement) {
//            return (IJavaElement)obj;
//          }
//          if (obj instanceof IResource) {
//            IJavaElement je = JavaCore.create((IResource)obj);
//            if (je == null) {
//              IProject pro = ((IResource)obj).getProject();
//              je = JavaCore.create(pro);
//            }
//            if (je != null) {
//              return je;
//            }
//          }
//        }
//      }
//      IEditorPart part = page.getActiveEditor();
//      if (part != null) {
//        IEditorInput input = part.getEditorInput();
//        return (IJavaElement) input.getAdapter(IJavaElement.class);
//      }
//    }
//    return null;
//  }

  /**
   * Set the java project attribute based on the IJavaElement.
   */
  // Copied from JavaLaunchConfigurationTab.
//  protected void initializeJavaProject(IJavaElement javaElement, ILaunchConfigurationWorkingCopy config) {
//    IJavaProject javaProject = javaElement.getJavaProject();
//    String name = null;
//    if (javaProject != null && javaProject.exists()) {
//      name = javaProject.getElementName();
//    }
//    config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, name);
//  } 

  public void createControl(final Composite parent)
  {
    Composite top = new Composite(parent, SWT.NONE);
    
    top.setLayout(new GridLayout(3, false));
    
    this.projectNameText = createTextAttribute(top, "Project");
    //TODO: Not userfriendly.
//    projectName.setEditable(false);

    this.jadFileText = createTextAttribute(top, "JAD file");
    this.hostNameText = createTextAttribute(top, "Hostname");
    this.portText = createTextAttribute(top, "Port");
    this.polishHomeText = createDirectoryAttribute(top, "Polish Home");
    this.wtkHomeText = createDirectoryAttribute(top, "WTK Home");
    
    setControl(top);
  }

  private Text createDirectoryAttribute(final Composite top, String name)
  {
    Label label = new Label(top, SWT.NONE);
    label.setText(name);
    label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    
    final Text text = new Text(top, SWT.NONE);
    text.addModifyListener(new ModifyListener()
        {
          public void modifyText(ModifyEvent e)
          {
            updateLaunchConfiguration();
          }
        });
    text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    
    Button button = new Button(top, SWT.PUSH);
    button.setText("...");
    button.addSelectionListener(new SelectionAdapter()
        {
          public void widgetSelected(SelectionEvent event)
          {
            DirectoryDialog dialog = new DirectoryDialog(top.getShell(), SWT.APPLICATION_MODAL);
            dialog.setText("Choose directory");

            String dir = dialog.open();

            if (dir != null)
              {
                text.setText(dir);
                updateLaunchConfiguration();
              }
          }
        });
    button.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
    
    return text;
  }

  private Text createTextAttribute(Composite top, String name)
  {
    Label label = new Label(top, SWT.NONE);
    label.setText(name);
    label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
    
    Text text = new Text(top, SWT.NONE);
    text.addModifyListener(new ModifyListener()
        {
          public void modifyText(ModifyEvent e)
          {
            updateLaunchConfiguration();
          }
        });
    text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    
    Composite spare = new Composite(top, SWT.NONE);
    spare.setLayout(new GridLayout(1,false));
    spare.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
    
    return text;
  }

  public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
  {

    String projectName = getSelectedJavaProjectName();
    IProject project = null;
    if(projectName != null && projectName.length() != 0) {
        project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    }
    MeposeModel model = null;
    if(project != null) {
        model = MeposePlugin.getDefault().getMeposeModelManager().getModel(project);
    }
    IPath workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().findMember("/").getLocation();

    configuration.setAttribute(MeposeConstants.ID_PROJECT_NAME, projectName);
    configuration.setAttribute(MIDletLauncherConstants.WORKSPACE, workspaceLocation.toOSString());
    configuration.setAttribute(MIDletLauncherConstants.JAD_FILE, "/path/to/file.jar");
    
    String polishHomePath = null;
    if(model != null) {
        polishHomePath = model.getPolishHome().getAbsolutePath();
    }
    if(polishHomePath == null || polishHomePath.length() == 0) {
        polishHomePath = MeposePlugin.getDefault().getPluginPreferences().getString(MeposeConstants.ID_PROJECT_NAME);
    }
    if(polishHomePath == null || polishHomePath.length() == 0) {
        polishHomePath = "/path/to/polish/home";
    }
    configuration.setAttribute(MeposeConstants.ID_POLISH_HOME, polishHomePath);
    
    String wtkHomePath = null;
    if(model != null) {
        wtkHomePath = model.getWTKHome().getAbsolutePath();
    }
    if(wtkHomePath == null || wtkHomePath.length() == 0) {
        wtkHomePath = MeposePlugin.getDefault().getPluginPreferences().getString(MIDletLauncherConstants.WTK_HOME);
    }
    if(wtkHomePath == null || wtkHomePath.length() == 0) {
        wtkHomePath = "/path/to/wtk/home";
    }
    configuration.setAttribute(MeposeConstants.ID_WTK_HOME, wtkHomePath);

    HashMap attrMap = new HashMap();
    attrMap.put("hostname", "localhost");
    attrMap.put("port", "8000");
    configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, attrMap);
  }

  protected String getSelectedJavaProjectName() {
      
      ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
      if(! (selection instanceof StructuredSelection)) {
          return "";
      }
      StructuredSelection ss = (StructuredSelection)selection;
      Object firstElement = ss.getFirstElement();
      if(firstElement instanceof IProject) {
          return ((IProject) firstElement).getName();
      }
      if(firstElement instanceof IJavaProject) {
          return ((IJavaProject)firstElement).getProject().getName();
      }
      if(firstElement instanceof IResource) {
          return ((IResource)firstElement).getProject().getName();
      }
      
      return "";
  }
  
  public void initializeFrom(ILaunchConfiguration configuration)
  {
    try
      {
        System.out.println("DEBUG:MIDletLaunchConfigurationTab.initializeFrom(...):enter.");
//      projectName.setText(configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "unknown"));
        // MeposeConstants.ID_PROJECT_NAME was used here.
        String projectNameString = configuration.getAttribute(MeposeConstants.ID_PROJECT_NAME, "");
        if(projectNameString == null) {
            projectNameString = getSelectedJavaProjectName();
        }

        this.projectNameText.setText(projectNameString);
        this.jadFileText.setText(configuration.getAttribute(MIDletLauncherConstants.JAD_FILE, "Generic-Midp2Cldc11-en_US-example.jad"));
        this.polishHomeText.setText(configuration.getAttribute(MeposeConstants.ID_POLISH_HOME, "/home/mkoch/J2ME-Polish"));
        this.wtkHomeText.setText(configuration.getAttribute(MeposeConstants.ID_WTK_HOME, "/home/mkoch/local/WTK2.2"));

        Map attrMap = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, (Map) null);

        if (attrMap != null)
          {
            this.hostNameText.setText((String) attrMap.get("hostname"));
            this.portText.setText((String) attrMap.get("port"));
          }
        else
          {
            // Use default values.
            this.hostNameText.setText("localhost");
            this.portText.setText("8000");
          }
      }
    catch (CoreException e)
      {
        // TODO: Implement proper logging here.
        e.printStackTrace();
      }
  }

  public void performApply(ILaunchConfigurationWorkingCopy configuration)
  {
      System.out.println("DEBUG:MIDletLaunchConfigurationTab.performApply(...):enter.");
//      configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, this.projectNameText.getText());
    configuration.setAttribute(MeposeConstants.ID_PROJECT_NAME, this.projectNameText.getText());
    
    HashMap attrMap = new HashMap();
    attrMap.put("hostname", this.hostNameText.getText());
    attrMap.put("port", this.portText.getText());
    configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, attrMap);
    
    configuration.setAttribute(MIDletLauncherConstants.JAD_FILE, this.jadFileText.getText());
    configuration.setAttribute(MeposeConstants.ID_POLISH_HOME, this.polishHomeText.getText());
    configuration.setAttribute(MeposeConstants.ID_WTK_HOME, this.wtkHomeText.getText());
  }

  void updateLaunchConfiguration()
  {
    updateLaunchConfigurationDialog();
  }

  public String getName()
  {
    return "MIDlet";
  }
}
