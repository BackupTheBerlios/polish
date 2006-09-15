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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.eclipse.jdt.core.IClasspathEntry;

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
    private PolishTask polishTask;
    private Environment environment;

    // Path.
//    private String projectPath = "";
    private File polishHome = null;
    private File wtkHome = new File("");
    private File nokiaHome = new File("");
    private File sonyHome = new File("");
    private File motorolaHome = new File("");
    private File siemensHome = new File("");
    private File projectHome = new File("");
    private File mppHome = new File("");

    // Supported Config.
//    private DeviceDatabase deviceDatabase;
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
    private String buildTargetName = "j2mepolish";
    private List buildListener;
//    private ClassLoader antClassLoader;

    private Map variables;
    
    
    public MeposeModel() {
        reset();
    }
    
    
    // ###################################################################
    // Methods.
    
    //TODO: Check if all fields are initialized.
    //TODO: If fields are already set dispose them properly before resetting them.
    public void reset() {
        resetAntBox();

        this.buildxml = new File("");
        this.projectHome = new File("");
        this.propertyChangeListeners = new LinkedList();
        
        this.jadFile = new File("");
        
        this.classpathEntries = null;
        this.buildListener = new LinkedList();
        
        this.variables = new HashMap();
        
    }

    private void resetAntBox() {
        this.antBox = new AntBox();
        
        // This code was used set a classloader with tools.jar on board. It is not needed
        // anymore as this injection is done in the MeposePlugin on startup.
//        AntCorePreferences antPreferences = AntCorePlugin.getPlugin().getPreferences();
//        IAntClasspathEntry[] antClasspathEntries = antPreferences.getDefaultAntHomeEntries();
//        
//        List antClasspathList = new LinkedList();
//        try {
//            for (int i = 0; i < antClasspathEntries.length; i++) {
//                IAntClasspathEntry entry = antClasspathEntries[i];
//                antClasspathList.add(new URL("file://"+entry.toString()));
//            }
//            URL toolsJarUrl = new URL("file://"+antPreferences.getToolsJarEntry().toString());
//            antClasspathList.add(toolsJarUrl);
//        } catch (MalformedURLException exception) {
//            MeposePlugin.log("No tools.jar found.",exception);
//        }
        
        // We inject the tools.jar classpath element now directly into the eclipse class loader.
//        URL[] antClasspathAsUrls = (URL[]) antClasspathList.toArray(new URL[antClasspathList.size()]);
//        ClassLoader eclipseClassLoader = getClass().getClassLoader();
//        this.antClassLoader = new URLClassLoader(antClasspathAsUrls,eclipseClassLoader);
//        this.antClassLoader = new AntClassLoader(eclipseClassLoader,false);
//        for (int i = 0; i < antClasspathAsUrls.length; i++) {
//            ((AntClassLoader)this.antClassLoader).addPathElement(antClasspathAsUrls[i].getPath());
//        }
//        //TODO: Commented for testing.
//        this.antBox.setAlternativeClassLoader(this.antClassLoader);
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
        if( ! this.buildxml.exists()) {
            setPropertyStatus(ID_PATH_BUILDXML_FILE,STATUS_BUILDXML_MISSING);
            return;
        }
        try {
            extractTaskFromBuildXML();
        } catch (Throwable e) {
            // Something went wrong.
            MeposePlugin.log("build.xml could not be set."+e);
        }
        firePropertyChangeEvent(ID_PATH_BUILDXML_FILE,null,this.buildxml);
    }

    // TODO: Break this method up to have several smaller ones.
    private void extractTaskFromBuildXML() {
        this.antBox.setWorkingDirectory(this.projectHome);
        this.antBox.setBuildxml(this.buildxml);
        this.antBox.setProperty("dir.work","build/test");
        
        // Configure all ant targets.
        Project project = this.antBox.createProject();
        Map targetObjectForTargetName = project.getTargets();
        Collection targetObjectSet = targetObjectForTargetName.values();
        this.polishTask = null;
        for (Iterator iterator = targetObjectSet.iterator(); iterator.hasNext(); ) {
            Target target = (Target) iterator.next();
            try {
                this.antBox.configureTarget(target);
            }
            catch(BuildException e) {
                // configuring the target failed, maybe because the taskdef could not be resolved.
                MeposePlugin.log("MeposeProject.setBuildxml():Could not configure target:"+target.getName(),e);
                continue;
            }
            Task[] tasks = target.getTasks();
            Task task;
            for (int taskIndex = 0; taskIndex < tasks.length; taskIndex++) {
                task = tasks[taskIndex];
                    if(task instanceof PolishTask) {
                        this.polishTask = (PolishTask)task;
                        setPropertyStatus(ID_ANT_TASK_POLISH,Status.OK);
                        break;
                    }
            }
            if(this.polishTask != null) {
                break;
            }
        }
        //TODO: Think about alternative error reporting facility instead of boolean return value.
        if(this.polishTask == null) {
            setPropertyStatus(ID_ANT_TASK_POLISH,new Status(Status.TYPE_ERROR,"Ant task 'polish' not found in build.xml",null));
            MeposePlugin.log("Ant task 'polish' not found in build.xml");
            return;
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
        // Needed for polish initialization.
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
  
    public String getSourcePath(Device device) {
        this.polishTask.initialize(device,null);
        return device.getSourceDir();
    }
    
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
        
        // TODO: This needs to be done as a listener task as the case of a change
        // in the supported devices must be handled.
        //TODO: Remove the old values of devices not present any more.
        if(this.environment != null) {
            this.variables.clear();
            for (int i = 0; i < supportedDevices.length; i++) {
                this.environment.initialize(supportedDevices[i],null);
                this.variables.putAll(this.environment.getSymbols());
                this.variables.putAll(this.environment.getVariables());
            }
        }
        firePropertyChangeEvent(ID_SUPPORTED_DEVICES,null,this.supportedDevices);
    }
    
    public Map getVariables() {
        return this.variables;
    }
    
    public void setClasspath(IClasspathEntry[] classpathEntries) {
        if(classpathEntries == null){
            throw new IllegalArgumentException("setClasspath(...):parameter 'classpathEntries' is null contrary to API.");
        }
        this.classpathEntries = classpathEntries;
        firePropertyChangeEvent(ID_SUPPORTED_CLASSPATH,null,this.classpathEntries);
    }
    
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
    
    public Device getCurrentDevice() {
        return this.currentDevice;
    }

    public void setCurrentDevice(Device currentDevice) {
        if(currentDevice == null){
            throw new IllegalArgumentException("setCurrentDevice(...):parameter 'currentDevice' is null contrary to API.");
        }
        this.currentDevice = currentDevice;
        firePropertyChangeEvent(ID_CURRENT_DEVICE,null,this.currentDevice);
        if(this.environment != null) {
            this.environment.initialize(currentDevice,null);
        }
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
    
    public void setSonyHome(File sonyHome) {
        this.sonyHome = sonyHome;
    }
    
    public File getSonyHome() {
        return this.sonyHome;
    }
    
    public File getMotorolaHome() {
        return this.motorolaHome;
    }


    public void setMotorolaHome(File motorolaHome) {
        this.motorolaHome = motorolaHome;
    }


    public File getSiemensHome() {
        return this.siemensHome;
    }


    public void setSiemensHome(File siemensHome) {
        this.siemensHome = siemensHome;
    }

    public File getMppHome() {
        return this.mppHome;
    }


    public void setMppHome(File mppHome) {
        this.mppHome = mppHome;
    }


    /**
     * @param file May be null to indicate that there is no polish home specified.
     */
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

    /**
     * 
     * @return May be null.
     * @throws DeviceDatabaseException 
     */
    public DeviceDatabase getDeviceDatabase() throws DeviceDatabaseException {
//        if(this.deviceDatabase == null) {
//            this.deviceDatabase = PolishDeviceDatabase.getDeviceDatabase(this.polishHome,this.projectHome);
//        }
//        return this.deviceDatabase;
        if(getPolishHome() != null && getProjectHome() != null) {
            return PolishDeviceDatabase.getDeviceDatabase(this.polishHome,this.projectHome);
        }
        return null;
    }

    // May be null.
    public DeviceTree getDeviceTree() {
        
        DeviceDatabase deviceDatabase2;
        try {
            deviceDatabase2 = getDeviceDatabase();
        } catch (DeviceDatabaseException exception) {
            //TODO: Rethrow the exception.
            return null;
        }
        if(this.deviceTree == null && deviceDatabase2 != null) {
            try {
                this.deviceTree = new DeviceTree(deviceDatabase2,null,null);
            }
            catch(Exception e) {
                return null;
            }
        }
      
        return this.deviceTree;
        
    }

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
        
        File value;
        // Paths.
        value = getPolishHome();
        if(value != null) {
            p.put(ID_PATH_POLISH_FILE,value.toString());
        }
        value = getWTKHome();
        p.put(ID_PATH_WTK_FILE,value==null?"":value.toString());
        value = getNokiaHome();
        p.put(ID_PATH_NOKIA_FILE,value==null?"":value.toString());
        value = getSonyHome();
        p.put(ID_PATH_SONY_FILE,value==null?"":value.toString());
        value = getSiemensHome();
        p.put(ID_PATH_SIEMENS_FILE,value==null?"":value.toString());
        value = getMotorolaHome();
        p.put(ID_PATH_MOTOROLA_FILE,value==null?"":value.toString());
        value = getMppHome();
        p.put(ID_PATH_MPP_FILE,value==null?"":value.toString());
        value = getProjectHome();
        p.put(ID_PATH_PROJECT_FILE,value==null?"":value.toString());
        value = getBuildxml();
        p.put(ID_PATH_BUILDXML_FILE,value==null?"":value.toString());
//        p.put(ID_PATH_WORKINGDIRECTORY_BUILD)
        
        // Supported Config.
        Configuration[] supportedConfigurations2 = getSupportedConfigurations();
        if(supportedConfigurations2 != null) {
            p.put(ID_SUPPORTED_CONFIGURATIONS,Arrays.arrayToString(supportedConfigurations2));
        }
        
        Platform[] supportedPlatforms2 = getSupportedPlatforms();
        if(supportedPlatforms2 != null) {
            p.put(ID_SUPPORTED_PLATFORMS,Arrays.arrayToString(supportedPlatforms2));
        }
        Device[] supportedDevices2 = getSupportedDevices();
        if(supportedDevices2 != null) {
            p.put(ID_SUPPORTED_DEVICES,Arrays.arrayToString(supportedDevices2));
        }
        
        // Current Config.
        Device currentDevice2 = getCurrentDevice();
        if(currentDevice2 != null) {
            p.put(ID_CURRENT_DEVICE,currentDevice2.getIdentifier());
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
        String motorolaHomeTmp = (String)p.get(ID_PATH_MOTOROLA_FILE);
        if(motorolaHomeTmp != null) {
            setMotorolaHome(new File(motorolaHomeTmp));
        }
        String siemensHomeTmp = (String)p.get(ID_PATH_SIEMENS_FILE);
        if(siemensHomeTmp != null) {
            setSiemensHome(new File(siemensHomeTmp));
        }
        String sonyHomeTmp = (String)p.get(ID_PATH_SONY_FILE);
        if(sonyHomeTmp != null) {
            setSonyHome(new File(sonyHomeTmp));
        }
        String projectNameTmp = (String)p.get(ID_PATH_PROJECT_FILE);
        if(projectNameTmp != null) {
            setProjectHome(new File(projectNameTmp));
        }
        String buildXmlTmp = (String)p.get(ID_PATH_BUILDXML_FILE);
        if(buildXmlTmp != null) {
            setBuildxml(new File(buildXmlTmp));
        }
        
        DeviceDatabase db = null;
        try {
            db = getDeviceDatabase();
        } catch (DeviceDatabaseException exception) {
            MeposePlugin.log(exception);
        }
        if(db == null) {
            return;
            // Return as from here on we initialize things like supported devices.
            // But this is not possible without a db.
//            throw new IllegalStateException("Device Database not found. Most likely some paths are invalid.");
        }
        
        // Supported Config.
        String supportedConfigurationsString = (String)p.get(ID_SUPPORTED_CONFIGURATIONS);
        if(supportedConfigurationsString != null) {
            String[] supportedConfigurationsArray = supportedConfigurationsString.split(",");
            Configuration[] supportedConfigurationsTmp = new Configuration[supportedConfigurationsArray.length];
            ConfigurationManager cm = db.getConfigurationManager();
            // TODO: We can return here only because we know that the whole deviceDatabase is not initialized
            // and everything from here on is about this db.
            if(cm == null) {
                return;
            }
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
            if(pm == null) {
                return;
            }
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
            List supportedDevicesTemp = new LinkedList();
            DeviceManager deviceManager = db.getDeviceManager();
            if(deviceManager == null) {
                return;
            }
            for(int i = 0; i < supportedDevicesArray.length; i++) {
                String identifier = supportedDevicesArray[i];
                Device device = deviceManager.getDevice(identifier);
                //TODO: Sometimes the Nokia/6611 is not found although it is in the supportedDevicesString.
                if(device == null) {
                    continue;
                }
                supportedDevicesTemp.add(device);
            }
            setSupportedDevices((Device[]) supportedDevicesTemp.toArray(new Device[supportedDevicesTemp.size()]));
        }
        
        String currentDeviceString = (String)p.get(ID_CURRENT_DEVICE);
        if(currentDeviceString != null) {
            Device device = db.getDeviceManager().getDevice(currentDeviceString);
            if(device != null) {
                setCurrentDevice(device);
            }
        }
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

    public void setDeviceTree(DeviceTree deviceTreeModel) {
        this.deviceTree = deviceTreeModel;
    }
    
}
