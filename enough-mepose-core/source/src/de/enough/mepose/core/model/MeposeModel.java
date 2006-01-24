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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;

import de.enough.mepose.core.CorePlugin;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.PolishTask;
import de.enough.polish.devices.Configuration;
import de.enough.polish.devices.ConfigurationManager;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.DeviceTree;
import de.enough.polish.devices.Platform;
import de.enough.polish.devices.PlatformManager;
import de.enough.utils.AntBox;
import de.enough.utils.Arrays;
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
    
    private static final Status STATUS_BUILDXML_MISSING = new Status(Status.TYPE_ERROR,"build.xml file does not exist.",null);

//    public static final String ERROR_NOBUILDXML_FILE = "No build.xml file specified.";
//    public static final String ERROR_NO_DEVICE = "No device specified.";
//    public static final String ERROR_PARSE_ERROR = "Errors while parsing build.xml";
//    public static final String ERROR_INVALID_WORKING_DIR = "Working directory is not valid";
    
    // ###################################################################
    // IDs.
    
    // Ant.
    public static final String ID_ANT_TASK_POLISH = "id.ant.task.polish";
    
    // Paths.
    public static final String ID_PATH_WORKINGDIRECTORY_BUILD = "id.build.wd";
    public static final String ID_PATH_BUILDXML_FILE = "id.buildxml";
    public static final String ID_PATH_PROJECT_FILE = "id.path.projecthome";
    public static final String ID_PATH_POLISH_FILE = "id.path.polishhome";
    public static final String ID_PATH_WTK_FILE = "id.path.wtkhome";
    public static final String ID_PATH_MPP_FILE = "id.path.mpphome";
    public static final String ID_PATH_NOKIA_FILE = "id.path.nokiahome";
    
    // Supported Config.
    public static final String ID_SUPPORTED_CONFIGURATIONS = "id.supported.configurations";
    public static final String ID_SUPPORTED_PLATFORMS = "id.supported.platforms";
    public static final String ID_SUPPORTED_DEVICES = "id.supported.devices";

    // Current Config.
    public static final String ID_CURRENT_DEVICE = "id.current.device";
    public static final String ID_CURRENT_JADJARPAIRS = "id.current.jadjarpairs";
    
    // Info.
    public static final String ID_INFO_DESCRIPTION = "id.info.description";
    
    
    // ###################################################################
    // Fields.
    
    // Ant.
    private AntBox antBox;
    private File buildxml = new File("");
    private PolishTask polishTask;
    private Environment environment;

    // Path.
    private String projectPath = "";
    private File polishHome = new File("");
    private File wtkHome = new File("");
    private File nokiaHome = new File("");
    private File mppHome = new File("");
    private File projectHome = new File("");

    // Supported Config.
