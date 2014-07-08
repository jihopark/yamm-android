package com.teamyamm.yamm.app;

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
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by parkjiho on 5/24/14.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private final static int FRIEND_ACTIVITY_REQUEST_CODE = 1001;
    private final static long LOCATION_MIN_TIME = 100; //0.1sec
    private final static float LOCATION_MIN_DISTANCE = 1.0f; //1 meters
    private FrameLayout main_layout;
    private ImageView imageOne, imageTwo;
    private int currentImage = 1;
    private DishItem currentDishItem;

    private Button friendPickButton, nextButton, searchMapButton, pokeFriendButton;
    private Spinner datePickSpinner;
    public ArrayAdapter<CharSequence> spinnerAdapter;
    public YammDatePickerFragment datePickerFragment;
    private RelativeLayout mainButtonsContainer;

    private ArrayList<String> selectedFriendList = new ArrayList<String>();

    //For Place Pick
    private AutoCompleteTextView placePickEditText;
    private LocationManager locationManager;
    private LocationListener locationListener;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MainFragment/onCreateView", "onCreateView started");

        main_layout = (FrameLayout) inflater.inflate(R.layout.new_main_fragment, container, false);
        friendPickButton = (Button) main_layout.findViewById(R.id.friends_pick_button);
        nextButton = (Button) main_layout.findViewById(R.id.next_button);
        datePickSpinner = (Spinner) main_layout.findViewById(R.id.date_pick_spinner);
        mainButtonsContainer = (RelativeLayout) main_layout.findViewById(R.id.main_buttons_container);
        searchMapButton = (Button) main_layout.findViewById(R.id.search_map_button);
        pokeFriendButton = (Button) main_layout.findViewById(R.id.poke_friend_button);

        currentDishItem = new DishItem(1,"쌀국수");
        setYammImageView();
        setFriendPickButton();
        setNextButton();
        setDatePickSpinner();
        setPlacePickEditText();
        setSearchMapButton();
        setLocationManagerListener();
        setPokeFriendButton();

        return main_layout;
    }

    private void setPokeFriendButton(){
        pokeFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment pokeMethodDialog = new PokeMethodDialog();
                pokeMethodDialog.show(getChildFragmentManager(), "pokeMethod");
                //sendKakaoLink();
            }
        });
    }

    private void setLocationManagerListener(){
        //Set Location Listener
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.i("LocationListener/onLocationChanged","Location Changed " + location.toString());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        Log.i("NewMainFragment/setLocationManagerListener","Location Manager and Listener Set");


    }

    private void setSearchMapButton(){
        searchMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri location = getLocationURI();
                if (location == null){
                    Toast.makeText(getActivity(),getString(R.string.location_error),Toast.LENGTH_LONG).show();
                    location = Uri.parse("geo:0,0?q="+ currentDishItem.getName());
                }
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

                //Verify Intent
                PackageManager packageManager = getActivity().getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe)
                    startActivity(mapIntent);
                else
                    Log.e("NewMainfragment", "Intent not safe");

            }
        });
    }

    private Uri getLocationURI(){
        int count = 0;
        String default_provider = LocationManager.NETWORK_PROVIDER; //default provider
        String place = placePickEditText.getText().toString();
        Location lastKnownLocation;
        Uri uri = null;
        if (place.equals(getString(R.string.place_pick_edit_text))){
            //Should get current location
            Log.i("NewMainFragment/getLocationURI","Current Location Search");

            List<String> providers = new ArrayList<String>();
            providers.add(default_provider);
            providers.add(LocationManager.GPS_PROVIDER);

            for (String provider : providers){
                locationManager.requestLocationUpdates(provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, locationListener);
                lastKnownLocation = locationManager.getLastKnownLocation(provider);
                if (lastKnownLocation!=null && lastKnownLocation.getAccuracy() < 1000) {
                    Log.i("NewMainFragment/getLocationURI","Appropriate Provider Found " + provider);
                    default_provider = provider;
                    break;
                }
            }

            lastKnownLocation = locationManager.getLastKnownLocation(default_provider);
            while(lastKnownLocation==null || lastKnownLocation.getAccuracy() > 500 || count < 20){
                locationManager.requestLocationUpdates(default_provider, LOCATION_MIN_TIME, LOCATION_MIN_DISTANCE, locationListener);

                lastKnownLocation = locationManager.getLastKnownLocation(default_provider);
                if (count++ > 150){
                    Log.e("NewMainFragment/getLocationURI", "Location Accuracy failed");
                    Toast.makeText(getActivity(),getString(R.string.gps_accuracy_warning_text), Toast.LENGTH_LONG).show();
                    break;
                }
            }
            locationManager.removeUpdates(locationListener);
            if (lastKnownLocation != null)
                place = getAddressFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            else
                place = null;
        }
        if (place == null){
            Log.e("NewMainFragment/getLocationURI","Unable to locate user");
            return null;
        }
        Log.i("NewMainFragment/getLocationURI","Location: " + place + " Dish:" + currentDishItem.getName() );
        uri = Uri.parse("geo:0,0?q=" + place + " " + currentDishItem.getName());

        return uri;
    }

    private String getAddressFromLocation(double latitude, double longitude){
        Geocoder geoCoder = new Geocoder(getActivity(), Locale.KOREAN);
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

                    Log.i("NewMainFragment/getAddressFromLocation","Address " + count + ": " + s);


                    //Extract pattern
                    Pattern pattern = patternList.get(p);
                    Matcher m = pattern.matcher(s);
                    while (m.find()) { // Find each match in turn; String can't do this.
                        match = m.group(1); // Access a submatch group; String can't do this.
                    }
                    Log.i("NewMainFragment/getAddressFromLocation","Match " + match);
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

    private void setPlacePickEditText(){
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        placePickEditText = new CustomAutoCompleteTextView(getActivity());
        placePickEditText.setText(getString(R.string.place_pick_edit_text));
        placePickEditText.setThreshold(1);
        placePickEditText.setSelectAllOnFocus(true);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.friends_pick_button);
        placePickEditText.setLayoutParams(params);

        mainButtonsContainer.addView(placePickEditText);

        unfocusPlacePickEditText(main_layout);
        ArrayAdapter<String> place_adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.places_array));
        placePickEditText.setAdapter(place_adapter);
        placePickEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    Log.i("MainFragment/placePickEditText","focus gone");
                    if ( ((TextView)v).getText().toString().equals("") ) {
                        ((TextView) v).setText(getActivity().getString(R.string.place_pick_edit_text));
                    }
                }
                else{
                    ((TextView)v).setText("");
                }
            }
        });
    }

    /*
    * For unfocusing PlacePickEditText when other views are touched
    * */
    private void unfocusPlacePickEditText(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof CustomAutoCompleteTextView)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    BaseActivity.hideSoftKeyboard(getActivity());
                    placePickEditText.clearFocus();
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                unfocusPlacePickEditText(innerView);
            }
        }
    }

    /*
     * For unfocusing PlacePickEditText when back button is pressed
     * */
    public class CustomAutoCompleteTextView extends AutoCompleteTextView{
        public CustomAutoCompleteTextView(Context context){ super(context); }
        public CustomAutoCompleteTextView(Context context, AttributeSet attrs){ super(context, attrs); }
        public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle){ super(context, attrs, defStyle); }

        @Override
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                clearFocus();
            }
            return super.dispatchKeyEvent(event);
        }
    }


    private void setDatePickSpinner(){
        spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.date_spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        datePickSpinner.setAdapter(spinnerAdapter);
        datePickSpinner.setOnItemSelectedListener(this);
    }

    /*
    * For implementing AdapterView.OnItemSelectedListener
    * For Date Pick Spinner
    * */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        if (pos == getResources().getInteger(R.integer.spinner_datepick_pos) ){
            datePickerFragment = new YammDatePickerFragment();
            datePickerFragment.show(getChildFragmentManager(), "timePicker");
        }
    }
    public void onNothingSelected(AdapterView<?> parent) { }


    private void setYammImageView(){
        imageOne = (ImageView) main_layout.findViewById(R.id.main_image_view_one);
        imageOne.setAdjustViewBounds(true);
        imageOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageTwo = (ImageView) main_layout.findViewById(R.id.main_image_view_two);
        imageTwo.setAdjustViewBounds(true);
        imageTwo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageTwo.setVisibility(View.GONE);

        imageOne.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.example2));
        imageTwo.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.example));
    }

    private void setNextButton(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentImage == 1){
                    imageOne.setVisibility(View.GONE);
                    imageTwo.setVisibility(View.VISIBLE);
                    loadNextImage();
                    currentImage = 2;
                }
                else{
                    imageTwo.setVisibility(View.GONE);
                    imageOne.setVisibility(View.VISIBLE);
                    loadNextImage();
                    currentImage = 1;
                }
            }
        });
    }

    /*
    * Loads next image on main imageview
    * */
    private void loadNextImage(){

    }

    private void setFriendPickButton(){
        friendPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((MainActivity)getActivity()).isFriendLoaded()){
                    Toast.makeText(getActivity(), R.string.friend_not_loaded_message, Toast.LENGTH_LONG).show();
                    return ;
                }
                Intent intent = new Intent(getActivity(), FriendActivity.class);
                v.setEnabled(false); //To prevent double fire
                intent.putStringArrayListExtra(FriendActivity.SELECTED_FRIEND_LIST, selectedFriendList); //send previously selected friend list
                startActivityForResult(intent, FRIEND_ACTIVITY_REQUEST_CODE);
                Log.i("MainFragment/onClick","FriendActivity called");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FRIEND_ACTIVITY_REQUEST_CODE){
            Log.i("MainFragment/onActivityResult","Got back from FriendActivity; resultcode: " + resultCode);

            friendPickButton.setEnabled(true);

            if (resultCode == BaseActivity.SUCCESS_RESULT_CODE) {
                //Get Friend List
                selectedFriendList = data.getStringArrayListExtra(FriendActivity.SELECTED_FRIEND_LIST);

                Toast.makeText(getActivity(), "Got Back from Friend" + selectedFriendList, Toast.LENGTH_LONG).show();
            }
        }
    }

    public DishItem getCurrentDishItem(){
        return currentDishItem;
    }

}
