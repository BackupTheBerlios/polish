/*
 * Created on 02-Nov-2005 at 02:19:42.
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
package de.enough.polish.preprocess.css;

import org.apache.tools.ant.BuildException;
import org.jdom.Element;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.util.StringUtil;

/**
 * <p>Maps a short CSS name to any string.</p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        02-Nov-2005 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssMapping {
	
	private String from;
	private String to;
	private String condition;
	private String converter;
	private String[] excludes;
	


	/**
	 * Creates a new mapping.
	 * 
	 * @param definition the definition 
	 */
	public CssMapping( Element definition ) {
		super();
		this.from = definition.getAttributeValue("from");
		if (this.from == null) {
			throw new BuildException("Invalid mapping without \"from\" attribute - check your css-attributes.xml file(s).");
		}
		this.to = definition.getAttributeValue("to");
		if (this.to == null) {
			throw new BuildException("Invalid mapping without \"to\" attribute - check your css-attributes.xml file(s) of the mapping from [" + this.from + "].");
		}
		this.condition = definition.getAttributeValue("condition");
		if (this.condition == null) {
			this.condition = definition.getChildTextTrim("condition");
		}
		this.converter = definition.getAttributeValue("converter");
		String excludesStr = definition.getAttributeValue("excludes");
		if (excludesStr != null) {
			this.excludes = StringUtil.splitAndTrim( excludesStr, ',');
		}
	}

	/**
	 * @return Returns the condition.
	 */
	public String getCondition() {
		return this.condition;
	}

	/**
	 * @return Returns the from.
	 */
	public String getFrom() {
		return this.from;
	}

	/**
	 * @return Returns the to.
	 */
	public String getTo() {
		return this.to;
	}
	

	/**
	 * @return Returns the converter.
	 */
	public String getConverter() {
		return this.converter;
	}

	/**
	 * @return Returns the excludes.
	 */
	public String[] getExcludes() {
		return this.excludes;
	}

	/**
	 * Checks whether this attribute mapping has a condion or an exclusion.
	 * When the condion is not met or there are conflicting values a BuildException will be thrown.
	 * 
	 * @param attributeName the name of the attribute
	 * @param attributeValue the value of the attribute 
	 * @param evaluator the boolean evaluator
	 * @throws BuildException when the condion is not met or when there are conflicting values.
	 */
	public void checkCondition( String attributeName, String attributeValue, BooleanEvaluator evaluator ) {
		if (this.condition != null) {
			if (!evaluator.evaluate(this.condition, "polish.css", 0)) {
				throw new BuildException( "Invalid CSS: the value \"" + this.from + "\" of attribute \"" + attributeName + "\" is invalid for the current device, the condition \"" + this.condition + "\" is not met. Use this value in appropriate subfolders like \"resources/midp2/polish.css\"."  );
			}
		}
		if (this.excludes != null) {
			for (int i = 0; i < this.excludes.length; i++) {
				String exclude = this.excludes[i];
				if ( attributeValue.indexOf( exclude ) != -1) {
					throw new BuildException( "Invalid CSS: the value \"" + this.from + "\" of the attribute \"" + attributeName + "\" conflicts with the value \"" + exclude + "\" - please change your polish.css setting for the value \"" + attributeValue + "\"."  );					
				}
			}
		}
	}

}
