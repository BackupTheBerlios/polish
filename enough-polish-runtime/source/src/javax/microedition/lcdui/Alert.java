// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Sun Feb 29 19:10:57 CET 2004
package javax.microedition.lcdui;

import de.enough.polish.ui.Screen;

/**
 * An alert is a screen that shows data to the user and waits for a certain
 * period of time before proceeding to the next
 * <code>Displayable</code>. An alert can
 * contain a text string and an image.
 * The intended use of <code>Alert</code> is to inform the user about
 * errors and other
 * exceptional conditions.
 * 
 * <P>The application can set the alert time to be infinity with
 * <code> setTimeout(Alert.FOREVER)</code>
 * in which case the <code>Alert</code> is considered to be <em>modal</em> and
 * the implementation provide a feature that allows the
 * user to &quot;dismiss&quot; the alert, whereupon the next
 * <code>Displayable</code>
 * is displayed as if the timeout had expired immediately.</P>
 * 
 * <P>If an application specifies an alert to be of a
 * timed variety <em>and</em> gives it too much content such that it must
 * scroll,
 * then it automatically becomes a modal alert.</P>
 * 
 * <P> An alert may have an <code>AlertType</code> associated with it
 * to provide an indication of the nature of the alert.
 * The implementation may use this type to play an
 * appropriate sound when the <code>Alert</code> is presented to the user.
 * See <A HREF="../../../javax/microedition/lcdui/AlertType.html#playSound(javax.microedition.lcdui.Display)"><CODE>AlertType.playSound()</CODE></A>.</P>
 * 
 * <P>An alert may contain an optional <code>Image</code>.  The
 * <code>Image</code> may be mutable or
 * immutable.  If the <code>Image</code> is mutable, the effect is as
 * if a snapshot of its
 * contents is taken at the time the <code>Alert</code> is constructed
 * with this <code>Image</code> and
 * when <code>setImage</code> is called with an <code>Image</code>.
 * This snapshot is used whenever the contents of the
 * <code>Alert</code> are to be
 * displayed.  Even if the application subsequently draws into the
 * <code>Image</code>, the
 * snapshot is not modified until the next call to <code>setImage</code>.  The
 * snapshot is <em>not</em> updated when the <code>Alert</code>
 * becomes current or becomes
 * visible on the display.  (This is because the application does not have
 * control over exactly when <code>Displayables</code> appear and
 * disappear from the
 * display.)</P>
 * 
 * <a name="indicator"></a>
 * <h3>Activity Indicators</h3>
 * 
 * <P>An alert may contain an optional <A HREF="../../../javax/microedition/lcdui/Gauge.html"><CODE>Gauge</CODE></A> object that is used as an
 * activity or progress indicator.  By default, an <code>Alert</code>
 * has no activity
 * indicator; one may be set with the <A HREF="../../../javax/microedition/lcdui/Alert.html#setIndicator(javax.microedition.lcdui.Gauge)"><CODE>setIndicator(javax.microedition.lcdui.Gauge)</CODE></A> method.
 * The <code>Gauge</code>
 * object used for the activity indicator must conform to all of the following
 * restrictions:</P>
 * 
 * <ul>
 * <li>it must be non-interactive;</li>
 * <li>it must not be owned by another container (<code>Alert</code>
 * or <code>Form</code>);</li>
 * <li>it must not have any <code>Commands</code>;</li>
 * <li>it must not have an <code>ItemCommandListener</code>;</li>
 * <li>it must not have a label (that is, its label must be
 * <code>null</code>;</li>
 * <li>its preferred width and height must both be unlocked; and</li>
 * <li>its layout value must be <code>LAYOUT_DEFAULT</code>.</li>
 * </ul>
 * 
 * <P>It is an error for the application to attempt to use a
 * <code>Gauge</code> object that
 * violates any of these restrictions.  In addition, when the
 * <code>Gauge</code> object is
 * being used as the indicator within an <code>Alert</code>, the
 * application is prevented
 * from modifying any of these pieces of the <code>Gauge's</code> state.</P>
 * 
 * <a name="commands"></a>
 * <h3>Commands and Listeners</h3>
 * 
 * <P>Like the other <code>Displayable</code> classes, an
 * <code>Alert</code> can accept <code>Commands</code>, which
 * can be delivered to a <code>CommandListener</code> set by the
 * application.  The <code>Alert</code>
 * class adds some special behavior for <code>Commands</code> and listeners.</P>
 * 
 * <P>When it is created, an <code>Alert</code> implicitly has the
 * special <code>Command</code>
 * <A HREF="../../../javax/microedition/lcdui/Alert.html#DISMISS_COMMAND"><CODE>DISMISS_COMMAND</CODE></A> present on it.  If the application adds any
 * other <code>Commands</code> to the <code>Alert</code>,
 * <code>DISMISS_COMMAND</code> is implicitly removed.  If the
 * application removes all other <code>Commands</code>,
 * <code>DISMISS_COMMAND</code> is implicitly
 * restored.  Attempts to add or remove <code>DISMISS_COMMAND</code>
 * explicitly are
 * ignored.  Thus, there is always at least one <code>Command</code>
 * present on an <code>Alert</code>.
 * </P>
 * 
 * <P>If there are two or more <code>Commands</code> present on the
 * <code>Alert</code>, it is
 * automatically turned into a modal <code>Alert</code>, and the
 * timeout value is always
 * <A HREF="../../../javax/microedition/lcdui/Alert.html#FOREVER"><CODE>FOREVER</CODE></A>.  The <code>Alert</code> remains on the display
 * until a <code>Command</code> is
 * invoked.  If the Alert has one Command (whether it is DISMISS_COMMAND or it
 * is one provided by the application), the <code>Alert</code> may have
 * the timed behavior
 * as described above.  When a timeout occurs, the effect is the same as if
 * the user had invoked the <code>Command</code> explicitly.</P>
 * 
 * <P>When it is created, an <code>Alert</code> implicitly has a
 * <code>CommandListener</code> called the
 * <em>default listener</em> associated with it.  This listener may be
 * replaced by an application-provided listener through use of the <A HREF="../../../javax/microedition/lcdui/Alert.html#setCommandListener(javax.microedition.lcdui.CommandListener)"><CODE>setCommandListener(javax.microedition.lcdui.CommandListener)</CODE></A> method.  If the application removes its listener by
 * passing <code>null</code> to the <code>setCommandListener</code> method,
 * the default listener is implicitly restored.</P>
 * 
 * <P>The <A HREF="../../../javax/microedition/lcdui/Display.html#setCurrent(javax.microedition.lcdui.Alert, javax.microedition.lcdui.Displayable)"><CODE>Display.setCurrent(Alert, Displayable)</CODE></A> method and the <A HREF="../../../javax/microedition/lcdui/Display.html#setCurrent(javax.microedition.lcdui.Displayable)"><CODE>Display.setCurrent(Displayable)</CODE></A> method (when called with an
 * <code>Alert</code>) define
 * special behavior for automatically advancing to another
 * <code>Displayable</code> after
 * the <code>Alert</code> is dismissed.  This special behavior occurs
 * only when the default
 * listener is present on the <code>Alert</code> at the time it is
 * dismissed or when a
 * command is invoked.  If the user invokes a <code>Command</code> and
 * the default listener
 * is present, the default listener ignores the <code>Command</code>
 * and implements the
 * automatic-advance behavior.</P>
 * 
 * <P>If the application has set its own <code>CommandListener</code>, the
 * automatic-advance behavior is disabled.  The listener code is responsible
 * for advancing to another <code>Displayable</code>.  When the
 * application has provided a
 * listener, <code>Commands</code> are invoked normally by passing
 * them to the listener's
 * <code>commandAction</code> method.  The <code>Command</code> passed
 * will be one of the
 * <code>Commands</code> present on the <code>Alert</code>: either
 * <code>DISMISS_COMMAND</code> or one of the
 * application-provided <code>Commands</code>.</P>
 * 
 * <P>The application can restore the default listener by passing
 * <code>null</code> to the <code>setCommandListener</code> method.</P>
 * 
 * <strong>Note:</strong> An application may set a <A HREF="../../../javax/microedition/lcdui/Ticker.html"><CODE>Ticker</CODE></A>
 * with <A HREF="../../../javax/microedition/lcdui/Displayable.html#setTicker(javax.microedition.lcdui.Ticker)"><CODE>Displayable.setTicker</CODE></A> on an
 * <code>Alert</code>, however it may not be displayed due to
 * implementation restrictions.
 * </P>
 * <HR>
 * 
 * 
 * @since MIDP 1.0
 */
