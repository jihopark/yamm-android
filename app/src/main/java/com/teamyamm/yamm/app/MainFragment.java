package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamyamm.yamm.app.interfaces.MainFragmentInterface;
import com.teamyamm.yamm.app.network.MixpanelController;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.network.YammAPIService;
import com.teamyamm.yamm.app.pojos.DishItem;
import com.teamyamm.yamm.app.util.LocationSearchHelper;
import com.teamyamm.yamm.app.util.WTFExceptionHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 5/24/14.
 */
public class MainFragment extends Fragment {
    public final static String MAIN_FRAGMENT = "mf";
    public final static int DEFAULT_NUMBER_OF_DISHES = 4;

    public final static String SHARE = "SHARE";
    public final static String SEARCH_MAP = "SEARCHMAP";


    private RelativeLayout main_layout;
    private ViewPager dishPager;
    private DishFragmentPagerAdapter dishAdapter;
    private ImageButton nextLeft, nextRight, searchMap, pokeFriend, dislike;

    private List<DishItem> dishItems;
    private int currentPage = 0;
    private boolean isGroup;
    private boolean hasPerformed = false;
    public boolean isPerforming = false;

    private ArrayList<DishFragment> fragments = null;


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
        setRetainInstance(true);

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
    public void onResume(){
        super.onResume();
        if (!isPerforming){
            Log.d("MainFragment/onResume","Is not Performing. Show Buttons");
            configureNextButtons(currentPage, nextLeft, nextRight, getResources().getInteger(R.integer.main_buttons_animation_duration));
            searchMap.setVisibility(View.VISIBLE);
            dislike.setVisibility(View.VISIBLE);
            pokeFriend.setVisibility(View.VISIBLE);
            dislike.setVisibility(View.VISIBLE);
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
        setNextButtons();
        setPokeButton();
        setSearchButton();
        setDislikeButton();
    }

    public ViewPager getDishPager(){
        return dishPager;
    }

    private class DishFragmentPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener{
        private final boolean LEFT = true;
        private final boolean RIGHT = false;

        private final int DEFAULT_NUMBER_OF_DISHES = 4;
        private int numPage;
        private boolean hasReachedEnd = false;
        private boolean buttonToLeft = false;

        public DishFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            if (fragments==null) {
                fragments = new ArrayList<DishFragment>();
                for (int i = 0; i < DEFAULT_NUMBER_OF_DISHES; i++) {
                    fragments.add(new DishFragment());
                }
                Log.d("DishFragmentPagerAdapter/constructor","Fragment List null. Initializing");
            }
            Log.d("DishFragmentPagerAdapter/constructor","Constructor");
            numPage = DEFAULT_NUMBER_OF_DISHES;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position){
            Object o = super.instantiateItem(container, position);
            if (o instanceof DishFragment) {
                fragments.set(position, (DishFragment) o);
                Log.d("DishFragmentPagerAdapter/instantiateItem","Save Item " + position);
            }
            return o;
        }

        @Override
        public Fragment getItem(int index) {
            DishFragment dishFragment = new DishFragment();
            Bundle bundle = new Bundle();
            bundle.putString("dish", new Gson().toJson(dishItems.get(index), DishItem.class));
            bundle.putBoolean("isGroup",isGroup);
            bundle.putInt("index", index);

            dishFragment.setArguments(bundle);
            Log.i("DishFragmentPagerAdapter/getItem", "Page " + index +" : " + dishItems.get(index).getName());
            dishFragment.setParentFragment(MainFragment.this);

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
            setNextButtons();
            setPokeButton();
            setSearchButton();
            setDislikeButton();

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
                    MixpanelController.trackEndOfRecommendationMixpanel();
                    hasReachedEnd = true;
                    if (pref!=null) {
                        pref.edit().putBoolean(key, true).commit();
                        Log.i("DishFragmentPagerAdapter/onPageSelected",key + " set true");
                    }
                }
            }
            Log.i("DishFragmentPagerAdapter/onPageSelected", dishItems.get(i).getName() + " Page " + i +" : Setting Buttons Again");

            try {
                fragments.get(i).setParentFragment(MainFragment.this);
                fragments.get(i).showTexts();
                configureNextButtons(i, nextLeft, nextRight, getResources().getInteger(R.integer.main_buttons_animation_duration));
            }catch(NullPointerException e){
                Log.e("MainFragment/onPageSelected","NullPointer Exception caught. Is fragments.get(i)==null? "+ (fragments.get(i)==null));
                e.printStackTrace();
            }catch (IndexOutOfBoundsException e){
                Log.e("MainFragment/onPageSelected","IndexOutOfBoundsException caught");
                e.printStackTrace();
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }

