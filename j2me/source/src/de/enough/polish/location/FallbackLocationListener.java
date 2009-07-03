//#condition polish.api.location
package de.enough.polish.location;
import javax.microedition.location.Criteria;

public interface FallbackLocationListener{

	public void providerEnabled(Criteria criteria);

}