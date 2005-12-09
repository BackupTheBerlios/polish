package de.enough.mepose.core.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import de.enough.mepose.core.CorePlugin;
import de.enough.mepose.core.MeposeCoreConstants;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		Preferences store = CorePlugin.getDefault().getPluginPreferences();
		store.setDefault(MeposeCoreConstants.POLISH_INSTALLATION_DIR,"hallo");
		store.setDefault(MeposeCoreConstants.WTK_INSTALLATION_DIR,"welt");
        
	}

}
