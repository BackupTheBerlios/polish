/*
 * Created on May 3, 2007 at 1:54:12 AM.
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
package de.enough.polish.resources;

import java.io.IOException;

/**
 * <p>Provides resources</p>
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        May 3, 2007 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public interface ResourcesProvider {
	
	
	public ImageProvider[] getImages();
	
	public ImageProvider getImage( String name );
	
	public void addImage( String name, ImageProvider image );
	
	public StyleProvider[] getStyles();
	
	public StyleProvider getStyle( String name );
	
	public void addStyle( String name, StyleProvider style );
	
	public ColorProvider[] getColors();
	
	public ColorProvider getColor( String name );
	
	public void addColor( String name, ColorProvider color );
	
	
	public void saveResources() throws IOException;
}
