/*
 * Created on Jun 23, 2005 at 12:13:27 PM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.mepose.core.ui.propertyPages;



import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;



public class ProjectPropertyPage extends PropertyPage{

    private Text textField;
    private Composite mainComposite;
//    private IProjectPropertyPageModel presentationModel;
    private PropertyChangeListener propertyChangeListenerForPresentationModel;
    
    class PresentationModelListener implements PropertyChangeListener{

        /*
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent arg0) {
            // TODO ricky implement propertyChange
            
        }
        
    }
    
    /*
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent) {
        if(parent == null  || parent.isDisposed()) {
            return null;
        }
        this.mainComposite= new Composite(parent, SWT.NONE);
        
        GridLayout layout= new GridLayout();
        layout.numColumns= 2;
        
        this.mainComposite.setLayout(layout);
        this.mainComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));

        Label nameLabel= new Label(this.mainComposite, SWT.NONE);
        nameLabel.setText("Buildxml file");
        
        this.textField = new Text(this.mainComposite, SWT.SINGLE | SWT.BORDER);
        this.textField.setFont(parent.getFont());
        this.textField.setText(getDefaultBuildxml());
        //TODO: Does not work:
        //this.textField.setSize(50,this.textField.getLineHeight());
        //this.textField.setTextLimit(20);
        setTitle("Properties for your J2ME Polish Project");
        return this.mainComposite;
    }

    private String getDefaultBuildxml() {
        String defaultBuildxml = "build.xml";
        
        IProject project = (IProject)getElement().getAdapter(IProject.class);
        if(project == null) {
            System.out.println("ERROR:ProjectPropertyPage.getDefaultBuildxml(...):we dont have IProject as element.");
            return "";
        }
        IPath projectPath = project.getLocation();
        if(projectPath == null) {
            System.out.println("ERRORProjectPropertyPage.getDefaultBuildxml(...):the location of project is null.");
            return defaultBuildxml;
        }
        return projectPath.toString();
    }

    protected void performApply() {
        System.out.println("DEBUG:ProjectPropertyPage.performApply(...):enter.");
        super.performApply();
    }

    public boolean performCancel() {
        System.out.println("DEBUG:ProjectPropertyPage.performCancel(...):enter.");
        return super.performCancel();
    }

    protected void performDefaults() {
        System.out.println("DEBUG:ProjectPropertyPage.performDefaults(...):enter.");
        super.performDefaults();
    }

    public void performHelp() {
        System.out.println("DEBUG:ProjectPropertyPage.performHelp(...):enter.");
        super.performHelp();
    }

    public boolean performOk() {
        System.out.println("DEBUG:ProjectPropertyPage.performOk(...):enter.");
        return super.performOk();
    }

    protected void updateApplyButton() {
        System.out.println("DEBUG:ProjectPropertyPage.updateApplyButton(...):enter.");
        super.updateApplyButton();
    }

    /*
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName() == "buildxml") {
            return;
        }
                
        
    }
    
    

}