//    private Device[] configuredDevicesANT = new Device[0];
    private DeviceDatabase deviceDatabase;
    private DeviceTree deviceTree;
    private IClasspathEntry[] classpathEntries;
    private Configuration[] supportedConfigurations = new Configuration[0];
    private Platform[] supportedPlatforms = new Platform[0];
    private Device[] supportedDevices = new Device[0];

    // Current Config.
    private File jadFile;
    private Device currentDevice;

    // Info.
    private String projectDescription = "";

    // Internal.
    private List propertyChangeListeners;
    
    
    public MeposeModel() {
        reset();
    }
    
    
    // ###################################################################
    // Methods.
    
    //TODO: Check if all fields are initialized.
    //TODO: If fields are already set dispose them properly.
    public void reset() {
        this.antBox = new AntBox();
        this.antBox.setAlternativeClassLoader(getClass().getClassLoader());
        this.buildxml = new File("");
        this.projectPath = "";
        this.propertyChangeListeners = new LinkedList();
        this.mppHome = new File("");
        File polishHomeFile;// = new File("");
        File mppHomeFile;// = new File("");
        this.jadFile = new File("");
        try {
            polishHomeFile  = new File(org.eclipse.core.runtime.Platform.asLocalURL(org.eclipse.core.runtime.Platform.find(CorePlugin.getDefault().getBundle(),new Path("/j2mepolish124"))).getPath());
        } catch (IOException exception) {
            CorePlugin.log("No embedded j2me polish found.",exception);
            throw new IllegalStateException("No embedded j2me polish found:"+exception);
        }
        try {
            mppHomeFile  = new File(org.eclipse.core.runtime.Platform.asLocalURL(org.eclipse.core.runtime.Platform.find(CorePlugin.getDefault().getBundle(),new Path("/mpp-sdk"))).getPath());
        } catch (IOException exception) {
            CorePlugin.log("No embedded mpp-sdk found.",exception);
            throw new IllegalStateException("No embedded mpp-sdk found.");
        }
        
        setPropertyValue(MeposeModel.ID_PATH_POLISH_FILE,polishHomeFile);
        setPropertyValue(MeposeModel.ID_PATH_MPP_FILE,mppHomeFile);
        
        setPolishHome(polishHomeFile);
        setMppHome(mppHomeFile);
        
        this.classpathEntries = null;
    }
    
    
    // ###################################################################
    // Ant.
    
    /**
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
        if( ! this.buildxml.exists()) {
            setPropertyStatus(ID_PATH_BUILDXML_FILE,STATUS_BUILDXML_MISSING);
            return;
        }
        extractTaskFromBuildXML();
    }

    // TODO: Break this method up to have several smaller ones.
    private void extractTaskFromBuildXML() {
        this.antBox.setWorkingDirectory(this.projectPath);
        this.antBox.createProject();
        
        // Configure all targets.
        Project project = this.antBox.getProject();
        Map targetNameToTargetObjectMapping = project.getTargets();
        Collection targetObjectSet = targetNameToTargetObjectMapping.values();
        boolean foundPolishTask = false;
        this.polishTask = null;
        for (Iterator iterator = targetObjectSet.iterator(); iterator.hasNext(); ) {
            Target target = (Target) iterator.next();
            try {
                this.antBox.configureTarget(target);
            }
            catch(BuildException e) {
                // configuring the target failed, maybe because the taskdef could not be resolved.
                CorePlugin.log("Error:MeposeProject.setBuildxml():Could not configure target:"+target.getName());
                continue;
            }
            Task[] tasks = target.getTasks();
            Task task;
            for (int taskIndex = 0; taskIndex < tasks.length; taskIndex++) {
                task = tasks[taskIndex];
                    if(task instanceof PolishTask) {
                        this.polishTask = (PolishTask)task;
                        foundPolishTask = true;
                        setPropertyStatus(ID_ANT_TASK_POLISH,Status.OK);
                        break;
                    }
            }
            if(foundPolishTask) {
                break;
            }
        }
        //TODO: Think about alternative error reporting facility instead of boolean return value.
        if(! foundPolishTask) {
            setPropertyStatus(ID_ANT_TASK_POLISH,new Status(Status.TYPE_ERROR,"Ant task 'polish' not found in build.xml",null));
            return;
            //throw new BuildException("No target with a 'PolishTask' was found.");
        }
        Environment oldEnvironment = this.environment;
        this.polishTask.initProject();
        this.environment = this.polishTask.getEnvironment();
        firePropertyChangeEvent("environment",oldEnvironment,this.environment);
        this.polishTask.selectDevices();
//        this.configuredDevicesANT = this.polishTask.getDevices();
    }

    // TODO: Check if needed anymore.
    /*
     * @see de.enough.polish.plugin.eclipse.core.PolishProject#setEnvironmentToDevice(de.enough.polish.Device)
     */
    protected void setEnvironmentToDevice(Device device) {
        if(device == null){
            throw new IllegalArgumentException("ERROR:AntPolishProject.setEnvironmentToDevice(...):Parameter 'device' is null.");
        }
        if(this.polishTask == null){
            throw new IllegalStateException("ERROR:MeposeProject.setEnvironmentToDevice(...):Field 'polishTask' is null.");
        }
        
        // This does not delete or create the environment object but modifies it in place.
        this.polishTask.initialize(device,null);
        this.environment = this.polishTask.getEnvironment();
    }
  
    /**
     * 
     * @return Never null.
     */
    public AntBox getAntBox() {
        return this.antBox;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setAntBox(AntBox antBox) {
        if(antBox == null){
            throw new IllegalArgumentException("ERROR:MeposeProject.setAntBox(...):Parameter 'antBox' is null.");
        }
        this.antBox = antBox;
    }

    public void setEnvironment(Environment environment) {
        if(environment == null){
            throw new IllegalArgumentException("ERROR:MeposeProject.setEnvironment(...):Parameter 'environment' is null.");
        }
        this.environment = environment;
    }
    
    /**
     * 
     * @return Never null.
     */
    public File getBuildxml() {
        return this.buildxml;
    }

    
    // ###################################################################
    // Supported Config methods.
    
    public Device[] getSupportedDevices() {
        return this.deviceTree.getSelectedDevices();
//        return this.supportedDevices;
    }
    
    public Platform[] getSupportedPlatforms() {
        return this.supportedPlatforms;
    }

    public void setSupportedPlatforms(Platform[] supportedPlatforms) {
        this.supportedPlatforms = supportedPlatforms;
    }

    public void setSupportedConfigurations(Configuration[] supportedConfigurations) {
        if(supportedConfigurations == null){
            throw new IllegalArgumentException("setSupportedConfigurations(...):parameter 'supportedConfigurations' is null contrary to API.");
        }
        this.supportedConfigurations = supportedConfigurations;
    }

    public Configuration[] getSupportedConfigurations() {
        return this.supportedConfigurations;
    }

    public void setSupportedDevices(Device[] supportedDevices) {
        this.supportedDevices = supportedDevices;
    }
    
    /**
     * @param classpathEntries
     */
    public void setClasspath(IClasspathEntry[] classpathEntries) {
        this.classpathEntries = classpathEntries;
    }
    
    /**
     * Get the classpath entries for the supported devices.
     * @return May be null.
     */
    public IClasspathEntry[] getClasspathEntries() {
        return this.classpathEntries;
    }
    
    
    // ###################################################################
    // Current methods.
    
    public File getJadFile() {
        return this.jadFile;
    }
    
    public void setJadFile(File jadFile) {
        if(jadFile == null){
            throw new IllegalArgumentException("setJadFile(...):parameter 'jadFile' is null contrary to API.");
        }
        this.jadFile = jadFile;
    }
    
    /**
     * 
     * @return May be null
     */
    public Device getCurrentDevice() {
        return this.currentDevice;
    }

    public void setCurrentDevice(Device currentDevice) {
        this.currentDevice = currentDevice;
    }
    
    
    // ###################################################################
    // Paths methods.
    
    public File getProjectName() {
        return this.projectHome;
    }

    public void setProjectName(File projectHome) {
        this.projectHome = projectHome;
    }
    
    /**
     * @param nokiaHome
     */
    public void setNokiaHome(File nokiaHome) {
        this.nokiaHome = nokiaHome;
    }

    public File getNokiaHome() {
        return this.nokiaHome;
    }
    
    public String getProjectPath() {
        return this.projectPath;
    }
   
    public void setProjectPath(String projectPath) {
        if(projectPath == null){
            throw new IllegalArgumentException("ERROR:MeposeProject.setProjectPath(...):Parameter 'projectPath' is null.");
        }
        this.projectPath = projectPath;
    }

    public void setPolishHome(File file) {
        if(file == null){
            throw new IllegalArgumentException("setPolishHome(...):parameter 'file' is null contrary to API.");
        }
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

    /**
     * 
     * @return Never null.
     */
    public File getMppHome() {
        return this.mppHome;
    }
    
    public void setMppHome(File mppHome) {
        this.mppHome = mppHome;
    }
    
    
//    public Device[] getConfiguredDevices() {
//        return this.configuredDevicesANT;
//    }
//
//    public void setConfiguredDevices(Device[] configuredDevices) {
//        if(configuredDevices == null){
//            throw new IllegalArgumentException("ERROR:MeposeProject.setConfiguredDevices(...):Parameter 'configuredDevices' is null.");
//        }
//        this.configuredDevicesANT = configuredDevices;
//    }

    
    /**
     * 
     * @return May be null.
     */
    public DeviceDatabase getDeviceDatabase() {
        if(this.deviceDatabase != null) {
            return this.deviceDatabase;
        }
        //TODO: Look out for places where this property is set instead of the setter method.
//        File polishHome = (File)getPropertyValue(ID_POLISH_HOME);
//        File projectHome = (File)getPropertyValue(ID_PROJECT_HOME);
//        File wtkHome = (File)getPropertyValue(ID_WTK_HOME);

        if(this.polishHome == null||this.projectHome == null) {
            CorePlugin.log("No polish home set in model");
            return null;
        }
        HashMap properties = new HashMap();
//        properties.put("wtk.home",this.wtkHome.getAbsolutePath());
        properties.put("mpp.home",this.mppHome.getAbsolutePath());
        try {
            this.deviceDatabase = new DeviceDatabase(properties,this.polishHome,this.projectHome,null,null,new HashMap(),new HashMap());
        } catch (Exception e) {
            System.out.println("ERROR:MeposeModel.getDeviceDatabase(...):"+e);
            e.printStackTrace();
        }
        return this.deviceDatabase;
    }

    // May be null.
    public DeviceTree getDeviceTree() {
        
        if(this.deviceTree == null && this.deviceDatabase != null) {
            this.deviceTree = new DeviceTree(this.deviceDatabase,null,null);
        }
      
        return this.deviceTree;
        
    }

    
    /**
     * @param projectDescription
     */
    public void setProjectDescription(String projectDescription) {
        if(projectDescription == null){
            throw new IllegalArgumentException("setProjectDescription(...):parameter 'projectDescription' is null contrary to API.");
        }
        
        this.projectDescription = projectDescription;
    }

    public String getProjectDescription() {
        return this.projectDescription;
    }

    

    
    
    // ###################################################################
    // Maintainance.
    
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if(propertyChangeListener == null){
            throw new IllegalArgumentException("ERROR:MeposeProject.addPropertyChangeListener(...):Parameter 'propertyChangeListener' is null.");
        }
        this.propertyChangeListeners.add(propertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if(propertyChangeListener == null){
            throw new IllegalArgumentException("ERROR:MeposeProject.removePropertyChangeListener(...):Parameter 'propertyChangeListener' is null.");
        }
        this.propertyChangeListeners.remove(propertyChangeListener);
    }
    
    public void firePropertyChangeEvent(String property,Object oldValue, Object newValue) {
        if(property == null){
            throw new IllegalArgumentException("ERROR:MeposeProject.firePropertyChangeEvent(...):Parameter 'property' is null.");
        }
        PropertyChangeEvent event = new PropertyChangeEvent(this,property,oldValue,newValue);
        for (Iterator iterator = this.propertyChangeListeners.iterator(); iterator.hasNext(); ) {
            PropertyChangeListener propertyChangeListener = (PropertyChangeListener) iterator.next();
            propertyChangeListener.propertyChange(event);
        }
    }

    

    public Map getStoreableProperties() {
        Map p = new HashMap();
        
        // Paths.
        p.put(ID_PATH_POLISH_FILE,getPolishHome().toString());
        p.put(ID_PATH_WTK_FILE,getWTKHome().toString());
        p.put(ID_PATH_MPP_FILE,getMppHome().toString());
        p.put(ID_PATH_NOKIA_FILE,getNokiaHome().toString());
        p.put(ID_PATH_PROJECT_FILE,getProjectName().toString());
        p.put(ID_PATH_BUILDXML_FILE,getBuildxml().toString());
//        p.put(ID_PATH_WORKINGDIRECTORY_BUILD)
        
        // Supported Config.
        p.put(ID_SUPPORTED_CONFIGURATIONS,Arrays.arrayToString(getSupportedConfigurations()));
        p.put(ID_SUPPORTED_PLATFORMS,Arrays.arrayToString(getSupportedPlatforms()));
        p.put(ID_SUPPORTED_DEVICES,Arrays.arrayToString(getSupportedDevices()));
        
        if(getCurrentDevice() != null) {
            p.put(ID_CURRENT_DEVICE,getCurrentDevice());
        }
        
        return p;
    }


    public void restoreFromProperties(Map p) {
        
        
        // Paths.
        setPolishHome(new File((String)p.get(ID_PATH_POLISH_FILE)));
        setWTKHome(new File((String)p.get(ID_PATH_WTK_FILE)));
        setMppHome(new File((String)p.get(ID_PATH_MPP_FILE)));
        setNokiaHome(new File((String)p.get(ID_PATH_NOKIA_FILE)));
        setProjectName(new File((String)p.get(ID_PATH_PROJECT_FILE)));
        setBuildxml(new File((String)p.get(ID_PATH_BUILDXML_FILE)));
        
        DeviceDatabase db = getDeviceDatabase();
        if(db == null) {
            throw new IllegalStateException("Device Database not found. Most likely some paths are invalid.");
        }
        
        // Supported Config.
        String supportedConfigurationsString = (String)p.get(ID_SUPPORTED_CONFIGURATIONS);
        String[] supportedConfigurationsArray = supportedConfigurationsString.split(",");
        Configuration[] supportedConfigurations = new Configuration[supportedConfigurationsArray.length];
        ConfigurationManager cm = db.getConfigurationManager();
        for (int i = 0; i < supportedConfigurationsArray.length; i++) {
            String identifier = supportedConfigurationsArray[i];
            Configuration c = cm.getConfiguration(identifier);
            supportedConfigurations[i] = c;
        }
        setSupportedConfigurations(supportedConfigurations);
        
        String supportedPlatformsString = (String)p.get(ID_SUPPORTED_PLATFORMS);
        String[] supportedPlatformsArray = supportedPlatformsString.split(",");
        Platform[] supportedPlatforms = new Platform[supportedPlatformsArray.length];
        PlatformManager pm = db.getPlatformManager();
        for (int i = 0; i < supportedPlatformsArray.length; i++) {
            String identifier = supportedPlatformsArray[i];
            Platform platform = pm.getPlatform(identifier);
            supportedPlatforms[i] = platform;
        }
        setSupportedPlatforms(this.supportedPlatforms);
        
        // TODO:
//        supportedDevices
    }

    
    
    
    
    
    
    

    
}
