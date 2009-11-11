//#condition polish.android
package de.enough.polish.android.pim.enough;

import java.util.Enumeration;

import de.enough.polish.android.pim.Contact;
import de.enough.polish.android.pim.ContactList;
import de.enough.polish.android.pim.PIMException;
import de.enough.polish.android.pim.PIMItem;

// TODO: Put a lot of stuff into a superclass.
public class ContactListImpl extends AbstractPIMList implements ContactList {

	public static final FieldInfo CONTACT_ADDR_FIELD_INFO = new FieldInfo(Contact.ADDR,PIMItem.STRING_ARRAY,"Address",7,Contact.ADDR_EXTRA,new int[] {Contact.ADDR_EXTRA}, new int[] {Contact.ATTR_HOME,Contact.ATTR_WORK,Contact.ATTR_OTHER,Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_NAME_FIELD_INFO = new FieldInfo(Contact.NAME,PIMItem.STRING_ARRAY,"Name",5,Contact.NAME_OTHER,new int[] {Contact.NAME_OTHER},new int[] {Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_TEL_FIELD_INFO = new FieldInfo(Contact.TEL,PIMItem.STRING,"Phone",0,0,new int[0],new int[] {Contact.ATTR_MOBILE,Contact.ATTR_WORK,Contact.ATTR_HOME,Contact.ATTR_OTHER,Contact.ATTR_PAGER,Contact.ATTR_FAX,Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_NOTE_FIELD_INFO = new FieldInfo(Contact.NOTE,PIMItem.STRING,"Note",0,0,new int[0],new int[] {Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_FORMATTED_NAME_FIELD_INFO = new FieldInfo(Contact.FORMATTED_NAME,PIMItem.STRING,"Formatted Name",0,0,new int[0],new int[] {Contact.ATTR_NONE});
	public static final FieldInfo CONTACT_UID_FIELD_INFO = new FieldInfo(Contact.UID,PIMItem.STRING,"Unique identifier",0,0,new int[0],new int[] {Contact.ATTR_NONE});

	
	// TODO: This is a helper class but it is also a dependency...
	private ContactDao contactDao;

	ContactListImpl(String name, int mode) {
		super(name,mode);
		this.contactDao = new ContactDao(this);
		setFieldInfos(new FieldInfo[] {CONTACT_ADDR_FIELD_INFO,CONTACT_NAME_FIELD_INFO,CONTACT_TEL_FIELD_INFO,CONTACT_NOTE_FIELD_INFO,CONTACT_FORMATTED_NAME_FIELD_INFO});
	}

	public void addCategory(String category) throws PIMException {
		if(category == null) {
			throw new NullPointerException();
		}
		ensureListWriteable();
		throw new UnsupportedOperationException();
	}

	public void close() throws PIMException {
		throw new UnsupportedOperationException();
	}

	public Contact createContact() {
		ContactImpl contact = new ContactImpl(this);
		return contact;
	}

	public void deleteCategory(String category, boolean deleteUnassignedItems) throws PIMException {
		throw new UnsupportedOperationException();
	}

	public String getArrayElementLabel(int stringArrayField, int arrayElement) {
		throw new UnsupportedOperationException();
	}

	public String getAttributeLabel(int attribute) {
		//#debug
		System.out.println("Method ContactList.getArrayElementLabel is not implemended.");
		return "";
	}

	public String[] getCategories() throws PIMException {
		return new String[0];
	}

	public Contact importContact(Contact contact) {
		throw new UnsupportedOperationException();
	}

	public boolean isCategory(String category) throws PIMException {
		if(category == null) {
			throw new NullPointerException();
		}
		throw new UnsupportedOperationException();
	}

	public Enumeration items() throws PIMException {
		ensureListReadable();
		return this.contactDao.items();
	}

	public Enumeration items(PIMItem matchingItem) throws PIMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Enumeration items(String matchingValue) throws PIMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public Enumeration itemsByCategory(String category) throws PIMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public int maxCategories() {
		return 0;
	}

	public void removeContact(Contact contact) throws PIMException {
		ensureListWriteable();
		ContactImpl contactImpl = (ContactImpl)contact;
		try {
			this.contactDao.removeContact(contactImpl);
		}
		catch(Exception e) {
			throw new PIMException(e.getMessage(),PIMException.GENERAL_ERROR);
		}
	}

	public void renameCategory(String currentCategory, String newCategory) throws PIMException {
		ensureListWriteable();
		throw new UnsupportedOperationException();
	}

	void persist(ContactImpl contact) {
		this.contactDao.persist(contact);
	}

}
