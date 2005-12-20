/*
 * Created on Dec 9, 2005 at 2:15:50 PM.
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

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Dec 9, 2005 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PathsPage extends WizardPage {

    private NewPolishProjectDAO newPolishProjectDAO;
    private Text polishHomeText;

    protected PathsPage(NewPolishProjectDAO newPolishProjectDAO) {
        super("Paths and Locations");
        this.newPolishProjectDAO = newPolishProjectDAO;
        setTitle("Paths and Locations");
        setDescription("Specify where to find the required locations.");
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent,SWT.NONE);
        composite.setLayout(new GridLayout(3,false));
        
        Label polishHomeLabel = new Label(composite,SWT.NONE);
        polishHomeLabel.setText("J2ME Polish Home:");
        
        this.polishHomeText = new Text(composite,SWT.NONE);
        this.polishHomeText.setText(this.newPolishProjectDAO.getPolishHome().toString());
        this.polishHomeText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        Button browseButton = new Button(composite,SWT.NONE);
        browseButton.setText("Browse for Path");
        browseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleBrowse();
            }
        });
        setControl(composite);
        
    }

    protected void handleBrowse() {
        FileDialog fileDialog = new FileDialog(getShell(),SWT.OPEN);
        String pathString = fileDialog.open();
        if(pathString != null) {
//            IPath path = new Path(pathString);
            //TODO: check if the path is only a dir, not a file.
            this.newPolishProjectDAO.setPolishHome(new File(pathString));
            this.polishHomeText.setText(pathString);
        }
    }

}
