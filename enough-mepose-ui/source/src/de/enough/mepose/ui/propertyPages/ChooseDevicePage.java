/*
 * Created on Sep 7, 2006 at 2:48:10 PM.
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
package de.enough.mepose.ui.propertyPages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.enough.mepose.core.model.MeposeModel;
import de.enough.mepose.widgets.ChooseDeviceManager;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Sep 7, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class ChooseDevicePage extends MeposePropertyPage {

    private ChooseDeviceManager chooseDeviceManager;

    protected Control createContents(Composite parent) {
        MeposeModel model = getMeposeModel();
        if(model == null) {
            return createErrorControl(parent,"The element is not within a Mepose Project.");
        }
        if(this.chooseDeviceManager == null) {
            this.chooseDeviceManager = new ChooseDeviceManager(parent,model);
        }
        Control control = this.chooseDeviceManager.getControl();
        return control;
    }

    /**
     * @param string
     * @return a control which indicates an error to the user.
     */
    private Control createErrorControl(Composite parent, String string) {
        Label label = new Label(parent,SWT.NONE);
        label.setText(string);
        return label;
    }

    public boolean performCancel() {
        if(this.chooseDeviceManager != null) {
            this.chooseDeviceManager.performCancel();
        }
        return super.performCancel();
    }

    protected void performDefaults() {
        // TODO rickyn implement performDefaults
        super.performDefaults();
    }

    public boolean performOk() {
    	if(this.chooseDeviceManager != null){
    		this.chooseDeviceManager.performOk();
    	}
        return super.performOk();
    }

    
}
