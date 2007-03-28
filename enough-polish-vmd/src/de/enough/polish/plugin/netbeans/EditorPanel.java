/*
 * EditorPanel.java
 * 
 * Created on Mar 28, 2007, 2:14:40 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;

import de.enough.polish.runtime.SimulationPanel;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.ui.backgrounds.SimpleBackground;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p></p>
 *
 * @author robertvirkus
 */
public class EditorPanel extends JPanel implements ChangeListener{

    private SimulationPanel simulationPanel;
    private JColorChooser colorChooser;
    
    public EditorPanel( SimulationPanel simulationPanel ) {
        this.simulationPanel = simulationPanel;
        this.colorChooser = new JColorChooser( Color.WHITE );
        colorChooser.getSelectionModel().addChangeListener( this );
        add( colorChooser );
    }

    public void stateChanged(ChangeEvent event) {
            int color = this.colorChooser.getColor().getRGB();
            Screen screen = (Screen) this.simulationPanel.getSimulation().getCurrentDisplayable();
            SimpleBackground background = new SimpleBackground( color );
            UiAccess.setBackground(screen, background);
    }

}
