/*
 * Created on 21-Jun-2004 at 13:05:20.
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
package de.enough.polish.preprocess.lineprocessors;

import de.enough.polish.Device;
import de.enough.polish.preprocess.LineProcessor;
import de.enough.polish.util.StringList;

/**
 * <p>Makes some standard preprocessing like the determination whether the Ticker-class is used etc.</p>
 *
 * <p>copyright Enough Software 2004</p>
 * <pre>
 * history
 *        21-Jun-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishLineProcessor extends LineProcessor {
	
	private boolean isTickerUsed;

	/**
	 * Creates a new uninitialised PolishLineProcessor 
	 */
	public PolishLineProcessor() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#notifyDevice(de.enough.polish.Device, boolean)
	 */
	public void notifyDevice(Device device, boolean usesPolishGui) {
		super.notifyDevice(device, usesPolishGui);
		this.isTickerUsed = false;
	}
	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#notifyPolishPackageStart()
	 */
	public void notifyPolishPackageStart() {
		super.notifyPolishPackageStart();
		if (this.isTickerUsed) {
			this.preprocessor.removeSymbol("polish.skipTicker");
		} else {
			this.preprocessor.addSymbol("polish.skipTicker");
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.preprocess.LineProcessor#processClass(de.enough.polish.util.StringList, java.lang.String)
	 */
	public void processClass(StringList lines, String className) {
		while (lines.next() ) {
			String line = lines.getCurrent();
			int startPos = line.indexOf(".getTicker"); 
			if ( startPos != -1) {
				line = line.substring(0, startPos)
					+ ".getPolishTicker"
					+ line.substring( startPos + 10 );
				this.isTickerUsed = true;
				lines.setCurrent( line );
			}
			startPos = line.indexOf(".setTicker");
			if ( startPos != -1) {
				line = line.substring(0, startPos)
					+ ".setPolishTicker"
					+ line.substring( startPos + 10 );
				this.isTickerUsed = true;
				lines.setCurrent( line );
			}
		}
	}
}