public class Alert extends Screen
{
	/**
	 * <code>FOREVER</code> indicates that an <code>Alert</code> is
	 * kept visible until the user
	 * dismisses it.  It is used as a value for the parameter to
	 * <A HREF="../../../javax/microedition/lcdui/Alert.html#setTimeout(int)"><CODE>setTimeout()</CODE></A>
	 * to indicate that the alert is modal.  Instead of waiting for a
	 * specified period of time, a modal <code>Alert</code> will wait
	 * for the user to take
	 * some explicit action, such as pressing a button, before proceeding to
	 * the next <code>Displayable</code>.
	 * 
	 * <P>Value <code>-2</code> is assigned to <code>FOREVER</code>.</P></DL>
	 * 
	 */
	public static final int FOREVER = -2;

	/**
	 * A <code>Command</code> delivered to a listener to indicate that
	 * the <code>Alert</code> has been
	 * dismissed.  This Command is implicitly present an on
	 * <code>Alert</code> whenever
	 * there are no other Commands present.  The field values of
	 * <code>DISMISS_COMMAND</code> are as follows:
	 * 
	 * <ul>
	 * <li>label = &quot;&quot; (an empty string)</li>
	 * <li>type = Command.OK</li>
	 * <li>priority = 0</li>
	 * </ul>
	 * 
	 * <p>The label value visible to the application must be as specified
	 * above.  However, the implementation may display
	 * <code>DISMISS_COMMAND</code> to the
	 * user using an implementation-specific label.</p>
	 * 
	 * <p>Attempting to add or remove <code>DISMISS_COMMAND</code>
	 * from an <code>Alert</code> has no
	 * effect.  However, <code>DISMISS_COMMAND</code> is treated as an
	 * ordinary <code>Command</code> if
	 * it is used with other <code>Displayable</code> types.</p>
	 * 
	 * 
	 * 
	 * @since MIDP 2.0
	 */
	public static final Command DISMISS_COMMAND = new Command("", Command.OK, 0);

