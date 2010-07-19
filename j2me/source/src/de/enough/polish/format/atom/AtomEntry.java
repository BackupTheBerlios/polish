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

import java.io.IOException;
import java.io.InputStream;

import de.enough.polish.io.RedirectHttpConnection;
import de.enough.polish.util.IdentityArrayList;
import de.enough.polish.util.StreamUtil;
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
	private IdentityArrayList images;
	private boolean hasLoadedImages;

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
						if (this.images == null) {
							this.images = new IdentityArrayList();
						}
						this.images.add( new AtomImage(href) );
					}
				}
			}
		}
	}

	/**
	 * Retrieves the ID of this entry
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Retrieves the title of this entry
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Retrieves the updated time
	 * @return the update time of this entry, might be null
	 */
	public TimePoint getUpdated() {
		if (this.updated == null && this.updatedString != null) {
			this.updated = TimePoint.parseRfc3339(this.updatedString);
		}
		return this.updated;
	}

	/**
	 * Retrieves the summary of this entry
	 * @return the summary
	 */
	public String getSummary() {
		return this.summary;
	}

	/**
	 * Retrieves the content of this entry
	 * @return the content
	 * @see #getContentType()
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Retrieves the type of this content
	 * @return the contentType, e.g. html
	 */
	public String getContentType() {
		return this.contentType;
	}
	
	/**
	 * Determines whether this entry has any images at all.
	 * @return true when there are referenced images
	 */
	public boolean hasImages() {
		return (this.images != null);
	}
	
	/**
	 * Retrieves the images that are stored in this entry
	 * @return an array of AtomImages of referenced images, can be empty but not null
	 */
	public AtomImage[] getImages() {
		if (this.images == null) {
			return new AtomImage[0];
		}
		return (AtomImage[]) this.images.toArray( new AtomImage[ this.images.size() ] );
	}
	
	/**
	 * Retrieves the internal array of the image URLs stored in this entry
	 * @return either null or the internal array of stored AtomImages which may contain null values
	 */
	public Object[] getImagesAsInternalArray() {
		if (this.images == null) {
			return null;
		}
		return this.images.getInternalArray();
	}

	/**
	 * Determines whether the referenced images have been loaded yet.
	 * @return true when the images have been loaded
	 * @see #getImages()
	 * @see #loadImages(AtomImageConsumer)
	 */
	public boolean hasLoadedImages() {
		return this.hasLoadedImages;
	}
	
	/**
	 * Loads the referenced images in this thread.
	 * @param consumer the consumer
	 */
	public void loadImages( AtomImageConsumer consumer ) {
		if (this.images == null) {
			consumer.onAtomImageLoadFinished(this);
			return;
		}
		InputStream in = null;
		RedirectHttpConnection connection = null;
		for (int i=0; i<this.images.size(); i++) {
			AtomImage image = (AtomImage) this.images.get(i);
			String url = image.getUrl();
			try {
				connection = new RedirectHttpConnection( url );
				in = connection.openInputStream();
				if (connection.getResponseCode() != 200) {
					throw new IOException("response code " + connection.getResponseCode() + " for " + url);
				}
				byte[] data = StreamUtil.readFully(in);
				image.setData( data );
				if (consumer != null) {
					consumer.onAtomImageLoaded(image, this);
				}
				try {
					in.close();
					in = null;
					connection.close();
					connection = null;
				} catch (Exception e) {
					// ignore
				}
			} catch (Throwable e) {
				if (consumer != null) {
					consumer.onAtomImageLoadError(image, this, e);
				}
			}
		}
		if (consumer != null) {
			consumer.onAtomImageLoadFinished(this);
		}
		this.hasLoadedImages = true;
	}
}
