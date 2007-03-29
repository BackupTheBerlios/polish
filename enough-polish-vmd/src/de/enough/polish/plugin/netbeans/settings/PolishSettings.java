/*
 * PolishSettings.java
 *
 * Created on March 29, 2007, 10:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.enough.polish.plugin.netbeans.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;

/**
 * @author dave
 */
public class PolishSettings {
    
    private static PolishSettings instance;
    
    private String polishHome = null;
    private boolean doNotAskForPolishHome = false;
    
    private PolishSettings() {
        load ();
    }
    
    public static synchronized PolishSettings getDefault() {
        if (instance == null)
            instance = new PolishSettings ();
        return instance;
    }
    
    public String getPolishHome () {
        return polishHome;
    }
    
    public boolean isDoNotAskForPolishHome () {
        return doNotAskForPolishHome;
    }
    
    public void setProperties (String polishHome, boolean doNotAskForPolishHome) {
        this.polishHome = polishHome;
        this.doNotAskForPolishHome = doNotAskForPolishHome;
        save ();
    }
    
    private void load() {
        final FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        try {
            fs.runAtomicAction(new FileSystem.AtomicAction() {
                public void run() throws IOException {
                    FileObject file = fs.findResource("j2mepolish/settings.properties");
                    if (file != null) {
                        Properties props = new Properties ();
                        InputStream is = file.getInputStream();
                        try {
                            props.load(is);
                        } finally {
                            try {
                                is.close ();
                            } catch (IOException e) {
                            }
                        }
                        polishHome = props.getProperty("polish.home");
                        doNotAskForPolishHome = Boolean.parseBoolean(props.getProperty("do.not.ask.for.polish.home"));
                    }
                }
            });
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }
    
    private void save () {
        final FileSystem fs = Repository.getDefault().getDefaultFileSystem();
        try {
            fs.runAtomicAction(new FileSystem.AtomicAction() {
                public void run() throws IOException {
                    FileObject file = fs.findResource("j2mepolish/settings.properties");
                    if (file != null) {
                        Properties props = new Properties ();
                        if (polishHome != null)
                            props.put("polish.home", polishHome);
                        if (doNotAskForPolishHome)
                            props.put("do.not.ask.for.polish.home", doNotAskForPolishHome);
                        OutputStream os = file.getOutputStream();
                        try {
                            props.store(os, "Settings for J2ME Polish NetBeans Plugin");
                        } finally {
                            try {
                                os.close ();
                            } catch (IOException e) {
                            }
                        }
                    }
                }
            });
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }
    }
    
}
