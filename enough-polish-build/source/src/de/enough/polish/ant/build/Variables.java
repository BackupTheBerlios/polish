/*
 * Created on 23-Jan-2004 at 23:14:37.
 *
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
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
package de.enough.polish.ant.build;


import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Environment;
import de.enough.polish.Variable;

/**
 * <p>Manages a list of variables.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        23-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Variables {
	
	protected ArrayList variablesList;
	protected ArrayList unconditionalVariablesList;
	private boolean includeAntProperties = true;
	private boolean replacePropertiesWithoutDirective;

	/**
	 * Creates a new list of variables.
	 */
	public Variables() {
		this.variablesList = new ArrayList();
		this.unconditionalVariablesList = new ArrayList();
	}
	
	public void addConfiguredVariable( Variable var ) {
		if (!var.containsMultipleVariables()) {
			if (var.getName() == null) {
				throw new BuildException("Please check your variable definition, each variable needs to have the attribute [name]");
			}
			if (var.getValue() == null) {
				throw new BuildException("Please check your variable definition, the variable [" + var.getName() + "] does lack the required [value] attribute");
			}
			if (var.getIfCondition() == null && var.getUnlessCondition() == null ) {
				this.unconditionalVariablesList.add( var );
			}
		/*
		} else {			
			Variable[] vars = var.loadVariables();
			for (int i = 0; i < vars.length; i++) {
				Variable variable = vars[i];
				variable.setIf( var.getIfCondition() );
				variable.setUnless( var.getUnlessCondition() );
				this.variablesList.add( variable );
			}
			*/
		}
		this.variablesList.add( var );
	}
	/*
	public Variable[] getVariables( Map environment ) {
		ArrayList list = new ArrayList();
		Variable[] variables = (Variable[]) this.variablesList.toArray( new Variable[ this.variablesList.size() ] );
		for (int i = 0; i < variables.length; i++) {
			Variable variable = variables[i];
			if (variable.containsMultipleVariables()) {
				Variable[] vars = variable.loadVariables( environment );
				for (int j = 0; j < vars.length; j++) {
					Variable var = vars[j];
					var.setIf( variable.getIfCondition() );
					var.setUnless( variable.getUnlessCondition() );
					list.add( var );
				}
			} else {
				list.add( variable );
			}
		}
		return (Variable[]) list.toArray( new Variable[ list.size() ] );
	}
	*/

	/**
	 * @return Returns the includeAntProperties.
	 */
	public boolean includeAntProperties() {
		return this.includeAntProperties;
	}
	/**
	 * @param includeAntProperties The includeAntProperties to set.
	 */
	public void setIncludeAntProperties(boolean includeAntProperties) {
		this.includeAntProperties = includeAntProperties;
	}
	
	public void setReplacePropertiesWithoutDirective( boolean replacePropertiesWithoutDirective ) {
		this.replacePropertiesWithoutDirective = replacePropertiesWithoutDirective;
	}
	
	public boolean replacePropertiesWithoutDirective() {
		return this.replacePropertiesWithoutDirective;	
	}
	
	public Variable[] getVariables( Project antProject, BooleanEvaluator evaluator, Environment environment ) {
		ArrayList list = new ArrayList();
		Variable[] variables = getVariables( this.variablesList );
		for (int i = 0; i < variables.length; i++) {
			Variable variable = variables[i];
			if (variable.isConditionFulfilled(evaluator, antProject)) {
				if (variable.containsMultipleVariables()) {
					Variable[] vars = variable.loadVariables( environment, antProject );
					for (int j = 0; j < vars.length; j++) {
						Variable var = vars[j];
						var.setIf( variable.getIfCondition() );
						var.setUnless( variable.getUnlessCondition() );
						list.add( var );
					}
				} else {
					list.add( variable );
				}
			}
		}
		return getVariables( list );
	}
	
	public Variable[] getAllVariables( Environment environment ) {
		Project antProject = environment.getProject(); 
		ArrayList list = new ArrayList();
		Variable[] variables = getVariables( this.variablesList );
		for (int i = 0; i < variables.length; i++) {
			Variable variable = variables[i];
			if (variable.containsMultipleVariables()) {
				Variable[] vars = variable.loadVariables( environment, antProject );
				for (int j = 0; j < vars.length; j++) {
					Variable var = vars[j];
					var.setIf( variable.getIfCondition() );
					var.setUnless( variable.getUnlessCondition() );
					list.add( var );
				}
			} else {
				list.add( variable );
			}
		}
		return getVariables( list );
	}


	/**
	 * @return
	 */
	protected Variable[] getVariables( List list ) {
		return (Variable[]) list.toArray( new Variable[ list.size() ] );	
	}

	/**
	 * @return
	 */
	public Variable[] getUnconditionalVariables() {
		return (Variable[]) this.unconditionalVariablesList.toArray( new Variable[ this.unconditionalVariablesList.size() ] );
	}

	/**
	 * @return
	 */
	public Variable[] getVariables() {
		return getVariables( this.variablesList );
	}
}
