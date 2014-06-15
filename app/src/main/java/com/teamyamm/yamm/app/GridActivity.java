package com.teamyamm.yamm.app;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
        setGridAllButton();
    }

    @Override
    public void onBackPressed() {
        goBackHome();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grid_activity_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.grid_confirm_button:
                Log.i("GridActivity/OnOptionsItemSelected","Confirm Grid Button Clicked");
                finishGridActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ////Private Methods
    private void setGridAllButton(){
        Button gridAllButton = (Button) findViewById(R.id.grid_all_button);
        gridAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("GridActivity/OnClickListener","All Button Clicked");
                finishGridActivity();
            }
        });
    }

    /*
    * Finshes GridActivity and saves&sends gridresults and go to BattleActivity
    * */
    private void finishGridActivity(){
        boolean resultSent = true;
        Log.i("GridActivity/finishGridActivity", "FinishGridActivity Started");

        //Save to Shared Pref
        String result = saveGridResult(gridFragment);

        //Send to Server

        if (!sendGridResult(result)){
            Log.e("Server Communication Error", "Sending Battle Results Failed");
            showInternetConnectionAlert(new CustomInternetListener(internetAlert));
            resultSent=false;
        }
        //If sendBattle Result Failed, don't go to Battle Activity
        if (resultSent!=false) {
            goToActivity(BattleActivity.class);
        }
    }

    /*
    * Send Grid Selected Result to server
    * Only executed right before stating Battle Activity
    * */
    private boolean sendGridResult(String s){
        Log.i("GridActivity/sendGridResults", "Sending " + s);
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
                finishGridActivity();
            }
        }
    }

    /*
   * Save Grid Selected Result to shared preferences
   * Only executed right before stating Battle Activity
   * */
    private String saveGridResult(GridFragment f){
        if (f.getSelectedItems().size() == 0)
            return "[]";

        String s = "[";
        for (GridItem i : f.getSelectedItems())
            s = s +i.getId()+",";
        s = s.substring(0, s.length()-2) + ']';
        Log.i("GridActivity/saveGridResult","Grid Result Saved - "+ f.getSelectedItems());

        return s;
    }



}
