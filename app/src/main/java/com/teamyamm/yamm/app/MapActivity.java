package com.teamyamm.yamm.app;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.teamyamm.yamm.app.util.LocationSearchHelper;

/**
 * Created by parkjiho on 10/2/14.
 */
public class MapActivity extends BaseActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{
    private final int DEFAULT_ZOOM_LEVEL = 14;

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap map;
    private double x,y;
    private String dishName;
    private int dishId;

    private LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initActivity();
        TextView title = (TextView) findViewById(R.id.map_title);
        title.setText(dishName);

        mLocationClient = new LocationClient(this, this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    @Override
    protected void onPause() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();

        initMap();
    }

    private void initActivity(){
        x = getIntent().getExtras().getDouble(LocationSearchHelper.LANG);
        y = getIntent().getExtras().getDouble(LocationSearchHelper.LONG);

        dishId = getIntent().getExtras().getInt(LocationSearchHelper.DISH_ID);
        dishName = getIntent().getExtras().getString(LocationSearchHelper.DISH_NAME);

        if (dishId == 0 || dishName == null){
            Log.e("MapActivity/initActivity", "Dish isn't Set.");
            finish();
        }
        if (!(x==0 && y==0)){
            if (mLocationClient!=null) {
                mLocationClient.disconnect();
                Log.i("MapActivity/initActivity", "LocationClient Disconnected");
            }
        }
        Log.i("MapActivity/initActivity", "Dish: " + dishName + " x,y:" + x + "," + y);
    }

    private void initMap(){
        if (map==null){
            map =  ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
            if (map!=null) {
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.setMyLocationEnabled(true);
                if (!(x==0 && y==0))
                    setMapCamera(x,y);
            }

        }
    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Log.d("MapActivity/onConnected", "Location Service Connected");
        Location mCurrentLocation = mLocationClient.getLastLocation();

        if (x==0 && y==0)
            setMapCamera(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
    }

    private void setMapCamera(double xx, double yy){
        Log.i("MapActivity/setCameraMap", "Camera Set for x,y:" + xx + "," + yy);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(xx, yy)).zoom(DEFAULT_ZOOM_LEVEL).build();
        if (map!=null)
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Log.d("MapActivity/onDisconnected", "Location Service Disconnected");

    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
            */
            makeYammToast(R.string.location_api_error, Toast.LENGTH_SHORT);
        }
    }
}
