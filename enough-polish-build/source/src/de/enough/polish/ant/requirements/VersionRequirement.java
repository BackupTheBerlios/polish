/*
 * Created on 10-Feb-2004 at 22:49:38.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant.requirements;

import de.enough.polish.Device;

/**
 * <p>Selects devices by the version of a specific capability.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        10-Feb-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class VersionRequirement extends Requirement {

	private VersionMatcher matcher;

	/**
	 * Creates a new Version requirement
	 * @param value the value of this requirement, e.g. "2.3+.2+"
	 * @param propertyName the name of the capability
	 */
	public VersionRequirement(String value, String propertyName) {
		super(value, propertyName);
		this.matcher = new VersionMatcher( value );
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ant.requirements.Requirement#isMet(de.enough.polish.Device, java.lang.String)
	 */
	protected boolean isMet(Device device, String property) {
		return this.matcher.matches( property );
	}

}
