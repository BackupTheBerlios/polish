/*
 * Created on 09-Feb-2004 at 13:40:12.
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
package de.enough.polish.ant.requirements;

import java.util.HashMap;

import de.enough.polish.Device;
import de.enough.polish.preprocess.BooleanEvaluator;

/**
 * <p>Selects a device that fullfill a preprocessing term.</p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        09-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class TermRequirement extends Requirement {
	
	private final String term;
	private final BooleanEvaluator booleanEvaluator;

	/**
	 * Creates a new requirement for a device feature.
	 * 
	 * @param term the preprocessing term, e.g. "polish.supportsPolishGui && !polish.isVirtual" 
	 */
	public TermRequirement(String term ) {
		super(term, "Term");
		this.term = term;
		this.booleanEvaluator = new BooleanEvaluator( new HashMap(), new HashMap() );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		// this is not needed, since we overried isMet(Device ) already.
		return false;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.Device)
	 */
	public boolean isMet(Device device) {
		this.booleanEvaluator.setEnvironment( device.getFeatures(), device.getCapabilities() );
		return this.booleanEvaluator.evaluate( this.term, "build.xml", 0 );
	}

}
