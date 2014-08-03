package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class BattleActivity extends BaseActivity {
    BattleFragment bf;

    public int battleCount = 0;
    public int totalBattle;
    private String result = "";
    private int previousLength = 0;
    private YammAPIService service;
    private TextView battleCountText;
    private ArrayList<YammAPIService.RawBattleItemForPost> battleItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_battle);

        battleCountText = (TextView) findViewById(R.id.battle_count_text);

        battleItems = new ArrayList<YammAPIService.RawBattleItemForPost>();

        service = YammAPIAdapter.getTokenService();

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
                goBackHome();
            }
        };

        createDialog(BattleActivity.this, R.string.battle_dialog_title, R.string.battle_dialog_message,
                R.string.dialog_positive, R.string.dialog_negative,positiveListener, null).show();
    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////
    /*
    * Custom Listener for Battle Activity InternetDialog for getInitialBattleItem
    * */
    private class CustomInternetListener implements View.OnClickListener {
        private final Dialog dialog;
        public CustomInternetListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            Log.e("BattleActivity/CustomInternetListener", "Listener activated");
            if (checkInternetConnection()) {
                Log.e("BattleActivity/CustomInternetListener","Internet came back");
                dialog.dismiss();
                getInitialBattleItem();
            }
        }
    }

     /*
    * Setup Battle Fragments
    * */
    private void setBattleFragments() {
        bf = (BattleFragment) getSupportFragmentManager().findFragmentById(R.id.battle_fragment);

        getInitialBattleItem();
    }

    /*
    * Set totalBattleCount and get InitialBattleItem
    * */
    private void getInitialBattleItem(){
        service.getBattleItem("",new Callback<YammAPIService.RawBattleItem>() {
            @Override
            public void success(YammAPIService.RawBattleItem rawBattleItem, Response response) {
                Log.i("BattleActivity/getBattleItem","Success " + rawBattleItem.getBattleItem());
                totalBattle = rawBattleItem.getRounds();
                Log.i("BattleActivity/getBattleItem","Total Rounds: " + totalBattle);

                bf.setDishItemView(rawBattleItem.getBattleItem());
                battleCountText.setText("1 out of " + totalBattle);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("BattleActivity/getBattleItem", "Fail");
                retrofitError.printStackTrace();
                if (retrofitError.isNetworkError())
                    Toast.makeText(getApplicationContext(), getString(R.string.network_error_message), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.unidentified_error_message), Toast.LENGTH_LONG).show();

                showInternetConnectionAlert(new CustomInternetListener(internetAlert));
            }
        });
    }



    /*
    * Change current item to next one, returns null if end of list
    * */
    public void loadNextItem(BattleItem item){
        battleCount++;
        previousLength = result.length();
        result = result + item;
        battleItems.add(new YammAPIService.RawBattleItemForPost(item));
        Log.i("BattleActivity/loadNextItem","Item added to list" + item);


        //If Last Item, Finish this
        if (battleCount == totalBattle){
            finishBattle();
            return ;
        }
        Log.i("BattleActivity/loadNextItem","Query param " + result);

        bf.setLayoutClickable(false);

        //Send Item to Server
        service.getBattleItem(result,new Callback<YammAPIService.RawBattleItem>() {
            @Override
            public void success(YammAPIService.RawBattleItem rawBattleItem, Response response) {
                Log.i("BattleActivity/getBattleItem","Success " + rawBattleItem.getBattleItem());

                bf.setLayoutClickable(true);

                bf.setDishItemView(rawBattleItem.getBattleItem());
                battleCountText.setText( (battleCount+1) + " out of " + totalBattle);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                bf.setLayoutClickable(true);

                Log.e("BattleActivity/getBattleItem", "Fail");
                retrofitError.printStackTrace();
                if (retrofitError.isNetworkError())
                    Toast.makeText(getApplicationContext(), getString(R.string.network_error_message), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.unidentified_error_message), Toast.LENGTH_LONG).show();

                retrieveResult();
            }
        });
    }

    /*
    * When error, reduce battleCount & cut result
    * */
    private void retrieveResult(){
        battleCount--;
        result = result.substring(0, previousLength);
        Log.e("BattleActivity/retrieveResult", "BattleCount to " + battleCount + "& Result : " + result);
    }

    /*
* Saves Battle Result and Proceed
* */
    private void finishBattle(){
        Log.i("BattleResult/finishBattle", battleCount + " rounds done. Result : "+ battleItems);
        final ProgressDialog finalDialog =  createProgressDialog(BattleActivity.this,
                R.string.battle_final_dialog_title,
                R.string.battle_final_dialog_message);

        finalDialog.show();

        service.postBattleItem(new YammAPIService.RawBattleItemList(battleItems), new Callback<String>() {
            @Override
            public void success(String msg, Response response) {
                Log.i("BattleActivity/getBattleItem",msg);
                finalDialog.dismiss();

                //for FB Dialog
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(MainActivity.loggedFirstTime, true);
                editor.commit();

                goToActivity(MainActivity.class);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("BattleActivity/postBattleItem", "Fail");
                retrofitError.printStackTrace();
                if (retrofitError.isNetworkError())
                    Toast.makeText(getApplicationContext(), getString(R.string.network_error_message), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.unidentified_error_message), Toast.LENGTH_LONG).show();

                retrieveResult();
                finalDialog.dismiss();
            }
        });
    }




}
