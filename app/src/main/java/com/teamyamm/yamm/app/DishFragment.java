package com.teamyamm.yamm.app;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 7/17/14.
 */
public class DishFragment extends Fragment {
    private final static long LOCATION_MIN_TIME = 100; //0.1sec
    private final static float LOCATION_MIN_DISTANCE = 1.0f; //1 meters
    private final int DEFAULT_NUMBER_OF_DISHES = 4;


    public final static String TOO_MANY_DISLIKE = "dis";
    public final static String SHARE = "SHARE";
    public final static String SEARCH_MAP = "SEARCHMAP";

    private RelativeLayout main_layout;
    private DishItem item;
    private int index;
    private ImageButton searchMap, pokeFriend, dislike, next;
    private YammImageView dishImage;
    private boolean isGroup;
    private Activity activity;
    private MainFragment parentFragment;
    private ImageView mainBar;
    private TextView nameText, commentText;

    //private AutoCompleteTextView placePickEditText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main_layout = (RelativeLayout) inflater.inflate(R.layout.fragment_dish, container, false);

        isGroup = this.getArguments().getBoolean("isGroup");
        index = this.getArguments().getInt("index");

        mainBar = (ImageView) main_layout.findViewById(R.id.main_image_bar);
        nameText = (TextView) main_layout.findViewById(R.id.dish_name_text);
        commentText = (TextView) main_layout.findViewById(R.id.dish_comment_text);


        if (getParentFragment() instanceof MainFragment){
            parentFragment = (MainFragment) getParentFragment();
            if (parentFragment.isPerforming){
                mainBar.setVisibility(View.INVISIBLE);
                nameText.setVisibility(View.INVISIBLE);
                commentText.setVisibility(View.INVISIBLE);
            }

        }
        else{
            Log.e("DishFragment/onCreateView", "Parent Fragment of DishFragment should be instanceof MainFragment!");
            return null;
        }

        loadDish();
        if (index == parentFragment.getCurrentPage()) {
            Log.i("DishFragment/onCreateView",  item.getName() + " Page " + index + " : "+ " Setting Button for Current index");
            setButtons();
        }


