package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by parkjiho on 6/1/14.
 */
public class GridActivity extends BaseActivity {
    private GridFragment gridFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        gridFragment = (GridFragment) getSupportFragmentManager().findFragmentById(R.id.grid_fragment);

    }

    @Override
    public void onBackPressed() {
        goBackHome();
    }


    ////Private Methods

    /*
    * Send Grid Selected Result to server
    * Only executed right before stating Battle Activity
    * */
    private boolean sendGridResult(String s){
        Log.i("GridActivity/sendGridResults", "sendGridResults Started");
        //Check internet connection
        if (!checkInternetConnection()){
            return false;
        }
        return true;
    }

    /*
   * Custom Listener for Intro Activity InternetDialog
   * */
    private class CustomInternetListener implements View.OnClickListener {
        private final Dialog dialog;
        public CustomInternetListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            Log.i("GridActivity/CustomInternetListener", "Listener activated");
            if (checkInternetConnection()) {
                Log.i("GridActivity/CustomInternetListener","Internet came back");
                dialog.dismiss();
               // finishIntro();
            }
        }
    }

    /*
   * Save Grid Selected Result to shared preferences
   * Only executed right before stating Battle Activity
   * */
    private String saveGridResult(GridFragment f){
        SharedPreferences prefs = getSharedPreferences(BaseActivity.packageName, MODE_PRIVATE);
        String s = "";
        for (GridItem i : f.getSelectedItems())
            s = s +i.getId()+",";
        BaseActivity.putInPref(prefs,getString(R.string.GRID_RESULT),s);
        Log.i("GridActivity/saveGridResult","Grid Result Saved - "+ f.getSelectedItems());
        return s;
    }



}
