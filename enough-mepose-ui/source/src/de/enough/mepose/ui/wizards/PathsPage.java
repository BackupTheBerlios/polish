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
package de.enough.mepose.ui.wizards;

import java.io.File;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.enough.mepose.core.MeposeConstants;
import de.enough.mepose.core.MeposePlugin;
import de.enough.swt.widgets.StatusGroup;

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

    private NewProjectModel newProjectModel;
    
    private Text wtkHomeText;
    private Text nokiaHomeText;
    private Text sonyHomeText;

    private StatusGroup wtkStatusGroup;
    private StatusGroup nokiaStatusGroup;
    private StatusGroup sonyStatusGroup;

    private StatusGroup polishStatusGroup;

    private Text polishHomeText;

    
    private class BrowsePathSelected extends SelectionAdapter{
        
        private Text textfieldToUpdate;
        private String messageToAsk;

        public BrowsePathSelected(Text textfieldToUpdate, String messageToAsk) {
            this.textfieldToUpdate = textfieldToUpdate;
            this.messageToAsk = messageToAsk;
        }
        
        public void widgetSelected(SelectionEvent e) {
            handleBrowseButtonSelected();
        }
        protected void handleBrowseButtonSelected() {
            DirectoryDialog directoryDialog = new DirectoryDialog(getShell(),SWT.OPEN);
            directoryDialog.setMessage(this.messageToAsk);
            String path = directoryDialog.open();
            if(path == null) {
                path = "";
            }
            this.textfieldToUpdate.setText(path);
        }
    }
    
    protected PathsPage(NewProjectModel newPolishProjectDAO) {
        super("Paths and Locations");
        this.newProjectModel = newPolishProjectDAO;
        setTitle("Paths and Locations");
        setDescription("Specify where to find the required resources");
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent,SWT.NONE);
        composite.setLayout(new GridLayout(1,false));
        
        Composite main;
        Button browseButton;

        this.wtkStatusGroup = new StatusGroup(composite,SWT.NONE);
        this.wtkStatusGroup.setLayoutData(new GridData(SWT.FILL,SWT.BEGINNING,true,false));
        main = this.wtkStatusGroup.getMainComposite();
        main.setLayout(new GridLayout(3,false));
        Label wtkLabel = new Label(main,SWT.NONE);
        wtkLabel.setText("WTK Home:");
        
        this.wtkHomeText = new Text(main,SWT.BORDER);
        this.wtkHomeText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        this.wtkHomeText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                modifyWtkHomeText();
            }
        });
        String wtkHomePath = MeposePlugin.getDefault().getPluginPreferences().getString(MeposeConstants.ID_WTK_HOME);
        this.wtkHomeText.setText(wtkHomePath);
        modifyWtkHomeText();
        
        browseButton = new Button(main,SWT.NONE);
        browseButton.setText("Browse for Path");
        browseButton.addSelectionListener(new BrowsePathSelected(this.wtkHomeText,"Choose the location of your WTK directory"));
                                          
        // ----------------------
        
        this.nokiaStatusGroup = new StatusGroup(composite,SWT.NONE);
        this.nokiaStatusGroup.setLayoutData(new GridData(SWT.FILL,SWT.BEGINNING,true,false));
        main = this.nokiaStatusGroup.getMainComposite();
        main.setLayout(new GridLayout(3,false));
        Label nokiaLabel = new Label(main,SWT.NONE);
        nokiaLabel.setText("Nokia Home:");
        
        this.nokiaHomeText = new Text(main,SWT.BORDER);
        this.nokiaHomeText.setLayoutData(new GridData(SWT.FILL,SWT.BEGINNING,true,false));
        this.nokiaHomeText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                modifyNokiaHomeText();
            }
        });
        String NokiaHomePath = MeposePlugin.getDefault().getPluginPreferences().getString(MeposeConstants.ID_NOKIA_HOME);
        this.nokiaHomeText.setText(NokiaHomePath);
        modifyNokiaHomeText();
        browseButton = new Button(main,SWT.NONE);
        browseButton.setText("Browse for Path");
        browseButton.addSelectionListener(new BrowsePathSelected(this.nokiaHomeText,"Choose the location of your Nokia directory"));
        
        // ----------------------
        
        this.sonyStatusGroup = new StatusGroup(composite,SWT.NONE);
        this.sonyStatusGroup.setLayoutData(new GridData(SWT.FILL,SWT.BEGINNING,true,false));
        main = this.sonyStatusGroup.getMainComposite();
        main.setLayout(new GridLayout(3,false));
        Label sonyLabel = new Label(main,SWT.NONE);
        sonyLabel.setText("Sony Home:");
        
        this.sonyHomeText = new Text(main,SWT.BORDER);
        this.sonyHomeText.setLayoutData(new GridData(SWT.FILL,SWT.BEGINNING,true,false));
        this.sonyHomeText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                modifySonyHomeText();
            }
        });
        String sonyHomePath = MeposePlugin.getDefault().getPluginPreferences().getString(MeposeConstants.ID_SONY_HOME);
        this.sonyHomeText.setText(sonyHomePath);
        modifySonyHomeText();
        browseButton = new Button(main,SWT.NONE);
        browseButton.setText("Browse for Path");
        browseButton.addSelectionListener(new BrowsePathSelected(this.sonyHomeText,"Choose the location of your Sony directory"));
        
        // ----------------------
        
        this.polishStatusGroup = new StatusGroup(composite,SWT.NONE);
        this.polishStatusGroup.setLayoutData(new GridData(SWT.FILL,SWT.BEGINNING,true,false));
        main = this.polishStatusGroup.getMainComposite();
        main.setLayout(new GridLayout(3,false));
        Label polishLabel = new Label(main,SWT.NONE);
        polishLabel.setText("J2ME Polish Home:");
        
        this.polishHomeText = new Text(main,SWT.BORDER);
        this.polishHomeText.setLayoutData(new GridData(SWT.FILL,SWT.BEGINNING,true,false));
        this.polishHomeText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                modifyPolishHomeText();
            }

        });
        String polishHomePath = MeposePlugin.getDefault().getPluginPreferences().getString(MeposeConstants.ID_POLISH_HOME);
        this.polishHomeText.setText(polishHomePath);
        modifyPolishHomeText();
        browseButton = new Button(main,SWT.NONE);
        browseButton.setText("Browse for Path");
        browseButton.addSelectionListener(new BrowsePathSelected(this.polishHomeText,"Choose the location of your J2ME Polish directory"));
        
        // ----------------------------------
        
        setControl(composite);
        updateGUIFromModel();
    }

    protected void modifyPolishHomeText() {
        String path = this.polishHomeText.getText();
        File file = new File(path);
        if( ! file.exists() || ! file.isDirectory()) {
            this.polishStatusGroup.setError("Path is not a directory.");
        }
        //TODO: Add something to recognize a polish directory.
        else {
            this.newProjectModel.getMeposeModel().setPolishHome(file);
            this.polishStatusGroup.setOK("");
        }
    }

    /**
     * 
     */
    protected void modifyNokiaHomeText() {
        String path = this.nokiaHomeText.getText();
        File file = new File(path);
        if( ! file.exists() || ! file.isDirectory()) {
            this.nokiaStatusGroup.setError("Path is not a directory.");
        }
        else {
            this.newProjectModel.getMeposeModel().setNokiaHome(file);
            this.nokiaStatusGroup.setOK("");
        }
    }
    
    protected void modifySonyHomeText() {
        String path = this.sonyHomeText.getText();
        File file = new File(path);
        if( ! file.exists() || ! file.isDirectory()) {
            this.sonyStatusGroup.setError("Path is not a directory.");
        }
        else {
            this.newProjectModel.getMeposeModel().setSonyHome(file);
            this.sonyStatusGroup.setOK("");
        }
    }

    /**
     * 
     */
    protected void modifyWtkHomeText() {
        String path = PathsPage.this.wtkHomeText.getText();
        File file = new File(path);
        if( ! file.exists() || ! file.isDirectory()) {
            this.wtkStatusGroup.setError("Path is not a directory.");
        }
        else {
            PathsPage.this.newProjectModel.getMeposeModel().setWTKHome(file);
            this.wtkStatusGroup.setOK("");
        }
    }

    
    
