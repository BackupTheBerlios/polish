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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.enough.mepose.MeposeCoreUIConstants;
import de.enough.mepose.ui.UIPlugin;

/**
 * 
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

    public StatusGroup(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(1,false));
        this.group = new Group(this,SWT.NONE);
        this.group.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        this.group.setLayout(new GridLayout(1,false));
        
        this.mainArea = new Composite(this.group,SWT.NONE);
        this.mainArea.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        
        this.echoArea = new Composite(this.group,SWT.NONE);
        this.echoAreaGridData = new GridData(SWT.FILL,SWT.CENTER,true,false);
        this.echoArea.setLayoutData(this.echoAreaGridData);
        this.echoArea.setLayout(new GridLayout(2,false));
        
        this.iconLabel = new Label(this.echoArea,SWT.NONE);
        
        this.echoLabel = new Label(this.echoArea,SWT.NONE);
        this.echoLabel.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
    }
    
    public Composite getMainComposite() {
        return this.mainArea;
    }
    
    // TODO: This is such an ugly API. But we are not allowed to subclass group
    // and duplicating the API is tiresom.
    public Group getGroup() {
        return this.group;
    }

    public void setWarning(String message) {
//        showEchoArea(true);
        this.iconLabel.setImage(UIPlugin.getDefault().getImage(MeposeCoreUIConstants.KEY_IMAGE_WARNING).createImage());
        if(message == null) {
            message = "";
        }
        this.echoLabel.setText(message);
    }
    
    public void setError(String message) {
//        showEchoArea(true);
        this.iconLabel.setImage(UIPlugin.getDefault().getImage(MeposeCoreUIConstants.KEY_IMAGE_ERROR).createImage());
        if(message == null) {
            message = "";
        }
        this.echoLabel.setText(message);
    }
    
    public void setOK(String message) {
//        showEchoArea(true);
        this.iconLabel.setImage(UIPlugin.getDefault().getImage(MeposeCoreUIConstants.KEY_IMAGE_OK).createImage());
        this.iconLabel.pack();
        if(message == null) {
            message = "";
        }
        this.echoLabel.setText(message);
    }
    
    public void setNothing() {
        this.iconLabel.setImage(null);
        this.echoLabel.setText("");
    }

//    private void showEchoArea(boolean show) {
//        this.echoAreaGridData.exclude =  ! show;
//        layout();
//        this.group.layout();
//    }
    
}
