/*
 * PolishDataEditorVisual.java
 * 
 * Created on Mar 28, 2007, 2:05:49 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;
import de.enough.polish.Environment;
import de.enough.polish.plugin.netbeans.settings.PolishSettings;
import de.enough.polish.preprocess.css.CssAttributesManager;
import de.enough.polish.runtime.SelectionListener;
import de.enough.polish.styleeditor.*;
import de.enough.polish.styleeditor.swing.*;
import de.enough.polish.runtime.Simulation;
import de.enough.polish.runtime.SimulationDevice;
import de.enough.polish.runtime.SimulationPanel;
import de.enough.polish.runtime.overlays.HoverOverlay;
import de.enough.polish.runtime.overlays.SelectionOverlay;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.Border;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.util.ResourceUtil;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import javax.microedition.lcdui.Displayable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.modules.vmd.api.io.javame.MidpProjectPropertiesSupport;
import org.netbeans.modules.vmd.api.io.ProjectUtils;

/**
 * <p>Visalizes the currently edited displayable, accepts droppping.</p>
 *
 * @author robertvirkus
 */
public class PolishDataEditorVisual extends JPanel 
        implements StyleEditorListener, SelectionListener
{

    private Simulation simulation;
    private SimulationPanel simulationPanel;
    private ResourcesPanel editor;
    private StyleEditor styleEditor;

    public PolishDataEditorVisual() {
        super( new BorderLayout() );
        SimulationDevice device = new SimulationDevice ();
        simulation = new Simulation (device);
        simulation.addOverlay( new SelectionOverlay() );
        simulation.addOverlay( new HoverOverlay() );
        simulation.addSelectionListener( this );
        simulationPanel = new SimulationPanel( simulation );
        editor = new ResourcesPanel( simulationPanel );
        JSplitPane horizontalSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, simulationPanel, new JScrollPane( editor) );
        
       SwingStyleEditor styleEditorVisual = new SwingStyleEditor();
        ResourceUtil resourceUtil = new ResourceUtil( getClass().getClassLoader() );
        File polishHome = new File(PolishSettings.getDefault ().getPolishHome ());
        Environment environment = new Environment( polishHome );
        CssAttributesManager manager = CssAttributesManager.getInstance( polishHome, resourceUtil );
	this.styleEditor = new StyleEditor( manager, environment, styleEditorVisual );
	this.styleEditor.addDefaultPartEditors();
	this.styleEditor.addStyleListener( this );

        JSplitPane splitPane =  new JSplitPane( JSplitPane.VERTICAL_SPLIT, horizontalSplitPane, styleEditorVisual );
        //splitPane.setResizeWeight( 0.5 );
        add( splitPane, BorderLayout.CENTER );
        
        // add drag'n'drop support:
        new DropTarget( simulation, new SimulationDropListener( simulation ) );
    }
    
    public void setCurrent( Displayable displayable ) {
        this.simulation.setCurrent(displayable);
    }
    
     
    public void deviceChanged( DataObjectContext context ) {
        String deviceName = MidpProjectPropertiesSupport.getActiveConfiguration(ProjectUtils.getProject (context));
        if ( deviceName.indexOf('/') == -1) {
            deviceName = "Generic/" + deviceName;
        }
        
        System.out.println(">> At " + System.currentTimeMillis() + " device changed to " + deviceName );
        SimulationDevice device = new SimulationDevice( deviceName );
        Dimension deviceScreenSize = MidpProjectPropertiesSupport.getDeviceScreenSizeFromProject(context);
        if (deviceScreenSize != null) {
            device.setCapability( SimulationDevice.FULL_CANVAS_WIDTH, Integer.toString(deviceScreenSize.width) );
            device.setCapability( SimulationDevice.FULL_CANVAS_HEIGHT, Integer.toString(deviceScreenSize.height) );
        }
        this.simulation.setDevice( device );
        this.simulationPanel.setSimulation( this.simulation );
    }
    
    /* (non-Javadoc)
     * @see de.enough.polish.styleeditor.StyleListener#notifyStyleUpdated(de.enough.polish.styleeditor.EditStyle)
    */
    public void notifyStyleUpdated(EditStyle style) {
        //System.out.println("Test.notifyStyleUpdated(): layout=" + Integer.toHexString( style.getStyle().layout ));
        style.getItemOrScreen().setStyle( style.getStyle() );
        style.getItemOrScreen().requestInit();
        this.simulation.getCurrentDisplayable()._requestRepaint();
    }

   /* (non-Javadoc)
     * @see de.enough.polish.runtime.SelectionListener#notifyItemSelected(de.enough.polish.ui.Item, de.enough.polish.ui.Style, int, int)
     */
    public void notifyItemSelected(Item item, Style style, int x, int y) {
            ItemOrScreen itemOrScreen = new ItemOrScreen( item );
            editStyle( itemOrScreen, style );
    }

    /* (non-Javadoc)
     * @see de.enough.polish.runtime.SelectionListener#notifyScreenSelected(de.enough.polish.ui.Screen, de.enough.polish.ui.Style, int, int)
     */
    public void notifyScreenSelected(Screen screen, Style style, int x, int y) {
            ItemOrScreen itemOrScreen = new ItemOrScreen( screen );
            editStyle( itemOrScreen, style );
    }

    /**
     * @param itemOrScreen
     * @param style
     */
    private void editStyle(ItemOrScreen itemOrScreen, Style style) {
            if (style == null || style == StyleSheet.defaultStyle) {
                style = new Style();
            }
            EditStyle editStyle = new EditStyle( style, itemOrScreen );
            this.styleEditor.setStyle(editStyle);
    }

    public void clearSelection() {
        // ignore
    }

}

