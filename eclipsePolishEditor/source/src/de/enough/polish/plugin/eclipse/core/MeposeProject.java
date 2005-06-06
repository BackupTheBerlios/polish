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
package de.enough.polish.plugin.eclipse.core;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.PolishTask;
import de.enough.polish.plugin.eclipse.utils.AntBox;

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
    
    //private File polishProject;
    
    //private List configuredDevices;
    private Environment environment;
    // Other stuff like infosection, requirements, obfuscators,...

    private PolishTask polishTask;
    private AntBox antBox;

    private List objectChangeListeners;
    
    public MeposeProject() {
        this.antBox = new AntBox();
    }
    
    public boolean setBuildxml(File buildxml) {
        if(buildxml == null){
            throw new IllegalArgumentException("ERROR:MeposeProject.setBuildxml(...):Parameter 'buildxml' is null.");
        }
        this.antBox.setBuildxml(buildxml);
        this.antBox.createProject(this.getClass().getClassLoader());
        Project project = this.antBox.getProject();
        Map targetNameToTargetObjectMapping = project.getTargets();
        Collection targetObjectSet = targetNameToTargetObjectMapping.values();
        boolean foundPolishTask = false;
        for (Iterator iterator = targetObjectSet.iterator(); iterator.hasNext(); ) {
            Target target = (Target) iterator.next();
            this.antBox.configureTarget(target);
            Task[] tasks = target.getTasks();
            Task task;
            for (int taskIndex = 0; taskIndex < tasks.length; taskIndex++) {
                task = tasks[taskIndex];
                //if(task instanceof PolishTask) {
//                try {
                    //task.getClass().equals(getClass().getClassLoader().loadClass("de.enough.polish.ant.PolishTask")
                    if(task instanceof PolishTask) {
                        //this.targetContainingPolishTask = target;
                        this.polishTask = (PolishTask)task;
                        foundPolishTask = true;
                        break;
                    }
//                } catch (ClassNotFoundException exception) {
//                    foundPolishTask = false;
//                }
            }
            if(foundPolishTask) {
                break;
            }
        }
        //TODO: Think about alternative error reporting facility instead of boolean return value.
        if(! foundPolishTask) {
            return false;
        }
        Environment oldEnvironment = this.environment;
        this.polishTask.initProject();
        this.environment = this.polishTask.getEnvironment();
        fireObjectChangedEvent(oldEnvironment,this.environment);
        this.polishTask.selectDevices();
        return true;
    }

    /*
     * @see de.enough.polish.plugin.eclipse.core.PolishProject#setEnvironmentToDevice(de.enough.polish.Device)
     */
    public void setEnvironmentToDevice(Device device) {
        if(device == null){
            throw new IllegalArgumentException("ERROR:AntPolishProject.setEnvironmentToDevice(...):Parameter 'device' is null.");
        }
        //TODO: Is null as Locale alright?
        // This does not delete or create the environment object but modifies it.
        this.polishTask.initialize(device,null);
    }
    
    public void fireObjectChangedEvent(Object oldObject,Object newObject) {
        if(oldObject == null && newObject == null){
            throw new IllegalArgumentException("ERROR:MeposeProject.fireObjectChangedEvent(...):Both parameter are null.");
        }
        ObjectChangeEvent objectChangeEvent = new ObjectChangeEvent(this,oldObject,newObject);
        IObjectChangeListener objectChangeListener;
        WeakReference weakReference;
        for (Iterator iterator = this.objectChangeListeners.iterator(); iterator.hasNext(); ) {
            weakReference = (WeakReference)iterator.next();
            objectChangeListener = (IObjectChangeListener)weakReference.get();
            // When the editor was closed, all listeners were deleted. So we have to remove them from the listers list.
            if(objectChangeListener == null) {
                this.objectChangeListeners.remove(weakReference);
            }
            else {
                objectChangeListener.handleObjectChangedEvent(objectChangeEvent);
            }
        }
    }
    
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
    
    public void addObjectChangedListener(IObjectChangeListener objectChangeListener) {
        if(objectChangeListener == null){
            throw new IllegalArgumentException("ERROR:MeposeProject.addObjectChangedListener(...):Parameter 'objectChangeListener' is null.");
        }
        WeakReference weakReference = new WeakReference(objectChangeListener);
        this.objectChangeListeners.add(weakReference);
    }

    public AntBox getAntBox() {
        return this.antBox;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setAntBox(AntBox antBox) {
        this.antBox = antBox;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    
    
    
    // Triggered events:
    //   environment created(Environment newEnvironment)
    //   environment deleted(Environment deletedEnvironment)
    //   environment modified(Environment modifiedEnvironment)
}
