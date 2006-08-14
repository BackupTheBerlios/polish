package de.enough.mepose.launcher;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import de.enough.mepose.core.MeposeConstants;

public class MIDletLaunchShortcut
    implements ILaunchShortcut
{
  public static final String TXT_MIDLET_LAUNCHER = "Midlet Launcher";
  public static final String LAUNCH_CONFIGURATION_TYPE = "de.enough.mepose.launcher.launchConfigurationType";

  public void launch(ISelection selection, String mode)
  {
    if (selection instanceof IStructuredSelection)
    {
        Object[] objects = ((IStructuredSelection) selection).toArray();
        
        if (objects == null)
        {
            return;
        }

        for (int i = 0; i < objects.length; i++)
        {
            if (objects[i] instanceof IAdaptable)
              {
                IJavaElement element = (IJavaElement) ((IAdaptable) objects[i]).getAdapter(IJavaElement.class);
                    
                if (element != null)
                  {
                    launch(element.getResource().getProject(),mode);
                  }
              }
          }
      }
    else
      {
        showError("No java project selected.Selections must not contain "+selection.getClass().getName());
      }
  }

  public void launch(IEditorPart editor, String mode)
  {
    IEditorInput input = editor.getEditorInput();
    // Look for Midlets in the asociated project.
    // showError if it is not present.
  }

  private void launch(IProject project,String mode)
  {
      List launchConfigurations = findConfigsForProject(project);
      if(launchConfigurations == null || launchConfigurations.size() == 0) {
        ILaunchConfiguration launchConfig;
        launchConfig = createLaunchConfiguration(project);
          if(launchConfig == null) {
              showError("Could not create launch Configuration");
              return;
          }
          launchConfigurations = new LinkedList();
          launchConfigurations.add(launchConfig);
      }
      if(launchConfigurations.size() == 1) {
          launch(mode, (ILaunchConfiguration)launchConfigurations.get(0));
      }
      else {
          ILaunchConfiguration launchConfiguration = chooseConfiguration(launchConfigurations,mode);
          if(launchConfiguration == null) {
              showError("Launch was aborded.");
              return;
          }
          launch(mode,(ILaunchConfiguration)launchConfigurations.get(0));
      }
  }

/**
 * @param mode
 * @param launchConfiguration
 */
private void launch(String mode, ILaunchConfiguration launchConfiguration) {
    DebugUITools.launch(launchConfiguration, mode);
}
 /**
 * @param errorMessage
 */
private void showError(String errorMessage) {
    MessageDialog.openError(getShell(), TXT_MIDLET_LAUNCHER, errorMessage);
}

  /**
 * @param project
 * @return true if the launchConfiguration was created, false otherwise.
 */
private ILaunchConfiguration createLaunchConfiguration(IProject project) {
    ILaunchManager launchManager = getLaunchManager();
    ILaunchConfigurationType type = launchManager.getLaunchConfigurationType(LAUNCH_CONFIGURATION_TYPE);
    ILaunchConfiguration launchConfig;
    String projectName = project.getName();
    try {
        ILaunchConfigurationWorkingCopy launchConfigWorkingCopy = type.newInstance(project,"Midlet "+project.getName());
        launchConfigWorkingCopy.setAttribute(MeposeConstants.ID_PROJECT_NAME, projectName);
        launchConfig = launchConfigWorkingCopy.doSave();
    } catch (CoreException exception) {
        showError("Something bad happend.");
        return null;
    }
    return launchConfig;
}

/**
 * @return
 */
private ILaunchManager getLaunchManager() {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    return launchManager;
}

/**
 * @param project
 */
private List findConfigsForProject(IProject project) {
    ILaunchConfiguration[] launchConfigurations;
    try {
        launchConfigurations = getLaunchManager().getLaunchConfigurations();
    } catch (CoreException exception) {
        showError("Could not get launch configurations.");
        return null;
    }
    if(launchConfigurations == null || launchConfigurations.length == 0) {
        return null;
    }
    
    List launchConfigurationsList = new LinkedList();
    
    String projectName = project.getName();
    for (int i = 0; i < launchConfigurations.length; i++) {
        String attributeProjectName;
        ILaunchConfiguration launchConfiguration = launchConfigurations[i];
        try {
            attributeProjectName = launchConfiguration.getAttribute(MeposeConstants.ID_PROJECT_NAME,"");
        } catch (CoreException exception) {
            continue;
        }
        if(projectName.equals(attributeProjectName)){
            launchConfigurationsList.add(launchConfiguration);
        }
    }
    if(launchConfigurationsList.size() == 0) {
        return null;
    }
    return launchConfigurationsList;
}

protected ILaunchConfiguration chooseConfiguration(List configList, String mode) {
        IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
        ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);
        dialog.setElements(configList.toArray());
        dialog.setTitle(LauncherMessages.JavaApplicationLaunchShortcut_0);  //$NON-NLS-1$
        dialog.setMessage(LauncherMessages.JavaLaunchShortcut_2);
        // In 3.2 there is only one message.
//        if (mode.equals(ILaunchManager.DEBUG_MODE)) {
//            dialog.setMessage(LauncherMessages.JavaApplicationAction_Choose_a_launch_configuration_to_debug_2);  //$NON-NLS-1$
//        } else {
//            dialog.setMessage(LauncherMessages.JavaApplicationAction_Choose_a_launch_configuration_to_run_3); //$NON-NLS-1$
//        }
        dialog.setMultipleSelection(false);
        int result = dialog.open();
        labelProvider.dispose();
        if (result == Window.OK) {
            return (ILaunchConfiguration) dialog.getFirstResult();
        }
        return null;        
    }
  
    protected Shell getShell() {
        return JDIDebugUIPlugin.getActiveWorkbenchShell();
    }
}
