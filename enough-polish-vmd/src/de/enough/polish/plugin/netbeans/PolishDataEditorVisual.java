/*
 * PolishDataEditorVisual.java
 * 
 * Created on Mar 28, 2007, 2:05:49 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;

import de.enough.polish.runtime.Simulation;
import de.enough.polish.runtime.SimulationDevice;
import de.enough.polish.runtime.SimulationPanel;
import de.enough.polish.ui.Background;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;
import java.awt.BorderLayout;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import javax.microedition.lcdui.Displayable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/**
 * <p></p>
 *
 * @author robertvirkus
 */
public class PolishDataEditorVisual extends JPanel {

    private Simulation simulation;
    private SimulationPanel simulationPanel;
    private EditorPanel editor;

    public PolishDataEditorVisual() {
        super( new BorderLayout() );
        SimulationDevice device = new SimulationDevice ();
        simulation = new Simulation (device);
        simulationPanel = new SimulationPanel( simulation );
        editor = new EditorPanel( simulationPanel );
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, simulationPanel, new JScrollPane( editor) );
        //splitPane.setResizeWeight( 0.5 );
        add( splitPane, BorderLayout.CENTER );
        
        // add drag'n'drop support:
        new DropTarget( simulation, new SimulationDropListener( simulation ) );
    }
    
    public void setCurrent( Displayable displayable ) {
        this.simulation.setCurrent(displayable);
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
                final Transferable transferable = event.getTransferable(); 
                if (transferable.isDataFlavorSupported(Simulation.BACKGROUND_DATA_FLAVOR)) {
                        event.acceptDrop(DnDConstants.ACTION_COPY);
                        try {
                                final Background background = (Background)transferable.getTransferData( Simulation.BACKGROUND_DATA_FLAVOR );
                                UiAccess.setBackground( (Screen)simulation.getCurrentDisplayable(), background);
                                // Signal transfer success.
                                event.dropComplete(true);
                        } catch (Exception e) {
                                // Signal transfer failure.
                                event.dropComplete(false); 
                        }
                } else {
                        event.rejectDrop();
                } 
        }
		
	}
