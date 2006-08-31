package de.enough.mepose.ui.preferencePages;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.enough.mepose.core.MeposeConstants;
import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.ui.propertyPages.PreferencesAdapter;

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
		setPreferenceStore(new PreferencesAdapter(MeposePlugin.getDefault().getPluginPreferences()));
		setDescription("Installation Paths");
	}
	
	public void createFieldEditors() {
        
	    addField(new DirectoryFieldEditor(MeposeConstants.ID_POLISH_HOME, 
	                                      "J2ME Polish Installation Directory:", getFieldEditorParent()));

        addField(new DirectoryFieldEditor(MeposeConstants.ID_WTK_HOME, 
	                                      "WTK Installation Directory:", getFieldEditorParent()));
	
        addField(new DirectoryFieldEditor(MeposeConstants.ID_MOTOROLA_HOME, 
                                          "Motorola Installation Directory:", getFieldEditorParent()));
        
	    addField(new DirectoryFieldEditor(MeposeConstants.ID_NOKIA_HOME, 
	                                      "Nokia Installation Directory:", getFieldEditorParent()));
	    
	    addField(new DirectoryFieldEditor(MeposeConstants.ID_SIEMENS_HOME, 
	                                      "Siemens Installation Directory:", getFieldEditorParent()));

        addField(new DirectoryFieldEditor(MeposeConstants.ID_SONY_HOME, 
	                                      "Sony-Ericsson Installation Directory:", getFieldEditorParent()));
	    
        
        
    }

    
    
	public void init(IWorkbench workbench) {
        // Not needed.
	}

    public boolean performOk() {
//       
        boolean isPerformOK = super.performOk();
        if(isPerformOK) {
            MeposePlugin.getDefault().savePluginPreferences();
        }
        return isPerformOK;
    }
	
    
}