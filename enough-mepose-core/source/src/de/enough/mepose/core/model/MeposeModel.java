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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.AntCorePreferences;
import org.eclipse.ant.core.IAntClasspathEntry;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;

import de.enough.mepose.core.MeposeConstants;
import de.enough.mepose.core.MeposePlugin;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.PolishTask;
import de.enough.polish.devices.Configuration;
import de.enough.polish.devices.ConfigurationManager;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.DeviceManager;
import de.enough.polish.devices.DeviceTree;
import de.enough.polish.devices.DeviceTreeItem;
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

    private static final String ID_SUPPORTED_CLASSPATH = "id.supported.classpath";

    private static final Object ID_CURRENT_TARGET_ONLYBUILD = "id.current.target.onlybuild";

    
    
    // ###################################################################
    // Fields.
    
    // Ant.
    private AntBox antBox;
    private File buildxml = new File("");
    private PolishTask polishTask;
    private Environment environment;

    // Path.
//    private String projectPath = "";
    private File polishHome = new File("");
    private File wtkHome = new File("");
    private File nokiaHome = new File("");
    private File mppHome = new File("");
    private File projectHome = new File("");

    // Supported Config.
    private DeviceDatabase deviceDatabase;
    private DeviceTree deviceTree;
    private IClasspathEntry[] classpathEntries;
    private Configuration[] supportedConfigurations = new Configuration[0];
    private Platform[] supportedPlatforms = new Platform[0];
    private Device[] supportedDevices = new Device[0];

    // Current Config.
    private File jadFile;
    private Device currentDevice;
    
    private String currentDeviceName = DEFAULT_DEVICE_NAME;

    // Info.
    private String projectDescription = "";

    // Internal.
    private List propertyChangeListeners;
    private String buildTargetName = "j2mepolish";
    private List buildListener;
    private ClassLoader antClassLoader;
    
    
    public MeposeModel() {
        reset();
    }
    
    
    // ###################################################################
    // Methods.
    
    //TODO: Check if all fields are initialized.
    //TODO: If fields are already set dispose them properly before resetting them.
    public void reset() {
        this.antBox = new AntBox();
        AntCorePreferences p = AntCorePlugin.getPlugin().getPreferences();
        IAntClasspathEntry[] antClasspathEntries = p.getDefaultAntHomeEntries();
        
        List antClasspathList = new LinkedList();
        try {
            for (int i = 0; i < antClasspathEntries.length; i++) {
                IAntClasspathEntry entry = antClasspathEntries[i];
                antClasspathList.add(new URL("file://"+entry.toString()));
            }
            antClasspathList.add(new URL("file://"+p.getToolsJarEntry().toString()));
        } catch (MalformedURLException exception) {
            MeposePlugin.log("No tools.jar found.",exception);
        }
        
        URL[] toolsUrls = (URL[]) antClasspathList.toArray(new URL[antClasspathList.size()]);
        ClassLoader eclipseClassLoader = getClass().getClassLoader();
        this.antClassLoader = new URLClassLoader(toolsUrls,eclipseClassLoader);
        
        this.antBox.setAlternativeClassLoader(this.antClassLoader);
        this.buildxml = new File("");
        this.projectHome = new File("");
        this.propertyChangeListeners = new LinkedList();
        this.mppHome = new File("");
        File polishHomeFile;// = new File("");
        File mppHomeFile;// = new File("");
        this.jadFile = new File("");
        try {
            polishHomeFile  = new File(org.eclipse.core.runtime.Platform.asLocalURL(org.eclipse.core.runtime.Platform.find(MeposePlugin.getDefault().getBundle(),new Path("/j2mepolish124"))).getPath());
        } catch (IOException exception) {
            MeposePlugin.log("No embedded j2me polish found.",exception);
            throw new IllegalStateException("No embedded j2me polish found:"+exception);
        }
        try {
            mppHomeFile  = new File(org.eclipse.core.runtime.Platform.asLocalURL(org.eclipse.core.runtime.Platform.find(MeposePlugin.getDefault().getBundle(),new Path("/mpp-sdk"))).getPath());
        } catch (IOException exception) {
            MeposePlugin.log("No embedded mpp-sdk found.",exception);
            throw new IllegalStateException("No embedded mpp-sdk found.");
        }
        setPropertyValue(MeposeModel.ID_PATH_POLISH_FILE,polishHomeFile);
        setPropertyValue(MeposeModel.ID_PATH_MPP_FILE,mppHomeFile);
        
        setPolishHome(polishHomeFile);
        setMppHome(mppHomeFile);
        
        this.classpathEntries = null;
        this.buildListener = new LinkedList();
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
        firePropertyChangeEvent(ID_PATH_BUILDXML_FILE,null,this.buildxml);
    }

    // TODO: Break this method up to have several smaller ones.
    private void extractTaskFromBuildXML() {
        this.antBox.setWorkingDirectory(this.projectHome);
        this.antBox.setBuildxml(this.buildxml);
        
        // Configure all targets.
        Project project = this.antBox.createProject();
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
                MeposePlugin.log("Error:MeposeProject.setBuildxml():Could not configure target:"+target.getName());
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
        try {
            this.polishTask.initProject();
        } catch (Exception exception) {
            // Maybe a ClassNotFoundException if obfuscator is not present.
            MeposePlugin.log("Something went wrong while polishTask.initProject().",exception);
        }
        this.environment = this.polishTask.getEnvironment();
        firePropertyChangeEvent("environment",oldEnvironment,this.environment);
        this.polishTask.selectDevices();
//        this.configuredDevicesANT = this.polishTask.getDevices();
    }

    // TODO: Check if method is needed anymore.
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
        firePropertyChangeEvent(ID_ANT_ENVIRONMENT,null,this.environment);
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
        DeviceTree dt = getDeviceTree();
        if(dt == null) {
            return new Device[0];
        }
        return dt.getSelectedDevices();
