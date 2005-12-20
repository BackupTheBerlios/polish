package de.enough.mepose.core.ui.preferencePages;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import de.enough.mepose.MeposeCoreUIConstants;
import de.enough.mepose.core.ui.plugin.UIPluginActivator;
import de.enough.utils.ErrorSituation;

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

public class MainPage extends PreferencePage implements IWorkbenchPreferencePage {

	public MainPage() {
		setDescription("Overview of J2ME Polish preferences");
        setImageDescriptor(UIPluginActivator.getDefault().getImage(MeposeCoreUIConstants.KEY_IMAGE_LOGO));
        
    }
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
        //
	}

    /*
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent,SWT.NONE);
        composite.setLayout(new GridLayout(1,false));

        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(MeposeCoreUIConstants.ID_EXTPOINT_PREFERENCE_PAGE_SUMMARY);
        if(extensionPoint == null) {
            return composite;
        }
        
        IConfigurationElement[] configurationElements = extensionPoint.getConfigurationElements();
        
        for (int i = 0; i < configurationElements.length; i++) {
            String pageID = configurationElements[i].getAttribute("pageID");
            String shortDescription = configurationElements[i].getAttribute("shortDescription");
            new PreferenceLinkArea(composite,
                                   SWT.WRAP,
                                   pageID,
                                   "<a>{0}</a>: "+shortDescription,
                                   (IWorkbenchPreferenceContainer) getContainer(), null);
        }
        return composite;
    }
	
}