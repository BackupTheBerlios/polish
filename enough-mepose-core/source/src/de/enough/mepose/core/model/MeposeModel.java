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
import java.net.URISyntaxException;
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
import org.eclipse.core.runtime.Platform;

import de.enough.mepose.core.CorePlugin;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.PolishTask;
import de.enough.polish.devices.DeviceDatabase;
import de.enough.polish.devices.DeviceTree;
import de.enough.utils.AntBox;
import de.enough.utils.PropertyModel;
import de.enough.utils.Status;

/**
 * This class encapsulates the concepts of the polish build.xml in an abstract manner.
 * There is always a instance of this class present in the CorePlugin although
 * its fields may not be set.
 * Every project may have a MeposeModel.
 * <br>
 *
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
    
    public static final String ID_DEVICES_SUPPORTED = "id.devices.supported";
    public static final String ID_PLATFORMS_SUPPORTED = "id.platforms.supported";
    public static final String ID_POLISH_HOME = "id.path.polishhome";
    // This is a File instance
    public static final String ID_PROJECT_HOME = "id.path.projecthome";
    public static final String ID_WTK_HOME = "id.path.wtkhome";
    public static final String ID_BUILD_WD = "id.build.wd";
    public static final String ID_BUILDXML = "id.buildxml";
    public static final String ID_ANT_TASK_POLISH = "id.ant.task.polish";
    public static final String ID_MPP_HOME = "id.path.mpphome";
    
    private Device[] configuredDevices;
    private File buildxml = new File("");
    private String projectPath;
    private File polishHome = new File("");
    private File wtkHome = new File("");
    private File nokiaHome = new File("");
    private File mppHome = new File("");

    private AntBox antBox;
    private Environment environment;
    private PolishTask polishTask;
//    private ErrorSituation errorSituation;
    
    private List propertyChangeListeners;

    private DeviceDatabase deviceDatabase;

    private DeviceTree deviceTree;

    private String projectDescription = "";

    
    public MeposeModel() {
        reset();
    }
    
    public void reset() {
        this.antBox = new AntBox();
        this.antBox.setAlternativeClassLoader(getClass().getClassLoader());
        this.buildxml = new File("");
        this.projectPath = "";
        this.propertyChangeListeners = new LinkedList();
        this.mppHome = new File("");
        File polishHomeFile;
        File mppHomeFile;
        try {
            polishHomeFile  = new File(Platform.asLocalURL(Platform.find(CorePlugin.getDefault().getBundle(),new Path("/j2mepolish124"))).toURI());
            mppHomeFile  = new File(Platform.asLocalURL(Platform.find(CorePlugin.getDefault().getBundle(),new Path("/mpp-sdk"))).toURI());
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("No embedded j2me polish found.");
        } catch (IOException exception) {
            throw new IllegalStateException("IO excpetion:"+exception);
        }
        setPropertyValue(MeposeModel.ID_POLISH_HOME,polishHomeFile);
        setPropertyValue(MeposeModel.ID_MPP_HOME,mppHomeFile);
    }
    
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
            setPropertyStatus(ID_BUILDXML,STATUS_BUILDXML_MISSING);
            return;
        }
        extractTaskFromBuildXML();
    }

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
        this.configuredDevices = this.polishTask.getDevices();
    }

    /*
     * @see de.enough.polish.plugin.eclipse.core.PolishProject#setEnvironmentToDevice(de.enough.polish.Device)
     */
    public void setEnvironmentToDevice(Device device) {
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

    public Device[] getConfiguredDevices() {
        return this.configuredDevices;
    }

    public void setConfiguredDevices(Device[] configuredDevices) {
        if(configuredDevices == null){
            throw new IllegalArgumentException("ERROR:MeposeProject.setConfiguredDevices(...):Parameter 'configuredDevices' is null.");
        }
        this.configuredDevices = configuredDevices;
    }

    public File getBuildxml() {
        return this.buildxml;
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

    public void setPolishHome(File file) {
        // TODO rickyn implement setPolishHome
        
    }

    public File getPolishHome() {
        return this.polishHome;
    }

    public void setWTKHome(File file) {
        // TODO rickyn implement setWTKHome
        
    }
    
    public File getWTKHome() {
        return this.wtkHome;
    }

    public void setConfiguredDevicesAsString(String configuredDevices2) {
        // TODO rickyn implement setConfiguredDevicesAsString
        
    }

    public void setPlatformConfiguredAsString(String configuredPlatforms) {
        // TODO rickyn implement setPlatformConfiguredAsString
        
    }

    public DeviceDatabase getDeviceDatabase() {
        if(this.deviceDatabase != null) {
            return this.deviceDatabase;
        }
        File polishHome = (File)getPropertyValue(ID_POLISH_HOME);
        File projectHome = (File)getPropertyValue(ID_PROJECT_HOME);
        File wtkHome = (File)getPropertyValue(ID_WTK_HOME);
        // What to do here?
        if(polishHome == null||projectHome == null) {
            if(logger.isDebugEnabled()) {
                logger.debug("No polishHome or projectHome");
            }
            return null;
        }
        HashMap properties = new HashMap();
        properties.put("wtk.home",wtkHome.getAbsolutePath());
        try {
            this.deviceDatabase = new DeviceDatabase(properties,polishHome,projectHome,null,null,new HashMap(),new HashMap());
        } catch (Exception e) {
            System.out.println("ERROR:MeposeModel.getDeviceDatabase(...):"+e);
            e.printStackTrace();
        }
        return this.deviceDatabase;
    }

    public DeviceTree getDeviceTree() {
        
        if(this.deviceTree == null && this.deviceDatabase != null) {
            this.deviceTree = new DeviceTree(this.deviceDatabase,null,null);
        }
      
        return this.deviceTree;
        
    }

    public Device[] getSupportedDevices() {
        return this.deviceTree.getSelectedDevices();
    }
    
    /**
     * @param projectDescription
     */
    public void setProjectDescription(String projectDescription) {
        assert projectDescription != null : "In method 'MeposeModel.setProjectDescription(...)' parameter 'projectDescription' is null contrary to API.";
        
        this.projectDescription = projectDescription;
    }

    public String getProjectDescription() {
        return this.projectDescription;
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
    
    
    
}
