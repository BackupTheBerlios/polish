/*
 * Created on May 30, 2005 at 11:36:04 AM.
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
package de.enough.polish.plugin.eclipse.utils;

import java.io.File;
import java.io.PrintStream;

import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.helper.ProjectHelper2;

/**
 * Beware that this class is written for ant-1.6.3. but eclipse uses 1.6.2.
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        May 30, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class AntBox {

    private Project project;
    private File buildxml;
    private ProjectHelper projectHelper;
    
    private int messageOutputLevel;
    private PrintStream outputStream;
    private PrintStream errorStream;
    
    public AntBox(File buildxml) {
        if(buildxml == null){
            throw new IllegalArgumentException("ERROR:AntBox.AntBox(...):Parameter 'buildxml' is null.");
        }
        this.buildxml = buildxml;
        init();
    }
    
    public AntBox() {
        init();
    }
    
    private void init() {
        this.messageOutputLevel = Project.MSG_INFO;
        this.outputStream = System.out;
        this.errorStream = System.err;
    }
    
    /**
     * Creates a project configured with the current build.xml file.
     * 
     */
    public void createProject(ClassLoader eclipseClassLoader) {
        if(this.buildxml == null){
            throw new IllegalArgumentException("ERROR:AntBox.createProject():Field 'buildxml' is null.");
        }
        this.project = new Project();
        //this.project.setCoreLoader(eclipseClassLoader);
        this.project.init();
        this.project.setUserProperty("ant.version", Main.getAntVersion());
        this.project.setUserProperty("ant.file",this.buildxml.getAbsolutePath());
        
        // This property is necessary to give ProjectHelper a concrete class to parse the build.xml file.
        //System.setProperty("HELPER_PROPERTY","org.apache.tools.ant.helper.ProjectHelper2");
        // Method is deprecated.
        //ProjectHelper.configureProject(this.project, this.buildxml);
        this.projectHelper = new ProjectHelper2();
        this.projectHelper.parse(this.project,this.buildxml);
        
        BuildLogger logger = new DefaultLogger();
        logger.setMessageOutputLevel(this.messageOutputLevel);
        logger.setOutputPrintStream(this.outputStream);
        logger.setErrorPrintStream(this.errorStream);
        logger.setEmacsMode(true);
    }

    /**
     * This method needs to be called when a target is used in any way. It will replace all UnknownElements tasks
     * with concrete task classes.
     * @param target
     */
    public void configureTarget(Target target) {
        if(target == null){
            throw new IllegalArgumentException("ERROR:AntBox.configureTarget(...):Parameter 'target' is null.");
        }
        Task[] tasks = target.getTasks();
        for (int i = 0; i < tasks.length; i++) {
            tasks[i].maybeConfigure();
        }
    }
    
    //##############################################################
    // Getter
    
    /**
     * @return Returns the buildxml.
     */
    public File getBuildxml() {
        return this.buildxml;
    }

    /**
     * @return Returns the project.
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * @return Returns the errorStream.
     */
    public PrintStream getErrorStream() {
        return this.errorStream;
    }

    /**
     * @return Returns the messageOutputLevel.
     */
    public int getMessageOutputLevel() {
        return this.messageOutputLevel;
    }

    /**
     * @return Returns the outputStream.
     */
    public PrintStream getOutputStream() {
        return this.outputStream;
    }
    
    
    //##############################################################
    // Setter

    /**
     * @param project The project to set.
     */
    public void setProject(Project project) {
        if(project == null){
            throw new IllegalArgumentException("ERROR:AntBox.setProject(...):Parameter 'project' is null.");
        }
        this.project = project;
    }
    
    /**
     * @param buildxml The buildxml file to set.
     */
    public void setBuildxml(File buildxml) {
        if(buildxml == null){
            throw new IllegalArgumentException("ERROR:AntBox.setBuildxml(File buildxml):Parameter 'buildxml' is null.");
        }
        this.buildxml = buildxml;
    }

    /**
     * @param errorStream The errorStream to set.
     */
    public void setErrorStream(PrintStream errorStream) {
        if(errorStream == null){
            throw new IllegalArgumentException("ERROR:AntBox.setErrorStream(...):Parameter 'errorStream' is null.");
        }
        this.errorStream = errorStream;
    }

    /**
     * @param messageOutputLevel The messageOutputLevel to set.
     */
    public void setMessageOutputLevel(int messageOutputLevel) {
        this.messageOutputLevel = messageOutputLevel;
    }

    /**
     * @param outputStream The outputStream to set.
     */
    public void setOutputStream(PrintStream outputStream) {
        if(outputStream == null){
            throw new IllegalArgumentException("ERROR:AntBox.setOutputStream(...):Parameter 'outputStream' is null.");
        }
        this.outputStream = outputStream;
    }
    
    
}
