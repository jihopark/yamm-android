package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
import com.teamyamm.yamm.app.network.GeocodeAPIService;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.pojos.YammPlace;
import com.teamyamm.yamm.app.util.LocationSearchHelper;
import com.teamyamm.yamm.app.util.YammPlacesListAdapter;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 10/2/14.
 */
public class MapActivity extends BaseActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{
    public final static int DEFAULT_ZOOM_LEVEL = 13;
    private final double DEFAULT_RADIUS = 1.5;


    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap map;
    private double x,y;
    private String dishName;
    private int dishId;
    private Dialog fullScreenDialog;
    private ListView list;
    private TextView descriptionText;
    private Button currentLocationText;
    private String currentLocation;

    private LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        currentLocationText = (Button) findViewById(R.id.current_location_text);
        descriptionText = (TextView) findViewById(R.id.current_page_description);

        initActivity();
        fullScreenDialog = createFullScreenDialog(MapActivity.this, getString(R.string.progress_dialog_message));
        setActionBarBackButton(true);
        setCurrentLocationText();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initActivity(){

        //Set Empty TextView
        TextView tv = (TextView) findViewById(R.id.empty_view_text);
        Spannable span = new SpannableString(getString(R.string.no_place_message));
        span.setSpan(new ForegroundColorSpan(Color.WHITE), 17, 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(span);

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

    private void setCurrentLocationText(){
        currentLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationSearchHelper.showLocationDialog(MapActivity.this);
            }
        });
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

        if (x==0 && y==0) {
            findCurrentLocation();
        }
    }

    private void findCurrentLocation() {
        Location mCurrentLocation = mLocationClient.getLastLocation();

        if (mCurrentLocation == null) {
            Log.e("MapActivity/findCurrentLocation", "Location Connected " + mLocationClient.isConnected());
            Log.e("MapActivity/findCurrentLocation", "Cannot get Location");
            x = 0;
            y = 0;
            makeYammToast(R.string.location_error, Toast.LENGTH_SHORT);
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
            finish();
        }
        else{
            x = mCurrentLocation.getLatitude();
            y = mCurrentLocation.getLongitude();
            setMapCamera(x, y);
            setTextViews(LocationSearchHelper.getAddressFromLocation(x, y, MapActivity.this));
        }
    }

    private void setTextViews(String place){
        currentLocation = place;
        if (place.equals("")){
            currentLocationText.setText("현재 위치");
        }
        else {
            currentLocationText.setText("현재 위치 : " + currentLocation);
        }
        descriptionText.setText("주변 " + dishName + "을(를) 드실 수 있는 음식점입니다");
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
        list = (ListView) findViewById(R.id.yamm_places_list);
        list.setEmptyView(findViewById(R.id.empty_view));
        YammAPIAdapter.getTokenService().getPlacesNearby(x,y, DEFAULT_RADIUS , dishId, new Callback<List<YammPlace>>() {
            @Override
            public void success(List<YammPlace> yammPlaces, Response response) {
                YammPlacesListAdapter adapter = new YammPlacesListAdapter(MapActivity.this, yammPlaces);
                list.setAdapter(adapter);
                Log.i("MapActivity/getPlacesNearBy/Success", "Succeeded in Getting " + yammPlaces.size() + " Places");
                fullScreenDialog.dismiss();
                addMarkers(yammPlaces);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                makeYammToast(R.string.unidentified_error_message, Toast.LENGTH_SHORT);
                Log.e("MapActivity/getPlacesNearBy/Failure", "Get Places Nearby Fail");
                finish();
            }
        });
    }

    private void addMarkers(List<YammPlace> places){
        for (YammPlace place : places){
            map.addMarker(new MarkerOptions()
            .position(new LatLng(place.lat, place.lng))
            .title(place.getName()));
        }
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

    public void changeLocation(String place){
        fullScreenDialog.show();

        if (place.equals(getString(R.string.place_pick_edit_text))){
            Log.d("LocationSearchHelper/changeLocation", "Setting Current Location");
            findCurrentLocation();
            return ;
        }

        //Perform Geocoding
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(GeocodeAPIService.googleGeocodeAPI)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        GeocodeAPIService service = restAdapter.create(GeocodeAPIService.class);
        service.getAddressFromLocation(place, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d("LocationSearchHelper/changeLocation", "GeoCoding Success");
                LatLng location = LocationSearchHelper.getLocationFromJson(YammAPIAdapter.responseToString(response));
                if (location==null){
                    fullScreenDialog.dismiss();
                    makeYammToast(R.string.geocoding_error,Toast.LENGTH_SHORT);
                    return ;
                }

                x = location.latitude;
                y = location.longitude;
                setMapCamera(x,y);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                fullScreenDialog.dismiss();
                makeYammToast(R.string.geocoding_error,Toast.LENGTH_SHORT);
            }
        });
        setTextViews(place);
    }
}
