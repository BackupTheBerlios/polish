/*
 * PolishAdvancedOption.java
 *
 * Created on March 29, 2007, 11:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.settings;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 * @author dave
 */
public class PolishAdvancedOption extends AdvancedOption {
    
    public PolishAdvancedOption() {
    }
    
    public String getDisplayName() {
        return "J2ME Polish Plug-in";
    }

    public String getTooltip() {
        return "Settings for J2ME Polish plugin";
    }

    public OptionsPanelController create() {
        return new Controller ();
    }
    
    private static class Controller extends OptionsPanelController {
        
        private StartPanel panel = new StartPanel ();
    
        public void update() {
            panel.load ();
        }

        public void applyChanges() {
            panel.store ();
        }

        public void cancel() {
        }

        public boolean isValid() {
            return true; // TODO - check validity
        }

        public boolean isChanged() {
            return panel.isChanged ();
        }

        public JComponent getComponent(Lookup masterLookup) {
            return panel;
        }

        public HelpCtx getHelpCtx() {
            return new HelpCtx (StartPanel.class);
        }

        public void addPropertyChangeListener(PropertyChangeListener l) {
            panel.addListener (l);
        }

        public void removePropertyChangeListener(PropertyChangeListener l) {
            panel.removeListener (l);
       }
}

}
