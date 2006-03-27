package de.enough.mepose.launcher;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

public class MIDletLaunchShortcut
    implements ILaunchShortcut
{
  public MIDletLaunchShortcut()
  {
    System.out.println("Michael: MIDletLaunchShortcut instance created");
  }
  
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
                    launch(element.getResource().getProject());
                  }
              }
          }
      }
    else
      {
        // FIXME: Show error message here.
        // MessageDialog.openError(getShell(), LauncherMessages.JavaApplicationAction_Launch_failed_7, LauncherMessages.JavaApplicationLaunchShortcut_The_selection_does_not_contain_a_main_type__2); //$NON-NLS-1$ //$NON-NLS-2$
      }
  }

  public void launch(IEditorPart editor, String mode)
  {
    IEditorInput input = editor.getEditorInput();
    IJavaElement element = (IJavaElement) input.getAdapter(IJavaElement.class);

    if (element != null)
      {
        launch(element.getResource().getProject());
      }
    else
      {
        // FIXME: Show error message here.
        // MessageDialog.openInformation(getShell(), LauncherMessages.appletlauncher_search_dialog_title, LauncherMessages.appletlauncher_search_dialog_error_noapplets);   //$NON-NLS-1$ //$NON-NLS-2$
      }
  }

  private void launch(IProject project)
  {
    // Launch J2ME Polish project here.
  }
}
