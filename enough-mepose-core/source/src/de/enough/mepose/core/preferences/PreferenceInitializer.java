package de.enough.mepose.core.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import de.enough.mepose.core.MeposePlugin;
import de.enough.mepose.core.MeposeConstants;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		Preferences store = MeposePlugin.getDefault().getPluginPreferences();
		store.setDefault(MeposeConstants.POLISH_INSTALLATION_DIR,"/");
		store.setDefault(MeposeConstants.WTK_INSTALLATION_DIR,"/");
        
	}

}
