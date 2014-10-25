package com.teamyamm.yamm.app;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.teamyamm.yamm.app.pojos.YammPlace;

/**
 * Created by parkjiho on 10/13/14.
 */
public class PlaceActivity extends BaseActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final double DEFAULT_RADIUS = 1.5;



    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap map;
    public static final String YAMM_PLACE = "YP";
    private YammPlace place;
    private LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        initActivity();
        mLocationClient = new LocationClient(this, this, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.place_activity_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.close_button:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void initActivity(){
        Gson gson = new Gson();

        place = gson.fromJson(getIntent().getExtras().getString(YAMM_PLACE), YammPlace.class);
        if (place==null){
            Log.e("PlaceActivity/initActivity","Cannot init Place Activity");
            finish();
        }

        setTitle(place.name);

        TextView tv = (TextView) findViewById(R.id.name_text);
        tv.setText(place.name);

        tv = (TextView) findViewById(R.id.address_text);
        tv.setText(place.address);

        tv = (TextView) findViewById(R.id.phone_text);
        tv.setText(place.phone);

        if (place.phone.equals(""))
            tv.setVisibility(View.GONE);
        else
            tv.setOnClickListener(createPhoneIntentListener());

        tv = (TextView) findViewById(R.id.type_text);
        tv.setText(place.type);

        tv = (TextView) findViewById(R.id.distance_text);
        tv.setText(place.getDistanceString());

        tv = (TextView) findViewById(R.id.menu_text);
        tv.setText(place.getDishesString());

        map =  ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        if (map!=null) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(place.lat, place.lng)).zoom(MapActivity.DEFAULT_ZOOM_LEVEL+1).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(place.lat, place.lng))
                    .title(place.name));
        }
    }

    private View.OnClickListener createPhoneIntentListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+place.phone));
                startActivity(intent);
            }
        };
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Log.d("MapActivity/onConnected", "Location Service Connected");
        if (map!=null)
            map.setMyLocationEnabled(true);
    }

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