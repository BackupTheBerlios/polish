/*
 * Created on Jul 19, 2010 at 5:46:39 PM.
 * 
 * Copyright (c) 2010 Robert Virkus / Enough Software
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

/**
 * <p>Provides access to an image of an AtomEntry</p>
 *
 * <p>Copyright Enough Software 2010</p>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class AtomImage {
	
	private String url;
	private byte[] data;
	private Object nativeRepresentation;

	/**
	 * Creates a new empty image
	 */
	public AtomImage() {
		// nothing to init
	}
	
	/**
	 * Creates a new image with the specified URL
	 * @param url the URL of the image
	 */
	public AtomImage( String url ) {
		this.url = url;
	}
	
	/**
	 * Retrieves a new image
	 * @param url the URL of the image
	 * @param data the data of this image
	 */
	public AtomImage( String url, byte[] data ) {
		this.url = url;
		this.data = data;
		
	}
	
	/**
	 * Retrieves the data of this image
	 * @return the data
	 * @see #setData(byte[])
	 */
	public byte[] getData() {
		return this.data;
	}
	
	/**
	 * Sets the data of this image
	 * @param data the raw data of this image
	 * @see #getData()
	 */
	public void setData( byte[] data) {
		this.data = data;
	}
	
	/**
	 * Retrieves the URL of this image
	 * @return the URL
	 */
	public String getUrl() {
		return this.url;
	}
	
	/**
	 * Retrieve a native representation as it has been set before.
	 * @return the native representation e.g. de.enough.polish.ui.Image
	 * @see #setNativeRepresentation(Object)
	 */
	public Object getNativeRepresentation() {
		return this.nativeRepresentation;
	}
	
	/**
	 * Sets the native representation as it has been set before.
	 * @param object the native representation e.g. de.enough.polish.ui.Image
	 * @see #getNativeRepresentation()
	 */
	public void setNativeRepresentation( Object object ) {
		this.nativeRepresentation = object;
	}

}
