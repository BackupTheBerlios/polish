//#condition polish.midp
/*
 * Created on 13-Mar-2006 at 21:34:23.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * <p>Stores serializable objects in the record store system.</p>
 *
 * <p>Copyright Enough Software 2006</p>
 * <pre>
 * history
 *        13-Mar-2006 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class RmsStorage implements Storage {
	
	private final RecordStore masterRecordStore;
	private final Hashtable masterRecordSetIdsByName;
	private final int indexRecordId;

	/**
	 * Creates a new RmsStorage that uses different record stores for each stored object
	 */
	public RmsStorage() 
	{
		this.masterRecordStore = null;
		this.masterRecordSetIdsByName = null;
		this.indexRecordId = -1;
	}
	
	/**
	 * Creates a new RmsStorage that uses different record stores for each stored object
	 * 
	 * @param singleRecordStoreName the name of the record store, when one record store 
	 *        should be used for all entries - or null when for each name a new record store
	 *        should be created.  
	 * @throws IOException when the singleRecordStore is not null and the corresponding recordstore could not be opened or created.
	 */
	public RmsStorage( String singleRecordStoreName ) 
	throws IOException 
	{
		if ( singleRecordStoreName != null ) {
			try {
				this.masterRecordStore = RecordStore.openRecordStore(singleRecordStoreName, true);
				this.masterRecordSetIdsByName = new Hashtable();
				// now read index record set:
				RecordEnumeration enumeration = this.masterRecordStore.enumerateRecords( null, null, false );
				int firstId = Integer.MAX_VALUE;
				while ( enumeration.hasNextElement() ) {
					// read index:	
					int id = enumeration.nextRecordId();
					if ( id < firstId ) {
						firstId = id;
					}
				}
				enumeration.destroy();
				if (firstId == Integer.MAX_VALUE) {
					// ok, this record store has just been created, so add a dumny 
					// recordset for now:
					ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream( byteOut );
					out.writeInt( 0 );
					byte[] data = byteOut.toByteArray();
					this.indexRecordId = this.masterRecordStore.addRecord(data, 0, data.length );
					out.close();
					byteOut.close();
				} else {
					this.indexRecordId = firstId;
					DataInputStream in = new DataInputStream( 
							new ByteArrayInputStream( this.masterRecordStore.getRecord(firstId) ));
					int numberOfEntries = in.readInt();
					for (int i = 0; i < numberOfEntries; i++) {
						String name = in.readUTF();
						int recordId = in.readInt();
						this.masterRecordSetIdsByName.put( name, new Integer( recordId ) );
					}
					in.close();
				}
			} catch (RecordStoreException e) {
				throw new IOException( e.toString() );
			}
		} else {
			this.masterRecordStore = null;			
			this.masterRecordSetIdsByName = null;
			this.indexRecordId = -1;
		}
	}

	/**
	 * Retrieves the record set ID for the given name.
	 * This method can only be used when a master record store is used.
	 * 
	 * @param name the name of the set
	 * @return either the set ID or -1 when the name is not yet used 
	 */
	private int getRecordSetId( String name ) {
		Integer id = (Integer) this.masterRecordSetIdsByName.get(name);
		if (id != null) {
			return id.intValue();
		} else {
			return -1;
		}
	}
	
	/**
	 * Registers a new record set ID in the index record set of the master record store.
	 * 
	 * @param id the ID of the new record set
	 * @param name the name for the set
	 * @throws IOException when the index record set could not be prepared
	 * @throws RecordStoreException when the index record set could not be written
	 */
	private void registerRecordSetId( int id, String name )
	throws IOException, RecordStoreException
	{
		this.masterRecordSetIdsByName.put( name, new Integer( id ) );
		Enumeration enumeration = this.masterRecordSetIdsByName.keys();
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( byteOut );
		out.write( this.masterRecordSetIdsByName.size() );
		while (enumeration.hasMoreElements() ) {
			String key = (String)enumeration.nextElement();
			Integer idInt = (Integer) this.masterRecordSetIdsByName.get( key );
			out.writeUTF(name);
			out.writeInt( idInt.intValue() );
		}
		byte[] data = byteOut.toByteArray();
		this.masterRecordStore.setRecord( this.indexRecordId, data, 0, data.length );
		out.close();
		byteOut.close();		
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Storage#save(de.enough.polish.io.Serializable, java.lang.String)
	 */
	public void save(Serializable object, String name) throws IOException {
		Externalizable extern = (Externalizable) object;
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( byteOut );
		out.writeUTF( extern.getClass().getName() );
		// TODO: write version of class?
		extern.write(out);
		byte[] data = byteOut.toByteArray();
		out.close();
		byteOut.close();
		saveData( name, data);
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Storage#saveAll(de.enough.polish.io.Serializable[], java.lang.String)
	 */
	public void saveAll(Serializable[] objects, String name) throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( byteOut );
		out.writeInt( objects.length );
		if (objects.length > 0) {
			Externalizable extern = (Externalizable) objects[0];
			// TODO: write version of class?
			out.writeUTF( extern.getClass().getName() );
			for (int i = 0; i < objects.length; i++) {
				extern = (Externalizable) objects[i];
				extern.write(out);				
			}
		}
		byte[] data = byteOut.toByteArray();
		out.close();
		byteOut.close();
		saveData( name, data);
	}
	
	/**
	 * Stores the data under the given name.
	 * 
	 * @param name the name
	 * @param data the data
	 * @throws IOException when storage fails
	 */
	private void saveData(String name, byte[] data) throws IOException {
		try {
			if (this.masterRecordStore != null) {
				int recordSetId = getRecordSetId( name );
				if (recordSetId != -1 ) {
					this.masterRecordStore.setRecord(recordSetId, data, 0, data.length );
				} else {
					recordSetId = this.masterRecordStore.addRecord( data, 0, data.length );
					registerRecordSetId(recordSetId, name);
				}
			} else {
				RecordStore store = RecordStore.openRecordStore(name, true);
				int recordSetId = -1;
				RecordEnumeration enumeration = store.enumerateRecords(null, null, false);
				if (enumeration.hasNextElement()) {
					recordSetId = enumeration.nextRecordId();
				}
				enumeration.destroy();
				if (recordSetId == -1) {
					// new record set:
					store.addRecord(data, 0, data.length);
				} else {
					// existing record set:
					store.setRecord(recordSetId, data, 0, data.length);
				}
				store.closeRecordStore();
			}
		} catch (RecordStoreException e ) {
			//#debug error
			System.out.println("Unable to store object under name [" + name + "]" + e );
			throw new IOException( e.toString() );
		}
	}


	/* (non-Javadoc)
	 * @see de.enough.polish.io.Storage#read(java.lang.String)
	 */
	public Serializable read(String name) throws IOException {
		byte[] data;
		try {
			if (this.masterRecordStore != null) {
				int recordId = getRecordSetId(name);
				if (recordId == -1) {
					throw new IOException( name + " is unknown");
				}
				data = this.masterRecordStore.getRecord(recordId);
			} else {
				RecordStore store = RecordStore.openRecordStore(name, false);
				RecordEnumeration enumeration = store.enumerateRecords(null, null, false);
				data = enumeration.nextRecord();
				enumeration.destroy();
				store.closeRecordStore();
			}
		} catch (RecordStoreException e) {
			throw new IOException( e.toString() );
		}
		DataInputStream in = new DataInputStream( new ByteArrayInputStream( data ));
		String className = in.readUTF();
		try {
			Externalizable extern = (Externalizable) Class.forName(className).newInstance();
			extern.read(in);
			in.close();
			return extern; 
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to load/initialize class " + className + e );
			throw new IOException( e.toString() ); 
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Storage#enumerate(java.lang.String)
	 */
	public Enumeration enumerate(String name) throws IOException {
		throw new IOException("Sorry, not supported - might drop this method altogether");
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.io.Storage#readAll(java.lang.String)
	 */
	public Serializable[] readAll(String name) throws IOException {
		byte[] data;
		try {
			if (this.masterRecordStore != null) {
				int recordId = getRecordSetId(name);
				if (recordId == -1) {
					throw new IOException( name + " is unknown");
				}
				data = this.masterRecordStore.getRecord(recordId);
			} else {
				RecordStore store = RecordStore.openRecordStore(name, false);
				RecordEnumeration enumeration = store.enumerateRecords(null, null, false);
				data = enumeration.nextRecord();
				enumeration.destroy();
				store.closeRecordStore();
			}
		} catch (RecordStoreException e) {
			throw new IOException( e.toString() );
		}
		DataInputStream in = new DataInputStream( new ByteArrayInputStream( data ));
		int arraySize = in.readInt();
		String className = in.readUTF();
		try {
			Class externClass = Class.forName(className);
			Externalizable[] externs = new Externalizable[ arraySize ];
			for (int i = 0; i < arraySize; i++) {
				Externalizable extern = (Externalizable) externClass.newInstance();
				extern.read(in);
				externs[i] = extern;
			}
			in.close();
			return externs; 
		} catch (Exception e) {
			//#debug error
			System.out.println("Unable to load/initialize class " + className + e );
			throw new IOException( e.toString() ); 
		}
	}

}
