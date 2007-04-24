/*
 * EditorPanel.java
 * 
 * Created on Mar 28, 2007, 2:14:40 AM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans;

import de.enough.polish.runtime.SelectionListener;
import de.enough.polish.runtime.SimulationPanel;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.UiAccess;
import de.enough.polish.ui.backgrounds.SimpleBackground;
import java.awt.Color;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>Contains a list of project specific resources like color definitions, backgrounds, borders, fonts, images and styles.</p>
 *
 * @author robertvirkus
 */
public class ResourcesPanel extends JPanel implements ChangeListener, SelectionListener {

    private SimulationPanel simulationPanel;
    private JColorChooser colorChooser;
    
    public ResourcesPanel( SimulationPanel simulationPanel ) {
        this.simulationPanel = simulationPanel;
        this.simulationPanel.getSimulation().addSelectionListener( this );
        this.colorChooser = new JColorChooser( Color.WHITE );
        colorChooser.getSelectionModel().addChangeListener( this );
        //add( new JScrollPane( colorChooser ) );
        add( new JLabel("Resources List (tbd)"));
    }

    public void stateChanged(ChangeEvent event) {
            int color = this.colorChooser.getColor().getRGB();            
            SimpleBackground background = new SimpleBackground( color );
            if (this.currentItem != null) {
                Style style = this.currentItem.getStyle();
                if (style == null) {
                    style = new Style();
                }
                style.background = background;
                UiAccess.setStyle(this.currentItem, style);
            } else {
                Screen screen = (Screen) this.simulationPanel.getSimulation().getCurrentDisplayable();
                Style style = screen.getStyle();
                if (style == null) {
                    style = new Style();
                }
                style.background = background;
                UiAccess.setStyle(screen, style);                
            }
    }

    public void notifyItemSelected(Item item, Style arg1, int arg2, int arg3) {
        this.currentItem = item;
        
    }

    public void notifyScreenSelected(Screen arg0, Style arg1, int arg2, int arg3) {
        this.currentItem = null;
    }

    public void clearSelection() {
        // ignore
    }
    private Item currentItem;

}
