/*
 * SimpleBackgroundCreator.java
 *
 * Created on March 28, 2007, 4:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.palette;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Color;
import de.enough.polish.ui.backgrounds.SimpleBackground;
import javax.swing.JColorChooser;

/**
 * @author dave
 */
public class SimpleBackgroundCreator implements Creator {
    
    public SimpleBackgroundCreator() {
    }
    
    public Background createBackground() {
        java.awt.Color color = JColorChooser.showDialog(null, "Choose Color for background", java.awt.Color.WHITE);
        return color != null ? new SimpleBackground (new Color (color.getRGB())) : null;
    }

}
