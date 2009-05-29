//#condition polish.android
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Tue Mar 24 07:57:17 EET 2009
package de.enough.polish.android.location;

/**
 * This interface represents a listener to events associated with
 * detecting proximity to some registered coordinates.
 * Applications implement this interface and
 * register it with a static method in <code>LocationProvider</code>
 * to obtain notfications
 * when proximity to registered coordinates is detected.
 * 
 * <p>This listener is called when the terminal enters the proximity of the
 * registered coordinates. The proximity is defined as the proximity
 * radius around the coordinates combined with the horizontal accuracy
 * of the current sampled location.
 * </p>
 * <p>The listener is called only once when the terminal enters the
 * proximity of the registered coordinates. The registration with
 * these coordinates is cancelled when the listener is called.
 * If the application wants to be notified again about these coordinates,
 * it must re-register the coordinates and the listener.
 * </p>
 */
public interface ProximityListener
{
	/**
	 * After registering this listener with the <code>LocationProvider</code>,
	 * this method will be called by the platform when the implementation
	 * detects that the current location of the terminal is within the
	 * defined proximity radius of the registered coordinates.
	 * <P>
	 * 
	 * @param coordinates - the registered coordinates to which proximity has been detected
	 * @param location - the current location of the terminal
	 */
	public void proximityEvent( Coordinates coordinates, Location location);

	/**
	 * Called to notify that the state of the proximity monitoring
	 * has changed.
	 * <p>These state changes are delivered to the application
	 * as soon as possible after the state of the monitoring changes.
	 * </p>
	 * <p>Regardless of the state, the <code>ProximityListener</code>
	 * remains registered until the application explicitly
	 * removes it with <code>LocationProvider.removeProximityListener</code>
	 * or the application exits.
	 * </p>
	 * <p>These state changes may be related to state changes
	 * of some location providers, but this is implementation dependent
	 * as implementations can freely choose the method used to implement
	 * this proximity monitoring.
	 * </p>
	 * <P>
	 * 
	 * @param isMonitoringActive - a boolean indicating the new state of the proximity monitoring. true indicates that the proximity monitoring is active and false indicates that the proximity monitoring can't be done currently.
	 */
	public void monitoringStateChanged(boolean isMonitoringActive);

}
