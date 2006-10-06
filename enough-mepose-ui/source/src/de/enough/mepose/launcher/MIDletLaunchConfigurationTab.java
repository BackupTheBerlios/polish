package de.enough.mepose.launcher;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.enough.mepose.core.MeposeConstants;

public class MIDletLaunchConfigurationTab extends
        AbstractLaunchConfigurationTab {

    private Text projectNameText;

    public void createControl(final Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(3, false));
        this.projectNameText = createTextAttribute(main, "Project");
        setControl(main);
    }

    private Text createTextAttribute(Composite top, String name) {
        Label label = new Label(top, SWT.NONE);
        label.setText(name);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Text text = new Text(top, SWT.NONE);
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                updateLaunchConfiguration();
            }
        });
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Composite spare = new Composite(top, SWT.NONE);
        spare.setLayout(new GridLayout(1, false));
        spare.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));

        return text;
    }

    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        String projectName = getSelectedJavaProjectName();
        configuration
                .setAttribute(MeposeConstants.ID_PROJECT_NAME, projectName);

        HashMap attrMap = new HashMap();
        attrMap.put("hostname", "localhost");
        attrMap.put("port", "8000");
        configuration
                .setAttribute(
                              IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP,
                              attrMap);
    }

    protected String getSelectedJavaProjectName() {

        ISelection selection = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().getSelection();
        if (!(selection instanceof StructuredSelection)) {
            return "";
        }
        StructuredSelection ss = (StructuredSelection) selection;
        Object firstElement = ss.getFirstElement();
        if (firstElement instanceof IProject) {
            return ((IProject) firstElement).getName();
        }
        if (firstElement instanceof IJavaProject) {
            return ((IJavaProject) firstElement).getProject().getName();
        }
        if (firstElement instanceof IResource) {
            return ((IResource) firstElement).getProject().getName();
        }
        if(firstElement instanceof JarPackageFragmentRoot) {
            IJavaElement parent = ((JarPackageFragmentRoot)firstElement).getParent();
            String elementName = parent.getElementName();
            return elementName;
        }

        return "";
    }

    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            String projectNameString = configuration
                    .getAttribute(MeposeConstants.ID_PROJECT_NAME,
                                  (String) null);
            if (projectNameString == null) {
                projectNameString = getSelectedJavaProjectName();
            }

            this.projectNameText.setText(projectNameString);
        } catch (CoreException e) {
            // TODO: Implement proper logging here.
            e.printStackTrace();
        }
    }

    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(MeposeConstants.ID_PROJECT_NAME,
                                   this.projectNameText.getText());
    }

    void updateLaunchConfiguration() {
        updateLaunchConfigurationDialog();
    }

    public String getName() {
        return "MIDlet";
    }
}
