package de.enough.mepose.ui;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class SubstitutionDirectoryFieldEditor extends DirectoryFieldEditor
{
  private static IStringVariableManager VARIABLE_MANAGER =
    VariablesPlugin.getDefault().getStringVariableManager();

  public SubstitutionDirectoryFieldEditor(String name, String labelText, Composite parent)
  {
    super(name, labelText, parent);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.preference.DirectoryFieldEditor#doCheckState()
   */
  protected boolean doCheckState()
  {
    String fileName = getTextControl().getText();
    try {
      fileName = VARIABLE_MANAGER.performStringSubstitution(fileName);
    }
    catch (CoreException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    fileName = fileName.trim();
    if (fileName.length() == 0 && isEmptyStringAllowed()) {
      return true;
    }
    File file = new File(fileName);
    return file.isDirectory();
  }
}
