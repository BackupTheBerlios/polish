/*
 * PolishWarmUp.java
 *
 * Created on March 29, 2007, 10:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.settings;

/**
 * @author dave
 */
public class PolishWarmUp implements Runnable {
    
    public PolishWarmUp() {
    }
    
    public void run() {
        if (PolishSettings.getDefault().isDoNotAskForPolishHome())
            return;
        PolishSettingsAction.openSettings ();
    }
    
}
