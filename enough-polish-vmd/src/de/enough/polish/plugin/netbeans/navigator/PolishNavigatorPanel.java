/*
 * PolishNavigatorPanel.java
 *
 * Created on March 28, 2007, 8:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.navigator;

import de.enough.polish.plugin.netbeans.PolishDataEditorView;
import de.enough.polish.plugin.netbeans.PolishViewController;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.netbeans.modules.vmd.api.io.IOUtils;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.util.Lookup;

/**
 *
 * @author dave
 */
public class PolishNavigatorPanel extends JPanel implements NavigatorPanel {
    
    private Lookup context;
    private PolishViewController controller;
    private DefaultListModel model;
    
    public PolishNavigatorPanel() {
        model = new DefaultListModel ();
        setLayout (new BorderLayout ());
        JList list = new JList (model);
        list.setCellRenderer(new StyleRenderer ());
        add (new JScrollPane (list), BorderLayout.CENTER);
    }
    
    public String getDisplayName() {
        return "J2ME Polish Styles";
    }

    public String getDisplayHint() {
        return "J2ME Polish styles available for edited screen";
    }

    public JComponent getComponent() {
        return this;
    }

    public void panelActivated(Lookup context) {
        this.context = context;
        PolishDataEditorView view = context.lookup(PolishDataEditorView.class);
        controller = view != null ? view.getViewController () : null;
        // TODO - if (controller != null)
        // controller.setEditedScreenInSimulatorChangeListener (this)
        reloadModel ();
    }

    public void panelDeactivated() {
        // TODO - if (controller != null)
        //     controller.setEditedScreenInSimulatorChangeListener (null);
        controller = null;
        context = null;
    }

    public Lookup getLookup() {
        return context;
    }

    private void reloadModel () {
        IOUtils.runInAWTNoBlocking(new Runnable () {
            public void run() {
                Style[] styles = StyleSheet.getDynamicStyles();
                model.clear();
                for (Style style : styles)
                    model.addElement(style);
            }
        });
    }
    
    private static class StyleRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent(list, value instanceof Style ? ((Style) value).dynamicName : null, index, isSelected, cellHasFocus);
        }
        
    }
    
}
