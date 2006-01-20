package de.enough.mepose.core.ui.preferencePages;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.enough.mepose.core.CorePlugin;
import de.enough.mepose.core.MeposeCoreConstants;
import de.enough.mepose.core.ui.propertyPages.PreferencesAdapter;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class InstallationPage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public InstallationPage() {
		super(GRID);
		setPreferenceStore(new PreferencesAdapter(CorePlugin.getDefault().getPluginPreferences()));
		setDescription("Installation Paths");
	}
	
	public void createFieldEditors() {
        
	    addField(new DirectoryFieldEditor(MeposeCoreConstants.POLISH_INSTALLATION_DIR, 
	                                      "Polish Installation Directory:", getFieldEditorParent()));
	    
		addField(new DirectoryFieldEditor(MeposeCoreConstants.WTK_INSTALLATION_DIR, 
		                                  "WTK Installation Directory:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
        // Not needed.
	}

    public boolean performOk() {
//       
        boolean isPerformOK = super.performOk();
        if(isPerformOK) {
            CorePlugin.getDefault().savePluginPreferences();
        }
        return isPerformOK;
    }
	
    
}