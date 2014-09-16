package com.teamyamm.yamm.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    public final static int DEFAULT_NUMBER_OF_DISHES = 4;


    private RelativeLayout main_layout;
    private ViewPager dishPager;
    private DishFragmentPagerAdapter dishAdapter;
    private ImageButton nextLeft, nextRight, searchMap, pokeFriend, dislike;

    private List<DishItem> dishItems;
    private int currentPage = 0;
    private boolean isGroup;
    private boolean hasPerformed = false;
    public boolean isPerforming = false;

    private Animation buttonAnimation, buttonAnimation2, buttonAnimation3, buttonAnimation4,
            mainBarAnimation, textAnimation1, textAnimation2;

    //For Place Pick
    //private AutoCompleteTextView placePickEditText;
    private LocationManager locationManager;
    private LocationListener locationListener;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MainFragment/onCreateView", "onCreateView started");

        main_layout = (RelativeLayout) inflater.inflate(R.layout.fragment_main, container, false);

        nextLeft = (ImageButton) main_layout.findViewById(R.id.dish_next_left_button);
        nextRight = (ImageButton) main_layout.findViewById(R.id.dish_next_right_button);
        searchMap = (ImageButton) main_layout.findViewById(R.id.search_map_button);
        pokeFriend = (ImageButton) main_layout.findViewById(R.id.poke_friend_button);
        dislike = (ImageButton) main_layout.findViewById(R.id.dish_dislike_button);


        initFragment();
        setDishPager();
        setLocationManagerListener();

        return main_layout;
    }

    @Override
    public void onStart(){
        super.onStart();
        if (!hasPerformed) {
            startButtonsAnimation();
            hasPerformed = true;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.i("MainFragment/onDetach","Detaching all other components");
        dishPager.setOnPageChangeListener(null);
        buttonAnimation.setAnimationListener(null);
        mainBarAnimation.setAnimationListener(null);
        buttonAnimation.cancel();
        mainBarAnimation.cancel();
        textAnimation1.cancel();
        textAnimation2.cancel();

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

    public ImageButton getButton(int viewId){
        View v = main_layout.findViewById(viewId);
        if (v instanceof ImageButton)
            return (ImageButton) v;
        Log.e("MainFragment/getButtons","Non-imagebutton view Id!");
        return null;
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
        dishPager.setOffscreenPageLimit(3);
        dishPager.setAdapter(dishAdapter);
        dishPager.setOnPageChangeListener(dishAdapter);
    }

    public ViewPager getDishPager(){
        return dishPager;
    }

    private class DishFragmentPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener{
        private final boolean LEFT = true;
        private final boolean RIGHT = false;

        private final int DEFAULT_NUMBER_OF_DISHES = 4;
        private int numPage;
        private ArrayList<DishFragment> fragments;
        private boolean hasReachedEnd = false;
        private boolean buttonToLeft = false;

        public DishFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<DishFragment>();
            for (int i=0; i < DEFAULT_NUMBER_OF_DISHES ; i++){
                fragments.add(new DishFragment());
            }

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
            fragments.set(index, dishFragment);
            Log.i("DishFragmentPagerAdapter/getItem", "Page " + index +" : " + dishItems.get(index).getName());

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


            if (i == DEFAULT_NUMBER_OF_DISHES - 1){
                SharedPreferences pref = null;
                String key;

                if (isGroup)
                    key = getString(R.string.PREV_END_OF_RECOMMENDATION_GROUP);
                else
                    key = getString(R.string.PREV_END_OF_RECOMMENDATION_PERSONAL);

                if (getActivity() instanceof BaseActivity) {
                    pref = ((BaseActivity) getActivity()).prefs;
                    hasReachedEnd = pref.getBoolean(key, false);
                    Log.i("DishFragmentPagerAdapter/onPageSelected",key + " retrived. " + hasReachedEnd);
                }
                if (!hasReachedEnd) {
                    trackEndOfRecommendationMixpanel();
                    hasReachedEnd = true;
                    if (pref!=null) {
                        pref.edit().putBoolean(key, true).commit();
                        Log.i("DishFragmentPagerAdapter/onPageSelected",key + " set true");
                    }
                }
            }
            Log.i("DishFragmentPagerAdapter/onPageSelected", dishItems.get(i).getName() + " Page " + i +" : Setting Buttons Again");

            try {
                fragments.get(i).setButtons();
                fragments.get(i).showTexts();
                configureNextButtons(i, nextLeft, nextRight, getResources().getInteger(R.integer.main_buttons_animation_duration));
            }catch(NullPointerException e){
                Log.e("MainFragment/onPageSelected","NullPointer Exception caught. Is fragments.get(i)==null? "+ (fragments.get(i)==null));
                e.printStackTrace();
                if (getActivity() instanceof BaseActivity) {
                    BaseActivity activity = (BaseActivity) getActivity();
                    activity.trackCaughtExceptionMixpanel("MainFragment/onPageSelected", e.getMessage());
                    if (fragments.get(i)!=null)
                        fragments.get(i).setParentFragment(MainFragment.this);
                }
            }catch (IndexOutOfBoundsException e){
                Log.e("MainFragment/onPageSelected","IndexOutOfBoundsException caught");
                e.printStackTrace();
                if (getActivity() instanceof BaseActivity) {
                    BaseActivity activity = (BaseActivity) getActivity();
                    activity.trackCaughtExceptionMixpanel("MainFragment/onPageSelected", e.getMessage());
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }

        public void setButtonsForCurrentIndex(){
            Log.i("DishFragmentPagerAdapter/setButtonsForCurrentIndex", dishItems.get(currentPage).getName() + "Page " + currentPage +" : Setting Buttons For Current Index");
            fragments.get(currentPage).setButtons();
            fragments.get(currentPage).showTexts();
        }

        public DishItem getCurrentDishItem(){
            if (fragments.size() > currentPage)
                return fragments.get(currentPage).getDishItem();
            Log.e("DishFragmentPagerAdapter/getCurrentDishItem","Current Page is wrong, returning first item");
            return fragments.get(0).getDishItem();
        }

        public DishFragment getFirstFragment(){return fragments.get(0); }
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
        dishAdapter.setButtonsForCurrentIndex();
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

    public void startButtonsAnimation() {

        isPerforming = true;

        buttonAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.main_buttons_alpha);
        buttonAnimation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.main_buttons_alpha2);
        buttonAnimation3 = AnimationUtils.loadAnimation(getActivity(), R.anim.main_buttons_alpha3);
        buttonAnimation4 = AnimationUtils.loadAnimation(getActivity(), R.anim.main_buttons_alpha4);

        mainBarAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.main_text_container_slide);
        textAnimation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.main_text_container_slide);
        textAnimation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.main_text_container_slide);

        buttonAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                searchMap.setVisibility(View.VISIBLE);
                Log.d("Animation", "Button2 on Animation Start");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //dislike.startAnimation(buttonAnimation3);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        buttonAnimation3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                dislike.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //nextRight.startAnimation(buttonAnimation4);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        buttonAnimation4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                nextRight.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MainFragmentInterface activity = null;
                if (getActivity() instanceof MainFragmentInterface)
                    activity = (MainFragmentInterface) getActivity();
                if (activity!=null && activity.shouldTutorialOpen() && !activity.isLoading())
                    activity.showTutorial();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        final Dialog dialog = ((MainFragmentInterface) getActivity()).getFullScreenDialog();

        buttonAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                pokeFriend.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mainBarAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                hasPerformed = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                pokeFriend.startAnimation(buttonAnimation);
                searchMap.startAnimation(buttonAnimation2);
                dislike.startAnimation(buttonAnimation3);
                nextRight.startAnimation(buttonAnimation4);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final MainFragmentInterface main = (MainFragmentInterface) getActivity();

        nextRight.setVisibility(View.INVISIBLE);
        searchMap.setVisibility(View.INVISIBLE);
        pokeFriend.setVisibility(View.INVISIBLE);
        dislike.setVisibility(View.INVISIBLE);

        if (main.isFullScreenDialogOpen()){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (!main.isLoading()) {
                        main.closeFullScreenDialog();
                        Log.d("MainFragment/run", "Dialog Dismissed here - 6");
                    }
                    actuallyRunAnimations();
                    Log.i("MainFragment/startButtonsAnimation", "Animation Started for Main Buttons after Dialog");

                }
            }, getResources().getInteger(R.integer.dialog_delay_duration));
        }
        else
            actuallyRunAnimations();


    }

    private void actuallyRunAnimations(){
        if (dishAdapter.getFirstFragment() != null) {
            startMainBarTextAnimation();
        }
        else {
            ((MainFragmentInterface) getActivity()).closeFullScreenDialog();
            Log.e("MainFragment/startButtonsAnimation", "First Fragment was null. Couldn't perform animation");
        }
    }

    private void startMainBarTextAnimation(){
        try {

            isPerforming = false;

            TextView tv1 = dishAdapter.getFirstFragment().getNameText();
            TextView tv2 = dishAdapter.getFirstFragment().getCommentText();

            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);

            tv1.startAnimation(textAnimation1);
            tv2.startAnimation(textAnimation2);

            dishAdapter.getFirstFragment().getMainBar().startAnimation(mainBarAnimation);
            dishAdapter.getFirstFragment().getMainBar().setVisibility(View.VISIBLE);
        }catch(NullPointerException e){
            Log.e("MainFragment/AnimationListener", "Is getFirstFragment Null? " + (dishAdapter.getFirstFragment().getMainBar() == null));
            e.printStackTrace();
        }
    }

    public static void configureNextButtons(int i, ImageButton left, ImageButton right, int duration){
        int previousLeftV = left.getVisibility();
        int previousRightV = right.getVisibility();
        final int currentLeftV, currentRightV;

        if (i==0) {
            currentLeftV = View.GONE;
            currentRightV = View.VISIBLE;
        }
        else if (i==DEFAULT_NUMBER_OF_DISHES-1){
            currentRightV = View.GONE;
            currentLeftV = View.VISIBLE;
        }
        else{
            currentRightV = View.VISIBLE;
            currentLeftV = View.VISIBLE;
        }
        AlphaAnimation appear = new AlphaAnimation(0f, 1.0f);
        AlphaAnimation disappear = new AlphaAnimation(1.0f, 0.0f);
        appear.setDuration(duration);
        disappear.setDuration(duration);

        final ImageButton fLeft = left, fRight = right;


        if (previousLeftV!=currentLeftV){
            if (previousLeftV == View.GONE) {
                appear.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        fLeft.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                left.startAnimation(appear);
            }
            else {
                disappear.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fLeft.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                left.startAnimation(disappear);
            }
        }

        if (previousRightV!=currentRightV){
            if (previousRightV == View.GONE) {
                appear.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        fRight.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                right.startAnimation(appear);
            }
            else {
                disappear.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fRight.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                right.startAnimation(disappear);
            }
        }

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
