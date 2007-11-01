/*
 * Created on 08-Apr-2006 at 19:20:28.
 * 
 * Copyright (c) 2006 Richard Nkrumah / Enough Software
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

import java.util.Hashtable;

import de.enough.polish.util.ArrayList;

/**
 * <p>Copyright Enough Software 2006, 2007</p>
 * 
 * <pre>
 * history
 *        Apr 8, 2006 - rickyn creation
 * </pre>
 * 
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class XmlDomNode
{
    private XmlDomNode parent;
    private ArrayList childList;
    private Hashtable attributes;
    private String name;
    private int type;
    private String text;
    
    /**
     * 
     * @param parent
     * @param name
     * @param type
     */
    public XmlDomNode(XmlDomNode parent, String name, int type) {
    	this(parent, name, null, type);
    }

    /**
     * 
     * @param parent
     * @param name
     * @param attributes
     * @param type
     */
    public XmlDomNode(XmlDomNode parent, String name, Hashtable attributes, int type) {
        this.parent = parent;

        if(this.parent != null) {
            this.parent.addChild(this);
        }

        this.name = name;
        this.attributes = attributes;
        this.type = type;
        this.childList = new ArrayList(); 
    }
    
    /**
     * 
     * @param childName
     * @return the first child with the given name or null if no child node is found.
     */
    public XmlDomNode getChild(String childName) {
        XmlDomNode child;

        for(int i = 0; i < this.childList.size();i++) {
            child = (XmlDomNode) this.childList.get(i);

            if(childName.equals(child.getName())) {
                return child;
            }
        }

        childName = childName.toLowerCase();

        for(int i = 0; i < this.childList.size();i++) {
            child = (XmlDomNode)this.childList.get(i);

            if(childName.equals(child.getName())) {
                return child;
            }
        }

        return null;
    }
   
    /**
     * 
     * @param i 
     * @return Node
     */
    public XmlDomNode getChild(int i) {
        return (XmlDomNode) this.childList.get(i);
    }
    
    /**
     * Add Node (child).
     * 
     * @param childNode
     */
    public void addChild(XmlDomNode childNode) {
        this.childList.add(childNode);
    }
    
    /**
     * 
     * @return number of Childs.
     */
    public int getChildCount() {
        return this.childList.size();
    }
    
    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public XmlDomNode getParent() {
        return this.parent;
    }
    
    public void setParent(XmlDomNode parent) {
        this.parent = parent;
    }

    public int getType() {
        return this.type;
    }
    
    public void setType(int type) {
        this.type = type;
    }

	public Hashtable getAttributes() {
		return this.attributes;
	}
	
	public void setAttributes(Hashtable attributes) {
		this.attributes = attributes;
	}
}
