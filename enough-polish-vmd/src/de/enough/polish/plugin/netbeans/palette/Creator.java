/*
 * Creator.java
 *
 * Created on March 28, 2007, 4:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.palette;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Border;

/**
 *
 * @author dave
 */
public interface Creator {
    
    Background createBackground ();
    
    Border createBorder ();
    
}
