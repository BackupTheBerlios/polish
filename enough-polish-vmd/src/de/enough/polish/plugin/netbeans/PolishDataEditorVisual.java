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
import de.enough.polish.plugin.netbeans.components.PolishPropertiesProcessor;
import de.enough.polish.preprocess.css.CssAttributesManager;
import de.enough.polish.resources.ColorProvider;
import de.enough.polish.resources.ResourcesProvider;
import de.enough.polish.resources.StyleProvider;
import de.enough.polish.resources.swing.ColorSelectionListener;
import de.enough.polish.resources.swing.ResourcesTree;
import de.enough.polish.resources.swing.SpecifyOrSelectEntryDialog;
import de.enough.polish.resources.swing.StyleSelectionListener;
import de.enough.polish.runtime.SelectionListener;
import de.enough.polish.styleeditor.*;
import de.enough.polish.styleeditor.swing.*;
import de.enough.polish.runtime.Simulation;
import de.enough.polish.runtime.SimulationDevice;
import de.enough.polish.runtime.SimulationPanel;
import de.enough.polish.runtime.overlays.HoverOverlay;
import de.enough.polish.runtime.overlays.SelectionOverlay;
import de.enough.polish.styleeditor.swing.components.ColorChooserComponent;
import de.enough.polish.styleeditor.swing.components.ColorChooserListener;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.Border;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.StyleSheet;
import de.enough.polish.ui.UiAccess;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import javax.microedition.lcdui.Displayable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.modules.vmd.api.io.javame.MidpProjectPropertiesSupport;
import org.netbeans.modules.vmd.api.io.ProjectUtils;
import org.netbeans.modules.vmd.api.model.DesignComponent;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.openide.util.Exceptions;

/**
 * <p>Visalizes the currently edited displayable, accepts droppping.</p>
 *
 * @author robertvirkus
 */
