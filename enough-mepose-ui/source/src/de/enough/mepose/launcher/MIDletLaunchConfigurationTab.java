package de.enough.mepose.launcher;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.widgets.Composite;

public class MIDletLaunchConfigurationTab
	extends AbstractLaunchConfigurationTab
{
	// TODO: Don't hardcode this.
	public static final String TEST_PROJECT = "debuggerTestMIDlet";

	public void createControl(Composite parent)
	{
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		// TODO: This is a fake.
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, TEST_PROJECT);
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
	}
	
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		// TODO: This is a fake.
		configuration.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, TEST_PROJECT);
	}

	public String getName()
	{
		return "Enough Java Launch Tab";
	}
}
