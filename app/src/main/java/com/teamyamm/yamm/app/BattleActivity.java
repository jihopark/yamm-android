package com.teamyamm.yamm.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;


public class BattleActivity extends BaseActivity {
    BattleFragment bf1;
    BattleFragment bf2;

    FragmentTransaction fragmentTransaction;
    public ArrayList<BattleItem> items = new ArrayList<BattleItem>();
    public BattleItem currentFirstItem, currentSecondItem;
    public int count = 1;
    public boolean isFinished = false;

    public final boolean FRAGMENT_ONE_SHOWN = true;
    public final boolean FRAGMENT_TWO_SHOWN = false;

    public boolean fragmentShown = FRAGMENT_ONE_SHOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadBattleItems();

        setContentView(R.layout.activity_battle);

        hideActionBar();
        setBattleFragments();
        //introButtonConfig();
    }

    /*
    * Go to Home Screen When Back Button of IntroActivity
    * */
    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Go to IntroActivity
                Intent introActivity = new Intent(getBaseContext(), IntroActivity.class);
                introActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(introActivity);
                goBackHome();
            }
        };

        createDialog(BattleActivity.this, R.string.battle_dialog_title, R.string.battle_dialog_message,
                R.string.dialog_positive, R.string.dialog_negative,positiveListener, null).show();
    }

    /*
    * Switch Fragment and loadNext
    * */
    public boolean switchFragment(){
        if (isFinished == true) {
            Log.v("switchFragment", "Last Item");
            Log.v("switchFragment","Items Selected : " + items);
            return false;
        }
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        BattleFragment hidden;

        if (fragmentShown == FRAGMENT_ONE_SHOWN){
            fragmentTransaction.hide(bf1);
            fragmentTransaction.show(bf2);
            Log.v("switchFragment", "Hide bf1 and show bf2");
            hidden = bf1;
        }
        else{ //if fragmentShown == FRAGMENT_TWO_SHOWN
            fragmentTransaction.show(bf1);
            fragmentTransaction.hide(bf2);
            hidden = bf2;
            Log.v("switchFragment", "Hide bf2 and show bf1");
        }
        fragmentShown = !fragmentShown;
        fragmentTransaction.commit();
        Log.v("switchFragment", "FT Commited");
        //If no more item to show


        //Load Next Item to currentFirstItem and currentSecondItem. get false if there is no next item
        isFinished = !loadNextItem();
        if (isFinished==false) {
            Log.v("switchFragment","Current Second Item is not null");
            hidden.setDishItemView(currentSecondItem);
        }
        return true;
    }


    ////////////////////////////////Private Methods/////////////////////////////////////////////////
    /*
    * Setup Battle Fragments
    * */
    private void setBattleFragments(){
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        bf1 = (BattleFragment) getSupportFragmentManager().findFragmentById(R.id.battle_fragment1);
        bf2 = (BattleFragment) getSupportFragmentManager().findFragmentById(R.id.battle_fragment2);
        bf1.setDishItemView(currentFirstItem);
        Log.v("setBattleFragment", "bf1 init");
        bf2.setDishItemView(currentSecondItem);
        Log.v("setBattleFragment", "bf2 init");
        fragmentTransaction.commit();
        Log.v("setBattleFragment", "first commit");
    }



    /*
    * Load Battle Items from Server
    * */
    private void loadBattleItems(){
        items.add(new BattleItem(new DishItem(1,"설렁탕"), new DishItem(1,"된장국")));
        items.add(new BattleItem(new DishItem(3,"치킨"), new DishItem(4,"피자")));
        items.add(new BattleItem(new DishItem(5,"비빔냉면"), new DishItem(6,"샐러드")));
        items.add(new BattleItem(new DishItem(5,"국밥"), new DishItem(6,"해장국")));


        currentFirstItem = items.get(0);
        currentSecondItem = items.get(1);
        Log.v("loadBattleItems", "Current First Item " + currentFirstItem);
        Log.v("loadBattleItems","Current Second Item " + currentSecondItem);
    }

    /*
    * Change current item to next one, returns null if end of list
    * */
    public boolean loadNextItem(){
        if (count >= items.size())
            return false;

        currentFirstItem = currentSecondItem;
        Log.v("loadNextItem", "Current First Item " + currentFirstItem);

        if (++count >= items.size()){
            currentSecondItem = null;
            Log.v("loadNextItem","Current Second Item null");
            return false;
        }
        currentSecondItem = items.get(count);
        Log.v("loadNextItem","Current Second Item " + currentSecondItem);
        return true;
    }

     /*
    * Setup OnclickListener for button that finishes battle
    * */
    private void introButtonConfig(){
        Button battleButton = (Button) findViewById(R.id.battle_button);
        battleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "배틀 끝", Toast.LENGTH_SHORT).show(); //To be deleted

                //Save Preference that finished first run
                SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);        //Save preference that INTRO was run once
                prefs.edit().putBoolean("firstrun", false).commit();

                // Send Battle Result to Server

                //Go to BattleResult Activity
                Intent battleResultActivity = new Intent(getBaseContext(), BattleResult.class);
                startActivity(battleResultActivity);
            }
        });
    }

}
