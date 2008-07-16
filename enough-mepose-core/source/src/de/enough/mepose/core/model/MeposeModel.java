/*
 * Created on May 31, 2005 at 1:39:13 PM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.mepose.core.model;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.eclipse.jdt.core.IClasspathEntry;

import de.enough.utils.AntBox;
import de.enough.utils.PropertyModel;
import de.enough.utils.Status;

/**
 * This class encapsulates the concepts of the polish build.xml in an abstract manner.
 * It consists of:
 * <li>Paths (WTKs,polish home,...)
 * <li>Infos (description, licence)
 * <li>Supported Config (platforms,configurations,target devices, general classpath)
 * <li>Current Config (device to run, specific classpath, jar/jad pairs)
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        May 31, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class MeposeModel extends PropertyModel{
    
    public static final String DEFAULT_DEVICE_NAME = "Generic/Midp2Cldc11";

    public static final Status STATUS_BUILDXML_MISSING = new Status(Status.TYPE_ERROR,"build.xml file does not exist.",null);

//    public static final String ERROR_NOBUILDXML_FILE = "No build.xml file specified.";
//    public static final String ERROR_NO_DEVICE = "No device specified.";
//    public static final String ERROR_PARSE_ERROR = "Errors while parsing build.xml";
//    public static final String ERROR_INVALID_WORKING_DIR = "Working directory is not valid";
    
    // ###################################################################
    // IDs.
    
    // Ant.
    public static final String ID_ANT_TASK_POLISH = "id.ant.task.polish";
    public static final String ID_ANT_ENVIRONMENT = "id.ant.environment";
    
    // Paths.
    public static final String ID_PATH_WORKINGDIRECTORY_BUILD = "id.build.wd";
    public static final String ID_PATH_BUILDXML_FILE = "id.buildxml";
    public static final String ID_PATH_PROJECT_FILE = "id.path.projecthome";
    public static final String ID_PATH_POLISH_FILE = "id.path.polishhome";
    public static final String ID_PATH_WTK_FILE = "id.path.wtkhome";
    public static final String ID_PATH_NOKIA_FILE = "id.path.nokiahome";
    public static final String ID_PATH_SONY_FILE = "id.path.sonyhome";
    public static final String ID_PATH_MOTOROLA_FILE = "id.path.motorolahome";
    public static final String ID_PATH_SIEMENS_FILE = "id.path.siemenshome";
    
    // Supported Config.
    public static final String ID_SUPPORTED_CONFIGURATIONS = "id.supported.configurations";
    public static final String ID_SUPPORTED_PLATFORMS = "id.supported.platforms";
    public static final String ID_SUPPORTED_DEVICES = "id.supported.devices";

    // Current Config.
    public static final String ID_CURRENT_DEVICE = "id.current.device";
    public static final String ID_CURRENT_JADJARPAIRS = "id.current.jadjarpairs";
    
    // Info.
    public static final String ID_INFO_DESCRIPTION = "id.info.description";

    private static final String ID_SUPPORTED_CLASSPATH = "id.supported.classpath";

    private static final Object ID_CURRENT_TARGET_ONLYBUILD = "id.current.target.onlybuild";

    private static final String ID_PATH_MPP_FILE = "id.path.mpphome";

    
    
    // ###################################################################
    // Fields.
    
    // Ant.
    private AntBox antBox;
    private File buildxml = new File("");

    // Path.
    private File polishHome = null;
    private File wtkHome = new File("");
    private File nokiaHome = new File("");
    private File sonyHome = new File("");
    private File motorolaHome = new File("");
    private File siemensHome = new File("");
    private File projectHome = new File("");
    private File mppHome = new File("");

    private IClasspathEntry[] classpathEntries;

    private File jadFile;
    
    // Info.
    private String projectDescription = "";

    // Internal.
    private List propertyChangeListeners;
    private String buildTargetName = "j2mepolish";
    private List buildListener;

    private Map variables;
    
    
    public MeposeModel() {
        reset();
    }
    
    
    // ###################################################################
    // Methods.
    
    //TODO: Check if all fields are initialized.
    //TODO: If fields are already set dispose them properly before resetting them.
    public void reset() {
        this.antBox = new AntBox();

        this.buildxml = new File("");
        this.projectHome = new File("");
        this.propertyChangeListeners = new LinkedList();
        
        this.jadFile = new File("");
        
        this.classpathEntries = null;
        this.buildListener = new LinkedList();
        
        this.variables = new HashMap();
        
    }

    
    // ###################################################################
    // Ant.
    
    /**
     * TODO: Change the BuildException to a model specific exception.
     * When called initializes the antBox with the given buildxml, creates a antproject, sets
     * 'environment' and gathers the configured devices.
     * @param buildxml 
     * @throws BuildException if parsing of the build.xml file was not possible or no 'PolishTask' was found.
     */
    public void setBuildxml(File buildxml) throws BuildException{
        if(buildxml == null){
            throw new IllegalArgumentException("ERROR:MeposeProject.setBuildxml(...):Parameter 'buildxml' is null.");
        }
        this.buildxml = buildxml;
    }

  
    public String getSourcePath(String deviceIdentifier) {
        throw new UnsupportedOperationException();
    }
    
    public File getBuildxml() {
        return this.buildxml;
    }

    public void setPolishHome(File file) {
        this.polishHome = file;
    }

    public File getPolishHome() {
        return this.polishHome;
    }

    public void setWTKHome(File file) {
        if(file == null){
            throw new IllegalArgumentException("setWTKHome(...):parameter 'file' is null contrary to API.");
        }
        this.wtkHome = file;
    }
    
    public File getWTKHome() {
        return this.wtkHome;
    }

    public Map getStoreableProperties() {
        throw new UnsupportedOperationException();
    }

    public void restoreFromProperties(Map p) {
        throw new UnsupportedOperationException();
    }