//        return this.supportedDevices;
    }
    
    public Platform[] getSupportedPlatforms() {
        return this.supportedPlatforms;
    }

    public void setSupportedPlatforms(Platform[] supportedPlatforms) {
        this.supportedPlatforms = supportedPlatforms;
        firePropertyChangeEvent(ID_SUPPORTED_PLATFORMS,null,this.supportedPlatforms);
    }

    public void setSupportedConfigurations(Configuration[] supportedConfigurations) {
        if(supportedConfigurations == null){
            throw new IllegalArgumentException("setSupportedConfigurations(...):parameter 'supportedConfigurations' is null contrary to API.");
        }
        this.supportedConfigurations = supportedConfigurations;
        firePropertyChangeEvent(ID_SUPPORTED_CONFIGURATIONS,null,this.supportedConfigurations);
    }

    public Configuration[] getSupportedConfigurations() {
        return this.supportedConfigurations;
    }

    public void setSupportedDevices(Device[] supportedDevices) {
        if(supportedDevices == null){
            throw new IllegalArgumentException("setSupportedDevices(...):parameter 'supportedDevices' is null contrary to API.");
        }
        this.supportedDevices = supportedDevices;
        DeviceTree deviceTreeTemp = getDeviceTree();
        if(deviceTreeTemp == null) {
            return;
        }
        DeviceTreeItem[] deviceTreeItems = deviceTreeTemp.getDeviceTreeItems();
        for (int i = 0; i < deviceTreeItems.length; i++) {
            for (int j = 0; j < supportedDevices.length; j++) {
                if(deviceTreeItems[i].getDevice().getIdentifier().equals(supportedDevices[j].getIdentifier())) {
                    deviceTreeItems[i].setIsSelected(true);
                    break;
                }
            }
        }
        firePropertyChangeEvent(ID_SUPPORTED_DEVICES,null,this.supportedDevices);
    }
    
    /**
     * @param classpathEntries
     */
    public void setClasspath(IClasspathEntry[] classpathEntries) {
        if(classpathEntries == null){
            throw new IllegalArgumentException("setClasspath(...):parameter 'classpathEntries' is null contrary to API.");
        }
        this.classpathEntries = classpathEntries;
        firePropertyChangeEvent(ID_SUPPORTED_CLASSPATH,null,this.classpathEntries);
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
        firePropertyChangeEvent(ID_CURRENT_JADJARPAIRS,null,this.jadFile);
    }
    
    /**
     * 
     * @return May be null
     */
    public Device getCurrentDevice() {
        return this.currentDevice;
    }

    public void setCurrentDevice(Device currentDevice) {
        if(currentDevice == null){
            throw new IllegalArgumentException("setCurrentDevice(...):parameter 'currentDevice' is null contrary to API.");
        }
        this.currentDevice = currentDevice;
        setCurrentDeviceName(this.currentDevice.getIdentifier());
        firePropertyChangeEvent(ID_CURRENT_DEVICE,null,this.currentDevice);
    }
    
    
    // ###################################################################
    // Paths methods.
    
    public File getProjectHome() {
        return this.projectHome;
    }

    public void setProjectHome(File projectHome) {
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
    
//    public String getProjectPath() {
//        return this.projectPath;
//    }
//   
//    public void setProjectPath(String projectPath) {
//        if(projectPath == null){
//            throw new IllegalArgumentException("ERROR:MeposeProject.setProjectPath(...):Parameter 'projectPath' is null.");
//        }
//        this.projectPath = projectPath;
//    }

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
            MeposePlugin.log("No polish home set in model");
            return null;
        }
        HashMap properties = new HashMap();
