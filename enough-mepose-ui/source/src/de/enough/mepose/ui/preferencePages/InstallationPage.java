package de.enough.mepose.ui.preferencePages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.enough.mepose.core.MeposeConstants;
import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.ui.SubstitutionDirectoryFieldEditor;
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

	private SubstitutionDirectoryFieldEditor polishHomeFieldEditor;

    public InstallationPage() {
		super(GRID);
		setPreferenceStore(new PreferencesAdapter(MeposePlugin.getDefault().getPluginPreferences()));
		setDescription("Installation Paths");
	}
	
	public void createFieldEditors() {
        
	    this.polishHomeFieldEditor = new SubstitutionDirectoryFieldEditor(MeposeConstants.ID_POLISH_HOME, 
                	                                      "J2ME Polish Installation Directory:", getFieldEditorParent());
        addField(this.polishHomeFieldEditor);

        /* Not needed as these settings are per project and the defaults come from the globals.properties in polish.home.
        
        if(MeposePlugin.isMacOS()) {
            addField(new SubstitutionDirectoryFieldEditor(MeposeConstants.ID_MPP_HOME, 
                                              "MPP Installation Directory:", getFieldEditorParent()));
        }
        else {
            addField(new SubstitutionDirectoryFieldEditor(MeposeConstants.ID_WTK_HOME, 
                                              "WTK Installation Directory:", getFieldEditorParent()));
        }
	
        addField(new SubstitutionDirectoryFieldEditor(MeposeConstants.ID_MOTOROLA_HOME, 
                                          "Motorola Installation Directory:", getFieldEditorParent()));
        
	    addField(new SubstitutionDirectoryFieldEditor(MeposeConstants.ID_NOKIA_HOME, 
	                                      "Nokia Installation Directory:", getFieldEditorParent()));
	    
	    addField(new SubstitutionDirectoryFieldEditor(MeposeConstants.ID_SIEMENS_HOME, 
	                                      "Siemens Installation Directory:", getFieldEditorParent()));

        addField(new SubstitutionDirectoryFieldEditor(MeposeConstants.ID_SONY_HOME, 
	                                      "Sony-Ericsson Installation Directory:", getFieldEditorParent()));
                                          
        */
    }
    
	public void init(IWorkbench workbench) {
        // Not needed.
	}

    public boolean performOk() {
        boolean isPerformOK = super.performOk();
        if(isPerformOK) {
            
            // TODO: Look if the user is requesting to force the new default into all projects.
            // If forcing the defaults, give the ModelManager the change with setDefaultProperty.
            
            // Not needed anymore as we do not use the preferences. They are not portable with cvs.
            MeposePlugin.getDefault().savePluginPreferences();
        }
        return isPerformOK;
    }
}