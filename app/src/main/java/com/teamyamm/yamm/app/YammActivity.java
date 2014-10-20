package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.teamyamm.yamm.app.interfaces.MainFragmentInterface;
import com.teamyamm.yamm.app.network.YammAPIAdapter;
import com.teamyamm.yamm.app.pojos.DishItem;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by parkjiho on 10/15/14.
 */
public class YammActivity extends BaseActivity implements MainFragmentInterface {
    private final static int[] titleResId = {R.string.today_yamm_title, R.string.today_lunch_title, R.string.today_dinner_title, R.string.today_drink_title};
    private final static int[] messageResId = {0, R.string.today_lunch_message, R.string.today_dinner_message, R.string.today_drink_message};
    private final String[] suggestionType = {"lunch","dinner"};


    private Dialog fullScreenDialog;
    private boolean isDialogOpen = false;
    private TextView titleText;
    private int type;
    private List<DishItem> dishes;
    private MainFragment mainFragment;
    private boolean shouldLoadNewDishes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_recommendation);
        setActionBarBackButton(true);

        initActivity();
    }

    private void initActivity(){
        titleText = (TextView) findViewById(R.id.selected_items_textview);

        try {
            type = getIntent().getExtras().getInt("TYPE");
            shouldLoadNewDishes = getIntent().getExtras().getBoolean("LOADNEWDISHES");
        }catch(NullPointerException e){
            Log.e("YammActivity/initActivity","Type should be set for Yamm Activity.");
            finish();
        }
        Log.i("YammActivity/initActivity","Yamm Activity Type " + type);

        setTitle(getString(titleResId[type-1]));
        if (type!=YammFragment.TODAY)
            titleText.setText(getString(messageResId[type-1]));
        else{
            //Today's Yamm Title
        }
        if (shouldLoadNewDishes)
            loadNewDishes();
        else{
            loadOldDishes();
        }
    }

    private void loadOldDishes(){
        String s = prefs.getString(suggestionType[type-2], "");
        if (s.equals("")){
            loadNewDishes();
            return ;
        }
        dishes = new Gson().fromJson(s, DISH_ITEM_LIST_TYPE);
        setMainFragment(false);
    }

    private void loadNewDishes(){

        fullScreenDialog = createFullScreenDialog(YammActivity.this, getString(R.string.dialog_main));
        isDialogOpen = true;
        fullScreenDialog.show();

        if (type == YammFragment.DINNER || type == YammFragment.LUNCH) {
            Log.e("YammActivity/loadDishes","Request Dishes " + suggestionType[type-2]);
            YammAPIAdapter.getTokenService().getSuggestion(suggestionType[type-2], new Callback<List<DishItem>>() {
                @Override
                public void success(List<DishItem> dishItems, Response response) {
                    Log.i("YammActivity/getSuggestion/success",suggestionType[type-2] + " " + dishItems);
                    dishes = dishItems;
                    saveDishInPrefs();
                    setMainFragment(true);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.e("YammActivity/getSuggestion/failure","Failure");
                    if (retrofitError.isNetworkError())
                        makeYammToast(R.string.network_error_message, Toast.LENGTH_SHORT);
                    else
                        makeYammToast(R.string.unidentified_error_message, Toast.LENGTH_SHORT);
                    fullScreenDialog.dismiss();
                    finish();
                }
            });
        }
        else{
            dishes = new ArrayList<DishItem>();
            dishes.add(new DishItem(181, "팟타이", "맛있는"));
            dishes.add(new DishItem(182, "팟죽", "맛있는"));
            dishes.add(new DishItem(183, "피자", "맛있는"));
            dishes.add(new DishItem(184, "함박스테이크", "맛있는"));
            setMainFragment(true);
        }
    }

    private void saveDishInPrefs(){
        putInPref(prefs, suggestionType[type-2], new Gson().toJson(dishes, DISH_ITEM_LIST_TYPE));
        Log.d("YammActivity/saveDishInPrefs","Save Dish in Pref " + suggestionType[type-2]);
    }

    private void setMainFragment(boolean shouldPerformAnimation){
        if (dishes == null)
            return ;


        Bundle bundle = new Bundle();
        bundle.putString("dishes", new Gson().toJson(dishes, DISH_ITEM_LIST_TYPE));
        bundle.putBoolean("isGroup", false);
        bundle.putBoolean("shouldPerform", shouldPerformAnimation);
        mainFragment = new MainFragment();
        mainFragment.setArguments(bundle);

        try {
            FragmentTransaction tact = getSupportFragmentManager().beginTransaction();
            tact.add(R.id.main_fragment_container, mainFragment, MainFragment.MAIN_FRAGMENT);
            tact.commitAllowingStateLoss();
        }catch (IllegalStateException e){
            Log.e("YammActivity/setFragment","Activity Destroyed");
        }
    }

    public void changeInDishItem(List<DishItem> list){

    }

    public Dialog getFullScreenDialog(){
        return fullScreenDialog;
    }

    public boolean isFullScreenDialogOpen(){
        return isDialogOpen;
    }

    public void setFullScreenDialogOpen(boolean b){
        isDialogOpen = b;
    }

    public void closeFullScreenDialog(){
        if (isDialogOpen)
            fullScreenDialog.dismiss();
    }

    public boolean isLoading(){
        return false;
    }

    public boolean shouldTutorialOpen(){
        return false;
    }

    public void showTutorial(){ }
}
