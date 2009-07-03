//#condition polish.api.location
package de.enough.polish.location;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

public class FallbackLocationProvider extends LocationProvider {

	public void setFallbackLocationListener(FallbackLocationListener fallbackLocationListener) {
		// TODO: 
	}

	@Override
	public Location getLocation(int arg0) throws LocationException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLocationListener(LocationListener arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
}