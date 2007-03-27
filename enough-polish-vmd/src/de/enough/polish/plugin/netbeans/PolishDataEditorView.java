/*
 * PolishDataEditorView.java
 *
 * Created on March 27, 2007, 11:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;

import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.netbeans.modules.vmd.api.io.DataEditorView;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.openide.awt.UndoRedo;
import org.openide.util.HelpCtx;

/**
 *
 * @author dave
 */
public class PolishDataEditorView implements DataEditorView {
    
    private static final long serialVersionUID = -1;

    private DataObjectContext context;
    private transient PolishViewController controller;
    
    public PolishDataEditorView (DataObjectContext context) {
        this.context = context;
        init ();
    }

    private void init () {
        controller = new PolishViewController (context);
    }

    public DataObjectContext getContext() {
        return context;
    }

    public Kind getKind() {
        return DataEditorView.Kind.MODEL;
    }

    public boolean canShowSideWindows() {
        return false;
    }

    public String preferredID() {
        return PolishViewController.POLISH_ID;
    }

    public String getDisplayName() {
        return "Polish";
    }

    public HelpCtx getHelpCtx() {
        return null; // TODO
    }

    public JComponent getVisualRepresentation() {
        return controller.getVisualRepresentation ();
    }

    public JComponent getToolbarRepresentation() {
        return controller.getToolbarRepresentation ();
    }

    public UndoRedo getUndoRedo() {
        return null; // TODO - put your undo redo manager here
    }

    public void componentOpened() {
    }

    public void componentClosed() {
    }

    public void componentShowing() {
    }

    public void componentHidden() {
    }

    public void componentActivated() {
    }

    public void componentDeactivated() {
    }

    public int getOpenPriority() {
        return 500;
    }

    public int getEditPriority() {
        return 500;
    }

    public int getOrder() {
        return 500;
    }

    private void writeObject (java.io.ObjectOutputStream out) throws IOException {
        out.writeObject (context);
    }

    private void readObject (java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object object = in.readObject ();
        if (! (object instanceof DataObjectContext))
            throw new ClassNotFoundException ("DataObjectContext expected but not found");
        context = (DataObjectContext) object;
        init ();
    }

}