        public void setButtonsForCurrentIndex(){
            Log.i("DishFragmentPagerAdapter/setButtonsForCurrentIndex", dishItems.get(currentPage).getName() + "Page " + currentPage +" : Setting Buttons For Current Index");
            //    fragments.get(currentPage).setButtons();
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

    private void setNextButtons(){
        Log.d("MainFragment/setNextButtons","Set Next Buttons");
        nextRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(currentPage == DEFAULT_NUMBER_OF_DISHES - 1))
                    dishPager.setCurrentItem(currentPage + 1, true);

            }
        });


        nextLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(currentPage == 0))
                    dishPager.setCurrentItem(currentPage - 1, true);

            }
        });
    }

    private void setPokeButton(){
        pokeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentDishItem()==null)
                    return ;

                Log.d("MainFragment/PokeButtonOnClick","Poke " +getCurrentDishItem().getName());
                addDishToPositive(SHARE, null, getCurrentDishItem());

                if (isGroup && getActivity() instanceof GroupRecommendationActivity){
                    GroupRecommendationActivity activity = (GroupRecommendationActivity) getActivity();
                    activity.sendPokeMessage(getCurrentDishItem());
                }
                else {
                    Intent intent = new Intent(getActivity(), PokeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    Bundle bundle = new Bundle();
                    bundle.putString("dish", new Gson().toJson(getCurrentDishItem(), DishItem.class));

                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private void addDishToPositive(String category, String detail, DishItem item){

        YammAPIService service = YammAPIAdapter.getTokenService();

        Log.d("MainFragment/addDishToPositive", "Like " + item.getName() + " " + category + " " + detail);

        if (service==null) {
            if (getActivity() instanceof BaseActivity) {
                ((BaseActivity) getActivity()).invalidToken();
                WTFExceptionHandler.sendLogToServer(getActivity(), "WTF Invalid Token Error @DishFragment/addDishToPositive");
            }
            return ;
        }

        service.postLikeDish(new YammAPIService.RawLike(item.getId(), category, detail), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.i("MainFragment/postLikeDish","Success " + s);
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

    private void setSearchButton(){
        searchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixpanelController.trackSearchMapMixpanel("");
                LocationSearchHelper.startMapActivity(getActivity(), getCurrentDishItem());
                addDishToPositive(SEARCH_MAP, "", getCurrentDishItem());
            }
        });
    }

    private void setDislikeButton(){
        final View.OnClickListener positiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MainFragment/onClick", "Dislike pressed for " + getCurrentDishItem().getName());
                MixpanelController.trackDislikeMixpanel(getCurrentDishItem());
                ((BaseActivity)getActivity()).makeYammToast(R.string.dish_dislike_toast, Toast.LENGTH_SHORT);
                toggleEnableButtons(false);
                YammAPIService service = YammAPIAdapter.getDislikeService();

                Callback<DishItem> callback = new Callback<DishItem>() {
                    @Override
                    public void success(DishItem dishItem, Response response) {
                        Log.i("MainFragment/postDislikeDish", "Success " + dishItem.getName());
                        changeInDishItem(getCurrentDishItem(), dishItem);
                        toggleEnableButtons(true);

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
                        toggleEnableButtons(true);
                    }
                };

                if (isGroup) {
                    service.postDislikeDishGroup(new YammAPIService.RawDislike(getCurrentDishItem().getId()), callback);
                    Log.i("MainFragment/onClickListener", "Group Dislike API Called");
                } else
                    service.postDislikeDish(new YammAPIService.RawDislike(getCurrentDishItem().getId()), callback);
                ((BaseActivity)getActivity()).dismissCurrentDialog();
            }
        };

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixpanelController.trackClickedDislikeMixpanel(getCurrentDishItem());
                ((BaseActivity)getActivity()).createDialog(getActivity(),R.string.dish_dislike_title,
                        R.string.dish_dislike_message, R.string.dish_dislike_positive, R.string.dish_dislike_negative,
                        positiveListener, null).show();
            }
        });
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

    private void toggleEnableButtons(boolean b){
        dislike.setEnabled(b);
        searchMap.setEnabled(b);
        pokeFriend.setEnabled(b);
        nextRight.setEnabled(b);
        nextLeft.setEnabled(b);
    }



    private void setLocationManagerListener(){
        //Set Location Listener
        LocationSearchHelper.initLocationSearchHelper((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE));
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
            try {
                ((MainFragmentInterface) getActivity()).closeFullScreenDialog();
                Log.e("MainFragment/startButtonsAnimation", "First Fragment was null. Couldn't perform animation");
            }catch(NullPointerException e){
                Log.e("MainFragment/startButtonsAnimation","Nullpointer Exception");
                e.printStackTrace();
            };
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
            Log.e("MainFragment/AnimationListener", "Nullpointer in MainBar");
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

    public void detachDishFragment(int p){
        fragments.add(p, null);
    }


}