	//following variables are implicitely defined by getter- or setter-methods:
	private int defaultTimeout;
	private int timeout;
	private AlertType type;
	private Image image;
	//private Gauge indicator;
	private CommandListener commandListener;
	private final de.enough.polish.ui.StringItem message;

	/**
	 * Constructs a new, empty <code>Alert</code> object with the
	 * given title. If <code>null</code> is
	 * passed, the <code>Alert</code> will have no title.  Calling
	 * this constructor is
	 * equivalent to calling
	 * 
	 * <pre>
	 * <code>Alert(title, null, null, null)</code>
	 * </pre>
	 * 
	 * @param title - the title string, or null
	 * @see #Alert(String, String, Image, AlertType)
	 */
	public Alert( String title)
	{
		this( title, null, null, null );
	}

	/**
	 * Constructs a new <code>Alert</code> object with the given title,
	 * content
	 * string and image, and alert type.
	 * The layout of the contents is implementation dependent.
	 * The timeout value of this new alert is the same value that is
	 * returned by <code>getDefaultTimeout()</code>.
	 * The <code>Image</code> provided may either be mutable or immutable.
	 * The handling and behavior of specific <code>AlertTypes</code>
	 * is described in
	 * <A HREF="../../../javax/microedition/lcdui/AlertType.html"><CODE>AlertType</CODE></A>.  <code>null</code> is allowed as the value
	 * of the <code>alertType</code>
	 * parameter and indicates that the <code>Alert</code> is not to
	 * have a specific alert
	 * type.  <code>DISMISS_COMMAND</code> is the only
	 * <code>Command</code> present on the new
	 * <code>Alert</code>.  The <code>CommandListener</code>
	 * associated with the new <code>Alert</code> is the
	 * <em>default listener</em>.  Its behavior is described in more detail in
	 * the section <a href="#commands">Commands and Listeners</a>.
	 * 
	 * @param title - the title string, or null if there is no title
	 * @param alertText - the string contents, or null if there  is no string
	 * @param alertImage - the image contents, or null if there is no image
	 * @param alertType - the type of the Alert, or null if the Alert has no specific type
	 */
	public Alert( String title, String alertText, Image alertImage, AlertType alertType)
	{
		super( title, null, true ); // screen constructor
		this.message = new de.enough.polish.ui.StringItem( null, alertText );
		this.container.add( this.message );
	}

	/**
	 * Gets the default time for showing an <code>Alert</code>.  This
	 * is either a
	 * positive value, which indicates a time in milliseconds, or the special
	 * value
	 * <A HREF="../../../javax/microedition/lcdui/Alert.html#FOREVER"><CODE>FOREVER</CODE></A>,
	 * which indicates that <code>Alerts</code> are modal by default.  The
	 * value returned will vary across implementations and is presumably
	 * tailored to be suitable for each.
	 * 
	 * @return default timeout in milliseconds, or FOREVER
	 */
	public int getDefaultTimeout()
	{
		return this.defaultTimeout;
	}

