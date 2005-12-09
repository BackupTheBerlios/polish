/*
 * Created on Dec 9, 2005 at 2:55:14 PM.
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
package de.enough.mepose.core.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 9, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PlatformPage extends WizardPage {

    private NewPolishProjectDAO newPolishProjectDAO;

    /**
     * @param pageName
     */
    protected PlatformPage(NewPolishProjectDAO newPolishProjectDAO) {
        super("Select Platform");
        this.newPolishProjectDAO = newPolishProjectDAO;
        setTitle("Platform Selection");
        setDescription("Select the the platforms you want so support");
    }

    /*
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent,SWT.NONE);
        composite.setLayout(new GridLayout(1,false));
        
        Label label = new Label(composite,SWT.NONE);
        label.setText("baum");
        
        Tree tree = new Tree(composite,SWT.CHECK);
        tree.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        TreeItem item;
        item = new TreeItem(tree,SWT.NONE);
        item.setExpanded(true);
        item.setText("Virtual Devices");
        item = new TreeItem(item,SWT.NONE);
        item.setChecked(true);
        item.setText("Generic");
        
        setControl(composite);
    }

}
