package com.teamyamm.yamm.app;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by parkjiho on 7/17/14.
 */
public class DishFragment extends Fragment {
    private final static long LOCATION_MIN_TIME = 100; //0.1sec
    private final static float LOCATION_MIN_DISTANCE = 1.0f; //1 meters

    private LinearLayout main_layout;
    private DishItem item;
    private int itemID;
    private Button searchMap, pokeFriend;
    private boolean isGroup;
    private Activity activity;

    //private AutoCompleteTextView placePickEditText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main_layout = (LinearLayout) inflater.inflate(R.layout.fragment_dish, container, false);

        isGroup = this.getArguments().getBoolean("isGroup");

        loadDish();
        setButton();

        return main_layout;
    }

    public DishItem getDishItem(){
        return item;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    private void loadDish(){
        String s = this.getArguments().getString("dish");
        item = new Gson().fromJson(s, DishItem.class);

        TextView name = (TextView) main_layout.findViewById(R.id.dish_name_text);
        name.setText(item.getName());

        TextView comment = (TextView) main_layout.findViewById(R.id.dish_comment_text);
        comment.setText(item.getComment());

    }

    private void setButton(){
        searchMap = (Button) main_layout.findViewById(R.id.search_map_button);

        pokeFriend = (Button) main_layout.findViewById(R.id.poke_friend_button);

        if (isGroup)
            pokeFriend.setText("메뉴 선택");
        else
            pokeFriend.setText("친구랑 같이 먹기");


        pokeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment pokeMethodDialog = new PokeMethodDialog();
                pokeMethodDialog.show(getChildFragmentManager(), "pokeMethod");
            }
        });

        searchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationDialog();
            }
        });
    }

    private void showLocationDialog(){
        final Dialog dialog = new Dialog(activity);

        dialog.setContentView(R.layout.dialog_map);
        dialog.setTitle(R.string.map_dialog_title);

        final AutoCompleteTextView textView = (AutoCompleteTextView) dialog.findViewById(R.id.map_autocomplete_text);

        Button negative = (Button) dialog.findViewById(R.id.map_dialog_negative_button);
        Button positive = (Button) dialog.findViewById(R.id.map_dialog_positive_button);
        Button current = (Button) dialog.findViewById(R.id.map_dialog_current_button);

        setPlacePickEditText(textView);

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DishFragment.this.isAdded())
                    searchMap(textView.getText().toString());
                else
                    dialog.dismiss();
            }
        });

        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DishFragment.this.isAdded())
                    searchMap(activity.getString(R.string.place_pick_edit_text));
                else
                    dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void setPlacePickEditText(AutoCompleteTextView placePickEditText){
        placePickEditText.setText(activity.getString(R.string.place_pick_edit_text));
        placePickEditText.setThreshold(1);
        placePickEditText.setSelectAllOnFocus(true);
        ArrayAdapter<String> place_adapter =
                new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.places_array));
        placePickEditText.setAdapter(place_adapter);
        placePickEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (!hasFocus){
                    Log.i("DishFragment/placePickEditText", "focus gone");
                    if ( ((TextView)v).getText().toString().equals("") ) {
                        ((TextView) v).setText(activity.getString(R.string.place_pick_edit_text));
                    }
                    if (imm!=null)
                        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
                else{
                    ((TextView)v).setText("");
                    Log.i("DishFragment/placePickEditText", "focus came");
                    if (imm!=null)
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                }
            }
        });
    }

    private void searchMap(String input){
        Uri location = getLocationURI(input);
        if (location == null){
            Toast.makeText(activity, activity.getString(R.string.location_error), Toast.LENGTH_SHORT).show();
            location = Uri.parse("geo:0,0?q="+ item.getName());
        }
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

        //Verify Intent
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe)
            startActivity(mapIntent);
        else
            Log.e("DishFragment", "Intent not safe");
    }

    private LocationManager getLocationManager(){
        return ((MainFragment)getParentFragment()).getLocationManager();
    }

    private LocationListener getLocationListener(){
        return ((MainFragment)getParentFragment()).getLocationListener();
    }

    private Uri getLocationURI(String input){
        int count = 0;
        String default_provider = LocationManager.NETWORK_PROVIDER; //default provider
        String place = input;
        LocationManager locationManager = getLocationManager();
        Location lastKnownLocation;
        Uri uri = null;
        if (place.equals(activity.getString(R.string.place_pick_edit_text))){
            //Should get current location
            Log.i("DishFragment/getLocationURI","Current Location Search");

            List<String> providers = new ArrayList<String>();
            providers.add(default_provider);
            providers.add(LocationManager.GPS_PROVIDER);

            for (String provider : providers){
                locationManager.requestLocationUpdates(provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, getLocationListener());
                lastKnownLocation = locationManager.getLastKnownLocation(provider);
                if (lastKnownLocation!=null && lastKnownLocation.getAccuracy() < 1000) {
                    Log.i("DishFragment/getLocationURI","Appropriate Provider Found " + provider);
                    default_provider = provider;
                    break;
                }
            }

            lastKnownLocation = locationManager.getLastKnownLocation(default_provider);
            while(lastKnownLocation==null || lastKnownLocation.getAccuracy() > 500 || count < 20){
                locationManager.requestLocationUpdates(default_provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, getLocationListener());

                lastKnownLocation = locationManager.getLastKnownLocation(default_provider);
                if (count++ > 150){
                    Log.e("DishFragment/getLocationURI", "Location Accuracy failed");
                    Toast.makeText(activity,activity.getString(R.string.gps_accuracy_warning_text), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            locationManager.removeUpdates(getLocationListener());
            if (lastKnownLocation != null)
                place = getAddressFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            else
                place = null;
        }
        if (place == null){
            Log.e("DishFragment/getLocationURI","Unable to locate user");
            return null;
        }
        Log.i("DishFragment/getLocationURI","Location: " + place + " Dish:" + getDishItem().getName() );
        uri = Uri.parse("geo:0,0?q=" + place + " " + getDishItem().getName());

        return uri;
    }

    private String getAddressFromLocation(double latitude, double longitude){
        Geocoder geoCoder = new Geocoder(activity, Locale.KOREAN);
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

                    Log.i("Dish Fragment/getAddressFromLocation","Address " + count + ": " + s);


                    //Extract pattern
                    Pattern pattern = patternList.get(p);
                    Matcher m = pattern.matcher(s);
                    while (m.find()) { // Find each match in turn; String can't do this.
                        match = m.group(1); // Access a submatch group; String can't do this.
                    }
                    Log.i("DishFragment/getAddressFromLocation","Match " + match);
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

}