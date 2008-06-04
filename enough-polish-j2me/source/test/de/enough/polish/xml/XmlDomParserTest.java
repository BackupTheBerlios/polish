package de.enough.polish.xml;
import junit.framework.TestCase;

/*
 * Created on Jun 4, 2008 at 12:11:41 PM.
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

/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2008</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class XmlDomParserTest extends TestCase
{
	
	public void testParse() {
		String document = "<rootelement><childnode x=\"10\">textvalue10</childnode><childnode x=\"20\">textvalue20</childnode></rootelement>";
		XmlDomNode root = XmlDomParser.parseTree(document);
		System.out.println("root " + root.getName() + ", has " + root.getChildCount() + " children");
		// indirect access:
		for (int i=0; i < root.getChildCount(); i++ ) {
			XmlDomNode child = root.getChild(i);
			System.out.println("child: " + child.getName() );
			System.out.println("child: " + child.getName() + ", x=" + child.getAttribute("x") + ", text=" + child.getText() );
		}
		// direct access:
		XmlDomNode child = root.getChild("childnode");		
		System.out.println("first child: " + child.getName() + ", x=" + child.getAttribute("x") + ", text=" + child.getText() );
	}

}
