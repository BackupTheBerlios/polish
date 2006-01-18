/*
 * Created on 23-May-2005 at 10:34:03.
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
package de.enough.polish.devices;

import org.jdom.Element;

import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.CastUtil;

/**
 * <p>Configures the behavior of &lt;capability&gt; elements.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        23-May-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class Capability {

	private final String identifier;
	private final boolean appendExtensions;
	private final String group;
	private final boolean required;
	private final String type;
	private final String implicitGroup;
	private final String description;

	/**
	 * Creates a new capability
	 * 
	 * @param definition the XML definition
	 * @param manager the parent manager
	 * @throws InvalidComponentException when the XML definition has syntax errors 
	 */
	public Capability(Element definition, CapabilityManager manager) 
	throws InvalidComponentException 
	{
		super();
		this.identifier = definition.getChildTextTrim("identifier");
		if (this.identifier == null) {
			System.out.println("definition=" + definition.toString() );
			throw new InvalidComponentException("Each defined capability need the <identifier> element in capabilities.xml");
		}
		this.appendExtensions = "append".equals( definition.getChildTextTrim("extension-mode") );
		this.group = definition.getChildTextTrim("group");
		this.required = CastUtil.getBoolean( definition.getChildTextTrim("required") );
		this.type = definition.getChildTextTrim("type");
		this.implicitGroup = definition.getChildTextTrim("implicit-group");
		this.description = definition.getChildTextTrim("description");
	}

	
	/**
	 * @return Returns true when extensions of this capability should be appended (instead overwriting the old values).
	 */
	public boolean appendExtensions() {
		return this.appendExtensions;
	}
	/**
	 * @return Returns the group.
	 */
	public String getGroup() {
		return this.group;
	}
	/**
	 * @return Returns the identifier.
	 */
	public String getIdentifier() {
		return this.identifier;
	}
	/**
	 * @return Returns the implicitGroup.
	 */
	public String getImplicitGroup() {
		return this.implicitGroup;
	}
	/**
	 * @return Returns the required.
	 */
	public boolean isRequired() {
		return this.required;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return this.type;
	}
	
	public String getDescription() {
		return this.description;
	}

}
