/*
 * PolishViewController.java
 *
 * Created on March 27, 2007, 11:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;

import de.enough.polish.runtime.Simulation;
import de.enough.polish.runtime.SimulationDevice;
import de.enough.polish.ui.TextBox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.modules.vmd.api.io.DesignDocumentAwareness;
import org.netbeans.modules.vmd.api.io.IOUtils;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.DesignDocument;
import org.netbeans.modules.vmd.api.model.DesignEvent;
import org.netbeans.modules.vmd.api.model.DesignEventFilter;
import org.netbeans.modules.vmd.api.model.DesignListener;
import org.netbeans.modules.vmd.api.model.PropertyValue;
import org.netbeans.modules.vmd.midp.components.MidpDocumentSupport;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.MidpValueSupport;
import org.netbeans.modules.vmd.midp.components.categories.DisplayablesCategoryCD;
import org.netbeans.modules.vmd.midp.components.displayables.DisplayableCD;
import org.netbeans.modules.vmd.midp.components.displayables.TextBoxCD;

/**
 *
 * @author dave
 */
public class PolishViewController implements DesignDocumentAwareness, DesignListener {

    public static final String POLISH_ID = "polish"; // NOI18N
    
    private Simulation simulation;

    private DesignDocument designDocument;

    private JPanel visual;
    private JComponent toolbar;

//    private JComponent loadingPanel;

    public PolishViewController (DataObjectContext context) {
//        loadingPanel = IOUtils.createLoadingPanel (); // you can use this JComponent when the document is null
        
        SimulationDevice device = new SimulationDevice ();
        simulation = new Simulation (device);
        
        visual = new JPanel ();
        visual.setLayout(new GridBagLayout ());
        visual.add(simulation, new GridBagConstraints ());

        toolbar = new JLabel ("This is a polish toolbar"); // TODO

        context.addDesignDocumentAwareness (this);
    }

    public JComponent getVisualRepresentation () {
        return visual;
    }

    public JComponent getToolbarRepresentation () {
        return toolbar;
    }

    public void setDesignDocument (final DesignDocument newDesignDocument) {
        IOUtils.runInAWTNoBlocking (new Runnable () {
            public void run () {
                if (designDocument == newDesignDocument)
                    return;
                if (designDocument != null)
                    designDocument.getListenerManager().removeDesignListener(PolishViewController.this);
                designDocument = newDesignDocument;
                if (designDocument != null)
                    designDocument.getListenerManager().addDesignListener(PolishViewController.this, new DesignEventFilter ().setGlobal (true));
                invokeSwingRefresh ();
            }
        });
    }
    
    public void designChanged(DesignEvent event) {
        invokeSwingRefresh ();
    }

    private void invokeSwingRefresh () {
        SwingUtilities.invokeLater (new Runnable() {
            public void run () {
                final DesignDocument doc = designDocument;
                if (doc == null) {
                    refreshFromModel (null);
                    return;
                }
                doc.getTransactionManager().readAccess(new Runnable () {
                    public void run () {
                        refreshFromModel (doc);
                    }
                });
            }
        });
    }
    
    private void refreshFromModel (DesignDocument document) {
        if (document == null) {
            simulation.setCurrent(null);
            return;
        }

        // TODO - change your visual, toolbar views based on data in document, if document is null, then clean your views
        System.out.println(">> At " + System.currentTimeMillis() + " refreshing polish view for document " + document);

//        document.getRootComponent().readProperty(RootCD.PROP_VERSION);
//        DesignComponent displayablesCategory = MidpDocumentSupport.getCategoryComponent(document, DisplayablesCategoryCD.TYPEID);
//        int size = displayablesCategory.getComponents().size();
//        System.out.println(">>>> You have " + size + " displayable(s) in your application");
        
        DesignComponent displayablesCategory = MidpDocumentSupport.getCategoryComponent(document, DisplayablesCategoryCD.TYPEID);
        Collection<DesignComponent> displayables = displayablesCategory.getComponents();
        if (displayables.isEmpty()) {
            simulation.setCurrent(null);
            return;
        }
        DesignComponent displayable = displayables.iterator().next();

//        displayable.getComponentDescriptor().getTypeDescriptor().getSuperType();
//        displayable.getDocument().getDescriptorRegistry().getComponentDescriptor(componentType);
//        displayable.getComponentDescriptor().getPropertyDescriptors();

        if (displayable.getDocument().getDescriptorRegistry().isInHierarchy(TextBoxCD.TYPEID, displayable.getType())) {
            String title = MidpValueSupport.getHumanReadableString(displayable.readProperty(DisplayableCD.PROP_TITLE));
            String string = MidpValueSupport.getHumanReadableString(displayable.readProperty(TextBoxCD.PROP_STRING));
            PropertyValue value =  displayable.readProperty(TextBoxCD.PROP_MAX_SIZE);
            int maxsize = 100;
            if (value.getKind() == PropertyValue.Kind.VALUE) {   
                maxsize = MidpTypes.getInteger(value);
            }
            int constraint = 0;
            value =  displayable.readProperty(TextBoxCD.PROP_CONSTRAINTS);
            if (value.getKind() == PropertyValue.Kind.VALUE) {   
                constraint = MidpTypes.getInteger(value);                
            }

            TextBox textbox = new TextBox (title, string, maxsize, constraint);
            textbox._callShowNotify();
            simulation.setCurrent (textbox);
        }

//        DataObjectContext context = ProjectUtils.getDataObjectContextForDocument(displayable.getDocument());
//        Project project = ProjectUtils.getProject(context);
//        FileObject projectRoot = project.getProjectDirectory();
//        FileObject object = projectRoot.getFileObject("resources/polish.css");
//        object.getInputStream();
        
        // see http://wiki.netbeans.org/wiki/view/MobilityDesigner2MIDP
        //  http://wiki.netbeans.org/wiki/view/MobilityDesignerHome
        // for a description of all MIDP components, document structure that you can crawl
        
        // if you start IDE with: -J-Dvmd.structure.show=true parameter,
        // then you will have additional two tabs with a current document and registry structure
    }

}