public class PolishDataEditorVisual extends JPanel 
        implements StyleEditorListener, SelectionListener, StyleSelectionListener, ColorSelectionListener, ColorChooserListener
{

    private Simulation simulation;
    private SimulationPanel simulationPanel;
    //private ResourcesPanel editor;
    private StyleEditor styleEditor;
    private ResourcesProvider resourceProvider;
    private ResourcesTree resourcesTree;

    public PolishDataEditorVisual(DataObjectContext context, Environment environment, ResourcesProvider resourcesProvider, CssAttributesManager attributesManager) {
        super( new BorderLayout() );
        SimulationDevice device = new SimulationDevice ();
        simulation = new Simulation (device);
        simulation.addOverlay( new SelectionOverlay() );
        simulation.addOverlay( new HoverOverlay() );
        simulation.addSelectionListener( this );
        simulation.setSelectionMode( Simulation.SELECTION_MODE_PASSIVE );
        simulationPanel = new SimulationPanel( simulation );
        //editor = new ResourcesPanel( simulationPanel );
        
        
        this.resourceProvider = resourcesProvider;
        SwingStyleEditor styleEditorVisual = new SwingStyleEditor();
	this.styleEditor = new StyleEditor( this.resourceProvider, attributesManager, environment, styleEditorVisual );
        
	this.styleEditor.addDefaultPartEditors();
	this.styleEditor.addStyleListener( this );

        ResourcesTree tree = ResourcesTree.getInstance( this.resourceProvider );
        tree.addStyleSelectionListener(this);
        tree.addColorSelectionListener( this );
        this.resourcesTree = tree;

        JSplitPane horizontalSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, simulationPanel, new JScrollPane( tree) );
        JSplitPane splitPane =  new JSplitPane( JSplitPane.VERTICAL_SPLIT, horizontalSplitPane, styleEditorVisual );
        //splitPane.setResizeWeight( 0.5 );
        add( splitPane, BorderLayout.CENTER );
        
        // add drag'n'drop support:
        new DropTarget( simulation, new SimulationDropListener( simulation ) );
    }
    
   
    
    public void setCurrent( Displayable displayable ) {
        if (displayable == null) {
            return;
        }
        this.simulation.setCurrent(displayable);
        if (!(displayable instanceof Screen)) {
            return;
        }
        Screen screen = (Screen) displayable;
        Style style = UiAccess.getStyle( screen );
        EditStyle editStyle = getEditStyle( new ItemOrScreen( screen ), style );
        if (editStyle != null) {
            System.out.println("setCurrent: editStyle.getScreen()=" + editStyle.getItemOrScreen().getItemOrScreen()  );
            this.styleEditor.setStyle(editStyle);
        }
    }
    
    private String getDefaultStyleName( Class itemOrScreenClass ) {
        String defaultName = itemOrScreenClass.getName();
        if (defaultName.lastIndexOf('.' ) != -1) {
            defaultName = defaultName.substring( defaultName.lastIndexOf('.' ) + 1 );
        }
        return "my" + defaultName;
    }
    
    private EditStyle getEditStyle( ItemOrScreen itemOrScreen, Style style ) {       
        EditStyle editStyle = getEditStyleImpl(itemOrScreen, style);
        if (editStyle != null) {
            itemOrScreen.setStyle( editStyle.getStyle() );
            itemOrScreen.requestInit();
        }
        return editStyle;
    }
    private EditStyle getEditStyleImpl( ItemOrScreen itemOrScreen, Style style ) {       
        EditStyle editStyle = null;
        if (style != null && style.name != null) {
            editStyle = (EditStyle) this.resourceProvider.getStyle(style.name);
            if (editStyle == null) {
                editStyle = new EditStyle( style.name, style,  itemOrScreen );
                this.resourceProvider.addStyle(style.name, editStyle);
                this.resourcesTree.addStyle(editStyle);
            } else {
                editStyle.setItemOrScreen(itemOrScreen);
            }
        }         
        if (editStyle == null) {
            //StyleProvider[] knownStyles = this.resourceProvider.getStyles();
            //String sty
            Object itemOrScreenObj = itemOrScreen.getItemOrScreen();
            String defaultName = getDefaultStyleName( itemOrScreenObj.getClass() );
            
            String chosenName = SpecifyOrSelectEntryDialog.showDialog(
                    null,
                    "Choose Style Name",
                    itemOrScreenObj instanceof Item ? 
                        "This item has no attached style, please enter the desired name: "
                        :
                        "This screen has no attached style, please enter the desired name: ",
                    this.resourceProvider.getStyles(),
                    defaultName);
            if (chosenName == null) {
                return null;
            }
            // check if the user selected an existing style:
            editStyle = (EditStyle) this.resourceProvider.getStyle(chosenName);
            if (editStyle != null) {
                editStyle.setItemOrScreen(itemOrScreen);
            } else {
                if (style == null || style == StyleSheet.defaultStyle) {
                    style = new Style();
                }
                style.name = chosenName;
                DesignComponent dc;
                if (itemOrScreenObj instanceof Item) {
                    Item item = (Item) itemOrScreenObj;
                    item.setStyle( style );
                    //UiAccess.setStyle( (Item)itemOrScreenObj, style );
                    dc =  (DesignComponent)item.getAttribute("DesignComponent" );
                    //dc = (DesignComponent) UiAccess.getAttribute( (Item)itemOrScreenObj, "DesignComponent" );
                } else {
                    UiAccess.setStyle( (Screen)itemOrScreenObj, style );
                    dc = (DesignComponent) ((Screen)itemOrScreenObj).getScreenData();
                }
                editStyle = new EditStyle( style.name, style,  itemOrScreen );
                if (dc != null) {
                    final DesignComponent designComponent = dc;
                    final String styleName = chosenName;
                    designComponent.getDocument().getTransactionManager().writeAccess( new Runnable() {
                        public void run() {
                            designComponent.writeProperty(PolishPropertiesProcessor.PROP_STYLE, MidpTypes.createStringValue( styleName ) );
                        }
                    });
                }

                this.resourceProvider.addStyle(style.name, editStyle);
                this.resourcesTree.addStyle(editStyle);
            }
        }
        return editStyle;
    }
    
     
    public void deviceChanged( DataObjectContext context ) {
        String deviceName = MidpProjectPropertiesSupport.getActiveConfiguration(ProjectUtils.getProject (context));
        System.out.println("deviceChanged: new device=" + deviceName );
        if (deviceName == null) {
            System.err.println("WARNING: no device found!!!");
            return;
        }
        if ( deviceName.indexOf('/') == -1) {
        	int underlinePos = deviceName.indexOf('_');
        	if (underlinePos != -1) {
        		deviceName = deviceName.substring(0, underlinePos) + "/" + deviceName.substring( underlinePos + 1);	
        	} else {
           		deviceName = "Generic/" + deviceName;
        	}
        }
        //TODO alternatively load device with DeviceDatabase.loadDevice( polishHome, deviceName );
        System.out.println(">> At " + System.currentTimeMillis() + " device changed to " + deviceName );
        SimulationDevice device = new SimulationDevice( deviceName );
        Dimension deviceScreenSize = MidpProjectPropertiesSupport.getDeviceScreenSizeFromProject(context);
        if (deviceScreenSize != null) {
            device.setCapability( SimulationDevice.FULL_CANVAS_WIDTH, Integer.toString(deviceScreenSize.width) );
            device.setCapability( SimulationDevice.FULL_CANVAS_HEIGHT, Integer.toString(deviceScreenSize.height) );
        }
        device.setCapability( "polish.Identifier", deviceName );
        this.simulation.setDevice( device );
        this.simulationPanel.setSimulation( this.simulation );
    }
    
    /* (non-Javadoc)
     * @see de.enough.polish.styleeditor.StyleListener#notifyStyleUpdated(de.enough.polish.styleeditor.EditStyle)
    */
    public void notifyStyleUpdated(EditStyle style) {
        if (style.getItemOrScreen() == null) {
            System.out.println("No item or screen found!");
            return;
        }
        style.getItemOrScreen().setStyle(style.getStyle());
        style.getItemOrScreen().requestInit();
        this.simulation.getCurrentDisplayable()._requestRepaint();
        System.out.println( style.toSourceCode() );
        try {
            this.resourceProvider.saveResources();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
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
        //System.out.println("start1: itemOrScreen=" + itemOrScreen );
        EditStyle editStyle = getEditStyle(itemOrScreen, style );
        //System.out.println("start2: editStyle.getItemOrScreen()=" + (editStyle.getItemOrScreen()) );
        if (editStyle != null) {
            this.styleEditor.setStyle(editStyle);
            this.resourcesTree.selectStyle(editStyle);    
        }
    }

    public void clearSelection() {
        // ignore
    }
    
    	/* (non-Javadoc)
	 * @see de.enough.polish.resources.swing.StyleSelectionListener#notifyStyleSelected(de.enough.polish.resources.StyleProvider)
	 */
	public void notifyStyleSelected(StyleProvider styleProvider) {
		System.out.println("notify from tree: style selected: " + styleProvider.getName() );
		if (styleProvider instanceof EditStyle) {
			this.styleEditor.setStyle( (EditStyle)styleProvider );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.resources.swing.ColorSelectionListener#notifyColorSelected(de.enough.polish.resources.ColorProvider)
	 */
	public void notifyColorSelected(ColorProvider color) {
		System.out.println("notify from tree: color selected: " + color.getName() );
		ColorProvider colorProvider = ColorChooserComponent.showDialog(color.getName(), color, null, this, true );
		if (colorProvider != null) {
			color.setColor( color.getColor() );
//			this.colorButton.setBackground( new java.awt.Color( colorProvider.getColor().getColor() ) );
//			editor.setColor( colorProvider );
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.styleeditor.swing.components.ColorChooserListener#notifyColorUpdated(de.enough.polish.resources.ColorProvider)
	 */
	public void notifyColorUpdated(ColorProvider colorProvider) {
		// TODO robertvirkus implement notifyColorUpdated
		System.out.println("named color has changed: " + colorProvider.getName() + "=" + Integer.toHexString( colorProvider.getColor().getColor() ));
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


