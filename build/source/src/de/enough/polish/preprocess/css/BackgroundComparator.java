/*
 * Created on Nov 21, 2007 at 9:45:55 PM.
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

import java.util.Comparator;
import java.util.Map;

import de.enough.polish.BuildException;
import de.enough.polish.preprocess.css.attributes.ParameterizedCssAttribute;

/**
 * <p>Compares backgrounds within the backgrounds section - backgrounds without dependencies need to be created before backgrounds with dependencies like the combined or the mask background.</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        Nov 21, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class BackgroundComparator implements Comparator
{

	private final Map backgrounds;
	private final ParameterizedCssAttribute backgroundTypes;

	/**
	 * @param backgrounds
	 * @param backgroundTypes 
	 */
	public BackgroundComparator(Map backgrounds, ParameterizedCssAttribute backgroundTypes)
	{
		this.backgrounds = backgrounds;
		this.backgroundTypes = backgroundTypes;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object name1, Object name2)
	{
//		System.out.println();
//		System.out.println("comparing background " + name1 + " with " + name2 );
		Map background1 = (Map) this.backgrounds.get(name1);
		Map background2 = (Map) this.backgrounds.get(name2);
		
		String type1 = (String) background1.get("type");
		String type2 = (String) background2.get("type");
//		System.out.println("comparing background type " + type1 + " with " + type2 );
		if (type1 == null && type2 == null) {
			return 0;
		}
		if (type1 == null) {
			return -1;
		}
		if (type2 == null) {
			return 1;
		}
		ParameterizedCssMapping backgroundMapping1 = (ParameterizedCssMapping) this.backgroundTypes.getMapping(type1);
		if (backgroundMapping1 == null) {
			throw new BuildException("Unable to resolve background mapping for type \"" + type1 + "\" - check your polish.css file(s).");
		}
		int backgroundTypeParams1 = 0;
		CssAttribute[] parameters1 = backgroundMapping1.getParameters();
		for (int i = 0; i < parameters1.length; i++)
		{
			CssAttribute parameter = parameters1[i];
			if ("background".equals(parameter.getType()) || "background".equals(parameter.getName())) {
//				System.out.println("background1: " + i + "=" + parameter.getName() + " / " + parameter.getType() + " / value=" +  background1.get( parameter.getName() ) );
				backgroundTypeParams1++;
				if (name2.equals( background1.get( parameter.getName() )) ) {
					// background1 depends on background2:
//					System.out.println( name1 + " depends on " + name2 );
					return 1;
				}
			}
		}
		ParameterizedCssMapping backgroundMapping2 = (ParameterizedCssMapping) this.backgroundTypes.getMapping(type2);
		if (backgroundMapping2 == null) {
			throw new BuildException("Unable to resolve background mapping for type \"" + type2 + "\" - check your polish.css file(s).");
		}
		int backgroundTypeParams2 = 0;
		CssAttribute[] parameters2 = backgroundMapping2.getParameters();
		for (int i = 0; i < parameters2.length; i++)
		{
			CssAttribute parameter = parameters2[i];
			if ("background".equals(parameter.getType())  || "background".equals(parameter.getName()) ) {
//				System.out.println("background2: " + i + "=" + parameter.getName() + " / " + parameter.getType() + " / value=" +  background1.get( parameter.getName() ) );
				backgroundTypeParams2++;
				if (name1.equals( background2.get( parameter.getName() )) ) {
					// background2 depends on background1:
//					System.out.println( name2 + " depends on " + name1 );
					return -1;
				}
			}
		}
//		System.out.println("background1.number=" + backgroundTypeParams1 + ", background2.number=" + backgroundTypeParams2);
		if (backgroundTypeParams1 == 0 && backgroundTypeParams2 == 0) {
			return 0;
		}
		if (backgroundTypeParams1 == 0 && backgroundTypeParams2 != 0) {
//			System.out.println( name2 + " contains background references" );
			return -1;
		}
		if (backgroundTypeParams1 != 0 && backgroundTypeParams2 == 0) {
//			System.out.println( name1 + " contains background references");
			return 1;
		}
		return 0;
	}

}
