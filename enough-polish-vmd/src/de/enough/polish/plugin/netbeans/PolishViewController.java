/*
 * PolishViewController.java
 *
 * Created on March 27, 2007, 11:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;

import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.modules.vmd.api.io.DesignDocumentAwareness;
import org.netbeans.modules.vmd.api.io.IOUtils;
import org.netbeans.modules.vmd.api.io.javame.DeviceListener;
import org.netbeans.modules.vmd.api.io.javame.MidpProjectPropertiesSupport;
import org.netbeans.modules.vmd.api.model.*;
import org.netbeans.modules.vmd.api.model.presenters.InfoPresenter;
import org.netbeans.modules.vmd.api.screen.editor.EditedScreenSupport;

import javax.microedition.lcdui.Displayable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 *
 * @author dave
 */
public class PolishViewController implements DesignDocumentAwareness, DesignListener, DeviceListener, EditedScreenSupport.Listener {

    public static final String POLISH_ID = "polish"; // NOI18N
    
    private DataObjectContext context;
    
    private DisplayableParserManager displayableParserManager;
    private DesignDocument designDocument;
    private DesignComponent editedScreen;

    private PolishDataEditorVisual visual;
    private JToolBar toolbar;
    private JComboBox editedScreenComboBox;
    private final ActionListener editedScreenComboBoxListener;


//    private JComponent loadingPanel;

    public PolishViewController (DataObjectContext context) {
        this.context = context;
//        loadingPanel = IOUtils.createLoadingPanel (); // you can use this JComponent when the document is null
        displayableParserManager = DisplayableParserManager.getInstance();
                
        visual = new PolishDataEditorVisual ();
        
        //visual.setLayout(new GridBagLayout ());
        //visual.add( simulationPanel, new GridBagConstraints ());

        toolbar = new JToolBar ();
        JToolBar.Separator separator = new JToolBar.Separator ();
        separator.setOrientation(JSeparator.VERTICAL);
        toolbar.add (separator);

        editedScreenComboBox = new JComboBox ();
        editedScreenComboBox.setRenderer(new EditedComboRenderer ());
        toolbar.add(editedScreenComboBox);

        editedScreenComboBoxListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final DesignComponent component = (DesignComponent) editedScreenComboBox.getSelectedItem();
                if (component != null)
                    component.getDocument().getTransactionManager().readAccess(new Runnable() {
                        public void run() {
                            EditedScreenSupport.getSupportForDocument(component.getDocument()).setEditedScreenComponentID(component.getComponentID());
                        }
                    });
            }
        };
        editedScreenComboBox.addActionListener(editedScreenComboBoxListener);

        context.addDesignDocumentAwareness (this);
    }

    public JComponent getVisualRepresentation () {
        return visual;
    }

    public JComponent getToolbarRepresentation () {
        return toolbar;
    }
    
    void open () {
//        System.out.println(">>> PolishViewController opened");
        MidpProjectPropertiesSupport.addDeviceListener (context, this);
    }
    
    void close () {
//        System.out.println(">>> PolishViewController closed");
        MidpProjectPropertiesSupport.removeDeviceChangedListener (context, this);
    }

    public void deviceChanged () {
//        Dimension deviceScreenSize = MidpProjectPropertiesSupport.getDeviceScreenSizeFromProject(context);
//        System.out.println(">> At " + System.currentTimeMillis() + " device screen size changed to " + deviceScreenSize);
        // TODO - update your screen size here
    }

    public void setDesignDocument (final DesignDocument newDesignDocument) {
        IOUtils.runInAWTNoBlocking (new Runnable () {
            public void run () {
                if (designDocument == newDesignDocument)
                    return;
                if (designDocument != null) {
                    designDocument.getListenerManager().removeDesignListener(PolishViewController.this);
                    EditedScreenSupport.getSupportForDocument(designDocument).removeListener(PolishViewController.this);
                }
                designDocument = newDesignDocument;
                if (designDocument != null) {
                    EditedScreenSupport.getSupportForDocument(designDocument).addListener(PolishViewController.this);
                    designDocument.getListenerManager().addDesignListener(PolishViewController.this, new DesignEventFilter ().setGlobal (true));
                }
                invokeSwingRefresh ();
            }
        });
    }
    
    public void editedScreenChanged(long editedScreenComponentID) {
//        System.out.println(">> At " + System.currentTimeMillis() + " edited screen was changed to " + editedScreenComponentID);
        refreshFromModel(designDocument);
    }

    public void designChanged(DesignEvent event) {
        invokeSwingRefresh ();
    }

    private void invokeSwingRefresh () {
        IOUtils.runInAWTNoBlocking (new Runnable() {
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
            editedScreen = null;
            this.editedScreenComboBox.removeAllItems();
            this.visual.setCurrent(null);
            return;
        }
        
        refreshToolBar (document);
        refreshVisual (document);
    }
    
    private void refreshToolBar (DesignDocument document) {
        editedScreen = document.getComponentByUID(EditedScreenSupport.getSupportForDocument(document).getEditedScreenComponentID());
        List<DesignComponent> allEditableScreens = EditedScreenSupport.getAllEditableScreensInDocument(document);
        editedScreenComboBox.removeActionListener(editedScreenComboBoxListener);
        editedScreenComboBox.setModel(new DefaultComboBoxModel (allEditableScreens.toArray ()));
        editedScreenComboBox.setSelectedItem(editedScreen);
        editedScreenComboBox.addActionListener(editedScreenComboBoxListener);
    }
    
    private void refreshVisual (DesignDocument document) {
        // TODO - change your visual, toolbar views based on data in document, if document is null, then clean your views
//        System.out.println(">> At " + System.currentTimeMillis() + " refreshing polish view for document " + document + " with edited screen " + editedScreen);

//        document.getRootComponent().readProperty(RootCD.PROP_VERSION);
//        DesignComponent displayablesCategory = MidpDocumentSupport.getCategoryComponent(document, DisplayablesCategoryCD.TYPEID);
//        int size = displayablesCategory.getComponents().size();
//        System.out.println(">>>> You have " + size + " displayable(s) in your application");
        
        if (editedScreen == null) {
            this.visual.setCurrent(null);
            return;
        }
        //TODO add all displayables to drop down box and use the currently selected one
        // - would be great if we could have the very same one as the ScreenDesigner is using,
        // so that we always stay in sync...
        Displayable displayable = this.displayableParserManager.parseDisplayable(editedScreen);
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

    private class EditedComboRenderer extends DefaultListCellRenderer {
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            final Image[] image = new Image[1];
            final String[] label = new String[1];
            if (value != null) {
                final DesignComponent dc = (DesignComponent) value;
                dc.getDocument().getTransactionManager().readAccess(new Runnable() {
                    public void run() {
                        InfoPresenter presenter = dc.getPresenter(InfoPresenter.class);
                        label[0] = presenter.getDisplayName(InfoPresenter.NameType.PRIMARY);
                        image[0] = presenter.getIcon(InfoPresenter.IconType.COLOR_16x16);
                    }
                });
            }
            super.getListCellRendererComponent(list, label[0], index, isSelected, cellHasFocus);
            if (image[0] != null)
                setIcon(new ImageIcon(image[0]));
            return this;
        }
    }
    
}
