//#condition polish.android
package de.enough.polish.android.pim.enough;

import java.util.Enumeration;

import de.enough.polish.android.midlet.MidletBridge;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.Contacts.People;

public class ContactEnumeration implements Enumeration{

	private Cursor peopleCursor;
	private ContentResolver contentResolver;
	private final ContactDao contactDao;
	private final int count;
	private int position;

	public ContactEnumeration(ContactDao contactDao) {
		this.contactDao = contactDao;
		this.contentResolver = MidletBridge.instance.getContentResolver();
		this.peopleCursor = this.contentResolver.query(People.CONTENT_URI, null, null, null, null);
		this.count = this.peopleCursor.getCount();
		this.position = -1;
	}

	public boolean hasMoreElements() {
		return this.position < this.count -1;
//		boolean isAfterLast = this.peopleCursor.isAfterLast();
//		return ! isAfterLast;
	}

	public Object nextElement() {
		//#debug
		System.out.println("Current contact is at position '"+this.position+"' of '"+this.count+"' contacts.");
		if(this.peopleCursor.isClosed()) {
			return null;
		}
		this.position++;
		this.peopleCursor.moveToPosition(this.position);
		ContactImpl contactFromCursor = this.contactDao.getContactFromCursor(this.peopleCursor);
		return contactFromCursor;
	}

}
