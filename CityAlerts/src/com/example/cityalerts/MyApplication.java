package com.example.cityalerts;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MyApplication extends Application implements LocationListener {
	
	LocationManager locationManager;
	
	@Override
	  public void onCreate() {
	    super.onCreate();
	    WebApiClient.getInstance();
	    
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
	    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
	    	locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 500, 0, this);
	    }
	    
	    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	    	locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 500, 0, this);
	    }
	  }
	
	public Location getLocation() {

		Location location = null;

		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				return location;
			}
		}

		else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				return location;
			}
		}
		return location;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onTerminate() {
		locationManager.removeUpdates(this);
	}
}