	/**
	 * Gets the time this <code>Alert</code> will be shown.  This is
	 * either a positive
	 * value, which indicates a time in milliseconds, or the special value
	 * <code>FOREVER</code>, which indicates that this
	 * <code>Alert</code> is modal.  This value is not
	 * necessarily the same value that might have been set by the
	 * application
	 * in a call to <A HREF="../../../javax/microedition/lcdui/Alert.html#setTimeout(int)"><CODE>setTimeout(int)</CODE></A>.  In particular, if the
	 * <code>Alert</code> is made
	 * modal because its contents is large enough to scroll, the value
	 * returned by <code>getTimeout</code> will be <code>FOREVER</code>.
	 * 
	 * @return timeout in milliseconds, or FOREVER
	 * @see setTimeout(int)
	 */
	public int getTimeout()
	{
		return this.timeout;
	}

	/**
	 * Set the time for which the <code>Alert</code> is to be shown.
	 * This must either
	 * be a positive time value in milliseconds, or the special value
	 * <code>FOREVER</code>.
	 * 
	 * @param time - timeout in milliseconds, or FOREVER
	 * @throws IllegalArgumentException - if time is not positive and is not FOREVER
	 * @see getTimeout()
	 */
	public void setTimeout(int time)
	{
		this.timeout = time;
	}

	/**
	 * Gets the type of the <code>Alert</code>.
	 * 
	 * @return a reference to an instance of AlertType, or null if the Alert has no specific type
	 * @see setType(javax.microedition.lcdui.AlertType)
	 */
	public AlertType getType()
	{
		return this.type;
	}

	/**
	 * Sets the type of the <code>Alert</code>.
	 * The handling and behavior of specific <code>AlertTypes</code>
	 * is described in
	 * <A HREF="../../../javax/microedition/lcdui/AlertType.html"><CODE>AlertType</CODE></A>.
	 * 
	 * @param type - an AlertType, or null if the Alert has no specific type
	 * @see #getType()
	 */
	public void setType( AlertType type)
	{
		this.type = type;
	}

	/**
	 * Gets the text string used in the <code>Alert</code>.
	 * 
	 * @return the Alert's text string, or null  if there is no text
	 * @see #setString(java.lang.String)
	 */
	public String getString()
	{
		return this.message.getText();
	}

	/**
	 * Sets the text string used in the <code>Alert</code>.
	 * 
	 * <p>If the <code>Alert</code> is visible on the display when its
	 * contents are updated
	 * through a call to <code>setString</code>, the display will be
	 * updated with the new
	 * contents as soon as it is feasible for the implementation to do so.
	 * </p>
	 * 
	 * @param str - the Alert's text string, or null if there is no text
	 * @see #getString()
	 */
	public void setString( String str)
	{
		this.message.setText(str);
	}

	/**
	 * Gets the <code>Image</code> used in the <code>Alert</code>.
	 * 
	 * @return the Alert's image, or null  if there is no image
	 * @see setImage(javax.microedition.lcdui.Image)
	 */
	public Image getImage()
	{
		return this.image;
	}

	/**
	 * Sets the <code>Image</code> used in the <code>Alert</code>.
	 * The <code>Image</code> may be mutable or
	 * immutable.  If <code>img</code> is <code>null</code>, specifies
	 * that this <code>Alert</code> has no image.
	 * If <code>img</code> is mutable, the effect is as if a snapshot is taken
	 * of <code>img's</code> contents immediately prior to the call to
	 * <code>setImage</code>.  This
	 * snapshot is used whenever the contents of the
	 * <code>Alert</code> are to be
	 * displayed.  If <code>img</code> is already the
	 * <code>Image</code> of this <code>Alert</code>, the effect
	 * is as if a new snapshot of img's contents is taken.  Thus, after
	 * painting into a mutable image contained by an <code>Alert</code>, the
	 * application can call
	 * 
	 * <TABLE BORDER="2">
	 * <TR>
	 * <TD ROWSPAN="1" COLSPAN="1">
	 * <pre><code>
	 * alert.setImage(alert.getImage());    </code></pre>
	 * </TD>
	 * </TR>
	 * </TABLE>
	 * <p>to refresh the <code>Alert's</code> snapshot of its
	 * <code>Image</code>.</p>
	 * 
	 * <p>If the <code>Alert</code> is visible on the display when its
	 * contents are updated
	 * through a call to <code>setImage</code>, the display will be
	 * updated with the new
	 * snapshot as soon as it is feasible for the implementation to do so.
	 * </p>
	 * 
	 * @param img - the Alert's image, or null if there is no image
	 * @see getImage()
	 */
	public void setImage( Image img)
	{
		this.image = img;
	}

