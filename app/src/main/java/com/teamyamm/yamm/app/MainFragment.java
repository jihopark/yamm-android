package com.teamyamm.yamm.app;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 5/24/14.
 */
public class MainFragment extends Fragment {
    public final static String MAIN_FRAGMENT = "mf";


    private RelativeLayout main_layout;
    private ViewPager dishPager;
    private DishFragmentPagerAdapter dishAdapter;

    private List<DishItem> dishItems;
    private int currentPage;
    private boolean isGroup;

    //For Place Pick
    //private AutoCompleteTextView placePickEditText;
    private LocationManager locationManager;
    private LocationListener locationListener;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MainFragment/onCreateView", "onCreateView started");

        main_layout = (RelativeLayout) inflater.inflate(R.layout.fragment_main, container, false);

        initFragment();
        setDishPager();
        setLocationManagerListener();

        return main_layout;
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

    public LocationManager getLocationManager(){
        return locationManager;
    }

    public LocationListener getLocationListener(){
        return locationListener;
    }

    private void initFragment(){
        Bundle bundle =  this.getArguments();
        Type type = new TypeToken<List<DishItem>>(){}.getType();

        String s = bundle.getString("dishes");


        dishItems = new Gson().fromJson(s, type);
        isGroup = bundle.getBoolean("isGroup");
    }



    /*
    * Dish View Pager Related Methods & Classes
    * */

    public DishItem getCurrentDishItem(){
        if (dishAdapter!=null)
            return dishAdapter.getCurrentDishItem();
        return null;
    }

    public int getCurrentPage(){ return currentPage; }

    private void setDishPager(){
        dishAdapter = new DishFragmentPagerAdapter(getChildFragmentManager());
        dishPager = (ViewPager) main_layout.findViewById(R.id.dish_pager);
        dishPager.setAdapter(dishAdapter);
        dishPager.setOnPageChangeListener(dishAdapter);
    }

    public ViewPager getDishPager(){
        return dishPager;
    }

    private class DishFragmentPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener{

        private final int DEFAULT_NUMBER_OF_DISHES = 4;
        private int numPage;
        private ArrayList<DishFragment> fragments;
        private boolean hasReachedEnd = false;

        public DishFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<DishFragment>();

         //   if (isGroup)
        //        numPage = DEFAULT_NUMBER_OF_DISHES + 1;
        //    else
                numPage = DEFAULT_NUMBER_OF_DISHES;

        }

        @Override
        public Fragment getItem(int index) {
      //      if (isGroup && index == DEFAULT_NUMBER_OF_DISHES){
        //        return  new BattleOfferFragment();
      //      }


            DishFragment dishFragment = new DishFragment();
            Bundle bundle = new Bundle();
            bundle.putString("dish", new Gson().toJson(dishItems.get(index), DishItem.class));
            bundle.putBoolean("isGroup",isGroup);
            bundle.putInt("index", index);

            dishFragment.setArguments(bundle);

            fragments.add(index, dishFragment);

            return dishFragment;
        }

        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return numPage;
        }

        @Override
        public CharSequence getPageTitle(int position){
            return "";
        }

       /* @Override
        public float getPageWidth(int position)
        {
            return 0.95f;
        }*/

        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }

        @Override
        public void onPageSelected(int i) {
            if (i == DEFAULT_NUMBER_OF_DISHES)
                currentPage = DEFAULT_NUMBER_OF_DISHES - 1;
            else
                currentPage = i;

            if (i == DEFAULT_NUMBER_OF_DISHES - 1 && !hasReachedEnd){
                trackEndOfRecommendationMixpanel();
                hasReachedEnd = true;
            }

        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }

        public DishItem getCurrentDishItem(){
            if (fragments.size() > currentPage)
                return fragments.get(currentPage).getDishItem();
            Log.e("DishFragmentPagerAdapter/getCurrentDishItem","Current Page is wrong, returning first item");
            return fragments.get(0).getDishItem();
        }
    }

    public void changeInDishItem(DishItem original, DishItem replace){
        Log.i("MainFragment/changeInDishItem","Original " + original + " Replace    " + replace);
        for (int i = 0; i < dishItems.size() ; i++){
            if (dishItems.get(i).equals(original)){
                dishItems.remove(i);
                break;
            }
        }
        dishItems.add(replace);
        dishAdapter.notifyDataSetChanged();
        Log.i("MainFragment/changeInDishItem","Change In items " + dishItems);


        ((MainFragmentInterface)getActivity()).changeInDishItem(dishItems);
    }



    private void setLocationManagerListener(){
        //Set Location Listener
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.i("LocationListener/onLocationChanged", "Location Changed " + location.toString());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        Log.i("MainFragment/setLocationManagerListener","Location Manager and Listener Set");
    }

    private void trackEndOfRecommendationMixpanel(){
        Activity activity = getActivity();
        if (activity instanceof BaseActivity){
            BaseActivity base = (BaseActivity) activity;
            JSONObject props = new JSONObject();
            base.getMixpanelAPI().track("End Of Recommendation", props);
            Log.i("MainFragment/trackEndOfRecommendationMixpanel","End of Recommendation Tracked");
        }
    }

}
