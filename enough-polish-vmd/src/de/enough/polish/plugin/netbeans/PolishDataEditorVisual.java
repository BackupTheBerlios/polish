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
import java.awt.BorderLayout;
import javax.microedition.lcdui.Displayable;
import javax.swing.JPanel;
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
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, simulationPanel, editor );
        add( splitPane, BorderLayout.CENTER );
    }
    
    public void setCurrent( Displayable displayable ) {
        this.simulation.setCurrent(displayable);
    }

}
