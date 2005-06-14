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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
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
    
    // TODO:Other stuff like infosection, requirements, obfuscators,...

    private AntBox antBox;
    private PolishTask polishTask;
    private File buildxml;
    
    public MeposeProject() {
        this.antBox = new AntBox();
        this.antBox.setAlternativeClassLoader(getClass().getClassLoader());
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
        
        this.antBox.setBuildxml(buildxml);
        this.antBox.createProject();
        
        // Configure all targets.
        Project project = this.antBox.getProject();
        Map targetNameToTargetObjectMapping = project.getTargets();
        Collection targetObjectSet = targetNameToTargetObjectMapping.values();
        boolean foundPolishTask = false;
        this.polishTask = null;
//        System.out.print("prefixMeposeProject.setBuildxml(...):all targets:");
//        for (Iterator iterator = targetObjectSet.iterator(); iterator.hasNext(); ) {
//            Target element = (Target) iterator.next();
//            System.out.print(element+" ");
//        }
//        System.out.println();
        for (Iterator iterator = targetObjectSet.iterator(); iterator.hasNext(); ) {
            Target target = (Target) iterator.next();
            try {
                this.antBox.configureTarget(target);
            }
            catch(BuildException e) {
                // configuring the target failed, maybe because the taskdef could not be resolved.
                //System.out.println("Error:MeposeProject.setBuildxml():Could not configure target:"+target.getName());
                CorePlugin.log("Error:MeposeProject.setBuildxml():Could not configure target:"+target.getName());
                continue;
            }
            Task[] tasks = target.getTasks();
            Task task;
            
            for (int taskIndex = 0; taskIndex < tasks.length; taskIndex++) {
                task = tasks[taskIndex];
//                if(task.getClass().getName().equals("de.enough.polish.ant.PolishTask")) {
//                    System.out.println("DEBUG:MeposeProject.setBuildxml(...):PolishTask found.");
//                    System.out.println("taskloader:"+task.getClass().getClassLoader());
//                    System.out.println("polishloader:"+PolishTask.class.getClassLoader());
//                }
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
        //Environment oldEnvironment = this.environment;
        this.polishTask.initProject();
        this.environment = this.polishTask.getEnvironment();
        //TODO: Fire a propertyChangeEvent to inform others that the object reference
        // has changed. ContentProcessors are typical clients to this mechanism.
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
        //TODO: Is null as Locale alright?
        // This does not delete or create the environment object but modifies it.
        this.polishTask.initialize(device,null);
        this.environment = this.polishTask.getEnvironment();
    }
    /*
    // TODO: Put this stuff into the core plugin.
    public static MeposeProject getTestProject() {
        String oldUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir","/Users/ricky/workspace/enough-polish-demo");
        MeposeProject meposeProject = new MeposeProject();
        File buildxml = new File("build.xml");
        
        meposeProject.setBuildxml(buildxml);
        AntBox antBox = meposeProject.getAntBox();
        antBox.createProject(MeposeProject.class.getClassLoader());
        Project project = antBox.getProject();
        Target polishTarget = (Target)project.getTargets().get("j2mepolish");
        antBox.configureTarget(polishTarget);
        Task[] tasks = polishTarget.getTasks();
        Task t = tasks[0];
        PolishTask task;
//        try {
            //t.getClass().equals(MeposeProject.class.getClassLoader().loadClass("de.enough.polish.ant.PolishTask")
            if(t instanceof PolishTask) {
                task = (PolishTask)t;
            }
            else {
                throw new RuntimeException("MeposeProject.getTestProject():task is not a PolishTask.");
            }
//        } catch (ClassNotFoundException exception) {
//            throw new RuntimeException("eposeProject.getTestProject():task is not a PolishTask.");
//        }
        
        meposeProject.setEnvironment(task.getEnvironment());
        System.setProperty("user.dir",oldUserDir);
        return meposeProject;
    }
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
    
    
}
