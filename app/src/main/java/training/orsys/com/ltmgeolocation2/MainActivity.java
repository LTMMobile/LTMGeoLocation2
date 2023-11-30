package training.orsys.com.ltmgeolocation2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.GnssAntennaInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationListenerCompat;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
/** */
public class MainActivity extends AppCompatActivity  {
    private LocationManager locationManager = null;
    private LocationListener locationListener;

    private TextView latitude;
    private TextView longitude;
    private TextView altitude;
    private double latitudeVal;
    private double longitudeVal;
    private double altitudeVal;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this <-> Context
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        latitude = findViewById( R.id.label_latitude ); // java objects created from UI in xml
        longitude = findViewById( R.id.label_longitude );
        altitude = findViewById( R.id.label_altitude );

        List<String> tabProviders = locationManager.getAllProviders();
        Log.v("ltm", tabProviders.toString());

         // Define a listener that responds to location updates
        locationListener = new LocationListenerCompat() {
            public void onLocationChanged( Location location ) {
                Log.v( "ltm", "onLocationChanged" );

                altitudeVal = location.getAltitude();
                latitudeVal = location.getLatitude();
                longitudeVal = location.getLongitude();

                latitude.setText( String.format(Locale.getDefault(), "Latitude : %f", location.getLatitude()) );
                longitude.setText( String.format(Locale.getDefault(),"Longitude : %f", location.getLongitude()) );
                altitude.setText( String.format(Locale.getDefault(),"Altitude : %f", location.getAltitude()) );

                // geocoding ... need internet connection
                Geocoder geocoder = new Geocoder( MainActivity.this, Locale.getDefault() );
                TextView geocode = findViewById(R.id.label_geocoding);

                try {
                    List<Address> myList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Log.v( "ltm", "myList = " + myList.toString() );
                    geocode.setText(myList.toString());
                }catch (IOException e) {
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

        // Bouton demande permission
        Button b_request = findViewById(R.id.button_request);
        b_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                }else {
                    TextView permission = findViewById(R.id.label_permission);
                    permission.setText("Permission donnée");
                }
            }
        });

        Button b_geolocation = findViewById(R.id.button_geolocation);
        b_geolocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    try {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    }catch(IllegalArgumentException ex){
                        ex.printStackTrace();
                    }
                }
                else {
                    TextView permission = findViewById(R.id.label_permission);
                    permission.setText("Permission non donnée");
                }
                }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.bouton_map) {
            if( latitudeVal != 0.0 && longitudeVal != 0.0 ) {
                String t = String.format( Locale.getDefault(), "geo:%f,%f", latitudeVal, longitudeVal);
                Log.v("ltm", t );
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(t));
                startActivity(i);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( grantResults.length > 0 && requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
            //...
            Log.v("ltm", "callback reçue : " + permissions[0]);
            TextView permission = findViewById(R.id.label_permission);
            permission.setText("Permission donnée");
        }if( grantResults.length > 0 && requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED ){
            TextView permission = findViewById(R.id.label_permission);
            permission.setText("Permission refusée");
        }
    }
}