//    public void build(String targetName) {
//        if(getBuildxml() == null) {
//            throw new IllegalStateException("No build.xml file specified.");
//        }
//        //TODO: Why is this needed?
//        System.setProperty("java.home","/usr/lib/j2sdk1.5-sun");
//        this.antBox = new AntBox();
//        
//        AntCorePreferences p = AntCorePlugin.getPlugin().getPreferences();
//        IAntClasspathEntry toolsJarEntry = p.getToolsJarEntry();
//        this.antBox.setToolsLocation(new File(toolsJarEntry.toString()));
//        
//        this.antBox.setAlternativeClassLoader(this.antClassLoader);
//        this.antBox.setWorkingDirectory(this.projectHome);
//        this.antBox.setBuildxml(this.buildxml);
//        Project antProject = this.antBox.createProject();
//        List list = getBuildListeners();
//        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
//            BuildListener aBuildListener = (BuildListener) iterator.next();
//            antProject.addBuildListener(aBuildListener);
//        }
//        
//        antProject.setUserProperty("device",getCurrentDevice().getIdentifier());
//        antProject.setUserProperty("polish.home",getPolishHome().getAbsolutePath());
//        Vector targets = new Vector();
//        targets.add("clean");
//        targets.add("init");
//        targets.add(getBuildTargetName());
//        antProject.executeTargets(targets);
//    }

//    private List getBuildListeners() {
//        List list = new LinkedList();
//        IExtensionPoint point = org.eclipse.core.runtime.Platform.getExtensionRegistry().getExtensionPoint(MeposeConstants.ID_BUILD_LISTENERS);
//        IExtension[] extensions = point.getExtensions();
//        for (int i = 0; i < extensions.length; i++) {
//            
//            IConfigurationElement[] configurationElements = extensions[i].getConfigurationElements();
//            for (int j = 0; j < configurationElements.length; j++) {
//                try {
//                    Object o = configurationElements[j].createExecutableExtension("class");
//                    BuildListener aBuildListener = (BuildListener)o;
//                    list.add(aBuildListener);
//                } catch (CoreException exception) {
//                    MeposePlugin.log("Could not create extensions.",exception);
//                }
//            }
//        }
//        return list;
//    }

    /**
     * @return the name of the ant target which only builds the sources.
     */
    public String getBuildTargetName() {
        return this.buildTargetName;
    }
    
    public void setBuildTargetName(String buildTargetName) {
        this.buildTargetName = buildTargetName;
    }
    
    public void addBuildListener(BuildListener aBuildListener) {
        this.buildListener.add(aBuildListener);
        this.antBox.getProject().addBuildListener(aBuildListener);
    }
    
    public void removeBuildListener(BuildListener aBuildListener) {
        this.buildListener.remove(aBuildListener);
        this.antBox.getProject().removeBuildListener(aBuildListener);
    }


    /**
     * @return
     */
    public String getCurrentDeviceIdentifier() {
        // TODO rickyn implement getCurrentDeviceIdentifier
        throw new UnsupportedOperationException();
    }


    /**
     * @return
     */
    public String getProjectHome() {
        throw new UnsupportedOperationException();
    }
    
}
