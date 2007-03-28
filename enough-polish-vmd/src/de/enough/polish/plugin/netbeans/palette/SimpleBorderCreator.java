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
import de.enough.polish.ui.Border;
import de.enough.polish.ui.borders.SimpleBorder;

/**
 * @author dave
 */
public class SimpleBorderCreator implements Creator {
    
    public SimpleBorderCreator() {
    }
    
    public Background createBackground() {
        return null;
    }

    public Border createBorder() {
        return new SimpleBorder (); // TODO - ask user
    }

}
