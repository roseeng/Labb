package se.roseen.testapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        // 1 is a integer which will return the result in onRequestPermissionsResult

        /********** get Gps location service LocationManager object ***********/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Parameters :
        //   First(provider)    :  the name of the provider with which to register
        //   Second(minTime)    :  the minimum time interval for notifications,
        //                         in milliseconds. This field is only used as a hint
        //                         to conserve power, and actual time between location
        //                         updates may be greater or lesser than this value.
        //   Third(minDistance) :  the minimum distance interval for notifications, in meters
        //   Fourth(listener)   :  a {#link LocationListener} whose onLocationChanged(Location)
        //                         method will be called for each location update
/*
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000,   // 3 sec
                    10, this);

            Log.i("TESTAPP", "requestLocationUpdates OK");
            Log.i("TESTAPP", "requestLocationUpdates OK");
            Log.i("TESTAPP", "requestLocationUpdates OK");

        } catch (SecurityException ex) {
            Log.e("TESTAPP", "requestLocationUpdates failed. \n" + ex.getMessage());
            Log.e("TESTAPP", "requestLocationUpdates failed. \n" + ex.getMessage());
            Log.e("TESTAPP", "requestLocationUpdates failed. \n" + ex.getMessage());
        }
*/
        Log.d("TESTAPP", "Starting service...");
        Intent intent = new Intent(this, ExampleService.class);
        startService(intent);

        Log.d("TESTAPP", "onCreate done.");
    }

    public void onButtonClick(android.view.View view) {
        Log.i("TESTAPP", "Button clicked!");

        //locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, );
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String str = "Latitude: "+location.getLatitude()+"\nLongitude: "+location.getLongitude();

        Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
    }

    /************* Called after each 3 sec **********/
    @Override
    public void onLocationChanged(Location location) {

        String str = "Latitude: "+location.getLatitude()+"\nLongitude: "+location.getLongitude();

        Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        Toast.makeText(getBaseContext(), "Gps Status Changed:  " + status, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {

        /******** Called when User off Gps *********/

        Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        /******** Called when User on Gps  *********/

        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"GPS permission granted", Toast.LENGTH_LONG).show();

                    //  get Location from your device by some method or code

                } else {
                    // show user that permission was denied. inactive the location based feature or force user to close the app
                    Toast.makeText(this,"GPS permission NOT granted!!", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
}
