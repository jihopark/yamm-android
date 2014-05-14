package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

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
    }

    /*
    * Go to Home Screen When Back Button of IntroActivity
    * */
    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Deleted because after battle activity, it shouldn't go back to intro activity
               /*
                //Save Previous Activity
                BaseActivity.putInPref(getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE)
                        ,getString(R.string.PREVIOUS_ACTIVITY), getString(R.string.PREVIOUS_ACTIVITY_INTRO));

                //Go to IntroActivity
                Intent introActivity = new Intent(getBaseContext(), IntroActivity.class);
                introActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(introActivity);*/
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
            finishBattle();
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
    * Saves Battle Result and Proceed to Battle Result Activity
    * */
    private void finishBattle(){
        boolean resultSent = true;

        Log.v("BattleActivity/finishBattle", "FinishBattle Started");
        Log.v("BattleActivity/finishBattle","Items Selected : " + items);
        //Save items to Shared Preferences

        //BattleItem.toString = (firstdishitem.id),(seconddishitem.id),(result)
        //list will be saved in String like this  ex. 1,2,2;2,3,-1;3,4,5;
        String result = saveBattleResults();

        //Send to Server, sleeps if internet isn't connected
        if (!sendBattleResults(result)){
            Log.e("Server Communication Error", "Sending Battle Results Failed");
            showInternetConnectionAlert(new CustomBattleListener(internetAlert));
            resultSent=false;
        }

        //If sendBattle Result Failed, don't go to Battle Result
        if (resultSent!=false) {
            goToActivity(BattleResultActivity.class);
        }
    }

    /*
    * Custom Listener for Battle Activity InternetDialog
    * */
    private class CustomBattleListener implements View.OnClickListener {
        private final Dialog dialog;
        public CustomBattleListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            Log.v("BattleActivity/CustomBattleListener", "Listener activated");
            if (checkInternetConnection()) {
                Log.v("BattleActivity/CustomBattleListener","Internet came back");
                dialog.dismiss();
                goToActivity(BattleResultActivity.class);
            }
        }
    }

    /*
    * Sends result string to server
    * returns false if sending fails
    * */
    private boolean sendBattleResults(String s){
        Log.v("BattleActivity/sendBattleResults", "sendBattleResults Started");

        //Check internet connection
        if (!checkInternetConnection()){
            return false;
        }
        return true;
    }
    /*
    * Save battle results to sharedpref
    * Returns saved string
    * */
    private String saveBattleResults(){
        Log.v("BattleActivity/saveBattleResults", "saveBattleResults Started");
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
        String saved = prefs.getString(getString(R.string.BATTLE_RESULTS),"");

        for (BattleItem i : items)
            saved = saved + i +";";

        BaseActivity.putInPref(prefs,getString(R.string.BATTLE_RESULTS),saved);
        Log.v("BattleActivity/saveBattleResults","BattleItems saved");
        return saved;
    }

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
        items.add(new BattleItem(new DishItem(7,"국밥"), new DishItem(8,"해장국")));
        items.add(new BattleItem(new DishItem(9,"짜장면"), new DishItem(10,"짬뽕")));


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



}
