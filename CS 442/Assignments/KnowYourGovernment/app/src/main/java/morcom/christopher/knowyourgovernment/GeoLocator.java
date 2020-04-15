package morcom.christopher.knowyourgovernment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;

public class GeoLocator {

    private MainActivity mainActivity;
    public LocationManager locationManager;
    public LocationListener locationListener;

    private static final String TAG = "GeoLocator";
    
    public GeoLocator(MainActivity m) {
        mainActivity = m;

        if (checkPermission()) {
            onCreateLocationManager();
            determineLocation();
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            Log.d(TAG, "checkPermission: Access BAD. Requesting...");
            return false;
        }
        Log.d(TAG, "checkPermission: access GOOD");
        return true;
    }

    public void onCreateLocationManager() {
        if (!checkPermission())
            return;

        locationManager = (LocationManager) mainActivity.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mainActivity.getLocationData(location.getLatitude(), location.getLongitude());
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    public void determineLocation() {

        if (!checkPermission()) return;
        if (locationManager == null) {
            onCreateLocationManager();
        }
        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //Network Provided
            if (loc != null) {
                mainActivity.getLocationData(loc.getLatitude(), loc.getLongitude());
                return;
            }
        }
        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER); //Passive Location
            if (loc != null) {
                mainActivity.getLocationData(loc.getLatitude(), loc.getLongitude());
                return;
            }
        }
        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //GPS Provided
            if (loc != null) {
                mainActivity.getLocationData(loc.getLatitude(), loc.getLongitude());
                return;
            }
        }
        Toast.makeText(this.mainActivity, "No location providers available.", Toast.LENGTH_LONG).show();
    }

    public void shutdown() {
        locationManager.removeUpdates(locationListener);
        locationManager = null;
    }
}
