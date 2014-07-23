package com.teamyamm.yamm.app;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by parkjiho on 7/23/14.
 */
public class GroupRecommendationActivity extends BaseActivity {

    ArrayList<Friend> selectedFriend;
    String selectedTime;
    MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_recommendation);

        setActionBarBackButton(true);
        loadBundle();
        setFragment();
    }

    private void setFragment(){
        loadDishes();
/*
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList("dishIDs",currentDishIDs);
        bundle.putBoolean("isGroup", true);

        mainFragment = new MainFragment();
        mainFragment.setArguments(bundle);

        FragmentTransaction tact = getSupportFragmentManager().beginTransaction();
        tact.add(R.id.main_layout, mainFragment, MainFragment.MAIN_FRAGMENT);
        tact.commit();
        */
    }

    private ArrayList<DishItem> loadDishes(){
        return null;
    }

    private void loadBundle(){
        String jsonMyObject = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            jsonMyObject = bundle.getString("friendlist");
        }

        Type type = new TypeToken<ArrayList<Friend>>(){}.getType();


        selectedFriend = new Gson().fromJson(jsonMyObject, type);
        selectedTime = bundle.getString("time");

        Log.i("GroupRecommendationActivity/loadBundle", "Selected Friends : " + selectedFriend);
        Log.i("GroupRecommendationActivity/loadBundle","Selected Time : " + selectedTime);

    }
}
