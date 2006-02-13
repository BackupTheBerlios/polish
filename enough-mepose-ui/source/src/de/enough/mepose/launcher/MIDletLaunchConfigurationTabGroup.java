package de.enough.mepose.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class MIDletLaunchConfigurationTabGroup
	extends AbstractLaunchConfigurationTabGroup
{
	public void createTabs(ILaunchConfigurationDialog dialog, String mode)
	{
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
			new CommonTab(),
			new MIDletLaunchConfigurationTab(),
		};
		setTabs(tabs);
	}
}
