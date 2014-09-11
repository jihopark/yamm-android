package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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
    private YammAPIService.RawBattleItem dishes;
    private Dialog fullScreenDialog;
    CallPreloadImageAsyncTask imagePreloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_battle);

        //battleCountText = (TextView) findViewById(R.id.battle_count_text);

        battleItems = new ArrayList<YammAPIService.RawBattleItemForPost>();

        setInitialLoading();

        service = YammAPIAdapter.getTokenService();

        setBattleFragments();

        imagePreloadTask = new CallPreloadImageAsyncTask();

        hideActionBar();
    }

    @Override
    public void onStop(){
        super.onStop();
        imagePreloadTask.cancel(true);
    }

    /*
    * Go to Home Screen When Back Button of IntroActivity
    * */
    @Override
    public void onBackPressed() {
        View.OnClickListener positiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackHome();
                dismissCurrentDialog();
            }
        };
        createDialog(BattleActivity.this, R.string.battle_dialog_title, R.string.battle_dialog_message,
                    R.string.dialog_positive, R.string.dialog_negative, positiveListener, null).show();

    }

    ////////////////////////////////Private Methods/////////////////////////////////////////////////
    private void setInitialLoading(){
        fullScreenDialog = createBattleFullScreenDialog(BattleActivity.this, getString(R.string.battle_intro_dialog));
        fullScreenDialog.show();

    }
    private Dialog createBattleFullScreenDialog(Context context, String message){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_battle_full_screen);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black_overlay)));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView tv = (TextView) dialog.findViewById(R.id.dialog_message);
        tv.setText(message);

        Button start = (Button) dialog.findViewById(R.id.start_button);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bf == null || bf.first ==null || bf.second == null){
                    makeYammToast(R.string.battle_loading_toast, Toast.LENGTH_SHORT);
                    return ;
                }
                fullScreenDialog.dismiss();
                bf.startBattleIntroAnimation();
            }
        });

        return dialog;
    }

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
        service.getBattleItems(new Callback<YammAPIService.RawBattleItem>() {
            @Override
            public void success(YammAPIService.RawBattleItem rawBattleItem, Response response) {
                totalBattle = rawBattleItem.getRounds();
                dishes = rawBattleItem;

                Log.i("BattleActivity/getBattleItems","Total Rounds: " + totalBattle);
                for (int i=0; i< totalBattle; i++){
                    Log.i("BattleActivity/getBattleItems","Round " + (i+1) + ":" +
                            dishes.getBattleItem(i).getFirst() + "," + dishes.getBattleItem(i).getSecond());
                }

                bf.setDishItemView(dishes.getBattleItem(0));
                try {
                    imagePreloadTask.execute();
                }catch(IllegalStateException e){
                    Log.e("BattleActivity/getInitialBattleItem","The task already been Executed");
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                imagePreloadTask.cancel(true);
                Log.e("BattleActivity/getBattleItems", "Fail");
                retrofitError.printStackTrace();
                if (retrofitError.isNetworkError())
                    makeYammToast(getString(R.string.network_error_message), Toast.LENGTH_LONG);
                else
                    makeYammToast(getString(R.string.unidentified_error_message), Toast.LENGTH_LONG);
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

        bf.setLayoutClickable(false);
        bf.setDishItemView(dishes.getBattleItem(battleCount));
        bf.setLayoutClickable(true);


        //Send Item to Server
       /* service.getBattleItem(result,new Callback<YammAPIService.RawBattleItem>() {
            @Override
            public void success(YammAPIService.RawBattleItem rawBattleItem, Response response) {
                Log.i("BattleActivity/getBattleItem","Success " + rawBattleItem.getBattleItem());

                bf.setLayoutClickable(true);

                bf.setDishItemView(rawBattleItem.getBattleItem());
      //          battleCountText.setText( (battleCount+1) + " out of " + totalBattle);
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
        });*/
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
        final Dialog finalDialog = createFullScreenDialog(BattleActivity.this, getString(R.string.progress_dialog_message));

        finalDialog.show();

        service.postBattleItem(new YammAPIService.RawBattleItemList(battleItems), new Callback<String>() {
            @Override
            public void success(String msg, Response response) {
                finalDialog.dismiss();

                trackBattleMixpanel();

                goToActivity(MainActivity.class);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("BattleActivity/postBattleItem", "Fail");
                retrofitError.printStackTrace();
                if (retrofitError.isNetworkError())
                    makeYammToast(getString(R.string.network_error_message), Toast.LENGTH_LONG);
                else
                    makeYammToast(getString(R.string.unidentified_error_message), Toast.LENGTH_LONG);

                retrieveResult();
                finalDialog.dismiss();
            }
        });
    }

    private void preloadImages(int w, int h){
        for (int i=1;i<totalBattle;i++) {
            DishItem a = dishes.getBattleItem(i).getFirst();
            DishItem b = dishes.getBattleItem(i).getSecond();
            Log.i("BattleActivity/preloadImages", "Preloading:" +
                    a + "," + b + " " + w + "x" + h);
            Picasso.with(BattleActivity.this)
                    .load(YammImageView.getURL(YammImageView.DISH, w, h, a.getId()))
                    .skipMemoryCache()
                    .error(R.drawable.mainback_test)
                    .fetch();
            Picasso.with(BattleActivity.this)
                    .load(YammImageView.getURL(YammImageView.DISH, w, h, b.getId()))
                    .skipMemoryCache()
                    .error(R.drawable.mainback_test)
                    .fetch();
        }
        return ;
    }


    public class CallPreloadImageAsyncTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            while(true){
                Log.d("BattleActivity/CallPreloadImageAsyncTask", "Checking");
                if (bf!=null)
                    Log.d("BattleActivity/CallPreloadImageAsyncTask", "Dimensions measured"
                            + bf.getImageWidth() + "x" + bf.getImageHeight());

                if (bf!=null && bf.getImageHeight() != 0 && bf.getImageWidth() != 0) {
                    int w = bf.getImageWidth();
                    int h = bf.getImageHeight();

                    Log.d("BattleActivity/CallPreloadImageAsyncTask", "Dimensions measured" + w + "x" + h);
                    preloadImages(w, h);
                    break;
                }

            }
            return 1;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Log.i("BattleActivity/CallPreloadImageAsyncTask", "Done");
        }
    }


    private void trackBattleMixpanel(){
        JSONObject props = new JSONObject();
        try{
            props.put("Battle Count", battleCount);
        }catch(JSONException e){
            Log.e("BattleActivity/trackBattleMixpanel","Error in JSON");
            props = new JSONObject();
        }
        mixpanel.track("Battle", props);
    }


}
