/*
 * Created on Dec 9, 2005 at 6:38:36 PM.
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
package de.enough.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

import de.enough.mepose.ui.MeposeUIConstants;
import de.enough.mepose.ui.MeposeUIPlugin;

/**
 * Do not set the layout of this composite.
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 9, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class StatusGroup extends Composite{

    private Group group;
    private Composite echoArea;
    private Composite mainArea;
    private Label iconLabel;
    private Label echoLabel;
    private GridData echoAreaGridData;
//    private List statusListeners;
//    private String propertyToWatch;

    public StatusGroup(Composite parent, int style) {
        super(parent, style);
        GridLayout gridLayout;
        gridLayout = new GridLayout(1,false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        super.setLayout(gridLayout);
        this.group = new Group(this,SWT.NONE);
        this.group.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        gridLayout = new GridLayout(1,false);
        this.group.setLayout(gridLayout);
        
        this.mainArea = new Composite(this.group,SWT.NONE);
        this.mainArea.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        
        this.echoArea = new Composite(this.group,SWT.NONE);
        this.echoAreaGridData = new GridData(SWT.FILL,SWT.CENTER,true,false);
        this.echoArea.setLayoutData(this.echoAreaGridData);
        this.echoArea.setLayout(new GridLayout(2,false));
        
        this.iconLabel = new Label(this.echoArea,SWT.NONE);
        
        this.echoLabel = new Label(this.echoArea,SWT.NONE);
        this.echoLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        
//        this.statusListeners = new LinkedList();
    }
    
    /**
     * Call this method to get the main area which sould be the parent of any
     * widgets which should be within the group.
     * @return the main area of this StatusGroup. It should be the parent of
     * a child which is displayed in the group.
     */
    public Composite getMainComposite() {
        return this.mainArea;
    }
    
    
    
    // This is such an ugly API. But we are not allowed to subclass group
    // and duplicating the API is tiresome.
    /*
     * Get the underlying Group object. All child objects of the StatusGroup must be child
     * of this Group object.
     * @return the underlying Group widget. Do not use this thing! Use getMainComposite().
     */
//    public Group getGroup() {
//        return this.group;
//    }

    public void setLayout(Layout layout) {
        throw new UnsupportedOperationException("It is not possible to set the layout of a StatusGroup object.");
    }

    public void setLabelText(String label) {
        this.group.setText(label);
    }
    
    public void setWarning(String message) {
        this.iconLabel.setImage(MeposeUIPlugin.getDefault().getImage(MeposeUIConstants.KEY_IMAGE_WARNING).createImage());
        if(message == null) {
            message = "";
        }
        this.echoLabel.setText(message);
    }
    
    public void setError(String message) {
        this.iconLabel.setImage(MeposeUIPlugin.getDefault().getImage(MeposeUIConstants.KEY_IMAGE_ERROR).createImage());
        if(message == null) {
            message = "";
        }
        this.echoLabel.setText(message);
    }
    
    public void setOK(String message) {
        disposeOldIcon();
        this.iconLabel.setImage(MeposeUIPlugin.getDefault().getImage(MeposeUIConstants.KEY_IMAGE_OK).createImage());
        if(message == null) {
            message = "";
        }
        this.echoLabel.setText(message);
    }
    
    public void setNothing() {
        this.iconLabel.setImage(null);
        this.echoLabel.setText("");
    }

    private void disposeOldIcon() {
        Image image = this.iconLabel.getImage();
        if(image == null) {
            return;
        }
        image.dispose();
    }
//    public void setPropertyToWatch(String property) {
//        this.propertyToWatch = property;
//    }
    
//    public void setChildrenEnabled(boolean enabled) {
//        this.group.getChildren();
//    }

//    private void showEchoArea(boolean show) {
//        this.echoAreaGridData.exclude =  ! show;
//        layout();
//        this.group.layout();
//    }
    
}