class SimulationDropListener extends DropTargetAdapter {
    
    private Simulation simulation;
    
    public SimulationDropListener( Simulation simulation ) {
        this.simulation = simulation;
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
     */
    public void drop(DropTargetDropEvent event) {
        if (!(this.simulation.getCurrentDisplayable() instanceof Screen)) {
            event.rejectDrop();                
        }
        Screen screen = (Screen) this.simulation.getCurrentDisplayable();
        Point location = event.getLocation();
        System.out.println("drag'n'drop at " + location.x + ", " + location.y);
        Item item = screen.getItemAt( location.x, location.y );
        Style style = item != null ? item.getStyle() : screen.getScreenStyle();
        final Transferable transferable = event.getTransferable(); 
        if (transferable.isDataFlavorSupported(Simulation.BACKGROUND_DATA_FLAVOR)) {
            //System.out.println("data flavor supported...");

            try {
                final Background background = (Background)transferable.getTransferData( Simulation.BACKGROUND_DATA_FLAVOR );
                System.out.println("got background: " + background);
                if (background != null) {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    if (item != null) {
                        UiAccess.setBackground(item, background);
                    } else {
                        UiAccess.setBackground( screen, background);
                    }
                    // Signal transfer success.
                    event.dropComplete(true);
                    return;
                }
            } catch (Exception e) {
                    // Signal transfer failure.
                    event.dropComplete(false); 
            }
        } 
        if (transferable.isDataFlavorSupported(Simulation.BORDER_DATA_FLAVOR)) {
           // System.out.println("data flavor supported...");

            try {
                final Border border = (Border)transferable.getTransferData( Simulation.BORDER_DATA_FLAVOR );
                System.out.println("got border: " + border);
                if (border != null) {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    
                    Style existing = style;
                    if (existing == null) {
                        existing = StyleSheet.defaultStyle;
                    }
                    Style style2 = new Style(
                            0,0,0,0, // margin
                            1,1,1,1,1,1, // padding
                            existing.layout,
                            existing.fontColor,
                            existing.font,
                            existing.background,
                            border,
                            null, null
                            );
                    if (item != null) {
                        UiAccess.setStyle(item, style2);
                    } else {
                        UiAccess.setStyle( screen, style2);
                    }
                    //UiAccess.setStyle( screen, style2);
                    screen.repaint();
                    // Signal transfer success.
                    event.dropComplete(true);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                    // Signal transfer failure.
                    event.dropComplete(false); 
            }
        }
        System.out.println("data flavor not supported...");
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        for (int i=0; i < flavors.length; i++ ) {
            System.out.println( flavors[i] );
        }
        event.rejectDrop();
 
    }
        
		
}


