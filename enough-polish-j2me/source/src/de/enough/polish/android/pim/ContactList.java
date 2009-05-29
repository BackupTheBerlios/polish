//#condition polish.android
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Wed Mar 25 13:56:47 CET 2009
package de.enough.polish.android.pim;

/**
 * Represents a Contact list containing Contact items.
 * 
 * Represents a Contact list containing Contact items.
 * A Contact List is responsible for determining which of the fields from a
 * Contact are retained when a Contact is persisted into the List. A Contact List
 * does not have to retain all of the fields in a Contact when the Contact is
 * persisted into the List. See the Contact interface for a description of the
 * fields in a Contact. The fields that are supported by a particular Contact List
 * are queried through the methods <A HREF="../../../javax/microedition/pim/PIMList.html#isSupportedField(int)">
 * <CODE>PIMList.isSupportedField(int)</CODE></A> and <A HREF="../../../javax/microedition/pim/PIMList.html#getSupportedAttributes(int)">
 * <CODE>PIMList.getSupportedAttributes(int)</CODE></A>. If a field ID that is
 * not in the Contact interface is provided as the parameter to the <A HREF="../../../javax/microedition/pim/PIMList.html#isSupportedField(int)">
 * <CODE>PIMList.isSupportedField(int)</CODE></A> method, a <code>java.lang.IllegalArgumentException</code>
 * is thrown. The <A HREF="../../../javax/microedition/pim/PIMList.html#isSupportedField(int)">
 * <CODE>PIMList.isSupportedField(int)</CODE></A> method for a Contact List
 * also accepts field type values (e.g. <code>TYPE_*</code> constants) as valid
 * parameters for checking for support.
 * </P>
 * <h3>Inherited Method Behavior</h3>
 * A ContactList only accepts objects implementing the Contact interface as a
 * parameter to <A HREF="../../../javax/microedition/pim/PIMList.html#items(javax.microedition.pim.PIMItem)">
 * <CODE>PIMList.items(PIMItem)</CODE></A>). A <code>java.lang.IllegalArgumentException</code>
 * is thrown by this method if the input parameter does not implement the Contact
 * interface.
 * </P>
 * Enumerations returned by <A HREF="../../../javax/microedition/pim/PIMList.html#items()">
 * <CODE>PIMList.items()</CODE></A> and <A HREF="../../../javax/microedition/pim/PIMList.html#items(javax.microedition.pim.PIMItem)">
 * <CODE>PIMList.items(PIMItem)</CODE></A> in a Contact List contain only
 * objects implementing a Contact interface.</P>
 */
public interface ContactList extends PIMList
{
	

	/**
	 * 
	 * Factory method to create a Contact for this contact list. The Contact is empty
	 * upon creation with none of its fields containing any data (i.e. a call to the
	 * method <code>Contact.getFields()</code> returns an array of zero length). Even
	 * though it is initially empty, the Contact is <i>capable</i> of containing data
	 * for exactly those fields that this list supports. Note that creation of the
	 * Contact does not add the Contact to the list from which the item was created; a
	 * specific call to <code>PIMItem.commit()</code> must be made to commit the item
	 * and its data to the list.
	 * <P></P>
	 * 
	 * 
	 * 
	 * @return a new, empty Contact object associated with this list. However, the Contact is still not persistent in the list until a call to PIMItem.commit() for the Contact is made.
	 */
	public Contact createContact();

	/**
	 * 
	 * Imports the given Contact into this contact list by making a new Contact for
	 * the list and filling its information with as much information as it can from
	 * the provided Contact. If the input Contact is already in the list, a new
	 * Contact is still created with information similar to the input item (but not
	 * necessarily identical).
	 * <P></P>
	 * <P>Note that not all data from the input Contact may be supported in the new
	 * Contact due to field restrictions for the list instance. In this case, data
	 * fields not supported are not transferred to the new Contact object.
	 * </P>
	 * <P>Also note that creation of the Contact does not add the Contact to this list; a
	 * specific call to <code>PIMItem.commit()</code> must be made to commit the item
	 * and its data to the list.
	 * </P>
	 * <P></P>
	 * 
	 * 
	 * 
	 * @param contact - the Contact to import into the list.
	 * @return a newly created Contact.
	 * @throws java.lang.NullPointerException - If contact is null.
	 */
	public Contact importContact( Contact contact);

	/**
	 * 
	 * Removes a specific Contact from the list. The item must already exist in the
	 * list for this method to succeed.
	 * <P></P>
	 * 
	 * 
	 * 
	 * @param contact - the Contact to be removed from the list.
	 * @throws PIMException - If an error occurs deleting the item or the list is no longer accessible or closed.
	 * @throws java.lang.NullPointerException - If contact is null.
	 * @throws java.lang.SecurityException - if the application is not given permission to write to the Contact list or the list is opened READ_ONLY.
	 */
	public void removeContact( Contact contact) throws PIMException;

}
