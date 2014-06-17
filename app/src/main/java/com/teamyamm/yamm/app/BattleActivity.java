package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class BattleActivity extends BaseActivity {
    BattleFragment bf;

    public ArrayList<BattleItem> items = new ArrayList<BattleItem>();
    public BattleItem currentFirstItem, currentSecondItem;
    public int battleCount = 0;
    public int totalBattle;
    private ProgressDialog progressDialog;
    public boolean isFinished = false;
    private YammAPIService service;

    public final boolean FRAGMENT_ONE_SHOWN = true;

    public boolean fragmentShown = FRAGMENT_ONE_SHOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_battle);

        hideActionBar();
        setAPIService();
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
                goBackHome();
            }
        };

        createDialog(BattleActivity.this, R.string.battle_dialog_title, R.string.battle_dialog_message,
                R.string.dialog_positive, R.string.dialog_negative,positiveListener, null).show();
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////
    private void setAPIService(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiURL)
                .setLog(setRestAdapterLog())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(setRequestInterceptorWithToken())
                .build();

        service = restAdapter.create(YammAPIService.class);
    }

    /*
    * Saves Battle Result and Proceed to Battle Result Activity
    * */
    private void finishBattle(){
/*        boolean resultSent = true;

        Log.v("BattleActivity/finishBattle", "FinishBattle Started");
        Log.v("BattleActivity/finishBattle","Items Selected : " + items);
        //Save items to Shared Preferences

        //BattleItem.toString = (firstdishitem.id),(seconddishitem.id),(result)
        //list will be saved in String like this  ex. 1,2,2;2,3,-1;3,4,5;
        String result = saveBattleResults();

        //Send to Server, sleeps if internet isn't connected
        if (!sendBattleResults(result)){
            Log.e("Server Communication Error", "Sending Battle Results Failed");
            showInternetConnectionAlert(new CustomInternetListener(internetAlert));
            resultSent=false;
        }

        //If sendBattle Result Failed, don't go to Battle Result
        if (resultSent!=false) {
            goToActivity(MainActivity.class);
        }
        */
    }

    /*
    * Custom Listener for Battle Activity InternetDialog
    * */
    private class CustomInternetListener implements View.OnClickListener {
        private final Dialog dialog;
        public CustomInternetListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            Log.v("BattleActivity/CustomInternetListener", "Listener activated");
            if (checkInternetConnection()) {
                Log.v("BattleActivity/CustomInternetListener","Internet came back");
                dialog.dismiss();
                finishBattle();
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
    /*private String saveBattleResults(){
        int i;

        Log.v("BattleActivity/saveBattleResults", "saveBattleResults Started");
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
        String saved = prefs.getString(getString(R.string.BATTLE_RESULTS),"");

        for (i=0; i<items.size() - 1 ; i++) {
            BattleItem item = items.get(i);
            saved = saved + item + "_";
        }
        saved = saved + items.get(i) + "~";

        BaseActivity.putInPref(prefs,getString(R.string.BATTLE_RESULTS),saved);
        Log.v("BattleActivity/saveBattleResults","BattleItems saved" + saved);
        return saved;
    }*/

     /*
    * Setup Battle Fragments
    * */
    private void setBattleFragments() {
        bf = (BattleFragment) getSupportFragmentManager().findFragmentById(R.id.battle_fragment);

        // Show Progress Dialog
        progressDialog = createProgressDialog(this,
                R.string.battle_progress_dialog_title,
                R.string.battle_progress_dialog_message);
        progressDialog.show();

        getInitialBattleItem();
    }

    /*
    * Set totalBattleCount and get InitialBattleItem
    * */
    private void getInitialBattleItem(){
        BattleItem item = null;

        service.getBattleItem("",new Callback<YammAPIService.RawBattleItem>() {
            @Override
            public void success(YammAPIService.RawBattleItem rawBattleItem, Response response) {
                Log.i("BattleActivity/getBattleItem","Success " + rawBattleItem.getBattleItem());
                totalBattle = rawBattleItem.getRounds();
                Log.i("BattleActivity/getBattleItem","Total Rounds: " + totalBattle);

                bf.setDishItemView(rawBattleItem.getBattleItem());
                progressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("BattleActivity/getBattleItem", "Fail ");
            }
        });
    }



    /*
    * Change current item to next one, returns null if end of list
    * */
    public void loadNextItem(BattleItem item){
        //Send Item to Server

        //If Last Item, Finish this
        if (++battleCount == totalBattle){

            finishBattle();
        }
        //Load Next Item

    }



}
