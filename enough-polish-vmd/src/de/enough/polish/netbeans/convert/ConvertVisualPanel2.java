package de.enough.polish.netbeans.convert;

import de.enough.polish.ide.swing.DeviceSelectionComponent;
import de.enough.polish.plugin.netbeans.settings.PolishSettings;
import org.openide.util.NbBundle;

import javax.swing.*;
import java.awt.*;

public final class ConvertVisualPanel2 extends JPanel {

    private DeviceSelectionComponent component;

    /** Creates new form ConvertVisualPanel2 */
    public ConvertVisualPanel2() {
        initComponents();
    }
    
    public String getName() {
        return "Select Devices";
    }

    public void reload (boolean toPolish) {
        removeAll ();
        if (toPolish) {
            component = new DeviceSelectionComponent (PolishSettings.getDefault ().getPolishHome ());
            add (component, BorderLayout.CENTER);
        } else {
            component = null;
            add (new JLabel (NbBundle.getMessage (ConvertVisualPanel2.class, "TXT_NoAdditionalDataRequired")), BorderLayout.CENTER);
        }
    }


    public String[] getSelectedDeviceIdentifiers () {
        return component.getSelectedDeviceIdentifiers ();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}

