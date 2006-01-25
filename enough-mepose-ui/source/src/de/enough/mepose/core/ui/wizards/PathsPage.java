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
    private StatusGroup wtkStatusGroup;
    private StatusGroup nokiaStatusGroup;
//    private Text blackberryText;
//    private Text sonyText;
    
//    public class VerifyEvent{
//        private Object source;
//        private Object object;
//        private Status oldStatus;
//        private Status newStatus;
//        public VerifyEvent(Object source, Object object, Status oldStatus,Status newStatus) {
//            this.source = source;
//            this.object = object;
//            this.oldStatus = oldStatus;
//            this.newStatus = newStatus;
//        }
//        public Status getNewStatus() {
//            return this.newStatus;
//        }
//        public Object getObject() {
//            return this.object;
//        }
//        public Status getOldStatus() {
//            return this.oldStatus;
//        }
//        public Object getSource() {
//            return this.source;
//        }
//    }
//    
//    public interface VerifierListener{
//        public void handleVerifyEvent(VerifyEvent verifyEvent);
//    }
//    
//    //TODO: A change listener for the input would be handy.
//    public interface Verifier{
//        public void setVerifierDelegate(VerifierDelegate verifierDelegate);
//        public void removeVerifierDelegate();
//        public void addVerifierListener(VerifierListener verifierListener);
//        public void removeVerifierListener(VerifierListener verifierListener);
//        public void verify();
//        
//    }
//    
//    public interface VerifierDelegate{
//        public VerifyEvent verify();
//    }
//    
//    //TODO: Instead of an abstract class having a delegate whould be cooler as
//    // no subclassing is needed.
//    public class AbstractVerifier implements Verifier{
//        private List verifierListeners;
//        private VerifierDelegate verifierDelegate;
//        public AbstractVerifier() {
//            this.verifierListeners = new LinkedList();
//        }
//        public void addVerifierListener(VerifierListener verifierListener) {
//            this.verifierListeners.add(verifierListener);
//        }
//        public void removeVerifierListener(VerifierListener verifierListener) {
//            this.verifierListeners.remove(verifierListener);
//        }
//        protected void fireVerifyEvent(VerifyEvent verifyEvent) {
//            if(verifyEvent == null) {
//                return;
//            }
//            for (Iterator iterator = this.verifierListeners.iterator(); iterator.hasNext(); ) {
//                VerifierListener verifyListener = (VerifierListener) iterator.next();
//                verifyListener.handleVerifyEvent(verifyEvent);
//            }
//        }
//        public void verify() {
//            if(this.verifierDelegate != null) {
//                fireVerifyEvent(this.verifierDelegate.verify());
//            }
//        }
//        public void setVerifierDelegate(VerifierDelegate newVerifierDelegate) {
//            this.verifierDelegate = newVerifierDelegate;
//        }
//        public void removeVerifierDelegate() {
//            this.verifierDelegate = null;
//        }
//    }
    
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
        setDescription("Specify where to find the required resources.");
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
        
        this.wtkHomeText = new Text(main,SWT.NONE);
        this.wtkHomeText.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
        this.wtkHomeText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                modifyWtkHomeText();
            }
        });
        this.wtkHomeText.setText("");
        browseButton = new Button(main,SWT.NONE);
        browseButton.setText("Browse for Path");
        browseButton.addSelectionListener(new BrowsePathSelected(this.wtkHomeText,"Choose the location of your WTK directory"));
                                          
        // ----------------------
        
        this.nokiaStatusGroup = new StatusGroup(composite,SWT.NONE);
        this.nokiaStatusGroup.setLayoutData(new GridData(SWT.FILL,SWT.BEGINNING,true,false));
        this.nokiaStatusGroup.setLayoutData(new GridData(SWT.FILL,SWT.BEGINNING,true,false));
        main = this.nokiaStatusGroup.getMainComposite();
        main.setLayout(new GridLayout(3,false));
        Label nokiaLabel = new Label(main,SWT.NONE);
        nokiaLabel.setText("Nokia Home:");
        
        this.nokiaHomeText = new Text(main,SWT.NONE);
        this.nokiaHomeText.setLayoutData(new GridData(SWT.FILL,SWT.BEGINNING,true,false));
        this.nokiaHomeText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                modifyNokiaHomeText();
            }
        });
        this.nokiaHomeText.setText("");
        browseButton = new Button(main,SWT.NONE);
        browseButton.setText("Browse for Path");
        browseButton.addSelectionListener(new BrowsePathSelected(this.nokiaHomeText,"Choose the location of your Nokia directory"));
        
        // ----------------------------------
        
        setControl(composite);
        updateGUIFromModel();
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
//        this.newProjectModel.getMeposeModel().setPropertyValue(MeposeModel.ID_POLISH_HOME,polishHome);
        File wtkHomeFile = new File((wtkHome==null)?"":wtkHome);
        File nokiaHomeFile = new File((nokiaHome==null)?"":nokiaHome);
        this.newProjectModel.getMeposeModel().setWTKHome(wtkHomeFile);
        this.newProjectModel.getMeposeModel().setNokiaHome(nokiaHomeFile);
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
