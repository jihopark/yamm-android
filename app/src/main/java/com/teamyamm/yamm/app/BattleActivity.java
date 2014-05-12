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
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    public ArrayList<BattleItem> items = new ArrayList<BattleItem>();
    public BattleItem currentFirstItem, currentSecondItem;
    public int count = 0;
    public boolean isFinished = false;

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


    ////////////////////////////////Private Methods/////////////////////////////////////////////////
    /*
    * Setup Battle Fragments
    * */
    private void setBattleFragments(){

        BattleFragment bf1 = (BattleFragment) getSupportFragmentManager().findFragmentById(R.id.battle_fragment1);
        BattleFragment bf2 = (BattleFragment) getSupportFragmentManager().findFragmentById(R.id.battle_fragment2);
        bf2.setDishItemView(currentSecondItem);
        bf1.setDishItemView(currentFirstItem);
        ft.hide(bf2);
        ft.commit();
    }

    /*
    * Load Battle Items from Server
    * */
    private void loadBattleItems(){
        items.add(new BattleItem(new DishItem(1,"설렁탕"), new DishItem(1,"된장국")));
        items.add(new BattleItem(new DishItem(3,"치킨"), new DishItem(4,"피자")));
        items.add(new BattleItem(new DishItem(5,"비빔냉면"), new DishItem(6,"샐러드")));

        currentFirstItem = items.get(0);
        currentSecondItem = items.get(1);
        Log.v("BattleActivity", "Current First Item " + currentFirstItem);
        Log.v("BattleActivity","Current Second Item " + currentSecondItem);
    }

    /*
    * Change current item to next one, returns null if end of list
    * */
    public boolean loadNextItem(){
        if (count == items.size()-1)
            return false;

        currentFirstItem = currentSecondItem;
        currentSecondItem = items.get(++count);
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
