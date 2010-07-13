/*
 * Created on Jul 13, 2010 at 1:39:27 PM.
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
package de.enough.polish.format.atom;

import de.enough.polish.util.IdentityArrayList;
import de.enough.polish.util.TimePoint;
import de.enough.polish.xml.XmlDomNode;

/**
 * <p>Manages an entry within an Atom based news feed</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AtomEntry {

	private String id;
	private String title;
	private String updatedString;
	private TimePoint updated;
	private String summary;
	private String content;
	private String contentType;
	private IdentityArrayList imageUrls;

	public AtomEntry() {
		// nothing to init here
	}
	
	public AtomEntry(XmlDomNode node) {
		this.id = node.getChildText("id");
		this.title = node.getChildText("title");
		this.updatedString = node.getChildText("updated");
		this.summary = node.getChildText("summary");
		XmlDomNode contNode = node.getChild("atom:content");
		if (contNode != null) {
			this.content = contNode.getText();
			this.contentType = contNode.getAttribute("type");
		}
		int childCount = node.getChildCount();
		for (int i=0; i<childCount; i++) {
			XmlDomNode linkNode = node.getChild(i);
			if ("link".equals(linkNode.getName())) {
				String type = linkNode.getAttribute("type");
				if (type != null && type.startsWith("image")) {
					String href = linkNode.getAttribute("href");
					if (href != null) {
						if (this.imageUrls == null) {
							this.imageUrls = new IdentityArrayList();
						}
						this.imageUrls.add( href );
					}
				}
			}
		}
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @return the updatedString
	 */
	public TimePoint getUpdated() {
		if (this.updated == null && this.updatedString != null) {
			this.updated = TimePoint.parseRfc3339(this.updatedString);
		}
		return this.updated;
	}

	/**
	 * @return the summary
	 */
	public String getSummary() {
		return this.summary;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return this.contentType;
	}
	
	/**
	 * Retrieves the URLs for images that are stored in this entry
	 * @return an array of URLs of referenced images, can be empty but not null
	 */
	public String[] getImageUrls() {
		if (this.imageUrls == null) {
			return new String[0];
		}
		return (String[]) this.imageUrls.toArray( new String[ this.imageUrls.size() ] );
	}
	
	/**
	 * Retrieves the internal array of the image URLs stored in this entry
	 * @return either null or the internal array of stored URLs which may contain null values
	 */
	public Object[] getImageUrlsAsInternalArray() {
		if (this.imageUrls == null) {
			return null;
		}
		return this.imageUrls.getInternalArray();
	}
	
}