//    private void createRow(Composite parent,String labelString,final String directoryMessage) {
//        StatusGroup statusGroup = new StatusGroup(parent,SWT.NONE);
//        Group main = statusGroup.getGroup();
//        main.setLayout(new GridLayout(3,false));
//        Label label = new Label(main,SWT.NONE);
//        label.setText(labelString);
//        final Text text = new Text(main,SWT.NONE);
//        text.addModifyListener(new ModifyListener() {
//            public void modifyText(ModifyEvent e) {
//                updateModelFromGUI();
//                e.
//            }
//        });
//        Button browseButton = new Button(main,SWT.NONE);
//        browseButton.setText("Browse for Path");
//        browseButton.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent e) {
//                text.setText(handleBrowse(directoryMessage));
//            }
//        });
//        
//    }
    
    
    protected void updateModelFromGUI() {
        String wtkHome = this.wtkHomeText.getText();
        String nokiaHome = this.nokiaHomeText.getText();
        String polishHome = this.polishHomeText.getText();
//        this.newProjectModel.getMeposeModel().setPropertyValue(MeposeModel.ID_POLISH_HOME,polishHome);
        File wtkHomeFile = new File((wtkHome==null)?"":wtkHome);
        File nokiaHomeFile = new File((nokiaHome==null)?"":nokiaHome);
        File polishHomeFile = new File((polishHome==null)?"":polishHome);
        this.newProjectModel.getMeposeModel().setWTKHome(wtkHomeFile);
        this.newProjectModel.getMeposeModel().setNokiaHome(nokiaHomeFile);
        this.newProjectModel.getMeposeModel().setPolishHome(polishHomeFile);
    }
    
    protected void updateGUIFromModel() {
//        File polishHomeString = (File)(this.newProjectModel.getMeposeModel().getPropertyValue(MeposeModel.ID_POLISH_HOME));
//        this.wtkHomeText.setText((polishHomeString == null)?"":polishHomeString.getAbsolutePath());
        // TODO: Get the other values.
    }

    

    public IWizardPage getNextPage() {
        PlatformPage pp = (PlatformPage)super.getNextPage();
        updateModelFromGUI();
        pp.fillGUI();
        return pp;
    }

    
    public boolean canFlipToNextPage() {
        boolean anyWtkPresent = anyWtkPresent();
        return anyWtkPresent;
    }

    private boolean anyWtkPresent() {
//        MeposeModel meposeModel = this.newProjectModel.getMeposeModel();
//        if(meposeModel.getWTKHome() != null && meposeModel.getWTKHome().exists()) {
//            return true;
//        }
        // mpp is present.
        return true;
    }
    
    

    
}
