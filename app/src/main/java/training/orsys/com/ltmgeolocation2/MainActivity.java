package training.orsys.com.ltmgeolocation2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnDoubleTapListener,
        GestureDetector.OnGestureListener {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private GestureDetectorCompat gestureDetector;

    private TextView latitude;
    private TextView longitude;
    private TextView altitude;
    private double latitudeVal;
    private double longitudeVal;
    private double altitudeVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // GestureDectector
        gestureDetector = new GestureDetectorCompat(this,this);

        locationManager = (LocationManager)getSystemService( Context.LOCATION_SERVICE );

        latitude = findViewById( R.id.label_latitude ); // java objects created from UI in xml
        longitude = findViewById( R.id.label_longitude );
        altitude = findViewById( R.id.label_altitude );

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
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
                    List<Address> myList = geocoder.getFromLocation( location.getLatitude(), location.getLongitude(), 1 );
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

        // Boutons
        Button b_request = findViewById(R.id.button_request);
        b_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    /*if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    }else {
                        //...
                    }*/
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                    //....

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
                        == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    try {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
                    }catch(IllegalArgumentException ex){
                        ex.printStackTrace();
                    }catch(Exception ex){
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    // implements GestureDetector.OnDoubleTapListener
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Toast.makeText(this, "DoubleTap detected", Toast.LENGTH_SHORT);
        if( latitudeVal != 0.0 && longitudeVal != 0.0 ) {
            String t = String.format( Locale.getDefault(), "geo:%f,%f", latitudeVal, longitudeVal);
            Log.v("ltm", t );
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(t));
            startActivity(i);
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Toast.makeText(this, "SingleTapUp detected", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(this.gestureDetector.onTouchEvent(event))
            return true;

        return super.onTouchEvent(event);
    }
}



















