// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sun Feb 29 19:10:56 CET 2004
package javax.microedition.lcdui;

/**
 * A listener type for receiving notification of commands that have been
 * invoked on <A HREF="../../../javax/microedition/lcdui/Item.html"><CODE>Item</CODE></A> objects.  An <code>Item</code> can have
 * <code>Commands</code> associated with
 * it.  When such a command is invoked, the application is notified by having
 * the <A HREF="../../../javax/microedition/lcdui/ItemCommandListener.html#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Item)"><CODE>commandAction()</CODE></A> method called on the
 * <code>ItemCommandListener</code> that had been set on the
 * <code>Item</code> with a call to
 * <A HREF="../../../javax/microedition/lcdui/Item.html#setItemCommandListener(javax.microedition.lcdui.ItemCommandListener)"><CODE>setItemCommandListener()</CODE></A>.
 * <HR>
 * 
 * 
 * @since MIDP 2.0
 */
public interface ItemCommandListener
{
	/**
	 * Called by the system to indicate that a command has been invoked on a
	 * particular item.
	 * 
	 * @param c - the Command that was invoked
	 * @param item - the Item on which the command was invoked
	 */
	public void commandAction( Command c, Item item);

}
