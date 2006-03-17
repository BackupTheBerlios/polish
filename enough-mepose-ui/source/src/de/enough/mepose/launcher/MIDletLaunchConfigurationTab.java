package de.enough.mepose.launcher;

import de.enough.mepose.ui.MeposeUIPlugin;

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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

public class MIDletLaunchConfigurationTab
    extends AbstractLaunchConfigurationTab
{
  private Text projectName;
  private Text jadFile;
  private Text hostName;
  private Text port;
  private Text polishHome;
  private Text wtkHome;
    
  /**
   * Returns the current Java element context from which to initialize
   * default settings, or <code>null</code> if none.
   * 
   * @return Java element context.
   */
  // Copied from JavaLaunchConfigurationTab.
  protected IJavaElement getContext() {
    IWorkbenchPage page = MeposeUIPlugin.getActivePage();
    if (page != null) {
      ISelection selection = page.getSelection();
      if (selection instanceof IStructuredSelection) {
        IStructuredSelection ss = (IStructuredSelection)selection;
        if (!ss.isEmpty()) {
          Object obj = ss.getFirstElement();
          if (obj instanceof IJavaElement) {
            return (IJavaElement)obj;
          }
          if (obj instanceof IResource) {
            IJavaElement je = JavaCore.create((IResource)obj);
            if (je == null) {
              IProject pro = ((IResource)obj).getProject();
              je = JavaCore.create(pro);
            }
            if (je != null) {
              return je;
            }
          }
        }
      }
      IEditorPart part = page.getActiveEditor();
      if (part != null) {
        IEditorInput input = part.getEditorInput();
        return (IJavaElement) input.getAdapter(IJavaElement.class);
      }
    }
    return null;
  }

  /**
   * Set the java project attribute based on the IJavaElement.
   */
  // Copied from JavaLaunchConfigurationTab.
  protected void initializeJavaProject(IJavaElement javaElement, ILaunchConfigurationWorkingCopy config) {
    IJavaProject javaProject = javaElement.getJavaProject();
    String name = null;
    if (javaProject != null && javaProject.exists()) {
      name = javaProject.getElementName();
    }
    config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, name);
  } 

  public void createControl(final Composite parent)
  {
    Composite top = new Composite(parent, SWT.NONE);
    
    top.setLayout(new GridLayout(3, false));
    
    projectName = createTextAttribute(top, "Project");
    projectName.setEditable(false);

    jadFile = createTextAttribute(top, "JAD file");
    hostName = createTextAttribute(top, "Hostname");
    port = createTextAttribute(top, "Port");
    polishHome = createDirectoryAttribute(top, "Polish Home");
    wtkHome = createDirectoryAttribute(top, "WTK Home");
    
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
            updateLaunchConfigurationDialog();
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
                updateLaunchConfigurationDialog();
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
            updateLaunchConfigurationDialog();
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
    IJavaElement javaElement = getContext();
    
    if (javaElement != null)
      {
        initializeJavaProject(javaElement, configuration);
      }
    else
      {
        configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "menu");
      }

    IPath workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().findMember("/").getLocation();
    configuration.setAttribute(MIDletLauncherConstants.WORKSPACE, workspaceLocation.toOSString());
    configuration.setAttribute(MIDletLauncherConstants.JAD_FILE, "Generic-Midp2Cldc11-en_US-example.jad");
    configuration.setAttribute(MIDletLauncherConstants.POLISH_HOME, "/home/mkoch/J2ME-Polish");
    configuration.setAttribute(MIDletLauncherConstants.WTK_HOME, "/home/mkoch/local/WTK2.2");

    HashMap attrMap = new HashMap();
    attrMap.put("hostname", "localhost");
    attrMap.put("port", "8000");
    configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, attrMap);
  }

  public void initializeFrom(ILaunchConfiguration configuration)
  {
    try
      {
        projectName.setText(configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "unknown"));
        jadFile.setText(configuration.getAttribute(MIDletLauncherConstants.JAD_FILE, "Generic-Midp2Cldc11-en_US-example.jad"));
        polishHome.setText(configuration.getAttribute(MIDletLauncherConstants.POLISH_HOME, "/home/mkoch/J2ME-Polish"));
        wtkHome.setText(configuration.getAttribute(MIDletLauncherConstants.WTK_HOME, "/home/mkoch/local/WTK2.2"));

        Map attrMap = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, (Map) null);

        if (attrMap != null)
          {
            hostName.setText((String) attrMap.get("hostname"));
            port.setText((String) attrMap.get("port"));
          }
        else
          {
            // Use default values.
            hostName.setText("localhost");
            port.setText("8000");
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
    configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName.getText());
    
    HashMap attrMap = new HashMap();
    attrMap.put("hostname", hostName.getText());
    attrMap.put("port", port.getText());
    configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, attrMap);
    
    configuration.setAttribute(MIDletLauncherConstants.JAD_FILE, jadFile.getText());
    configuration.setAttribute(MIDletLauncherConstants.POLISH_HOME, polishHome.getText());
    configuration.setAttribute(MIDletLauncherConstants.WTK_HOME, wtkHome.getText());
  }

  public String getName()
  {
    return "MIDlet";
  }
}