	/**
	 * Sets an activity indicator on this <code>Alert</code>.  The
	 * activity indicator is a
	 * <A HREF="../../../javax/microedition/lcdui/Gauge.html"><CODE>Gauge</CODE></A> object.  It must be in a restricted state in order for it
	 * to be used as the activity indicator for an <code>Alert</code>.
	 * The restrictions
	 * are listed <a href="#indicator">above</a>.  If the
	 * <code>Gauge</code> object
	 * violates any of these restrictions,
	 * <code>IllegalArgumentException</code> is thrown.
	 * 
	 * <p>If <code>indicator</code> is <code>null</code>, this removes any
	 * activity indicator present on this <code>Alert</code>.</p>
	 * 
	 * @param indicator - the activity indicator for this Alert, or null if there is to be none
	 * @throws IllegalArgumentException - if indicator does not meet the restrictions for its use in an Alert
	 * @see getIndicator()
	 * @since  MIDP 2.0
	public void setIndicator( Gauge indicator)
	{
		this.indicator = indicator;
	}
	 */

	/**
	 * Gets the activity indicator for this <code>Alert</code>.
	 * 
	 * @return a reference to this Alert's activity indicator, or null if there is none
	 * @see setIndicator(javax.microedition.lcdui.Gauge)
	 * @since  MIDP 2.0
	public Gauge getIndicator()
	{
		return this.indicator;
	}
	 */

	/**
	 * Similar to <A HREF="../../../javax/microedition/lcdui/Displayable.html#addCommand(javax.microedition.lcdui.Command)"><CODE>Displayable.addCommand(javax.microedition.lcdui.Command)</CODE></A>, however when the
	 * application first adds a command to an <code>Alert</code>,
	 * <A HREF="../../../javax/microedition/lcdui/Alert.html#DISMISS_COMMAND"><CODE>DISMISS_COMMAND</CODE></A> is implicitly removed.  Calling this
	 * method with <code>DISMISS_COMMAND</code> as the parameter has
	 * no effect.
	 * 
	 * @param cmd - the command to be added
	 * @throws NullPointerException - if cmd is null
	 * @see addCommand in class Displayable
	 */
	public void addCommand( Command cmd)
	{
		//TODO implement addCommand
	}

	/**
	 * Similar to <A HREF="../../../javax/microedition/lcdui/Displayable.html#removeCommand(javax.microedition.lcdui.Command)"><CODE>Displayable.removeCommand(javax.microedition.lcdui.Command)</CODE></A>, however when the
	 * application removes the last command from an
	 * <code>Alert</code>, <A HREF="../../../javax/microedition/lcdui/Alert.html#DISMISS_COMMAND"><CODE>DISMISS_COMMAND</CODE></A> is implicitly
	 * added.  Calling this method with <code>DISMISS_COMMAND</code>
	 * as the parameter has no effect.
	 * 
	 * @param cmd - the command to be removed
	 * @see removeCommand in class Displayable
	 */
	public void removeCommand( Command cmd)
	{
		//TODO implement removeCommand
	}

	/**
	 * The same as <A HREF="../../../javax/microedition/lcdui/Displayable.html#setCommandListener(javax.microedition.lcdui.CommandListener)"><CODE>Displayable.setCommandListener(javax.microedition.lcdui.CommandListener)</CODE></A> but with the
	 * following additional semantics.  If the listener parameter is
	 * <code>null</code>, the <em>default listener</em> is restored.
	 * See <a href="#commands">Commands and Listeners</a> for the definition
	 * of the behavior of the default listener.
	 * 
	 * @param l - the new listener, or null
	 * @see setCommandListener in class Displayable
	 */
	public void setCommandListener( CommandListener l)
	{
		this.commandListener = l;
	}
	
	
	
	//#ifdef polish.useDynamicStyles	
	/**
	 * Retrieves the CSS selector for this screen.
	 * The CSS selector is used for the dynamic assignment of styles -
	 * that is the styles are assigned by the usage of the screen and
	 * not by a predefined style-name.
	 * With the #style preprocessing command styles are set in a static way, this method
	 * yields in a faster GUI and is recommended. When in a style-sheet
	 * dynamic styles are used, e.g. "form>p", than the selector of the
	 * screen is needed.
	 * This abstract method needs only be implemented, when dynamic styles
	 * are used: #ifdef polish.useDynamicStyles
	 * 
	 * @return the name of the appropriate CSS Selector for this screen.
	 */
	protected String createCssSelector() {
		return "alert";
	}
	//#endif



}
