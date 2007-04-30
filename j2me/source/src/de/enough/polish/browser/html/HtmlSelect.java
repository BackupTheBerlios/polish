//#condition polish.usePolishGui

/*
 * Created on 11-Jan-2006 at 19:20:28.
 * 
 * Copyright (c) 2007 Michael Koch / Enough Software
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
package de.enough.polish.browser.html;

import de.enough.polish.ui.Choice;
import de.enough.polish.ui.ChoiceGroup;

import java.util.Vector;

public class HtmlSelect
{
	private static final String SELECT = "select";

	private String name;
	private int selectedIndex;
	private Vector optionNames;
	private Vector optionValues;

	public HtmlSelect(String name)
	{
		this.name = name;

		this.selectedIndex = -1;
		this.optionNames = new Vector();
		this.optionValues = new Vector();
	}

	public String getName()
	{
		return this.name;
	}
	
	public void addOption(String name)
	{
		addOption(name, name, false);
	}

	public void addOption(String name, String value, boolean selected)
	{
		this.optionNames.addElement(name);
		this.optionValues.addElement(value);

		if (selected) {
			this.selectedIndex = this.optionNames.size() - 1;
		}
	}
	
	public ChoiceGroup getChoiceGroup()
	{
		try
		{
			//#style browserOption
			ChoiceGroup choiceGroup = new ChoiceGroup(null, Choice.EXCLUSIVE);

			for (int i = 0; i < this.optionNames.size(); i++) {
				//#style browserOptionItem
				choiceGroup.append((String) this.optionNames.elementAt(i), null);
			}

			if (this.selectedIndex != -1) {
				choiceGroup.setSelectedIndex(this.selectedIndex, true);
			}

			choiceGroup.setAttribute(SELECT, choiceGroup);
			return choiceGroup;
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
}
