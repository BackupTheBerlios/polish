/*
 * Created on 11-Sep-2004 at 10:32:23.
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
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.resources;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import de.enough.polish.ant.ConditionalElement;
import de.enough.polish.preprocess.BooleanEvaluator;

/**
 * <p>A fileset which can have additional conditions (if and unless attributes).</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        11-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class ResourcesFileSet extends FileSet {
	
	private ConditionalElement condition = new ConditionalElement(); 

	/**
	 * Creates a new empty file set.
	 */
	public ResourcesFileSet() {
		super();
	}

	/**
	 * Creates a new file set which inherits all settings from the given parent.
	 * 
	 * @param parent the parent file set.
	 */
	public ResourcesFileSet(FileSet parent) {
		super(parent);
	}
	
	public void setIf( String ifCondition ) {
		this.condition.setIf(ifCondition);
	}
	
	public void setUnless( String unlessCondition ) {
		this.condition.setUnless(unlessCondition);
	}
	
	/**
	 * Checks if the conditions for this element are met.
	 * 
	 * @param evaluator the boolean evaluator with the settings for the current device
	 * @param parentProject the Ant project into which this variable is embedded
	 * @return true when no condition has been specified 
	 * 			or the specified conditions have been met.
	 */
	public boolean isActive(BooleanEvaluator evaluator, Project parentProject) {
		return this.condition.isActive(evaluator, parentProject);
	}

}