        return main_layout;
    }

    public DishItem getDishItem(){
        return item;
    }
    public ImageView getMainBar(){ return mainBar; }
    public TextView getNameText(){ return nameText; }
    public TextView getCommentText(){ return commentText; }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (getParentFragment() == null){
            Log.e("DishFragment/getParentFragment", "DishFragment Removed, because ParentFragment is null");
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    private void loadDish(){
        String s = this.getArguments().getString("dish");
        item = new Gson().fromJson(s, DishItem.class);

        TextView name = (TextView) main_layout.findViewById(R.id.dish_name_text);
        name.setText(item.getName());

        TextView comment = (TextView) main_layout.findViewById(R.id.dish_comment_text);
        comment.setText(item.getComment());

        YammImageView image = (YammImageView) main_layout.findViewById(R.id.dish_image);
        image.setID(item.getId());
        image.setPath(YammImageView.MAIN);
    }

    public void showTexts(){
        mainBar.setVisibility(View.VISIBLE);
        nameText.setVisibility(View.VISIBLE);
        commentText.setVisibility(View.VISIBLE);
    }

    public void setButtons(){
        try {
            next = parentFragment.getButton(R.id.dish_next_button);

            searchMap = parentFragment.getButton(R.id.search_map_button);

            pokeFriend = parentFragment.getButton(R.id.poke_friend_button);

            dislike = parentFragment.getButton(R.id.dish_dislike_button);


            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewPager dishPager = parentFragment.getDishPager();

                    if (index == DEFAULT_NUMBER_OF_DISHES - 1)
                        dishPager.setCurrentItem(index - 1, true);
                    else
                        dishPager.setCurrentItem(index + 1, true);

                }
            });


            pokeFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //DialogFragment pokeMethodDialog = new PokeMethodDialog();
                    //pokeMethodDialog.show(getChildFragmentManager(), "pokeMethod");
                    addDishToPositive(SHARE, null);

                    if (isGroup && getActivity() instanceof GroupRecommendationActivity){
                        GroupRecommendationActivity activity = (GroupRecommendationActivity) getActivity();
                        activity.sendPokeMessage(item);
                    }
                    else {
                        Intent intent = new Intent(parentFragment.getActivity(), PokeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        Bundle bundle = new Bundle();
                        bundle.putString("dish", new Gson().toJson(item, DishItem.class));

                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });

            searchMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLocationDialog();
                }
            });


            final DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("DishFragment/onClick", "Dislike pressed for " + getDishItem().getName());
                    trackDislikeMixpanel();
                    ((BaseActivity)getActivity()).makeYammToast(R.string.dish_dislike_toast, Toast.LENGTH_SHORT);

                    YammAPIService service = YammAPIAdapter.getDislikeService();

                    Callback<DishItem> callback = new Callback<DishItem>() {
                        @Override
                        public void success(DishItem dishItem, Response response) {
                            Log.i("DishFragment/postDislikeDish", "Success " + dishItem.getName());
                            changeInDishItems(getDishItem(), dishItem);
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            String msg = retrofitError.getCause().getMessage();

                            if (msg.equals(DishFragment.TOO_MANY_DISLIKE)) {
                                ((BaseActivity)getActivity()).makeYammToast(R.string.dish_too_many_dislike_toast, Toast.LENGTH_SHORT);
                            }
                            else if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION)) {
                                Log.e("PokeActivity/pokeWithYamm", "Invalid Token, Logging out");
                                if (getActivity() instanceof BaseActivity) {
                                    ((BaseActivity) getActivity()).invalidToken();
                                    return;
                                }
                            }
                            else {
                                if (getActivity() instanceof BaseActivity)
                                    ((BaseActivity)getActivity()).makeYammToast(R.string.unidentified_error_message, Toast.LENGTH_SHORT);
                            }
                        }
                    };

                    if (isGroup) {
                        service.postDislikeDishGroup(new YammAPIService.RawDislike(getDishItem().getId()), callback);
                        Log.i("DishFragment/onClickListener", "Group Dislike API Called");
                    } else
                        service.postDislikeDish(new YammAPIService.RawDislike(getDishItem().getId()), callback);


                }
            };

            dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trackClickedDislikeMixpanel();
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                    AlertDialog alert = builder.setPositiveButton(R.string.dish_dislike_positive, positiveListener)
                            .setNegativeButton(R.string.dish_dislike_negative, null)
                            .setTitle(R.string.dish_dislike_title)
                            .setMessage(R.string.dish_dislike_message)
                            .create();

                    alert.show();
                }
            });
        }catch(NullPointerException e){
            Log.e("DishFragment/setButtons","NullPointerException caught. Is ParentFragment Null? " + (parentFragment == null));
            e.printStackTrace();
            if (getActivity() instanceof BaseActivity) {
                BaseActivity activity = (BaseActivity) getActivity();
                activity.trackCaughtExceptionMixpanel("DishFragment/setButtons", e.getMessage());
            }

        }
    }

    private void changeInDishItems(DishItem original, DishItem replace){
        ((MainFragment)getParentFragment()).changeInDishItem(original, replace);
    }

    private void addDishToPositive(String category, String detail){

        YammAPIService service = YammAPIAdapter.getTokenService();

        Log.i("DishFragment/addDishToPositive","Like "  + getDishItem().getName() + " " + category + " " + detail);

        service.postLikeDish(new YammAPIService.RawLike(getDishItem().getId(), category, detail), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.i("DishFragment/postLikeDish","Success " + s);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                String msg = retrofitError.getCause().getMessage();
                if (msg.equals(YammAPIService.YammRetrofitException.AUTHENTICATION)) {
                    Log.e("PokeActivity/addDishToPositive", "Invalid Token, Logging out");
                    if (getActivity() instanceof BaseActivity) {
                        ((BaseActivity) getActivity()).invalidToken();
                        return ;
                    }
                }
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
                dialog.dismiss();
            }
        });

        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DishFragment.this.isAdded())
                    searchMap(activity.getString(R.string.place_pick_edit_text));
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
            ((BaseActivity)activity).makeYammToast(activity.getString(R.string.location_error), Toast.LENGTH_SHORT);
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
                    ((BaseActivity)activity).makeYammToast(activity.getString(R.string.gps_accuracy_warning_text), Toast.LENGTH_SHORT);
                    break;
                }
            }
            locationManager.removeUpdates(getLocationListener());
            if (lastKnownLocation != null)
                place = getAddressFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            else
                place = null;
        }

        addDishToPositive(SEARCH_MAP, place);
        trackSearchMapMixpanel(place);

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

    private void trackSearchMapMixpanel(String place){
        Activity activity = parentFragment.getActivity();
        if (activity instanceof BaseActivity){
            BaseActivity base = (BaseActivity) activity;
            JSONObject props = new JSONObject();
            try {
                props.put("Place", place);
            }catch(JSONException e){
                Log.e("DishFragment/trackSearchMapMixpanel","JSON Error");
            }

            base.getMixpanelAPI().track("Search Map", props);
            Log.i("DishFragment/trackSearchMapMixpanel","Search Map Tracked " + place);
        }
        else
            Log.e("DishFragment/trackSearchMapMixpanel","Wrong Activity");

    }

    private void trackClickedDislikeMixpanel(){
        Activity activity = parentFragment.getActivity();
        if (activity instanceof BaseActivity){
            BaseActivity base = (BaseActivity) activity;
            JSONObject props = new JSONObject();
            try{
                props.put("Dish", item.getName());
            }catch (JSONException e){
                Log.e("DishFragment/trackClickedDislikeMixpanel","JSON Error");
            }
            base.getMixpanelAPI().track("Clicked Dislike", props);
            Log.i("DishFragment/trackClicked DislikeMixpanel","Clicked Dislike Tracked");
        }
    }

    private void trackDislikeMixpanel(){
        Activity activity = parentFragment.getActivity();
        if (activity instanceof BaseActivity){
            BaseActivity base = (BaseActivity) activity;
            JSONObject props = new JSONObject();
            try{
                props.put("Dish", item.getName());
            }catch (JSONException e){
                Log.e("DishFragment/trackDislikeMixpanel","JSON Error");
            }
            base.getMixpanelAPI().track("Dislike", props);
            Log.i("DishFragment/trackDislikeMixpanel","Dislike Tracked");
        }
    }


}