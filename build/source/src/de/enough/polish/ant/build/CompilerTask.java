/*
 * Created on 25-Feb-2005 at 15:29:28.
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ant.build;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import de.enough.polish.ant.ConditionalElement;
import de.enough.polish.preprocess.BooleanEvaluator;

/**
 * <p>Allows the setting of any <javac>-settings.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        25-Feb-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CompilerTask extends Javac {
	
	private final ConditionalElement condition;
	private boolean bootClassPathSet;
	private boolean classPathSet;
	private boolean debugLevelSet;
	private boolean destDirSet;
	private boolean sourceSet;
	private boolean debugSet;
	private boolean sourceDirSet;
	private boolean targetSet;
	private boolean taskNameSet;

	/**
	 * Creates a new Compiler task
	 */
	public CompilerTask() {
		super();
		this.condition = new ConditionalElement();
	}
	
	/**
	 * Sets the ant-property which needs to be defined to allow the execution of this task.
	 *  
	 * @param ifExpr the ant-property which needs to be defined 
	 */
	public void setIf(String ifExpr) {
		this.condition.setIf( ifExpr );
	}
	
	/**
	 * Sets the ant-property which must not be defined to allow the execution of this task.
	 * 
	 * @param unlessExpr the ant-property which must not be defined 
	 */
	public void setUnless(String unlessExpr) {
		this.condition.setUnless(unlessExpr);
	}

	/**
	 * Checks if this element should be used.
	 * 
	 * @param currentProject The project to which this nested element belongs to.
	 * @return true when this element is valid
	 */
	public boolean isActive( Project currentProject ) {
		return this.condition.isActive( currentProject );
	}

	/**
	 * Checks if the conditions for this element are met.
	 * 
	 * @param evaluator the boolean evaluator with the settings for the current device
	 * @param currentProject the Ant project into which this variable is embedded
	 * @return true when no condition has been specified 
	 * 			or the specified conditions have been met.
	 */
	public boolean isActive(BooleanEvaluator evaluator, Project currentProject) {
		return this.condition.isActive( evaluator, currentProject );
	}

	public void setBootclasspath(Path path) {
		this.bootClassPathSet = true;
		super.setBootclasspath(path);
	}
	public void setDirectBootclasspath(Path path) {
		super.setBootclasspath(path);
	}
	public void setBootClasspathRef(Reference reference) {
		this.bootClassPathSet = true;
		super.setBootClasspathRef(reference);
	}
	public void setDirectBootClasspathRef(Reference reference) {
		super.setBootClasspathRef(reference);
	}
	public void setClasspath(Path path) {
		this.classPathSet = true;
		super.setClasspath(path);
	}
	public void setDirectClasspath(Path path) {
		super.setClasspath(path);
	}
	public void setClasspathRef(Reference reference) {
		this.classPathSet = true;
		super.setClasspathRef(reference);
	}
	public void setDirectClasspathRef(Reference reference) {
		super.setClasspathRef(reference);
	}
	public void setDebug(boolean enable) {
		this.debugSet = true;
		super.setDebug(enable);
	}
	public void setDirectDebug(boolean enable) {
		super.setDebug(enable);
	}
	public void setDebugLevel(String level) {
		this.debugLevelSet = true;
		super.setDebugLevel(level);
	}
	public void setDirectDebugLevel(String level) {
		super.setDebugLevel(level);
	}
	public void setDestdir(File file) {
		this.destDirSet = true;
		super.setDestdir(file);
	}
	public void setDirectDestdir(File file) {
		super.setDestdir(file);
	}
	public void setSource(String source) {
		this.sourceSet = true;
		super.setSource(source);
	}
	public void setDirectSource(String source) {
		super.setSource(source);
	}
	public void setSrcdir(Path path) {
		this.sourceDirSet = true;
		super.setSrcdir(path);
	}
	public void setDirectSrcdir(Path path) {
		super.setSrcdir(path);
	}
	public void setTarget(String target) {
		this.targetSet = true;
		super.setTarget(target);
	}
	public void setDirectTarget(String target) {
		super.setTarget(target);
	}
	public void setTaskName( String name ) {
		this.taskNameSet = true;
		super.setTaskName(name);
	}
	public void setDirectTaskName( String name ) {
		super.setTaskName(name);
	}
	
	
	public boolean isBootClassPathSet() {
		return this.bootClassPathSet;
	}
	public boolean isClassPathSet() {
		return this.classPathSet;
	}
	public ConditionalElement getCondition() {
		return this.condition;
	}
	public boolean isDebugLevelSet() {
		return this.debugLevelSet;
	}
	public boolean isDebugSet() {
		return this.debugSet;
	}
	public boolean isDestDirSet() {
		return this.destDirSet;
	}
	public boolean isSourceDirSet() {
		return this.sourceDirSet;
	}
	public boolean isSourceSet() {
		return this.sourceSet;
	}
	public boolean isTargetSet() {
		return this.targetSet;
	}
	public boolean isTaskNameSet() {
		return this.taskNameSet;
	}
	
	
	public void execute() throws BuildException {
        super.execute();
	}
}
