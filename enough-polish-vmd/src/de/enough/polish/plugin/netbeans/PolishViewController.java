/*
 * PolishViewController.java
 *
 * Created on March 27, 2007, 11:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;

import java.awt.Dimension;
import java.util.Collection;
import javax.microedition.lcdui.Displayable;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.netbeans.api.project.Project;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.modules.vmd.api.io.DesignDocumentAwareness;
import org.netbeans.modules.vmd.api.io.IOUtils;
import org.netbeans.modules.vmd.api.io.ProjectUtils;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.api.model.DesignDocument;
import org.netbeans.modules.vmd.api.model.DesignEvent;
import org.netbeans.modules.vmd.api.model.DesignEventFilter;
import org.netbeans.modules.vmd.api.model.DesignListener;
import org.netbeans.modules.vmd.midp.components.MidpDocumentSupport;
import org.netbeans.modules.vmd.midp.components.categories.DisplayablesCategoryCD;
import org.netbeans.modules.vmd.midp.project.MidpProjectPropertiesSupport;
import org.netbeans.spi.project.support.ant.AntProjectEvent;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.AntProjectListener;

/**
 *
 * @author dave
 */
public class PolishViewController implements DesignDocumentAwareness, DesignListener, AntProjectListener {

    public static final String POLISH_ID = "polish"; // NOI18N
    
    private DataObjectContext context;
    
    private DisplayableParserManager displayableParserManager;
    private DesignDocument designDocument;
   

    private PolishDataEditorVisual visual;
    private JComponent toolbar;

//    private JComponent loadingPanel;

    public PolishViewController (DataObjectContext context) {
        this.context = context;
//        loadingPanel = IOUtils.createLoadingPanel (); // you can use this JComponent when the document is null
        displayableParserManager = DisplayableParserManager.getInstance();
                
        visual = new PolishDataEditorVisual ();
        
        //visual.setLayout(new GridBagLayout ());
        //visual.add( simulationPanel, new GridBagConstraints ());

        toolbar = new JLabel ("This is a polish toolbar"); // TODO

        context.addDesignDocumentAwareness (this);
    }

    public JComponent getVisualRepresentation () {
        return visual;
    }

    public JComponent getToolbarRepresentation () {
        return toolbar;
    }
    
    void open () {
        System.out.println(">>> PolishViewController opened");
        Project project = ProjectUtils.getProject (context);
        AntProjectHelper helper = project.getLookup ().lookup (AntProjectHelper.class);
        helper.addAntProjectListener(this);
        propertiesChanged(null);
    }
    
    void close () {
        System.out.println(">>> PolishViewController closed");
        Project project = ProjectUtils.getProject (context);
        AntProjectHelper helper = project.getLookup ().lookup (AntProjectHelper.class);
        helper.removeAntProjectListener(this);
    }

    public void configurationXmlChanged(AntProjectEvent ev) {
        propertiesChanged(ev); // TODO
    }

    public void propertiesChanged(AntProjectEvent ev) {
        Dimension deviceScreenSize = MidpProjectPropertiesSupport.getDeviceScreenSizeFromProject(context);
        System.out.println(">> At " + System.currentTimeMillis() + " device screen size changed to " + deviceScreenSize);
        // TODO - update your screen size here
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
            this.visual.setCurrent(null);
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
            this.visual.setCurrent(null);
            return;
        }
        //TODO add all displayables to drop down box and use the currently selected one
        // - would be great if we could have the very same one as the ScreenDesigner is using,
        // so that we always stay in sync...
        DesignComponent designComponent = displayables.iterator().next();
        Displayable displayable = this.displayableParserManager.parseDisplayable(designComponent);
        if (displayable != null) {
            displayable._callShowNotify();
            this.visual.setCurrent(displayable);
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
