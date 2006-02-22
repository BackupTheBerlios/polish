package javax.microedition.rms;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;



public class RecordEnumerationImpl implements RecordEnumeration {
	
	private final RecordStore store;
	private boolean keepUpdated;
	private final RecordComparator comparator;
	private final RecordFilter filter;
	private final File storeDir;
	private RecordSet[] recordSets;
	private int currentIndex = -1;

	public RecordEnumerationImpl( RecordStore store, RecordFilter filter, RecordComparator comparator, boolean keepUpdated ) {
		this.store = store;
		this.filter = filter;
		this.comparator = comparator;
		this.keepUpdated = keepUpdated;
		this.storeDir = store.getDir();
		rebuild();
	}

	public int numRecords() {
		return this.recordSets.length;
	}

	public byte[] nextRecord() 
	throws InvalidRecordIDException,
			RecordStoreNotOpenException, RecordStoreException 
	{
		int nextIndex = this.currentIndex + 1;
		if (nextIndex >= this.recordSets.length ) {
			throw new InvalidRecordIDException();
		}
		RecordSet set = this.recordSets[ nextIndex ];
		this.currentIndex = nextIndex;
		return set.getData();
	}

	public int nextRecordId() 
	throws InvalidRecordIDException 
	{
		int nextIndex = this.currentIndex + 1;
		if (nextIndex >= this.recordSets.length ) {
			throw new InvalidRecordIDException();
		}
		RecordSet set = this.recordSets[ nextIndex ];
		this.currentIndex = nextIndex;
		return set.getId();
	}

	public byte[] previousRecord() throws InvalidRecordIDException,
			RecordStoreNotOpenException, RecordStoreException 
	{
		int nextIndex = this.currentIndex - 1;
		if (nextIndex < 0 ) {
			throw new InvalidRecordIDException();
		}
		RecordSet set = this.recordSets[ nextIndex ];
		this.currentIndex = nextIndex;
		return set.getData();
	}

	public int previousRecordId() throws InvalidRecordIDException {
		int nextIndex = this.currentIndex - 1;
		if (nextIndex < 0 ) {
			throw new InvalidRecordIDException();
		}
		RecordSet set = this.recordSets[ nextIndex ];
		this.currentIndex = nextIndex;
		return set.getId();
	}

	public boolean hasNextElement() {
		return this.currentIndex + 1 < this.recordSets.length;
	}

	public boolean hasPreviousElement() {
		return this.currentIndex > 0;
	}

	public void reset() {
		this.currentIndex = -1;
	}

	public void rebuild() {
		if (!this.storeDir.exists()) {
			this.recordSets = new RecordSet[0];
		} else {
			// sort and filter the files:
			File[] allFiles = this.storeDir.listFiles( new VersionFileFilter() );

			RecordSet[] sets = new RecordSet[ allFiles.length ];
			for ( int i = 0; i < sets.length; i++ ) {
				File file = allFiles[i];
				sets[i] =  new RecordSet( file );
			}
			sets = filter( sets  );
			sets = sort( sets );
			this.recordSets = sets;
		}
		this.currentIndex = -1;
	}

	private RecordSet[] sort( RecordSet[] sets ) {
		// TODO Auto-generated method stub
		return sets;
	}

	private RecordSet[] filter( RecordSet[] sets ) {
		if (this.filter == null ) {
			return sets;
		}
		ArrayList setsList = new ArrayList( sets.length );
		for ( int i = 0; i < sets.length; i++ ) {
			RecordSet set = sets[i];
			try {
				if ( this.filter.matches( set.getData()) ) {
					setsList.add( set );
				}
			} catch (RecordStoreException e) {
				e.printStackTrace();
			}
		}
		sets = (RecordSet[]) setsList.toArray( new RecordSet[ setsList.size() ] );
		return sets;
	}

	public void keepUpdated( boolean keepUpdated ) {
		this.keepUpdated = keepUpdated;
		if (keepUpdated) {
			rebuild();
		}
	}

	public boolean isKeptUpdated() {
		return this.keepUpdated;
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}
	
	class RecordSet {
		private final File file;
		private final int id;
		private byte[] data;

		public RecordSet( File file ) {
			this.file = file;
			this.id = Integer.parseInt( file.getName() );
		}

		public int getId() {
			return this.id;
		}

		public byte[] getData()
		throws RecordStoreException
		{
			if (this.data == null || RecordEnumerationImpl.this.keepUpdated) {
				this.data = RecordEnumerationImpl.this.store.getRecord( this.id );
			}
			return this.data;
		}
	}
	
	class VersionFileFilter implements FileFilter {

		public boolean accept( File file ) {
			if ( "version".equals(file.getName() ) ) {
				return false;
			} else {
				return true;
			}
		}
		
	}

}
