package de.enough.polish.netbeans;

import de.enough.polish.ide.swing.DeviceSelector;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileUtil;

public class SetupProjectPanelVisual extends JPanel implements DocumentListener {
    
    public static final String PROP_PROJECT_NAME = "projectName";
    
    private SetupProjectWizardPanel panel;
    private int type;
    
    /** Creates new form PanelProjectLocationVisual */
    public SetupProjectPanelVisual(SetupProjectWizardPanel panel) {
        initComponents();
        this.panel = panel;
        this.type = type;
        // Register listener on the textFields to make the automatic updates
//        projectNameTextField.getDocument().addDocumentListener(this);
//        projectLocationTextField.getDocument().addDocumentListener(this);
        
        // check for default J2ME Polish installation directory:
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        activatePolishGuiLabel = new javax.swing.JLabel();
        activatePolishGuiCheckBox = new javax.swing.JCheckBox();
        projectNameLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        textFieldEmulatorPlatform = new javax.swing.JTextField();
        comboBoxTemplate = new javax.swing.JComboBox();

        org.openide.awt.Mnemonics.setLocalizedText(activatePolishGuiLabel, "&Activate J2ME Polish GUI:");

        activatePolishGuiCheckBox.setSelected(true);
        activatePolishGuiCheckBox.setToolTipText("Uncheck this element if you do not want to use the advanced J2ME Polish GUI for your project.");
        activatePolishGuiCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        activatePolishGuiCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(projectNameLabel3, "Template");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Emulator Platform");
        jLabel1.setName("labelEmulatorPlatform");

        textFieldEmulatorPlatform.setText("MPowerPlayer");
        textFieldEmulatorPlatform.setName("textFieldEmulatorPlatform");

        comboBoxTemplate.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(projectNameLabel3)
                    .add(jLabel1)
                    .add(activatePolishGuiLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 18, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(activatePolishGuiCheckBox)
                        .add(317, 317, 317))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(comboBoxTemplate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 315, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(textFieldEmulatorPlatform, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(activatePolishGuiLabel)
                    .add(activatePolishGuiCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(projectNameLabel3)
                    .add(comboBoxTemplate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(textFieldEmulatorPlatform, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(214, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox activatePolishGuiCheckBox;
    private javax.swing.JLabel activatePolishGuiLabel;
    private javax.swing.JComboBox comboBoxTemplate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel projectNameLabel3;
    private javax.swing.JTextField textFieldEmulatorPlatform;
    // End of variables declaration//GEN-END:variables
    
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
    }
    
    boolean valid(WizardDescriptor wizardDescriptor) {
        wizardDescriptor.putProperty("WizardPanel_errorMessage", "");
        return true;
    }
    
    void store(WizardDescriptor settings) {
        settings.putProperty("polish.EmulatorPlatform", this.textFieldEmulatorPlatform.getText() );
        // read template:
        String template = (String) this.comboBoxTemplate.getSelectedItem();
        settings.putProperty("polish.template", template );
        if (this.activatePolishGuiCheckBox.isSelected()) {
            settings.putProperty("poliush.usePolishGui", "true");
        } else {
            settings.putProperty("poliush.usePolishGui", "false");
        }
    }
    
    void read(WizardDescriptor settings) {
        //TODO find out which emulator platform to use!
        if (File.separatorChar == '\\') {
            // windows:
            this.textFieldEmulatorPlatform.setText("J2ME_Wireless_Toolkit_2_2");
        }
        String polishHome =  (String) settings.getProperty("polish.home");
        ArrayList templatesList = new ArrayList();
        File samplesDir = new File( polishHome + "/samples");
        File[] samples = samplesDir.listFiles();
        for (int i = 0; i < samples.length; i++) {
            File file = samples[i];
            if (file.isDirectory()) {
                templatesList.add( file.getName() );
            }
        }
        //TODO add other netbeans templates..
        this.comboBoxTemplate.removeAllItems();
        for (Iterator it = templatesList.iterator(); it.hasNext();) {
            String templateName = (String) it.next();
            this.comboBoxTemplate.addItem( templateName );
        }


    }
    
    void validate(WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }
    
    // Implementation of DocumentListener --------------------------------------
    
    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
    }
    
    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
    }
    
    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
    }
    
    /** Handles changes in the Project name and project directory, */
    private void updateTexts(DocumentEvent e) {
        
        Document doc = e.getDocument();
        
        panel.fireChangeEvent(); // Notify that the panel changed
    }

    
}
