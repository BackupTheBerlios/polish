package de.enough.polish.netbeans;

import de.enough.polish.ide.swing.DeviceSelector;
import de.enough.polish.ide.swing.DirectInvocationHandler;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.filesystems.FileUtil;

public class SelectTargetDevicesPanelVisual extends JPanel implements DocumentListener, PropertyChangeListener {
    
    public static final String PROP_PROJECT_NAME = "projectName";
    
    private SelectTargetDevicesWizardPanel panel;
    private int type;
    private JComponent deviceSelectionComponent;
    private boolean isWrongJ2mePolishHomeError;
    private DeviceSelector deviceSelector;
    
    /** Creates new form PanelProjectLocationVisual */
    public SelectTargetDevicesPanelVisual(SelectTargetDevicesWizardPanel panel) {
        initComponents();
        this.panel = panel;
        this.type = type;
        
    }
    
   
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 499, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 429, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    public void addNotify() {
        super.addNotify();
        //same problem as in 31086, initial focus on Cancel button
        //projectNameTextField.requestFocus();
    }
    
    boolean valid(WizardDescriptor wizardDescriptor) {
        
        if (this.isWrongJ2mePolishHomeError) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", "Unable to read selected devices - check your J2ME Polish installation.\nYou will need at least J2ME Polish 2.0 Beta 4 or later.");
            return false;
        }
//        if (this.deviceSelector.getSelectedDeviceIdentifiers().length == 0 ) {
//            wizardDescriptor.putProperty("WizardPanel_errorMessage", "Please select at least one target device.");
//            return false;            
//        }
        wizardDescriptor.putProperty("WizardPanel_errorMessage", "");
        return true;
    }
    
    void store(WizardDescriptor d) {
        if (this.isWrongJ2mePolishHomeError) {
            // store is also called when the user wants to go back
            return;
        }
        try {
            Map properties = this.deviceSelector.getSelectedDeviceProperties();
            //System.out.println("properties of selected devices: " + properties );
            String[] deviceIdentifiers = this.deviceSelector.getSelectedDeviceIdentifiers();
            d.putProperty( "polish.devices", properties );
//            Method method = this.deviceSelectionComponent.getClass().getMethod("getSelectedDeviceIdentifiers", new Class[0]);
//            String[] deviceIdentifiers = (String[]) method.invoke( this.deviceSelectionComponent, new Object[0]);
//            d.putProperty( "polish.DeviceIdentifiers", deviceIdentifiers );
//            for (int i = 0; i < deviceIdentifiers.length; i++) {
//                System.out.println( deviceIdentifiers[i]);
//            }
//            d.putProperty( "polish.DevicesSelectionComponent", this.deviceSelectionComponent );
        } catch (Exception e) {
            e.printStackTrace();
            remove( this.deviceSelectionComponent );
            add( new JTextField("Unable to read selected devices - check your J2ME Polish installation." +
                    "\nYou will need at least J2ME Polish 2.0 Beta 4 or later." +
                    "\nException: " + e.toString() ), BorderLayout.CENTER );
        }
    }
    
    void read(WizardDescriptor settings) {
        String polishHome =  (String) settings.getProperty("polish.home");
        if (polishHome == null || polishHome.length() == 0) {
            throw new IllegalStateException("No J2ME Polish installation directory has been defined.");
        }
        if (polishHome.charAt(0) != '/') {
                polishHome = '/' + polishHome; 
        }
        if (this.deviceSelectionComponent == null) {
            setLayout( new BorderLayout() );
            JComponent component = (JComponent) settings.getProperty("polish.DevicesSelectionComponent");
            if (component != null) {
                    this.deviceSelectionComponent = component;
                    add( component, BorderLayout.CENTER );
            } else {
                try {
                    URL[] urls = new URL[] {
                        new URL("file://" + polishHome + "/lib/enough-j2mepolish-build.jar"),
                        new URL("file://" + polishHome + "/lib/jdom.jar"),
                        new URL("file://" + polishHome + "/lib/ant.jar")
                    };
                    //System.out.println(polishHome + "/lib/enough-j2mepolish-build.jar exists: " + (new File(polishHome + "/lib/enough-j2mepolish-build.jar").exists()) );
                    //System.out.println(polishHome + "/lib/jdom.jar exists: " + (new File(polishHome + "/lib/jdom.jar").exists()) );
                    //System.out.println(polishHome + "/lib/ant.jar exists: " + (new File(polishHome + "/lib/ant.jar").exists()) );
                    URLClassLoader urlClassLoader = new URLClassLoader( urls, getClass().getClassLoader() );
                    Class componentClass = urlClassLoader.loadClass("de.enough.polish.ide.swing.DeviceSelectionComponent");
                    Constructor constructor = componentClass.getConstructor( new Class[]{ String.class } );
                    component = (JComponent) constructor.newInstance( new Object[]{ polishHome} );
                    this.deviceSelectionComponent = component;
                    add( component, BorderLayout.CENTER );
                    component.addPropertyChangeListener( this );
                    settings.putProperty("polish.DeviceSelectionComponent", component );
                    
                    this.deviceSelector = (DeviceSelector) Proxy.newProxyInstance(
                        DeviceSelector.class.getClassLoader(),
                        new Class[] { DeviceSelector.class },
                        new DirectInvocationHandler(this.deviceSelectionComponent)
                    );
                    settings.putProperty( "polish.devicesselector",  deviceSelector );
                  
                            
                } catch (Exception ex) {
                    this.isWrongJ2mePolishHomeError = true;
                    ex.printStackTrace();
                }
            }
        }
    }
    
    void validate(WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
        if (this.isWrongJ2mePolishHomeError) {
            String message = "Wrong J2ME Polish Location specified. You will need at least J2ME Polish 2.0 Beta 4 or later.";
            throw new WizardValidationException(this, message, message);
        }
    }
    
//    // Implementation of DocumentListener --------------------------------------
//    
//    public void changedUpdate(DocumentEvent e) {
//        updateTexts(e);
//        if (this.projectNameTextField.getDocument() == e.getDocument()) {
//            firePropertyChange(PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
//        }
//    }
//    
//    public void insertUpdate(DocumentEvent e) {
//        updateTexts(e);
//        if (this.projectNameTextField.getDocument() == e.getDocument()) {
//            firePropertyChange(PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
//        }
//    }
//    
//    public void removeUpdate(DocumentEvent e) {
//        updateTexts(e);
//        if (this.projectNameTextField.getDocument() == e.getDocument()) {
//            firePropertyChange(PROP_PROJECT_NAME,null,this.projectNameTextField.getText());
//        }
//    }
//    
//    /** Handles changes in the Project name and project directory, */
//    private void updateTexts(DocumentEvent e) {
//        
//        Document doc = e.getDocument();
//        
//        if (doc == projectNameTextField.getDocument() || doc == projectLocationTextField.getDocument()) {
//            // Change in the project name
//            
//            String projectName = projectNameTextField.getText();
//            String projectFolder = projectLocationTextField.getText();
//            
//            //if (projectFolder.trim().length() == 0 || projectFolder.equals(oldName)) {
//            createdFolderTextField.setText(projectFolder + File.separatorChar + projectName);
//            //}
//            
//        }
//        panel.fireChangeEvent(); // Notify that the panel changed
//    }

    public void insertUpdate(DocumentEvent e) {
    }

    public void removeUpdate(DocumentEvent e) {
    }

    public void changedUpdate(DocumentEvent e) {
    }

    public void propertyChange(PropertyChangeEvent event) {
        System.out.println("property change event: " + event );
        this.panel.fireChangeEvent();
    }


    
}
