package training.orsys.com.ltmgeolocation2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager _locationManager = null;
    private LocationListener _locationListener = null;

    private TextView latitude;
    private TextView longitude;
    private TextView altitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _locationManager = (LocationManager)this.getSystemService( Context.LOCATION_SERVICE );

        latitude = (TextView)findViewById( R.id.label_latitude );
        longitude = (TextView)findViewById( R.id.label_longitude );
        altitude = (TextView)findViewById( R.id.label_altitude );

        // Define a listener that responds to location updates
        _locationListener = new LocationListener() {
            public void onLocationChanged( Location location ) {
                Log.v( "ltm", "onLocationChanged" );

                latitude.setText( "Latitude : " + Double.valueOf( location.getLatitude() ).toString() );
                longitude.setText( "Longitude : " + Double.valueOf( location.getLongitude() ).toString() );
                altitude.setText( "Altitude : " + Double.valueOf( location.getAltitude() ).toString() );

                // geocoding ... need internet connection
                Geocoder myLocation = new Geocoder( MainActivity.this, Locale.getDefault() );

                TextView geocode = (TextView)findViewById(R.id.label_geocoding);

                try {
                    List<Address> myList = myLocation.getFromLocation( location.getLatitude(), location.getLongitude(), 1 );
                    Log.v( "ltm", "myList = " + myList.toString() );
                    geocode.setText(myList.toString());
                } catch (IOException e) {
                    geocode.setText(e.getLocalizedMessage());
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.v( "ltm", provider + " onStatusChanged" );
            }

            public void onProviderEnabled(String provider) {
                Log.v( "ltm", provider + " onProviderEnabled" );
            }

            public void onProviderDisabled(String provider) {
                Log.v( "ltm", provider + " onProviderDisabled" );
            }
        };

        // boutons
        Button b_request = (Button)findViewById(R.id.button_request);
        b_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        /*new AlertDialog.Builder(MainActivity.this).setCancelable(true).setIcon(R.mipmap.ic_launcher)
                                .setMessage("Nous avons besoin de votre autorisation")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).create()
                                .show();*/
                    }else {
                        //...
                    }
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                }else {
                    TextView permission = (TextView)findViewById(R.id.label_permission);
                    permission.setText("Permission donnée");
                }
            }
        });

        Button b_geolocation = (Button)findViewById(R.id.button_geolocation);
        b_geolocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, _locationListener);
                _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, _locationListener);
            }
        });
    }

    // Callback de requête de permission dynamique
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //if( requestCode == 0) {
        if( grantResults.length > 0 && requestCode == 1 ){
            //...
            Log.v("ltm", "callback reçue : " + permissions[0].toString() );
            TextView permission = (TextView)findViewById(R.id.label_permission);
            permission.setText("Permission donnée");
        }
    }
}



















