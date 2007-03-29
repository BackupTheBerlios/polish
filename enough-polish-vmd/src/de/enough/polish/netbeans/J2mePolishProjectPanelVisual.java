package de.enough.polish.netbeans;

import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public class J2mePolishProjectPanelVisual extends JPanel implements DocumentListener {
    
    public static final String PROP_PROJECT_NAME = "projectName";
    
    private J2mePolishProjectWizardPanel panel;
    private int type;
    
    /** Creates new form PanelProjectLocationVisual */
    public J2mePolishProjectPanelVisual(J2mePolishProjectWizardPanel panel) {
        initComponents();
        this.panel = panel;
        this.type = type;
        // Register listener on the textFields to make the automatic updates
        projectNameTextField.getDocument().addDocumentListener( this );
        projectLocationTextField.getDocument().addDocumentListener( this );
        
        // Check for default J2ME Polish installation:
        File file = new File( "/Applications/J2ME-Polish" );
        if (file.exists()) {
            this.j2mePolishInstallationFolderField.setText( file.getAbsolutePath() );
        }
    }
    
    
    public String getProjectName() {
        return this.projectNameTextField.getText();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        projectNameLabel = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        projectLocationLabel = new javax.swing.JLabel();
        projectLocationTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        createdFolderLabel = new javax.swing.JLabel();
        createdFolderTextField = new javax.swing.JTextField();
        createdFolderLabel1 = new javax.swing.JLabel();
        j2mePolishInstallationFolderField = new javax.swing.JTextField();
        browseButton1 = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        projectNameLabel.setLabelFor(projectNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectNameLabel, "Project &Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        add(projectNameLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 0);
        add(projectNameTextField, gridBagConstraints);

        projectLocationLabel.setLabelFor(projectLocationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(projectLocationLabel, "Project &Location:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(projectLocationLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 5, 0);
        add(projectLocationTextField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, "Br&owse...");
        browseButton.setActionCommand("BROWSE");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 0);
        add(browseButton, gridBagConstraints);

        createdFolderLabel.setLabelFor(createdFolderTextField);
        org.openide.awt.Mnemonics.setLocalizedText(createdFolderLabel, "Project Folder:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(createdFolderLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        add(createdFolderTextField, gridBagConstraints);

        createdFolderLabel1.setLabelFor(createdFolderTextField);
        org.openide.awt.Mnemonics.setLocalizedText(createdFolderLabel1, "J2ME Polish Installation Folder:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        add(createdFolderLabel1, gridBagConstraints);

        j2mePolishInstallationFolderField.setName("j2mepolishInstallFolderField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(j2mePolishInstallationFolderField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(browseButton1, "Br&owse...");
        browseButton1.setActionCommand("BROWSE");
        browseButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseJ2mePolishInstallationFolderActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        add(browseButton1, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void chooseJ2mePolishInstallationFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseJ2mePolishInstallationFolderActionPerformed
        String command = evt.getActionCommand();
        if ( "BROWSE".equals( command ) ) {
            // problem: on Mac OS X a FileDialog is preferred over JFileChooser(), but it requires a Frame
//            FileDialog dialog = new FileDialog( this.panel, "Choose J2ME Polish Localization");
//            dialog.show();
                    
            JFileChooser chooser = new JFileChooser();
            FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
            chooser.setDialogTitle("Select J2ME Polish Location");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            String path = this.projectLocationTextField.getText();
            if (path.length() > 0) {
                File f = new File(path);
                if (f.exists()) {
                    chooser.setSelectedFile(f);
                }
            }
            if ( JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
                File projectDir = chooser.getSelectedFile();
                j2mePolishInstallationFolderField.setText( FileUtil.normalizeFile(projectDir).getAbsolutePath() );
            }
            panel.fireChangeEvent();
        }
    }//GEN-LAST:event_chooseJ2mePolishInstallationFolderActionPerformed
    
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        String command = evt.getActionCommand();
        if ( "BROWSE".equals( command ) ) {
            JFileChooser chooser = new JFileChooser();
            FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
            chooser.setDialogTitle("Select Project Location");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            String path = this.projectLocationTextField.getText();
            if (path.length() > 0) {
                File f = new File(path);
                if (f.exists()) {
                    chooser.setSelectedFile(f);
                }
            }
            if ( JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
                File projectDir = chooser.getSelectedFile();
                projectLocationTextField.setText( FileUtil.normalizeFile(projectDir).getAbsolutePath() );
            }
            panel.fireChangeEvent();
        }
        
    }//GEN-LAST:event_browseButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton browseButton1;
    private javax.swing.JLabel createdFolderLabel;
    private javax.swing.JLabel createdFolderLabel1;
    private javax.swing.JTextField createdFolderTextField;
    private javax.swing.JTextField j2mePolishInstallationFolderField;
    private javax.swing.JLabel projectLocationLabel;
    private javax.swing.JTextField projectLocationTextField;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JTextField projectNameTextField;
    // End of variables declaration//GEN-END:variables
    
    private void browseLocationAction(java.awt.event.ActionEvent evt) {
    }
    
    
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        projectNameTextField.requestFocus();
    }
    
    boolean valid( WizardDescriptor wizardDescriptor ) {
        
        if ( projectNameTextField.getText().length() == 0 ) {
            wizardDescriptor.putProperty( "WizardPanel_errorMessage",
                    "Project Name is not a valid folder name.");
            return false; // Display name not specified
        }
        File f = FileUtil.normalizeFile(new File(projectLocationTextField.getText()).getAbsoluteFile());
        if (!f.isDirectory()) {
            String message = "Project Folder is not a valid path.";
            wizardDescriptor.putProperty("WizardPanel_errorMessage", message);
            return false;
        }
        final File destFolder = FileUtil.normalizeFile(new File(createdFolderTextField.getText()).getAbsoluteFile());
        
        File projLoc = destFolder;
        while (projLoc != null && !projLoc.exists()) {
            projLoc = projLoc.getParentFile();
        }
        if (projLoc == null || !projLoc.canWrite()) {
            wizardDescriptor.putProperty( "WizardPanel_errorMessage",
                    "Project Folder cannot be created.");
            return false;
        }
        
        if (FileUtil.toFileObject(projLoc) == null) {
            String message = "Project Folder is not a valid path.";
            wizardDescriptor.putProperty("WizardPanel_errorMessage", message);
            return false;
        }
        
        File[] kids = destFolder.listFiles();
        if ( destFolder.exists() && kids != null && kids.length > 0) {
            // Folder exists and is not empty
            wizardDescriptor.putProperty( "WizardPanel_errorMessage",
                    "Project Folder already exists and is not empty.");
            return false;
        }
        wizardDescriptor.putProperty( "WizardPanel_errorMessage", "");
        return true;
    }
    
    void store( WizardDescriptor d ) {
        
        String name = projectNameTextField.getText().trim();
        String location = projectLocationTextField.getText().trim();
        String folder = createdFolderTextField.getText().trim();
        
        d.putProperty(  "projdir", new File( folder ));
        d.putProperty(  "name", name );
    }
    
    void read(WizardDescriptor settings) {
        File projectLocation = (File) settings.getProperty("projdir");
        if (projectLocation == null || projectLocation.getParentFile() == null || !projectLocation.getParentFile().isDirectory()) {
            projectLocation = ProjectChooser.getProjectsFolder();
        } else {
            projectLocation = projectLocation.getParentFile();
        }
        this.projectLocationTextField.setText(projectLocation.getAbsolutePath());
        
        String projectName = (String) settings.getProperty("name");
        if (projectName == null) {
            projectName = "J2mePolishProject";
        }
        this.projectNameTextField.setText(projectName);
        this.projectNameTextField.selectAll();
    }
    
    void validate(WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }
    
    
    // Private methods ---------------------------------------------------------
    
    private static JFileChooser createChooser() {
        JFileChooser chooser = new JFileChooser();
        FileUtil.preventFileChooserSymlinkTraversal(chooser, null);
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        chooser.setAcceptAllFileFilterUsed( false );
        chooser.setName( "Select Project Directory" );
        return chooser;
    }
    
    private String validFreeProjectName(final File parentFolder, final String formater, final int index) {
        String name = MessageFormat.format(formater, new Object[]{new Integer(index)});
        File file = new File(parentFolder, name);
        return file.exists() ? null : name;
    }
    
    // Implementation of DocumentListener --------------------------------------
    
    public void changedUpdate( DocumentEvent e ) {
        updateTexts( e );
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange(PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
        }
    }
    
    public void insertUpdate( DocumentEvent e ) {
        updateTexts( e );
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange(PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
        }
    }
    
    public void removeUpdate( DocumentEvent e ) {
        updateTexts( e );
        if (this.projectNameTextField.getDocument() == e.getDocument()) {
            firePropertyChange(PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
        }
    }
    
    
    /** Handles changes in the Project name and project directory
     */
    private void updateTexts( DocumentEvent e ) {
        
        Document doc = e.getDocument();
        
        if ( doc == projectNameTextField.getDocument() || doc == projectLocationTextField.getDocument() ) {
            // Change in the project name
            
            String projectName = projectNameTextField.getText();
            String projectFolder = projectLocationTextField.getText();
            
            //if ( projectFolder.trim().length() == 0 || projectFolder.equals( oldName )  ) {
            createdFolderTextField.setText( projectFolder + File.separatorChar + projectName );
            //}
            
        }
        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
}