//        properties.put("wtk.home",this.wtkHome.getAbsolutePath());
        properties.put("mpp.home",this.mppHome.getAbsolutePath());
        try {
            //TODO: Get the deviceDatabase from the PolishTask instead of creating a new one.
            this.deviceDatabase = new DeviceDatabase(properties,this.polishHome,this.projectHome,null,null,new HashMap(),new HashMap());
        } catch (Exception e) {
            System.out.println("ERROR:MeposeModel.getDeviceDatabase(...):"+e);
            e.printStackTrace();
        }
        return this.deviceDatabase;
    }

    // May be null.
    public DeviceTree getDeviceTree() {
        
        if(this.deviceTree == null && getDeviceDatabase() != null) {
            this.deviceTree = new DeviceTree(getDeviceDatabase(),null,null);
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
        firePropertyChangeEvent(ID_INFO_DESCRIPTION,null,this.projectDescription);
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
        p.put(ID_PATH_PROJECT_FILE,getProjectHome().toString());
        p.put(ID_PATH_BUILDXML_FILE,getBuildxml().toString());
//        p.put(ID_PATH_WORKINGDIRECTORY_BUILD)
        
        // Supported Config.
        p.put(ID_SUPPORTED_CONFIGURATIONS,Arrays.arrayToString(getSupportedConfigurations()));
        p.put(ID_SUPPORTED_PLATFORMS,Arrays.arrayToString(getSupportedPlatforms()));
        p.put(ID_SUPPORTED_DEVICES,Arrays.arrayToString(getSupportedDevices()));
        
        // Current Config.
        if(getCurrentDevice() != null) {
            p.put(ID_CURRENT_DEVICE,getCurrentDevice());
        }
        p.put(ID_CURRENT_TARGET_ONLYBUILD,getBuildTargetName());
        return p;
    }


    public void restoreFromProperties(Map p) {
        
        
        // Paths.
        String polishHomeTmp = (String)p.get(ID_PATH_POLISH_FILE);
        if(polishHomeTmp != null) {
            setPolishHome(new File(polishHomeTmp));
        }
        
        String wtkHomeTmp = (String)p.get(ID_PATH_WTK_FILE);
        if(wtkHomeTmp != null) {
            setWTKHome(new File(wtkHomeTmp));
        }
        String mppHomeTmp = (String)p.get(ID_PATH_MPP_FILE);
        if(mppHomeTmp != null) {
            setMppHome(new File(mppHomeTmp));
        }
        String nokiaHomeTmp = (String)p.get(ID_PATH_NOKIA_FILE);
        if(nokiaHomeTmp != null) {
            setNokiaHome(new File(nokiaHomeTmp));
        }
        String projectNameTmp = (String)p.get(ID_PATH_PROJECT_FILE);
        if(projectNameTmp != null) {
            setProjectHome(new File(projectNameTmp));
        }
        String buildXmlTmp = (String)p.get(ID_PATH_BUILDXML_FILE);
        if(buildXmlTmp != null) {
            setBuildxml(new File(buildXmlTmp));
        }
        
        DeviceDatabase db = getDeviceDatabase();
        if(db == null) {
            throw new IllegalStateException("Device Database not found. Most likely some paths are invalid.");
        }
        
        // Supported Config.
        String supportedConfigurationsString = (String)p.get(ID_SUPPORTED_CONFIGURATIONS);
        if(supportedConfigurationsString != null) {
            String[] supportedConfigurationsArray = supportedConfigurationsString.split(",");
            Configuration[] supportedConfigurationsTmp = new Configuration[supportedConfigurationsArray.length];
            ConfigurationManager cm = db.getConfigurationManager();
            for (int i = 0; i < supportedConfigurationsArray.length; i++) {
                String identifier = supportedConfigurationsArray[i];
                Configuration c = cm.getConfiguration(identifier);
                supportedConfigurationsTmp[i] = c;
            }
            setSupportedConfigurations(supportedConfigurationsTmp);
        }
        
        String supportedPlatformsString = (String)p.get(ID_SUPPORTED_PLATFORMS);
        if(supportedPlatformsString != null) {
            String[] supportedPlatformsArray = supportedPlatformsString.split(",");
            Platform[] supportedPlatformsTmp = new Platform[supportedPlatformsArray.length];
            PlatformManager pm = db.getPlatformManager();
            for (int i = 0; i < supportedPlatformsArray.length; i++) {
                String identifier = supportedPlatformsArray[i];
                Platform platform = pm.getPlatform(identifier);
                supportedPlatformsTmp[i] = platform;
            }
            setSupportedPlatforms(supportedPlatformsTmp);
        }
        
        String supportedDevicesString = (String)p.get(ID_SUPPORTED_DEVICES);
        if(supportedDevicesString != null && ! supportedDevicesString.equals("")) {
            String[] supportedDevicesArray = supportedDevicesString.split(",");
            Device[] supportedDevicesTemp = new Device[supportedDevicesArray.length];
            DeviceManager deviceManager = db.getDeviceManager();
            for(int i = 0; i < supportedDevicesArray.length; i++) {
                String identifier = supportedDevicesArray[i];
                Device device = deviceManager.getDevice(identifier);
                supportedDevicesTemp[i] = device;
            }
            setSupportedDevices(supportedDevicesTemp);
        }
    }

    public void build(String targetName) {
        if(getBuildxml() == null) {
            throw new IllegalStateException("No build.xml file specified.");
        }
        System.setProperty("java.home","/usr/lib/j2sdk1.5-sun");
        this.antBox = new AntBox();
        
        AntCorePreferences p = AntCorePlugin.getPlugin().getPreferences();
        IAntClasspathEntry antClasspathEntry = p.getToolsJarEntry();
        this.antBox.setToolsLocation(new File(antClasspathEntry.toString()));
        
        this.antBox.setAlternativeClassLoader(this.antClassLoader);
        this.antBox.setWorkingDirectory(this.projectHome);
        this.antBox.setBuildxml(this.buildxml);
        Project antProject = this.antBox.createProject();
        //TODO: Do this with an extension point.
        List list = getBuildListeners();
        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            BuildListener aBuildListener = (BuildListener) iterator.next();
            antProject.addBuildListener(aBuildListener);
        }
        
//        Requirements r = new Requirements();
//        r.isMet(getDeviceDatabase().getDeviceManager().getDevice(getCurrentDeviceName()));
//        this.polishTask.addConfiguredDeviceRequirements(r);
        
//        antProject.setInheritedProperty("java.home","/usr/lib/j2sdk1.5-sun");
        antProject.setUserProperty("device",getCurrentDeviceName());
        antProject.setUserProperty("polish.home",getPolishHome().getAbsolutePath());
        Vector targets = new Vector();
        targets.add("clean");
        targets.add(getBuildTargetName());
        antProject.executeTargets(targets);
    }

    private List getBuildListeners() {
        List list = new LinkedList();
        IExtensionPoint point = org.eclipse.core.runtime.Platform.getExtensionRegistry().getExtensionPoint(MeposeConstants.ID_BUILD_LISTENERS);
        IExtension[] extensions = point.getExtensions();
        for (int i = 0; i < extensions.length; i++) {
            
            IConfigurationElement[] configurationElements = extensions[i].getConfigurationElements();
            for (int j = 0; j < configurationElements.length; j++) {
                try {
                    Object o = configurationElements[j].createExecutableExtension("class");
                    BuildListener aBuildListener = (BuildListener)o;
                    list.add(aBuildListener);
                } catch (CoreException exception) {
                    MeposePlugin.log("Could not create extensions.",exception);
                }
            }
        }
        return list;
    }

    /**
     * @return the name of the ant target which only builds the sources.
     */
    public String getBuildTargetName() {
        return this.buildTargetName;
    }
    
    public void setBuildTargetName(String buildTargetName) {
        this.buildTargetName = buildTargetName;
    }


    /*
     * May be out of sync with getCurrentDevice(). It is mostly for bootstrapping
     * when no DeviceDatabase is present.
     */
    public String getCurrentDeviceName() {
        return this.currentDeviceName;
    }


    public void setCurrentDeviceName(String currentDeviceName) {
        if(currentDeviceName == null){
            throw new IllegalArgumentException("setCurrentDeviceName(...):parameter 'currentDeviceName' is null contrary to API.");
        }
        this.currentDeviceName = currentDeviceName;
    }
    
    public void addBuildListener(BuildListener aBuildListener) {
        this.buildListener.add(aBuildListener);
    }
    
    public void removeBuildListener(BuildListener aBuildListener) {
        this.buildListener.remove(aBuildListener);
    }
    
}
