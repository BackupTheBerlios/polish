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
package de.enough.mepose.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.PolishTask;
import de.enough.utils.AntBox;

/**
 * This class encapsulates the concepts in a build.xml in an abstract manner.
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        May 31, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class MeposeProject {
    
    // The environment for a specific device. May be null when no device is choosen.
    private Environment environment;
    private Device[] configuredDevices;
    private String projectPath;

    private AntBox antBox;
    private PolishTask polishTask;
    private File buildxml;
    
    // TODO: Make a map and register listeners with properties directly.
    private List propertyChangeListeners;
    
    public MeposeProject() {
        reset();
    }
    
    public void reset() {
        this.antBox = new AntBox();
        this.antBox.setAlternativeClassLoader(getClass().getClassLoader());
        this.buildxml = new File("");
        this.projectPath = "";
        this.propertyChangeListeners = new LinkedList();
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
        this.antBox.setWorkingDirectory(this.projectPath);
        this.antBox.setBuildxml(buildxml);
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
                        break;
                    }
            }
            if(foundPolishTask) {
                break;
            }
        }
        //TODO: Think about alternative error reporting facility instead of boolean return value.
        if(! foundPolishTask) {
            throw new BuildException("No target with a 'PolishTask' was found.");
        }
        Environment oldEnvironment = this.environment;
        this.polishTask.initProject();
        this.environment = this.polishTask.getEnvironment();
        //TODO: Fire a propertyChangeEvent to inform others that the object reference
        // has changed. ContentProcessors are typical clients to this mechanism.
        firePropertyChangeEvent("environment",oldEnvironment,this.environment);
        this.polishTask.selectDevices();
        this.configuredDevices = this.polishTask.getDevices();
        this.buildxml = buildxml;
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
}
