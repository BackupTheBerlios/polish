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
package de.enough.utils;

import java.io.File;
import java.io.PrintStream;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.types.Path;

/**
 * 1. Set the build.xml file and a working directory by setBuildxml()/setWorkingDirectory().
 * 2. Call createProject().
 * 3. Configure a target.
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
    private ClassLoader alternativeClassLoader = null;
    private String workingDirectory = "";
    private String previousWorkingDirectory = "";
    private File toolsLocation;
    

    // TODO: Add working directory to argument list as it is a prerequisit.
    public AntBox(File buildxml) {
        if(buildxml == null){
            throw new IllegalArgumentException("ERROR:AntBox.AntBox(...):Parameter 'buildxml' is null.");
        }
        reset();
        this.buildxml = buildxml;
    }
    
    public AntBox() {
        reset();
    }
    
    public void reset() {
        this.messageOutputLevel = Project.MSG_INFO;
        this.outputStream = System.out;
        this.errorStream = System.err;
        this.alternativeClassLoader = null;
        this.workingDirectory = "";
        this.previousWorkingDirectory = "";
        this.buildxml = new File("");
        this.projectHelper = new ProjectHelper2();
        this.project = new Project();
    }
    
    /**
     * TODO: put the prerequisits in as parameters to reduce inter method
     * dependencies. (order of setBuildxml,setWorkingDirectory and createProject)
     * Creates a project configured with the current build.xml file.
     * @return the new Project instance
     * @throws IllegalStateException if no buildxml file was specified.
     * @throws BuildException 
     */
    public Project createProject() throws BuildException{
        if(this.buildxml == null){
            throw new IllegalStateException("ERROR:AntBox.createProject():Field 'buildxml' is null.");
        }
        // Ant has some stong dependencies on the current working directory.
        // So we set it with a hack (setting global properties) and set it back
        // if we are done.
        setWorkingDirectory();
        this.project = new Project();
//        this.project.setCoreLoader(this.alternativeClassLoader);
        this.project.init();
        this.project.setUserProperty("ant.version", Main.getAntVersion());
        this.project.setUserProperty("ant.file",this.buildxml.getAbsolutePath());
        
        this.projectHelper.parse(this.project,this.buildxml);
        
        // The logger is needed as j2me polish will replace this instance with
        // its own. Without this logger a warning is issued.
        BuildLogger logger = new DefaultLogger();
        logger.setMessageOutputLevel(this.messageOutputLevel);
        logger.setOutputPrintStream(this.outputStream);
        logger.setErrorPrintStream(this.errorStream);
        logger.setEmacsMode(true);
        
        this.project.addBuildListener(logger);
        
        // Restore previous working directory.
        restorePreviousWorkingDirectory();
        return this.project;
    }

    public File getToolsLocation() {
        return this.toolsLocation;
    }
    
    public void setToolsLocation(File toolsLocation) {
        this.toolsLocation = toolsLocation;
    }
    /**
     * This method needs to be called when a target is used in any way. It will replace all UnknownElements tasks
     * with concrete task classes.
     * @param target
     * @throws BuildException
     */
    public void configureTarget(Target target) throws BuildException{
        if(target == null){
            throw new IllegalArgumentException("ERROR:AntBox.configureTarget(...):Parameter 'target' is null.");
        }
        setWorkingDirectory();
//        target.setIf("test");
        Task[] tasks = target.getTasks();
        for (int i = 0; i < tasks.length; i++) {
            tasks[i].maybeConfigure();
        }
        restorePreviousWorkingDirectory();
    }
    
    
    private void restorePreviousWorkingDirectory() {
        System.setProperty("user.dir",this.previousWorkingDirectory);
    }

    private void setWorkingDirectory() {
        this.previousWorkingDirectory = System.getProperty("user.dir");
        System.setProperty("user.dir",this.workingDirectory);
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
     * @return the project, may be uninitialized if createProject was not called.
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
    

    public ProjectHelper getProjectHelper() {
        return this.projectHelper;
    }

    public ClassLoader getAlternativeClassLoader() {
        return this.alternativeClassLoader;
    }
    
//  ##############################################################
    // Setter
    
    public void setProjectHelper(ProjectHelper projectHelper) {
        if(projectHelper == null){
            throw new IllegalArgumentException("ERROR:AntBox.setProjectHelper(...):Parameter 'projectHelper' is null.");
        }
        this.projectHelper = projectHelper;
    }

    public void setAlternativeClassLoader(ClassLoader alternativeClassLoader) {
        // Null as parameter is allowed and means to use the default class loader.
        this.alternativeClassLoader = alternativeClassLoader;
    }

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
    
    public String getWorkingDirectory() {
        return this.workingDirectory;
    }

    public void setWorkingDirectory(File workingDirectory) {
        if(workingDirectory == null){
            throw new IllegalArgumentException("ERROR:AntBox.setUsedWorkingDirectory(...):Parameter 'usedWorkingDirectory' is null.");
        }
        this.workingDirectory = workingDirectory.getAbsolutePath();
    }

    public void run(String[] targetNames) {
        Vector argumentList = new Vector();
        for (int i = 0; i < targetNames.length; i++) {
            argumentList.add(targetNames[i]);
        }
//        this.project.setCoreLoader(this.alternativeClassLoader);
        this.project.setProperty("ant.executor.class","org.apache.tools.ant.helper.SingleCheckExecutor");
        this.project.executeTargets(argumentList);
        // This method does not call the executor.
//        this.project.executeSortedTargets(this.project.topoSort(targetNames, this.project.getTargets(), false));
    }

    public void setProperty(String propertyName, String value) {
        this.project.setProperty(propertyName,value);
    }

    /**
     * @param logger
     */
    public void addLogger(BuildLogger logger) {
        this.project.addBuildListener(logger);
    }
    
    public void removeLoggeR(BuildLogger logger) {
        this.project.removeBuildListener(logger);
    }
}
