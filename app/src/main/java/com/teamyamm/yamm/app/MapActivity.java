package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.teamyamm.yamm.app.pojos.YammPlace;
import com.teamyamm.yamm.app.util.LocationSearchHelper;
import com.teamyamm.yamm.app.util.YammPlacesListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private Dialog fullScreenDialog;

    private LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initActivity();
        setTitle(dishName + " 음식점 보기");
        fullScreenDialog = createFullScreenDialog(MapActivity.this, getString(R.string.progress_dialog_message));
        setActionBarBackButton(true);

        mLocationClient = new LocationClient(this, this, this);
        fullScreenDialog.show();
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
        fullScreenDialog.dismiss();
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

        if (x==0 && y==0) {
            x = mCurrentLocation.getLatitude();
            y = mCurrentLocation.getLongitude();
            setMapCamera(x, y);

        }
    }

    private void setMapCamera(double xx, double yy){
        Log.i("MapActivity/setCameraMap", "Camera Set for x,y:" + xx + "," + yy);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(xx, yy)).zoom(DEFAULT_ZOOM_LEVEL).build();
        if (map!=null) {
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            loadPlaces();
        }
    }

    private void loadPlaces(){
        List<YammPlace> places = new ArrayList<YammPlace>();
        places.add(new YammPlace(2, "짬뽕집", "경기도 고양시 2", 0.5, 1, 1));
        places.add(new YammPlace(1, "설렁탕집", "경기도 고양시", 0.3, 1, 1));
        places.add(new YammPlace(3, "짜장면집", "경기도 고양시 3", 0.6, 1,1) );
        places.add(new YammPlace(4, "이상한집", "경기도 파주시", 0.8,1,1));
        places.add(new YammPlace(5, "이상한집2", "경기도 파주시", 0.2,1,1));
        places.add(new YammPlace(6, "이상한집3", "경기도 파주시", 0.1,1,1));
        places.add(new YammPlace(7, "이상한집4", "경기도 파주시", 1.1,1,1));


        Collections.sort(places);
        ListView list = (ListView) findViewById(R.id.yamm_places_list);
        YammPlacesListAdapter adapter = new YammPlacesListAdapter(MapActivity.this, places);
        list.setAdapter(adapter);
        fullScreenDialog.dismiss();
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
