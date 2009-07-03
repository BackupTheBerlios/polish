//#condition polish.api.location
package de.enough.polish.location;

import javax.microedition.location.Criteria;

public class LocationService {

	public static final Criteria CRITERIA_GPS_PROVIDER = null;
	public static final Criteria CRITERIA_NETWORK_PROVIDER = null;

	/**
	 * Returns a FallbackLocationProvider object.
	 * 
	 * @param criteria
	 *            The value is a array containing all Critiera objects which
	 *            should be used in the given order. If the location provider
	 *            fitting the first criteria is not available anymore, the next
	 *            criteria is used to find an available location provider.
	 * @return a FallbackLocationProvider object which will use location
	 *         providers in the order of the given criteria. Returns nulli if
	 *         non of the criteria matches a location provider.
	 */
	public static FallbackLocationProvider getFallbackLocationProvider(Criteria[] criteria) {
		return null;
	}

	/**
	 * Informs the caller about the status of the GPS device.
	 * 
	 * @return The value is 'true' if the GPS device is enabled on the device
	 *         and can be used. This does not say anything about the capability
	 *         of the GPS device to actually get a fix. The value 'false' is
	 *         returned if the GPS device is disabled.
	 */
	public static boolean isGpsEnabled() {
		return false;
	}
}