/*
 * PolishWarmUp.java
 *
 * Created on March 29, 2007, 10:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.settings;

import java.awt.Dialog;
import javax.swing.SwingUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;

/**
 * @author dave
 */
public class PolishWarmUp implements Runnable {
    
    public PolishWarmUp() {
    }
    
    public void run() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (PolishSettings.getDefault().isDoNotAskForPolishHome())
                    return;
                StartPanel panel = new StartPanel ();
                panel.load ();
                DialogDescriptor dd = new DialogDescriptor (panel, "Enter Polish Installation Directory", true, new Object[] { DialogDescriptor.OK_OPTION }, DialogDescriptor.OK_OPTION, DialogDescriptor.DEFAULT_ALIGN, new HelpCtx (StartPanel.class), null);
                Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
                dialog.setVisible (true);
                panel.store ();
            }
        });
    }
    
}
