package com.teamyamm.yamm.app.util;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.teamyamm.yamm.app.MapActivity;
import com.teamyamm.yamm.app.R;
import com.teamyamm.yamm.app.network.GeocodeAPIService;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.pojos.DishItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 9/23/14.
 */
public class LocationSearchHelper {
    public final static String LANG = "LANG";
    public final static String LONG = "LONG";
    public final static String DISH_NAME = "name";
    public final static String DISH_ID = "id";


    private final static long LOCATION_MIN_TIME = 200; //0.1sec
    private final static float LOCATION_MIN_DISTANCE = 1.0f; //1 meters

    private static LocationManager manager = null;
    private static LocationListener listener = null;

    private static DishItem mItem;
    private static Context mContext;

    public static void initLocationSearchHelper(LocationManager m){
        manager = m;
        listener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.i("LocationSearchHelper/initLocationSearchHelper", "Location Changed " + location.toString());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
    }

    public static void searchMap(DishItem item, String input, Context context){
      /*  RawURI raw = getLocationURI(item, input, context);
        Uri location = raw.uri;
        if (location == null){
            if (context instanceof BaseActivity) {
                ((BaseActivity) context).makeYammToast(context.getString(R.string.location_error), Toast.LENGTH_SHORT);
                location = Uri.parse("geo:0,0?q=" + item.getName());
            }
        }*/
        double x=0, y=0;
        mItem = item;
        mContext = context;

        if (!input.equals(context.getString(R.string.place_pick_edit_text))){
            //만약 custom 주소일 시
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(GeocodeAPIService.googleGeocodeAPI)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            GeocodeAPIService service = restAdapter.create(GeocodeAPIService.class);
            service.getAddressFromLocation(input, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    Log.d("LocationSearchHelper/getAddressFromLocation", "GeoCoding Success");
                    LatLng location = getLocationFromJson(YammAPIAdapter.responseToString(response));
                    if (location!=null){
                        startMapActivity(location.latitude, location.longitude);
                    }
                }

                @Override
                public void failure(RetrofitError retrofitError) {

                }
            });
        }
        else {
            startMapActivity(x,y);
        }
        /*Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

        //Verify Intent
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
        boolean isIntentSafe = activities.size() > 0;


        if (isIntentSafe)
            context.startActivity(mapIntent);
        else
            Log.e("LocationSearchHelper/searchMap", "Intent not safe");
        */
//        return raw.place;
    }

    public static void startMapActivity(double x, double y){
        Intent mapIntent = new Intent(mContext, MapActivity.class);
        mapIntent.putExtra(LANG, x);
        mapIntent.putExtra(LONG, y);
        mapIntent.putExtra(DISH_NAME, mItem.getName());
        mapIntent.putExtra(DISH_ID, mItem.getId());
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        mContext.startActivity(mapIntent);
        mContext = null;
        mItem = null;
    }


    public static LatLng getLocationFromJson(String json){
        JSONObject jsonObject;
        double lng=0, lat=0;

        try {
            jsonObject = new JSONObject(json);

            lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            Log.d("LocationSearchHelper/getLocationFromJson", "Lat: " + lat);
            Log.d("LocationSearchHelper/getLocationFromJson", "Long: " + lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new LatLng(lat, lng);
    }

    /*private static RawURI getLocationURI(DishItem item, String input, Context context){
        int count = 0;
        String default_provider = LocationManager.NETWORK_PROVIDER; //default provider
        String place = input;

        if (manager == null)
            return new RawURI(null,null);

        Location lastKnownLocation;
        Uri uri = null;
        if (place.equals(context.getString(R.string.place_pick_edit_text))){
            //Should get current location
            Log.i("LocationSearchManager/getLocationURI","Current Location Search");

            List<String> providers = new ArrayList<String>();
            providers.add(default_provider);
            providers.add(LocationManager.GPS_PROVIDER);

            for (String provider : providers){
                manager.requestLocationUpdates(provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, listener);
                lastKnownLocation = manager.getLastKnownLocation(provider);
                if (lastKnownLocation!=null && lastKnownLocation.getAccuracy() < 1000) {
                    Log.i("DishFragment/getLocationURI","Appropriate Provider Found " + provider);
                    default_provider = provider;
                    break;
                }
            }

            lastKnownLocation = manager.getLastKnownLocation(default_provider);
            while(lastKnownLocation==null || lastKnownLocation.getAccuracy() > 500 || count < 40){
                manager.requestLocationUpdates(default_provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, listener);

                lastKnownLocation = manager.getLastKnownLocation(default_provider);
                if (count++ > 200){
                    Log.e("DishFragment/getLocationURI", "Location Accuracy failed");
                    if (context instanceof BaseActivity)
                        ((BaseActivity)context).makeYammToast(context.getString(R.string.gps_accuracy_warning_text), Toast.LENGTH_SHORT);
                    break;
                }
            }
            manager.removeUpdates(listener);
            if (lastKnownLocation != null) {
                x = lastKnownLocation.getLatitude();
                y = lastKnownLocation.getLongitude();
                place = getAddressFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), context);
            }
            else
                place = null;
        }

//        addDishToPositive(SEARCH_MAP, place);
//        trackSearchMapMixpanel(place);

        if (place == null){
            Log.e("LocationSearchHelper/getLocationURI","Unable to locate user");
            return new RawURI(null,null);
        }
        Log.i("LocationSearchHelper/getLocationURI","Location: " + place + " Dish:" + item.getName() );
        uri = Uri.parse("geo:0,0?q=" + place + " " + item.getName());


        return new RawURI(uri, place);
    }

    private static String getAddressFromLocation(double latitude, double longitude, Context context){
        Geocoder geoCoder = new Geocoder(context, Locale.KOREAN);
        String match = null;


        ArrayList<Pattern> patternList = new ArrayList<Pattern>();
        patternList.add(Pattern.compile("(\\S+)동 "));
        patternList.add(Pattern.compile("(\\S+)구 "));
        patternList.add(Pattern.compile("(\\S+)시 "));


        int count = 0, p = 0;
        try {
            List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 5);
            if (addresses.size() > 0) {
                while (count < 5) {
                    Address mAddress = addresses.get(count);
                    String s = mAddress.getLocality() + " " + mAddress.getThoroughfare() + " " + mAddress.getFeatureName();

                    Log.i("LocationSearchHelper/getAddressFromLocation","Address " + count + ": " + s);


                    //Extract pattern
                    Pattern pattern = patternList.get(p);
                    Matcher m = pattern.matcher(s);
                    while (m.find()) { // Find each match in turn; String can't do this.
                        match = m.group(1); // Access a submatch group; String can't do this.
                    }
                    Log.i("LocationSearchHelper/getAddressFromLocation","Match " + match);
                    if (match != null) {
                        if (p == 0)
                            return match+"동";
                        if (p == 1)
                            return match;
                        if (p == 2)
                            return match;
                    }
                    count++;

                    if (count == 5   && p < 2) {
                        //get new pattern
                        p++;
                        count = 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class RawURI{
        public Uri uri;
        public String place;

        public RawURI(Uri uri, String place){
            this.uri = uri;
            this.place= place;
        }
    }*/
}
