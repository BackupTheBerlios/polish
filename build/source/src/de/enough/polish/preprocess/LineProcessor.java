/*
 * Created on 19-Jun-2004 at 20:23:00.
 * 
 * Copyright (c) 2004 Robert Virkus / Enough Software
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
package de.enough.polish.preprocess;

import de.enough.polish.Device;
import de.enough.polish.util.StringList;

/**
 * <p>Processes single lines of source-code.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        19-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public abstract class LineProcessor {

	protected Preprocessor preprocessor;
	protected BooleanEvaluator booleanEvaluator;
	protected boolean isUsingPolishGui;
	protected boolean isInJ2MEPolishPackage;
	protected Device currentDevice;
	protected StyleSheet currentStyleSheet;

	/**
	 * Creates a new line-processor.
	 * The actual initialisation work is done in the init()-method.
	 * 
	 * @see #init(Preprocessor)
	 */
	public LineProcessor() {
		// no initialisation work done
	}
	
	public void init( Preprocessor processor ) {
		this.preprocessor = processor;
		this.booleanEvaluator = processor.getBooleanEvaluator();
	}
	
	/**
	 * Notifies the processor that from now on source code from the J2ME Polish package is processed.
	 * This will last until the notifyDevice(...)-method is called.
	 */
	public void notifyPolishPackageStart() {
		this.isInJ2MEPolishPackage = true;
	}
		
	/**
	 * Notifies this processor about a new device for which code is preprocessed.
	 * The default implementation set the currentDevice, currentStyleSheet
	 * and isUsingPolishGui and resets the isInJ2MEPolishPackage instance variables.
	 *  
	 * @param device the new device
	 * @param usesPolishGui true when the J2ME Polish GUI is used for the new device
	 */
	public void notifyDevice( Device device, boolean usesPolishGui ) {
		this.currentDevice = device;
		this.isUsingPolishGui = usesPolishGui;
		this.currentStyleSheet = this.preprocessor.getStyleSheet();
		this.isInJ2MEPolishPackage = false;
	}
	
	/**
	 * Processes the given class.
	 * 
	 * @param lines the source code of the class
	 * @param className the name of the class
	 */
	public abstract void processClass( StringList lines, String className );

}
