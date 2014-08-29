package com.teamyamm.yamm.app;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Handler;

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
    private ImageButton next, searchMap, pokeFriend, dislike;

    private List<DishItem> dishItems;
    private int currentPage = 0;
    private boolean isGroup;
    private boolean hasPerformed = false;

    private Animation buttonAnimation, mainBarAnimation, textAnimation1, textAnimation2;

    //For Place Pick
    //private AutoCompleteTextView placePickEditText;
    private LocationManager locationManager;
    private LocationListener locationListener;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("MainFragment/onCreateView", "onCreateView started");

        main_layout = (RelativeLayout) inflater.inflate(R.layout.fragment_main, container, false);

        next = (ImageButton) main_layout.findViewById(R.id.dish_next_button);
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
        dishPager.setOffscreenPageLimit(2);
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

            if (i == DEFAULT_NUMBER_OF_DISHES - 1 && !hasReachedEnd){
                trackEndOfRecommendationMixpanel();
                hasReachedEnd = true;
            }
            Log.i("DishFragmentPagerAdapter/onPageSelected", dishItems.get(i).getName() + " Page " + i +" : Setting Buttons Again");
            try {
                fragments.get(i).setButtons();

                if (i == DEFAULT_NUMBER_OF_DISHES - 1) {
                    next.setImageDrawable(getResources().getDrawable(R.drawable.arrow_left));
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) next.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    next.setLayoutParams(params);
                    buttonToLeft = true;
                } else {
                    if (buttonToLeft) {
                        next.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right));
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) next.getLayoutParams();
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        next.setLayoutParams(params);
                        buttonToLeft = false;
                    }
                }
            }catch(NullPointerException e){
                Log.e("MainFragment/onPageSelected","NullPointer Exception caught. Is fragments.get(i)==null? "+ (fragments.get(i)==null));
                e.printStackTrace();
                if (getActivity() instanceof BaseActivity) {
                    BaseActivity activity = (BaseActivity) getActivity();
                    activity.trackCaughtExceptionMixpanel("MainFragment/onPageSelected", e.getMessage());
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
        buttonAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.main_buttons_alpha);
        mainBarAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.main_text_container_slide);
        textAnimation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.main_text_alpha);
        textAnimation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.main_text_alpha);

        textAnimation1.setStartOffset(getResources().getInteger(R.integer.main_text_animation_offset));

        mainBarAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TextView tv1 = dishAdapter.getFirstFragment().getNameText();
                TextView tv2 = dishAdapter.getFirstFragment().getCommentText();

                tv1.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);

                tv1.startAnimation(textAnimation1);
                tv2.startAnimation(textAnimation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        final Dialog dialog = ((MainFragmentInterface) getActivity()).getFullScreenDialog();

        buttonAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                hasPerformed = true;
                try {
                    dishAdapter.getFirstFragment().getMainBar().setVisibility(View.INVISIBLE);
                    dishAdapter.getFirstFragment().getNameText().setVisibility(View.INVISIBLE);
                    dishAdapter.getFirstFragment().getCommentText().setVisibility(View.INVISIBLE);
                }catch(NullPointerException e){
                    Log.e("MainFragment/AnimationListener","Is getFirstFragment Null? " + (dishAdapter.getFirstFragment()==null));
                    e.printStackTrace();
                    animation.cancel();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dishAdapter.getFirstFragment().getMainBar().setVisibility(View.VISIBLE);
                dishAdapter.getFirstFragment().getMainBar().startAnimation(mainBarAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final MainFragmentInterface main = (MainFragmentInterface) getActivity();

        if (main.isFullScreenDialogOpen()){

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    main.closeFullScreenDialog();
                    Log.d("MainFragment/run", "Dialog Dismissed here - 6");
                }
            }, getResources().getInteger(R.integer.dialog_delay_duration));

            buttonAnimation.setStartOffset(getResources().getInteger(R.integer.dialog_delay_duration) - 1000);
        }

        if (dishAdapter.getFirstFragment()!=null){
            next.startAnimation(buttonAnimation);
            searchMap.startAnimation(buttonAnimation);
            pokeFriend.startAnimation(buttonAnimation);
            dislike.startAnimation(buttonAnimation);
            Log.i("MainFragment/startButtonsAnimation", "Animation Started for Main Buttons");
        }
        else{
            main.closeFullScreenDialog();
            Log.e("MainFragment/startButtonsAnimation", "First Fragment was null. Couldn't perform animation");
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
