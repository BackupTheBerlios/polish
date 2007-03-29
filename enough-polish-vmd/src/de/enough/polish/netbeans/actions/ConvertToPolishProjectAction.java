/*
 * ConvertToPolishProjectAction.java
 *
 * Created on March 29, 2007, 6:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.netbeans.actions;

import java.awt.event.ActionEvent;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;

/**
 * @author dave
 */
public class ConvertToPolishProjectAction extends SystemAction {
    
    public ConvertToPolishProjectAction() {
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }
    
    public String getName() {
        return "Convert a JavaME project to J2ME Polish project";
    }

    public HelpCtx getHelpCtx() {
        return new HelpCtx (ConvertToPolishProjectAction.class);
    }

    public void actionPerformed(ActionEvent ev) {
//        WizardDescriptor desc = new WizardDescriptor (J2mePolishProjectWizardIterator.createIterator());
//        Dialog dia = DialogDisplayer.getDefault().createDialog(desc);
//        dia.setVisible (true);

//        Project project = Utilities.actionsGlobalContext().lookup(Project.class);
//        if (! "J2MEProject".equals (project.getClass().getSimpleName())) { // TODO - HACK because of J2MEProject is not in API
//            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message ("Select Mobile Project in Projects window before invoking this action.")); // TODO
//            return;
//        }

//        // TODO - use project.getProjectDirectory()
//        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message ("Not implemented yet!"));
    }
    
}
