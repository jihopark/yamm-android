package com.teamyamm.yamm.app.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.teamyamm.yamm.app.MapActivity;
import com.teamyamm.yamm.app.R;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.pojos.DishItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

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

   /* public static void searchMap(DishItem item, String input, Context context){
        double x=0, y=0;
        mItem = item;

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
                        startMapActivity(location.latitude, location.longitude, context);
                    }
                }

                @Override
                public void failure(RetrofitError retrofitError) {

                }
            });
        }
        else {
            startMapActivity(x,y, context);
        }
    }*/

    public static void startMapActivity(Context context, DishItem item){
        Intent mapIntent = new Intent(context, MapActivity.class);
        mapIntent.putExtra(LANG, 0);
        mapIntent.putExtra(LONG, 0);
        mapIntent.putExtra(DISH_NAME, item.getName());
        mapIntent.putExtra(DISH_ID, item.getId());
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        context.startActivity(mapIntent);
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
            return null;
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
            //Should get current lstKnownLocation.getAccuracy() < 1000) {
                    Log.i("DishFragment/getLocationURI","Appropriate Provider Found " + provider);
                    default_provider = provider;
                    break;
                }
            }

            lastKnownLocation = manager.getLastKnownLocation(default_provider);
            while(lastKnownLocation==null || lastKnownLocation.getAccuracy() > 500 || count < 40){
                manager.requestLocationUpdates(default_providerocation
            Log.i("LocationSearchManager/getLocationURI","Current Location Search");

            List<String> providers = new ArrayList<String>();
            providers.add(default_provider);
            providers.add(LocationManager.GPS_PROVIDER);

            for (String provider : providers){
                manager.requestLocationUpdates(provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, listener);
                lastKnownLocation = manager.getLastKnownLocation(provider);
                if (lastKnownLocation!=null && la, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, listener);

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
    }*/

    public static String getAddressFromLocation(double latitude, double longitude, Context context){
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
                    String s = mAddress.getLocality() + " " + mAddress.getThoroughfare();
                    Log.i("LocationSearchHelper/getAddressFromLocation","Address " + count + ": " + s);
                    return s;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void showLocationDialog(MapActivity context){
        final MapActivity mContext = context;
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_map);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final AutoCompleteTextView textView = (AutoCompleteTextView) dialog.findViewById(R.id.map_autocomplete_text);

        ImageButton setMap = (ImageButton) dialog.findViewById(R.id.map_icon);
        ImageButton negative = (ImageButton) dialog.findViewById(R.id.map_dialog_negative_button);
        Button positive = (Button) dialog.findViewById(R.id.map_dialog_positive_button);

        setPlacePickEditText(textView, context);

        setMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(mContext.getString(R.string.place_pick_edit_text));
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mContext.changeLocation(textView.getText().toString());
               dialog.dismiss();
                MixpanelController.trackChangeMapLocationMixpanel(textView.getText().toString());
            }
        });
        dialog.show();
    }


    private static void setPlacePickEditText(AutoCompleteTextView placePickEditText, Context context){
        final Context mContext = context;
        placePickEditText.setText(context.getString(R.string.place_pick_edit_text));
        placePickEditText.setThreshold(1);
        placePickEditText.setSelectAllOnFocus(true);
        ArrayAdapter<String> place_adapter =
                new ArrayAdapter<String>(context, R.layout.place_pick_item, context.getResources().getStringArray(R.array.places_array));
        placePickEditText.setAdapter(place_adapter);
        placePickEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (!hasFocus){
                    Log.i("LocationSearchHelper/placePickEditText", "focus gone");
                    if ( ((TextView)v).getText().toString().equals("") ) {
                        ((TextView) v).setText(mContext.getString(R.string.place_pick_edit_text));
                    }
                    if (imm!=null)
                        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
                else{
                    ((TextView)v).setText("");
                    Log.i("LocationSearchHelper/placePickEditText", "focus came");
                    if (imm!=null)
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                }
            }
        });
    }
}
