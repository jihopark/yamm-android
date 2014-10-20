package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamyamm.yamm.app.interfaces.MainFragmentInterface;
import com.teamyamm.yamm.app.pojos.DishItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 10/15/14.
 */
public class YammActivity extends BaseActivity implements MainFragmentInterface {
    private final static int[] titleResId = {R.string.today_yamm_title, R.string.today_lunch_title, R.string.today_dinner_title, R.string.today_drink_title};
    private final static int[] messageResId = {0, R.string.today_lunch_message, R.string.today_dinner_message, R.string.today_drink_message};


    private Dialog fullScreenDialog;
    private boolean isDialogOpen = false;
    private TextView titleText;
    private int type;
    private List<DishItem> dishItems;
    private MainFragment mainFragment;

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
        loadDishes();
    }

    private void loadDishes(){
        fullScreenDialog = createFullScreenDialog(YammActivity.this, getString(R.string.dialog_main));
        isDialogOpen = true;
        fullScreenDialog.show();

        dishItems = new ArrayList<DishItem>();
        dishItems.add(new DishItem(181, "팟타이","맛있는"));
        dishItems.add(new DishItem(182, "팟죽","맛있는"));
        dishItems.add(new DishItem(183, "피자","맛있는"));
        dishItems.add(new DishItem(184, "함박스테이크","맛있는"));

        setMainFragment();
    }

    private void setMainFragment(){
        if (dishItems == null)
            return ;

        Type type = new TypeToken<List<DishItem>>(){}.getType();


        Bundle bundle = new Bundle();
        bundle.putString("dishes", new Gson().toJson(dishItems, type));
        bundle.putBoolean("isGroup", false);

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
