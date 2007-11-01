/*
 * Created on 08-Apr-2006 at 19:20:28.
 * 
 * Copyright (c) 2006 Michael Koch / Enough Software
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
package de.enough.polish.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

/**
 * <p>Copyright Enough Software 2006, 2007</p>
 * 
 * <pre>
 * history
 *        Apr 8, 2006 - mkoch creation
 * </pre>
 * 
 * @author Michael Koch, michael.koch@enough.de
 */
public class XmlDomParser
{
    public static XmlDomNode parseTree(String document)
    {
    	XmlPullParser parser;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(document.getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);

        try {
            parser = new XmlPullParser(inputStreamReader);
        } catch (IOException exception) {
            throw new RuntimeException("Could not create xml parser."+exception);
        }

        XmlDomNode root = new XmlDomNode(null, null, -1);
        XmlDomNode currentNode = root;
        String newName;
        int newType;
        
        try {
            while ((parser.next()) != XmlPullParser.END_DOCUMENT) {
                newName = parser.getName();
                newType = parser.getType();
                
                if(newType == XmlPullParser.START_TAG) {
                	Hashtable attributes = null;
                	int attributeCount = parser.getAttributeCount(); 

                	if (attributeCount > 0) {
                		attributes = new Hashtable();

                		for (int i = 0; i < attributeCount; i++) {
                			attributes.put(parser.getAttributeName(i), parser.getAttributeValue(i));
                		}
                	}

                    XmlDomNode newNode = new XmlDomNode(currentNode, newName, attributes, newType);
                    currentNode = newNode;
                }
                
                else if(newType == XmlPullParser.END_TAG) {
                    currentNode = currentNode.getParent();
                }
                
                else if(newType == XmlPullParser.TEXT) {
                    String text = parser.getText();
                    currentNode.setText(text);
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException("Could not parse tree:"+exception);
        }
        
        return root;
    }
}
