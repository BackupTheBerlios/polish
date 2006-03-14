/*
 * Created on 13-Mar-2006 at 19:20:28.
 * 
 * Copyright (c) 2006 Robert Virkus / Enough Software
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
package de.enough.polish.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * <p>Defines methods for serializing and de-serializing classes.</p>
 * <p>Note that classes implementing the Externalizable interface are required
 *    to provide a default constructor without any parameters.
 *    This is turn means that no final instance fields are allowed.
 * </p>
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        13-Mar-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 * @see de.enough.polish.io.Storage
 * @see de.enough.polish.io.RmsStorage
 */
public interface Externalizable extends Serializable {
	
	/**
	 * Stores the internal instance fields to the output stream.
	 * 
	 * @param out the output stream to which instance fields should be written
	 */
	public void write( DataOutputStream out );
	
	/**
	 * Restores the internal instance fields from the given input stream.
	 * 
	 * @param in the input stream from which the data is loaded
	 */
	public void read( DataInputStream in );

}
