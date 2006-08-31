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
		store.setDefault(MeposeConstants.ID_POLISH_HOME,"/");
		store.setDefault(MeposeConstants.ID_WTK_HOME,"/");
		store.setDefault(MeposeConstants.ID_NOKIA_HOME,"/");
		store.setDefault(MeposeConstants.ID_SONY_HOME,"/");
		store.setDefault(MeposeConstants.ID_MOTOROLA_HOME,"/");
		store.setDefault(MeposeConstants.ID_SIEMENS_HOME,"/");
	}

}
