//#condition polish.android
package de.enough.polish.drone.rms;

import java.util.List;

import android.content.ContentValues;
import android.content.DatabaseContentProvider;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

public class RecordStoreContentProvider extends DatabaseContentProvider implements BaseColumns{

	public static final String AUTHORITY = "de.enough.polish.drone.rms";
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	
	private static final int STORE = 0;
	private static final int RECORD = 1;
	
	public static final UriMatcher MATCHER = new UriMatcher(0);
	
	public static final String[] PROJECTION = new String[]{"_id","_data"};
	
    static
    {
    	MATCHER.addURI(AUTHORITY, "/*", STORE);
        MATCHER.addURI(AUTHORITY, "/*/#", RECORD);
    }

	public RecordStoreContentProvider(String arg0, int arg1) {
		super(arg0, arg1);
	}

	protected int deleteInternal(Uri url, String selection, String[] selectionArgs) {
		if((MATCHER.match(url)) == RECORD){
			List<String> segments = url.getPathSegments();
            
            String storeID = segments.get(0);
            String recordID = segments.get(1);
			
            return this.mDb.delete(storeID, "_id=" + recordID,null);
		} else if(MATCHER.match(url) == STORE) 
		{
			String storeID = url.getLastPathSegment();
			
			return this.mDb.delete(storeID, null,null);
		}
		else
		{
			return -1;
		}
	}

	protected Uri insertInternal(Uri url, ContentValues values) {
		if((MATCHER.match(url)) == STORE){
			String storeID = url.getLastPathSegment();
			
			long recordID = this.mDb.insert(storeID, null, values);
			
			return Uri.parse("content://" + AUTHORITY + "/" + storeID + "/" + recordID);
		}
		else
		{
			return null;
		}
	}

	protected Cursor queryInternal(Uri url, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		if((MATCHER.match(url)) == RECORD){
			List<String> segments = url.getPathSegments();
            
            String storeID = segments.get(0);
            String recordID = segments.get(1);
            
            SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        	
        	qBuilder.setTables(storeID);
        	//TODO FIX !!!
            //qBuilder.appendWhere("_id=" + recordID);
            
            return qBuilder.query(this.mDb,
            		PROJECTION,
                    null,
                    null,
                    null,
                    null,
                    null);
        }
        else
        {
        	// No record id set, return null
        	return null;
        }
	}

	protected int updateInternal(Uri url, ContentValues values, String arg2,
			String[] arg3) {
		if((MATCHER.match(url)) == RECORD){
			List<String> segments = url.getPathSegments();
            
            String storeID = segments.get(0);
            String recordID = segments.get(1);
            
            return this.mDb.update(storeID, values, "_id=" + recordID,null);
		}
		else
		{
			return 0;
		}
	}

	protected void upgradeDatabase(int arg0, int arg1) {
		//Do nothing
	}

	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
