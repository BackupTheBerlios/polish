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
package de.enough.mepose.ui.propertyPages;



import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;



public class OverviewPage extends PropertyPage{

    protected Control createContents(Composite parent) {
        
        Composite main = new Composite(parent,SWT.NONE);
        main.setLayout(new GridLayout(1,false));
        createNoteComposite(null,main,"Overview","Tweak all J2ME Polish settings in this menu.");
        return main;
//        
//        if(parent == null  || parent.isDisposed()) {
//            return null;
//        }
//        
//        this.mainComposite= new Composite(parent, SWT.NONE);
//        
//        GridLayout layout= new GridLayout();
//        layout.numColumns= 2;
//        
//        this.mainComposite.setLayout(layout);
//        this.mainComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
//
//        Label nameLabel= new Label(this.mainComposite, SWT.NONE);
//        nameLabel.setText("Buildxml file");
//        
//        this.textField = new Text(this.mainComposite, SWT.SINGLE | SWT.BORDER);
//        this.textField.setFont(parent.getFont());
//        this.textField.setText("Hallo Welt");
//        //TODO: Does not work:
//        //this.textField.setSize(50,this.textField.getLineHeight());
//        //this.textField.setTextLimit(20);
//        setTitle("Properties for your J2ME Polish Project");
//        return this.mainComposite;
    }


    
    

}
