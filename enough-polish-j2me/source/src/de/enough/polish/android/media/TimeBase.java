//#condition polish.android
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Wed Jan 21 21:07:48 CET 2009
package de.enough.polish.android.media;

/**
 * 
 * A <code>TimeBase</code> is a constantly ticking source of time.
 * It measures the progress of time and
 * provides the basic means for synchronizing media playback for
 * <code>Player</code>s.
 * <p>
 * A <code>TimeBase</code> measures time in microseconds in
 * order to provide the necessary resolution for synchronization.
 * It is acknowledged that some implementations may not be able to
 * support time resolution in the microseconds range.  For such
 * implementations, the internal representation of time can be done
 * within their limits.
 * But the time reported via the API must be scaled to the microseconds
 * range.
 * <p>
 * <code>Manager.getSystemTimeBase</code> provides the default
 * <code>TimeBase</code> used by the system.
 */
public interface TimeBase
{
	/**
	 * Get the current time of this <code>TimeBase</code>.  The values
	 * returned must be non-negative and non-decreasing over time.
	 * <P>
	 * 
	 * 
	 * @return the current TimeBase time in microseconds.
	 */
	long getTime();

}
