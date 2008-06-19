/*
 * Created on Jun 14, 2008 at 12:10:10 PM.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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

import java.util.HashMap;
import java.util.Map;

import de.enough.polish.BuildException;

/**
 * <p>Stores animation CSS attributes</p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class CssAnimationSetting
{
	
	private static final Map TIMING_FUNCTION_BY_NAME;
	static {
		TIMING_FUNCTION_BY_NAME = new HashMap();
		TIMING_FUNCTION_BY_NAME.put("ease", "CssAnimation.FUNCTION_EASE");
		TIMING_FUNCTION_BY_NAME.put("ease-out", "CssAnimation.FUNCTION_EASE_OUT");
		TIMING_FUNCTION_BY_NAME.put("ease-in", "CssAnimation.FUNCTION_EASE_IN");
		TIMING_FUNCTION_BY_NAME.put("ease-in-out", "CssAnimation.FUNCTION_EASE");
		TIMING_FUNCTION_BY_NAME.put("ease-out-in", "CssAnimation.FUNCTION_EASE");
		TIMING_FUNCTION_BY_NAME.put("linear", "CssAnimation.FUNCTION_LINEAR");
	}
	
	private final String cssAttributeName;
	private final Map animationAttributesByName;
	
	public CssAnimationSetting( String cssAttributeName ) {
		this.cssAttributeName = cssAttributeName;
		this.animationAttributesByName = new HashMap();
	}
	
	public void addAnimationSetting( String name, String value ) {
		this.animationAttributesByName.put(name, value);
	}
	
	public String getValue( String name ) {
		return (String) this.animationAttributesByName.get( name );
	}

	/**
	 * @return
	 */
	public String getCssAttributeName()
	{
		return this.cssAttributeName;
	}

	public String[] getKeys() {
		return (String[]) this.animationAttributesByName.keySet().toArray( new String[ this.animationAttributesByName.size() ]);
	}

	/**
	 * @return
	 */
	public String getOn()
	{
		String on = getValue("on");
		if (on == null) {
			on = "focused";
		}
		return on;
	}

	/**
	 * @return
	 */
	public long getDuration()
	{
		String durationStr = getValue("duration");
		if (durationStr == null) {
			return 1000L;
		}
		return parseTime( durationStr );
	}
	
	/**
	 * @param durationStr
	 * @return
	 */
	private long parseTime(String timeStr)
	{
		int factor = 1;
		if (timeStr.endsWith( "ms")) {
			timeStr = timeStr.substring(0, timeStr.length() - 2).trim();
		} else if (timeStr.endsWith("s")) {
			timeStr = timeStr.substring(0, timeStr.length() - 1).trim();
			factor = 1000;
		}
		long value = Long.parseLong(timeStr) * factor;
		return value;
	}

	/**
	 * @return
	 */
	public long getDelay()
	{
		String delayStr = getValue("delay");
		if (delayStr == null) {
			return 0L;
		}
		return parseTime( delayStr );
	}

	/**
	 * @return
	 */
	public String getTimingFunction()
	{
		String function = getValue("function");
		if (function == null) {
			return "CssAnimation.FUNCTION_EASE_OUT";
		}
		String realFunction = (String) TIMING_FUNCTION_BY_NAME.get(function);
		if (realFunction == null) {
			throw new BuildException("Unknown \"function\" \"" + function + "\" in CSS animation - check your polish.css for CSS animation " + getCssAttributeName() );
		}
		return realFunction;
	}
	
}
