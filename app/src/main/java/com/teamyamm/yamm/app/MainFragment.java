package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;
/**
 * Created by parkjiho on 5/24/14.
 */
public class MainFragment extends Fragment {
    private final static long LOCATION_MIN_TIME = 100; //0.1sec
    private final static float LOCATION_MIN_DISTANCE = 1.0f; //1 meters

    private RelativeLayout main_layout;
    private Button searchMapButton;
    private ViewPager dishPager;
    private DishFragmentPagerAdapter dishAdapter;

    private ArrayList<Integer> dishIDs;
    private int currentPage;


    //For Place Pick
    //private AutoCompleteTextView placePickEditText;
   // private LocationManager locationManager;
  //  private LocationListener locationListener;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MainFragment/onCreateView", "onCreateView started");

        main_layout = (RelativeLayout) inflater.inflate(R.layout.fragment_main, container, false);
        searchMapButton = (Button) main_layout.findViewById(R.id.search_map_button);

        setDishes();
        setDishPager();


       // setPlacePickEditText();
       // setSearchMapButton();
       // setLocationManagerListener();

        return main_layout;
    }

    private void setDishes(){
        dishIDs = this.getArguments().getIntegerArrayList("dishIDs");

        //Set Current DishItem to 0

    }

   /* private void setLocationManagerListener(){
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
                    location = Uri.parse("geo:0,0?q="+ getCurrentDishItem().getName());
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
        Log.i("NewMainFragment/getLocationURI","Location: " + place + " Dish:" + getCurrentDishItem().getName() );
        uri = Uri.parse("geo:0,0?q=" + place + " " + getCurrentDishItem().getName());

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

        main_layout.addView(placePickEditText);

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
*/
    /*
    * For unfocusing PlacePickEditText when other views are touched
    * */
  /*  private void unfocusPlacePickEditText(View view) {
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
    */
    /*
     * For unfocusing PlacePickEditText when back button is pressed
     * */
    /*
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
    }*/

    /*
    * Dish View Pager Related Methods & Classes
    * */

    public DishItem getCurrentDishItem(){
        return dishAdapter.getCurrentDishItem();
    }

    private void setDishPager(){
        dishAdapter = new DishFragmentPagerAdapter(getChildFragmentManager());
        dishPager = (ViewPager) main_layout.findViewById(R.id.dish_pager);
        dishPager.setAdapter(dishAdapter);
        dishPager.setOnPageChangeListener(dishAdapter);
    }

    private class DishFragmentPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener{
        private final int NUMBER_OF_PAGES = 4;
        private ArrayList<DishFragment> fragments;

        public DishFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<DishFragment>();
        }

        @Override
        public Fragment getItem(int index) {
            DishFragment dishFragment = new DishFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("dish",dishIDs.get(index));
            dishFragment.setArguments(bundle);

            fragments.add(index, dishFragment);

            return dishFragment;
        }

        @Override
        public int getCount() {
            return NUMBER_OF_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position){
            return "";
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }

        @Override
        public void onPageSelected(int i) {
            currentPage = i;
            Log.i("DishFragmentPagerAdapter/onPageSelected",i+":"+fragments.get(i).getDishItem());
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }

        public DishItem getCurrentDishItem(){
            return fragments.get(currentPage).getDishItem();
        }
    }

}
