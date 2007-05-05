package de.enough.polish.plugin.netbeans.settings;

import java.awt.Dialog;
import javax.swing.SwingUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class PolishSettingsAction extends CallableSystemAction {

    public void performAction() {
        openSettings();
    }
    
    public String getName() {
        return NbBundle.getMessage(PolishSettingsAction.class, "CTL_PolishSettingsAction"); // NOI18N
    }
    
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    static void openSettings () {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
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
