package com.teamyamm.yamm.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkjiho on 7/23/14.
 */
public class GroupRecommendationActivity extends BaseActivity {

    ArrayList<Friend> selectedFriend;
    List<DishItem> dishItems;
    String selectedTime;
    MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_recommendation);

        setActionBarBackButton(true);
        loadBundle();
        setSelectedItems();
        setFragment();
    }

    private void setSelectedItems(){
        TextView selectedItemsText = (TextView) findViewById(R.id.selected_items_textview);
        String s = "";
        int count = 0;

        for (Friend f : selectedFriend) {
            if (count++ != 0) {
                Spannable newSpan = new SpannableString(" ");
                newSpan.setSpan(new BackgroundColorSpan(Color.TRANSPARENT),
                        0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                selectedItemsText.append(newSpan);
            }

            Spannable newSpan = new SpannableString(f.getName());
            newSpan.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.default_color)),
                    0, newSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            selectedItemsText.append(newSpan);
        }
    }

    private void setFragment(){
        dishItems = loadDishes();

        Type type = new TypeToken<List<DishItem>>(){}.getType();


        Bundle bundle = new Bundle();
        bundle.putString("dishes", new Gson().toJson(dishItems, type));
        bundle.putBoolean("isGroup", true);

        mainFragment = new MainFragment();
        mainFragment.setArguments(bundle);

        FragmentTransaction tact = getSupportFragmentManager().beginTransaction();
        tact.add(R.id.main_fragment_container, mainFragment, MainFragment.MAIN_FRAGMENT);
        tact.commit();
    }

    private List<DishItem> loadDishes(){
        ArrayList<DishItem> temp = new ArrayList<DishItem>();

        temp.add(new DishItem(1,"짜장면","맛있는"));

        temp.add(new DishItem(2,"짬뽕","맛있는" ));

        temp.add(new DishItem(3,"탕수육","맛있는"));

        temp.add(new DishItem(4,"냉면","맛있는"));

        return temp;

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
